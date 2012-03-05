package org.me.five_stones_project.activity;


import org.me.five_stones_project.game.GameOptions;
import org.me.five_stones_project.type.Descriptions;
import org.me.five_stones_project.type.Languages;

import org.me.five_stones_project.R;

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
	private ListView list;
	private SeekBar sensitivity, ai;
	private Languages selectedLanguage;
	private Descriptions selectedStyle, selectedQuality; 

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
		
		ai = (SeekBar) findViewById(R.options.seekbarai);
		ai.setProgress(instance.getAi(true));
		
		/*
		 * create other list
		 */
		final String[] descriptions = new String[] { 
				getResources().getString(R.string.animation),
				getResources().getString(R.string.vibration),
				getResources().getString(R.string.fullscreen)};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, descriptions);

		list = (ListView) findViewById(R.options.listOther);
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		list.setItemChecked(0, GameOptions.getInstance().isAnimation());
		list.setItemChecked(1, GameOptions.getInstance().isVibration());
		list.setItemChecked(2, GameOptions.getInstance().isFullScreen());
		
		/*
		 * create quality list
		 */
		final String[] qualityDescriptions = Descriptions.getDescriptions(this, Descriptions.Quality);
		
		ArrayAdapter<String> qualityAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, qualityDescriptions);

		ListView qualityList = (ListView) findViewById(R.options.listQuality);
		qualityList.setAdapter(qualityAdapter);
		qualityList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				selectedQuality = Descriptions.findByDescription(OptionsActivity.this, qualityDescriptions[pos]);
			}
		});
		qualityList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		for (int i = 0; i < qualityDescriptions.length; ++i)
			if (instance.getCurrentQuality().getDescription(this).equals(qualityDescriptions[i])) {
				qualityList.setItemChecked(i, true);
				selectedQuality = Descriptions.findByDescription(this, qualityDescriptions[i]);
				break;
			}

		/*
		 * create style list
		 */
		final String[] styleDescriptions = Descriptions.getDescriptions(this, Descriptions.Style);
		
		ArrayAdapter<String> styleAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, styleDescriptions);

		ListView styleList = (ListView) findViewById(R.options.listStyle);
		styleList.setAdapter(styleAdapter);
		styleList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				selectedStyle = Descriptions.findByDescription(OptionsActivity.this, styleDescriptions[pos]);
			}
		});
		styleList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		for (int i = 0; i < styleDescriptions.length; ++i)
			if (instance.getCurrentStyle().getDescription(this).equals(styleDescriptions[i])) {
				styleList.setItemChecked(i, true);
				selectedStyle = Descriptions.findByDescription(this, styleDescriptions[i]);
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
        	
        	if(!selectedLanguage.equals(currentLanguage) || 
        			GameOptions.getInstance().isFullScreen() != list.isItemChecked(2))
        		new AlertDialog.Builder(this)
        			.setPositiveButton(R.string.restartPos, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
			        		updateLanguage(selectedLanguage);
			        		GameOptions.getInstance().setFullScreen(list.isItemChecked(2));
			            	saveChanges();

			        		finish();
						}
					})
					.setNegativeButton(R.string.restartNeg, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
			            	saveChanges();
							finish();
						}
					})
        			.setTitle(R.string.restartTitle)
        			.setMessage(R.string.restartMsg)
        			.show(); 
        	else {
        		saveChanges();
        		finish();
        	}
            		
			return true;
		} else if (key == KeyEvent.KEYCODE_HOME)
			finish();
		return false;
	}
	
	private void saveChanges() {
		GameOptions instance = GameOptions.getInstance();
		
		instance.setAi(ai.getProgress(), true);
		instance.setSensitivity(sensitivity.getProgress());
		instance.setCurrentStyle(selectedStyle);
		instance.setCurrentQuality(selectedQuality);
		
		instance.setAnimation(list.isItemChecked(0));
		instance.setVibration(list.isItemChecked(1));
		
		instance.commit(this);
	}
}
