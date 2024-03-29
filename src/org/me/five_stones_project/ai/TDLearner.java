package org.me.five_stones_project.ai;

import java.util.ArrayList;

/**
 *
 * @author Tangl Andras
 */

public class TDLearner {
	private float alfa = 0.05f;
	
	private float gamma = .84f;	
	private float lambda = .34f;

	private float[] tetas;

	private ArrayList<State> states = new ArrayList<State>();
	
	public TDLearner(int patternSize) {
		tetas = new float[patternSize];
	}

	public void execute() {	
		// TD(gamma) with eligibility traces
		float[] e = new float[tetas.length];
		
		for(int i = Math.max(states.size() - 10, 0); i < states.size() - 1 ; ++i) {
			State s = states.get(i);
			State ns = states.get(i + 1);
			
			float delta = ns.getReward() + gamma * ns.getUtility() - s.getUtility();
			for(int j = 0; j < e.length; ++j)
				e[j] = gamma * lambda * e[j] + s.getFi(j);
			for(int j = 0; j < tetas.length; ++j)
				tetas[j] += alfa * delta * e[j];
		}
	}
	
	public void addState(State ns) {
		states.add(ns);
	}
	
	public void removeState(State s) {
		states.remove(s);
	}
	
	public State getPreviousState() {
		return states.get(states.size() - 1);
	}
	
	public void clearStates() {
		states.clear();
	}
	
	public void reset(int patternSize) {
		alfa = 0;
		gamma = 0;
		lambda = 0;

		tetas = new float[patternSize];	
	}
	
	public float[] getTetas() {
		return tetas;
	}
	
	public void setTetas(float[] tetas) {
		for(int i = 0; i < tetas.length; ++i)
			this.tetas[i] = tetas[i];
	}
}
