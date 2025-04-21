package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;
import com.badlogic.savethebill.characters.MainCharacter;
import com.badlogic.savethebill.characters.Orc;
import com.badlogic.savethebill.characters.Zoro;
import com.badlogic.savethebill.objects.Rock;
import com.badlogic.savethebill.objects.Sign;
import com.badlogic.savethebill.visualelements.DialogBox;
import com.badlogic.savethebill.visualelements.TilemapActor;
import com.badlogic.savethebill.visualelements.Whirlpool;

public class LevelScreen3 extends BaseScreen {
    private MainCharacter mainCharacter;
    private boolean win;
    private boolean gameOver;
    private BaseActor continueMessage;
    private Label npcLabel;
    private DialogBox dialogBox;
    private float audioVolume;
    private Sound pickUp;
    private Music instrumental;
    private Music windSurf;
    private boolean isMuted = false;
    private static final float INSTRUMENTAL_VOLUME = 0.1f;
    private static final float WIND_VOLUME = 0.1f;
    private static final float DROP_VOLUME = 0.1f;

    public void initialize() {
        TilemapActor tma = new TilemapActor("map.tmx", mainStage);

        for (MapObject obj : tma.getTileList("Zoro")) {
            MapProperties props = obj.getProperties();
            new Zoro((float) props.get("x"), (float) props.get("y"), mainStage);
        }
        for (MapObject obj : tma.getTileList("Orc")) {
            MapProperties props = obj.getProperties();
            new Orc((float) props.get("x"), (float) props.get("y"), mainStage);
        }
        for (MapObject obj : tma.getTileList("Rock")) {
            MapProperties props = obj.getProperties();
            new Rock((float) props.get("x"), (float) props.get("y"), mainStage);
        }
        for (MapObject obj : tma.getTileList("Sign")) {
            MapProperties props = obj.getProperties();
            Sign s = new Sign((float) props.get("x"), (float) props.get("y"), mainStage);
            s.setText((String) props.get("message"));
        }

        MapObject startPoint = tma.getRectangleList("Start").get(0);
        MapProperties props = startPoint.getProperties();

        mainCharacter = new MainCharacter((float) props.get("x"), (float) props.get("y"), mainStage);

        win = false;
        gameOver = false;
        continueMessage = null;

        npcLabel = new Label("NPC Left:", BaseGame.labelStyle);
        npcLabel.setColor(Color.CYAN);

        ButtonStyle buttonStyle = new ButtonStyle();

        Texture buttonTex = new Texture(Gdx.files.internal("undo.png"));
        TextureRegion buttonRegion = new TextureRegion(buttonTex);
        buttonStyle.up = new TextureRegionDrawable(buttonRegion);

        Button restartButton = new Button(buttonStyle);
        restartButton.setColor(Color.CYAN);

        restartButton.addListener(
            (Event e) ->
            {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent) e).getType().equals(Type.touchDown))
                    return false;

                instrumental.dispose();
                windSurf.dispose();

