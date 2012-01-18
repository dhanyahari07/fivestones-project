package org.me.five_stones_project.game;

import org.me.five_stones_project.AndroidMenu;
import org.me.five_stones_project.R;
import org.me.five_stones_project.type.Descriptions;
import org.me.five_stones_project.type.Players;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 
 * @author Tangl Andras
 */

public class GameView extends View {
	private static final int DRAG_MODE = 1;
	private static final int ZOOM_MODE = 2;
	
	private AndroidMenu menu;
	private GameHandler handler;
	
	private Bitmap board;
	private Display display;
	private int cellSize, minCellSize;
	private int[] cellPixels, cellXPixels, cellOPixels;

	public GameView(Context context, GameHandler handler, AndroidMenu menu) {
		super(context);
		
		this.menu = menu;
		this.handler = handler;
		
		display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		cellSize = BitmapFactory.decodeResource(getResources(), R.drawable.cell_classic).getWidth();
		
		openBitmaps();
		
		initialize();
	}
	
	private void initialize() {		
		int width = display.getWidth() / (cellSize / 2) + 1;
        int height = display.getHeight() / (cellSize / 2) + 1;
        this.handler.signs = new int[width][height];
        
        mode = 0;
    	originalDistance = 0;
    	start = new PointF();
    	midPoint = new PointF();
    	matrix = new Matrix();
    	savedMatrix = new Matrix();
        
		drawBoard();
	}
	
