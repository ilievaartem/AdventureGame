package com.badlogic.savethebill.characters;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class NPCDog extends NPC {
    public NPCDog(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("npc-dog.png");
        setBoundaryRectangle();
    }
}
