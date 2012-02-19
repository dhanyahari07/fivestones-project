package org.me.five_stones_project.activity;


import org.me.five_stones_project.AndroidMenu;
import org.me.five_stones_project.IEnemy;
import org.me.five_stones_project.common.BackgroundImage;
import org.me.five_stones_project.game.GameHandler;
import org.me.five_stones_project.game.GameView;

import org.me.five_stones_project.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 *
 * @author Tangl Andras
 */

public class GameActivity extends BaseActivity {
	private static GameActivity instance;
	public static GameActivity getInstance() {
		return instance;
	}
	
	protected IEnemy enemy;
	protected GameView view;
	protected AndroidMenu menu;
	protected GameHandler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		instance = this;
		
		getWindow().setBackgroundDrawable(new BackgroundImage(this,
                R.drawable.background, Color.BLACK));
        getWindow().setFormat(PixelFormat.RGBA_8888);
        
        handler = new GameHandler();
        menu = new AndroidMenu(this);
        view = new GameView(this, handler, menu);
        
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.menu);
        RelativeLayout.LayoutParams params = new LayoutParams(bmp.getWidth(), bmp.getHeight());
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        
        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(view);
        layout.addView(menu, params);
                
        setContentView(layout);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		view.releaseBoard();
		System.gc();
	}

	public GameView getView() {
		return view;
	}

	public GameHandler getHandler() {
		return handler;
	}	
	
	public void reinitilize() {	}
}
