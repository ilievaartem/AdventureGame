package com.cryptotracker.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.cryptotracker.CryptoTrackerApp;

/**
 * Desktop launcher for CryptoTracker
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }
    
    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new CryptoTrackerApp(), getDefaultConfiguration());
    }
    
    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("CryptoTracker - Real-time Cryptocurrency Monitoring with AML");
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setWindowedMode(1280, 720);
        // Note: Add CryptoTracker-specific icons to the assets folder
        // config.setWindowIcon("cryptotracker128.png", "cryptotracker64.png", "cryptotracker32.png", "cryptotracker16.png");
        return config;
    }
}
