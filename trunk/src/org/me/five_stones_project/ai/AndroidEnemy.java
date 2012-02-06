package org.me.five_stones_project.ai;

import org.me.five_stones_project.IEnemy;
import org.me.five_stones_project.R;
import org.me.five_stones_project.activity.GameActivity;
import org.me.five_stones_project.game.GameHandler;
import org.me.five_stones_project.type.Players;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;

/**
 *
 * @author Tangl Andras
 */

public abstract class AndroidEnemy implements IEnemy {
	private AndroidStep step;
	protected Players android, human;

	public AndroidEnemy() {	}
	
	public void initialize(Players human, Players android) {
		this.human = human;
		this.android = android;
	}
	
	@Override
	public boolean cancel() {
		if(step == null || step.getStatus() == Status.FINISHED)
			return true;
		
		if(step != null && !step.isCancelled())
			try {
				return step.cancel(true);
			} catch(Exception e) {
				return false;
			}
		
		return false;
	}
	
	@Override
	public void makeStep(GameHandler handler) {
		step = new AndroidStep(handler);
		step.execute(new Object());
	}

	@Override
	public void showEndDialog(GameHandler handler) {
		int drawable;
		if(handler.stat.winner == android)
			drawable = R.drawable.emo_im_cool;
		else
			drawable = R.drawable.emo_im_crying;
		
		final GameActivity instance = GameActivity.getInstance();
		new AlertDialog.Builder(instance)
	    	.setIcon(drawable)
	    	.setPositiveButton(R.string.yes, new OnClickListener() {
	    		
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					instance.reinitilize();								
				}
			})
			.setNegativeButton(R.string.no, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					finish();
					instance.finish();
				}
			})
			.setTitle(R.string.newGame).show();
	}
	
	protected abstract void finish();
		
	protected abstract Point findBestStep(GameHandler handler);
	
	protected int isBoardEmpty(int[][] board) {
		int count = 0;
		for(int i = 0; i < board.length; ++i)
			for(int j = 0; j < board[0].length; ++j)
				if(board[i][j] != Players.None.ordinal())
					count++;
		return count;
	}
	
	protected void arrayCopy(int[][] src, int[][] dst, int skipX, int skipY) {
		for(int i = 0; i < src.length; ++i)
			for(int j = 0; j < src[0].length; ++j)
				dst[skipX + i][skipY + j] = src[i][j];
	}
	
	protected int[][] copyBoard(int[][] board) {
		int[][] copy = new int[board.length][board[0].length];
		for(int i = 0; i < board.length; ++i)
			for(int j = 0; j < board[0].length; ++j)
				copy[i][j] = board[i][j];
		return copy;
	}
	
	private class AndroidStep extends AsyncTask<Object, Object, Point> {
		private GameHandler handler;
		
		public AndroidStep(GameHandler handler) {
			this.handler = handler;
		}

		@Override
		protected Point doInBackground(Object... paramArrayOfParams) {
			return findBestStep(handler);
		}
		
		@Override
		protected void onPostExecute(Point result) {
			super.onPostExecute(result);
			
			handler.enemyStep(result, true);
		}
	}
}
