package com.ptrprograms.geofencing.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.CompoundButton;
import android.widget.Toast;
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

	private static String FENCE_ID = "com.ptrprograms.geofence";

	private ToggleButton mToggleButton;
	private Geofence mGeofence;
	private LocationClient mLocationClient;
	private Intent mIntent;
	private PendingIntent mPendingIntent;
	private GeofenceSampleReceiver mBroadcastReceiver;
	IntentFilter mIntentFilter = new IntentFilter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		verifyPlayServices();

		mLocationClient = new LocationClient( this, this, this );
		mIntent = new Intent( this, GeofencingService.class );
		mPendingIntent = PendingIntent.getService( this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT );

		mBroadcastReceiver = new GeofenceSampleReceiver();

		mToggleButton = (ToggleButton) findViewById( R.id.geofencing_button );
		mToggleButton.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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
				Toast.makeText( this,
						"Geofencing service requires an update, please open Google Play.",
						Toast.LENGTH_SHORT ).show();
				finish();
			}
			default: {
				Toast.makeText(this,
						"Geofencing service is not available.",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void startGeofence() {
		Toast.makeText( this, "startGeofence", Toast.LENGTH_SHORT ).show();
		Location location = mLocationClient.getLastLocation();
		int radius = 100;

		Geofence.Builder builder = new Geofence.Builder();
		mGeofence = builder
				//Unique to this geofence
				.setRequestId(FENCE_ID)
						//Size and location
				.setCircularRegion(
						location.getLatitude(),
						location.getLongitude(),
						radius)
						//Events both in and out of the fence
				.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
						| Geofence.GEOFENCE_TRANSITION_EXIT)
						//Keep alive
				.setExpirationDuration(Geofence.NEVER_EXPIRE)
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
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationClient.disconnect();
	}

	public class GeofenceSampleReceiver extends BroadcastReceiver {
		/*
		 * Define the required method for broadcast receivers
		 * This method is invoked when a broadcast Intent triggers the receiver
		 */
		@Override
		public void onReceive(Context context, Intent intent) {

			Toast.makeText( context, "onReceive: " + intent.getAction().toString() , Toast.LENGTH_SHORT ).show();
			startService( mIntent );
		}
	}

	@Override
	public void onConnected(Bundle bundle) {

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onAddGeofencesResult(int status, String[] geofenceIds ) {
		Toast.makeText( this, "addGeofencesResult", Toast.LENGTH_SHORT ).show();
		if( status == LocationStatusCodes.SUCCESS ) {
			Toast.makeText( this, "addGseofencesResult SUCCESS", Toast.LENGTH_SHORT ).show();
			Intent intent = new Intent( mIntent );
			intent.setAction(GeofencingService.ACTION_INIT);
			startService( intent );
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	public void onRemoveGeofencesByRequestIdsResult(int status, String[] strings) {
		if( status == LocationStatusCodes.SUCCESS ) {
		}

		stopService( mIntent );
	}

	@Override
	public void onRemoveGeofencesByPendingIntentResult(int status, PendingIntent pendingIntent) {
		if( status == LocationStatusCodes.SUCCESS ) {
			Toast.makeText( this, "onRemoveGeofencesByPendingIntentResult SUCCESS", Toast.LENGTH_SHORT ).show();
			stopService( mIntent );
		}
	}
}