                BillGame.setActiveScreen(new LevelScreen3());
                return false;
            }
        );

        ButtonStyle buttonStyle2 = new ButtonStyle();

        Texture buttonTex2 = new Texture(Gdx.files.internal("audio.png"));
        Texture buttonTex2Muted = new Texture(Gdx.files.internal("no-audio.png"));

        TextureRegion buttonRegion2 = new TextureRegion(buttonTex2);
        TextureRegion buttonRegion2Muted = new TextureRegion(buttonTex2Muted);

        buttonStyle2.up = new TextureRegionDrawable(buttonRegion2);

        Button muteButton = new Button(buttonStyle2);
        muteButton.setColor(Color.CYAN);

        muteButton.addListener(
            (Event e) ->
            {
                if (!isTouchDownEvent(e))
                    return false;

                isMuted = !isMuted;
                instrumental.setVolume(isMuted ? 0 : INSTRUMENTAL_VOLUME);
                windSurf.setVolume(isMuted ? 0 : WIND_VOLUME);

                muteButton.getStyle().up = isMuted
                    ? new TextureRegionDrawable(buttonRegion2Muted)
                    : new TextureRegionDrawable(buttonRegion2);

                return true;
            }
        );

        uiTable.pad(10);
        uiTable.add(npcLabel).top();
        uiTable.add().expandX().expandY();
        uiTable.add(muteButton).top();
        uiTable.add(restartButton).top();

        dialogBox = new DialogBox(0, 0, uiStage);
        dialogBox.setBackgroundColor(Color.TAN);
        dialogBox.setFontColor(Color.BROWN);
        dialogBox.setDialogSize(600, 100);
        dialogBox.setFontScale(0.80f);
        dialogBox.alignCenter();
        dialogBox.setVisible(false);

        uiTable.row();
        uiTable.add(dialogBox).colspan(4);

        //TODO знайти інший звук для знаходження NPC
        pickUp = Gdx.audio.newSound(Gdx.files.internal("Power_Drain.ogg"));
        instrumental = Gdx.audio.newMusic(Gdx.files.internal("Master_of_the_Feast.ogg"));
        windSurf = Gdx.audio.newMusic(Gdx.files.internal("Birds_Wind.ogg"));

        audioVolume = 1.00f;
        instrumental.setLooping(true);
        instrumental.setVolume(INSTRUMENTAL_VOLUME);
        instrumental.play();
        windSurf.setLooping(true);
        windSurf.setVolume(WIND_VOLUME);
        windSurf.play();
    }

    public void update(float dt) {
        for (BaseActor rockActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Rock"))
            mainCharacter.preventOverlap(rockActor);
        for (BaseActor zoroActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Zoro")) {
            Zoro zoro = (Zoro) zoroActor;
            if (mainCharacter.overlaps(zoro) && !zoro.collected) {
                zoro.collected = true;
                pickUp.play(DROP_VOLUME * audioVolume);
                zoro.clearActions();
                zoro.addAction(Actions.fadeOut(1));
                zoro.addAction(Actions.after(Actions.removeActor()));

                Whirlpool whirl = new Whirlpool(0, 0, mainStage);
                whirl.centerAtActor(zoro);
                whirl.setOpacity(0.25f);
            }
        }

        npcLabel.setText("Zoro's Left: " + BaseActor.count(mainStage, "com.badlogic.savethebill.characters.Zoro"));

        for (BaseActor signActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Sign")) {
            Sign sign = (Sign) signActor;
            mainCharacter.preventOverlap(sign);
            boolean nearby = mainCharacter.isWithinDistance(4, sign);
            if (nearby && !sign.isViewing()) {
                dialogBox.setText(sign.getText());
                dialogBox.setVisible(true);
                sign.setViewing(true);
            }
            if (sign.isViewing() && !nearby) {
                dialogBox.setText(" ");
                dialogBox.setVisible(false);
                sign.setViewing(false);
            }
        }
        for (BaseActor orcActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Orc")) {
            Orc orc = (Orc) orcActor;
            if (mainCharacter.overlaps(orc) && !gameOver && !win) {
                gameOver = true;
                mainCharacter.remove();

                BaseActor gameOverMessage = new BaseActor(0, 0, mainStage);
                gameOverMessage.loadTexture("game-over.png");
                gameOverMessage.centerAtPosition(400, 300);
                gameOverMessage.setOpacity(0);
                gameOverMessage.addAction(Actions.fadeIn(1));
            }
        }
        if (BaseActor.count(mainStage, "com.badlogic.savethebill.characters.Zoro") == 0 && !win) {
            win = true;
            BaseActor youWinMessage = new BaseActor(0, 0, mainStage);
            youWinMessage.loadTexture("you-win.png");
            youWinMessage.centerAtPosition(mainViewport.getWorldWidth() / 2, mainViewport.getWorldHeight() / 2);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction(Actions.delay(1));
            youWinMessage.addAction(Actions.after(Actions.fadeIn(1)));

            continueMessage = new BaseActor(0, 0, mainStage);
            continueMessage.loadTexture("message-continue.png");
            continueMessage.centerAtPosition(mainViewport.getWorldWidth() / 2, mainViewport.getWorldHeight() / 2 - 100);
            continueMessage.addAction(Actions.delay(2));
            continueMessage.addAction(Actions.after(Actions.fadeIn(1)));
        }
        if (win && Gdx.input.isKeyPressed(Input.Keys.C)) {
            instrumental.stop();
            windSurf.stop();
            BaseGame.setActiveScreen(new LevelScreen2());
        }
    }
}
