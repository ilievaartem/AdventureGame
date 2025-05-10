package com.badlogic.savethebill.characters;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class NPCHoe extends NPC {
    public NPCHoe(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("npc-4.png");
        setBoundaryRectangle();
    }
}
