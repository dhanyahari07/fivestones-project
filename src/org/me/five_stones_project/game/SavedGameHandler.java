package org.me.five_stones_project.game;

import java.util.BitSet;

import org.me.five_stones_project.ai.AndroidEnemy;
import org.me.five_stones_project.common.Properties;
import org.me.five_stones_project.type.Players;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;

/**
 *
 * @author Tangl Andras
 */

public class SavedGameHandler {
	private static final String ELAPSED_TIME = "elapsed_time";
	private static final String IS_SAVED_GAME = "saved_game";
	private static final String ROW_COUNT = "row_count";
	private static final String COLUMN_COUNT = "column_count";
	private static final String BOARD = "board";
	private static final String MY_SIGN = "my_sign";
	private static final String LAST_STEP_X = "last_step_x";
	private static final String LAST_STEP_Y = "last_step_y";
	private static final String LAST_STEP_PLAYER = "last_step_palyer";

	public static void save(Context ctx, GameHandler handler) {
		if(handler.signs == null || isBoardEmpty(handler.signs))
			return;
		
		SharedPreferences.Editor editor = ctx.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE).edit();
		
		editor.putLong(ELAPSED_TIME, handler.getElapsedTime());
		editor.putBoolean(IS_SAVED_GAME, true);
		editor.putInt(ROW_COUNT, handler.signs.length);
		editor.putInt(COLUMN_COUNT, handler.signs[0].length);
		editor.putString(BOARD, compressBoard(handler.signs));
		editor.putInt(MY_SIGN, handler.me.ordinal());
		editor.putInt(LAST_STEP_X, handler.getLastStep().x);
		editor.putInt(LAST_STEP_Y, handler.getLastStep().y);
		editor.putBoolean(LAST_STEP_PLAYER, handler.getLastStepPlayer() == handler.me);
		
		editor.commit();
	}
	
	public static void load(Context ctx, GameHandler handler, AndroidEnemy enemy, GameView view) {
		SharedPreferences sp = ctx.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE);

		Players me = Players.values()[sp.getInt(MY_SIGN, Players.X.ordinal())];
		Players android = me == Players.X ? Players.O : Players.X;
		
    	enemy.initialize(me, android);
        handler.initialize(enemy, view, me, android, false);
        
        handler.setElapsedTime(sp.getLong(ELAPSED_TIME, 0));
		handler.signs = decompressBoard(sp.getInt(ROW_COUNT, 0), 
				sp.getInt(COLUMN_COUNT, 0), sp.getString(BOARD, "0"));
		handler.setLastStep(new Point(sp.getInt(LAST_STEP_X, 0), sp.getInt(LAST_STEP_Y, 0)), 
				sp.getBoolean(LAST_STEP_PLAYER, true) ? handler.me : handler.enemy);
	}
	
	public static boolean isSavedGame(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE);
		
		return sp.getBoolean(IS_SAVED_GAME, false);
	}
	
	public static void clearSavedGame(Context ctx) {
		SharedPreferences.Editor editor = ctx.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE).edit();
		
		editor.putBoolean(IS_SAVED_GAME, false);
		
		editor.commit();
	}
	
	public static String compressBoard(int[][] board) {
		int bit = 0;
		BitSet set = new BitSet();
		
		for(int i = 0; i < board.length; ++i)
			for(int j = 0; j < board[0].length; ++j) { 					
				if(board[i][j] == Players.None.ordinal()) {					
					bit++;
				}
				else {
					if(board[i][j] == Players.X.ordinal()) {					
						set.set(bit);
						bit += 2;
					}
					else if(board[i][j] == Players.O.ordinal()) {					
						set.set(bit);					
						set.set(bit + 1);
						bit += 2;
					}
				}
			}
		
		int pos = 0;
		String ret = "";
		for(int i = 0; i < set.size(); i += 7) {
			for(int j = 0; j < 7; ++j)
				pos |= ((set.get(i + j) ? 1 : 0) << j);
			ret += Properties.characterTable[pos];
			pos = 0;
		}
			
		return ret;
	}
	
	public static int[][] decompressBoard(int rowCount, int columnCount, String boardString) {
		if(rowCount == 0 || columnCount == 0 || boardString.equals("0"))
			return null;
		
		int[][] board = new int[rowCount][columnCount];
		
		int bit = 0;		
		BitSet set = new BitSet();
		for(int i = 0; i < boardString.length(); ++i) {
			int pos = Properties.getPosition(boardString.charAt(i));
			for(int j = 0; j < 7; ++j)
				if((pos >> j) % 2 != 0)
					set.set(i * 7 + j);
		}
			
		bit = 0;
		for(int i = 0; i < board.length; ++i)
			for(int j = 0; j < board[0].length; ++j) {
				if(!set.get(bit)) {
					board[i][j] = 0;
					bit++;
				}
				else {
					if(!set.get(bit + 1))
						board[i][j] = Players.X.ordinal();
					else
						board[i][j] = Players.O.ordinal();
					bit += 2;
				}
			}
		
		return board;
	}
	
	public static boolean isBoardEmpty(int[][] board) {
		for(int i = 0; i < board.length; ++i)
			for(int j = 0; j < board[0].length; ++j)
				if(board[i][j] != Players.None.ordinal())
					return false;
		return true;
	}
}
