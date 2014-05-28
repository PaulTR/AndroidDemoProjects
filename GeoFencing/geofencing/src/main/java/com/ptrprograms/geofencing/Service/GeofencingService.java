package com.ptrprograms.geofencing.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.ptrprograms.geofencing.R;

/**
 * Created by PaulTR on 5/26/14.
 */
public class GeofencingService extends IntentService {

	private NotificationManager mNotificationManager;

	public GeofencingService(String name) {
		super(name);
	}

	public GeofencingService() {
		this( "Geofencing Service" );
	}

	@Override
	public IBinder onBind( Intent intent ) {
		return null;
	}

	@Override
	protected void onHandleIntent( Intent intent ) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
		builder.setSmallIcon( R.drawable.ic_launcher );
		builder.setDefaults( Notification.DEFAULT_ALL );
		builder.setOngoing( true );

		int transitionType = LocationClient.getGeofenceTransition( intent );
		if( transitionType == Geofence.GEOFENCE_TRANSITION_ENTER ) {
			builder.setContentTitle( "Geofence Transition" );
			builder.setContentText( "Entering Geofence" );
			mNotificationManager.notify( 1, builder.build() );
		}
		else if( transitionType == Geofence.GEOFENCE_TRANSITION_EXIT ) {
			builder.setContentTitle( "Geofence Transition" );
			builder.setContentText( "Exiting Geofence" );
			mNotificationManager.notify( 1, builder.build() );
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onHandleIntent( intent );
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mNotificationManager.cancel( 1 );
	}
}
