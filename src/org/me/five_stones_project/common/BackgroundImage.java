package org.me.five_stones_project.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;

/**
 * 
 * @author Tangl Andras
 */

public class BackgroundImage extends Drawable {
	private int padding_top;
	private int padding_left;
	private int backgroundColor;

	private Bitmap hosted_drawable;

	public BackgroundImage(Context context, int resource, int backgroundColor) {
		this.backgroundColor = backgroundColor;
		hosted_drawable = BitmapFactory.decodeResource(context.getResources(),
				resource);
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		padding_left = (display.getWidth() - hosted_drawable.getWidth()) / 2;
		padding_top = (display.getHeight() - hosted_drawable.getHeight()) / 2;
	}

	public void draw(Canvas canvas) {
		canvas.drawColor(backgroundColor);
		canvas.drawBitmap(hosted_drawable, padding_left, padding_top, null);
	}

	public int getOpacity() {
		return 0;
	}

	public void setAlpha(int alpha) {
	}

	public void setColorFilter(ColorFilter cf) {
	}

	public Bitmap getBitmap() {
		return hosted_drawable;
	}
}