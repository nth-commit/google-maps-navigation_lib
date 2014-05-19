package com.gmnav.model.google;

import com.gmnav.model.map.IMap.OnTouchEventHandler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GoogleMapEventsListener extends View {
	
	private OnTouchEventHandler handler;

	public GoogleMapEventsListener(Context context) {
		super(context);
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
