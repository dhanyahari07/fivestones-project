package org.me.five_stones_project.activity;

import java.util.ArrayList;

import org.me.five_stones_project.LevelDialog;
import org.me.five_stones_project.R;
import org.me.five_stones_project.bluetooth.BluetoothListener;
import org.me.five_stones_project.common.BackgroundImage;
import org.me.five_stones_project.common.HighScore;
import org.me.five_stones_project.common.HighScoreHelper;
import org.me.five_stones_project.game.SavedGameHandler;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.Button;


/**
 *
 * @author Tangl Andras
 */

public class MainActivity extends BaseActivity {
	private static MainActivity myContext;
	public static MainActivity getContext() {
		return myContext;
	}
	
	private static PendingIntent pIntent;
	public static void restartApplication() {
		AlarmManager mgr = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, pIntent);
		System.exit(2);
	}
	
	// main code
	public int lastAction;
	private ViewFlipper flipper;
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myContext = this;  
        pIntent = PendingIntent.getActivity(getBaseContext(), 0,
                new Intent(getIntent()), getIntent().getFlags());        
        
        /*
         * initialize
         */
        getWindow().setBackgroundDrawable(new BackgroundImage(this,
                R.drawable.background, Color.BLACK));
        getWindow().setFormat(PixelFormat.RGBA_8888);    
	}

    @Override
	protected void onResume() {
		super.onResume();

        setContentView(R.layout.main);
        
        createHighScoreView();
        flipper = (ViewFlipper) findViewById(R.main.flipper);
        
        if(SavedGameHandler.isSavedGame())
        	((Button) findViewById(R.main.btnContinue)).setVisibility(View.VISIBLE);
        
        BluetoothListener.startListening();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	BluetoothListener.stopListening();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK && flipper.getDisplayedChild() == 1) {
    		flipper.showPrevious();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    private void startNewGame() {
    	Intent intent = new Intent(this, SimpleGameActivity.class);
    	startActivity(intent);
    }
    
    public void onClick(View view){
		lastAction = view.getId();
        if(view.getId() == R.main.btnContinue) {
        	Intent intent = new Intent(this, SimpleGameActivity.class);
        	startActivity(intent);
        }
        else if(view.getId() == R.main.btnStart) {
        	if(SavedGameHandler.isSavedGame())
        		new AlertDialog.Builder(this)
			    	.setIcon(android.R.drawable.ic_dialog_alert)
			    	.setPositiveButton(R.string.yes, new OnClickListener() {
			    		
						@Override
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							SavedGameHandler.clearSavedGame();
							startNewGame();
						}
					})
					.setNegativeButton(R.string.no, null)
					.setMessage(R.string.existedSavedGameMsg)
					.setTitle(R.string.existedSavedGameTitle).show();
        	else 
        		startNewGame();
        }
        else if(view.getId() == R.main.btnLevel) {
            new LevelDialog(this).show();
        }
        else if(view.getId() == R.main.btnHighscore) {
            flipper.showNext();
        }
        else if(view.getId() == R.main.btnQuit) {
            finish();
        }
        else if(view.getId() == R.main.btnOptions) {
        	Intent intent = new Intent(this, OptionsActivity.class);
        	startActivity(intent);
        }
        else if(view.getId() == R.main.btnBluetooth) {
        	Intent intent = new Intent(this, BluetoothServiceActivity.class);
        	startActivity(intent);
        }
        else if(view.getId() == R.main.btnBack) {
        	flipper.showPrevious();
        }
    }
    
    private void createHighScoreView() {        
		ArrayList<HighScore> highScores = HighScoreHelper.loadHighScores();
		
		LinearLayout sc = (LinearLayout) findViewById(R.main.highScroll);
		
		for(HighScore highScore : highScores) {
			LinearLayout view = (LinearLayout) LayoutInflater
					.from(this).inflate(R.layout.highscore, null);
			
			TextView tv = (TextView) view.findViewById(R.highscore.level);
			tv.setText(highScore.getLevel().getDescription());
			tv = (TextView) view.findViewById(R.highscore.wins);
			tv.setText(Integer.toString(highScore.getWins()));
			tv = (TextView) view.findViewById(R.highscore.loses);
			tv.setText(Integer.toString(highScore.getLoses()));
			tv = (TextView) view.findViewById(R.highscore.best);
			tv.setText(highScore.getFormattedElapseTime());
			tv = (TextView) view.findViewById(R.highscore.date);
			tv.setText(highScore.getFormattedDate());
			sc.addView(view);
		}
    }
}