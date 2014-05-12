package com.gmnav.model.positioning;

import com.google.android.gms.maps.model.LatLng;

public class DebugSimulatedGps extends AbstractSimulatedGps {
	
	public DebugSimulatedGps(LatLng location) {
		super(location);
	}

	@Override
	public void doFollowPath() {
		currentPosition = new Position(currentPosition.location, 0, System.currentTimeMillis());
		onTickHandler.invoke(currentPosition);
		whileHasCurrentPath(new WhileHasCurrentPathAction() {
			@Override
			public void invoke() {
				advancePosition(currentPath);
				onTickHandler.invoke(currentPosition);	
			}
		});
	}
}
