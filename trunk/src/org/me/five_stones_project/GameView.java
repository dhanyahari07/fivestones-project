package org.me.five_stones_project;

import android.view.*;
import android.content.*;
import android.graphics.*;
import android.view.View.*;
import android.widget.AdapterView.*;

import android.os.Handler;
import java.util.ArrayList;
import android.app.AlertDialog;

/**
 *
 * @author Tangl
 */

class GameView  extends View 
        implements OnTouchListener,DialogInterface.OnClickListener{

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////VARIABLES///////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

    private int cellsize=40;//the default cellsize is 40 pixels
    private String style="hu";//the default style is hungarian
    private int sens=400;//the default sensitivity

    private myTimer timer;
    private Handler handler;
    private Display display;

    private AI ai;
    private Matrix matrix;
    private MainActivity main;
    private Android_Menu android_menu;
    private AndroidMenuDialog menuDialog;

    private int[][] board_array;//stores the values of the board
    private long current_time=-1;
    private boolean android=false;
    private Bitmap board,background;
    private VelocityTracker velocitytracker;
    private Coordinate old,size,delta,menu,last_step;
    private ArrayList<Coordinate> a_last_step,h_last_step;//these stores the previous steps
    private boolean zoom_out=false,moving=false,zoom_in=true;
    
    private int[] cell,cell_x,cell_o;

///////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////CONSTRUCTOR///////////////////////////////
///////////////////////////////////////////////////////////////////////////////

    public GameView(MainActivity context){
        super(context);timer=new myTimer();handler=new Handler();
        //initialize variables
        setOnTouchListener(this);
        main=context;
        matrix=new Matrix();
        ai=new AI(matrix,main);
        display=
            ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay();
        menuDialog=new AndroidMenuDialog(context);
        background=BitmapFactory.
                decodeResource(getResources(),R.drawable.background);
        a_last_step=new ArrayList<Coordinate>();
        h_last_step=new ArrayList<Coordinate>();
        //open used bitmaps
        openBitmaps();
    }

///////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////OVERRIDE METHODS//////////////////////////
///////////////////////////////////////////////////////////////////////////////

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int width=board.getWidth();
        int height=board.getHeight();
        //update the delta value, that not ot hang out the screen
        if(width+delta.getX()<display.getWidth())
            delta.setXY(display.getWidth()-width,delta.getY());
        if(delta.getX()>0)
            delta.setXY(0,delta.getY());
        if(height+delta.getY()<display.getHeight())
            delta.setXY(delta.getX(),display.getHeight()-height);
        if(delta.getY()>0)
            delta.setXY(delta.getX(),0);

        int _delta_x=0,_delta_y=0;
        float ratio=0;
        Rect src,dst;
        RadialGradient shader=null;
        int[] colors=new int[]{0x00000000,0x40000000};
        float[] positions=new float[]{0,1};

        //define three importand values, the destination rectangle, where to draw,
        //this is the screen, the source rectangle from the board picture,
        //so which part of the board should be diplayed, and a new gradient, that
        //the last step would be shinier than the neighbour environment
        //these values are different, if zoom out or not
        if(!zoom_out){
            src=new Rect(0-delta.getX(),0-delta.getY(),
                    display.getWidth()-delta.getX(),
                    display.getHeight()-delta.getY());
            dst=new Rect(0,0,display.getWidth(),display.getHeight());
            canvas.drawBitmap(board,src,dst,null);
            shader=new RadialGradient(
                last_step.getX()*cellsize+delta.getX()+cellsize/2,
                last_step.getY()*cellsize+delta.getY()+cellsize/2,
                cellsize*2,colors,positions,Shader.TileMode.CLAMP);
        }
        //in zoom out mode it is important, that which dimension is greater than
        //the screen size, the width or the height
        else{
            src=new Rect(0,0,width,height);
            if(display.getWidth()*board.getHeight()/board.getWidth()
                    >display.getHeight()){
                ratio=(float)display.getHeight()/board.getHeight();
                _delta_x=(display.getWidth()-(int)(board.getWidth()*ratio))/2;
                dst=new Rect(_delta_x,0,display.getWidth()-_delta_x,display.getHeight());
                shader=new RadialGradient(
                        (last_step.getX()*cellsize+cellsize/2)*ratio+_delta_x,
                        (last_step.getY()*cellsize+cellsize/2)*ratio,
                        cellsize,colors,positions,Shader.TileMode.CLAMP);
            }
            else{
                ratio=(float)display.getWidth()/board.getWidth();
                _delta_y=(display.getHeight()-(int)(board.getHeight()*ratio))/2;
                dst=new Rect(0,_delta_y,display.getWidth(),display.getHeight()-_delta_y);
                shader=new RadialGradient(
                        (last_step.getX()*cellsize+cellsize/2)*ratio,
                        (last_step.getY()*cellsize+cellsize/2)*ratio+_delta_y,
                        cellsize,colors,positions,Shader.TileMode.CLAMP);
            }
            canvas.drawBitmap(background,
                    new Rect(0,0,background.getWidth(),background.getHeight()),
                    new Rect((display.getWidth()-background.getWidth())/2,
                             (display.getHeight()-background.getHeight())/2,
                             background.getWidth(),background.getHeight()),null);
            canvas.drawBitmap(board,src,dst,null);
        }
        //draw the gardient with the specified paint below
        RectF rect=new RectF(0,0,display.getWidth(),display.getHeight());              
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setShader(shader);
        canvas.drawRect(rect,paint);
        //if the game is ended, draw a line, which bound the five signs
        //the line color is red, if x (human) win, else the color is blue
        if(!main.isGame()){
            Paint line=new Paint();
            if(matrix.getType()==gamertypes.x.ordinal())
                line.setColor(Color.RED);
            else if(matrix.getType()==gamertypes.o.ordinal())
                line.setColor(Color.BLUE);
            if(!zoom_out){
                line.setStrokeWidth(4);
                canvas.drawLine(matrix.getFirst().getX()*cellsize+delta.getX()+cellsize/2,
                        matrix.getFirst().getY()*cellsize+delta.getY()+cellsize/2,
                        matrix.getLast().getX()*cellsize+delta.getX()+cellsize/2,
                        matrix.getLast().getY()*cellsize+delta.getY()+cellsize/2,
                        line);
            }
            else{
                line.setStrokeWidth(2);
                canvas.drawLine((matrix.getFirst().getX()*cellsize+cellsize/2)*ratio+_delta_x,
                        (matrix.getFirst().getY()*cellsize+cellsize/2)*ratio+_delta_y,
                        (matrix.getLast().getX()*cellsize+cellsize/2)*ratio+_delta_x,
                        (matrix.getLast().getY()*cellsize+cellsize/2)*ratio+_delta_y,
                        line);
            }
        }
    }

