//Map
var map = L.map('map').setView([55.605, 13.003], 13);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap'
}).addTo(map);



// TOALETT-DATABAS, ska kompletteras med getmetoder från vårt API

const toilets = [
    {
        name: "Nobeltorget",
        lat: 55.5916569774,
        lng: 13.0202647274,
        category: "lyxig"
    },
    {
        name: "Parktoalett",
        lat: 55.583,
        lng: 13.0230,
        category: "sunk"
    },
    {
        name: "Sofielund",
        lat: 55.589,
        lng: 13.0155,
        category: "standard"
    }
];

// Rita ut markers från listan
toilets.forEach(t => {
    L.marker([t.lat, t.lng]).addTo(map).bindPopup(t.name);
});


// AUTOCOMPLETE

var input = document.getElementById("address");
var suggestions = document.getElementById("suggestions");

input.addEventListener("input", function () {
    const text = input.value.toLowerCase();
    suggestions.innerHTML = "";

    if (text.length < 1) {
        suggestions.style.display = "none";
        return;
    }

    const matches = toilets.filter(t =>
        t.name.toLowerCase().includes(text)
    );

    if (matches.length === 0) {
        suggestions.style.display = "none";
        return;
    }

    matches.forEach(t => {
        const item = document.createElement("div");
        item.className = "suggestion-item";
        item.innerText = t.name;

        item.addEventListener("click", function () {
            selectToilet(t);
        });

        suggestions.appendChild(item);
    });

    suggestions.style.display = "block";
});

// ENTER → välj första, borde ändras så att man kan använda pilarna för att välja olika menyval.
input.addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
        const first = document.querySelector(".suggestion-item");
        if (first) first.click();
    }
});


// SELECT TOILET
function selectToilet(t) {
    suggestions.style.display = "none";
    input.value = t.name;

    const latlng = [t.lat, t.lng];
    map.setView(latlng, 16);

    L.marker(latlng)
        .addTo(map)
        .bindPopup(t.name)
        .openPopup();
}
