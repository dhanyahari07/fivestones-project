package org.me.five_stones_project;

import android.content.*;
import android.graphics.*;
import android.view.*;
import android.view.animation.*;

/**
 *
 * @author Tangl
 */

public class Android_Menu extends View{
        private Bitmap menu;
        private Animation anim;
        private Display display;

        public Android_Menu(Context context){
            super(context);
            menu=BitmapFactory.
                decodeResource(getResources(),R.drawable.menu);
            setVisibility(INVISIBLE);
            
            display=
            ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay();
            
            createAnimation();
        }

        @Override
        protected void onDraw(Canvas canvas){
            canvas.drawBitmap(menu,display.getWidth()-50,
                    display.getHeight()-49,null);
        }

        public void createAnimation(){
            anim=new TranslateAnimation(
                        TranslateAnimation.ABSOLUTE,menu.getWidth(),
                        TranslateAnimation.ABSOLUTE,0,
                        TranslateAnimation.ABSOLUTE,menu.getHeight(),
                        TranslateAnimation.ABSOLUTE,0);
            anim.setDuration(500);
            anim.setStartOffset(200);
            anim.setInterpolator(new LinearInterpolator());
        }

        public void zoomOut(){
            setVisibility(VISIBLE);
            startAnimation(anim);
        }

        public void zoomIn(){
            setVisibility(INVISIBLE);
        }

        public Coordinate getDimension(){
            return new Coordinate(menu.getWidth(),menu.getHeight());
        }
    }