//////////////////////////////////////////////////////////////////////////////
////////////////////////////INITIALIZATION METHODS////////////////////////////
//////////////////////////////////////////////////////////////////////////////

    public void openBitmaps(){
        //open cell, x, o bitmaps and store the pixels int the two dimensional array
        //the opened bitmaps hang on the style set in the options menu
        Bitmap bitmap;
        cell=new int[(int)Math.pow(cellsize,2)];
        if(style.equals("hu"))
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.cell_hu);
        else if(style.equals("gomoku"))
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.cell_gomoku);
        else
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.cell_modern);
        bitmap=Bitmap.createScaledBitmap(bitmap,cellsize,cellsize,true);
        bitmap.getPixels(cell,0,cellsize,0,0,cellsize,cellsize);
        ///////////////////////////////////////////////////////////////////////
        cell_x=new int[(int)Math.pow(cellsize,2)];
        if(style.equals("hu"))
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.x_hu);
        else if(style.equals("gomoku"))
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.x_gomoku);
        else
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.x_modern);
        bitmap=Bitmap.createScaledBitmap(bitmap,cellsize,cellsize,true);
        bitmap.getPixels(cell_x,0,cellsize,0,0,cellsize,cellsize);
        ///////////////////////////////////////////////////////////////////////
        cell_o=new int[(int)Math.pow(cellsize,2)];
        if(style.equals("hu"))
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.o_hu);
        else if(style.equals("gomoku"))
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.o_gomoku);
        else
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.o_modern);
        bitmap=Bitmap.createScaledBitmap(bitmap,cellsize,cellsize,true);
        bitmap.getPixels(cell_o,0,cellsize,0,0,cellsize,cellsize);
    }

    public void createBoard(){
        //set everything to default, this method called when a new game started
        old=new Coordinate();size=new Coordinate();
        delta=new Coordinate();
        last_step=new Coordinate(display.getWidth()/2,display.getHeight()/2);
        zoom_out=false;
        zoom_in=true;
        moving=false;
        android=false;

        a_last_step.clear();
        h_last_step.clear();

        if(android_menu!=null)
            android_menu.setVisibility(INVISIBLE);
        
        int width=display.getWidth()/cellsize+1;
        int height=display.getHeight()/cellsize+1;
        size.setXY(width,height);
        board=Bitmap.createBitmap(width*cellsize,height*cellsize,
                Bitmap.Config.ARGB_8888);
        //set the board array to default, so set every part to zero
        board_array=new int[width][height];
        for(int i=0;i<height;++i)
            for(int j=0;j<width;++j){
                board_array[j][i]=gamertypes.none.ordinal();
                board.setPixels(cell,0,cellsize,j*cellsize,
                        i*cellsize,cellsize,cellsize);
            }
        ai.update(null,-1,-1);//see ai class
    }

    public void drawBoard(){
        //create a board picture
        for(int i=0;i<board_array.length;++i)
            for(int j=0;j<board_array[0].length;++j){
                if(board_array[i][j]==gamertypes.none.ordinal())
                    board.setPixels(cell,0,cellsize,i*cellsize,
                            j*cellsize,cellsize,cellsize);
                if(board_array[i][j]==gamertypes.x.ordinal())
                    board.setPixels(cell_x,0,cellsize,i*cellsize,
                            j*cellsize,cellsize,cellsize);
                if(board_array[i][j]==gamertypes.o.ordinal())
                    board.setPixels(cell_o,0,cellsize,i*cellsize,
                            j*cellsize,cellsize,cellsize);
            }
    }

