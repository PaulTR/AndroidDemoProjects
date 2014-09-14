package com.ptrprograms.wear;

import android.app.ListFragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.preview.support.wearable.notifications.*;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;

/**
 * Created by PaulTR.
 */
public class WearList extends ListFragment {

	private static final String EXTRA_STACKED_GROUP = "EXTRA_GROUP_STACKED_NOTIFICATIONS";

	private int notificationId = 1;
	private int stackedId = 600;

	private NotificationManagerCompat mNotificationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<String> items = Arrays.asList( getResources().getStringArray( R.array.wear_notification_types ) );

		setListAdapter( new ArrayAdapter( getActivity(), android.R.layout.simple_list_item_1, items ) );

		mNotificationManager = NotificationManagerCompat.from(getActivity());
	}

	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		super.onListItemClick(list, v, position, id);

		String item = list.getAdapter().getItem( position ).toString();
		if( item.equalsIgnoreCase( getString( R.string.notification_basic ) ) ) {
			showBasicWearNotification();
		} else if( item.equalsIgnoreCase( getString( R.string.notification_add_action ) ) ) {
			showWearActionNotification();
		} else if( item.equalsIgnoreCase( getString( R.string.notification_quick_replies ) ) ) {
			showQuickRepliesNotification();
		} else if( item.equalsIgnoreCase( getString( R.string.notification_pages ) ) ) {
			showMultiPageWearNotification();
		} else if( item.equalsIgnoreCase( getString( R.string.notification_stacked ) ) ) {
			showStackedWearNotification();
		}
	}

	private void showBasicWearNotification() {
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( getActivity() )
				.setSmallIcon( R.drawable.ic_launcher )
				.setLargeIcon( BitmapFactory.decodeResource( getResources(), R.drawable.batman_punching_shark ) )
				.setContentText( getString( R.string.big_content_summary ) )
				.setContentTitle( getString( R.string.notification_basic ) );

		Notification notification =
				new WearableNotifications.Builder( notificationBuilder )
						.setHintHideIcon(true)
						.build();


		mNotificationManager.notify( ++notificationId, notification );
	}

	private void showWearActionNotification() {
		Intent intent = new Intent( Intent.ACTION_VIEW );
		intent.setData( Uri.parse( "http://ptrprograms.blogspot.com" ) );
		PendingIntent pendingIntent = PendingIntent.getActivity( getActivity(), 0, intent, 0 );

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( getActivity() )
				.setSmallIcon( R.drawable.ic_launcher )
				.setLargeIcon( BitmapFactory.decodeResource( getResources(), R.drawable.batman_punching_shark ) )
				.setContentText( getString( R.string.big_content_summary ) )
				.setContentTitle( getString( R.string.notification_add_action ) )
				.addAction( R.drawable.ic_launcher, "Launch Blog", pendingIntent );

		Notification notification =
				new WearableNotifications.Builder( notificationBuilder )
						.setHintHideIcon( true )
						.build();

		mNotificationManager.notify( ++notificationId, notification );
	}

	private void showQuickRepliesNotification() {

		Intent intent = new Intent( getActivity(), MainActivity.class );
		PendingIntent pendingIntent = PendingIntent.getActivity( getActivity(), 0, intent, 0 );

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( getActivity() )
				.setSmallIcon( R.drawable.ic_launcher )
				.setLargeIcon( BitmapFactory.decodeResource( getResources(), R.drawable.batman_punching_shark ) )
				.setContentText( getString( R.string.big_content_summary ) )
				.setContentTitle( getString( R.string.notification_quick_replies ) )
				.setContentIntent( pendingIntent );

		String replyLabel = "Transportation";
		String[] replyChoices = getResources().getStringArray( R.array.getting_around );

		RemoteInput remoteInput = new RemoteInput.Builder( "extra_replies" )
				.setLabel(replyLabel)
				.setChoices(replyChoices)
				.build();

		Notification notification =
				new WearableNotifications.Builder( notificationBuilder )
						.setHintHideIcon( true )
						.addRemoteInputForContentIntent( remoteInput )
						.build();

		mNotificationManager.notify( ++notificationId, notification );
	}

	private void showMultiPageWearNotification() {
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(getActivity())
						.setSmallIcon( R.drawable.ic_launcher )
						.setContentTitle( "Page 1" )
						.setContentText( "Short message" );

		NotificationCompat.BigTextStyle additionalPageStyle = new NotificationCompat.BigTextStyle();
		additionalPageStyle.setBigContentTitle( "Page 2" );

		Notification secondPageNotification =
				new NotificationCompat.Builder( getActivity() )
						.setStyle( additionalPageStyle )
						.build();

		additionalPageStyle.setBigContentTitle( "Page 3" );
		Notification thirdPageNotification =
				new NotificationCompat.Builder( getActivity() )
						.setStyle( additionalPageStyle )
						.build();

		additionalPageStyle.setBigContentTitle( "Page 4" );

		Notification fourthPageNotification =
				new NotificationCompat.Builder( getActivity() )
						.setStyle( additionalPageStyle )
						.build();

		List<Notification> list = new ArrayList<Notification>();
		list.add( secondPageNotification );
		list.add( thirdPageNotification );
		list.add( fourthPageNotification );
		Notification twoPageNotification =
				new WearableNotifications.Builder(notificationBuilder)
						.addPages(list)
						.build();

		mNotificationManager.notify( ++notificationId, twoPageNotification );
	}

	private void showStackedWearNotification() {

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( getActivity() )
				.setSmallIcon( R.drawable.ic_launcher )
				.setLargeIcon( BitmapFactory.decodeResource( getResources(), R.drawable.batman_punching_shark ) )
				.setContentText( getString( R.string.big_content_summary ) )
				.setContentTitle( getString( R.string.notification_stacked ) );

		Notification notification =
				new WearableNotifications.Builder( notificationBuilder )
						.setHintHideIcon(true)
						.setGroup( EXTRA_STACKED_GROUP, ++stackedId )
						.build();

		Notification notification2 =
				new WearableNotifications.Builder( notificationBuilder )
						.setHintHideIcon(true)
						.setGroup( EXTRA_STACKED_GROUP, ++stackedId )
						.build();

		Notification summaryNotification = new WearableNotifications.Builder( notificationBuilder )
				.setGroup( EXTRA_STACKED_GROUP, WearableNotifications.GROUP_ORDER_SUMMARY )
				.build();

		mNotificationManager.notify( ++notificationId, notification );
		mNotificationManager.notify( ++notificationId, notification2 );
		mNotificationManager.notify( ++notificationId, summaryNotification );
	}
}
