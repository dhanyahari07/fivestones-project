package org.me.five_stones_project;

import android.app.*;
import android.view.*;
import android.widget.*;

import android.os.Bundle;
import android.content.SharedPreferences;

/**
 *
 * @author Tangl
 */

public class Options extends Activity
        implements SeekBar.OnSeekBarChangeListener,AdapterView.OnItemClickListener{
    private String style;
    private ListView list;
    private int result,sensitivity;

  @Override
  public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //set content view
        setContentView(R.layout.options);
        //get seekbar, and set the status by the shared values, if exists
        SeekBar cell=(SeekBar)findViewById(R.options.seekbarcell);
        SeekBar sens=(SeekBar)findViewById(R.options.seekbarsens);
        SharedPreferences sp=getSharedPreferences(ACTIVITY_SERVICE,MODE_WORLD_READABLE);
        result=sp.getInt("cellsize",4);
        style=sp.getString("style","hu");
        sensitivity=sp.getInt("sens",2);
        cell.setProgress(result);
        cell.setOnSeekBarChangeListener(this);
        sens.setProgress(sensitivity);
        sens.setOnSeekBarChangeListener(this);
        //set the adapter of the listview
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_multiple_choice,
            new String[]{"\tHungarian","\tGomoku","\tModern"});

        list=(ListView)findViewById(R.options.list);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if(style.equals("hu"))
            list.setItemChecked(0,true);
        else if(style.equals("gomoku"))
            list.setItemChecked(1,true);
        else
            list.setItemChecked(2,true);
        list.setOnItemClickListener(this);
    }

    @Override
    public boolean onKeyDown(int key,KeyEvent event){
        if(key==KeyEvent.KEYCODE_BACK){
            //if back, overwrite the shared values and finish the activity
            SharedPreferences.Editor ed=
                    getSharedPreferences(ACTIVITY_SERVICE,MODE_WORLD_READABLE).edit();
            ed.putInt("cellsize",result);
            ed.putString("style",style);
            ed.putInt("sens",sensitivity);
            ed.commit();
            finish();
            return true;
        }
        else if(key==KeyEvent.KEYCODE_HOME)
            finish();
        return false;
    }

    public void onProgressChanged(SeekBar arg0,int arg1,boolean arg2){
        SeekBar cell=(SeekBar)findViewById(R.options.seekbarcell);
        if(arg0.equals(cell))
            result=arg0.getProgress();//update state and sensitivity,
        else
            sensitivity=arg0.getProgress();
        //if the progress of the seekbar is changes
    }

    public void onStartTrackingTouch(SeekBar arg0){
    }

    public void onStopTrackingTouch(SeekBar arg0){
    }

    public void onItemClick(AdapterView<?> arg0,View view,int pos,long id){
        //update the selection of the listview
        view.setSelected(true);
        switch(pos){
            case 0: style="hu";break;
            case 1: style="gomoku";break;
            case 2: style="modern";break;
            default: break;
        }
    }
}