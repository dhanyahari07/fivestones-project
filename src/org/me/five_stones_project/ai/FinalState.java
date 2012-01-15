package org.me.five_stones_project.ai;

/**
 *
 * @author Tangl Andras
 */

public class FinalState extends State {
	
	public FinalState(float reward) {
		super(null, null, reward, true);
	}
	
	public FinalState(float[] fi, float[] tetas, float reward) {
		super(fi, tetas, reward, true);
	}
}
