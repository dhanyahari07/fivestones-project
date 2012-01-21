package org.me.five_stones_project.activity;

import org.me.five_stones_project.R;
import org.me.five_stones_project.ai.AndroidEnemy;
import org.me.five_stones_project.ai.RBPlayer;
import org.me.five_stones_project.ai.RLPlayer;
import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.game.SavedGameHandler;
import org.me.five_stones_project.type.Descriptions;
import org.me.five_stones_project.type.Players;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;


/**
 *
 * @author Tangl Andras
 */

public class SimpleGameActivity extends GameActivity implements OnClickListener {
	private AndroidEnemy enemy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Descriptions level  = GameOptions.getInstance().getCurrentLevel();
		if(level == Descriptions.Beginner || level == Descriptions.Average)
			enemy = new RBPlayer();
		else
			enemy = new RLPlayer();
        
        if(MainActivity.getContext().lastAction == R.main.btnStart)
	        showStartDialog();
        else if(MainActivity.getContext().lastAction == R.main.btnContinue) {
        	SavedGameHandler.load(handler, enemy, view);

        	view.drawBoard();
        	view.invalidate();
        	
        	if(handler.getLastStepPlayer() == handler.me)
        		handler.enemyStep();
        	
        	SavedGameHandler.clearSavedGame();
        }
		
		view.showAndroidMenu();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which == DialogInterface.BUTTON_POSITIVE) {			
			enemy.initialize(Players.O, Players.X);
	        handler.initialize(enemy, view, Players.O, Players.X);
	        
			handler.enemyStep();
		}
		else if(which == DialogInterface.BUTTON_NEGATIVE) {			
			enemy.initialize(Players.X, Players.O);		
	        handler.initialize(enemy, view, Players.X, Players.O);
		}
		
		dialog.dismiss();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			handler.makeStepBack();
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_HOME)
			SavedGameHandler.save(handler);
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void reinitilize() {
		super.reinitilize();
		
		handler.reinitilize();
		view.reinitilize();
		showStartDialog();
	}
	
	private void showStartDialog() {
		new AlertDialog.Builder(this)
	    	.setIcon(R.drawable.emo_im_happy)
	    	.setPositiveButton(R.string.yes, this)
			.setNegativeButton(R.string.no, this)
			.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface paramDialogInterface, int paramInt,
						KeyEvent paramKeyEvent) {
					if(paramInt == KeyEvent.KEYCODE_BACK) {
						paramDialogInterface.dismiss();
						SimpleGameActivity.this.finish();
						return true;
					}
					return false;
				}
			})
			.setTitle(R.string.androidStart).show();
	}
}
