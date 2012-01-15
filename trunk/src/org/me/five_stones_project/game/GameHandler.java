package org.me.five_stones_project.game;

import java.util.ArrayList;

import org.me.five_stones_project.IEnemy;
import org.me.five_stones_project.type.Players;
import org.me.five_stones_project.ai.PatternCounter;
import org.me.five_stones_project.common.HighScoreHelper;

import android.graphics.Point;
import android.os.Handler;
import android.util.Pair;

/**
 *
 * @author Tangl Andras
 */

public class GameHandler {
	private static final int MAX_BOARD_X = 25;
	private static final int MAX_BOARD_Y = 25;
	
	public static final int INC_LEFT = 1;
	public static final int INC_RIGHT = 2;
	public static final int INC_TOP = 4;
	public static final int INC_BOTTOM = 8;
	public static final int INC_LEFT_TOP = 5;
	public static final int INC_RIGHT_BOTTOM = 10;
	public static final int INC_LEFT_BOTTOM = 9;
	public static final int INC_RIGHT_TOP = 6;
	
	private class Step {
		public Point point;
		public Players player;
		
		public Step(Point p, Players player) {
			this.point = p;
			this.player = player;
		}
	}
	
	public int[][] signs;
	public Players me, enemy;
	public GameStatistics stat = null;

	private GameView view;
	private IEnemy ienemy;
	private boolean gameEnds = false;
	private Object lock = new Object();
	private long startTime, elapsedTime = 0;
	private ArrayList<Step> mySteps = new ArrayList<Step>(), 
							 enemySteps = new ArrayList<Step>();
	private Step lastStep = new Step(new Point(-1, -1), Players.None);
	
	public void initialize(IEnemy ienemy, GameView view, Players me, Players enemy) {
		this.me = me;
		this.view = view;
		this.enemy = enemy;
		this.ienemy = ienemy;
		
		reinitilize();
	}
	
	public void reinitilize() {		
		stat = null;
		gameEnds = false;
		elapsedTime = 0;
		mySteps.clear();
		enemySteps.clear();
		startTime = System.currentTimeMillis();
		lastStep = new Step(new Point(-1, -1), Players.None);
	}

	//getters
	public boolean IsGameEnds() {
		return gameEnds;
	}
	
	public Point getLastStep() {
		return lastStep.point;
	}
	
	public void setLastStep(Point p, Players player) {
		lastStep.point.set(p.x, p.y);
		lastStep.player = player;
		
		if(player == me)
			mySteps.add(lastStep);
		else if(player == enemy)
			enemySteps.add(lastStep);
	}
	
	public Players getLastStepPlayer() {
		return lastStep.player;
	}
	
	//	
	public void pauseGame() {
		elapsedTime += System.currentTimeMillis() - startTime;		
	}
	
	public void continueGame() {
		startTime = System.currentTimeMillis();
	}
	
	public void saveGame() {
		SavedGameHandler.save(this);
	}
	
	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	/**
	 * increment board size if it is necessary	
	 * @param where
	 */
	public void incrementBoard(int where) {
		int width = signs.length, 
			height = signs[0].length;
		int[][] newSigns = null; 
		switch(where) {
		case INC_LEFT :
			lastStep.point.x++;
			newSigns = new int[width + 1][height];
			refreshPreviousSteps(1, 0);
			arrayCopy(signs, newSigns, 1, 0);
			break;
		case INC_LEFT_BOTTOM :
			lastStep.point.x++;
			newSigns = new int[width + 1][height + 1];
			refreshPreviousSteps(1, 0);
			arrayCopy(signs, newSigns, 1, 0);
			break;
		case INC_RIGHT : 
			newSigns = new int[width + 1][height];
			refreshPreviousSteps(0, 0);
			arrayCopy(signs, newSigns, 0, 0);
			break;
		case INC_BOTTOM : 
			newSigns = new int[width][height + 1];
			refreshPreviousSteps(0, 0);
			arrayCopy(signs, newSigns, 0, 0);
			break;
		case INC_RIGHT_BOTTOM : 
			newSigns = new int[width + 1][height + 1];
			refreshPreviousSteps(0, 0);
			arrayCopy(signs, newSigns, 0, 0);
			break;
		case INC_TOP : 
			lastStep.point.y++;
			newSigns = new int[width][height + 1];
			refreshPreviousSteps(0, 1);
			arrayCopy(signs, newSigns, 0, 1);
			break;
		case INC_RIGHT_TOP : 
			lastStep.point.y++;
			newSigns = new int[width + 1][height + 1];
			refreshPreviousSteps(0, 1);
			arrayCopy(signs, newSigns, 0, 1);
			break;
		case INC_LEFT_TOP : 
			lastStep.point.x++;
			lastStep.point.y++;
			newSigns = new int[width + 1][height + 1];
			refreshPreviousSteps(1, 1);
			arrayCopy(signs, newSigns, 1, 1);
			break;
		default: break;
		}
		signs = newSigns;
	}
	
