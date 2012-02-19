package org.me.five_stones_project;


import org.me.five_stones_project.activity.GameActivity;
import org.me.five_stones_project.game.GameHandler;

import org.me.five_stones_project.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 *
 * @author Tangl Andras
 */

public class HumanEnemy implements IEnemy {
	
	public HumanEnemy() { }

	@Override
	public boolean cancel() {
		return false;
	}

	@Override
	public void makeStep(GameHandler handler) {

	}

	@Override
	public void updateState(GameHandler handler) {

	}

	@Override
	public void showEndDialog(GameHandler handler) {
		int drawable = android.R.drawable.ic_popup_sync;
		
		final GameActivity instance = GameActivity.getInstance();
		new AlertDialog.Builder(instance)
	    	.setIcon(drawable)
	    	.setPositiveButton(R.string.yes, new OnClickListener() {
	    		
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					instance.reinitilize();								
				}
			})
			.setNegativeButton(R.string.no, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					instance.finish();
				}
			})
			.setTitle(R.string.newGame).show();
	}

}
