package org.me.five_stones_project.internet;


import org.me.five_stones_project.IEnemy;
import org.me.five_stones_project.activity.GameActivity;
import org.me.five_stones_project.activity.InternetGameActivity;
import org.me.five_stones_project.common.MapFactory;
import org.me.five_stones_project.game.GameHandler;
import org.me.five_stones_project.game.GameOptions;

import org.me.five_stones_project.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.os.Vibrator;
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
				new String[] { "id", "x", "y", "grow"}, 
				new String[] { activity.getId(), 
					Integer.toString(handler.getLastStep().x), 
					Integer.toString(handler.getLastStep().y),
					Integer.toString(handler.grow.x * 10 + handler.grow.y)}));
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	@Override
	public void onSuccess(String result) {
		if(result.equals("destroy")) {
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					new AlertDialog.Builder(activity)
					.setMessage(R.string.finish)
					.setPositiveButton(R.string.again, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							activity.reinitilize();					
						}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							activity.finish();					
						}
					}).show();
				}
			});
			try {
				WebService.executeRequest("/gamep/finish", MapFactory.createMap(
					new String[] { "id" }, new String[] { activity.getId() }));
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		else {
			if(GameOptions.getInstance().isVibration()) {
				Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(300);
			}
			
			final String[] res = result.split("_");
			
			if(res.length == 3) {
				int grow = Integer.parseInt(res[2]);
				int t = grow / 10;
				handler.grow.set(t, grow - t * 10);
			}
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					handler.enemyStep(new Point(
						Integer.parseInt(res[0]), 
						Integer.parseInt(res[1])), GameOptions.getInstance().isAnimation());
				}
			});
		}
	}

	@Override
	public void onFailed() {
		exit();
	}
	
	public void close() {
		if(pending != null)
			pending.terminate();
	}
	
	public void exit() {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				showToast(R.string.networkError);
				activity.finish();
			}
		});
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