	/**
	 * update the table stores the current state of the game
	 * @param src
	 * @param dst
	 * @param skipX
	 * @param skipY
	 */
	private void arrayCopy(int[][] src, int[][] dst, int skipX, int skipY) {
		for(int i = 0; i < src.length; ++i)
			for(int j = 0; j < src[0].length; ++j)
				dst[skipX + i][skipY + j] = src[i][j];
	}
	
	/**
	 * refresh the previous steps if the board size changed
	 * @param dx
	 * @param dy
	 */
	private void refreshPreviousSteps(int dx, int dy) {
		for(Step step : mySteps) {
			step.point.x += dx;
			step.point.y += dy;
		}
		for(Step step : enemySteps) {
			step.point.x += dx;
			step.point.y += dy;
		}
	}
	
	public void makeMyStep(Point myStep) {
		lastStep = new Step(myStep, me);
		signs[myStep.x][myStep.y] = me.ordinal();
				
		checkStatus(myStep);		
		mySteps.add(lastStep);
		ienemy.updateState(this);
		
		if(!gameEnds) 
			enemyStep();
		else
			checkFinish();
	}
	
	public void enemyStep() {
		ienemy.makeStep(this);
	}
	
	public void enemyStep(Point point) {
		synchronized (lock) {
			if(lastStep.player != enemy) {
				lastStep = new Step(point, enemy);						
				signs[point.x][point.y] = enemy.ordinal();
									
				checkStatus(point);
				enemySteps.add(lastStep);
				ienemy.updateState(this);
				
				view.translate();
			}
		}
	}
	
	public void checkFinish() {
		view.invalidate();
		if(gameEnds) {
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					ienemy.showEndDialog(GameHandler.this);
				}
			}, 300);
		}
	}
	
	public void makeStepBack() {
		if(!ienemy.cancel())
			return;
		
		synchronized (lock) {
			if(lastStep.player == me) {
				if(mySteps.size() < 1)
					return;
				
				signs[lastStep.point.x][lastStep.point.y] = Players.None.ordinal();
				mySteps.remove(mySteps.size() - 1);
				
				view.clearCell(lastStep.point);				
			}
			else {
				if(mySteps.size() + enemySteps.size() <= 2)
					return;
				
				Step step = mySteps.get(mySteps.size() - 1);
				view.clearCell(step.point);
				signs[step.point.x][step.point.y] = Players.None.ordinal();
				mySteps.remove(step);
						
				step = enemySteps.get(enemySteps.size() - 1);
				view.clearCell(step.point);
				signs[step.point.x][step.point.y] = Players.None.ordinal();
				enemySteps.remove(step);
			}
			
			lastStep = enemySteps.get(enemySteps.size() - 1);
			view.translate();
		}
	}
	
	/**
	 * check whether the game has ended after the previous step
	 * @param step
	 */
	private void checkStatus(Point step) {
		int inc = 0;
		if(step.x - 1 <= 0 && signs.length < MAX_BOARD_X)
			inc += INC_LEFT;
		if(step.y - 1 <= 0 && signs[0].length < MAX_BOARD_Y)
			inc += INC_TOP;
		if(step.x + 2 >= signs.length && signs.length < MAX_BOARD_X)
			inc += INC_RIGHT;
		if(step.y + 2 >= signs[0].length && signs[0].length < MAX_BOARD_Y)
			inc += INC_BOTTOM;
		
		if(inc != 0) {
			incrementBoard(inc);
			view.increaseBoard(inc);
		}
		
		Pair<Point, Point> five = PatternCounter.searchForFive(
				signs, lastStep.point, lastStep.player.getShift());
		if(five != null) {
			gameEnds = true;
			stat = new GameStatistics();
			stat.me = lastStep.player == me;
			stat.winner = lastStep.player;
			stat.elapsedTime = elapsedTime + System.currentTimeMillis() - startTime;
			stat.start = five.first;
			stat.end = five.second;
			
			HighScoreHelper.updateHighScores(stat);
		}
		else if(isBoardFull()) {
			gameEnds = true;
			stat = new GameStatistics();
			stat.winner = Players.Draw;
		}
	}
	
	/**
	 * check whether the board is full and no empty spaces left
	 * If this becomes true the game will end with a draw
	 * @return
	 */
	private boolean isBoardFull() {
		for(int i = 0; i < signs.length; ++i)
			for(int j = 0; j < signs[0].length; ++j)
				if(signs[i][j] == Players.None.ordinal())
					return false;
		return true;
	}
}
