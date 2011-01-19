package org.me.five_stones_project;

import java.io.*;
import java.text.*;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.telephony.*;
import android.view.View.*;
import android.view.animation.*;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.PixelFormat;

/**
 *
 * @author Tangl Andras
 */

enum gamertypes{none,x,o};//gamer types enum for the readable code

public class MainActivity extends Activity 
        implements OnClickListener,
        Animation.AnimationListener,DialogInterface.OnClickListener{

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////VARIABLES///////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

    private GameView game;
    private Display display;
    private Android_Menu menu;
    private LevelDialog dialog;

    private long start_time,elapsed_time;
    private boolean retreat_question=false;
    private boolean isGame=false,options=false;

    private static ViewFlipper flipper;
    private static Animation animLeftIn,animRightIn,
                             animLeftOut,animRightOut,
                             animAlphaIn,animAlphaOut;

///////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////OVERRIDE METHODS//////////////////////////
///////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstance){try{
        super.onCreate(savedInstance);
        //initialization part
        dialog=new LevelDialog(this);
        //set background picture
        getWindow().setBackgroundDrawable(new BackgroundImage(this,
                    R.drawable.background,Color.BLACK));
        getWindow().setFormat(PixelFormat.RGBA_8888);
        display=((WindowManager)getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay();

        createAnimations();
        //create game view
        FrameLayout frame=new FrameLayout(this);
        frame.addView(game=new GameView(this));
        frame.addView(menu=new Android_Menu(this));

        game.setAndroidMenu(menu);
        //make flipper as a content view of this activity
        setContentView(R.layout.main);

        flipper=(ViewFlipper)findViewById(R.main.flipper);
        //add game view, highscore and status view to a flipper
        flipper.addView(frame);
        flipper.addView(LayoutInflater.from(this).inflate(R.layout.status,null));
        flipper.addView(LayoutInflater.from(this).inflate(R.layout.highscores,null));
        //implement a broadcastReceiver interface, to receive any changes in the
        //battery status, and display it in the status window
        BroadcastReceiver broadrec=new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0,Intent intent) {
                int level = intent.getIntExtra("level",0);
                ProgressBar battery=(ProgressBar)findViewById(R.status.battery);
                battery.setProgress(level);}};
        registerReceiver(broadrec,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //like the previous implement an interface to receive changes int the
        //antenna signal strength. In the display section I use a log scale, so
        //the status line increment with the same unit.
        TelephonyManager telephone=(TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        telephone.listen(new PhoneStateListener(){
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength){
                 super.onSignalStrengthsChanged(signalStrength);
                 double strength=signalStrength.getGsmSignalStrength();
                 if(strength==99)
                     strength=0;
                 else if(strength==1)
                     strength=5;
                 else
                     strength=Math.log(strength)*20/Math.log(2)+5;
                 ProgressBar signal=(ProgressBar)findViewById(R.status.signal);
                 signal.setProgress((int)strength);
            }},PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);}catch(Exception e){}
    }

    @Override
    public void onResume(){
        super.onResume();
        //On resume get shared values named "cellsize" and "style", which come
        //from the options menu
        if(options){
            SharedPreferences sp=getSharedPreferences(ACTIVITY_SERVICE,MODE_WORLD_READABLE);
            int cell=sp.getInt("cellsize",-1);
            String style=sp.getString("style","hu");
            int sens=sp.getInt("sens",2);
            game.resizeBoard(cell*5+20,style,sens+1);
            options=false;
        }
    }

    @Override
    public void onPause(){
        //on pause if there is an unfinished game, paused it, saved the current
        //elapsed time, zoom out the game view and clear the animations of the flipper
        if(isGame)
            pauseGame();
        flipper.clearAnimation();
        game.zoomOut();
        super.onPause();
    }

    @Override
    public void onDestroy(){
        //on destroy make everything to the default status
        flipper.clearAnimation();
        flipper.setDisplayedChild(0);
        Button cont=(Button)findViewById(R.main.continueButton);
        cont.setVisibility(View.INVISIBLE);
        isGame=false;
        //and delete the shared values
        SharedPreferences.Editor ed=
                    getSharedPreferences(ACTIVITY_SERVICE,MODE_WORLD_READABLE).edit();
        ed.remove("cellsize");
        ed.remove("style");
        ed.remove("sens");
        ed.commit();
        super.onDestroy();
    }

