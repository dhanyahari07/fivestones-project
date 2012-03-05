package org.me.five_stones_project.ai;

import java.util.ArrayList;

import org.me.five_stones_project.game.GameHandler;
import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.type.Descriptions;
import org.me.five_stones_project.type.Players;


import android.graphics.Point;
import android.util.Pair;

/**
 * android player using reinforcement
 * learning with value (function) approximation
 * @author Tangl Andras
 */

public class RLPlayer extends AndroidEnemy {
	public static final int FI_LENGTH = 16;
	private static final float[] DEFAULT_TETAS = new float[] {		
		-0.001f, -0.006f, -0.08f, -0.14f,
		-0.003f, -0.01f, -0.1f, -0.4f,
		0.001f, 0.006f, 0.08f, 0.14f,
		0.003f, 0.01f, 0.1f, 0.4f
	};

	private float MAX = 1;
	private int minimaxDepth;
	
	//private State lastState;
	private Descriptions level;
	private FiCalculator calculator;
	//private TDLearner androidLearner;

	public RLPlayer() {
		calculator = new FiCalculator();
		//androidLearner = new TDLearner(FI_LENGTH);
		//androidLearner.setTetas(DEFAULT_TETAS);
		
		level = GameOptions.getInstance().getCurrentLevel();
		
		if(level == Descriptions.Normal) {
			minimaxDepth = 0;
		}
		else if(level == Descriptions.Hard) {
			minimaxDepth = 2;
			
		}
		else if(level == Descriptions.VeryHard) {
			minimaxDepth = 4;
		}
	}
	
	@Override
	public void showEndDialog(GameHandler handler) {
		/*if(level == Descriptions.VeryHard) {
			float reward = -0.04f;
			if(handler.stat != null) {	
				if(handler.stat.winner == android)
					reward = MAX;
				else if(handler.stat.winner == human)
					reward = -MAX;
				else if(handler.stat.winner == Players.Draw)
					reward = 0f;
	
				if(!(lastState instanceof FinalState))
					androidLearner.removeState(lastState);
				
				androidLearner.addState(new FinalState(reward));
				androidLearner.execute();
				androidLearner.clearStates();
			}
		}*/
		
		super.showEndDialog(handler);
	}
	
	@Override
	public void updateState(GameHandler handler) {
		/*float reward = -0.04f;		
		if(handler.getLastStepPlayer() == human) {
			lastState = new State(updatedFi = calculator.calcFi(handler.signs, android, human), 
					androidLearner.getTetas(), reward);
			androidLearner.addState(lastState);
		}*/
	}
	
	@Override
	protected Point findBestStep(GameHandler handler) {
		float[] updatedFi = calculator.calcFi(handler.signs, android, human);
		int[][] copy = copyBoard(handler.signs);
		if(isBoardEmpty(copy) == 0) {
			//androidLearner.addState(new EmptyState(FI_LENGTH));
			return new Point(copy.length / 2, copy[0].length / 2);
		}
	
		ArrayList<Point> relevantSpaces = new ArrayList<Point>();
		for(int i = 0; i < copy.length; ++i)
			for(int j = 0; j < copy[0].length; ++j) {
				if(copy[i][j] != Players.None.ordinal())
					continue;
				
				if(searchNonEmptyNeighbours(copy, i, j, 1) != 0)
					relevantSpaces.add(new Point(i, j));
			}
		
		//search for possible final states - fix rule
		for(int i = 0; i < 2; ++i) {
			Players player = i == 0 ? android : human;
			for(Point p : relevantSpaces) {
				copy[p.x][p.y] = player.ordinal();
				Pair<Point, Point> five = PatternCounter.
					searchForFive(copy, p, player.getShift());
				copy[p.x][p.y] = Players.None.ordinal();
				
				if(five != null)
					return p;
			}
		}

		Point best = new Point();
		float alfa = Float.NEGATIVE_INFINITY;
		float beta = Float.POSITIVE_INFINITY;
		float maxU = Float.NEGATIVE_INFINITY;
		long start = System.currentTimeMillis();
		
		for(Point point : relevantSpaces) {	
			copy[point.x][point.y] = android.ordinal();
			
			float[] nfi = calculator.modifyFi(updatedFi, 
				calculator.calcDeltaFi(copy, point, android, android, human));
			float minU = minSearch(copy, start, updateRelevantSpaces(
					copy, relevantSpaces, point, 1), minimaxDepth, 0, alfa, beta, nfi);
			
			if(minU > maxU) {
				maxU = minU;
				best.set(point.x, point.y);
			}
			//add some random movement
			else if(minU == maxU && Math.random() < 0.5)
				best.set(point.x, point.y);
			
			copy[point.x][point.y] = Players.None.ordinal();
		}
		
		return best;
	}

