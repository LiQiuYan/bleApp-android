package com.healthme.app.ui;

import com.healthme.app.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public final class RPM extends View {

	private static final String TAG = RPM.class.getSimpleName();

	// drawing tools
	private RectF rimRect;
	private Paint rimPaint;
	private Paint rimCirclePaint;
	
	private RectF faceRect;
	private Paint facePaint;
	
	private RectF titleRect;
	private Paint titlePaint;
	
	private Paint scalePaint;
	private Paint scaleGreenPaint;
	private Paint scaleRedPaint;	
	private RectF scaleRect;
	
	
	private Paint heartbeatPaint;
	
	private Paint handPaint;
	
	private Paint backgroundPaint; 
	// end drawing tools
	
	private Bitmap background; // holds the cached static part
	
	// scale configuration
	private static final int totalNicks = 45;
	private static final float degreesPerNick = 300.0f / totalNicks;	
	private static final int minValue = 0;
	private static final int maxValue = 180;
	private static final int rotateDegree = 150;
	
	// hand dynamics
	private float handPosition = 70f;
	
	public RPM(Context context) {
		super(context);
		init();
	}

	public RPM(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RPM(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		Parcelable superState = bundle.getParcelable("superState");
		super.onRestoreInstanceState(superState);
		
		handPosition = bundle.getFloat("handPosition");
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		
		Bundle state = new Bundle();
		state.putParcelable("superState", superState);
		state.putFloat("handPosition", handPosition);
		return state;
	}

	private void init() {
		initDrawingTools();
	}

	private String getTitle() {
		return "当前心率";
	}

	private void initDrawingTools() {
		rimRect = new RectF(1f, 1f, 99f, 99f);

		rimPaint = new Paint();
		rimPaint.setAntiAlias(true);
		rimPaint.setColor(Color.TRANSPARENT);

		rimCirclePaint = new Paint();
		rimCirclePaint.setAntiAlias(true);
		rimCirclePaint.setStyle(Paint.Style.STROKE);
		rimCirclePaint.setColor(Color.TRANSPARENT);
		rimCirclePaint.setStrokeWidth(1f);

		float rimSize = 2f;
		faceRect = new RectF();
		faceRect.set(rimRect.left + rimSize, rimRect.top + rimSize, 
			     rimRect.right - rimSize, rimRect.bottom - rimSize);
		
		facePaint = new Paint();
		facePaint.setStyle(Paint.Style.FILL);
		facePaint.setColor(Color.TRANSPARENT);
		
		
		scalePaint = new Paint();
		scalePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		scalePaint.setColor(Color.YELLOW);
		scalePaint.setStrokeWidth(0.5f);
		scalePaint.setAntiAlias(true);
		
		scalePaint.setTextSize(6f);
		scalePaint.setTypeface(Typeface.SANS_SERIF);
		scalePaint.setTextAlign(Paint.Align.CENTER);
		
		scaleGreenPaint = new Paint();
		scaleGreenPaint.setStyle(Paint.Style.STROKE);
		scaleGreenPaint.setColor(Color.parseColor("#9ec421"));
		scaleGreenPaint.setStrokeWidth(5f);
		scaleGreenPaint.setAntiAlias(true);
		
		scaleRedPaint = new Paint();
		scaleRedPaint.setStyle(Paint.Style.STROKE);
		scaleRedPaint.setColor(Color.RED);
		scaleRedPaint.setStrokeWidth(2f);
		scaleRedPaint.setAntiAlias(true);
		
		float scalePosition = 3f;
		scaleRect = new RectF();
		scaleRect.set(faceRect.left + scalePosition, faceRect.top + scalePosition,
					  faceRect.right - scalePosition, faceRect.bottom - scalePosition);

		titleRect = new RectF(35, 55, 65, 65);
		titlePaint = new Paint();
		titlePaint.setColor(Color.WHITE);
		titlePaint.setAntiAlias(true);
		titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		titlePaint.setTextAlign(Paint.Align.CENTER);
		titlePaint.setTextSize(6f);
		titlePaint.setAlpha(200);
		
		heartbeatPaint = new Paint();
		heartbeatPaint.setColor(Color.WHITE);
		heartbeatPaint.setAntiAlias(true);
		heartbeatPaint.setTypeface(Typeface.DEFAULT_BOLD);
		heartbeatPaint.setTextAlign(Paint.Align.CENTER);
		heartbeatPaint.setTextSize(20f);
		heartbeatPaint.setAlpha(200);
		
		handPaint = new Paint();
		handPaint.setAntiAlias(true);
		handPaint.setColor(Color.GREEN);
		handPaint.setStrokeWidth(2f);
		handPaint.setStyle(Paint.Style.STROKE);	
		handPaint.setAlpha(200);
		
		backgroundPaint = new Paint();
		backgroundPaint.setFilterBitmap(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);
		
		int chosenDimension = Math.min(chosenWidth, chosenHeight);
		
		setMeasuredDimension(chosenDimension, chosenDimension);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}
	
	// in case there is no size specified
	private int getPreferredSize() {
		return 300;
	}

	private void drawRim(Canvas canvas) {
		// first, draw the metallic body
		
		canvas.drawOval(rimRect, rimPaint);
	}
	
	private void drawFace(Canvas canvas) {		
		canvas.drawOval(faceRect, facePaint);
		// draw the inner rim circle
		canvas.drawOval(faceRect, rimCirclePaint);
	}

	private void drawScale(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		
		// draw green range 0 - 75
		LinearGradient startSharder = new LinearGradient(25,93,23,7,  
                new int[]{Color.RED,Color.GREEN},  
                null,TileMode.REPEAT);  
		scaleGreenPaint.setShader(startSharder);
		canvas.drawArc(scaleRect, valueToAngle(0f) - 90f, valueToAngle(76f) - valueToAngle(0f), false, scaleGreenPaint);
		LinearGradient endShader = new LinearGradient(22,7,75,93,  
                new int[]{Color.GREEN,Color.RED},  
                null,TileMode.REPEAT);  
		scaleGreenPaint.setShader(endShader);
		canvas.drawArc(scaleRect, valueToAngle(74f) - 90f, valueToAngle(180f) - valueToAngle(74f), false, scaleGreenPaint);
		
		scaleGreenPaint.setAlpha(255);
		
		canvas.rotate(-1f*rotateDegree, 50f, 50f);

		for (int i = 0; i <= totalNicks; ++i) {
			float y1 = scaleRect.top;
			float y2 = y1 + 3f;
			
			
			if (i % 5 == 0) { // every 10
//				canvas.drawLine(50f, y1, 50f, y2, scalePaint);
				canvas.drawLine(50f, y1, 50f, y2 + 1f, scalePaint);
				
				int value = nickToValue(i);
				String valueString = Integer.toString(value);
				
				// draw vertical text
				canvas.save(Canvas.MATRIX_SAVE_FLAG);
				canvas.rotate(-degreesPerNick * i + rotateDegree, 50f, y2 + 8f);
				if(value!=minValue&&value!=maxValue)
					canvas.drawText(valueString, 50f, y2 + 10f, scalePaint);
				canvas.restore();
			}
			
			// draw red line at 3000 RPM
//			if (i == 30)
//				canvas.drawLine(50f, y1, 50f, y2 + 5f, scaleRedPaint);
			
			canvas.rotate(degreesPerNick, 50f, 50f);
		}
		canvas.restore();		
	}
	
	private int nickToValue(int nick) {
		int rawValue = minValue + nick * (maxValue - minValue) / totalNicks;
		return rawValue;
	}
	
	private float valueToAngle(float value) {
		float valuePerNick = (float)(maxValue - minValue) / totalNicks;
		return degreesPerNick * (value - minValue) / valuePerNick - rotateDegree;
	}
	
	private void drawTitle(Canvas canvas) {
		String title = getTitle();
		canvas.drawRoundRect(titleRect, 5, 5, titlePaint);
		titlePaint.setColor(getResources().getColor(R.color.title_blue));
		canvas.drawText(title, 50f, 62f, titlePaint);
	}
	

	private void drawHand(Canvas canvas) {
		float handAngle = valueToAngle(handPosition);
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		
		canvas.drawText((int)handPosition+"", 50f, 45f, heartbeatPaint);
		canvas.rotate(handAngle, 50f, 50f);
		canvas.drawCircle(50, 50, 3, handPaint);
		canvas.drawLine(50f, 47f, 50f, 15f, handPaint);
		canvas.restore();
	}

	private void drawBackground(Canvas canvas) {
		if (background == null) {
			Log.w(TAG, "Background not created");
		} else {
			canvas.drawBitmap(background, 0, 0, backgroundPaint);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		drawBackground(canvas);

		float scale = (float) getWidth();		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale / 100f, scale / 100f);

		drawHand(canvas);
		
		canvas.restore();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(TAG, "Size changed to " + w + "x" + h);
		
		regenerateBackground();
	}
	
	private void regenerateBackground() {
		// free the old bitmap
		if (background != null) {
			background.recycle();
		}
		
		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(background);
		float scale = (float) getWidth();		
		backgroundCanvas.scale(scale / 100f, scale / 100f);
		
//		drawRim(backgroundCanvas);
		drawFace(backgroundCanvas);
		drawScale(backgroundCanvas);
		drawTitle(backgroundCanvas);		
	}

		
	public void setRPM(float value) {
		if (value < minValue) {
			value = minValue;
		} else if (value > maxValue) {
			value = maxValue;
		}
		handPosition = value;
		invalidate();
//		postInvalidate();
	}
}
