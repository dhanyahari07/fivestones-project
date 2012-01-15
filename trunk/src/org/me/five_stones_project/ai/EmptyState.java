package org.me.five_stones_project.ai;

/**
 *
 * @author Tangl Andras
 */

public class EmptyState extends State {

	public EmptyState(int patternSize) {
		super();
		
		reward = 0;
		utility = 0;
		terminal = false;
		
		fi = new float[patternSize];
	}

}
