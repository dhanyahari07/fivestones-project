/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.five_stones_project;

import android.graphics.*;

import android.view.Display;
import android.content.Context;
import android.view.WindowManager;
import android.graphics.drawable.Drawable;

/**
 *
 * @author tungi
 */

public class BackgroundImage extends Drawable{
    private int padding_top;
    private int padding_left;
    private int background_color;

    private Bitmap hosted_drawable;

    public BackgroundImage(Context context, int resource,int background_color){
//        hosted_drawable=context.getResources().getDrawable(resource);
//        Rect r=hosted_drawable.getBounds();
        this.background_color=background_color;
        hosted_drawable=BitmapFactory.decodeResource(context.getResources(),resource);
        Display display=((WindowManager)context.
            getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        padding_left=(display.getWidth()-hosted_drawable.getWidth())/2;
        padding_top=(display.getHeight()-hosted_drawable.getHeight())/2;
//        padding_left=(display.getWidth()-hosted_drawable.getMinimumWidth())/2;
//        padding_top=(display.getHeight()-hosted_drawable.getMinimumHeight())/2;
    }

    public void draw(Canvas canvas) {
//                int w = hosted_drawable.getIntrinsicWidth();
//                int h = hosted_drawable.getIntrinsicHeight();
//                int view_w = view.getWidth();
//                int view_h = view.getHeight();
//                int padded_horizontal_room = view_w - (padding_left + padding_right);
//                int padded_vertical_room = view_h - (padding_top + padding_bottom);
//                float scale;
//                float intrinsic_aspect_ratio = w / (float) h;
//                float padded_canvas_aspect_ratio = padded_horizontal_room / (float) padded_vertical_room;
//                if (intrinsic_aspect_ratio > padded_canvas_aspect_ratio)
//                        // Our source image is wider than the canvas, so we scale by width.
//                        scale = padded_horizontal_room / (float) w;
//                else
//                        scale = padded_vertical_room / (float) h;
//                int scaled_width = (int) (scale*w);
//                int scaled_height = (int) (scale*h);
//                // Here we fit the image into the bottom-right corner.
//                int left = view_w - scaled_width - padding_right;
//                int top = view_h - scaled_height - padding_bottom;
//                int right = view_w - padding_right;
//                int bottom = view_h - padding_bottom;

//            hosted_drawable.setBounds(padding_left,padding_top,
//                    hosted_drawable.getIntrinsicWidth()+padding_left,
//                    hosted_drawable.getIntrinsicHeight()+padding_top);
//            hosted_drawable.draw(canvas);
            canvas.drawColor(background_color);
            canvas.drawBitmap(hosted_drawable,padding_left,padding_top,null);
    }

    public int getOpacity() {
        return 0;//hosted_drawable.getOpacity();
    }

    public void setAlpha(int alpha) {
        //hosted_drawable.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        //hosted_drawable.setColorFilter(cf);
    }

    public Bitmap getBitmap(){
        return hosted_drawable;
    }
}
