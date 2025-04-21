package com.badlogic.savethebill.characters;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;

public class NPC extends BaseActor {
    private String text;
    private boolean viewing;
    private String ID;

    public NPC(float x, float y, Stage s) {
        super(x, y, s);
        text = " ";
        viewing = false;
    }

    public void setText(String t) {
        text = t;
    }

    public String getText() {
        return text;
    }

    public void setViewing(boolean v) {
        viewing = v;
    }

    public boolean isViewing() {
        return viewing;
    }

    public void setID(String id) {
        ID = id;
        if (ID.equals("Gatekeeper"))
            loadTexture("npc-1.png");
        else if (ID.equals("Shopkeeper"))
            loadTexture("npc-2.png");
        else
            loadTexture("npc-3.png");
    }

    public String getID() {
        return ID;
    }
}
