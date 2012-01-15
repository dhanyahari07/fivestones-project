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
	private Point last = new Point();
	
	@Override
	public void updateState(GameHandler handler) {
		this.last.set(handler.getLastStep().x, handler.getLastStep().y);
	}

	@Override
	protected Point findBestStep(GameHandler handler) {
		int c = isBoardEmpty(handler.signs);
		if(c == 0)
			return new Point(handler.signs.length / 2, handler.signs[0].length / 2);
		else if(c == 1)
			return new Point(last.x + 1, last.y + 1);

		int[][][] allPatterns = null;
		if(GameOptions.getInstance().getCurrentLevel() == Descriptions.Beginner)
			allPatterns = Patterns.PATTERNS_BEGINNER;
		else if(GameOptions.getInstance().getCurrentLevel() == Descriptions.Average)
			allPatterns = Patterns.PATTERNS_AVERAGE;
		
		for(int[][] patterns : allPatterns) {
			for(int p = 0; p < 2; ++p) {
				Players player = (p == 0 ? android : human);
				for(int[] pattern : patterns)
					for(int i = 0; i < handler.signs.length; ++i)
						for(int j = 0; j < handler.signs[0].length; ++j) {
							if(handler.signs[i][j] == Players.None.ordinal()) {
								handler.signs[i][j] = player.ordinal();
							
								Point point = new Point(i, j);
								int cp = PatternCounter.countPattern(
									handler.signs, pattern, point, player.getShift());
								
								handler.signs[i][j] = Players.None.ordinal();
								
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
	
	@Override
	protected void finish() { }
}
