package com.gmnav.navigation;

import java.util.ArrayList;
import java.util.List;

import com.gmnav.Defaults;
import com.gmnav.R;
import com.gmnav.directions.Direction;
import com.gmnav.map.MapEventsListener;
import com.gmnav.map.NavigationMap;
import com.gmnav.positioning.DebugSimulatedGps;
import com.gmnav.positioning.Gps;
import com.gmnav.positioning.IGps;
import com.gmnav.positioning.SimulatedGps;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.MapFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class NavigationFragment extends Fragment implements
	ConnectionCallbacks,
	OnConnectionFailedListener  {
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;	

	private NavigationEvents events;
	private NavigationOptions options;
	private Activity parent;
	private LocationClient locationClient;
	private NavigationMap map;
	private IGps gps;
	private Navigator navigator;
	private NavigatorEvents internalEvents;
	
	private static List<NavigationEvents> eventsById = new ArrayList<NavigationEvents>();
	private static List<NavigationOptions> optionsById = new ArrayList<NavigationOptions>();
	
	public static final NavigationFragment newInstance(NavigationEvents events, NavigationOptions options) {
		NavigationFragment fragment = new NavigationFragment();
		int id = eventsById.size();
		eventsById.add(id, events);
		optionsById.add(id, options);
		Bundle args = new Bundle();
		args.putInt("index", id);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int id = getArguments().getInt("index");
		events = eventsById.get(id);
		options = optionsById.get(id);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.navigation_fragment, container, false);
	}
	
	@Override
	public void onStart() {
		parent = getActivity();
		parent.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
		MapEventsListener mapEventsListener = (MapEventsListener)getView().getRootView().findViewById(R.id.map_events_listener_view);
		map = new NavigationMap(mapFragment, mapEventsListener, options.mapOptions());
		locationClient = new LocationClient(parent, this, this);
		locationClient.connect();
		super.onStart();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(parent, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException ex) {
				ex.printStackTrace();
			}
		} else {
			// TODO: Handler errors with dialog
		}
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		gps = createGps();
		internalEvents = createNavigatorEvents();
		navigator = new Navigator(gps, map, options.vehicleOptions(), internalEvents);
		events.OnNavigatorReady(navigator);				
	}
	
	// TODO: Extract to SimulatedNavigationFragment
	private IGps createGps() {
		final boolean USE_SIMULATED_GPS = true;
		final boolean USE_DEBUG_SIMULATED_GPS = true;
		if (USE_SIMULATED_GPS) {
			return new SimulatedGps(Defaults.LOCATION);
		} else if (USE_DEBUG_SIMULATED_GPS) {
			return new DebugSimulatedGps(Defaults.LOCATION);
		} else {
			return new Gps(locationClient);
		}
	}
	
	private NavigatorEvents createNavigatorEvents() {
		return new NavigatorEvents() {
			@Override
			public void OnVehicleOffRoute() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnUpdate(UpdateEventArgs args) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnNewDirection(Direction direction) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnArrival() {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}
	
	public Navigator getNavigator() {
		return navigator;
	}
}

