package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.visualelements.DialogBox;
import com.badlogic.savethebill.visualelements.Scene;
import com.badlogic.savethebill.visualelements.SceneActions;
import com.badlogic.savethebill.visualelements.SceneSegment;

public class StoryScreen extends BaseScreen
{
    Scene scene;
    BaseActor continueKey;

    public void initialize()
    {
        BaseActor background = new BaseActor(0,0, mainStage);
        background.loadTexture( "valleyside.png" );
        background.setSize(mainStage.getViewport().getWorldWidth(), mainStage.getViewport().getWorldHeight());
        background.setOpacity(0);
        BaseActor.setWorldBounds(background);

        BaseActor excusedNPC = new BaseActor(0,0, mainStage);
        excusedNPC.loadTexture( "npc-big.png" );
        excusedNPC.setPosition( -excusedNPC.getWidth(), 0 );

        DialogBox dialogBox = new DialogBox(0,0, uiStage);
        dialogBox.setDialogSize(uiStage.getViewport().getWorldWidth() * 0.75f, uiStage.getViewport().getWorldHeight() * 0.25f);
        dialogBox.setBackgroundColor( new Color(0.6f, 0.6f, 0.8f, 1) );
        dialogBox.setFontScale(0.75f);
        dialogBox.setVisible(false);

        uiTable.add(dialogBox).expandX().expandY().bottom();

        continueKey = new BaseActor(0,0,uiStage);
        continueKey.loadTexture("key-C.png");
        continueKey.setSize(32,32);
        continueKey.setVisible(false);

        dialogBox.addActor(continueKey);
        continueKey.setPosition( dialogBox.getWidth() - continueKey.getWidth(), 0 );

        scene = new Scene();
        mainStage.addActor(scene);

        scene.addSegment( new SceneSegment( background, Actions.fadeIn(1) ));
        scene.addSegment( new SceneSegment(excusedNPC, SceneActions.moveToScreenCenter(2) ));
        scene.addSegment( new SceneSegment( dialogBox, Actions.show() ));

        scene.addSegment( new SceneSegment( dialogBox,
            SceneActions.setText("Can you help me find my friends?" ) ));

        scene.addSegment( new SceneSegment( continueKey, Actions.show() ));
        scene.addSegment( new SceneSegment( background, SceneActions.pause() ));
        scene.addSegment( new SceneSegment( continueKey, Actions.hide() ));

        scene.addSegment( new SceneSegment( dialogBox,
            SceneActions.setText("I've got to find them before it's getting late!" ) ));

        scene.addSegment( new SceneSegment( continueKey, Actions.show() ));
        scene.addSegment( new SceneSegment( background, SceneActions.pause() ));
        scene.addSegment( new SceneSegment( continueKey, Actions.hide() ));

        scene.addSegment( new SceneSegment( dialogBox, Actions.hide() ) );
        scene.addSegment( new SceneSegment(excusedNPC, SceneActions.moveToOutsideRight(1) ));
        scene.addSegment( new SceneSegment( background, Actions.fadeOut(1) ));

        scene.start();
    }

    public void update(float dt)
    {
        if ( scene.isSceneFinished() )
            BaseGame.setActiveScreen( new LevelScreen() );
    }

    public boolean keyDown(int keyCode)
    {
        if ( keyCode == Keys.C && continueKey.isVisible() )
            scene.loadNextSegment();

        return false;
    }
}
