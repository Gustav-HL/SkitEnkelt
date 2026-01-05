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
    //     L.marker([t.lat, t.lng]).addTo(map).bindPopup(t.name);
    // });
    
    let toilets = [];
    // Fetch Data from backend
    async function getAllToilets(){
        const options = {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        };
        const res = await fetch("http://localhost:7070/toilets", options);
        const data = await res.json();
        toilets = data;
        console.log(data);
        sidebarContent(); 
        // data.forEach(t => {
        //     L.marker([t.lat, t.lng]).addTo(map).bindPopup(t.name);
        //     const li = document.createElement("li");
        //     const reviewButton = document.createElement("button");
        //     document.querySelector("#toa-item")
        //     li.textContent = `${t.name}, ${t.lat}, ${t.lng}`;
        //     reviewButton.textContent = 'Review'
        //     li.append(reviewButton)
        //     document.querySelector("#toa-list").append(li);
        // });
    }
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

    // Fcuntion to move the map and open a popup when users select a toilet
    function selectToilet(toilet, element) {
        const input = document.getElementById("address");
        if (input) input.value = toilet.name;

        map.flyTo([toilet.lat, toilet.lng], 16);
        if (toilet.name in markerDict) {
            markerDict[toilet.name].openPopup();
        }

        // Removes selected syling from lists
        document.querySelectorAll('.toilet_card').forEach(li => li.classList.remove('selected'));

        // Adds selected styling to the just clicked one
        if (element) {
            element.classList.add('selected');
        }
    }

    // Function to sort and show sidebar list
    function sidebarContent(sortWith = 'name') {
        const listContainer = document.getElementById("toa-list");
        listContainer.innerHTML = ""; 

        // Sort list sort with name otherwise numberd with score highest to lowest
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
            if (!markerDict[toilet.name]) {
                const marker = L.marker([toilet.lat, toilet.lng], { icon: brownIcon }) 
                    .addTo(map)
                    .bindPopup(popupContent);
                markerDict[toilet.name] = marker;
            }
            // HTML for the list
            const li = document.createElement("li");
            li.className = "toilet_card";
            li.innerHTML = `
                <strong>${toilet.name}</strong>
                <small>Poäng: ${toilet.score} | Sunkighet: ${toilet.dankness}</small>
            `;
            const reviewButton = document.createElement("button");
            reviewButton.textContent = 'Review';
            reviewButton.className = "review-button";

            // Connection to rateAToilet
            reviewButton.onclick = (e) => {
                e.stopPropagation(); // Makes the review button the only place to click
                rateAToilet(); 
            };
            li.appendChild(reviewButton);
            li.onclick = () => selectToilet(toilet, li);
            listContainer.appendChild(li);
        });
    }

    // The sort buttons 
    const standardView = document.getElementById("sortName");
    const sortWithPoints = document.getElementById("sortScore");
    const sortWithDank = document.getElementById("sortDankness");
    
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