const priceTableBody = document.getElementById("priceTableBody");

function fetchPrices() {
  fetch("http://localhost:8080/api/prices/top20") 
    .then(res => res.json())
    .then(data => {
      priceTableBody.innerHTML = "";

      // If the data is an object Object.values() gets array
      Object.values(data).forEach(price => {
        const row = document.createElement("tr");

        row.innerHTML = `
          <td>${price.name}</td>
          <td>${price.symbol}</td>
          <td>$${price.price.toFixed(2)}</td>
        `;

        priceTableBody.appendChild(row);
      });

        //Log the updated prices to the console
        console.log("Updated Prices:", data);
    })
    .catch(err => {
      console.error("Failed to fetch prices:", err);
    });
}

fetchPrices();
setInterval(fetchPrices, 2000); //refresh every 2 seconds
