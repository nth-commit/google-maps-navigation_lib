package com.gmnav.model.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MapEventsListener extends View {
	
	public interface OnTouchEventHandler {
		void invoke();
	}
	
	private OnTouchEventHandler handler;

	public MapEventsListener(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (handler != null) {
			handler.invoke();
		}
		return super.onTouchEvent(event);
	}
	
	public void setOnTouchEventHandler(OnTouchEventHandler handler) {
		this.handler = handler;
	}
}
