package org.me.five_stones_project;

import android.widget.*;

import android.view.View;
import android.graphics.Color;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;

/**
 *
 * @author Tangl
 */

public class LevelDialog extends AlertDialog implements OnItemClickListener{
    private String level="\tEasy";
    private String[] options={"\tEasy","\tMedium","\tHard"};

    public LevelDialog(Context context){
        super(context);

        ArrayAdapter adapter=new ArrayAdapter<String>(context,
            android.R.layout.select_dialog_singlechoice,options);

        ListView view=new ListView(context);
        view.setAdapter(adapter);
        view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        view.setItemChecked(0,true);
        view.setOnItemClickListener(this);
        view.setBackgroundColor(Color.GRAY);

        setTitle("Select Level");
        setView(view);
    }

     public void onItemClick(AdapterView<?> arg, View view, int pos, long id){
        level=options[pos];
        view.setSelected(true);
        hide();
    }

    public int getLevelAsInt(){
        if(level.equals("\tEasy"))
            return 0;
        else if(level.equals("\tMedium"))
            return 1;
        else
            return 2;
    }

    public String getLevel(){
         return level;
    }
}
