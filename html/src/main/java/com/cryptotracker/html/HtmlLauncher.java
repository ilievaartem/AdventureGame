package com.cryptotracker.html;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.cryptotracker.CryptoTrackerApp;

/**
 * HTML launcher for CryptoTracker web application
 */
public class HtmlLauncher extends GwtApplication {
    
    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(1920, 1080);
        config.antialiasing = true;
        config.preferFlash = false;
        return config;
    }
    
    @Override
    public ApplicationListener createApplicationListener() {
        return new CryptoTrackerApp();
    }
}
