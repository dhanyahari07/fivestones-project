package org.me.five_stones_project.type;

/**
 *
 * @author Tangl Andras
 */

public enum Players {
	None, X, O, Draw;
	
	public int getShift() {
		if(ordinal() == Players.X.ordinal())
			return 1;
		return 0;
	}
}
