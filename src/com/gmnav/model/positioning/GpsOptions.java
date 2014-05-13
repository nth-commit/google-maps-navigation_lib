package com.gmnav.model.positioning;

public class GpsOptions {
	
	public enum GpsType {
		REAL,
		SIMULATED
	}
	
	private GpsType gpsType = GpsType.REAL;
	private boolean debugMode = false;
	private int updateIntervalMilliseconds = 500;
	
	public GpsOptions gpsType(GpsType gpsType) {
		this.gpsType = gpsType;
		return this;
	}
	
	public GpsType gpsType() {
		return gpsType;
	}
	
	public GpsOptions debugMode(boolean debugMode) {
		this.debugMode = debugMode;
		return this;
	}
	
	public boolean debugMode() {
		return debugMode;
	}
	
	public GpsOptions updateIntervalMilliseconds(int updateIntervalMilliseconds) {
		this.updateIntervalMilliseconds = updateIntervalMilliseconds;
		return this;
	}
	
	public int updateIntervalMilliseconds() {
		return updateIntervalMilliseconds;
	}
}
