package com.badlogic.starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
public class Character extends ActorBeta{
    private final float characterSpeed = 5.0f;
    public Character () {
        super();
    }

    public void act(float dt)
    {
        super.act(dt);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float newX = getX();
        float newY = getY();

//        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
//            this.moveBy(-characterSpeed,0);
//        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
//            this.moveBy(characterSpeed,0);
//        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
//            this.moveBy(0,characterSpeed);
//        if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))
//            this.moveBy(0,-characterSpeed);

        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
            newX -= characterSpeed;
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
            newX += characterSpeed;
        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
            newY += characterSpeed;
        if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))
            newY -= characterSpeed;

        if (newX < 0) newX = 0; // Ліва межа
        if (newX + getWidth() > screenWidth) newX = screenWidth - getWidth();
        if (newY < 0) newY = 0; // Нижня межа// Верхня межа

        setPosition(newX, newY);
    }
}
