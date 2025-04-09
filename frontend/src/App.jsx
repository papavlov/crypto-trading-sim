import React, { useEffect, useState } from "react";
import './App.css'; 
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowDown, faArrowUp } from '@fortawesome/free-solid-svg-icons';
import HistoryIcon from '@mui/icons-material/History';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';

const USER_ID = 1;

export default function CryptoDashboard() {
  const [username, setUsername] = useState("Loading...");
  const [userId, setUserId] = useState("Loading...");
  const [balance, setBalance] = useState(10000);
  const [prices, setPrices] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [holdings, setHoldings] = useState([]);

  const [buySymbol, setBuySymbol] = useState("");
  const [buyAmount, setBuyAmount] = useState(0);
  const [sellSymbol, setSellSymbol] = useState("");
  const [sellAmount, setSellAmount] = useState(0);
  

  useEffect(() => {
    fetchUserInfo();
    fetchPrices();
    fetchTransactionHistory();
    fetchHoldings();
    const interval = setInterval(fetchPrices, 2000);
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
      setTransactions(data || []); 
    } catch (error) {
      console.error("Failed to fetch transaction history:", error);
    }
  };

const fetchHoldings = async () => {
  try {
    const res = await fetch(`http://localhost:8080/api/transactions/${USER_ID}/holdings`);
    const data = await res.json();
    console.log("Fetched Holdings:", data); //Log to check the format

    //converting the map object to an array of {symbol, amount} pairs
    if (data && typeof data === 'object') {
      const holdingsArray = Object.entries(data)
        .map(([symbol, amount]) => ({
          symbol,
          totalAmount: amount,
          totalSpent: amount * prices.find(p => p.symbol === symbol)?.price || 0, 
          averagePrice: amount * prices.find(p => p.symbol === symbol)?.price / amount || 0
        }))
        .filter(h => h.totalAmount > 0); //Filter out cryptos with 0 holdings
  
      setHoldings(holdingsArray);
    } else {
      setHoldings([]); //Handle unexpected data format
    }
  } catch (error) {
    console.error("Failed to fetch holdings:", error);
    setHoldings([]); //Set an empty array in case of error
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
      fetchUserInfo();
      fetchTransactionHistory();
      fetchHoldings();
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
      fetchHoldings();
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
      fetchUserInfo();
      fetchTransactionHistory();
      fetchHoldings();
    } catch (error) {
      alert("❌ Reset failed: " + error.message);
    }
  };

  return (
    <div style={{ padding: "1rem" }}>
      <nav className="nav-pane">
        <h1>Crypto Trading Simulator</h1>
      </nav>

      {/* User Info */}
      <div className="user-info-container">
        <div className="user-info-item-heading">
          <h2>Account</h2>
        </div>
        <div className="user-info-item">
          <h2><AccountCircleIcon /> Username: <span>{username}</span></h2>
        </div>
        <div className="user-info-item">
          <h2><AccountCircleIcon /> User ID: <span>{userId}</span></h2>
        </div>
        <div className="user-info-item">
          <h2><AccountBalanceWalletIcon /> Balance: <span>${balance.toFixed(2)}</span></h2>
        </div>
        <button type="button" className="reset-account" onClick={handleReset}>Reset Account?</button>
      </div>

      {/* Crypto Prices and Forms */}
      <div style={{ display: "flex", alignItems: "flex-start", gap: "2rem" }}>
        <div style={{ flex: "2" }}>
          <h2><AttachMoneyIcon /> Top 20 Crypto Prices</h2>
          <table className="transaction-table">
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
                  <td>${(p.price || 0).toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div style={{ flex: "1" }}>
          <h3><FontAwesomeIcon icon={faArrowDown} /> Buy Cryptocurrency</h3>
          <form onSubmit={handleBuy} className="crypto-form">
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
              step="0.0001"
              placeholder="Amount"
              value={buyAmount}
              onChange={(e) => setBuyAmount(parseFloat(e.target.value))}
              required
            />
            <button type="submit">Buy</button>
          </form>

          <h3><FontAwesomeIcon icon={faArrowUp} /> Sell Cryptocurrency</h3>
          <form onSubmit={handleSell} className="crypto-form">
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
              step="0.0001"
              placeholder="Amount"
              value={sellAmount}
              onChange={(e) => setSellAmount(parseFloat(e.target.value))}
              required
            />
            <button type="submit">Sell</button>
          </form>
        </div>
      </div>

      {/* Holdings Section */}
      <h2>📊 Your Holdings</h2>

  <table className="transaction-table">
    <thead>
      <tr>
        <th>Symbol</th>
        <th>Total Amount</th>
        <th>Total Spent (USD)</th>
        
      </tr>
    </thead>
    <tbody>
      {holdings.map((h, i) => (
        <tr key={i}>
          <td>{h.symbol}</td>
          <td>{h.totalAmount.toFixed(4)}</td>
          <td>${h.totalSpent.toFixed(2)}</td>
          
        </tr>
      ))}
    </tbody>
  </table>




      {/* Transaction History */}
      <h2><HistoryIcon /> Transaction History</h2>
      <table className="transaction-table">
        <thead>
          <tr>
            <th>Type</th>
            <th>Symbol</th>
            <th>Amount</th>
            <th>Price</th>
            <th>Total</th>
            <th>Timestamp</th>
            <th>Profit/Loss</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((tx, i) => (
            <tr key={i}>
              <td>{tx.type}</td>
              <td>{tx.symbol}</td>
              <td>{tx.quantity}</td>
              <td>${(tx.price || 0).toFixed(2)}</td>
              <td>${(tx.quantity * (tx.price || 0)).toFixed(2)}</td>
              <td>{new Date(tx.timestamp).toLocaleString()}</td>
              <td className={`profit ${tx.profitLoss > 0 ? 'positive' : tx.profitLoss < 0 ? 'negative' : ''}`}>
                {tx.profitLoss !== null ? `$${tx.profitLoss.toFixed(2)}` : '-'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