	private float maxSearch(int[][] board, long start, ArrayList<Point> relevantSpaces, 
			int maxDepth, int currentDepth, float alfa, float beta, float[] fi) {		
		for(Point p : relevantSpaces) {
			board[p.x][p.y] = android.ordinal();
			Pair<Point, Point> five = PatternCounter.searchForFive(board, p, android.getShift());
			board[p.x][p.y] = Players.None.ordinal();
			
			if(five != null)
				return MAX * maxDepth / currentDepth;
		}			
		
		float maxU = Float.NEGATIVE_INFINITY;		
		for(Point space : relevantSpaces) {
			board[space.x][space.y] = android.ordinal();
								
			float minU;
			float[] nfi = calculator.modifyFi(fi, calculator.calcDeltaFi(board, space, android, android, human));
			if(maxDepth > currentDepth)
				minU = minSearch(board,	start, updateRelevantSpaces(board, 
					relevantSpaces, space, 1), maxDepth, currentDepth + 1, alfa, beta, nfi);
			else
				minU = State.calculateUtility(nfi,	/*androidLearner.getTetas()*/DEFAULT_TETAS);
			
			board[space.x][space.y] = Players.None.ordinal();
			
			if(minU >= beta)
				return minU;
			
			if(minU > maxU) {
				maxU = minU;			
				alfa = Math.max(alfa, maxU);
			}
		}
		return maxU;
	}
	
	private float minSearch(int[][] board, long start, ArrayList<Point> relevantSpaces, 
			int maxDepth, int currentDepth, float alfa, float beta, float[] fi) {		
		for(Point p : relevantSpaces) {
			board[p.x][p.y] = human.ordinal();
			Pair<Point, Point> five = PatternCounter.searchForFive(board, p, human.getShift());
			board[p.x][p.y] = Players.None.ordinal();
			
			if(five != null)
				return -MAX * maxDepth / currentDepth;
		}

		boolean timeup = false;
		float minU = Float.POSITIVE_INFINITY;
		if(System.currentTimeMillis() - start > GameOptions.getInstance().getAi(false) * 1000)
			timeup = true;
			
		for(Point space : relevantSpaces) {
			board[space.x][space.y] = human.ordinal();

			float maxU;
			float[] nfi = calculator.modifyFi(fi, calculator.calcDeltaFi(board, space, human, android, human));
			if(maxDepth > currentDepth && !timeup)
				maxU = maxSearch(board, start, updateRelevantSpaces(board, 
					relevantSpaces, space, 1), maxDepth, currentDepth + 1, alfa, beta, nfi);
			else
				maxU = State.calculateUtility(nfi, /*androidLearner.getTetas()*/DEFAULT_TETAS);
			
			board[space.x][space.y] = Players.None.ordinal();
			
			if(maxU <= alfa)
				return maxU;
			
			if(maxU < minU) {
				minU = maxU;
				beta = Math.min(beta, minU);
			}
		}
		return minU;
	}
	
	private int searchNonEmptyNeighbours(int[][] board, int x, int y, int radius) {
		int count = 0;
		for(int i = Math.max(0, x - radius); i <= Math.min(x + radius, board.length - 1); ++i)
			for(int j = Math.max(0, y - radius); j <= Math.min(y + radius, board[0].length - 1); ++j)
				if(board[i][j] != Players.None.ordinal()) {
					count++;
				}
		return count;
	}
	
	private ArrayList<Point> updateRelevantSpaces(
		int[][] board, ArrayList<Point> relevantSpaces, Point space, int radius) {
		ArrayList<Point> newSpaces = new ArrayList<Point>();
		newSpaces.addAll(relevantSpaces);
		newSpaces.remove(space);
		for(int i = Math.max(0, space.x - radius); i <= Math.min(space.x + radius, board.length - 1); ++i)
			for(int j = Math.max(0, space.y - radius); j <= Math.min(space.y + radius, board[0].length -1); ++j) {
				Point newPoint = new Point(i ,j);
				if(board[i][j] == Players.None.ordinal() && !newSpaces.contains(newPoint))
					newSpaces.add(newPoint);
			}
		return newSpaces;
	}
}
