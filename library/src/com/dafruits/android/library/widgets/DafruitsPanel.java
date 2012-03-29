package com.dafruits.android.library.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dafruits.darkud.Vect;

public class DafruitsPanel extends TextView{

	private boolean roundCounterEnabled = true;
	private Bitmap roundCounterBg = null;
	private int roundCounterPadding = 10;
	private int roundCounterSize;
	private float roundCounterHeightRatio;
	
	private int Linecolor = Color.GREEN;
	private float lineAlpha = 1f;
	
	private boolean resizedForCounter = false;
	
	private int position = 0;
	private int total = 0;
	
	public DafruitsPanel(Context context) {
		this(context,null);		
	}
	
	public DafruitsPanel(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
	}
	
	public DafruitsPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
				
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DafruitsPanel);
		final int bgPanelId = a.getResourceId(R.styleable.DafruitsPanel_drawableRoundCounter, -1);
		roundCounterPadding = a.getDimensionPixelSize(R.styleable.DafruitsPanel_paddingRoundCounter, 0);		
		roundCounterEnabled = a.getBoolean(R.styleable.DafruitsPanel_EnablingRoundCounter, true);
		final float size = a.getFloat(R.styleable.DafruitsPanel_RoundCounter_height, 1);
		Linecolor = a.getInt(R.styleable.DafruitsPanel_RoundCounter_lineColor, Color.GREEN);
		lineAlpha = a.getFloat(R.styleable.DafruitsPanel_RoundCounter_LineAlpha, 1);
		
		a.recycle();	
		
		if(bgPanelId != -1)
		{
			//if a background is provided ...
			setWidgetBg(bgPanelId);
		}else
		{
			//if not we load a default one
			setWidgetBg(R.drawable.bg);
		}
		
		if(size < 0)
		{
			//No negative attitude tolerated :p
			roundCounterHeightRatio = 0;
		}else if(size > 1)
		{
			//Not too big too
			roundCounterHeightRatio = 1f;
		}else
		{
			roundCounterHeightRatio = size;
		}
		
		if(lineAlpha < 0)
		{
			//No negative attitude tolerated :p
			lineAlpha = 0;
		}else if(lineAlpha > 1)
		{
			//Not too big too
			lineAlpha = 1f;
		}
	}
	
	public void setWidgetBg(Bitmap bg)
	{
		roundCounterBg = bg;
	}
	
	public void setWidgetBg(int idWidgetBg)
	{
		//Inflate the bitmap from it s resource id
		setWidgetBg(BitmapFactory.decodeResource(getResources(), idWidgetBg));
	}
	
	public void setPosition(int pos)
	{
		//Set the current element
		position = pos;
	}
	
	public void setTotal(int total)
	{
		//Set the total of elements in the listview
		this.total = total;
	}
			
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		
		if(roundCounterEnabled && !resizedForCounter)
		{
			//If we ask to see the roundCounter and the space for him havent been calculated yet...
			int w = right - left;
			int h = bottom - top;
			
			//We calculate the size of the roundCounter. It s a portion of the height. Mrans if ratio is 1 then we take the whole height
			//else we multiply height by the ratio for the actual size
			//Since we want it to stay on the panel we divide that size by 2 for the rayon 
			
			int counterSize = (int) (h*roundCounterHeightRatio) / 2;
			roundCounterSize = counterSize;
			/*
			 * We add space by increasing padding
			 * we need to define a padding for the round counter so they dont touch
			 * 
			 * Next improvement : choose the position of the roundcounter
			 */
			
			setPadding(getPaddingLeft() + counterSize + roundCounterPadding, getPaddingTop(), getPaddingRight(), getPaddingBottom());
			resizedForCounter = true;			
			
			setMeasuredDimension(w + counterSize + roundCounterPadding, h);
			
			if(roundCounterBg !=null)
			{
				//If an Image have been provided we use it as background
				roundCounterBg = Bitmap.createScaledBitmap(roundCounterBg, counterSize, counterSize, true);
			}
		}
		
		super.onLayout(changed, left, top, right, bottom);	
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		final Paint paint = new Paint();
		super.onDraw(canvas);
			
		if(!roundCounterEnabled)
		{
			//If the round counter is not enabled no need to go ahead
			return;
		}
		
		//We get the rayon of the round counter
				
		int h = getMeasuredHeight();
		//This is a trick I used since the image dont take all the space. In truth the bg take approximatively 
		//90% of the space then i set the line size to be slight smaller
		int lineRatio = (int) ((double)roundCounterSize *0.1d);
		
		if(roundCounterBg == null)
		{
			//If there is a problem with the bg
			paint.setColor(Color.GRAY);
			canvas.drawCircle(roundCounterSize, h/2, roundCounterSize, paint);
		}else
		{			
			//Else we draw the background
			canvas.drawBitmap(roundCounterBg, roundCounterSize/2,(h - roundCounterSize)/2, paint);
		}
		
		if(total != 0)
		{
			//We calculate the percentage of elements we already passed
			float percent =  (float)position / (float)total;
			percent = percent * 100;
			
			//We define the center of the round counter
			Vect center = new Vect(roundCounterSize, h/2);
			
			//And the initial position of the line  
			Vect point = new Vect(roundCounterSize/2 + lineRatio, h/2);
						
			//the color of the bar and its alpha
			paint.setColor(Linecolor);
			paint.setAlpha((int) (lineAlpha * 255));
			
			//Now we draw the first line
			canvas.drawLine(roundCounterSize, h/2, (float)point.x, (float)point.y, paint);
			
			for(int i = 0;i < percent;i++)
			{				
				//we will draw all the lines till we get to the desired angle
				double angle = (float) ((i * 2 * Math.PI)/100);
				
				point = new Vect(roundCounterSize/2 + lineRatio, h/2);						
				
				point = point.rotate(center, angle);
				
				canvas.drawLine(roundCounterSize, h/2, (float)point.x, (float)point.y, paint);
			}			
		}
		
	}

	
@Override
protected void finalize() throws Throwable {
	//We free the image we used
	roundCounterBg.recycle();
	super.finalize();
}
	

}
