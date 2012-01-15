package org.me.five_stones_project.activity;

import org.me.five_stones_project.R;
import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.type.Descriptions;
import org.me.five_stones_project.type.Languages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

/**
 * 
 * @author Tangl Andras
 */

public class OptionsActivity extends BaseActivity {
	private SeekBar sensitivity;
	private Languages selectedLanguage;
	private Descriptions selectedStyle; 

	@Override
	protected void onResume() {
		super.onResume();

		/*
		 * initialize
		 */
		setContentView(R.layout.options);
		GameOptions instance = GameOptions.getInstance();

		sensitivity = (SeekBar) findViewById(R.options.seekbarsens);
		sensitivity.setProgress(instance.getSensitivity());

		/*
		 * create style list
		 */
		final String[] styleDescriptions = Descriptions.getDescriptions(Descriptions.Style);
		
		ArrayAdapter<String> styleAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, styleDescriptions);

		ListView styleList = (ListView) findViewById(R.options.listStyle);
		styleList.setAdapter(styleAdapter);
		styleList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				selectedStyle = Descriptions.findByDescription(styleDescriptions[pos]);
			}
		});
		styleList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		for (int i = 0; i < styleDescriptions.length; ++i)
			if (instance.getCurrentStyle().getDescription().equals(styleDescriptions[i])) {
				styleList.setItemChecked(i, true);
				selectedStyle = Descriptions.findByDescription(styleDescriptions[i]);
				break;
			}
		
		/*
		 * create language list
		 */
		final String[] languageDescriptions = Languages.getDescriptions();
		
		ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, languageDescriptions);

		ListView languageList = (ListView) findViewById(R.options.listLanguage);
		languageList.setAdapter(languageAdapter);
		languageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				selectedLanguage = Languages.findByDescription(languageDescriptions[pos]);
			}
		});
		languageList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		selectedLanguage = currentLanguage;
		languageList.setItemChecked(selectedLanguage.ordinal() - 1, true);
	}

	@Override
	public boolean onKeyDown(int key, KeyEvent event) {
		if (key == KeyEvent.KEYCODE_BACK) {
        	saveChanges();
        	
        	if(!selectedLanguage.equals(currentLanguage))
        		//updateLanguage(selectedLanguage);
        		//MainActivity.restartApplication();
        		new AlertDialog.Builder(this)
        			.setPositiveButton(R.string.restartPos, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
			        		updateLanguage(selectedLanguage);

			        		finish();
						}
					})
					.setNegativeButton(R.string.restartNeg, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
						}
					})
        			.setTitle(R.string.restartTitle)
        			.setMessage(R.string.restartMsg)
        			.show(); 
        	else
        		finish();
            		
			return true;
		} else if (key == KeyEvent.KEYCODE_HOME)
			finish();
		return false;
	}
	
	private void saveChanges() {
		GameOptions instance = GameOptions.getInstance();
		
		instance.setSensitivity(sensitivity.getProgress());
		instance.setCurrentStyle(selectedStyle);
		
		instance.commit();
	}
}
