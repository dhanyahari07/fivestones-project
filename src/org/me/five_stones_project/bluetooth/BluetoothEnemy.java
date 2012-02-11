package org.me.five_stones_project.bluetooth;

import org.me.five_stones_project.IEnemy;
import org.me.five_stones_project.R;
import org.me.five_stones_project.activity.BluetoothGameActivity;
import org.me.five_stones_project.activity.GameActivity;
import org.me.five_stones_project.common.Message;
import org.me.five_stones_project.common.Properties;
import org.me.five_stones_project.game.GameHandler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.os.Handler;

/**
 *
 * @author Tangl Andras
 */

public class BluetoothEnemy implements IEnemy {
	private Handler osHandler;
	private ConnectedThread thread;
	private MessageProcessor processor;
	private GameHandler handler = null;
	private BluetoothGameActivity activity;
	
	public BluetoothEnemy(BluetoothGameActivity activity) {
		this.activity = activity;
		
		thread = new ConnectedThread(Properties.socket, this);
		thread.start();
		
		osHandler = new Handler();
		processor = new MessageProcessor();
	}
	
	public void processMessage(Message msg) {
		processor.point = new Point(msg.getPoint().x, msg.getPoint().y);
		osHandler.post(processor);
	}
	
	@Override
	public boolean cancel() { 
		return false;
	}

	@Override
	public void makeStep(GameHandler handler) {
		this.handler = handler;
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
	}

	@Override
	public void updateState(GameHandler handler) {
		thread.write(new Message(handler.getLastStep()));
	}
	
	public void close() {
		thread.cancel();
	}
	
	private class MessageProcessor implements Runnable {
		public Point point;
		
		public MessageProcessor() {
			point = new Point();
		}
		
		@Override
		public void run() {
			if(handler != null) {
				handler.enemyStep(point, true);
				handler = null;
			}
			else
				activity.firstStep(point);
		}
	}
}
