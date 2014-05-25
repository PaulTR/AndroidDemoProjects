package com.ptrprograms.videoplayer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

	private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main );

		initViews();
    }

	private void initViews() {
		mButton = ( Button ) findViewById( R.id.play_video_button );
		mButton.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				launchVideoPlayer();
			}
		});
	}

	private void launchVideoPlayer() {
		Intent i = new Intent( this, VideoPlayerActivity.class );
		i.putExtra( VideoPlayerActivity.EXTRA_VIDEO_URL, "http://www.pocketjourney.com/downloads/pj/video/famous.3gp" );
		startActivity( i );
	}
}
