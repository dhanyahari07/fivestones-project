package org.me.five_stones_project;


import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.type.Descriptions;

import org.me.five_stones_project.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 *
 * @author Tangl Andras
 */

public class LevelDialog extends AlertDialog implements OnItemClickListener {
	private String[] descriptions;
	
    public LevelDialog(Context context){
        super(context);

        int selectedLevel = 0;
        descriptions = Descriptions.getDescriptions(context, Descriptions.Level);
        for(int i = 0; i < descriptions.length; ++i)
        	if(descriptions[i].equals(GameOptions.getInstance().
        			getCurrentLevel().getDescription(context))) {
        		selectedLevel = i;
        		break;
        	}
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
            R.layout.select_dialog_single_choice, descriptions);

        ListView view = new ListView(context);
        view.setAdapter(adapter);
        view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        view.setItemChecked(selectedLevel, true);
        view.setOnItemClickListener(this);

        setTitle(context.getResources().getString(R.string.levelDialog));
        setView(view);
    }

     public void onItemClick(AdapterView<?> arg, View view, int pos, long id){
        GameOptions.getInstance().setCurrentLevel(
        		Descriptions.findByDescription(getContext(), descriptions[pos]));
        GameOptions.getInstance().commit(getContext());
        view.setSelected(true);
        //hide();
        dismiss();
    }
}
