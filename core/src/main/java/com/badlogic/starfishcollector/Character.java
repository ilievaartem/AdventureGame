package com.badlogic.starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Character extends ActorBeta {
    private final float normalSpeed = 2.5f;
    private final float sprintSpeed = 5.0f;

    public Character() {
        super();
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float newX = getX();
        float newY = getY();

        float currentSpeed = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) ? sprintSpeed : normalSpeed;

        // Рух персонажа
        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
            newX -= currentSpeed;
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
            newX += currentSpeed;
        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
            newY += currentSpeed;
        if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))
            newY -= currentSpeed;

        // Обмеження руху в межах екрану
        if (newX < 0) newX = 0;
        if (newX + getWidth() > screenWidth) newX = screenWidth - getWidth();
        if (newY < 0) newY = 0;
        if (newY + getHeight() > screenHeight) newY = screenHeight - getHeight();

        // Оновлення позиції персонажа
        setPosition(newX, newY);
    }
}
