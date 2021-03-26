package com.mygdx.tap.Utility;

public class HighScore {

    public float score;

    private static HighScore singleton = new HighScore();

    private HighScore() {

    }

    public static HighScore returnInstance() {
        return singleton;
    }

    public float addScore(float points) {
        score += points;
        return score;
    }

    public Float getTotal() {
        return score;
    }

    public void setScore(float newScore) {
        score = newScore;
    }
}
