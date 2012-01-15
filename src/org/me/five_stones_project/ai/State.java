package org.me.five_stones_project.ai;

/**
 *
 * @author Tangl Andras
 */

public class State {
	protected float[] fi;
	protected float reward;
	protected float utility;
	protected boolean terminal;
	
	protected State() { }
	
	public State(float[] fi, float[] tetas, float reward) {
		this(fi, tetas, reward, false);
	}
	
	public State(float[] fi, float[] tetas, float reward, boolean terminal) {
		this.fi = fi;
		this.reward = reward;
		this.terminal = terminal;
		calculateUtility(tetas);
	}
	
	public void calculateUtility(float[] tetas) {
		if(terminal)
			utility = reward;
		else
			utility = calculateUtility(fi, tetas);
	}
	
	public static float calculateUtility(float[] fi, float[] tetas) {
		float utility = 0;
		for(int i = 0; i < fi.length; ++i)
			utility += fi[i] * tetas[i];
		
		return utility;
	}
	
	public float getReward() {		
		return reward;
	}
	
	public float getUtility() {
		return utility;
	}
	
	public void setUtility(float utility) {
		this.utility = utility;
	}
	
	public boolean isTerminal() {
		return terminal;
	}
	
	public float[] getFi() {
		return fi;
	}
	
	public float getFi(int i) {
		return fi[i];
	}
}
