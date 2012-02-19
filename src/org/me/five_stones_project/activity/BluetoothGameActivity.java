package org.me.five_stones_project.activity;


import org.me.five_stones_project.bluetooth.BluetoothEnemy;
import org.me.five_stones_project.common.Properties;
import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.type.Players;

import org.me.five_stones_project.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;

/**
 *
 * @author Tangl Andras
 */

public class BluetoothGameActivity extends GameActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        enemy = new BluetoothEnemy(this);
        
        if(Properties.isServer)
        	handler.initialize(enemy, view, Players.O, Players.X, false);
        else
        	handler.initialize(enemy, view, Players.X, Players.O, false);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		((BluetoothEnemy)enemy).close();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
				.setMessage(R.string.exitQuestion)
			    .setPositiveButton(R.string.yes, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				})
				.setNegativeButton(R.string.no, null)
				.show();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void firstStep(Point point) {
		if(handler.getLastStepPlayer() == Players.None) {
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(300);
			
			handler.enemyStep(point, GameOptions.getInstance().isAnimation());
		}
	}
	
	@Override
	public void reinitilize() {
		super.reinitilize();
		
		handler.reinitilize();
		view.reinitilize();
	}
}
