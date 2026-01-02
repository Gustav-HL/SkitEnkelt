// Event Listner to load script first
document.addEventListener("DOMContentLoaded", function () {
    // Load and display map
    var map = L.map('map').setView([55.605, 13.003], 13);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap'
    }).addTo(map);

    // Temp data
    const toilets = [
        { 
            name: "Nobeltorget", 
            lat: 55.5916569774, 
            lng: 13.0202647274, 
            category: "lyxig", 
            score: 85, 
            dankness: 70 },
        { 
            name: "Parktoalett", 
            lat: 55.583, 
            lng: 13.0230, 
            category: "sunk", 
            score: 0, 
            dankness: 90 },
        { 
            name: "Sofielund", 
            lat: 55.589, 
            lng: 13.0155, 
            category: "standard", 
            score: 50, 
            dankness: 55 },
        {
            name: "Havet", 
            lat: 55.389, 
            lng: 13.0255, 
            category: "standard", 
            score: 50, 
            dankness: 55 },
        { 
            name: "Casa Björnheimer", 
            lat: 55.602, 
            lng: 13.0135, 
            category: "EPIC", 
            score: 100, 
            dankness: 100 }
    ];
    // Dict Stores marker by name 
    const markerDict = {};

    // Fcuntion to move the map and open a popup when users select a toilet
    function selectToilet(toilet) {
        const input = document.getElementById("address");
        
        if (input) input.value = toilet.name;

        map.flyTo([toilet.lat, toilet.lng], 16);
        if (toilet.name in markerDict) {
                markerDict[toilet.name].openPopup();
        }
    }
    // Function to sort and show sidebar list
    function sidebarContent(sortBy = 'name') {
        const listContainer = document.getElementById("toa-list");

        listContainer.innerHTML = ""; 

        const sorted = [...toilets].sort((toiletA, toiletB) => {
            // Alphabet sort
            if (sortBy === 'name') {
                return toiletA.name.localeCompare(toiletB.name);
            }

            // Sort by numver b - a to ensure the highest values appear at top
            return toiletB[sortBy] - toiletA[sortBy];
        });
        // Loop to create list of the data objects with HTML
        sorted.forEach(toilet => {
            const popupContent = `
                <div style="line-height: 1.5;">
                    <strong style="font-size: 1.2em;">${toilet.name}</strong><br>
                    <b>Kategori:</b> ${toilet.category}<br>
                    <b>Poäng:</b> ${toilet.score}/100<br>
                    <b>Sunkighet:</b> ${toilet.dankness}/100
                </div>
            `;

            if (!markerDict[toilet.name]) {
                const marker = L.marker([toilet.lat, toilet.lng])
                    .addTo(map)
                    .bindPopup(popupContent);
                markerDict[toilet.name] = marker;
            } else {
                markerDict[toilet.name].setPopupContent(popupContent);
            }
            // Create the list with clickable elements
            const li = document.createElement("li");
            li.style.cursor = "pointer";
            li.style.padding = "10px";
            li.style.borderBottom = "1px solid #ddd";
            li.innerHTML = `
                <strong>${toilet.name}</strong><br>
                <small> Poäng: ${toilet.score} || Sunkighet: ${toilet.dankness}</small>
            `;
            
            li.onclick = () => selectToilet(toilet);
            listContainer.appendChild(li);
        });
    }

    // The sort buttons
    const standardView = document.getElementById("sortName");
    const sortByPoints = document.getElementById("sortScore");
    const sortByDank = document.getElementById("sortDankness");
    // Events for what is clicked
    if (standardView) standardView.onclick = () => sidebarContent('name');
    if (sortByPoints) sortByPoints.onclick = () => sidebarContent('score');
    if (sortByDank) sortByDank.onclick = () => sidebarContent('dankness');

    // Run function on startup
    sidebarContent();
});