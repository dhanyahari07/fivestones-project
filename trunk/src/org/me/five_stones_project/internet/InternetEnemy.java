package org.me.five_stones_project.internet;

import org.me.five_stones_project.IEnemy;
import org.me.five_stones_project.R;
import org.me.five_stones_project.activity.InternetGameActivity;
import org.me.five_stones_project.activity.GameActivity;
import org.me.five_stones_project.common.MapFactory;
import org.me.five_stones_project.game.GameHandler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.widget.Toast;

/**
 *
 * @author Tangl Andras
 */

public class InternetEnemy implements IEnemy, PendingListener {
	private PendingThread pending;
	private GameHandler handler = null;
	private InternetGameActivity activity;
	
	public InternetEnemy(InternetGameActivity activity) {
		this.activity = activity;
	}
	
	public boolean start(GameHandler handler) {
		this.handler = handler;
		
		try {
			boolean start = Boolean.parseBoolean(WebService.executeRequest("/gamep/guess", 
				MapFactory.createMap(new String[] { "id" }, new String[] { activity.getId() })));
			
			if(start)
				showToast(R.string.begin);
			else {
				showToast(R.string.opponentStart);
				pending = new PendingThread("/gamep/getostep", activity.getId(), 1000, this);
				pending.start();
			}
			
			return start;
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		
		return false;
	}
	
	@Override
	public boolean cancel() { 
		return false;
	}

	@Override
	public void makeStep(GameHandler handler) {
		this.handler = handler;
		
		pending = new PendingThread("/gamep/getostep", activity.getId(), 1000, this);
		pending.start();
	}

	@Override
	public void showEndDialog(GameHandler handler) {
		int drawable;
		if(handler.stat.winner == handler.me)
			drawable = R.drawable.emo_im_cool;
		else
			drawable = R.drawable.emo_im_crying;
		
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
		
		try {
			WebService.executeRequest("/gamep/finish", MapFactory.createMap(
				new String[] { "id" }, new String[] { activity.getId() }));
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	@Override
	public void updateState(GameHandler handler) {		
		try {
			WebService.executeRequest("/gamep/move", MapFactory.createMap(
				new String[] { "id", "x", "y"}, 
				new String[] { activity.getId(), 
					Integer.toString(handler.getLastStep().x), 
					Integer.toString(handler.getLastStep().y) }));
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	@Override
	public void onSuccess(String result) {
		if(result.equals("destroy")) {
			exit();
		}
		else {
			handler.enemyStep(new Point(
				Integer.parseInt(result.split("_")[0]), 
				Integer.parseInt(result.split("_")[1])), true);
		}
	}

	@Override
	public void onFailed() {
		exit();
	}
	
	public void close() {
		pending.terminate();
	}
	
	public void exit() {
		showToast(R.string.networkError);
		activity.finish();
	}
	
	public void showToast(final int text) {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(activity, text, 3000).show();
			}
		});
	}
}
