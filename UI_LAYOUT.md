# CryptoTracker UI Layout

## Main Screen Layout (ASCII Representation)

```
╔════════════════════════════════════════════════════════════════════════════╗
║                                                                            ║
║   CryptoTracker - Real-time Crypto Monitoring with AML                    ║
║                                                                            ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║   Top Cryptocurrencies                                                    ║
║   ────────────────────────────────────────────────────────────────────   ║
║                                                                            ║
║   BTC         $45,000.00        +2.50%  ← Green (price increasing)        ║
║   ETH         $2,500.00         -1.20%  ← Red (price decreasing)          ║
║   USDT        $1.00             +0.01%  ← Green                           ║
║   BNB         $320.00           +3.80%  ← Green                           ║
║   SOL         $110.00           +5.20%  ← Green                           ║
║   XRP         $0.62             -0.80%  ← Red                             ║
║   ADA         $0.58             +1.50%  ← Green                           ║
║   DOGE        $0.12             +7.30%  ← Green                           ║
║                                                                            ║
║                                                                            ║
║   Wallet Analysis                                                         ║
║   ────────────────────────────────────────────────────────────────────   ║
║                                                                            ║
║   Address: 0x742d...bEb0                                                 ║
║   Total Balance: $45,500.00                                              ║
║                                                                            ║
║   AML Risk: High Risk  ← Red/Yellow/Green based on score                 ║
║   Risk Score: 75                                                          ║
║                                                                            ║
║   AML Alerts:  ← Yellow text                                             ║
║   - High transaction volume detected                                     ║
║   - Multiple suspicious transactions                                     ║
║                                                                            ║
║   Total Transactions: 13                                                  ║
║   Suspicious Transactions: 8  ← Yellow text                              ║
║                                                                            ║
╚════════════════════════════════════════════════════════════════════════════╝

Background Color: Dark Blue (#1a1a2e)
Text Colors:
  - White: Regular text
  - Green: Positive changes, low risk
  - Yellow: Warnings, medium risk
  - Red: Negative changes, high/critical risk
```

## Color Scheme

### Background
- **Main Background**: Dark Blue (#1a1a2e) - Professional crypto interface feel

### Text Colors
```
┌─────────────────┬──────────────┬─────────────────────────────────┐
│ Element         │ Color        │ Usage                           │
├─────────────────┼──────────────┼─────────────────────────────────┤
│ Title           │ White        │ Main headings                   │
│ Headers         │ Light Blue   │ Section headers                 │
│ Normal Text     │ White        │ Regular information             │
│ Price Increase  │ Green        │ Positive 24h change             │
│ Price Decrease  │ Red          │ Negative 24h change             │
│ Low Risk        │ Green        │ AML score 0-29                  │
│ Medium Risk     │ Yellow       │ AML score 30-69                 │
│ High Risk       │ Red          │ AML score 70-89                 │
│ Critical Risk   │ Dark Red     │ AML score 90-100                │
│ Warnings        │ Yellow       │ AML alerts and flags            │
└─────────────────┴──────────────┴─────────────────────────────────┘
```

## UI Components Detail

### Cryptocurrency List Item
```
┌─────────────────────────────────────────┐
│ Symbol    Price         24h Change      │
│ ──────    ─────         ──────────      │
│                                         │
│ BTC       $45,000.00    +2.50% (Green)  │
└─────────────────────────────────────────┘
  100px     150px         100px
  width     width         width
```

### Wallet Card
```
┌──────────────────────────────────────────────┐
│ Wallet Analysis                              │
│ ──────────────────────────────────────────  │
│                                              │
│ 📍 Address: 0x742d...bEb0                   │
│ 💰 Total Balance: $45,500.00                │
│                                              │
│ 🚨 AML Risk: [Color-coded risk level]       │
│ 📊 Risk Score: [0-100]                      │
│                                              │
│ ⚠️  AML Alerts:                             │
│     • [Flag 1]                              │
│     • [Flag 2]                              │
│                                              │
│ 📈 Total Transactions: [count]              │
│ 🔍 Suspicious Transactions: [count]         │
└──────────────────────────────────────────────┘
```

## Responsive Behavior

### Desktop (1280x720 minimum)
- Full width layout
- All columns visible
- Comfortable spacing
- Large, readable fonts

### Web Browser (1920x1080 recommended)
- Optimal viewing experience
- Full feature display
- Professional dashboard appearance

## Real-time Updates

```
Time: 0s                          Time: 5s
┌──────────────────┐              ┌──────────────────┐
│ BTC  $45,000.00  │    Auto      │ BTC  $45,112.50  │
│      +2.50%      │   Update     │      +2.75%      │
└──────────────────┘      →       └──────────────────┘
                                   Prices refreshed!
```

## Loading State

```
┌────────────────────────────────────┐
│                                    │
│         🔄  Loading...             │
│                                    │
│   Loading CryptoTracker...         │
│                                    │
│   [Animated spinner]               │
│                                    │
└────────────────────────────────────┘
```

## Risk Level Indicators

### Visual Representation

```
Low Risk (0-29)
┌────────────────────────┐
│ AML Risk: Low Risk     │  ← Green text
│ Risk Score: 15         │
└────────────────────────┘

Medium Risk (30-69)
┌────────────────────────┐
│ AML Risk: Medium Risk  │  ← Yellow text
│ Risk Score: 45         │
└────────────────────────┘

High Risk (70-89)
┌────────────────────────┐
│ AML Risk: High Risk    │  ← Red text
│ Risk Score: 75         │
└────────────────────────┘

Critical Risk (90-100)
┌────────────────────────┐
│ AML Risk: Critical     │  ← Dark Red text
│ Risk Score: 95         │
└────────────────────────┘
```

## Interactive Elements (Future Enhancement)

Currently static display. Future additions could include:
- Clickable cryptocurrency rows for details
- Expandable wallet information
- Transaction history modal
- Filter and sort controls
- Chart views
- Export buttons

## Accessibility

- High contrast text on dark background
- Color-coded information has text labels
- Clear visual hierarchy
- Readable font sizes (18px base)
- Descriptive labels for all data

## Performance

- Updates every 5 seconds (configurable)
- Efficient rendering (only changed elements)
- Minimal resource usage
- Smooth animations (60 FPS target)

---

**Note**: This is an ASCII representation. The actual UI uses libGDX's Scene2D
for smooth rendering with proper fonts, colors, and layout management.
