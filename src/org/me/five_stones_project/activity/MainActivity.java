package org.me.five_stones_project.activity;

import java.util.ArrayList;

import org.me.five_stones_project.LevelDialog;
import org.me.five_stones_project.R;
import org.me.five_stones_project.bluetooth.BluetoothListener;
import org.me.five_stones_project.common.BackgroundImage;
import org.me.five_stones_project.common.HighScore;
import org.me.five_stones_project.common.HighScoreHelper;
import org.me.five_stones_project.game.GameHandler;
import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.game.SavedGameHandler;
import org.me.five_stones_project.type.Descriptions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
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
	private final static String INITIALIZED = "initialized";
	
	private ViewFlipper flipper;
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GameOptions.initialize(this);
		        
        /*
         * first start
         */
        
        SharedPreferences sp = getSharedPreferences(ACTIVITY_SERVICE, MODE_PRIVATE);
        if(!sp.getBoolean(INITIALIZED, false))
        	firstStart();
        
        /*
         * initialize
         */
        getWindow().setBackgroundDrawable(new BackgroundImage(this,
                R.drawable.background, Color.BLACK));
        getWindow().setFormat(PixelFormat.RGB_565);
	}

    @Override
	protected void onResume() {
		super.onResume();

        setContentView(R.layout.main);
        
        createHighScoreView();
        flipper = (ViewFlipper) findViewById(R.main.flipper);
        
        if(SavedGameHandler.isSavedGame(this))
        	((Button) findViewById(R.main.btnContinue)).setVisibility(View.VISIBLE);
        
        BluetoothListener.startListening(this);
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
    	Intent intent = new Intent(this, SingleGameActivity.class);
    	startActivity(intent);
    }
    
    public void onClick(View view){
		lastAction = view.getId();
        if(view.getId() == R.main.btnContinue) {
        	Intent intent = new Intent(this, SingleGameActivity.class);
        	startActivity(intent);
        }
        else if(view.getId() == R.main.btnStart) {
        	if(SavedGameHandler.isSavedGame(this))
        		new AlertDialog.Builder(this)
			    	.setIcon(android.R.drawable.ic_dialog_alert)
			    	.setPositiveButton(R.string.yes, new OnClickListener() {
			    		
						@Override
						public void onClick(DialogInterface paramDialogInterface, int paramInt) {
							SavedGameHandler.clearSavedGame(MainActivity.this);
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
        else if(view.getId() == R.main.btnMulti) {
        	String[] items = new String[] {
        			getResources().getString(R.string.multiBT),
        			getResources().getString(R.string.multiTwoPlayer)
			};
        	new AlertDialog.Builder(this)
        		.setTitle(R.string.multiTitle)
        		.setItems(items, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent;
						switch(which) {
						case 0 :				        	
				        	intent = new Intent(MainActivity.this, 
				        			BluetoothServiceActivity.class);
				        	startActivity(intent);
							break;
						case 1 :
							intent = new Intent(MainActivity.this, 
				        			DualGameActivity.class);
				        	startActivity(intent);
							break;
						default: break;
						}
					}
				}).show();
        }
        else if(view.getId() == R.main.btnBack) {
        	flipper.showPrevious();
        }
    }
    
    private void createHighScoreView() {        
		ArrayList<HighScore> highScores = HighScoreHelper.loadHighScores(this);
		
		LinearLayout sc = (LinearLayout) findViewById(R.main.highScroll);
		
		for(HighScore highScore : highScores) {
			LinearLayout view = (LinearLayout) LayoutInflater
					.from(this).inflate(R.layout.highscore, null);
			
			TextView tv = (TextView) view.findViewById(R.highscore.level);
			tv.setText(highScore.getLevel().getDescription(this));
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
    
    private void firstStart() {
    	int size = 0;
    	
    	DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	switch(metrics.densityDpi) {
    	case DisplayMetrics.DENSITY_LOW : 
    		size = 75;
    		break;
    	case DisplayMetrics.DENSITY_MEDIUM :
    		size = 100;
    		break;
    	case DisplayMetrics.DENSITY_HIGH : 
    		size = 150;
    		break;
    	case DisplayMetrics.DENSITY_XHIGH :
    		size = 200;
    		break;
		default: break;
    	}

    	Bitmap b = null;
    	try {
    		b = Bitmap.createBitmap(size * GameHandler.MAX_BOARD_X, 
				size * GameHandler.MAX_BOARD_Y, Config.RGB_565);
    		GameOptions.getInstance().setCurrentQuality(Descriptions.High);
    	} catch(OutOfMemoryError err) {
    		GameOptions.getInstance().setCurrentQuality(Descriptions.Low);
    	}
    	
    	if(b != null)
    		b.recycle();
    	System.gc();

    	GameOptions.getInstance().commit(this);
    	
		SharedPreferences.Editor editor = 
			getSharedPreferences(ACTIVITY_SERVICE, MODE_PRIVATE).edit();
		editor.putBoolean(INITIALIZED, true);
		editor.commit();
    }
}