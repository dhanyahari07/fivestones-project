package org.me.five_stones_project.ai;

import java.util.ArrayList;

import org.me.five_stones_project.activity.GameActivity;
import org.me.five_stones_project.game.GameHandler;
import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.type.Descriptions;
import org.me.five_stones_project.type.Players;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.Pair;

/**
 * android player using reinforcement
 * learning with value (function) approximation
 * @author Tangl Andras
 */

public class RLPlayer extends AndroidEnemy {
	private static final int FI_LENGTH = 16;
	private static final float[] TETAS = new float[] {
		-4.397558f, 0.06014589f, -0.25848636f, -0.208716f,
		-0.2103642f, -0.11668284f, -0.1295836f, -1.3829728f,
		4.02998f, -0.28665635f, -0.11349648f, 1.6050756f,
		0.17167139f, -0.14448297f, 0.14321822f, 10.694175f
	};
	
	private State lastState;
	private Descriptions level;
	private TDLearner androidLearner;
	private float alfa, beta, MAX = 1, attack = 1.2f;

	public RLPlayer() {
		androidLearner = new TDLearner(FI_LENGTH);
		level = GameOptions.getInstance().getCurrentLevel();
		
		if(level == Descriptions.VeryHard)
			load();
		else
			/* use default teta array */
			androidLearner.setTetas(TETAS);
	}
	
	@Override
	public void showEndDialog(GameHandler handler) {
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
			
			/*
			 * modify the updated fi
			 * this modification depends on the winner of the last game
			 * the modification only necessary in the early stages of the
			 * execution of the learning algorithm
			float[] tetas = androidLearner.getTetas();
			if(handler.stat.winner == android)
				for(int i = 0; i < tetas.length / 2; ++i)
					tetas[i] -= 0.05 * tetas[tetas.length / 2 + i];
			else if(handler.stat.winner == human)
				for(int i = 0; i < tetas.length / 2; ++i)
					tetas[tetas.length / 2 + i] -= 0.05 * tetas[i];
			*/
		}
		