	public void reinitilize() {
		initialize();
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {		
		Point padding = new Point();
		float ratio = calculatePadding(padding);
		
		matrix.postTranslate(translate.x, translate.y);		

		PointF delta = new PointF();
	    float[] mappedPoints = new float[4];
		matrix.mapPoints(mappedPoints, new float[] {
			0, 0, display.getWidth(), display.getHeight()});
		
	    if(mappedPoints[0] > 0)
			delta.x = -mappedPoints[0];
	    else if(mappedPoints[2] < display.getWidth())
			delta.x = display.getWidth() - mappedPoints[2];
		if(mappedPoints[1] > 0)
			delta.y = -mappedPoints[1];
		else if(mappedPoints[3] < display.getHeight())
			delta.y = display.getHeight() - mappedPoints[3];
		
		matrix.postTranslate(delta.x, delta.y);
		
		super.onDraw(canvas);
		
		canvas.save();		
		canvas.setMatrix(matrix);		
		canvas.drawColor(Color.argb(150, 150, 150, 150));
		
		float[] positions = new float[] { 0, 1 };
		int[] colors = new int[] { 0x00000000, 0x40000000 };
        			
		Rect src = new Rect(0, 0, board.getWidth(), board.getHeight());
        Rect dst = new Rect(padding.x, padding.y, 
        		display.getWidth() - padding.x, display.getHeight() - padding.y);
		
        canvas.drawBitmap(board, src, dst, null);
		
		// draw the gradient
        if(!handler.getLastStep().equals(-1, -1) && !isAnimation) {
        	Shader shader=new RadialGradient(
                (handler.getLastStep().x * cellSize + cellSize / 2) * ratio + padding.x,
                (handler.getLastStep().y * cellSize + cellSize / 2) * ratio + padding.y,
                cellSize, colors, positions, Shader.TileMode.CLAMP);
        
			RectF rect = new RectF(0, 0, display.getWidth(), display.getHeight());
			Paint paint = new Paint();
			paint.setDither(true);
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setShader(shader);
			canvas.drawRect(rect, paint);
        }
		
		if (handler.IsGameEnds() && !isAnimation) {
			Paint line = new Paint();
			if (handler.stat.winner ==  Players.X)
				line.setColor(Color.RED);
			else if (handler.stat.winner ==  Players.O)
				line.setColor(Color.BLUE);
			
			line.setStrokeWidth(2);
			canvas.drawLine(
				(handler.stat.start.x * cellSize + cellSize / 2) * ratio + padding.x, 
				(handler.stat.start.y * cellSize + cellSize / 2) * ratio + padding.y, 
				(handler.stat.end.x * cellSize + cellSize / 2) * ratio + padding.x, 
				(handler.stat.end.y * cellSize + cellSize / 2) * ratio + padding.y, line);
		}
		
		canvas.restore();
		
		if(isAnimation)
			runAnimation();
	}
	
	private int mode = 0;
	private PointF midPoint, start;
	private Matrix matrix, savedMatrix;
	private double originalDistance = 0;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isAnimation)
			finishAnimation();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN : {
			mode = DRAG_MODE;
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
		} break;
		case MotionEvent.ACTION_POINTER_DOWN :
		case MotionEvent.ACTION_POINTER_2_DOWN : {
			mode = ZOOM_MODE;
			originalDistance = calculateDistance(event);

			if(originalDistance > 10) {
				savedMatrix.set(matrix);
				float x = event.getX(0) + event.getX(1);
			    float y = event.getY(0) + event.getY(1);
			    midPoint.set(x / 2, y / 2);
			}
		} break;
		case MotionEvent.ACTION_MOVE: {
			if(mode == ZOOM_MODE) {
				double newDistance = calculateDistance(event);
				
				if(newDistance > 10) {	                
	                matrix.set(savedMatrix);	
	                float scaleFactor = (float)(newDistance / originalDistance);
                	matrix.postScale(scaleFactor, scaleFactor, midPoint.x, midPoint.y);
	                if(scaleFactor < 1) {	                	
	                	float[] mappedPoints = new float[4];
	                	matrix.mapPoints(mappedPoints, new float[] {0, 0, 
	                			display.getWidth(), display.getHeight()});
	                	
	                	if(mappedPoints[2] - mappedPoints[0] < display.getWidth()) {
	                		//menu.show();
	                		
	            			float scale = display.getWidth() / (mappedPoints[2] - mappedPoints[0]);
	            			matrix.postScale(scale, scale, midPoint.x, midPoint.y);
	            		}
	                }
	                else {
                		float[] mappedPoints = new float[2];
                		matrix.mapPoints(mappedPoints, new float[] {0, minCellSize});
                		
                		if(mappedPoints[1] - mappedPoints[0] > cellSize) {
                			float scale = cellSize / (mappedPoints[1] - mappedPoints[0]);
	            			matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                		}
	                	//menu.hide();
	                }
				}
			}
			else if(mode == DRAG_MODE) {
	            matrix.set(savedMatrix);
	            matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			}
			
        	invalidate();
		} break;
		case MotionEvent.ACTION_UP: {
			if(handler.getLastStepPlayer() != handler.me && event.getEventTime() - event.getDownTime() 
					< GameOptions.getInstance().getSensitivity() * 100) {
				
				Point padding = new Point();
				calculatePadding(padding);
		        float[] mappedPoints = new float[4];
		    	matrix.mapPoints(mappedPoints, new float[] {
		    			0, 0, display.getWidth(), display.getHeight()});
		    	float ratio = (mappedPoints[2] - mappedPoints[0]) / display.getWidth();
		    	Point p = new Point(
		    			(int)((event.getX() - mappedPoints[0]) / ratio - padding.x) 
		    			* handler.signs.length / (display.getWidth() - 2 * padding.x),
		    			(int)((event.getY() - mappedPoints[1]) / ratio - padding.y) 
		    			* handler.signs[0].length / (display.getHeight() - 2 * padding.y));
				
				if(handler.signs[p.x][p.y] == Players.None.ordinal()) {
					handler.makeMyStep(p);
					setCell(handler.me);
					invalidate();
				}
			}
		} break;
		case MotionEvent.ACTION_CANCEL: {

		} break;
		default: break;
		}

		return true;
	}
	
	private double calculateDistance(MotionEvent event) {
		float dx = event.getX(0) - event.getX(1);
		float dy = event.getY(0) - event.getY(1);
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	private float calculatePadding(Point padding) {
		float ratio; 
        if(display.getWidth() * board.getHeight() / board.getWidth() > display.getHeight()) {
            ratio = (float) display.getHeight() / board.getHeight();
            padding.x = (display.getWidth() - (int)(board.getWidth() * ratio)) / 2;
        }
        else {
            ratio = (float) display.getWidth() / board.getWidth();
            padding.y = (display.getHeight() - (int)(board.getHeight() * ratio)) / 2;
        }
        
        minCellSize = (display.getWidth() - padding.x * 2) / handler.signs.length;
        
        return ratio;
	}
	
	public void increaseBoard(int where) {
		Point delta = new Point(), temp = new Point();
		
		float oldRatio = calculatePadding(temp);
		
		switch(where) {
			case GameHandler.INC_LEFT :
				delta.x = 1;
				break;
			case GameHandler.INC_TOP : 
				delta.y = 1;
				break;
			case GameHandler.INC_LEFT_TOP : 
				delta.x = delta.y = 1;
				break;
			default: break;
		}

        drawBoard();
        
        float newRatio = calculatePadding(temp);
        
        float[] mappedPoints = new float[1];
        matrix.mapPoints(mappedPoints, new float[] {cellSize});        
        matrix.postTranslate(delta.x * mappedPoints[0], delta.y * mappedPoints[0]);
        
        float scale = oldRatio / newRatio;
        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
	}
	
	private void openBitmaps(){		
        GameOptions instance = GameOptions.getInstance();
        cellPixels = new int[(int)Math.pow(cellSize, 2)];
        cellOPixels = new int[(int)Math.pow(cellSize, 2)];
        cellXPixels = new int[(int)Math.pow(cellSize, 2)];
        
        if(instance.getCurrentStyle().equals(Descriptions.Classic)) {
        	loadPixels(BitmapFactory.decodeResource(getResources(),R.drawable.o_classic), cellOPixels);
        	loadPixels(BitmapFactory.decodeResource(getResources(),R.drawable.x_classic), cellXPixels);
        	loadPixels(BitmapFactory.decodeResource(getResources(),R.drawable.cell_classic), cellPixels);
        }
        else if(instance.getCurrentStyle().equals(Descriptions.Gomoku)) {
        	loadPixels(BitmapFactory.decodeResource(getResources(),R.drawable.o_gomoku), cellOPixels);
        	loadPixels(BitmapFactory.decodeResource(getResources(),R.drawable.x_gomoku), cellXPixels);
        	loadPixels(BitmapFactory.decodeResource(getResources(),R.drawable.cell_gomoku), cellPixels);
        }
        else if(instance.getCurrentStyle().equals(Descriptions.Modern)) {
        	loadPixels(BitmapFactory.decodeResource(getResources(),R.drawable.o_modern), cellOPixels);
        	loadPixels(BitmapFactory.decodeResource(getResources(),R.drawable.x_modern), cellXPixels);
        	loadPixels(BitmapFactory.decodeResource(getResources(),R.drawable.cell_modern), cellPixels);
        }
    }
	
	private void loadPixels(Bitmap bitmap, int[] buffer) {
        bitmap = Bitmap.createScaledBitmap(bitmap, cellSize, cellSize, true);
        bitmap.getPixels(buffer, 0, cellSize, 0, 0, cellSize, cellSize);
	}

	public void drawBoard() {
		if(board != null)
			board.recycle();
		
        board = Bitmap.createBitmap(handler.signs.length * cellSize, 
    		handler.signs[0].length * cellSize, Bitmap.Config.ARGB_8888);
        
		for (int i = 0; i < handler.signs.length; ++i)
			for (int j = 0; j < handler.signs[0].length; ++j) {
				if (handler.signs[i][j] == Players.None.ordinal())
					board.setPixels(cellPixels, 0, cellSize, 
						i * cellSize, j * cellSize, cellSize, cellSize);
				if (handler.signs[i][j] == Players.X.ordinal())
					board.setPixels(cellXPixels, 0, cellSize, 
						i * cellSize, j * cellSize, cellSize, cellSize);
				if (handler.signs[i][j] == Players.O.ordinal())
					board.setPixels(cellOPixels, 0, cellSize, 
						i * cellSize, j * cellSize, cellSize, cellSize);
			}
	}
	
	public void clearCell(Point p) {
		board.setPixels(cellPixels, 0, cellSize, 
			p.x * cellSize,	p.y * cellSize, cellSize, cellSize);
	}
	
	public void setCell(Players player) {
		if(player == Players.X)
            board.setPixels(cellXPixels, 0, cellSize, handler.getLastStep().x * cellSize,
            		handler.getLastStep().y * cellSize, cellSize, cellSize);
        else if(player == Players.O)
        	board.setPixels(cellOPixels, 0, cellSize, handler.getLastStep().x * cellSize,
        			handler.getLastStep().y * cellSize, cellSize, cellSize);
	}
	
	public void showAndroidMenu() {
		menu.setVisibility(VISIBLE);
	}
	
	public void hideAndroidMenu() {
		menu.setVisibility(INVISIBLE);
	}
	
	/*
	 * create frame by frame animation
	 */
	
	public void translate() {
		Point padding = new Point();
		calculatePadding(padding);
		
		float[] lastStep = new float[2];
		matrix.mapPoints(lastStep, new float[] { 
			handler.getLastStep().x * minCellSize + padding.x, 
			handler.getLastStep().y * minCellSize + padding.y });
	  	
	    float[] mappedPoints = new float[4];
	    matrix.mapPoints(mappedPoints, new float[] {
			0, 0, display.getWidth(), display.getHeight()});

		PointF delta = new PointF();
	    if(mappedPoints[0] + display.getWidth() / 2 - lastStep[0] > 0)
			delta.x = -mappedPoints[0];
		else if(mappedPoints[2] + display.getWidth() / 2 - lastStep[0] < display.getWidth())
			delta.x = display.getWidth() - mappedPoints[2];
	    else
	    	delta.x = display.getWidth() / 2 - lastStep[0];
	    
		if(mappedPoints[1] + display.getHeight() / 2 - lastStep[1] > 0)
			delta.y = -mappedPoints[1];
		else if(mappedPoints[3] + display.getHeight() / 2 - lastStep[1] < display.getHeight())
			delta.y = display.getHeight() - mappedPoints[3];
		else
			delta.y = display.getHeight() / 2 - lastStep[1];
		
		frames = (int) Math.max(Math.abs(delta.x) / 5, Math.abs(delta.y) / 5) + 1;
		translate.set(delta.x / frames, delta.y / frames);
		
		startTime = System.currentTimeMillis();
		runAnimation();
	}
	
	private void runAnimation() {
		if(progress == frames) 
			finishAnimation();
		else {
			progress++;
			isAnimation = true;
			endTime = System.currentTimeMillis();
			animHandler.postDelayed(animation, endTime - startTime <= 200 / frames ? 200 / frames : 0);
		}
	}
	
	public void finishAnimation() {
		progress = 0;
		isAnimation = false;
		translate.set(0, 0);
		setCell(handler.enemy);
		handler.checkFinish();
	}
	
	private long startTime, endTime;
	private int frames, progress = 0;
	private boolean isAnimation = false;
	private PointF translate = new PointF();
	private Handler animHandler = new Handler();
	private FrameByFrameTranslateAnimation animation = 
			new FrameByFrameTranslateAnimation();
	private class FrameByFrameTranslateAnimation implements Runnable {

		@Override
		public void run() {
			startTime = System.currentTimeMillis();
			invalidate();
		}		
	}
}
