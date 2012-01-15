package org.me.five_stones_project;

import org.me.five_stones_project.activity.GameActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * 
 * @author Tangl Andras
 */

public class AndroidMenu extends View {
	private Bitmap menu;
	private Animation anim;
	private MenuDialog menuDialog;

	public AndroidMenu(Context context) {
		super(context);
		
		menuDialog = new MenuDialog(context);
		menu = BitmapFactory.decodeResource(getResources(), R.drawable.menu);
		createAnimation();		
		setVisibility(INVISIBLE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(menu, 0, 0, null);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			if(!menuDialog.isShowing()) {				
				GameActivity.getInstance().getHandler().pauseGame();
				menuDialog.show();
			}
		}
		return true;
	}

	public void createAnimation() {
		anim = new TranslateAnimation(TranslateAnimation.ABSOLUTE,
				menu.getWidth(), TranslateAnimation.ABSOLUTE, 0,
				TranslateAnimation.ABSOLUTE, menu.getHeight(),
				TranslateAnimation.ABSOLUTE, 0);
		anim.setDuration(500);
		anim.setStartOffset(200);
		anim.setInterpolator(new LinearInterpolator());
	}

	public void show() {
		if(getVisibility() == INVISIBLE) {
			setVisibility(VISIBLE);
			startAnimation(anim);
		}
	}

	public void hide() {
		if(getVisibility() == VISIBLE)
			setVisibility(INVISIBLE);
	}
}
