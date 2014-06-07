package com.navidroidgms.model.map;

import com.navidroid.model.map.IMap;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class MapEventsListener extends View {
	
	private IMap.OnTouchListener onTouchListener;
	
	public MapEventsListener(Context context) {
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (onTouchListener != null) {
			onTouchListener.invoke();
		}
		return super.onTouchEvent(event);
	}
	
	public void setOnTouchListener(IMap.OnTouchListener listener) {
		onTouchListener = listener;
	}
}
