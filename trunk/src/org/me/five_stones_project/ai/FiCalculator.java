package org.me.five_stones_project.ai;


import org.me.five_stones_project.type.Players;

import android.graphics.Point;

/**
 *
 * @author Tangl Andras
 */

public class FiCalculator {
	float[] humanOpenedOne;
	float[] humanOpenedTwo;
	float[] androidOpenedOne;
	float[] androidOpenedTwo;
	
	public float[] calcFi(int[][] board, Players android, Players human) {
		humanOpenedOne = new float[7];
		humanOpenedTwo = new float[7];
		androidOpenedOne = new float[7];
		androidOpenedTwo = new float[7];
		
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
		
		float[] fi = new float[RLPlayer.FI_LENGTH];
		System.arraycopy(humanOpenedOne, 0, fi, 0, 4);
		System.arraycopy(humanOpenedTwo, 0, fi, 4, 4);
		System.arraycopy(androidOpenedOne, 0, fi, 8, 4);
		System.arraycopy(androidOpenedTwo, 0, fi, 12, 4);		
		
		return fi;
	}	
	
	public float[] calcDeltaFi(int[][] board, Point p, 
			Players player, Players android, Players human) {
		humanOpenedOne = new float[7];
		humanOpenedTwo = new float[7];
		androidOpenedOne = new float[7];
		androidOpenedTwo = new float[7];
		
		//vertical
		int countBefore = -1, countAfter = -1, 
			endBefore = Players.None.ordinal(), 
			endAfter = Players.None.ordinal(), 
			before = 0, after = 0;
		if(p.x - 1 >= 0) {
			before = board[p.x - 1][p.y];
			if(before != Players.None.ordinal())
				for(int x = p.x - 1; x >= 0; --x) {
					if(board[x][p.y] == before)
						countBefore++;
					else {
						endBefore = board[x][p.y];
						break;
					}
				}
		}
		if(p.x + 1 < board.length) {
			after = board[p.x + 1][p.y];
			if(after != Players.None.ordinal())
				for(int x = p.x + 1; x < board.length; ++x) {
					if(board[x][p.y] == after)
						countAfter++;
					else {
						endAfter = board[x][p.y];
						break;
					}
				}
		}
		update(player, android, human, before, after, countBefore, countAfter, endBefore, endAfter);
		
		//horizontal
		countBefore = -1; countAfter = -1;
		endBefore = Players.None.ordinal();
		endAfter = Players.None.ordinal(); 
		before = 0; after = 0;
		if(p.y - 1 >= 0) {
			before = board[p.x][p.y - 1];
			if(before != Players.None.ordinal())
				for(int y = p.y - 1; y >= 0; --y) {
					if(board[p.x][y] == before)
						countBefore++;
					else {
						endBefore = board[p.x][y];
						break;
					}
				}
		}
		if(p.y + 1 < board[0].length) {
			after = board[p.x][p.y + 1];
			if(after != Players.None.ordinal())
				for(int y = p.y + 1; y < board[0].length; ++y) {
					if(board[p.x][y] == after)
						countAfter++;
					else {
						endAfter = board[p.x][y];
						break;
					}
				}
		}
		update(player, android, human, before, after, countBefore, countAfter, endBefore, endAfter);
	
		//diagonal '/'
		countBefore = -1; countAfter = -1;
		endBefore = Players.None.ordinal();
		endAfter = Players.None.ordinal(); 
		before = 0; after = 0;
		if(p.x - 1 >= 0 && p.y + 1 < board[0].length) {
			before = board[p.x - 1][p.y + 1];
			if(before != Players.None.ordinal())
				for(int b = 1; b <= Math.min(p.x, board[0].length - p.y - 1); ++b) {
					if(board[p.x - b][p.y + b] == before)
						countBefore++;
					else {
						endBefore = board[p.x - b][p.y + b];
						break;
					}
				}
		}
		if(p.x + 1 < board.length && p.y - 1 >= 0) {
			after = board[p.x + 1][p.y - 1];
			if(after != Players.None.ordinal())
				for(int a = 1; a <= Math.min(p.y, board.length - p.x - 1); ++a) {
					if(board[p.x + a][p.y - a] == after)
						countAfter++;
					else {
						endAfter = board[p.x + a][p.y - a];
						break;
					}
				}
		}
		update(player, android, human, before, after, countBefore, countAfter, endBefore, endAfter);
		
		//diagonal '\'
		countBefore = -1; countAfter = -1;
		endBefore = Players.None.ordinal();
		endAfter = Players.None.ordinal(); 
		before = 0; after = 0;
		if(p.x - 1 >= 0 && p.y - 1 >= 0) {
			before = board[p.x - 1][p.y - 1];
			if(before != Players.None.ordinal())
				for(int b = 1; b <= Math.min(p.x, p.y); ++b) {
					if(board[p.x - b][p.y - b] == before)
						countBefore++;
					else {
						endBefore = board[p.x - b][p.y - b];
						break;
					}
				}
		}
		if(p.x + 1 < board.length && p.y + 1 < board[0].length) {
			after = board[p.x + 1][p.y + 1];
			if(after != Players.None.ordinal())
				for(int a = 1; a <= Math.min(board[0].length - p.y - 1, board.length - p.x - 1); ++a) {
					if(board[p.x + a][p.y + a] == after)
						countAfter++;
					else {
						endAfter = board[p.x + a][p.y + a];
						break;
					}
				}
		}
		update(player, android, human, before, after, countBefore, countAfter, endBefore, endAfter);
		
		float[] fi = new float[RLPlayer.FI_LENGTH];
		System.arraycopy(humanOpenedOne, 0, fi, 0, 4);
		System.arraycopy(humanOpenedTwo, 0, fi, 4, 4);
		System.arraycopy(androidOpenedOne, 0, fi, 8, 4);
		System.arraycopy(androidOpenedTwo, 0, fi, 12, 4);		
		
		return fi;
	}
	
