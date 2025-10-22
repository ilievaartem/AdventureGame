package com.badlogic.savethebill.visualelements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;

public class InventoryHUD {
    private Label healthLabel;
    private Label coinLabel;
    private Label arrowLabel;
    private Table uiTable;

    public InventoryHUD(Stage uiStage, int initialHealth, int initialCoins, int initialArrows) {
        uiTable = new Table();
        uiTable.setFillParent(true);

        healthLabel = new Label(" x " + initialHealth, BaseGame.labelStyle);
        healthLabel.setColor(Color.PINK);
        healthLabel.setFontScale(0.6f);

        coinLabel = new Label(" x " + initialCoins, BaseGame.labelStyle);
        coinLabel.setColor(Color.GOLD);
        coinLabel.setFontScale(0.6f);

        arrowLabel = new Label(" x " + initialArrows, BaseGame.labelStyle);
        arrowLabel.setColor(Color.TAN);
        arrowLabel.setFontScale(0.6f);

        BaseActor healthIcon = new BaseActor(0, 0, uiStage);
        healthIcon.loadTexture("heart-icon.png");
        healthIcon.setSize(20, 20);

        BaseActor coinIcon = new BaseActor(0, 0, uiStage);
        coinIcon.loadTexture("coin-icon1.png");
        coinIcon.setSize(20, 20);

        BaseActor arrowIcon = new BaseActor(0, 0, uiStage);
        arrowIcon.loadTexture("arrow-icon.png");
        arrowIcon.setSize(20, 20);

        uiTable.top().left();
        uiTable.pad(10);

        uiTable.add(healthIcon).size(20, 20).padLeft(10);
        uiTable.add(healthLabel).padLeft(5);
        uiTable.row();
        uiTable.add(coinIcon).size(20, 20).padTop(5).padLeft(10);
        uiTable.add(coinLabel).padLeft(5).padTop(5);
        uiTable.row();
        uiTable.add(arrowIcon).size(20, 20).padTop(5).padLeft(10);
        uiTable.add(arrowLabel).padLeft(5).padTop(5);

        uiStage.addActor(uiTable);
    }

    public void update(int health, int coins, int arrows) {
        healthLabel.setText(" x " + health);
        coinLabel.setText(" x " + coins);
        arrowLabel.setText(" x " + arrows);
    }
}
