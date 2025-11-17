package com.badlogic.savethebill.inventory;

public class FoodConsumeEvent {
    private int healAmount;
    private String foodName;
    private float x, y;

    public FoodConsumeEvent(int healAmount, String foodName, float x, float y) {
        this.healAmount = healAmount;
        this.foodName = foodName;
        this.x = x;
        this.y = y;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public String getFoodName() {
        return foodName;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
