package com.cryptotracker.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.cryptotracker.CryptoTrackerApp;
import com.cryptotracker.aml.AMLAnalysisResult;
import com.cryptotracker.aml.AMLAnalyzer;
import com.cryptotracker.models.Cryptocurrency;
import com.cryptotracker.models.Wallet;
import com.cryptotracker.services.CryptoDataService;
import com.cryptotracker.services.WalletService;

import java.util.List;

/**
 * Main screen for CryptoTracker application
 */
public class CryptoTrackerScreen implements Screen {
    private CryptoTrackerApp app;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private CryptoDataService cryptoDataService;
    private WalletService walletService;
    private AMLAnalyzer amlAnalyzer;
    private Table mainTable;
    private BitmapFont font;
    
    private float updateTimer = 0;
    private static final float UPDATE_INTERVAL = 5.0f; // Update every 5 seconds
    
    public CryptoTrackerScreen(CryptoTrackerApp app) {
        this.app = app;
        this.stage = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();
        this.cryptoDataService = new CryptoDataService();
        this.walletService = new WalletService();
        this.amlAnalyzer = new AMLAnalyzer();
        this.font = app.font;
        
        Gdx.input.setInputProcessor(stage);
        
        createUI();
    }
    
    private void createUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().left();
        mainTable.pad(20);
        
        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.WHITE);
        Label titleLabel = new Label("CryptoTracker - Real-time Crypto Monitoring with AML", titleStyle);
        mainTable.add(titleLabel).colspan(3).padBottom(30).row();
        
        // Cryptocurrency list header
        Label.LabelStyle headerStyle = new Label.LabelStyle(font, new Color(0.7f, 0.7f, 1f, 1f));
        mainTable.add(new Label("Top Cryptocurrencies", headerStyle)).colspan(3).padBottom(15).row();
        
        // Display cryptocurrencies
        displayCryptocurrencies();
        
        // Wallet section
        mainTable.row();
        mainTable.add(new Label("", headerStyle)).padTop(30).row();
        mainTable.add(new Label("Wallet Analysis", headerStyle)).colspan(3).padTop(30).padBottom(15).row();
        
        // Display wallet info and AML analysis
        displayWalletAnalysis();
        
        stage.addActor(mainTable);
    }
    
    private void displayCryptocurrencies() {
        List<Cryptocurrency> cryptos = cryptoDataService.getTopCryptocurrencies(8);
        
        Label.LabelStyle normalStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle greenStyle = new Label.LabelStyle(font, Color.GREEN);
        Label.LabelStyle redStyle = new Label.LabelStyle(font, Color.RED);
        
        for (Cryptocurrency crypto : cryptos) {
            Label symbolLabel = new Label(crypto.getSymbol(), normalStyle);
            Label priceLabel = new Label(crypto.getFormattedPrice(), normalStyle);
            
            Label.LabelStyle changeStyle = crypto.isPriceIncreasing() ? greenStyle : redStyle;
            Label changeLabel = new Label(crypto.getFormattedChange(), changeStyle);
            
            mainTable.add(symbolLabel).width(100).padRight(20);
            mainTable.add(priceLabel).width(150).padRight(20);
            mainTable.add(changeLabel).width(100);
            mainTable.row();
        }
    }
    
    private void displayWalletAnalysis() {
        List<Wallet> wallets = walletService.getAllWallets();
        
        if (wallets.isEmpty()) {
            return;
        }
        
        Wallet wallet = wallets.get(0);
        
        Label.LabelStyle normalStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle warningStyle = new Label.LabelStyle(font, Color.YELLOW);
        Label.LabelStyle dangerStyle = new Label.LabelStyle(font, Color.RED);
        
        // Display wallet address
        mainTable.add(new Label("Address: " + wallet.getShortAddress(), normalStyle)).colspan(3).padBottom(10).row();
        
        // Display total balance
        mainTable.add(new Label("Total Balance: $" + String.format("%.2f", wallet.getTotalBalanceUSD()), normalStyle))
            .colspan(3).padBottom(10).row();
        
        // Perform AML analysis
        AMLAnalysisResult amlResult = amlAnalyzer.analyzeWallet(wallet.getAddress(), wallet.getTransactions());
        
        // Display AML risk level
        Label.LabelStyle riskStyle;
        switch (amlResult.getRiskLevel()) {
            case LOW:
                riskStyle = new Label.LabelStyle(font, Color.GREEN);
                break;
            case MEDIUM:
                riskStyle = warningStyle;
                break;
            case HIGH:
            case CRITICAL:
                riskStyle = dangerStyle;
                break;
            default:
                riskStyle = normalStyle;
        }
        
        mainTable.add(new Label("AML Risk: " + amlResult.getRiskLevel().getDisplayName(), riskStyle))
            .colspan(3).padBottom(10).row();
        mainTable.add(new Label("Risk Score: " + amlResult.getRiskScore(), riskStyle))
            .colspan(3).padBottom(10).row();
        
        // Display AML flags
        if (!amlResult.getFlags().isEmpty()) {
            mainTable.add(new Label("AML Alerts:", warningStyle)).colspan(3).padTop(10).padBottom(5).row();
            for (String flag : amlResult.getFlags()) {
                mainTable.add(new Label("- " + flag, warningStyle)).colspan(3).padLeft(20).row();
            }
        }
        
        // Display transaction count
        mainTable.add(new Label("Total Transactions: " + wallet.getTransactions().size(), normalStyle))
            .colspan(3).padTop(10).row();
        mainTable.add(new Label("Suspicious Transactions: " + amlResult.getSuspiciousTransactionCount(), warningStyle))
            .colspan(3).row();
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update prices periodically
        updateTimer += delta;
        if (updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0;
            cryptoDataService.updatePrices();
            // Only refresh UI when prices are actually updated
            refreshUI();
        }
        
        // Render stage
        stage.act(delta);
        stage.draw();
    }
    
    private void refreshUI() {
        // More efficient: only recreate UI when data changes
        // In a production app, consider updating only the labels instead of full recreation
        mainTable.clear();
        createUI();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void show() {
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
