/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.five_stones_project;

import android.app.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import android.content.Intent;
import android.graphics.Color;
import android.content.DialogInterface;

/**
 *
 * @author Tangl
 */

class AndroidMenuDialog extends AlertDialog 
        implements OnItemClickListener,DialogInterface.OnClickListener{
    private MainActivity main;

    public AndroidMenuDialog(MainActivity context){
        super(context);
        main=context;

        String[] options={"\tContinue","\tOptions","\tMenu","\tAbout"};
        int[] icons={R.drawable.cont,R.drawable.options,R.drawable.back,R.drawable.info};

        //create a new alert dialog with the specified chosen options
        ArrayAdapter adapter=new IconArrayAdapter<String>(context,
                R.layout.list_item,R.list_item.icon,
                R.list_item.label,icons,options);

        ListView view=new ListView(context);
        view.setAdapter(adapter);
        view.setBackgroundColor(Color.BLACK);
        view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        view.setOnItemClickListener(this);
        
        setView(view);
    }

    public void onItemClick(AdapterView<?> arg0,View view,int pos,long id){
        if(id==0){//Continue
            hide();
            main.continueGame();
        }
        else if(id==1){//options
            Intent intent=new Intent(main,Options.class);
            main.startActivity(intent);
            main.setOptions();
            hide();
        }
        else if(id==2){//back to main menu
            MainActivity.Flipping(2);
            hide();
        }
        else if(id==3){//about
            hide();//create a new alert dialog with about text
            String txt=main.getResources().getString(R.string.aboutcontent);
            new AlertDialog.Builder(getContext())
                .setView(main.getAlertDialogView(txt))
                .setNeutralButton("Back",this)
                .show();
        }
    }

    public void onClick(DialogInterface arg0, int arg1){//for about dialog
        arg0.dismiss();
        show();
    }
}
