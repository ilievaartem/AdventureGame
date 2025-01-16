package com.badlogic.starfishcollector;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameBeta extends Game {
    protected Stage mainStage;

    public void create() {
        mainStage = new Stage();
        initialize();
    }

    public void initialize() {
    }

    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        // act method
        mainStage.act(dt);
        // defined by user
        update(dt);
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // draw the graphics
        mainStage.draw();
    }

    public void update(float dt) {
    }
}
