package com.ptrprograms.maps.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ptrprograms.maps.R;
import com.ptrprograms.maps.interfaces.mapListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PaulTR on 2/9/14.
 */
public class PTRMapFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private static final String TAG = PTRMapFragment.class.getSimpleName();

	private final static String SAVED_STATE_LONG = "LONG";
	private final static String SAVED_STATE_LAT = "LAT";
	private final static String SAVED_STATE_ZOOM = "ZOOM";
	private final static String SAVED_STATE_TILT = "TILT";
	private final static String SAVED_STATE_BEARING = "BEARING";
	private final static String EXTRAS_SHARED_PREFERENCES = "SAVEDPREFERENCES";

	private GoogleMap mMap;
	private LocationClient mLocationClient;
	private mapListener mCallback;
	private List<Marker> markerLocations = new ArrayList<Marker>();

	public static PTRMapFragment newInstance() {
		return new PTRMapFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocationClient = new LocationClient( getActivity(), this, this );
	}

	public View onCreateView( LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState ) {
		View v = null;
		if( playServicesUnavailable() ) {
			if( mCallback != null )
				mCallback.playServicesUnavailable();
			return null;
		} else {
			try {
				v = inflater.inflate( R.layout.fragment_map, null, false );
			} catch ( InflateException e ) {

			}
		}

		mMap = ( (SupportMapFragment) getFragmentManager()
			.findFragmentById( R.id.map) )
			.getMap();

		return v;
	}
	

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState ) {
		super.onViewCreated(view, savedInstanceState);

		initializeMap();
	}

	protected void initializeMap() {
		if( mMap == null )
			return;

		mMap.setTrafficEnabled( true );
		mMap.setMapType( GoogleMap.MAP_TYPE_HYBRID );
		mMap.setMyLocationEnabled( true );
		mMap.setIndoorEnabled( true );
		mMap.setMyLocationEnabled( true );

		mMap.setOnMapLongClickListener( new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {
				if( mCallback == null )
					return;

				mCallback.longClickedMap( latLng );
			}
		});

		mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick( Marker marker ) {
				if ( marker == null )
					return false;
				marker.remove();
				if ( markerLocations.contains( marker ) )
					markerLocations.remove( marker );
				return true;
			}
		});
	}

	public void addMarker( LatLng latLng ) {
		if( latLng == null )
			return;

		Geocoder geocoder = new Geocoder( getActivity() );
		String address;
		try {
			address = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ).get( 0 ).getAddressLine( 0 );
		} catch( IOException e ) {
			address = "";
		}
		Log.e( TAG, address );
		addMarker( 0, latLng, address );
	}

	public void addMarker( float color, LatLng latLng, String title ) {
		if( mMap == null )
			mMap = ( (SupportMapFragment) getFragmentManager()
					.findFragmentById( R.id.map ) )
					.getMap();

		if( latLng == null || mMap == null )
			return;

		MarkerOptions markerOptions = new MarkerOptions().position( latLng );
		if( !title.isEmpty() )
			markerOptions.title( title );

		if( color == 0 )
			color = BitmapDescriptorFactory.HUE_RED;

		markerOptions.icon( BitmapDescriptorFactory.defaultMarker( color ) );
		Marker marker = mMap.addMarker( markerOptions );
		if( !markerLocations.contains( marker ) )
			markerLocations.add( marker );

		marker.showInfoWindow();
	}

	protected boolean playServicesUnavailable() {
		 return !( GooglePlayServicesUtil
				 .isGooglePlayServicesAvailable( getActivity() ) == ConnectionResult.SUCCESS );
	}

	private void setInitialCameraPosition() {
		double lng, lat;
		float tilt, bearing, zoom;

		SharedPreferences settings = getActivity().getSharedPreferences( EXTRAS_SHARED_PREFERENCES, 0 );
		lng = Double.longBitsToDouble( settings.getLong( SAVED_STATE_LONG, Double.doubleToLongBits( mLocationClient.getLastLocation().getLongitude() ) ) );
		lat = Double.longBitsToDouble( settings.getLong( SAVED_STATE_LAT, Double.doubleToLongBits( mLocationClient.getLastLocation().getLatitude() ) ) );
		zoom = settings.getFloat( SAVED_STATE_ZOOM, 17 );
		bearing = settings.getFloat( SAVED_STATE_BEARING, 0 );
		tilt = settings.getFloat( SAVED_STATE_TILT, 30 );

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target( new LatLng( lat, lng) )
				.zoom( zoom )
				.bearing( bearing )
				.tilt( tilt )
				.build();
		if( cameraPosition == null || mMap == null )
			return;
		mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach( activity );
		try {
			mCallback = (mapListener) getActivity();
		} catch ( ClassCastException e ) {
			throw new ClassCastException( getActivity().toString()
					+ " must implement onPlayServicesUnavailableListener" ) ;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		mLocationClient.disconnect();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if( mMap == null || mMap.getCameraPosition() == null )
			return;
		CameraPosition cam = mMap.getCameraPosition();
		SharedPreferences shared = getActivity().getSharedPreferences( EXTRAS_SHARED_PREFERENCES, 0 );
		SharedPreferences.Editor editor = shared.edit();

		editor.putLong( SAVED_STATE_LAT, Double.doubleToLongBits( cam.target.latitude ) );
		editor.putLong( SAVED_STATE_LONG, Double.doubleToLongBits( cam.target.longitude ) );
		editor.putFloat( SAVED_STATE_TILT, cam.tilt );
		editor.putFloat( SAVED_STATE_BEARING, cam.bearing );
		editor.putFloat( SAVED_STATE_ZOOM, cam.zoom );
		editor.commit();

	}

	@Override
	public void onConnected( Bundle bundle ) {
		setInitialCameraPosition();
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onConnectionFailed( ConnectionResult connectionResult ) {

	}
}