//////////////////////////////////////////////////////////////////////////////
///////////////////////////////////LISTENERS//////////////////////////////////
//////////////////////////////////////////////////////////////////////////////

    public boolean onTouch(View view, MotionEvent event){
        float velocity=0;
        //if zoom out, and the user click on the android, the menu will be show
        if(event.getX()>(display.getWidth()-menu.getX()) &&
                event.getY()>(display.getHeight()-menu.getY())
                && android_menu.getVisibility()==VISIBLE && zoom_out){
            menuDialog.show();
        }
        else if(event.getAction()==MotionEvent.ACTION_DOWN && main.isGame()){
            if((System.currentTimeMillis()-current_time)<370){
                //if click twice quickly on the screen, zoom out
                main.pauseGame();
                zoom_out=true;
                zoom_in=false;
                android_menu.zoomOut();
                handler.removeCallbacks(timer);
            }
            else if(!zoom_out){
                //on every touch save the current time, and use it to decide,
                //if the user want to zoom out
                current_time=System.currentTimeMillis();
            }
            else if(zoom_out){
                //when zoom out is true, and the user touch once more the screen
                //after this zoom in
                main.continueGame();
                zoom_in=true;
                delta.setXY((int)(display.getWidth()/2-
                        event.getX()*board.getWidth()/display.getWidth()),
                    (int)(display.getHeight()/2-
                        event.getY()*board.getHeight()/display.getHeight()));

            }
            //start the velocity tracker, this can use to go to the next or
            //previous view with drag the finger from right to left or backward fast.
            velocitytracker=VelocityTracker.obtain();
            velocitytracker.addMovement(event);
            //update old coordinate for the drag the board
            old.setXY((int)event.getX()-delta.getX(),
                    (int)event.getY()-delta.getY());
        }
        else if(event.getAction()==MotionEvent.ACTION_MOVE && main.isGame()){
            //update velocity
            if((System.currentTimeMillis()-current_time)>sens){
                moving=true;
                velocitytracker.addMovement(event);
                int current_x=(int)event.getX();
                int current_y=(int)event.getY();
                //update delta
                delta.setXY(current_x-old.getX(),current_y-old.getY());
                //redraw
                invalidate();
            }
        }
        else if(event.getAction()==MotionEvent.ACTION_UP && main.isGame()){                
            invalidate();
            //finish collect velocity, compute, and decide things to do
            velocitytracker.addMovement(event);
            velocitytracker.computeCurrentVelocity(1000);
            velocity=velocitytracker.getXVelocity();
            velocitytracker.recycle();
            if(velocity>1200){
                if(!zoom_out)
                    main.pauseGame();
                MainActivity.Flipping(2);
                zoomOut();
            }
            else if(velocity<-1200){
                if(!zoom_out)
                    main.pauseGame();
                MainActivity.Flipping(1);
                zoomOut();
            }
            //if the conditions are true that means a new step
            else if(!moving && !zoom_out && zoom_in && !android){
                timer.set(event.getX()-delta.getX(),event.getY()-delta.getY(),
                        gamertypes.x.ordinal());
                handler.postDelayed(timer,250);//it have to, because a first touch
                //not means, that a user want to step, maybe he want to zoom out.
            }
            moving=false;
            if(zoom_in){//to zoom in
                zoom_out=false;
                android_menu.zoomIn();
            }
        }
        return true;
    }

    public void onClick(DialogInterface arg0,int arg1){
        //on new game question
        if(arg1==arg0.BUTTON_POSITIVE){//start a new game
            main.newGame(true);
            createBoard();
            invalidate();
        }
        else if(arg1==arg0.BUTTON_NEGATIVE){
            zoomOut();
        }
    }

