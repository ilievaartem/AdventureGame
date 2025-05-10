package com.badlogic.savethebill.characters;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class NPCVillager extends NPC {
    public NPCVillager(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("npc-3.png");
        setBoundaryRectangle();
    }
}
