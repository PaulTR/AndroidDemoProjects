package com.ptrprograms.notificationscustomlayout;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * Created by PaulTR on 4/27/14.
 */
public class CustomNotificationService extends Service {

	public static final String ACTION_NOTIFICATION_PLAY_PAUSE = "action_notification_play_pause";
	public static final String ACTION_NOTIFICATION_FAST_FORWARD = "action_notification_fast_forward";
	public static final String ACTION_NOTIFICATION_REWIND = "action_notification_rewind";
	private boolean mIsPlaying = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void handleIntent( Intent intent ) {
		if( intent != null && intent.getAction() != null ) {
			if( intent.getAction().equalsIgnoreCase( ACTION_NOTIFICATION_PLAY_PAUSE ) ) {
				mIsPlaying = !mIsPlaying;
				showNotification(mIsPlaying);
			} else if( intent.getAction().equalsIgnoreCase( ACTION_NOTIFICATION_FAST_FORWARD ) ) {
				//fast forward function
			} else if( intent.getAction().equalsIgnoreCase( ACTION_NOTIFICATION_REWIND ) ) {
				//rewind action
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleIntent( intent );
		return super.onStartCommand(intent, flags, startId);
	}

	private void showNotification( boolean isPlaying ) {
		Notification notification = new NotificationCompat.Builder( getApplicationContext() )
				.setAutoCancel( true )
				.setSmallIcon( R.drawable.ic_launcher )
				.setContentTitle( getString( R.string.app_name ) )
				.build();

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
			notification.bigContentView = getExpandedView( isPlaying );

		NotificationManager manager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
		manager.notify( 1, notification );
	}

	private RemoteViews getExpandedView( boolean isPlaying ) {
		RemoteViews customView = new RemoteViews( getPackageName(), R.layout.view_notification );

		customView.setImageViewResource( R.id.large_icon, R.drawable.ic_launcher );
		customView.setImageViewResource( R.id.ib_rewind, R.drawable.ic_rewind );

		if( isPlaying )
			customView.setImageViewResource( R.id.ib_play_pause, R.drawable.ic_pause );
		else
			customView.setImageViewResource( R.id.ib_play_pause, R.drawable.ic_play );

		customView.setImageViewResource( R.id.ib_fast_forward, R.drawable.ic_fast_forward );

		Intent intent = new Intent( getApplicationContext(), CustomNotificationService.class );
		
		intent.setAction( ACTION_NOTIFICATION_PLAY_PAUSE );
		PendingIntent pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
		customView.setOnClickPendingIntent( R.id.ib_play_pause, pendingIntent );

		intent.setAction( ACTION_NOTIFICATION_FAST_FORWARD );
		pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
		customView.setOnClickPendingIntent( R.id.ib_fast_forward, pendingIntent );

		intent.setAction( ACTION_NOTIFICATION_REWIND );
		pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
		customView.setOnClickPendingIntent( R.id.ib_rewind, pendingIntent );

		return customView;
	}

}
