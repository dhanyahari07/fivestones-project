package org.me.five_stones_project;

import org.me.five_stones_project.activity.GameActivity;
import org.me.five_stones_project.common.IconArrayAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Tangl Andras
 */

public class MenuDialog extends AlertDialog implements OnItemClickListener,
		DialogInterface.OnClickListener {

	public MenuDialog(Context context) {
		super(context);

		Resources res = context.getResources();
		String[] options = { 
				res.getString(R.string.cont),
				res.getString(R.string.restart),
				/*res.getString(R.string.options),*/
				res.getString(R.string.about),
				res.getString(R.string.backToMenu) };
		int[] icons = { 
				R.drawable.cont, R.drawable.restart,
				/*R.drawable.options,*/	R.drawable.info, R.drawable.back };

		// create a new alert dialog with the specified chosen options
		ArrayAdapter<String> adapter = new IconArrayAdapter<String>(context,
				R.layout.listitem, R.listitem.icon, R.listitem.label, icons,
				options);

		ListView view = new ListView(context);
		view.setAdapter(adapter);
		view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		view.setOnItemClickListener(this);

		setView(view);
		
		setTitle(R.string.menuDialog);
		setIcon(android.R.drawable.ic_menu_more);
	}
	
	@Override
	public void onBackPressed() {
		GameActivity.getInstance().getHandler().continueGame();
		dismiss();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		if (id == 0) {// continue
			GameActivity.getInstance().getHandler().continueGame();
			dismiss();
		}
		else if (id == 1) {// restart
			GameActivity.getInstance().reinitilize();
			dismiss();
		}
		else if (id == 2) {// about*/
			dismiss();
			new AlertDialog.Builder(getContext())
					.setTitle(R.string.about)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setMessage(R.string.aboutContent)
					.setNeutralButton(R.string.hback, this).show();
		}
		else if (id == 3) {// back to main menu
			dismiss();
			GameActivity.getInstance().finish();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {// for about dialog
		dialog.dismiss();
		show();
	}

}
