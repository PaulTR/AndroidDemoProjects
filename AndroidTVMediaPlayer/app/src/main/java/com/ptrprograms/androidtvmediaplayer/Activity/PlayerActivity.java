package com.ptrprograms.androidtvmediaplayer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.ptrprograms.androidtvmediaplayer.Fragment.VideoDetailsFragment;
import com.ptrprograms.androidtvmediaplayer.Model.Movie;
import com.ptrprograms.androidtvmediaplayer.R;
import com.ptrprograms.androidtvmediaplayer.Util.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends Activity {

    public static final String EXTRA_START_POSITION = "extra_start_position";

    private final double MEDIA_BAR_TOP_MARGIN = 0.8;
    private final double MEDIA_BAR_RIGHT_MARGIN = 0.2;
    private final double MEDIA_BAR_BOTTOM_MARGIN = 0.0;
    private final double MEDIA_BAR_LEFT_MARGIN = 0.2;
    private final double MEDIA_BAR_HEIGHT = 0.1;
    private final double MEDIA_BAR_WIDTH = 0.9;

    private VideoView mVideoView;
    private TextView mStartText;
    private TextView mEndText;
    private SeekBar mSeekbar;
    private ImageView mPlayPause;
    private ProgressBar mLoading;
    private View mControllers;
    private Timer mSeekbarTimer;
    private Timer mControllersTimer;
    private PlaybackState mPlaybackState;
    private final Handler mHandler = new Handler();
    private Movie mSelectedMovie;
    private boolean mShouldStartPlayback;
    private int mDuration;
    private DisplayMetrics mMetrics;

    public static enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_player );

        initWindow();
        initViews();
        setupController();
        setupControlsCallbacks();
        startVideoPlayer();
        mVideoView.invalidate();
    }

    private void initWindow() {
        mMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( mMetrics );
    }

    private void initViews() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mStartText = (TextView) findViewById(R.id.startText);
        mEndText = (TextView) findViewById(R.id.endText);
        mSeekbar = (SeekBar) findViewById(R.id.seekBar);
        mPlayPause = (ImageView) findViewById(R.id.playpause);
        mLoading = (ProgressBar) findViewById(R.id.progressBar);
        mControllers = findViewById(R.id.controllers);

        mVideoView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mControllers.getVisibility() != View.VISIBLE  ) {
                    mControllers.setVisibility( View.VISIBLE );
                }

                if ( mPlaybackState == PlaybackState.PAUSED ) {
                    mPlaybackState = PlaybackState.PLAYING;
                    updatePlayButton( mPlaybackState );
                    mVideoView.start();
                    startControllersTimer();
                } else {
                    mVideoView.pause();
                    mPlaybackState = PlaybackState.PAUSED;
                    updatePlayButton( PlaybackState.PAUSED );
                    stopControllersTimer();
                }
            }
        });
    }

    private void setupController() {

        int w = (int) (mMetrics.widthPixels * MEDIA_BAR_WIDTH);
        int h = (int) (mMetrics.heightPixels * MEDIA_BAR_HEIGHT);
        int marginLeft = (int) (mMetrics.widthPixels * MEDIA_BAR_LEFT_MARGIN);
        int marginTop = (int) (mMetrics.heightPixels * MEDIA_BAR_TOP_MARGIN);
        int marginRight = (int) (mMetrics.widthPixels * MEDIA_BAR_RIGHT_MARGIN);
        int marginBottom = (int) (mMetrics.heightPixels * MEDIA_BAR_BOTTOM_MARGIN);
        LayoutParams lp = new LayoutParams(w, h);
        lp.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        mControllers.setLayoutParams(lp);
        mStartText.setText(getResources().getString(R.string.init_text));
        mEndText.setText(getResources().getString(R.string.init_text));
    }

    private void startVideoPlayer() {
        Bundle bundle = getIntent().getExtras();
        mSelectedMovie = (Movie) getIntent().getSerializableExtra( VideoDetailsFragment.EXTRA_MOVIE );

        if( mSelectedMovie == null || TextUtils.isEmpty( mSelectedMovie.getVideoUrl() ) || bundle == null )
            return;
        
        mShouldStartPlayback = bundle.getBoolean( VideoDetailsFragment.EXTRA_SHOULD_AUTO_START, true );
        int startPosition = bundle.getInt( EXTRA_START_POSITION, 0 );
        mVideoView.setVideoPath( mSelectedMovie.getVideoUrl() );
        if ( mShouldStartPlayback ) {
            mPlaybackState = PlaybackState.PLAYING;
            updatePlayButton( mPlaybackState );
            if ( startPosition > 0 ) {
                mVideoView.seekTo( startPosition );
            }
            mVideoView.start();
            mPlayPause.requestFocus();
            startControllersTimer();
        } else {
            updatePlaybackLocation();
            mPlaybackState = PlaybackState.PAUSED;
            updatePlayButton( mPlaybackState );
        }
    }

    private void updatePlaybackLocation() {
        if ( mPlaybackState == PlaybackState.PLAYING ||
            mPlaybackState == PlaybackState.BUFFERING ) {
            startControllersTimer();
        } else {
            stopControllersTimer();
        }
    }

    private void play( int position ) {
        startControllersTimer();
        mVideoView.seekTo( position );
        mVideoView.start();
        restartSeekBarTimer();
    }

    private void stopSeekBarTimer() {
        if( null != mSeekbarTimer ) {
            mSeekbarTimer.cancel();
        }
    }

    private void restartSeekBarTimer() {
        stopSeekBarTimer();
        mSeekbarTimer = new Timer();
        mSeekbarTimer.scheduleAtFixedRate( new UpdateSeekbarTask(),
                getResources().getInteger( R.integer.seekbar_delay_time ),
                getResources().getInteger( R.integer.seekbar_interval_time ) );
    }

    private void stopControllersTimer() {
        if ( mControllersTimer != null ) {
            mControllersTimer.cancel();
        }
    }

    private void startControllersTimer() {
        stopControllersTimer();
        mControllersTimer = new Timer();
        mControllersTimer.schedule( new HideControllersTask(), getResources().getInteger( R.integer.time_to_hide_controller ) );
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( mSeekbarTimer != null ) {
            mSeekbarTimer.cancel();
            mSeekbarTimer = null;
        }
        if ( mControllersTimer != null ) {
            mControllersTimer.cancel();
        }
        mVideoView.pause();
        mPlaybackState = PlaybackState.PAUSED;
        updatePlayButton( mPlaybackState );
    }

    @Override
    protected void onDestroy() {
        stopControllersTimer();
        stopSeekBarTimer();
        super.onDestroy();
    }

    private class HideControllersTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mControllers.setVisibility( View.GONE );
                }
            });
        }
    }

    private class UpdateSeekbarTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    updateSeekbar( mVideoView.getCurrentPosition(), mDuration );
                }
            });
        }
    }

    private class BackToDetailTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post( new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent( getApplicationContext(), DetailsActivity.class );
                    intent.putExtra( VideoDetailsFragment.EXTRA_MOVIE, mSelectedMovie );
                    startActivity( intent );
                    finish();
                }
            });

        }
    }

    private void setupControlsCallbacks() {

        mVideoView.setOnErrorListener( new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mVideoView.stopPlayback();
                mPlaybackState = PlaybackState.IDLE;
                return false;
            }
        } );

        mVideoView.setOnPreparedListener( new OnPreparedListener() {

            @Override
            public void onPrepared( MediaPlayer mp ) {
                mDuration = mp.getDuration();
                mEndText.setText( Utils.formatMillis( mDuration ) );
                mSeekbar.setMax( mDuration );
                restartSeekBarTimer();
            }
        } );

        mVideoView.setOnCompletionListener( new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSeekBarTimer();
                mPlaybackState = PlaybackState.IDLE;
                updatePlayButton( PlaybackState.IDLE );
                mControllersTimer = new Timer();
                mControllersTimer.schedule(new BackToDetailTask(), getResources().getInteger( R.integer.time_to_hide_controller ) );
            }
        });
    }

    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event ) {
        int currentPos = 0;
        int delta = mDuration / getResources().getInteger( R.integer.scrub_segment_divisor );
        if ( delta < getResources().getInteger( R.integer.min_scrub_time ) )
            delta = getResources().getInteger( R.integer.min_scrub_time );

        if ( mControllers.getVisibility() != View.VISIBLE ) {
            mControllers.setVisibility( View.VISIBLE );
        }
        switch ( keyCode ) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                currentPos = mVideoView.getCurrentPosition();
                currentPos -= delta;
                if (currentPos > 0)
                    play(currentPos);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                currentPos = mVideoView.getCurrentPosition();
                currentPos += delta;
                if( currentPos < mDuration )
                    play( currentPos );
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void updateSeekbar( int position, int duration ) {
        mSeekbar.setProgress( position );
        mSeekbar.setMax( duration );
        mStartText.setText( Utils.formatMillis( position ) );
        mEndText.setText( Utils.formatMillis( duration ) );
    }

    private void updatePlayButton( PlaybackState state ) {
        switch ( state ) {
            case PLAYING:
                mLoading.setVisibility( View.INVISIBLE );
                mPlayPause.setVisibility( View.VISIBLE );
                mPlayPause.setImageDrawable(
                        getResources().getDrawable( R.drawable.ic_pause_playcontrol_normal ) );
                break;
            case PAUSED:
            case IDLE:
                mLoading.setVisibility( View.INVISIBLE );
                mPlayPause.setVisibility( View.VISIBLE );
                mPlayPause.setImageDrawable(
                        getResources().getDrawable( R.drawable.ic_play_playcontrol_normal ) );
                break;
            case BUFFERING:
                mPlayPause.setVisibility( View.INVISIBLE );
                mLoading.setVisibility( View.VISIBLE );
                break;
            default:
                break;
        }
    }
}
