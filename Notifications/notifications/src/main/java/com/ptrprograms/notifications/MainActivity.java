package com.ptrprograms.notifications;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class MainActivity extends Activity {

	private Button mNotificationBuildButton;
	private EditText mNotificationTitleEditText;
	private EditText mNotificationTextEditText;
	private EditText mNotificationSubEditText;
	private EditText mNotificationTickerEditText;
	private EditText mNotificationInfoEditText;
	private CheckBox mNotificationLargeCheckBox;
	private CheckBox mNotificationSoundCheckBox;
	private CheckBox mNotificationShowChronometerCheckBox;
	private CheckBox mNotificationVibrationCheckBox;
	private CheckBox mNotificationUseIntent;
	private CheckBox mNotificationBigPictureStyleCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		initViews();
    }

	private void initViews() {
		mNotificationBuildButton = (Button) findViewById( R.id.notification_build );
		mNotificationTitleEditText = (EditText) findViewById( R.id.notification_title );
		mNotificationTextEditText = (EditText) findViewById( R.id.notification_text );
		mNotificationSubEditText = (EditText) findViewById( R.id.notification_sub );
		mNotificationTickerEditText = (EditText) findViewById( R.id.notification_ticker );
		mNotificationInfoEditText = (EditText) findViewById( R.id.notification_info );
		mNotificationLargeCheckBox = (CheckBox) findViewById( R.id.notification_use_large_icon );
		mNotificationSoundCheckBox = (CheckBox) findViewById( R.id.notification_use_sound );
		mNotificationShowChronometerCheckBox = (CheckBox) findViewById( R.id.notification_use_chronometer );
		mNotificationVibrationCheckBox = (CheckBox) findViewById( R.id.notification_use_vibration );
		mNotificationUseIntent = (CheckBox) findViewById( R.id.notification_use_intent );
		mNotificationBigPictureStyleCheckBox = (CheckBox) findViewById( R.id.notification_use_big_picture );
		mNotificationBuildButton.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				buildNotification();
			}
		});
	}

	private void buildNotification() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		if ( !TextUtils.isEmpty(mNotificationTitleEditText.getText() ) ) {
			builder.setContentTitle(mNotificationTitleEditText.getText());
		} else {
			builder.setContentTitle(getString(R.string.app_name));
		}
		if( !TextUtils.isEmpty( mNotificationTextEditText.getText() ) ) {
			builder.setContentText(mNotificationTextEditText.getText());
		} else {
			builder.setContentText( getString(R.string.app_name) );
		}
		builder.setSmallIcon( R.drawable.ic_launcher );

		if( !TextUtils.isEmpty( mNotificationInfoEditText.getText() ) )
			builder.setContentInfo( mNotificationInfoEditText.getText() );

		if( !TextUtils.isEmpty( mNotificationSubEditText.getText() ) )
			builder.setSubText(mNotificationSubEditText.getText());

		if( !TextUtils.isEmpty( mNotificationTickerEditText.getText() ) )
			builder.setTicker(mNotificationTickerEditText.getText());

		if( mNotificationLargeCheckBox.isChecked() )
			builder.setLargeIcon(BitmapFactory.decodeResource( this.getResources(), R.drawable.ic_launcher ) );

		if( mNotificationSoundCheckBox.isChecked() )
			builder.setSound( RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION ) );

		if( mNotificationShowChronometerCheckBox.isChecked() )
			builder.setUsesChronometer( true );

		if( mNotificationVibrationCheckBox.isChecked() )
			builder.setVibrate( new long[]{ 0, 500, 250, 500 } );

		if( mNotificationUseIntent.isChecked() ) {
			Intent intent = new Intent( this, MainActivity.class );
			TaskStackBuilder stackBuilder = TaskStackBuilder.create( this );
			stackBuilder.addNextIntent( intent );
			PendingIntent resultIntent =  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent( resultIntent );
			builder.setAutoCancel( true );
		}

		if( mNotificationBigPictureStyleCheckBox.isChecked() ) {
			NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
			style.bigPicture(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher));
			builder.setStyle(style);
		}

		NotificationManager manager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
		manager.notify( 1, builder.build() );
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
