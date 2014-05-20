package com.navidroidgms.model.map;

import com.navidroid.model.map.IMap.OnTouchEventHandler;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class MapEventsListener extends View {
	
	private OnTouchEventHandler handler;

	public MapEventsListener(Context context) {
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
