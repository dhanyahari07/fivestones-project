package org.me.five_stones_project.ai;

import org.me.five_stones_project.game.GameHandler;
import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.type.Descriptions;
import org.me.five_stones_project.type.Players;

import android.graphics.Point;

/**
 * rule based android player
 * @author Tangl Andras
 */

public class RBPlayer extends AndroidEnemy {
	
	@Override
	public void updateState(GameHandler handler) { }

	@Override
	protected Point findBestStep(GameHandler handler) {
		int[][] copy = copyBoard(handler.signs);
		int c = isBoardEmpty(copy);
		if(c == 0)
			return new Point(copy.length / 2, copy[0].length / 2);

		int[][][] allPatterns = null;
		if(GameOptions.getInstance().getCurrentLevel() == Descriptions.Beginner)
			allPatterns = Patterns.PATTERNS_BEGINNER;
		else if(GameOptions.getInstance().getCurrentLevel() == Descriptions.Average)
			allPatterns = Patterns.PATTERNS_AVERAGE;
		
		for(int[][] patterns : allPatterns) {
			for(int p = 0; p < 2; ++p) {
				Players player = (p == 0 ? android : human);
				for(int[] pattern : patterns)
					for(int i = 0; i < copy.length; ++i)
						for(int j = 0; j < copy[0].length; ++j) {
							if(copy[i][j] == Players.None.ordinal()) {
								copy[i][j] = player.ordinal();
							
								Point point = new Point(i, j);
								int cp = PatternCounter.countPattern(
										copy, pattern, point, player.getShift());
								
								copy[i][j] = Players.None.ordinal();
								
								if(cp != 0)
									return point;						
							}
						}
			}
		}
		
		/*
		 * make random move
		 * exactly find the first empty place on the board
		 */
		for(int i = 0; i < handler.signs.length; ++i)
			for(int j = 0; j < handler.signs[0].length; ++j)
				if(handler.signs[i][j] == Players.None.ordinal()) 
					return new Point(i, j);
		
		//default return value
		return null;
	}
}
