package org.me.five_stones_project.game;

import org.me.five_stones_project.activity.MainActivity;
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
	private final String STYLE_KEY = "game_style";
	private final String LEVEL_KEY = "current_level";
	private final String SENSITIVITY_KEY = "sensitivity";
	
	private final int DEFAULT_SENSITIVITY = 2;
	private final int DEFAULT_LEVEL = Descriptions.Normal.ordinal();
	private final int DEFAULT_STYLE = Descriptions.Classic.ordinal();
	
	private static GameOptions instance;
	
	public static synchronized GameOptions getInstance() {
		if(instance == null)
			instance = new GameOptions();
		return instance;
	}
	
	private int sensitivity;
	private Descriptions currentStyle;
	private Descriptions currentLevel;
	
	private GameOptions() { 
		load();
	}
	
	private void load() {
		Context currentContext = MainActivity.getContext();
		SharedPreferences sp = currentContext.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE);
		
		sensitivity = sp.getInt(SENSITIVITY_KEY, DEFAULT_SENSITIVITY);
		currentStyle = Descriptions.values()[sp.getInt(STYLE_KEY, DEFAULT_STYLE)];
		currentLevel = Descriptions.values()[sp.getInt(LEVEL_KEY, DEFAULT_LEVEL)];
	}
	
	public void commit() {
		Context currentContext = MainActivity.getContext();
		SharedPreferences.Editor editor = currentContext.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE).edit();
		
		editor.putInt(SENSITIVITY_KEY, sensitivity);
		editor.putInt(STYLE_KEY, currentStyle.ordinal());
		editor.putInt(LEVEL_KEY, currentLevel.ordinal());
		
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
}
