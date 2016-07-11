package com.chuannuo.tangguo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class ApproveView extends View {
	
	private int screenW,screenH;
	private int circleX,circleY;

	public ApproveView(Context context) {
		super(context);
		init(context);
	}

	public ApproveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ApproveView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		
		screenW = metric.widthPixels;
		screenH = metric.heightPixels;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

}
