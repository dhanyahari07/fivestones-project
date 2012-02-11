package org.me.five_stones_project.activity;

import org.me.five_stones_project.HumanEnemy;
import org.me.five_stones_project.type.Players;

import android.os.Bundle;
import android.view.KeyEvent;

/**
 *
 * @author Tangl Andras
 */

public class DualGameActivity extends GameActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		enemy = new HumanEnemy();
        
        handler.initialize(enemy, view, Players.X, Players.O, true);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void reinitilize() {
		super.reinitilize();
		
		handler.reinitilize();
		view.reinitilize();
	}
}
