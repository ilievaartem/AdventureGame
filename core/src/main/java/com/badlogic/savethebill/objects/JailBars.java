package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.characters.NPC;

public class JailBars extends BaseActor {
    private int hits;
    private NPC trappedNPC;
    private boolean hitThisSwing;

    public JailBars(float x, float y, Stage s, NPC npc) {
        super(x, y, s);
        loadTexture("jail-bars.png");
        setBoundaryRectangle();
        hits = 0;
        hitThisSwing = false;
        this.trappedNPC = npc;
    }

    public void hit() {
        if (!hitThisSwing) {
            hits++;
            hitThisSwing = true;
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
