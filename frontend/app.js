const priceTableBody = document.getElementById("priceTableBody");
const accountBalanceSpan = document.getElementById("accountBalance");
const transactionHistoryBody = document.getElementById("transactionHistory");

const buyForm = document.getElementById("buyForm");
const sellForm = document.getElementById("sellForm");
const resetBtn = document.getElementById("resetBtn");

const USER_ID = 1;

async function fetchUserInfo() {
  try {
    const response = await fetch('http://localhost:8080/api/users/1');
    const data = await response.json();
    
    
    document.getElementById('username').textContent = data.username;
    document.getElementById('userId').textContent = data.id;
  } catch (error) {
    console.error('Failed to fetch user info:', error);
  }
}

// === 1. FETCH PRICES ===
function fetchPrices() {
  fetch("http://localhost:8080/api/prices/top20")
    .then(res => res.json())
    .then(data => {
      priceTableBody.innerHTML = "";

      Object.values(data).forEach(price => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${price.name}</td>
          <td>${price.symbol}</td>
          <td>$${price.price.toFixed(2)}</td>
        `;
        priceTableBody.appendChild(row);
      });
    })
    .catch(err => {
      console.error("Failed to fetch prices:", err);
    });
}

// === 2. FETCH BALANCE ===
function fetchBalance() {
  fetch(`http://localhost:8080/api/users/${USER_ID}`)
    .then(res => res.json())
    .then(user => {
      accountBalanceSpan.textContent = `$${user.balance.toFixed(2)}`;
    })
    .catch(err => {
      console.error("Failed to fetch balance:", err);
    });
}

// === 3. FETCH TRANSACTION HISTORY ===
function fetchTransactionHistory() {
  fetch(`http://localhost:8080/api/transactions/${USER_ID}`)
    .then(res => res.json())
    .then(data => {
      transactionHistoryBody.innerHTML = "";

      data.forEach(tx => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${tx.type}</td>
          <td>${tx.symbol}</td>
          <td>${tx.quantity}</td>
          <td>$${tx.price.toFixed(2)}</td>
          <td>$${(tx.quantity * tx.price).toFixed(2)}</td>
          <td>${new Date(tx.timestamp).toLocaleString()}</td>
        `;
        transactionHistoryBody.appendChild(row);
      });
    })
    .catch(err => {
      console.error("Failed to fetch transaction history:", err);
    });
}

// === 4. BUY CRYPTO ===
buyForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const symbol = document.getElementById("buySymbol").value.toUpperCase();
  const quantity = parseFloat(document.getElementById("buyAmount").value);

  const params = new URLSearchParams({
    cryptoName: symbol, //symbol for name
    symbol,
    quantity
  });

  fetch(`http://localhost:8080/api/users/${USER_ID}/buy?${params.toString()}`, {
    method: "POST"
  })
    .then(res => res.text().then(msg => {
      if (!res.ok) throw new Error(msg);
      return msg;
    }))
    .then(msg => {
      alert("✅ " + msg);
      fetchBalance();
      fetchTransactionHistory();
      buyForm.reset();
    })
    .catch(err => {
      alert("❌ Buy failed: " + err.message);
    });
});

// === 5. SELL CRYPTO ===
sellForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const symbol = document.getElementById("sellSymbol").value.toUpperCase();
  const quantity = parseFloat(document.getElementById("sellAmount").value);

  const params = new URLSearchParams({
    cryptoName: symbol,
    symbol,
    quantity
  });

  fetch(`http://localhost:8080/api/users/${USER_ID}/sell?${params.toString()}`, {
    method: "POST"
  })
    .then(res => res.text().then(msg => {
      if (!res.ok) throw new Error(msg);
      return msg;
    }))
    .then(msg => {
      alert("✅ " + msg);
      fetchBalance();
      fetchTransactionHistory();
      sellForm.reset();
    })
    .catch(err => {
      alert("❌ Sell failed: " + err.message);
    });
});

// === 6. RESET ACCOUNT ===
resetBtn.addEventListener("click", () => {
  if (!confirm("Are you sure you want to reset your account?")) return;

  fetch(`http://localhost:8080/api/users/${USER_ID}/reset`, {
    method: "POST"
  })
    .then(res => res.text().then(msg => {
      if (!res.ok) throw new Error(msg);
      return msg;
    }))
    .then(msg => {
      alert("🔁 " + msg);
      fetchBalance();
      fetchTransactionHistory();
    })
    .catch(err => {
      alert("❌ Reset failed: " + err.message);
    });
});

// === INITIALIZE EVERYTHING ===
fetchUserInfo();
fetchPrices();
fetchBalance();
fetchTransactionHistory();
setInterval(fetchPrices, 2000); //update prices every 2s
