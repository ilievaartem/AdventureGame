# CryptoTracker Quick Start Guide

## What is CryptoTracker?

CryptoTracker is a cryptocurrency monitoring application with built-in Anti-Money Laundering (AML) detection. It tracks cryptocurrency prices in real-time and analyzes transaction patterns to identify potential money laundering activities.

## Quick Start

### Running the Application

**Desktop Version:**
```bash
./gradlew lwjgl3:run
```

**Web Version:**
```bash
./gradlew html:superDev
# Then open http://localhost:8080/html
```

## Main Features

### 📊 Cryptocurrency Dashboard
View real-time prices for major cryptocurrencies:
- Bitcoin (BTC)
- Ethereum (ETH)
- Tether (USDT)
- Binance Coin (BNB)
- Solana (SOL)
- Ripple (XRP)
- Cardano (ADA)
- Dogecoin (DOGE)

Prices update automatically every 5 seconds.

### 💼 Wallet Analysis
Monitor cryptocurrency wallets with:
- Total balance across all cryptocurrencies
- Individual asset balances
- Transaction history
- Wallet address information

### 🚨 AML Detection
Automatic risk assessment that identifies:

**Low Risk (Green)** - Normal activity
- Regular transaction patterns
- Small to medium transaction volumes
- No suspicious patterns

**Medium Risk (Yellow)** - Requires monitoring
- Moderate transaction volumes
- Some unusual patterns
- May need review

**High Risk (Red)** - Investigation recommended
- High transaction volumes (>$1M)
- Multiple suspicious transactions
- Potential structuring patterns

**Critical Risk (Red)** - Immediate action required
- Severe AML concerns
- Multiple high-risk indicators
- Strong evidence of suspicious activity

### 🔍 What Gets Flagged?

1. **High Transaction Volume**
   - Total volume exceeds $1,000,000
   
2. **Large Individual Transactions**
   - Single transactions over $10,000
   
3. **Structuring**
   - Multiple transactions of similar amounts
   - Pattern of breaking large amounts into smaller ones
   
4. **Mixing Services**
   - Multiple small transactions to different addresses
   - Pattern consistent with cryptocurrency mixing

5. **Rapid High-Value Transactions**
   - Many large transactions in short time period

## Understanding the Display

### Cryptocurrency List
```
Symbol    Price        24h Change
BTC       $45,000.00   +2.50%  (Green = increase, Red = decrease)
ETH       $2,500.00    -1.20%
```

### Wallet Information
```
Address: 0x742d...bEb0 (shortened for display)
Total Balance: $45,500.00
```

### AML Analysis
```
AML Risk: High Risk (Red color)
Risk Score: 75
AML Alerts:
- High transaction volume detected
- Multiple suspicious transactions
```

## Sample Data

The application comes pre-loaded with sample data:
- 8 major cryptocurrencies with realistic prices
- Sample wallet with multiple balances
- Sample transaction history for AML testing

## Configuration

### Adjusting AML Thresholds

Edit the constants in `AMLAnalyzer.java`:
```java
private static final double SUSPICIOUS_AMOUNT_THRESHOLD = 10000.0;
private static final int RAPID_TRANSACTION_WINDOW = 60; // seconds
```

### Modifying Update Interval

Edit `CryptoTrackerScreen.java`:
```java
private static final float UPDATE_INTERVAL = 5.0f; // seconds
```

## Color Coding

- **Green**: Positive changes, low risk
- **Yellow**: Medium risk, requires monitoring
- **Red**: Negative changes, high/critical risk
- **White**: Neutral information

## Keyboard/Mouse Controls

- The application is fully mouse-driven
- Scroll to view more information (if implemented in future)
- Click interactive elements (if added in future)

## Troubleshooting

### Application Won't Start
- Ensure Java 8 or higher is installed
- Check that all dependencies are downloaded
- Try running `./gradlew clean` first

### Font Not Loading
- The app will fallback to default font automatically
- Check that `OpenSans2.ttf` exists in assets folder

### Build Errors
- Run `./gradlew clean build`
- Check internet connection for dependency downloads
- Ensure Gradle wrapper is executable: `chmod +x gradlew`

## Technical Specifications

- **Framework**: libGDX (cross-platform)
- **Language**: Java 8+
- **Platforms**: Desktop (LWJGL3), Web (GWT/HTML5)
- **Resolution**: 1920x1080 (recommended), scalable

## Privacy & Security

- No data is sent to external servers
- All calculations are performed locally
- Sample data is for demonstration only
- No real cryptocurrency transactions are performed

## Next Steps

To use with real data:
1. Integrate cryptocurrency API (e.g., CoinGecko)
2. Connect to blockchain explorers (e.g., Etherscan)
3. Implement user authentication
4. Add database for persistent storage
5. Configure for specific regulatory requirements

## Support

For issues or questions:
- Check the IMPLEMENTATION_GUIDE.md for technical details
- Review CRYPTOTRACKER_README.md for feature documentation
- Submit issues on GitHub

## License

This application is part of the AdventureGame repository.

---

**⚠️ Disclaimer**: This is a demonstration application. AML detection is for educational purposes and should not be used as the sole basis for compliance decisions. Consult with legal and compliance professionals for production use.
