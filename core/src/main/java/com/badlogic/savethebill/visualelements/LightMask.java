package com.badlogic.savethebill.visualelements;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.gdx.graphics.g2d.Animation;

public class LightMask extends BaseActor {
    private int radius;

    public LightMask(float x, float y, Stage s, int radius) {
        super(x, y, s);
        this.radius = radius;

        createMaskTexture();
    }

    private void createMaskTexture() {
        int size = radius * 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1.0f);
        pixmap.fillCircle(radius, radius, radius);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        setAnimation(new Animation<>(1, new TextureRegion(texture)));
        setSize(size, size);
        setOriginCenter();
    }

    public void updatePosition(float x, float y) {
        setPosition(x - radius, y - radius);
    }
}
