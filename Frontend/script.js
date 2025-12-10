//Map
var map = L.map('map').setView([55.605, 13.003], 13);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap'
}).addTo(map);



// TOALETT-DATABAS, ska kompletteras med getmetoder från vårt API

const toilets = [
    {
        name: "Toalett #1 – Ribersborg Lyx",
        lat: 55.591,
        lng: 13.0202,
        category: "lyxig"
    },
    {
        name: "Toalett #2 – Sunkig Parktoalett",
        lat: 55.583,
        lng: 13.0230,
        category: "sunk"
    },
    {
        name: "Toalett #3 – Standard Sofielund",
        lat: 55.589,
        lng: 13.0155,
        category: "standard"
    }
];

// Rita ut markers från listan
toilets.forEach(t => {
    L.marker([t.lat, t.lng]).addTo(map).bindPopup(t.name);
});


