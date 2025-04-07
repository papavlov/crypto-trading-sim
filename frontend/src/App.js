import React, { useEffect, useState } from "react";
import './App.css'; 

const USER_ID = 1;

export default function CryptoDashboard() {
  const [username, setUsername] = useState("Loading...");
  const [userId, setUserId] = useState("Loading...");
  const [balance, setBalance] = useState(0);  // Set initial balance as 0
  const [prices, setPrices] = useState([]);
  const [transactions, setTransactions] = useState([]);

  const [buySymbol, setBuySymbol] = useState("");
  const [buyAmount, setBuyAmount] = useState(0);
  const [sellSymbol, setSellSymbol] = useState("");
  const [sellAmount, setSellAmount] = useState(0);

  // Fetch user info, prices, balance, and transactins 
  useEffect(() => {
    fetchUserInfo();
    fetchPrices();
    fetchTransactionHistory();
    const interval = setInterval(fetchPrices, 2000); //update prices every 2 seconds
    return () => clearInterval(interval); 
  }, []);

  const fetchUserInfo = async () => {
    try {
      const res = await fetch(`http://localhost:8080/api/users/${USER_ID}`);
      const data = await res.json();
      if (data) {
        setUsername(data.username);
        setUserId(data.id);
        setBalance(data.balance || 0); 
      }
    } catch (error) {
      console.error("Failed to fetch user info:", error);
    }
  };

  const fetchPrices = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/prices/top20");
      const data = await res.json();
      setPrices(Object.values(data) || []); 
    } catch (error) {
      console.error("Failed to fetch prices:", error);
    }
  };

  const fetchTransactionHistory = async () => {
    try {
      const res = await fetch(`http://localhost:8080/api/transactions/${USER_ID}`);
      const data = await res.json();
      setTransactions(data || []); //ensure transactions isnot undefined
    } catch (error) {
      console.error("Failed to fetch transaction history:", error);
    }
  };

  const handleBuy = async (e) => {
    e.preventDefault();
    const params = new URLSearchParams({
      cryptoName: buySymbol,
      symbol: buySymbol,
      quantity: buyAmount,
    });

    try {
      const res = await fetch(`http://localhost:8080/api/users/${USER_ID}/buy?${params.toString()}`, {
        method: "POST",
      });
      const msg = await res.text();
      if (!res.ok) throw new Error(msg);
      alert("✅ " + msg);
      fetchUserInfo(); //refresh user info, incl balance
      fetchTransactionHistory();
      setBuySymbol("");
      setBuyAmount(0);
    } catch (error) {
      alert("❌ Buy failed: " + error.message);
    }
  };

  const handleSell = async (e) => {
    e.preventDefault();
    const params = new URLSearchParams({
      cryptoName: sellSymbol,
      symbol: sellSymbol,
      quantity: sellAmount,
    });

    try {
      const res = await fetch(`http://localhost:8080/api/users/${USER_ID}/sell?${params.toString()}`, {
        method: "POST",
      });
      const msg = await res.text();
      if (!res.ok) throw new Error(msg);
      alert("✅ " + msg);
      fetchUserInfo(); 
      fetchTransactionHistory();
      setSellSymbol("");
      setSellAmount(0);
    } catch (error) {
      alert("❌ Sell failed: " + error.message);
    }
  };

  const handleReset = async () => {
    if (!window.confirm("Are you sure you want to reset your account?")) return;

    try {
      const res = await fetch(`http://localhost:8080/api/users/${USER_ID}/reset`, {
        method: "POST",
      });
      const msg = await res.text();
      if (!res.ok) throw new Error(msg);
      alert("🔁 " + msg);
      fetchUserInfo(); //refresh user info
      fetchTransactionHistory();
    } catch (error) {
      alert("❌ Reset failed: " + error.message);
    }
  };

  return (
    <div style={{ padding: "1rem" }}>
      <h1>Crypto Trading Simulator</h1>

      <div>
        <h2>Username: <span>{username}</span></h2>
        <h2>User ID: <span>{userId}</span></h2>
      </div>

      <h2>Balance: <span>${balance.toFixed(2)}</span></h2>

      <h2>Top 20 Crypto Prices</h2>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Symbol</th>
            <th>Price (USD)</th>
          </tr>
        </thead>
        <tbody>
          {prices.map((p, i) => (
            <tr key={i}>
              <td>{p.name}</td>
              <td>{p.symbol}</td>
              <td>${(p.price || 0).toFixed(2)}</td> {}
            </tr>
          ))}
        </tbody>
      </table>

      <h3> Buy Cryptocurrency</h3>
      <form onSubmit={handleBuy}>
        <input
          type="text"
          placeholder="Symbol (e.g., BTC)"
          value={buySymbol}
          onChange={(e) => setBuySymbol(e.target.value.toUpperCase())}
          required
        />
        <input
          type="number"
          min="0"
          step="any"
          placeholder="Amount"
          value={buyAmount}
          onChange={(e) => setBuyAmount(parseFloat(e.target.value))}
          required
        />
        <button type="submit">Buy</button>
      </form>

      <h3> Sell Cryptocurrency</h3>
      <form onSubmit={handleSell}>
        <input
          type="text"
          placeholder="Symbol (e.g., BTC)"
          value={sellSymbol}
          onChange={(e) => setSellSymbol(e.target.value.toUpperCase())}
          required
        />
        <input
          type="number"
          min="0"
          step="any"
          placeholder="Amount"
          value={sellAmount}
          onChange={(e) => setSellAmount(parseFloat(e.target.value))}
          required
        />
        <button type="submit">Sell</button>
      </form>

      <button onClick={handleReset}>🔁 Reset Account</button>

      <h3> Transaction History</h3>
      <table>
        <thead>
          <tr>
            <th>Type</th>
            <th>Symbol</th>
            <th>Amount</th>
            <th>Price</th>
            <th>Total</th>
            <th>Timestamp</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((tx, i) => (
            <tr key={i}>
              <td>{tx.type}</td>
              <td>{tx.symbol}</td>
              <td>{tx.quantity}</td>
              <td>${(tx.price || 0).toFixed(2)}</td> {}
              <td>${(tx.quantity * (tx.price || 0)).toFixed(2)}</td> {}
              <td>{new Date(tx.timestamp).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
