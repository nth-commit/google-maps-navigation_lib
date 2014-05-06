package com.gmnav.positioning;

import java.util.List;

import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;

public class SimulatedGps extends AbstractSimulatedGps {
	
	public SimulatedGps(LatLng location) {
		super(location);
	}

	public void followPath(final List<LatLng> path) {
		AsyncTask<Void, Void, Void> tickLoopTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				currentPosition = new GpsPosition(currentPosition.location, 0, System.currentTimeMillis());
				publishProgress();
				while (path.size() > 0) {
					advancePosition(path);
					publishProgress();
					try {
						Thread.sleep(TICK_MS);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}					
				}
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Void... progress) {
				onTickHandler.invoke(currentPosition);
			}
		};
		tickLoopTask.execute();
	}
}
