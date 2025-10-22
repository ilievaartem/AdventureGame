package com.badlogic.savethebill.visualelements;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.characters.Hero;

public class MiniMap {
    private Table miniMapTable;
    private Image miniMapBackground;
    private BaseActor playerDot;
    private static final int MINIMAP_SIZE = 120;
    private static final float SCALE_FACTOR = 0.1f;

    private Hero hero;
    private Stage mainStage;

    public MiniMap(Stage uiStage, Stage mainStage, Hero hero) {
        this.mainStage = mainStage;
        this.hero = hero;

        miniMapTable = new Table();
        miniMapTable.setFillParent(true);
        miniMapTable.top().left();
        miniMapTable.pad(10);

        createMiniMapBackground();

        createPlayerDot(uiStage);

        miniMapTable.add(miniMapBackground).size(MINIMAP_SIZE, MINIMAP_SIZE);

        uiStage.addActor(miniMapTable);
    }

    private void createMiniMapBackground() {
        Pixmap pixmap = new Pixmap(MINIMAP_SIZE, MINIMAP_SIZE, Pixmap.Format.RGBA8888);

        pixmap.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        pixmap.fill();

        pixmap.setColor(0.5f, 0.5f, 0.5f, 1.0f);
        pixmap.drawRectangle(0, 0, MINIMAP_SIZE, MINIMAP_SIZE);
        pixmap.drawRectangle(1, 1, MINIMAP_SIZE - 2, MINIMAP_SIZE - 2);

        Texture backgroundTexture = new Texture(pixmap);
        miniMapBackground = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));

        pixmap.dispose();
    }

    private void createPlayerDot(Stage uiStage) {
        playerDot = new BaseActor(0, 0, uiStage);

        Pixmap playerPixmap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        playerPixmap.setColor(1.0f, 0.2f, 0.2f, 1.0f);
        playerPixmap.fill();

        Texture playerTexture = new Texture(playerPixmap);
        TextureRegion playerRegion = new TextureRegion(playerTexture);

        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(playerRegion);
        Animation<TextureRegion> playerAnimation = new Animation<TextureRegion>(1.0f, frames);
        playerAnimation.setPlayMode(Animation.PlayMode.LOOP);
        playerDot.setAnimation(playerAnimation);
        playerDot.setSize(4, 4);

        playerPixmap.dispose();
    }

    public void update() {
        if (hero == null) return;

        float heroX = hero.getX();
        float heroY = hero.getY();

        float miniMapX = (heroX * SCALE_FACTOR) + (MINIMAP_SIZE / 2);
        float miniMapY = (heroY * SCALE_FACTOR) + (MINIMAP_SIZE / 2);

        miniMapX = Math.max(2, Math.min(MINIMAP_SIZE - 6, miniMapX));
        miniMapY = Math.max(2, Math.min(MINIMAP_SIZE - 6, miniMapY));

        if (miniMapTable != null) {
            float tableX = miniMapTable.getX() + 10;
            float tableY = miniMapTable.getY() + miniMapTable.getHeight() - MINIMAP_SIZE - 10;

            playerDot.setPosition(tableX + miniMapX, tableY + miniMapY);
        }
    }

    public void dispose() {
        if (miniMapBackground != null && miniMapBackground.getDrawable() instanceof TextureRegionDrawable) {
            TextureRegionDrawable drawable = (TextureRegionDrawable) miniMapBackground.getDrawable();
            if (drawable.getRegion().getTexture() != null) {
                drawable.getRegion().getTexture().dispose();
            }
        }
    }
}