		super.showEndDialog(handler);
	}
	
	@Override
	public void updateState(GameHandler handler) {		
		float reward = -0.04f;		
		if(handler.getLastStepPlayer() == human && level == Descriptions.VeryHard) {
			lastState = new State(calcFi(handler.signs), androidLearner.getTetas(), reward);
			androidLearner.addState(lastState);
		}
	}
	
	@Override
	protected void finish() {
		if(level == Descriptions.VeryHard)
			save();
	}
	
	private float[] calcFi(int[][] board) {
		float[] humanOpenedOne = new float[7];
		float[] humanOpenedTwo = new float[7];
		float[] androidOpenedOne = new float[7];
		float[] androidOpenedTwo = new float[7];
		
		int humanCount = -1, androidCount = -1;
		boolean humanClosed = true,	androidClosed = true;
		//horizontal
		for(int j = 0; j < board[0].length; ++j) {
			for(int i = 0; i < board.length; ++i) {
				if(board[i][j] == android.ordinal()) {
					if(i - 1 >= 0) {
						if(board[i - 1][j] == Players.None.ordinal()) {
							androidClosed = false;
							androidCount = 0;
						}
						else if(board[i - 1][j] == android.ordinal()) {
							androidCount++;
						}
						else if(board[i - 1][j] == human.ordinal()) {
							if(!humanClosed)
								humanOpenedOne[humanCount]++;
							androidClosed = true;
							androidCount = 0;
						}
					}
					else
						androidCount = 0;
				}
				else if(board[i][j] == human.ordinal()) {
					if(i - 1 >= 0) {
						if(board[i - 1][j] == Players.None.ordinal()) {
							humanClosed = false;
							humanCount = 0;
						}
						else if(board[i - 1][j] == human.ordinal()) {
							humanCount++;
						}
						else if(board[i - 1][j] == android.ordinal()) {
							if(!androidClosed)
								androidOpenedOne[androidCount]++;
							humanClosed = true;
							humanCount = 0;
						}
					}
					else {
						humanCount = 0;
					}
				}
				else if(board[i][j] == Players.None.ordinal() && i - 1 >= 0) {
					if(board[i - 1][j] == android.ordinal()) {
						if(androidClosed)
							androidOpenedOne[androidCount]++;
						else
							androidOpenedTwo[androidCount]++;
					}
					else if(board[i - 1][j] == human.ordinal()) {
						if(humanClosed)
							humanOpenedOne[humanCount]++;
						else
							humanOpenedTwo[humanCount]++;
					}
				}
			}
			humanCount = -1;
			androidCount = -1;
			humanClosed = true;
			androidClosed = true;
		}
		
		//vertical
		for(int i = 0; i < board.length; ++i) {
			for(int j = 0; j < board[0].length; ++j) {
				if(board[i][j] == android.ordinal()) {
					if(j - 1 >= 0) {
						if(board[i][j - 1] == Players.None.ordinal()) {
							androidClosed = false;
							androidCount = 0;
						}
						else if(board[i][j - 1] == android.ordinal()) {
							androidCount++;
						}
						else if(board[i][j - 1] == human.ordinal()) {
							if(!humanClosed)
								humanOpenedOne[humanCount]++;
							androidClosed = true;
							androidCount = 0;
						}
					}
					else
						androidCount = 0;
				}
				else if(board[i][j] == human.ordinal()) {
					if(j - 1 >= 0) {
						if(board[i][j - 1] == Players.None.ordinal()) {
							humanClosed = false;
							humanCount = 0;
						}
						else if(board[i][j - 1] == human.ordinal()) {
							humanCount++;
						}
						else if(board[i][j - 1] == android.ordinal()) {
							if(!androidClosed)
								androidOpenedOne[androidCount]++;
							humanClosed = true;
							humanCount = 0;
						}
					}
					else {
						humanCount = 0;
					}
				}
				else if(board[i][j] == Players.None.ordinal() && j - 1 >= 0) {
					if(board[i][j - 1] == android.ordinal()) {
						if(androidClosed)
							androidOpenedOne[androidCount]++;
						else
							androidOpenedTwo[androidCount]++;
					}
					else if(board[i][j - 1] == human.ordinal()) {
						if(humanClosed)
							humanOpenedOne[humanCount]++;
						else
							humanOpenedTwo[humanCount]++;
					}
				}
			}
			humanCount = -1;
			androidCount = -1;
			humanClosed = true;
			androidClosed = true;
		}
		
		//diagonal '/'
		for(int s = 0; s < board.length + board[0].length - 1; ++s) {
			int from = Math.max(0, s - board[0].length + 1);
			int to = Math.min(board.length - 1, s);
			
			for(int c = from; c <= to ; ++c) {
				if(board[c][s - c] == android.ordinal()) {
					if(c - 1 >= 0 && s - c + 1 < board[0].length) {
						if(board[c - 1][s - c + 1] == Players.None.ordinal()) {
							androidClosed = false;
							androidCount = 0;
						}
						else if(board[c - 1][s - c + 1] == android.ordinal()) {
							androidCount++;
						}
						else if(board[c - 1][s - c + 1] == human.ordinal()) {
							if(!humanClosed)
								humanOpenedOne[humanCount]++;
							androidClosed = true;
							androidCount = 0;
						}
					}
					else
						androidCount = 0;
				}
				else if(board[c][s - c] == human.ordinal()) {
					if(c - 1 >= 0 && s - c + 1 < board[0].length) {
						if(board[c - 1][s - c + 1] == Players.None.ordinal()) {
							humanClosed = false;
							humanCount = 0;
						}
						else if(board[c - 1][s - c + 1] == human.ordinal()) {
							humanCount++;
						}
						else if(board[c - 1][s - c + 1] == android.ordinal()) {
							if(!androidClosed)
								androidOpenedOne[androidCount]++;
							humanClosed = true;
							humanCount = 0;
						}
					}
					else {
						humanCount = 0;
					}
				}
				else if(board[c][s - c] == Players.None.ordinal() && c - 1 >= 0 && s - c + 1 < board[0].length) {
					if(board[c - 1][s - c + 1] == android.ordinal()) {
						if(androidClosed)
							androidOpenedOne[androidCount]++;
						else
							androidOpenedTwo[androidCount]++;
					}
					else if(board[c - 1][s - c + 1] == human.ordinal()) {
						if(humanClosed)
							humanOpenedOne[humanCount]++;
						else
							humanOpenedTwo[humanCount]++;
					}
				}
			}
			
			humanCount = -1;
			androidCount = -1;
			humanClosed = true;
			androidClosed = true;
		}
		
		//diagonal '\'
		for(int s = 0; s < board.length + board[0].length - 1; ++s) {
			int from = Math.max(0, s - board[0].length + 1);
			int to = Math.min(board.length - 1, s);
			for(int c = to; c >= from; --c) {
				int d = board.length - 1 - c;
				if(board[d][s - c] == android.ordinal()) {
					if(d - 1 >= 0 && s - c - 1 >= 0) {
						if(board[d - 1][s - c - 1] == Players.None.ordinal()) {
							androidClosed = false;
							androidCount = 0;
						}
						else if(board[d - 1][s - c - 1] == android.ordinal()) {
							androidCount++;
						}
						else if(board[d - 1][s - c - 1] == human.ordinal()) {
							if(!humanClosed)
								humanOpenedOne[humanCount]++;
							androidClosed = true;
							androidCount = 0;
						}
					}
					else
						androidCount = 0;
				}
				else if(board[d][s - c] == human.ordinal()) {
					if(d - 1 >= 0 && s - c - 1 >= 0) {
						if(board[d - 1][s - c - 1] == Players.None.ordinal()) {
							humanClosed = false;
							humanCount = 0;
						}
						else if(board[d - 1][s - c - 1] == human.ordinal()) {
							humanCount++;
						}
						else if(board[d - 1][s - c - 1] == android.ordinal()) {
							if(!androidClosed)
								androidOpenedOne[androidCount]++;
							humanClosed = true;
							humanCount = 0;
						}
					}
					else {
						humanCount = 0;
					}
				}
				else if(board[d][s - c] == Players.None.ordinal() && d - 1 >= 0 && s - c - 1 >= 0) {
					if(board[d - 1][s - c - 1] == android.ordinal()) {
						if(androidClosed)
							androidOpenedOne[androidCount]++;
						else
							androidOpenedTwo[androidCount]++;
					}
					else if(board[d - 1][s - c - 1] == human.ordinal()) {
						if(humanClosed)
							humanOpenedOne[humanCount]++;
						else
							humanOpenedTwo[humanCount]++;
					}
				}
			}
			
			humanCount = -1;
			androidCount = -1;
			humanClosed = true;
			androidClosed = true;
		}
		
		float[] fi = new float[FI_LENGTH];
		System.arraycopy(humanOpenedOne, 0, fi, 0, 4);
		System.arraycopy(humanOpenedTwo, 0, fi, 4, 4);
		System.arraycopy(androidOpenedOne, 0, fi, 8, 4);
		System.arraycopy(androidOpenedTwo, 0, fi, 12, 4);		
		
		return modifyFi(fi);
	}
	
	private float[] modifyFi(float[] fi) {
		float[] nfi = new float[fi.length];
		
		for(int i = 0; i < fi.length; ++i)
			nfi[i] = fi[i] == 0 ? 0 : 1;
		
		return nfi;
	}
	
	@Override
	protected Point findBestStep(GameHandler handler) {
		int[][] copy = copyBoard(handler.signs);
		if(isBoardEmpty(copy) == 0) {
			androidLearner.addState(new EmptyState(FI_LENGTH));
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
		alfa = Float.NEGATIVE_INFINITY;
		beta = Float.POSITIVE_INFINITY;
		float maxU = Float.NEGATIVE_INFINITY;

		int maxDepth = 0;
		if(level == Descriptions.Normal)
			maxDepth = 2;
		else if(level == Descriptions.Hard)
			maxDepth = 4;
		else if(level == Descriptions.VeryHard)
			maxDepth = 6;
		
		for(Point point : relevantSpaces) {	
			copy[point.x][point.y] = android.ordinal();
			
			float minU = minSearch(copy, updateRelevantSpaces(
					copy, relevantSpaces, point, 1), maxDepth, 0);
			
			if(minU > maxU) {
				maxU = minU;
				best.set(point.x, point.y);
			}
			
			copy[point.x][point.y] = Players.None.ordinal();
		}
		
		return best;
	}

	private float maxSearch(int[][] board, 
			ArrayList<Point> relevantSpaces, int maxDepth, int currentDepth) {		
		for(Point p : relevantSpaces) {
			board[p.x][p.y] = android.ordinal();
			Pair<Point, Point> five = PatternCounter.searchForFive(board, p, android.getShift());
			board[p.x][p.y] = Players.None.ordinal();
			
			if(five != null)
				return MAX;
		}
		
		float maxU = Float.NEGATIVE_INFINITY;
		for(Point space : relevantSpaces) {
			board[space.x][space.y] = android.ordinal();
								
			float minU;
			if(maxDepth > currentDepth)
				minU = minSearch(board,	updateRelevantSpaces(board, 
					relevantSpaces, space, 1), maxDepth, ++currentDepth);
			else
				minU = attack * State.calculateUtility(calcFi(board), androidLearner.getTetas());
						
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
	
	private float minSearch(int[][] board, 
			ArrayList<Point> relevantSpaces, int maxDepth, int currentDepth) {		
		for(Point p : relevantSpaces) {
			board[p.x][p.y] = human.ordinal();
			Pair<Point, Point> five = PatternCounter.searchForFive(board, p, human.getShift());
			board[p.x][p.y] = Players.None.ordinal();
			
			if(five != null)
				return -MAX;
		}
		
		float minU = Float.POSITIVE_INFINITY;
		for(Point space : relevantSpaces) {
			board[space.x][space.y] = human.ordinal();

			float maxU;
			if(maxDepth > currentDepth)
				maxU = maxSearch(board, updateRelevantSpaces(board, 
					relevantSpaces, space, 1), maxDepth, ++currentDepth);
			else
				maxU = State.calculateUtility(calcFi(board), androidLearner.getTetas());
			
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
	
	private final String TETA = "teta_";
	private void save() {
		Context currentContext = GameActivity.getInstance();
		SharedPreferences.Editor ed = currentContext.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE).edit();
		
		for(int i = 0; i < TETAS.length; ++i)
			ed.putFloat(TETA + Integer.toString(i), androidLearner.getTetas()[i]);
		
		ed.commit();
	}
	
	private void load() {
		float[] tetas = new float[TETAS.length];
		
		Context currentContext = GameActivity.getInstance();
		SharedPreferences sp = currentContext.getSharedPreferences(
				Activity.ACTIVITY_SERVICE, Activity.MODE_PRIVATE);
		
		for(int i = 0; i < TETAS.length; ++i)
			tetas[i] = sp.getFloat(TETA + Integer.toString(i), TETAS[i]);
		
		androidLearner.setTetas(tetas);
	}
}
