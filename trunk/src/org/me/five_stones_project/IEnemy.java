package org.me.five_stones_project;

import org.me.five_stones_project.game.GameHandler;

/**
 *
 * @author Tangl Andras
 */

public interface IEnemy {
	boolean cancel();
	void makeStep(GameHandler handler);
	void updateState(GameHandler handler);
	void showEndDialog(GameHandler handler);
}
