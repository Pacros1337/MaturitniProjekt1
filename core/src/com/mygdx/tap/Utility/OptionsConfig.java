package com.mygdx.tap.Utility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.mygdx.tap.Tap;

public class OptionsConfig {
    private static final String OptionNames = "tap";
    private static final String OptionMusicVolume = "volume";
    private static final String OptionSoundOn = "sound.enabled";
    private static final String OptionMusicOn = "music.enabled";
    private static final String PROPERTY_highscore = "highscore";
    private static final String PROPERTY_timeplayed = "time";
    private Tap tap_app;

    public Preferences prefs;

    HighScore score = HighScore.returnInstance();
    TimePlayed time = TimePlayed.returnInstance();

    public OptionsConfig(Tap tap) {
        tap_app = tap;
    }

    protected Preferences getOptions() {
        if (prefs == null)
            prefs = Gdx.app.getPreferences(OptionNames);
        return prefs;
    }

    public int loadHighScore() {
        float scores = prefs.getFloat(PROPERTY_highscore, 0);
        score.setScore(scores);
        return (int) scores;
    }

    public void saveHighScore() {
        prefs.putFloat(PROPERTY_highscore, score.getTotal());
        prefs.flush();
    }

    public void saveTime() {
        prefs.putFloat(PROPERTY_timeplayed, time.getTime());
        prefs.flush();
    }

    public float loadTime() {
        float times = prefs.getFloat(PROPERTY_timeplayed, 0);
        time.setTime(times);
        return times;
    }

    public boolean musicOn() {
        return getOptions().getBoolean(OptionMusicOn, true);
    }

    public void setMusicOn(boolean musicEnabled) {
        getOptions().putBoolean(OptionMusicOn, musicEnabled);
        getOptions().flush();
    }

    public boolean SoundOn() {
        return getOptions().getBoolean(OptionSoundOn, true);
    }

    public void setSoundOn(boolean soundEffectsEnabled) {
        getOptions().putBoolean(OptionSoundOn, soundEffectsEnabled);
        getOptions().flush();
    }

    public void setMusicVolume(float volume) {
        getOptions().putFloat(OptionMusicVolume, volume);
        getOptions().flush();
    }

    public float getMusicVolume() {
        return getOptions().getFloat(OptionMusicVolume, 0.5f);
    }

}
