package org.me.five_stones_project.activity;

import org.me.five_stones_project.R;
import org.me.five_stones_project.common.MapFactory;
import org.me.five_stones_project.internet.InternetEnemy;
import org.me.five_stones_project.internet.PendingListener;
import org.me.five_stones_project.internet.PendingThread;
import org.me.five_stones_project.internet.WebService;
import org.me.five_stones_project.type.Players;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 *
 * @author Tangl Andras
 */

public class InternetGameActivity extends GameActivity implements PendingListener {
	private String id;
	private ProgressDialog dialog;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		try {
			id = WebService.executeRequest("/gamec/connect", null);
			startDiscover();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(InternetGameActivity.this, 
					R.string.networkError, 3000);
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		try {
			WebService.executeRequest("/gamec/disconnect", 
				MapFactory.createMap(new String[] { "id" }, new String[] { id }));
			((InternetEnemy)enemy).close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public void startDiscover() {
		try {
			boolean start = Boolean.parseBoolean(WebService.executeRequest("/gamec/bind", 
				MapFactory.createMap(new String[] { "id" }, new String[] { id })));
			
			if(start) {
				enemy = new InternetEnemy(this);
				boolean begin = ((InternetEnemy)enemy).start(handler);
				handler.initialize(enemy, view, Players.X, Players.O, false);

				if(begin)
					handler.setLastStep(new Point(-1, -1), Players.O);
				else
					handler.setLastStep(new Point(-1, -1), Players.X);
			}
			else {				
				dialog = ProgressDialog.show(this, "", 
						getResources().getString(R.string.connect), true);
				new PendingThread("/gamec/getenemy", id, 1000, this).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, R.string.networkError, 3000);
			finish();
		}
	}
	
	@Override
	public void onSuccess(String result) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				dialog.dismiss();
			}
		});
		
		enemy = new InternetEnemy(this);
		boolean begin = ((InternetEnemy)enemy).start(handler);
		handler.initialize(enemy, view, Players.O, Players.X, false);
		
		if(begin)
			handler.setLastStep(new Point(-1, -1), Players.X);
		else
			handler.setLastStep(new Point(-1, -1), Players.O);
	}
	
	@Override
	public void onFailed() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				dialog.dismiss();

				new AlertDialog.Builder(InternetGameActivity.this)
					.setMessage(R.string.noEnemy)
					.setPositiveButton(R.string.again, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							startDiscover();
						}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							InternetGameActivity.this.finish();
						}
					}).show();
			}
		});
	}
	
	@Override
	public void reinitilize() {
		super.reinitilize();
		
		handler.reinitilize();
		view.reinitilize();
		
		startDiscover();
	}
	
	public String getId() {
		return id;
	}
}
