package org.me.five_stones_project.activity;

import org.me.five_stones_project.bluetooth.BluetoothEnemy;
import org.me.five_stones_project.common.Properties;
import org.me.five_stones_project.type.Players;

import android.graphics.Point;
import android.os.Bundle;

/**
 *
 * @author Tangl Andras
 */

public class BluetoothGameActivity extends GameActivity {
	private BluetoothEnemy enemy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        enemy = new BluetoothEnemy(this);
        
        if(Properties.isServer)
        	handler.initialize(enemy, view, Players.O, Players.X);
        else
        	handler.initialize(enemy, view, Players.X, Players.O);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		enemy.close();
	}
	
	public void firstStep(Point point) {
		if(handler.getLastStepPlayer() == Players.None)
			handler.enemyStep(point);
	}
	
	@Override
	public void reinitilize() {
		super.reinitilize();
		
		handler.reinitilize();
		view.reinitilize();
	}
}
