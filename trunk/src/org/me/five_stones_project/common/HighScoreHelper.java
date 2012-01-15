package org.me.five_stones_project.common;

import java.util.Date;
import java.util.ArrayList;

import org.me.five_stones_project.activity.MainActivity;
import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.game.GameStatistics;
import org.me.five_stones_project.type.Descriptions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 * @author Tangl Andras
 */

public class HighScoreHelper {
	private static final String DEFAULT = "__";
	private static final String KEY_PADDING = "_highscore";

	public static ArrayList<HighScore> loadHighScores() {
		ArrayList<HighScore> highScores = new ArrayList<HighScore>();
		
		Context currentContext = MainActivity.getContext();
		SharedPreferences sp = currentContext.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE);
		
		String[] levels = Descriptions.getDescriptions(Descriptions.Level);
		
		for(String level : levels) 
			highScores.add(loadHighScore(sp, level));
		
		return highScores;
	}
	
	public static void updateHighScores(GameStatistics stat) {
		Context currentContext = MainActivity.getContext();
		SharedPreferences sp = currentContext.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE);
		
		String level = GameOptions.getInstance().getCurrentLevel().getDescription();
		HighScore highScore = loadHighScore(sp, level);
		
		if(stat.me) {			
			if(stat.elapsedTime < highScore.getTime()) {
				highScore.setTime(stat.elapsedTime);
				highScore.setDate(new Date());
			}
			highScore.increaseWins();
		}
		else
			highScore.increaseLoses();
		
		SharedPreferences.Editor ed = sp.edit();
		
		ed.putString(level + KEY_PADDING, highScore.toString());
		
		ed.commit();
	}
	
	private static HighScore loadHighScore(SharedPreferences sp, String level)  {
		String s = sp.getString(level + KEY_PADDING, DEFAULT);
		if(!s.equals(DEFAULT))
			return HighScore.parseHighScore(s);
		else
			return new HighScore(Descriptions.findByDescription(level));
	}
}