///////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////LISTENERS/////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
    
    public void onClick(View view){
        if(view.getId()==R.main.continueButton){
            //if you click on the continue button flipp to a game view and set
            //the start time with the current system time
            Flipping(1);
            start_time=System.currentTimeMillis();
        }
        else if(view.getId()==R.main.startButton){
            //on start button click
            start_time=System.currentTimeMillis();
            elapsed_time=0;
            //if there is a previously started game, make an alert dialog, to
            //ask, the player want to finish that game
            if(isGame){
                retreat_question=true;
                new AlertDialog.Builder(this)
                .setView(getAlertDialogView("There is an unfinished game. Do you retreat?"))
                .setPositiveButton("Yes",this)
                .setNegativeButton("No",this)
                .show();
            }
            else{
                newGame(false);
            }
        }
        else if(view.getId()==R.main.levelButton){
            //show the level dialog
            dialog.show();
        }
        else if(view.getId()==R.main.highButton){
            //first read the results
            readResults();
            //then show the highscores
            Flipping(0);
            flipper.showPrevious();
        }
        else if(view.getId()==R.main.quitButton){
            //on quit button click finish the game, precisely finish the activity
            finish();
        }
        else if(view.getId()==R.status.back){
            //back button, this can be seen in the status view
            Flipping(2);
        }
        else if(view.getId()==R.highscore.backButton){
            //this button can see in the highscore view
            Flipping(0);
            flipper.showNext();
        }
    }

    @Override
    public boolean onKeyDown(int key, KeyEvent event){
        if(key==KeyEvent.KEYCODE_BACK){
            //on back button make a step back in the game
            game.Undo();
            return true;
        }
        else if(key==KeyEvent.KEYCODE_HOME)
            //on home button click the only thing to do is pause the game, so
            //save the elapsed time
            if(!game.isZoomOut())
                elapsed_time+=System.currentTimeMillis()-start_time;
        return false;//on home button click this method return false, so the event
        //will be hadle by the os
    }

    public void onAnimationStart(Animation arg0){
        //on animation start there is nothing to do
    }

    public void onAnimationEnd(Animation arg0){
        //on animation end set the continue button visibility hang on the
        //isGame boolean variable
        if(!flipper.isFlipping() && isGame){
            Button cont=(Button)findViewById(R.main.continueButton);
            cont.setVisibility(View.VISIBLE);
        }
        if(!flipper.isFlipping() && !isGame){
            Button cont=(Button)findViewById(R.main.continueButton);
            cont.setVisibility(View.INVISIBLE);
        }
    }

    public void onAnimationRepeat(Animation arg0){
        //nothing have to do
    }

    public void onClick(DialogInterface arg0,int arg1){
        //this method is the listener to the alert dialog created when there is an
        //unfinished game and the gamer want to start a new one
        if(retreat_question){
            if(arg1==DialogInterface.BUTTON_POSITIVE){
                //gameOver(gamertypes.o.ordinal());//if retreat, android win
                arg0.dismiss();
                //start a new game
                newGame(false);
            }
            else if(arg1==DialogInterface.BUTTON_NEGATIVE){
                //if no retreat, dismiss the dialog
                arg0.dismiss();
            }
            retreat_question=false;
        }
        else if(arg1==DialogInterface.BUTTON_POSITIVE){
            //this called when the first question after the game started displayed,
            //->"May I start?", if yes, android make the first step
            game.androidFirstStep();
        }
    }