//////////////////////////////////////////////////////////////////////////////
///////////////////////////////////PUBLIC METHODS/////////////////////////////
//////////////////////////////////////////////////////////////////////////////

    public void androidFirstStep(){
        //when android start, it make a step on the middle an the board
        android=true;
        updateBoard(size.getX()*cellsize/2,
                size.getY()*cellsize/2,gamertypes.o.ordinal());
    }

    public void setAndroidMenu(Android_Menu menu){
        //for MainActivity class
        android_menu=menu;
        this.menu=android_menu.getDimension();
    }

    public void updateBoard(float x,float y,int type){
        //place a new step
        int _x=(int)x/cellsize;
        int _y=(int)y/cellsize;

        if(board_array[_x][_y]!=gamertypes.none.ordinal()){
            android=false;
            return;
        }
        //this have to the increment the board
        int inc_x=0,inc_y=0;
        if((_x-1)<=0)
            inc_x=-1;
        if((_x+1)>=(size.getX()-1))
            inc_x=1;
        if((_y-1)<=0)
            inc_y=-1;
        if((_y+1)>=(size.getY()-1))
            inc_y=1;
        if(inc_x!=0 || inc_y!=0)
            incrementBoard(inc_x,inc_y);//------------

        int _inc_x=inc_x==-1?1:0;
        int _inc_y=inc_y==-1?1:0;
        //place a new step, that called this method
        board_array[_x+_inc_x][_y+_inc_y]=type;
        if(type==gamertypes.x.ordinal())
            board.setPixels(cell_x,0,cellsize,(_x+_inc_x)*cellsize,
                    (_y+_inc_y)*cellsize,cellsize,cellsize);
        else if(type==gamertypes.o.ordinal())
            board.setPixels(cell_o,0,cellsize,(_x+_inc_x)*cellsize,
                    (_y+_inc_y)*cellsize,cellsize,cellsize);

        last_step.setXY(_x+_inc_x,_y+_inc_y);//set the coordinates of the last step
        ai.update(last_step,_inc_x,_inc_y);//see ai class

        //the game stores only ten steps backward of each gamers
        if(type==gamertypes.x.ordinal()){
            h_last_step.add(new Coordinate(_x+_inc_x,_y+_inc_y));
            if(h_last_step.size()>10)
                h_last_step.remove(0);
        }
        else{
            a_last_step.add(new Coordinate(_x+_inc_x,_y+_inc_y));
            if(a_last_step.size()>10)
                a_last_step.remove(0);
        }
        //update delta, that the the new step will be in the centre of the board
        delta.setXY(display.getWidth()/2-last_step.getX()*cellsize,
                display.getHeight()/2-last_step.getY()*cellsize);
        invalidate();//redraw
        //check, if after the new step the gamer win
        if(matrix.searchFives(board_array,last_step,type)){
            if(type==gamertypes.x.ordinal())
                new AlertDialog.Builder(getContext())
                .setView(main.getAlertDialogView("Congratulations, you win!"+'\n'+"New Game?"))
                .setPositiveButton("Yes",(DialogInterface.OnClickListener)this)
                .setNegativeButton("No",(DialogInterface.OnClickListener)this)
                .show();
            else
                new AlertDialog.Builder(getContext())
                .setView(main.getAlertDialogView("Android win."+'\n'+"New Game?"))
                .setPositiveButton("Yes",(DialogInterface.OnClickListener)this)
                .setNegativeButton("No",(DialogInterface.OnClickListener)this)
                .show();
            main.gameOver(type);
        }
        //after a small delay android make its step after human 
        else if(type==gamertypes.x.ordinal()){
            handler.postDelayed(new AndroidStep(),500);
        }
        //android end its move
        else if(type==gamertypes.o.ordinal()){
            android=false;
        }
    }

    public void incrementBoard(int inc_x,int inc_y){
        //increment board, inc_x strore the increment of the x coordinate
        //inc_y stores the y coordinate
        int width=size.getX(),height=size.getY(),delta_x=0,delta_y=0;
        if(inc_x!=0)
            width=size.getX()+1;
        if(inc_y!=0)
            height=size.getY()+1;
        size.setXY(width,height);

        int[][] _board_array=new int[width][height];
        board=Bitmap.createBitmap(
                width*cellsize,height*cellsize,Bitmap.Config.ARGB_8888);

        if(inc_x==-1){
            for(int i=0;i<height;++i){
                _board_array[0][i]=gamertypes.none.ordinal();
            }
            delta_x=1;
            delta.setXY(delta.getX()-cellsize,delta.getY());
        }
        else if(inc_x==1)
            for(int i=0;i<height;++i){
                _board_array[width-1][i]=gamertypes.none.ordinal();
            }
        if(inc_y==-1){
            for(int i=0;i<width;++i){
                _board_array[i][0]=gamertypes.none.ordinal();
            }
            delta_y=1;
            delta.setXY(delta.getX(), delta.getY()-cellsize);
        }
        else if(inc_y==1)
            for(int i=0;i<width;++i){
                _board_array[i][height-1]=gamertypes.none.ordinal();
            }
        //create a new board, picture, and copy the values
        for(int i=delta_x;i<width-1+delta_x;++i)
            for(int j=delta_y;j<height-1+delta_y;++j){
                _board_array[i][j]=board_array[i-delta_x][j-delta_y];
            }
        board_array=_board_array;

        drawBoard();

        //update the two lists coordinates
        if(inc_x==-1 || inc_y==-1){
            int _inc_x=inc_x==-1?1:0;
            int _inc_y=inc_y==-1?1:0;
            for(int i=0;i<a_last_step.size();++i){
                Coordinate coord=a_last_step.get(i);
                a_last_step.set(i,new Coordinate(coord.getX()+_inc_x,coord.getY()+_inc_y));
            }
            for(int i=0;i<h_last_step.size();++i){
                Coordinate coord=h_last_step.get(i);
                h_last_step.set(i,new Coordinate(coord.getX()+_inc_x,coord.getY()+_inc_y));
            }
        }
    }

    public void zoomOut(){
        zoom_out=true;
        zoom_in=false;
        android_menu.setVisibility(VISIBLE);
    }

    public boolean isZoomOut(){
        return zoom_out;
    }

    public void Undo(){
        //make a step back
        if(!android && h_last_step.size()>1 && a_last_step.size()>1){
            //remove the last coordinates from the two list, the board_array,
            //and call a redraw
            Coordinate coord=a_last_step.get(a_last_step.size()-1);
            board_array[coord.getX()][coord.getY()]=0;
            board.setPixels(cell,0,cellsize,coord.getX()*cellsize,
                    coord.getY()*cellsize,cellsize,cellsize);
            a_last_step.remove(coord);
            coord=h_last_step.get(h_last_step.size()-1);
            board_array[coord.getX()][coord.getY()]=0;
            board.setPixels(cell,0,cellsize,coord.getX()*cellsize,
                    coord.getY()*cellsize,cellsize,cellsize);
            h_last_step.remove(coord);
            last_step.setXY(a_last_step.get(a_last_step.size()-1).getX(),
                    a_last_step.get(a_last_step.size()-1).getY());

             delta.setXY(display.getWidth()/2-last_step.getX()*cellsize,
                display.getHeight()/2-last_step.getY()*cellsize);
            invalidate();
        }
    }

    public void resizeBoard(int newSize,String newStyle,int newSens){
        sens=newSens*100+100;
        //this calls by after the options finishes
        if(!newStyle.equals(style) && (newSize==cellsize || newSize<20)){
            style=newStyle;
            openBitmaps();
            drawBoard();
            invalidate();
        }
        if(newSize!=cellsize && newSize>=20){
            style=newStyle;
            int width=0,height=0;
            if(size.getX()*newSize<display.getWidth()){
                width=display.getWidth()/newSize+1;
            }
            if(size.getY()*newSize<display.getHeight()){
                height=display.getHeight()/newSize+1;
            }
            //if new size smaller than the old size, and would not fill the
            //entery screen after the resize, so we must increment a board
            if(width!=0 || height!=0){
                if(width==0) width=size.getX();
                if(height==0) height=size.getY();

                int[][] _board=new int[board_array.length][board_array[0].length];
                for(int i=0;i<board_array.length;++i)
                    for(int j=0;j<board_array[0].length;++j)
                        _board[i][j]=board_array[i][j];

                int _delta_x=(width-size.getX())/2;
                int _delta_y=(height-size.getY())/2;
                size.setXY(width,height);
                //we copy the old part to the middle on the new board
                board_array=new int[width][height];
                for(int i=0;i<_board.length;++i)
                    for(int j=0;j<_board[0].length;++j)
                        board_array[_delta_x+i][_delta_y+j]=_board[i][j];
                last_step.setXY(last_step.getX()+_delta_x,last_step.getY()+_delta_y);
                if(!main.isGame())
                    matrix.update(_delta_x,_delta_y);
            }
            
            cellsize=newSize;

            board=Bitmap.createBitmap(board_array.length*cellsize,
                    board_array[0].length*cellsize,
                Bitmap.Config.ARGB_8888);
            openBitmaps();
            drawBoard();
            //update delta
            delta.setXY(board.getWidth()/2-last_step.getX()*cellsize,
                board.getHeight()/2-last_step.getY()*cellsize);
            //redraw
            invalidate();
        }
    }

//////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////INNER CLASS///////////////////////////
//////////////////////////////////////////////////////////////////////////////

//this must be to the delayed method calls

    protected class AndroidStep implements Runnable{
        @Override
        public void run(){
            Coordinate c=ai.nextStep(board_array,last_step);
            updateBoard(c.getX()*cellsize,c.getY()*cellsize,gamertypes.o.ordinal());
        }
    }

    protected class myTimer implements Runnable{
        private float x,y;
        private int type;

        public void set(float x,float y,int type){
            this.x=x;
            this.y=y;
            this.type=type;
        }

        @Override
        public void run(){
            android=true;
            updateBoard(x,y,type);
        }
        
    }
}
