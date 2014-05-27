package com.ptrprograms.videoplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

/**
 * Created by PaulTR on 5/25/14.
 */
public class VideoPlayerActivity extends Activity {

	public static final String EXTRA_VIDEO_URL = "video_url";
	protected static final String EXTRA_IS_PLAYING = "is_playing";
	protected static final String EXTRA_CURRENT_POSITION = "current_position";

	protected MediaController mMediaController;
	protected MediaPlayer mMediaPlayer;
	protected VideoView mVideoView;
	protected ProgressBar mSpinningProgressBar;
	protected Uri mUri;
	protected int mPosition;
	private String TAG = VideoPlayerActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_videoplayer );

		if( getIntent() == null || getIntent().getExtras() == null || TextUtils.isEmpty( getIntent().getExtras().getString( EXTRA_VIDEO_URL ) ) ) {
			showErrorMessage(0, 0);
			return;
		}

		initViews();
		setupMediaController();
	}

	protected void initViews() {
		mSpinningProgressBar = (ProgressBar) findViewById( R.id.progress_spinner );

		mVideoView = (VideoView) findViewById( R.id.video_view );
		mVideoView.setOnCompletionListener( onCompletionListener );
		mVideoView.setOnErrorListener( onErrorListener );
		mVideoView.setOnPreparedListener( onPreparedListener );

		if( mVideoView == null ) {
			throw new IllegalArgumentException( "Layout must contain a video view with ID video_view" );
		}

		mUri = Uri.parse( getIntent().getExtras().getString( EXTRA_VIDEO_URL ) );
		mVideoView.setVideoURI( mUri );
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		releaseVideoListeners();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if( mVideoView == null || mVideoView.getCurrentPosition() == 0 )
			return;

		outState.putInt( EXTRA_CURRENT_POSITION, mVideoView.getCurrentPosition() );
		outState.putBoolean( EXTRA_IS_PLAYING, mVideoView.isPlaying() );
		mVideoView.pause();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if( mVideoView == null || savedInstanceState == null )
			return;

		if( savedInstanceState.getBoolean( EXTRA_IS_PLAYING, false ) ) {
			mVideoView.seekTo(savedInstanceState.getInt(EXTRA_CURRENT_POSITION, 0));
			mVideoView.start();
		}
	}

	protected void setupMediaController() {
		mMediaController = new MediaController( this );
		mMediaController.setEnabled(true);
		mMediaController.show();
		mMediaController.setMediaPlayer( mVideoView );
	}

	protected void releaseVideoListeners() {
		mVideoView.setOnCompletionListener( null );
		mVideoView.setOnErrorListener( null );
		mVideoView.setOnPreparedListener( null );
	}

	@Override
	protected void onResume() {
		super.onResume();
		mVideoView.seekTo( mPosition );
		mSpinningProgressBar.setVisibility( View.VISIBLE );
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPosition = mVideoView.getCurrentPosition();
	}

	protected MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			mVideoView.seekTo( 0 );
			if( mVideoView.isPlaying() )
				mVideoView.pause();

			if( !mMediaController.isShowing() )
				mMediaController.show();
		}
	};

	protected MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
		@Override
		public boolean onError( MediaPlayer mediaPlayer, int what, int extra ) {
			try {
				mVideoView.stopPlayback();
			} catch( IllegalStateException e ) {
				Log.e( TAG, e.getStackTrace().toString() );
			}

			showErrorMessage( what, extra );

			return true;
		}
	};

	protected void showErrorMessage( int what, int extra ) {
		String errorMessage;
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		errorMessage = getResources().getString( R.string.default_video_error );

		builder.setMessage( errorMessage ).setTitle( getResources().getString( R.string.default_video_error_title ) );

		builder.setNegativeButton(getResources().getString(R.string.default_negative_button), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				finish();
			}
		});

		builder.setCancelable( false );
		AlertDialog errorDialog = builder.create();
		errorDialog.show();

	}

	protected MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mediaPlayer) {
			if( mediaPlayer == null )
				return;
			mMediaPlayer = mediaPlayer;
			mediaPlayer.start();
			if( mSpinningProgressBar != null )
				mSpinningProgressBar.setVisibility( View.GONE );

			mVideoView.setMediaController( mMediaController );
		}
	};
}