	private void update(Players player, Players android, Players human, 
		int before, int after, int countBefore, int countAfter, int endBefore, int endAfter) {
		if(before == android.ordinal()) {
			if(endBefore == Players.None.ordinal())
				androidOpenedTwo[countBefore]--;
			else
				androidOpenedOne[countBefore]--;
		}
		if(after == android.ordinal()) {
			if(endAfter == Players.None.ordinal())
				androidOpenedTwo[countAfter]--;
			else
				androidOpenedOne[countAfter]--;
		}
		if(before == human.ordinal()) {
			if(endBefore == Players.None.ordinal())
				humanOpenedTwo[countBefore]--;
			else
				humanOpenedOne[countBefore]--;
		}
		if(after == human.ordinal()) {
			if(endAfter == Players.None.ordinal())
				humanOpenedTwo[countAfter]--;
			else
				humanOpenedOne[countAfter]--;
		}
		/////////////////////////////////////////////
		if(before != player.ordinal() && before != Players.None.ordinal()) {
			if(endBefore == Players.None.ordinal() && player == android)
				humanOpenedOne[countBefore]++;
			else if(endBefore == Players.None.ordinal() && player == human)
				androidOpenedOne[countBefore]++;
		}
		if(after != player.ordinal() && after != Players.None.ordinal()) {
			if(endAfter == Players.None.ordinal() && player == android)
				humanOpenedOne[countAfter]++;
			else if(endAfter == Players.None.ordinal() && player == human)
				androidOpenedOne[countAfter]++;
		}
		if(after == Players.None.ordinal() && before == player.ordinal()) {
			if(player == human && endBefore == Players.None.ordinal())
				humanOpenedTwo[countBefore + 1]++;
			if(player == human && endBefore != Players.None.ordinal())
				humanOpenedOne[countBefore + 1]++;
			if(player == android && endBefore == Players.None.ordinal())
				androidOpenedTwo[countBefore + 1]++;
			if(player == android && endBefore != Players.None.ordinal())
				androidOpenedOne[countBefore + 1]++;
		}
		if(before == Players.None.ordinal() && after == player.ordinal()) {
			if(player == human && endAfter == Players.None.ordinal())
				humanOpenedTwo[countAfter + 1]++;
			if(player == human && endAfter != Players.None.ordinal())
				humanOpenedOne[countAfter + 1]++;
			if(player == android && endAfter == Players.None.ordinal())
				androidOpenedTwo[countAfter + 1]++;
			if(player == android && endAfter != Players.None.ordinal())
				androidOpenedOne[countAfter + 1]++;
		}
		if(after == before && before != Players.None.ordinal() && before == player.ordinal()) {
			if(player == android) {
				if(endAfter == Players.None.ordinal() && endBefore == Players.None.ordinal())
					androidOpenedTwo[countBefore + countAfter + 2]++;
				else if(endAfter == Players.None.ordinal() || endBefore == Players.None.ordinal())
					androidOpenedOne[countBefore + countAfter + 2]++;
			}
			else if(player == human) {
				if(endAfter == Players.None.ordinal() && endBefore == Players.None.ordinal())
					humanOpenedTwo[countBefore + countAfter + 2]++;
				else if(endAfter == Players.None.ordinal() || endBefore == Players.None.ordinal())
					humanOpenedOne[countBefore + countAfter + 2]++;
			}
		}
		if(after == before && after == Players.None.ordinal()) {
			if(player == human)
				humanOpenedTwo[0]++;
			else if(player == android)
				androidOpenedTwo[0]++;
		}
	}
	
	public float[] modifyFi(float[] fi, float[] deltaFi) {		
		float[] nfi = new float[fi.length];
		for(int i = 0; i < fi.length; ++i)
			nfi[i] = (fi[i] + deltaFi[i]) == 0 ? 0 : 1;
		
		if(fi[6] + deltaFi[6] == 2)
			nfi[6] = 2;
		if(fi[14] + deltaFi[14] == 2) 
			nfi[14] = 2;
		if(fi[3] + deltaFi[3] == 2)
			nfi[3] = 2;
		if(fi[11] + deltaFi[11] == 2) 
			nfi[11] = 2;
		
		return nfi;
	}
}
