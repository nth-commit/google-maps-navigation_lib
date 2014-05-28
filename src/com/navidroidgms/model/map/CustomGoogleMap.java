package com.navidroidgms.model.map;

import android.graphics.Point;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.navidroid.model.LatLng;
import com.navidroid.model.PointD;
import com.navidroid.model.WhenReadyWrapper;
import com.navidroid.model.map.PolylineOptions;

public class CustomGoogleMap extends WhenReadyWrapper<ICustomGoogleMap> implements ICustomGoogleMap {
	
	private LatLng location;
	private double bearing;
	private double tilt;
	private double zoom;
	private PointD anchor;

	@Override
	public void setLocation(final LatLng location) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.setLocation(location);
			}
		});
		this.location = location;		
	}
	
	@Override
	public void setBearing(final double bearing) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.setBearing(bearing);
			}
		});
		this.bearing = bearing;
	}

	@Override
	public void setTilt(final double tilt) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.setTilt(tilt);
			}
		});
		this.tilt = tilt;
	}

	@Override
	public void setZoom(final double zoom) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.setZoom(zoom);
			}
		});
		this.zoom = zoom;
	}

	@Override
	public void setAnchor(final PointD anchor) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.setAnchor(anchor);
			}
		});
		this.anchor = anchor;
	}

	@Override
	public void setOnTouchEventHandler(final OnTouchEventHandler handler) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.setOnTouchEventHandler(handler);
			}
		});
	}

	@Override
	public void setOnUpdateEventHandler(final OnUpdate handler) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.setOnUpdateEventHandler(handler);
			}
		});
	}

	@Override
	public LatLng getLocation() {
		return whenReadyReturn(new WhenReadyReturn<ICustomGoogleMap, LatLng>() {
			@Override
			public LatLng invoke(ICustomGoogleMap object) {
				return object.getLocation();
			}
		}, location);
	}

	@Override
	public double getBearing() {
		return whenReadyReturn(new WhenReadyReturn<ICustomGoogleMap, Double>() {
			@Override
			public Double invoke(ICustomGoogleMap object) {
				return object.getBearing();
			}
		}, bearing);
	}

	@Override
	public double getTilt() {
		return whenReadyReturn(new WhenReadyReturn<ICustomGoogleMap, Double>() {
			@Override
			public Double invoke(ICustomGoogleMap object) {
				return object.getTilt();
			}
		}, tilt);
	}

	@Override
	public double getZoom() {
		return whenReadyReturn(new WhenReadyReturn<ICustomGoogleMap, Double>() {
			@Override
			public Double invoke(ICustomGoogleMap object) {
				return object.getZoom();
			}
		}, zoom);
	}

	@Override
	public PointD getAnchor() {
		return whenReadyReturn(new WhenReadyReturn<ICustomGoogleMap, PointD>() {
			@Override
			public PointD invoke(ICustomGoogleMap object) {
				return object.getAnchor();
			}
		}, anchor);
	}

	@Override
	public Point getSize() {
		return whenReadyReturn(new WhenReadyReturn<ICustomGoogleMap, Point>() {
			@Override
			public Point invoke(ICustomGoogleMap object) {
				return object.getSize();
			}
		}, new Point(0, 0));
	}

	@Override
	public void invalidate() {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.invalidate();
			}
		});
	}

	@Override
	public void invalidate(final int animationTime) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.invalidate(animationTime);
			}
		});
	}

	@Override
	public void invalidate(final int animationTime, final OnInvalidationAnimationFinished invalidationAnimationFinished) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.invalidate(animationTime, invalidationAnimationFinished);
			}
		});
	}

	@Override
	public void addPolyline(final PolylineOptions options) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.addPolyline(options);
			}
		});
	}

	@Override
	public void removePolyline() {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.removePolyline();
			}
		});
	}

	@Override
	public GoogleMap getGoogleMap() {
		return whenReadyReturn(new WhenReadyReturn<ICustomGoogleMap, GoogleMap>() {
			@Override
			public GoogleMap invoke(ICustomGoogleMap object) {
				return object.getGoogleMap();
			}
		}, null);
	}

	@Override
	public void addMarker(final MarkerOptions options, final OnMarkerCreated onMarkerCreated) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.addMarker(options, onMarkerCreated);
			}
		});
	}

	@Override
	public void setOnMarkerClickListener(final OnMarkerClickListener listener) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.setOnMarkerClickListener(listener);
			}
		});
	}

	@Override
	public void addView(final View view) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.addView(view);
			}
		});
	}

	@Override
	public void setPadding(final int left, final int top, final int right, final int bottom) {
		whenReady(new WhenReady<ICustomGoogleMap>() {
			@Override
			public void invoke(ICustomGoogleMap object) {
				object.setPadding(left, top, right, bottom);
			}
		});
	}
}
