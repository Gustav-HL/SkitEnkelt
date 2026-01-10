// Event Listner to finish HTML load before script
document.addEventListener("DOMContentLoaded", async function () {
    // Load and display map
    var map = L.map('map').setView([55.605, 13.003], 13);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap'
    }).addTo(map);
    
    // TOALETT-DATABAS, ska kompletteras med getmetoder från vårt API
    // const toilets = [
    //         {
    //             name: "Nobeltorget",
    //             lat: 55.5916569774,
    //             lng: 13.0202647274,
    //             category: "lyxig",
    //             score: 85,
    //             dankness: 10
    //         },
    //         {
    //             name: "Parktoalett",
    //             lat: 55.583,
    //             lng: 13.0230,
    //             category: "sunk",
    //             score: 20,
    //             dankness: 95
    //         },
    //         {
    //             name: "Sofielund",
    //             lat: 55.589,
    //             lng: 13.0155,
    //             category: "standard",
    //             score: 55,
    //             dankness: 40
    //         },
    //         {
    //             name: "Casa Björnheimer",
    //             lat: 55.602,
    //             lng: 13.0135,
    //             category: "EPIC",
    //             score: 100,
    //             dankness: 0
    //         }
    //     ];
    //
    //
    //
    // // Rita ut markers från listan
    // toilets.forEach(t => {
    //     L.marker([t.lat, t.lng]).addTo(map).bindPopup(t.id);
    // });

    async function getAllToilets() {
        const hasChaningTable = document.getElementById("filterTable")?.checked;
        const freeEntry = document.getElementById("filterFree")?.checked;
        const toiletAmount = document.getElementById("filterAmount")?.value;

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
        // Creates full url, converts objects to a ful string for instence: 7070/toilets?wc=2&fee&change_table_child
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

    // Dict stores marker by name 
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
    const description = wrap.querySelector(".review-description").value.trim();

    try {
      const res = await fetch("http://localhost:7070/reviews", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          toiletId: Number(toiletId),
          toiletName,
          author,
          rating,
          description,
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
  };
});

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
    listEl.textContent = "Kunde inte ladda reviews.";
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
    }

    // Function to sort and show sidebar list
    function sidebarContent(sortWith = 'name') {
        const listContainer = document.getElementById("toa-list");
        listContainer.innerHTML = ""; 

        // Sort list sort with name otherwise numbered with score highest to lowest
        const sorted = [...toilets].sort((a, b) => {
            if (sortWith === 'name') return a.name.localeCompare(b.name);
            // Sort by numver b - a to ensure the highest values appear at top
            return b[sortWith] - a[sortWith];
        });

        sorted.forEach(toilet => {
            const popupContent = `
                <div class="toilet-popup">
                    <strong class="popup-title">${toilet.name}</strong>
                    
                    <div class="popup-info">
                        <span><b>Kategori:</b> ${toilet.category}</span>
                        <span><b>Poäng:</b> ${toilet.score}/100</span>
                        <span><b>Sunkighet:</b> ${toilet.dankness}/100</span>
                        <span><b>Antal toaletter:</b> ${toilet.nbrWcs}</span>
                        <span><b>Avgift:</b> ${toilet.fee !== "" ? toilet.fee : "Gratis"}</span>
                        <span><b>Skötbord:</b> ${toilet.change_table_child > 0 ? "Finns" : "Saknas"}</span>
                    </div>
                        <!-- REVIEW-DEL -->
                    <div class="popup-reviews" data-toilet-id="${toilet.id}" data-toilet-name="${toilet.name}">
                        <button type="button" class="popup-review-toggle">Review</button>

                        <form class="popup-review-form" style="display:none; margin-top:8px;">
                            <input class="review-author" type="text" placeholder="Ditt namn" required />
                            /*<input class="review-rating" type="number" min="1" max="5" step="0.5" value="3" required />*/
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
            li.innerHTML = `
                <strong>${toilet.name}</strong>
                <small>Poäng: ${toilet.score} | Sunkighet: ${toilet.dankness}</small>
            `;
            /*const reviewButton = document.createElement("button");
            reviewButton.textContent = 'Review';
            reviewButton.className = "review-button";

            // Connection to rateAToilet
            reviewButton.onclick = (e) => {
                e.stopPropagation(); // Makes the review button the only place to click
                selectToilet(toilet, li); 
            };
            li.appendChild(reviewButton);
            */
            li.onclick = () => selectToilet(toilet, li);
            listContainer.appendChild(li);
        });
    }

    const toiletSlider = document.getElementById("filterAmount");
    const sliderLabelValue = document.getElementById("toiletAmount");

    if (toiletSlider && sliderLabelValue) {
        // Updates number next to slider
        toiletSlider.addEventListener("input", function() {
            sliderLabelValue.textContent = this.value;
        });
        //Calls backend handler
        toiletSlider.addEventListener("change", function() {
            getAllToilets();
        });
    }
    // The sort buttons 
    const standardView = document.getElementById("sortName");
    const sortWithPoints = document.getElementById("sortScore");
    const sortWithDank = document.getElementById("sortDankness");

    // Filter event listners
    document.getElementById("filterTable")?.addEventListener("change", getAllToilets);
    document.getElementById("filterFree")?.addEventListener("change", getAllToilets);
    document.getElementById("filterAmount")?.addEventListener("input", getAllToilets);
    
    // Events for what is clicked
    if (standardView) standardView.onclick = () => sidebarContent('name');
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
    button.addEventListener('click', function() {
        buttons.forEach(btn => btn.classList.remove('active'));
        
        this.classList.add('active');
        
    });
});