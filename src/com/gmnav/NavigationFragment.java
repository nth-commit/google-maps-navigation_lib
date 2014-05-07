package com.gmnav;

import java.util.ArrayList;
import java.util.List;

import com.gmnav.R;
import com.gmnav.model.directions.Direction;
import com.gmnav.model.map.MapEventsListener;
import com.gmnav.model.map.NavigationMap;
import com.gmnav.model.navigation.DefaultNavigatorStateListener;
import com.gmnav.model.navigation.INavigatorStateListener;
import com.gmnav.model.navigation.NavigationOptions;
import com.gmnav.model.navigation.Navigator;
import com.gmnav.model.positioning.DebugSimulatedGps;
import com.gmnav.model.positioning.Gps;
import com.gmnav.model.positioning.IGps;
import com.gmnav.model.positioning.SimulatedGps;
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
	
	public interface OnNavigatorReady {
		void invoke(Navigator navigator);
	}
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;	

	private NavigationOptions options;
	private OnNavigatorReady onNavigatorReady;
	private Activity parent;
	private LocationClient locationClient;
	private NavigationMap map;
	private IGps gps;
	private Navigator navigator;
	
	private static List<NavigationOptions> optionsById = new ArrayList<NavigationOptions>();
	private static List<OnNavigatorReady> readyEventsById = new ArrayList<OnNavigatorReady>();
	
	public static final NavigationFragment newInstance(NavigationOptions options, OnNavigatorReady onNavigatorReady) {
		NavigationFragment fragment = new NavigationFragment();
		int id = optionsById.size();
		optionsById.add(id, options);
		readyEventsById.add(id, onNavigatorReady);
		Bundle args = new Bundle();
		args.putInt("index", id);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int id = getArguments().getInt("index");
		options = optionsById.get(id);
		onNavigatorReady = readyEventsById.get(id);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		navigator = new Navigator(gps, map, options.vehicleOptions());
		INavigatorStateListener stateListener = new DefaultNavigatorStateListener(this);
		navigator.addNavigatorStateListener(stateListener);
		onNavigatorReady.invoke(navigator);				
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

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}
	
	public Navigator getNavigator() {
		return navigator;
	}
}

