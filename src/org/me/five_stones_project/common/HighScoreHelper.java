package org.me.five_stones_project.common;

import java.util.Date;
import java.util.ArrayList;

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

	public static ArrayList<HighScore> loadHighScores(Context ctx) {
		ArrayList<HighScore> highScores = new ArrayList<HighScore>();
		
		SharedPreferences sp = ctx.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE);
		
		String[] levels = Descriptions.getDescriptions(ctx, Descriptions.Level);
		
		for(String level : levels) 
			highScores.add(loadHighScore(ctx, sp, level));
		
		return highScores;
	}
	
	public static void updateHighScores(Context ctx, GameStatistics stat) {
		SharedPreferences sp = ctx.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE);
		
		String level = GameOptions.getInstance().getCurrentLevel().getDescription(ctx);
		HighScore highScore = loadHighScore(ctx, sp, level);
		
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
		
		ed.putString(level + KEY_PADDING, highScore.toString(ctx));
		
		ed.commit();
	}
	
	private static HighScore loadHighScore(Context ctx, SharedPreferences sp, String level)  {
		String s = sp.getString(level + KEY_PADDING, DEFAULT);
		if(!s.equals(DEFAULT))
			return HighScore.parseHighScore(ctx, s);
		else
			return new HighScore(Descriptions.findByDescription(ctx, level));
	}
}
