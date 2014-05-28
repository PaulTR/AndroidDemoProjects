package com.ptrprograms.geofencing.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;
import com.ptrprograms.geofencing.R;
import com.ptrprograms.geofencing.Service.GeofencingService;

import java.util.ArrayList;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		LocationClient.OnAddGeofencesResultListener,
		LocationClient.OnRemoveGeofencesResultListener {

	private final static String FENCE_ID = "com.ptrprograms.geofence";
	private final int RADIUS = 100;

	private ToggleButton mToggleButton;
	private Geofence mGeofence;
	private LocationClient mLocationClient;
	private Intent mIntent;
	private PendingIntent mPendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		verifyPlayServices();

		mLocationClient = new LocationClient( this, this, this );
		mIntent = new Intent( this, GeofencingService.class );
		mPendingIntent = PendingIntent.getService( this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT );

		mToggleButton = (ToggleButton) findViewById( R.id.geofencing_button );
		mToggleButton.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged( CompoundButton compoundButton, boolean b ) {
				if( b ) {
					startGeofence();
				} else {
					stopGeofence();
				}
			}
		});
	}

	private void verifyPlayServices() {
		switch ( GooglePlayServicesUtil.isGooglePlayServicesAvailable( this ) ) {
			case ConnectionResult.SUCCESS: {
				break;
			}
			case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: {
				finish();
			}
			default: {
				finish();
			}
		}
	}

	private void startGeofence() {
		Location location = mLocationClient.getLastLocation();

		Geofence.Builder builder = new Geofence.Builder();
		mGeofence = builder.setRequestId( FENCE_ID )
				.setCircularRegion( location.getLatitude(), location.getLongitude(), RADIUS )
				.setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT )
				.setExpirationDuration( Geofence.NEVER_EXPIRE )
				.build();

		ArrayList<Geofence> geofences = new ArrayList<Geofence>();
		geofences.add( mGeofence );
		mLocationClient.addGeofences( geofences, mPendingIntent, this );
	}

	private void stopGeofence() {
		mLocationClient.removeGeofences( mPendingIntent, this );
	}

	@Override
	protected void onResume() {
		super.onResume();
		if ( !mLocationClient.isConnected() && !mLocationClient.isConnecting() ) {
			mLocationClient.connect();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationClient.disconnect();
	}

	@Override
	public void onConnected(Bundle bundle) {

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onAddGeofencesResult(int status, String[] geofenceIds ) {
		if( status == LocationStatusCodes.SUCCESS ) {
			Intent intent = new Intent( mIntent );
			startService( intent );
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	public void onRemoveGeofencesByRequestIdsResult(int status, String[] strings) {
		if( status == LocationStatusCodes.SUCCESS ) {
			stopService( mIntent );
		}
	}

	@Override
	public void onRemoveGeofencesByPendingIntentResult(int status, PendingIntent pendingIntent) {
		if( status == LocationStatusCodes.SUCCESS ) {
			stopService( mIntent );
		}
	}

}