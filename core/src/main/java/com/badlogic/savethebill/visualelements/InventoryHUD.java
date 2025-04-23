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
        coinLabel = new Label(" x " + initialCoins, BaseGame.labelStyle);
        coinLabel.setColor(Color.GOLD);
        arrowLabel = new Label(" x " + initialArrows, BaseGame.labelStyle);
        arrowLabel.setColor(Color.TAN);

        BaseActor healthIcon = new BaseActor(0, 0, uiStage);
        healthIcon.loadTexture("heart-icon.png");
        BaseActor coinIcon = new BaseActor(0, 0, uiStage);
        coinIcon.loadTexture("coin-icon1.png");
        BaseActor arrowIcon = new BaseActor(0, 0, uiStage);
        arrowIcon.loadTexture("arrow-icon.png");

        uiTable.pad(10);
        uiTable.add().expandY();
        uiTable.row();
        uiTable.add(healthIcon);
        uiTable.add(healthLabel);
        uiTable.add().expandX();
        uiTable.add(coinIcon);
        uiTable.add(coinLabel);
        uiTable.add().expandX();
        uiTable.add(arrowIcon);
        uiTable.add(arrowLabel);

        uiStage.addActor(uiTable);
    }

    public void update(int health, int coins, int arrows) {
        healthLabel.setText(" x " + health);
        coinLabel.setText(" x " + coins);
        arrowLabel.setText(" x " + arrows);
    }
}
