package com.gmnav.model.positioning;

public abstract class AbstractGps implements IGps {
	
	protected OnTickHandler onTickHandler;
	
	@Override
	public void onTick(OnTickHandler onTickHandler) {
		this.onTickHandler = onTickHandler;
	}
}
