package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BillGame;

public class MenuScreen extends BaseScreen
{
    public void initialize()
    {
        BaseActor grass = new BaseActor(0,0, mainStage);
        grass.loadTexture( "grass.jpg" );
        grass.setSize(800,600);
        BaseActor title = new BaseActor(0,0, mainStage);
        title.loadTexture( "game-name.png" );
        title.centerAtPosition(400,300);
        title.moveBy(0,100);
        BaseActor start = new BaseActor(0,0, mainStage);
        start.loadTexture( "message-start.png" );
        start.centerAtPosition(400,300);
        start.moveBy(0,-100);
    }
    public void update(float dt)
    {
        if (Gdx.input.isKeyPressed(Keys.S))
            BillGame.setActiveScreen( new LevelScreen() );
    }
}
