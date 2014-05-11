package com.gmnav;

import java.util.ArrayList;
import java.util.List;

import com.gmnav.R;
import com.gmnav.model.map.MapEventsListener;
import com.gmnav.model.map.NavigationMap;
import com.gmnav.model.navigation.DefaultNavigatorStateListener;
import com.gmnav.model.navigation.INavigatorStateListener;
import com.gmnav.model.navigation.InternalNavigator;
import com.gmnav.model.navigation.NavigationOptions;
import com.gmnav.model.navigation.Navigator;
import com.gmnav.model.positioning.DebugSimulatedGps;
import com.gmnav.model.positioning.Gps;
import com.gmnav.model.positioning.IGps;
import com.gmnav.model.positioning.SimulatedGps;
import com.gmnav.model.positioning.GpsOptions.GpsType;
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

public class NavigationFragment extends Fragment implements
	ConnectionCallbacks,
	OnConnectionFailedListener  {
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	private Navigator navigator = new Navigator();

	private NavigationOptions options;
	private InternalNavigator internalNavigator;
	private Activity parent;
	private LocationClient locationClient;
	private NavigationMap map;
	private IGps gps;
	
	private static List<NavigationOptions> optionsById = new ArrayList<NavigationOptions>();
	
	public static final NavigationFragment newInstance(NavigationOptions options) {
		NavigationFragment fragment = new NavigationFragment();
		int id = optionsById.size();
		optionsById.add(id, options);
		Bundle args = new Bundle();
		args.putInt("index", id);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int id = getArguments().getInt("index");
		options = optionsById.get(id);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.navigation_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
		MapEventsListener mapEventsListener = (MapEventsListener)getView().getRootView().findViewById(R.id.map_events_listener_view);
		map = new NavigationMap(mapFragment, mapEventsListener, options.mapOptions());
		
		if (options.gpsOptions().gpsType() == GpsType.REAL) {
			locationClient = new LocationClient(parent, this, this);
			locationClient.connect();
		} else {
			createGps();
			createNavigator();
		}
		
		super.onActivityCreated(savedInstanceState);
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
		createGps();
		createNavigator();			
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}
	
	public Navigator getNavigator() {
		return navigator;
	}
	
	private void createGps() {
		if (options.gpsOptions().gpsType() == GpsType.SIMULATED) {
			if (options.gpsOptions().debugMode()) {
				gps = new DebugSimulatedGps(Defaults.LOCATION);
			} else {
				gps = new SimulatedGps(Defaults.LOCATION);
			}
		} else {
			gps = new Gps(locationClient);
		}
	}
	
	private void createNavigator() {
		internalNavigator = new InternalNavigator(this, gps, map, options.vehicleOptions());
		INavigatorStateListener stateListener = new DefaultNavigatorStateListener(this);
		internalNavigator.addNavigatorStateListener(stateListener);
		navigator.setInternalNavigator(internalNavigator);
	}
}

