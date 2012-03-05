package org.me.five_stones_project.activity;


import org.me.five_stones_project.common.MapFactory;
import org.me.five_stones_project.internet.InternetEnemy;
import org.me.five_stones_project.internet.PendingListener;
import org.me.five_stones_project.internet.PendingThread;
import org.me.five_stones_project.internet.WebService;
import org.me.five_stones_project.type.Players;

import org.me.five_stones_project.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 *
 * @author Tangl Andras
 */

public class InternetGameActivity extends GameActivity implements PendingListener {
	private String id;
	private ProgressDialog dialog;
	private PendingThread pending;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		new AsyncTask<Void, Void, Boolean>() {
			
			@Override
			protected void onPreExecute() {
				createDialog(R.string.connect);
			};

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					id = WebService.executeRequest("/gamec/connect", null);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if(result) {
					dialog.dismiss();					
					startDiscover();
				}
				else {
					dialog.dismiss();
					Toast.makeText(InternetGameActivity.this, 
							R.string.networkError, 3000).show();
					finish();
				}
			};
		}.execute(new Void[] { });
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(pending != null)
			pending.terminate();
		if(enemy != null)
			((InternetEnemy)enemy).close();
		try {
			if(id != null)
				WebService.executeRequest("/gamec/disconnect", 
					MapFactory.createMap(new String[] { "id", "app" }, new String[] { id, "0" }));
		} catch (Exception e) {	}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
				.setMessage(R.string.exitQuestion)
			    .setPositiveButton(R.string.yes, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface idialog, int which) {						
						if(pending != null)
							pending.terminate();
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
				MapFactory.createMap(new String[] { "id", "app" }, new String[] { id, "0" })));
			
			if(start) {
				enemy = new InternetEnemy(this);
				boolean begin = ((InternetEnemy)enemy).start(handler);

				if(begin) {
					handler.initialize(enemy, view, Players.X, Players.O, false);
					handler.setLastStep(new Point(-1, -1), Players.O);
				}
				else {
					handler.initialize(enemy, view, Players.O, Players.X, false);
					handler.setLastStep(new Point(-1, -1), Players.O);
				}
			}
			else {				
				createDialog(R.string.enemys);
				pending = new PendingThread("/gamec/getenemy", id, 1000, this);
				pending.start();
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
		
		if(begin) {
			handler.initialize(enemy, view, Players.X, Players.O, false);
			handler.setLastStep(new Point(-1, -1), Players.O);
		}
		else {
			handler.initialize(enemy, view, Players.O, Players.X, false);
			handler.setLastStep(new Point(-1, -1), Players.O);
		}
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
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				return null;
			}
			
			protected void onPostExecute(Void result) {
				startDiscover();
			}
		}.execute(new Void[] { });
	}
	
	public String getId() {
		return id;
	}
	
	private void createDialog(int text) {
		dialog = ProgressDialog.show(this, "", 
				getResources().getString(text), true);
		dialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface paramDialogInterface, int paramInt,
					KeyEvent paramKeyEvent) {
				if(paramInt == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
					if(pending != null)
						pending.terminate();
					finish();
					return true;
				}
				return false;
			}
		});
	}
}
