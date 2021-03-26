package com.mygdx.tap.Utility;

public class TimePlayed {

    float currentTime = 0;

    private static TimePlayed singleton = new TimePlayed();

    private TimePlayed() {
    }

    public static TimePlayed returnInstance() {
        return singleton;
    }

    public void update(float delta) {
        currentTime += delta;
    }

    public float getTime() {
        return currentTime;
    }

    public void reset() {
        currentTime = 0;
    }

    public void setTime(float newTime) {
        currentTime = newTime;
    }
}
