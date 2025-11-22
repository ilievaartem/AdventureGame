# CryptoTracker - Real-time Cryptocurrency Monitoring with AML

## Overview

CryptoTracker is a web-based cryptocurrency tracking application with integrated Anti-Money Laundering (AML) detection capabilities. Built using libGDX framework, it provides real-time cryptocurrency price monitoring and transaction analysis.

## Features

### 1. **Real-time Cryptocurrency Tracking**
- Monitor top cryptocurrencies (BTC, ETH, USDT, BNB, SOL, XRP, ADA, DOGE)
- Live price updates every 5 seconds
- 24-hour price change indicators
- Market cap and volume information

### 2. **AML (Anti-Money Laundering) Analysis**
The application includes sophisticated AML detection logic that analyzes cryptocurrency transactions for suspicious patterns:

#### Risk Levels
- **Low Risk (0-30)**: Normal transaction patterns
- **Medium Risk (30-70)**: Some suspicious indicators present
- **High Risk (70-90)**: Multiple red flags detected
- **Critical Risk (90-100)**: Severe AML concerns

#### Detection Features
- **High Volume Detection**: Flags wallets with transaction volumes exceeding $1,000,000
- **Suspicious Transaction Monitoring**: Identifies transactions over $10,000
- **Structuring Detection**: Detects patterns of breaking large transactions into smaller ones to avoid reporting thresholds
- **Mixing Service Detection**: Identifies potential use of cryptocurrency mixing services
- **Rapid Transaction Analysis**: Flags rapid high-value transactions

### 3. **Wallet Analysis**
- View wallet balances across multiple cryptocurrencies
- Transaction history tracking
- Real-time AML risk assessment
- Detailed flag reporting for suspicious activities

## Project Structure

```
core/
  └── src/main/java/com/cryptotracker/
      ├── CryptoTrackerApp.java          # Main application
      ├── aml/
      │   ├── AMLAnalyzer.java           # AML detection engine
      │   ├── AMLAnalysisResult.java     # Analysis results
      │   └── AMLRiskLevel.java          # Risk level definitions
      ├── models/
      │   ├── Cryptocurrency.java        # Cryptocurrency data model
      │   ├── Transaction.java           # Transaction data model
      │   └── Wallet.java                # Wallet data model
      ├── services/
      │   ├── CryptoDataService.java     # Cryptocurrency data management
      │   └── WalletService.java         # Wallet data management
      └── screens/
          └── CryptoTrackerScreen.java   # Main UI screen

html/
  └── webapp/
      └── index.html                     # Web interface

lwjgl3/
  └── src/main/java/com/cryptotracker/lwjgl3/
      └── DesktopLauncher.java          # Desktop launcher
```

## Building and Running

### Desktop Version
```bash
./gradlew lwjgl3:run
```

### Web Version (HTML5)
```bash
./gradlew html:superDev
```
Then open http://localhost:8080/html in your browser.

### Build for Distribution
```bash
# Desktop JAR
./gradlew lwjgl3:jar

# Web build
./gradlew html:dist
```

## AML Detection Algorithm

The AML analyzer uses multiple heuristics to assess transaction risk:

1. **Transaction Volume Analysis**
   - Calculates total transaction volume
   - Assigns risk scores based on volume thresholds

2. **Pattern Recognition**
   - Detects structuring (multiple similar-amount transactions)
   - Identifies round-number transactions (potential structuring)

3. **Mixing Service Detection**
   - Analyzes transaction patterns for signs of cryptocurrency mixing
   - Detects multiple small transactions to different addresses

4. **Risk Scoring**
   - Combines multiple factors to calculate overall risk score
   - Automatically assigns risk level based on score

## Technologies Used

- **LibGDX**: Cross-platform game framework
- **GWT**: Google Web Toolkit for HTML5 deployment
- **LWJGL3**: Desktop backend
- **Java 8+**: Core programming language

## Future Enhancements

- Integration with real cryptocurrency APIs (CoinGecko, CoinMarketCap)
- Real-time blockchain transaction monitoring
- Machine learning-based AML detection
- User authentication and wallet management
- Historical transaction analysis and charting
- Export AML reports to PDF
- Multi-language support
- Dark/Light theme toggle

## License

This project is part of the AdventureGame repository and follows its licensing terms.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.
