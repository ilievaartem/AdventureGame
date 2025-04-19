package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class BaseScreen implements Screen, InputProcessor {
    protected Stage mainStage;
    protected Stage uiStage;
    protected Table uiTable;
    protected FitViewport mainViewport;
    protected FitViewport uiViewport;

    protected static final int WORLD_WIDTH = 800;
    protected static final int WORLD_HEIGHT = 600;

    public BaseScreen() {
        mainViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);

        mainStage = new Stage(mainViewport);
        uiStage = new Stage(uiViewport);

        uiTable = new Table();
        uiTable.setFillParent(true);
        uiStage.addActor(uiTable);

        initialize();
    }

    public abstract void initialize();

    public abstract void update(float dt);

    public void render(float dt) {
        uiStage.act(dt);
        mainStage.act(dt);
        update(dt);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mainStage.draw();
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        mainViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void dispose() {
    }

    public void show() {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.addProcessor(this);
        im.addProcessor(uiStage);
        im.addProcessor(mainStage);
    }

    public void hide() {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.removeProcessor(this);
        im.removeProcessor(uiStage);
        im.removeProcessor(mainStage);
    }

    public boolean keyDown(int keycode) {
        if (keycode == com.badlogic.gdx.Input.Keys.F) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(800, 600);
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
            return true;
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char c) {
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean isTouchDownEvent(Event e) {
        return (e instanceof InputEvent) && ((InputEvent) e).getType().equals(Type.touchDown);
    }
}
