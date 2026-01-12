// Event Listner to finish HTML load before script
document.addEventListener("DOMContentLoaded", async function () {
    // Load and display map
    const map = L.map('map', {
        zoomControl: false
    }).setView([55.585, 12.953], 13);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap'
    }).addTo(map);
    L.control.zoom({
        position: 'bottomright'
    }).addTo(map);

    let userMarker;
    let currentLat = null;
    let currentLng = null;
    let rangeArea;
    let routingActive = null;

    async function getAllToilets() {
        const hasChaningTable = document.getElementById("filterTable")?.checked;
        const freeEntry = document.getElementById("filterFree")?.checked;
        const toiletAmount = document.getElementById("filterAmount")?.value;
        const rangeSlider = document.getElementById("rangeSlider")?.value;

        const options = {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        };
        let queryParams = new URLSearchParams();

        // Back end filter queries
        if (hasChaningTable) queryParams.append("change_table_child", "1");
        if (freeEntry) queryParams.append("fee", "true");
        if (toiletAmount > 0) {
            queryParams.append("wc", toiletAmount);
        }
        if (currentLat && currentLng) {
            queryParams.append("lat", currentLat);
            queryParams.append("lon", currentLng);
            queryParams.append("range", rangeSlider);
            // console.log(currentLat, currentLng, rangeSlider);
        } else {
            console.log("start/error");
        }
        // Creates full url, converts objects to a full string for instence: 7070/toilets?wc=2&fee&change_table_child
        const url = `http://localhost:7070/toilets?${queryParams.toString()}`;

        const res = await fetch(url, options);
        const data = await res.json();

        toilets = data;

        // Clear old markers so theres no overlap
        Object.values(markerDict).forEach(marker => map.removeLayer(marker));
        for (let key in markerDict) delete markerDict[key];
        console.log(data);
        sidebarContent();

    }

    let toilets = [];
    // Fetch Data from backend

    // async function rateAToilet() {

    // const options = {
    //     method: "POST",
    //     headers: {
    //         "Content-Type": "application/json"
    //     },
    //     body: "Hej backend // frontend"
    // }

    // await fetch("http://localhost:7070/", options);
    // }

    // Dict stores markers by id 
    const markerDict = {};

    map.on("popupopen", async (e) => {
        const root = e.popup.getElement();
        if (!root) return;

        const wrap = root.querySelector(".popup-reviews");
        if (!wrap) return;

        const toiletId = wrap.dataset.toiletId;
        const toiletName = wrap.dataset.toiletName;

        const toggleBtn = wrap.querySelector(".popup-review-toggle");
        const form = wrap.querySelector(".popup-review-form");
        const listEl = wrap.querySelector(".popup-review-list");
        const statusEl = wrap.querySelector(".review-status");
        // --- STAR & POOP PICKERS ---
        const starPicker = wrap.querySelector(".star-picker");
        const poopPicker = wrap.querySelector(".poop-picker");
        const ratingInput = wrap.querySelector(".review-rating"); // hidden
        const poopInput = wrap.querySelector(".review-poop");     // hidden

        function paintStars(value) {
            const stars = starPicker.querySelectorAll(".star");
            stars.forEach(star => {
                const v = Number(star.dataset.value);
                star.classList.toggle("active", v <= value);
                star.setAttribute("aria-checked", v === value ? "true" : "false");
            });
        }

        function paintPoops(value) {
            const poops = poopPicker.querySelectorAll(".poop");
            poops.forEach(p => {
                const v = Number(p.dataset.value);
                p.classList.toggle("active", v <= value);
                p.setAttribute("aria-checked", v === value ? "true" : "false");
            });
        }

        if (starPicker && ratingInput) {
            starPicker.addEventListener("click", (ev) => {
                const target = ev.target.closest(".star");
                if (!target) return;
                const val = Number(target.dataset.value);
                ratingInput.value = String(val);
                paintStars(val);
            });

            // default
            paintStars(Number(ratingInput.value || 0));
        }

        if (poopPicker && poopInput) {
            poopPicker.addEventListener("click", (ev) => {
                const target = ev.target.closest(".poop");
                if (!target) return;
                const val = Number(target.dataset.value);
                poopInput.value = String(val);
                paintPoops(val);
            });

            // default
            paintPoops(Number(poopInput.value || 0));
        }

        // 1) Ladda och visa reviews direkt när popupen öppnas
        await loadReviews(toiletId, listEl);

        // 2) Toggle: visa/dölj formulär
        toggleBtn.onclick = (ev) => {
            ev.preventDefault();
            form.style.display = (form.style.display === "none") ? "block" : "none";
            statusEl.textContent = "";
        };

        // 3) Skicka review
        form.onsubmit = async (ev) => {
            ev.preventDefault();
            statusEl.textContent = "Skickar...";

            const author = wrap.querySelector(".review-author").value.trim();
            const rating = Number(wrap.querySelector(".review-rating").value);
            const poop = Number(wrap.querySelector(".review-poop").value); // Hämtar från dolt fält
            const description = wrap.querySelector(".review-description").value.trim();

            if (!rating || rating < 1) {
                statusEl.textContent = "Välj ett stjärnbetyg först ⭐";
                return;
            }
            if (!poop || poop < 1) {
                statusEl.textContent = "Välj hur sunkigt det är 💩";
                return;
            }

            try {
                const res = await fetch("http://localhost:7070/reviews", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        toiletId: Number(toiletId),
                        toiletName,
                        author: author,
                        rating: rating,
                        sunkRating: poop,
                        description: description,
                        photo: ""
                    })
                });

                if (!res.ok) throw new Error("Backend svarade inte OK");

                statusEl.textContent = "Tack! Review sparad ";
                form.reset();
                form.style.display = "none";

                // Ladda om listan i popupen
                await loadReviews(toiletId, listEl);

            } catch (err) {
                console.error(err);
                statusEl.textContent = "Något gick fel när review skulle sparas 💩 ";
            }
        }; // Stänger form.onsubmit
    }); // Stänger map.on("popupopen")

    // Hjälpfunktion: hämta & rendera reviews
    async function loadReviews(toiletId, listEl) {
        listEl.textContent = "Laddar reviews...";

        try {
            const res = await fetch(`http://localhost:7070/reviews?toiletId=${encodeURIComponent(toiletId)}`);
            const reviews = await res.json();

            if (!Array.isArray(reviews) || reviews.length === 0) {
                listEl.textContent = "Inga reviews än.";
                return;
            }

            listEl.innerHTML = reviews.map(r => `
<div class="review-item" style="margin-bottom:8px;">
            <div><b>${r.author}</b> • ${r.date} •  ${r.rating}</div>
            <div>${r.description}</div>
        </div>
        `).join("");

        } catch (err) {
            console.error(err);
            listEl.textContent = "Kunde inte ladda reviews :(";
        }
    }


    // Function to move the map and open a popup when users select a toilet
    function selectToilet(toilet, listElement) {
        map.flyTo([toilet.lat, toilet.lng], 16);
        if (toilet.id in markerDict) {
            markerDict[toilet.id].openPopup();
        }

        // Removes selected syling from lists
        document.querySelectorAll('.toilet_card').forEach(li => li.classList.remove('selected'));

        // Adds selected styling to the just clicked one
        if (listElement) {
            listElement.classList.add('selected');
        }

        // Checkbox for routing, toggleable.
        const routingEnabled = document.getElementById("routingToggle")?.checked;
        if (routingActive) {
            map.removeControl(routing);
            routingActive = null;
        }
        if (routingEnabled) {
            routeTo(currentLat, currentLng, toilet.lat, toilet.lng);
        }

    }


    // Adds routing lines from position to selected toilet
    function routeTo(fromLat, fromLng, toLat, toLng) {
        routing = L.Routing.control({
            waypoints: [
                L.latLng(fromLat, fromLng),
                L.latLng(toLat, toLng)
            ]
        }).addTo(map);
        routingActive = true;
    }

    function numOrNull(x) {
        const n = Number(x);
        return Number.isFinite(n) ? n : null;
    }

    function formatAvgRating(rating5) {
        const r = numOrNull(rating5);
        if (!r || r <= 0) return "Inga betyg ännu";
        //const score100 = Math.round(r * 20);
        //return `${score100}/100`;
        return `${r.toFixed(1).replace(".", ",")} / 5`
    }

    function formatDankness100(danknessValue) {
        const d = numOrNull(danknessValue);
        if (!d || d <= 0) return "Inga betyg ännu";

        // Om backend skickar 1–5 → konvertera till /100
        if (d <= 5) return `${Math.round(d * 20)}/100`;

        // Om backend redan skickar 0–100
        return `${Math.round(d)}/100`;
    }


    // Function to sort and show sidebar list
    function sidebarContent(sortWith = 'id') {
        const listContainer = document.getElementById("toa-list");
        const countContainer = document.getElementById("toilet-count");
        listContainer.innerHTML = "";

        // Filters data to only include what is being searched OR blank and include all data
        const searchTerm = document.getElementById("toiletSearch")?.value.toLowerCase() || "";
        const filtered = toilets.filter(t =>
            t.name.toLowerCase().includes(searchTerm)
        );



        // Updates number showing amount of toilets found
        if (countContainer) {
            countContainer.textContent = filtered.length;
        }
        // Sort list sort with id otherwise numbered with score highest to lowest
        const sorted = (!sortWith || sortWith === 'default' || sortWith === 'id')
            ? [...filtered]
            : [...filtered].sort((toiletA, toiletB) => {
                if (sortWith === 'name') {
                    return toiletA.name.localeCompare(toiletB.name);
                }

                if (sortWith === 'score') {
                    const valA = parseFloat(toiletA.avgRating) || 0;
                    const valB = parseFloat(toiletB.avgRating) || 0;
                    return valB - valA;
                }

                if (sortWith === 'distance') {
                    const distA = parseFloat(toiletA.distance) || 0;
                    const distB = parseFloat(toiletB.distance) || 0;
                    return distA - distB;
                }
                if (sortWith === 'dankness') {
                    const valA = parseFloat(toiletA.shittyness) || 0;
                    const valB = parseFloat(toiletB.shittyness) || 0;
                    return valB - valA;
                }
            });

        sorted.forEach(toilet => {
            const popupContent = `
                <div class="toilet-popup">
                    <strong class="popup-title">${toilet.name}</strong>
                    
                    <div class="popup-info">
                        <!--<span><b>Kategori:</b> ${toilet.category}</span>-->
                        <!--span><b>Poäng:</b> ${toilet.score}/100</span-->
                        <!--span><b>Sunkighet:</b> ${toilet.shittyness}/100</span-->
                        <span><b>Poäng:</b> ${formatAvgRating(toilet.avgRating)}</span>
                        <span><b>Sunkighet:</b> ${formatDankness100(toilet.shittyness)}</span>
                        <span><b>Antal toaletter:</b> ${toilet.nbrWcs}</span>
                        <span><b>Avgift:</b> ${toilet.fee !== "" ? toilet.fee : "Gratis"}</span>
                        <span><b>Skötbord:</b> ${toilet.change_table_child > 0 ? "Finns" : "Saknas"}</span>
                    </div>
                        <!-- REVIEW-DEL -->
                    <div class="popup-reviews" data-toilet-id="${toilet.id}" data-toilet-name="${toilet.name}">
                        <button type="button" class="popup-review-toggle">Review</button>

                        <form class="popup-review-form" style="display:none; margin-top:8px;">
                            <input class="review-author" type="text" placeholder="Ditt namn" required />
                            
                            <div class="rating-row" style="margin-top:8px;">
                                <div><b>Betyg:</b></div>
                                <div class="star-picker" role="radiogroup" aria-label="Välj betyg">
                                    <span class="star" data-value="1" role="radio" aria-checked="false">⭐️</span>
                                    <span class="star" data-value="2" role="radio" aria-checked="false">⭐️</span>
                                    <span class="star" data-value="3" role="radio" aria-checked="false">⭐️</span>
                                    <span class="star" data-value="4" role="radio" aria-checked="false">⭐️</span>
                                    <span class="star" data-value="5" role="radio" aria-checked="false">⭐️</span>
                                </div>
                                <input type="hidden" class="review-rating" value="0" />
                            </div>

                            <div class="poop-row" style="margin-top:8px;">
                                <div><b>Sunkighet:</b></div>
                                <div class="poop-picker" role="radiogroup" aria-label="Välj sunkighet">
                                    <span class="poop" data-value="1" role="radio" aria-checked="false">💩</span>
                                    <span class="poop" data-value="2" role="radio" aria-checked="false">💩</span>
                                    <span class="poop" data-value="3" role="radio" aria-checked="false">💩</span>
                                    <span class="poop" data-value="4" role="radio" aria-checked="false">💩</span>
                                    <span class="poop" data-value="5" role="radio" aria-checked="false">💩</span>
                                </div>
                                <input type="hidden" class="review-poop" value="0" />
                            </div>


                            <textarea class="review-description" placeholder="Skriv en kommentar..." required></textarea>
                            <button type="submit">Skicka</button>
                            <div class="review-status" style="margin-top:6px;"></div>
                        </form>

                    <div class="popup-review-list" style="margin-top:10px;"></div>
                    </div>
                </div>`;

            // Custom marker on map
            const brownIcon = L.divIcon({
                className: 'marker-box',
                html: '<div class="marker-icon"></div>',
                iconSize: [30, 42],
                iconAnchor: [15, 42]
            });

            // Creates marker if it dosent already exist to avoid duping
            if (!markerDict[toilet.id]) {
                const marker = L.marker([toilet.lat, toilet.lng], { icon: brownIcon })
                    .addTo(map)
                    .bindPopup(popupContent);
                markerDict[toilet.id] = marker;
            }
            // HTML for the list
            const li = document.createElement("li");
            li.className = "toilet_card";

            // Convert rating to one decimal
            const displayRating = toilet.avgRating > 0 ? toilet.avgRating.toFixed(1) : "Inga betyg";
            const reviewCount = toilet.reviews ? toilet.reviews.length : 0;

            const hasChangingTable = Number(toilet.change_table_child) > 0;
            const changingTableIcon = hasChangingTable
                ? `<i class="fa-solid fa-baby-carriage toilet-card-icon" title="Skötbord finns" aria-label="Skötbord finns"></i>`
                : "";

            li.innerHTML = `
                <div>
                    <h4>${toilet.name} ${changingTableIcon}</h4> | <h4>${toilet.distance} m</h4>
                </div>
                <small>
                    </i> ${displayRating} | 
                    Sunkighet: ${formatDankness100(toilet.shittyness)}
                    (${reviewCount} reviews)
                </small>
            `;

            li.onclick = () => selectToilet(toilet, li);
            listContainer.appendChild(li);
        });
    }
    // Handles matching slider with label for toilet amount
    const toiletSlider = document.getElementById("filterAmount");
    const sliderLabelValue = document.getElementById("toiletAmount");
    if (toiletSlider && sliderLabelValue) {
        // Updates number next to slider
        toiletSlider.addEventListener("input", function () {
            sliderLabelValue.textContent = this.value;
        });
        //Calls backend handler
        toiletSlider.addEventListener("change", function () {
            getAllToilets();
        });
    }
    // Handles matching slider with label for search range
    const rangeSlider = document.getElementById("rangeSlider");
    const rangerNumber = document.getElementById("rangeNumber");
    if (rangeSlider && rangerNumber) {
        // Updates number next to slider
        rangeSlider.addEventListener("input", function () {
            rangerNumber.textContent = this.value;

            if (rangeArea) {
                rangeArea.setRadius(Number(this.value));
            }
        });

        //Calls backend handler
        rangeSlider.addEventListener("change", function () {
            getAllToilets();
        });
    }

    function setStartMarker(latlng) {
        currentLat = latlng.lat;
        currentLng = latlng.lng;

        userMarker ||= L.marker(latlng).addTo(map);
        userMarker.setLatLng(latlng);
    }

    function markerMapPlacement(event) {
        const markerFilter = document.getElementById("markerFilter")?.checked;
        // Checks if marker checkbox is False and stops function in that case 
        if (!markerFilter) {
            return;
        }

        currentLat = event.latlng.lat;
        currentLng = event.latlng.lng;
        const rangeValue = document.getElementById("rangeSlider")?.value;
        // Creates or updates marker
        // ||= Creates object if it dosnt exist otherwise does nothing. It's good shit!
        userMarker ||= L.marker(event.latlng).addTo(map);
        userMarker.setLatLng(event.latlng);

        // Creates or updates a area around the marker
        rangeArea ||= L.circle(event.latlng, {
            color: '#a0522d',
            fillColor: '#e5dad5',
            fillOpacity: 0.2,
            weight: 1
        }).addTo(map);

        // Updates the values
        rangeArea.setLatLng(event.latlng).setRadius(rangeValue);
        getAllToilets();
    }

    document.getElementById("markerFilter")?.addEventListener("change", function () {
        const rangeContainer = document.getElementById("rangeSliderContainer");
        if (!this.checked) {
            if (rangeContainer) rangeContainer.style.display = "none";
            if (userMarker) {
                map.removeLayer(userMarker);
                userMarker = null;
            }
            if (rangeArea) {
                map.removeLayer(rangeArea);
                rangeArea = null;
            }
            currentLat = null;
            currentLng = null;
            getAllToilets();
        }
        else {
            if (rangeContainer) rangeContainer.style.display = "block";

            const rangeSlider = document.getElementById("rangeSlider");
            const rangerNumber = document.getElementById("rangeNumber");

            if (rangeSlider) {
                rangeSlider.value = 500;
                if (rangerNumber) {
                    rangerNumber.textContent = 500;
                }
            }
        }
    });

    map.on('click', markerMapPlacement);

    map.on('locationfound', function (e) {
        setStartMarker(e.latlng);
        getAllToilets();
    });

    map.on('locationerror', function () {
        setStartMarker(map.getCenter());
        getAllToilets();
    });

    map.locate({ setView: true, maxZoom: 14 });

    // The searchbar event listner only trigers if a value is input in html
    const searchInput = document.getElementById("toiletSearch");
    if (searchInput) {
        searchInput.addEventListener("input", function () {
            sidebarContent();
        });
    }

    // Filter event listners
    document.getElementById("filterTable")?.addEventListener("change", getAllToilets);
    document.getElementById("filterFree")?.addEventListener("change", getAllToilets);
    document.getElementById("filterAmount")?.addEventListener("input", getAllToilets);

    // The sort buttons 
    const standardView = document.getElementById("sortName");
    const sortWithPoints = document.getElementById("sortScore");
    const sortWithDank = document.getElementById("sortDankness");

    // Events for what sort button is clicked
    if (standardView) standardView.onclick = () => sidebarContent('id');
    if (sortWithPoints) sortWithPoints.onclick = () => sidebarContent('score');
    if (sortWithDank) sortWithDank.onclick = () => sidebarContent('dankness');

    // const testButton = document.querySelector("#testButton");
    // if (testButton) {
    //     testButton.addEventListener("click", getAllToilets);
    // }

    // Run function on startup
    getAllToilets();
});

// Adds reactive on-click sytling to tab buttons
const buttons = document.querySelectorAll('.tab-buttons');

buttons.forEach(button => {
    button.addEventListener('click', function () {
        buttons.forEach(btn => btn.classList.remove('active'));

        this.classList.add('active');

    });
});