package org.me.five_stones_project.game;

import org.me.five_stones_project.type.Players;

import android.graphics.Point;

/**
 *
 * @author Tangl Andras
 */

public class GameStatistics {
	public long elapsedTime;
	
	/**
	 * true if the phone owner win the game
	 * false if the enemy (android, bluetooth)
	 */
	public boolean me;
	public Players winner;
	public Point start, end;
}
