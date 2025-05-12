package com.badlogic.savethebill.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.characters.NPC;
import com.badlogic.savethebill.screens.LevelScreen3;

public class JailBars extends BaseActor {
    private int hits;
    private NPC trappedNPC;
    private boolean hitThisSwing;
    private LevelScreen3 screen;

    public JailBars(float x, float y, Stage s, NPC npc, LevelScreen3 screen) {
        super(x, y, s);
        loadTexture("jail-bars.png");
        setBoundaryRectangle();
        hits = 0;
        hitThisSwing = false;
        this.trappedNPC = npc;
        this.screen = screen;
    }

    public void hit() {
        if (!hitThisSwing) {
            hits++;
            hitThisSwing = true;

            if (screen != null && !screen.getControlHUD().isMuted()) {
                if (hits < 3) {
                    Sound hitSound = screen.getHitPrisonSound();
                    if (hitSound != null) {
                        hitSound.play(screen.getControlHUD().getEffectVolume());
                    }
                } else if (hits == 3) {
                    Sound destroySound = screen.getDestroyPrisonSound();
                    if (destroySound != null) {
                        destroySound.play(screen.getControlHUD().getEffectVolume());
                    }
                }
            }

            if (hits >= 3) {
                destroy();
            }
        }
    }

    public void resetHitThisSwing() {
        hitThisSwing = false;
    }

    private void destroy() {
        clearActions();
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveBy(0, -50, 0.5f),
                Actions.fadeOut(0.5f)
            ),
            Actions.removeActor()
        ));
    }

    public NPC getTrappedNPC() {
        return trappedNPC;
    }
}