///////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////PUBLIC METHODS////////////////////////////
///////////////////////////////////////////////////////////////////////////////
    
    public void createAnimations(){
        //create animation for the flipper
        animRightIn=new TranslateAnimation(
                Animation.ABSOLUTE,display.getWidth(),
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,0);
        animRightIn.setDuration(500);
        animRightIn.setAnimationListener(this);
        animRightIn.setInterpolator(new LinearInterpolator());

        animLeftOut=new TranslateAnimation(
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,-display.getWidth(),
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,0);
        animLeftOut.setDuration(500);
        animLeftOut.setAnimationListener(this);
        animLeftOut.setInterpolator(new LinearInterpolator());

        animLeftIn=new TranslateAnimation(
                Animation.ABSOLUTE,-display.getWidth(),
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,0);
        animLeftIn.setDuration(500);
        animLeftIn.setAnimationListener(this);
        animLeftIn.setInterpolator(new LinearInterpolator());

        animRightOut=new TranslateAnimation(
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,display.getWidth(),
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,0);
        animRightOut.setDuration(500);
        animRightOut.setAnimationListener(this);
        animRightOut.setInterpolator(new LinearInterpolator());

        animAlphaIn=new AlphaAnimation(0,1);
        animAlphaIn.setDuration(500);
        animAlphaOut=new AlphaAnimation(1,0);
        animAlphaOut.setDuration(500);
    }

    public void readResults(){
        //to open results.txt from sdCard, and display the content on the
        //higshscore view
        //make a result String array which contains the default result values
        String[] results={"0%0%0:00%-","0%0%0:00%-","0%0%0:00%-"};
        File f=new File("/sdcard/Five Stones/results.txt");
        try{
            //make input streams
            FileInputStream fis=new FileInputStream(f);
            BufferedInputStream bis=new BufferedInputStream(fis);
            DataInputStream dis=new DataInputStream(bis);
            int i=0;
            //read lines from the text file, and put content to the string array
            while(dis.available()!=0){
                results[i]=dis.readLine();++i;
            }
            //close the inputstreams
            dis.close();
            bis.close();
            fis.close();
        }catch(Exception except){//catch the exception, if the directory or the
            //file does not exist in the sdCard
            try{
                //in this case make the directory and a file
                new File("/sdcard/Five Stones").mkdir();
                f.createNewFile();
            }catch(Exception e){}
        }
        for(int i=0;i<3;++i){
            String[] part=results[i].split("%");
            //get textviews from the view, and set the text with the readed results
            switch(i){
                case 0:{
                    TextView easy=(TextView)findViewById(R.highscore.easy);
                    easy.setText("Level: Easy\n\tHuman:\t"+part[0]+
                        "\tAndroid:\t"+part[1]+"\n\tBest Time:\t"+part[2]+
                        "\n\tDate:\t"+part[3]);
                }break;
                case 1:{
                    TextView medium=(TextView)findViewById(R.highscore.medium);
                    medium.setText("Level: Medium\n\tHuman:\t"+part[0]+
                        "\tAndroid:\t"+part[1]+"\n\tBest Time:\t"+part[2]+
                        "\n\tDate:\t"+part[3]);
                }case 2:{
                    TextView hard=(TextView)findViewById(R.highscore.hard);
                    hard.setText("Level: Hard\n\tHuman:\t"+part[0]+
                        "\tAndroid:\t"+part[1]+"\n\tBest Time:\t"+part[2]+
                        "\n\tDate:\t"+part[3]);
                }break;
                default: break;
            }
        }
    }

    public void gameOver(int gamer){
        //on game over hide the continue button
        Button cont=(Button)findViewById(R.main.continueButton);
        cont.setVisibility(View.INVISIBLE);
        isGame=false;//set this to false
        //calculate the whole elapsed time of the game
        long time=(elapsed_time+System.currentTimeMillis()-start_time)/1000;
        //make a string array that contains the default values
        String[] results={"0%0%0:00%-","0%0%0:00%-","0%0%0:00%-"};
        //set the file will be open
        File f=new File("/sdcard/Five Stones/results.txt");
        //first open a file and read the content and put it into the string array
        try{
            FileInputStream fis=new FileInputStream(f);
            BufferedInputStream bis=new BufferedInputStream(fis);
            DataInputStream dis=new DataInputStream(bis);
            int i=0;
            while(dis.available()!=0){
                results[i]=dis.readLine();++i;
            }
            dis.close();
            bis.close();
            fis.close();
        }catch(Exception except){
            try{//if the file or the directory does not exist, create them
                new File("/sdcard/Five Stones").mkdir();
                f.createNewFile();
            }catch(Exception e){}
        }
        String[] part=results[dialog.getLevelAsInt()].split("%");
        //one row of the file contains the human score, android score, best time,
        //and the date, separate by "%". In this section update the results
        //with the new values, if can be
        if(gamer==gamertypes.x.ordinal())
            part[0]=Integer.toString(Integer.parseInt(part[0])+1);
        else if(gamer==gamertypes.o.ordinal())
            part[1]=Integer.toString(Integer.parseInt(part[1])+1);
        int best=Integer.parseInt((part[2].split(":"))[0])*60
                +Integer.parseInt((part[2].split(":"))[1]);
        if((time<best || best==0) && gamer==gamertypes.x.ordinal()){
            int min=(int)time/60;
            int sec=(int)(time-min*60);
            part[2]=Integer.toString(min)+":"+Integer.toString(sec);
            DateFormat dateFormat=new SimpleDateFormat("yyyy.MM.dd-HH:mm");
            java.util.Date date=new java.util.Date();
            part[3]=dateFormat.format(date);
        }
        //In the end write the new lines into the file
        String res=part[0]+"%"+part[1]+"%"+part[2]+"%"+part[3];
        try{
            //open output streams
            FileWriter fw=new FileWriter(f);
            BufferedWriter bw=new BufferedWriter(fw);
            if(dialog.getLevel().equals("\tEasy")){
                bw.write(res);
                bw.newLine();
                bw.write(results[1]);
                bw.newLine();
                bw.write(results[2]);
            }
            else if(dialog.getLevel().equals("\tMedium")){
                bw.write(results[0]);
                bw.newLine();
                bw.write(res);
                bw.newLine();
                bw.write(results[2]);
            }
            else if(dialog.getLevel().equals("\tHard")){
                bw.write(results[0]);
                bw.newLine();
                bw.write(results[1]);
                bw.newLine();
                bw.write(res);
            }
            //close output streams
            bw.flush();
            bw.close();
            fw.close();
        }catch(Exception e){ }
    }

    public void continueGame(){
        start_time=System.currentTimeMillis();
    }

    public void pauseGame(){
        elapsed_time+=System.currentTimeMillis()-start_time;
    }

    public boolean isGame(){
        return isGame;
    }

    public int getLevel(){
        return dialog.getLevelAsInt();
    }

    public void setOptions(){
        options=true;
    }

    public static void Flipping(int type){
        //set the flipper animations, every flipping need a different in and out
        //animations. With this method we can avoid code duplications
        switch(type){
            case 0: {//alpha in
                flipper.setInAnimation(animAlphaIn);
                flipper.setOutAnimation(animAlphaOut);
            }break;
            case 1: {//forward
                flipper.setInAnimation(animRightIn);
                flipper.setOutAnimation(animLeftOut);
                flipper.showNext();
            }break;
            case 2: {//backward
                flipper.setInAnimation(animLeftIn);
                flipper.setOutAnimation(animRightOut);
                flipper.showPrevious();
            }break;
            default: break;
        }
    }

    public void newGame(boolean fromgame){
        game.createBoard();//create a game board
        if(!fromgame)
            Flipping(1);
        isGame=true;//the game started
        //create an alert dialog to decide who step first       
        
        new AlertDialog.Builder(this)
            //.setMessage("May I start?")
            .setView(getAlertDialogView("May I start?"))
            .setPositiveButton("Yes",this)
            .setNegativeButton("No",this)
            .show();
    }

    public View getAlertDialogView(String text){
        LayoutInflater inflater=(LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.alertdialogview,null);

        TextView tv=(TextView)row.findViewById(R.text.label);
        tv.setBackgroundColor(android.graphics.Color.BLACK);
        tv.setText(text);

        return row;
    }
}