package com.ptrprograms.notificationscustomlayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button mLaunchNotificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main );

		mLaunchNotificationButton = (Button) findViewById( R.id.launch_notification );
		mLaunchNotificationButton.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				Intent intent = new Intent( getApplicationContext(), CustomNotificationService.class );
				intent.setAction( CustomNotificationService.ACTION_NOTIFICATION_PLAY_PAUSE );
				startService( intent );
			}
		});
    }
}