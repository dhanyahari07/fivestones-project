package org.me.five_stones_project.activity;

import java.util.Locale;

import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.type.Languages;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.Window;
import android.view.WindowManager;

/**
 *
 * @author Tangl Andras
 */

public class BaseActivity extends Activity {
	private static final String LANGUAGE_KEY = "language";
	private static final int DEFAULT_LANGUAGE = Languages.Undefined.ordinal();
	
	protected static int lastAction;
	protected Languages currentLanguage;
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		changeLanguage();
		
		if(GameOptions.getInstance().isFullScreen()) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		changeLanguage();
	}
	
	private void changeLanguage() {
		loadLanguage();

		if(currentLanguage.ordinal() != DEFAULT_LANGUAGE) {
			Locale locale = new Locale(currentLanguage.getLocale()); 
	        Locale.setDefault(locale);
	        Configuration config = new Configuration();
	        config.locale = locale;
	        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
		}
		else {
			String lang = getResources().getConfiguration().locale.getLanguage();
			currentLanguage = Languages.findByLocale(lang);
		}
	}
	
	private void loadLanguage() {
		SharedPreferences sp = getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE);

		currentLanguage = Languages.values()[sp.getInt(LANGUAGE_KEY, DEFAULT_LANGUAGE)];
	}
	
	protected void updateLanguage(Languages language) {
		SharedPreferences.Editor editor = getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE).edit();
		
		editor.putInt(LANGUAGE_KEY, language.ordinal());
		
		editor.commit();
	}
}
