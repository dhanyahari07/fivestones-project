package org.me.five_stones_project.game;

import org.me.five_stones_project.type.Descriptions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Tangl Andras
 * GameOptions is a singleton class stored a values
 * of the current game, game properties etc.
 */
public class GameOptions {
	private final String QUALITY_KEY = "quality";
	private final String STYLE_KEY = "game_style";
	private final String VIBRATION_KEY = "vibration";
	private final String ANIMATION_KEY = "animation";
	private final String LEVEL_KEY = "current_level";
	private final String FULL_SCREEN_KEY = "fullscreen";
	private final String SENSITIVITY_KEY = "sensitivity";
	
	private final int DEFAULT_SENSITIVITY = 2;
	private final int DEFAULT_QUALITY = Descriptions.High.ordinal();
	private final int DEFAULT_LEVEL = Descriptions.Normal.ordinal();
	private final int DEFAULT_STYLE = Descriptions.Classic.ordinal();
	
	private static GameOptions instance;
	
	public static synchronized GameOptions getInstance() {
		return instance;
	}
	
	public static synchronized void initialize(Context ctx) {
		if(instance == null)
			instance = new GameOptions(ctx);
	}
	
	private int sensitivity;
	private Descriptions currentStyle;
	private Descriptions currentLevel;
	private Descriptions currentQuality;
	private boolean vibration, fullScreen, animation;
	
	private GameOptions(Context ctx) { 
		load(ctx);
	}
	
	private void load(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE);

		animation = sp.getBoolean(ANIMATION_KEY, true);
		vibration = sp.getBoolean(VIBRATION_KEY, false);
		fullScreen = sp.getBoolean(FULL_SCREEN_KEY, true);
		sensitivity = sp.getInt(SENSITIVITY_KEY, DEFAULT_SENSITIVITY);
		currentStyle = Descriptions.values()[sp.getInt(STYLE_KEY, DEFAULT_STYLE)];
		currentLevel = Descriptions.values()[sp.getInt(LEVEL_KEY, DEFAULT_LEVEL)];
		currentQuality = Descriptions.values()[sp.getInt(QUALITY_KEY, DEFAULT_QUALITY)];		
	}
	
	public void commit(Context ctx) {
		SharedPreferences.Editor editor = ctx.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE).edit();
		
		editor.putBoolean(ANIMATION_KEY, animation);
		editor.putBoolean(VIBRATION_KEY, vibration);		
		editor.putInt(SENSITIVITY_KEY, sensitivity);
		editor.putBoolean(FULL_SCREEN_KEY, fullScreen);
		editor.putInt(STYLE_KEY, currentStyle.ordinal());
		editor.putInt(LEVEL_KEY, currentLevel.ordinal());
		editor.putInt(QUALITY_KEY, currentQuality.ordinal());
		
		editor.commit();
	}

	//getters and setters
	public Descriptions getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(Descriptions selectedLevel) {
		this.currentLevel = selectedLevel;
	}

	public int getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(int sensitivity) {
		this.sensitivity = sensitivity;
	}

	public Descriptions getCurrentStyle() {
		return currentStyle;
	}

	public void setCurrentStyle(Descriptions selectedLanguages) {
		this.currentStyle = selectedLanguages;
	}

	public Descriptions getCurrentQuality() {
		return currentQuality;
	}

	public void setCurrentQuality(Descriptions quality) {
		this.currentQuality = quality;
	}

	public boolean isVibration() {
		return vibration;
	}

	public void setVibration(boolean vibration) {
		this.vibration = vibration;
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	public boolean isAnimation() {
		return animation;
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}
}
