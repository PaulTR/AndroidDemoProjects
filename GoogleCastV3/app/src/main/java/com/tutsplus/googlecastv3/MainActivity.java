package com.tutsplus.googlecastv3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.AppVisibilityListener;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        SessionManagerListener,
        AppVisibilityListener,
        CastStateListener {

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    private IntroductoryOverlay mIntroductoryOverlay;
    private MenuItem mMediaRouterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mListView = (ListView) findViewById(R.id.list);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

        mAdapter.add("Movie 1");
        mAdapter.add("Movie 2");
        mAdapter.add("Movie 3");
        mAdapter.add("Movie 4");
        mAdapter.add("Movie 5");
        mAdapter.add("Movie 6");
        mAdapter.add("Movie 7");

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        CastContext.getSharedInstance(this).addCastStateListener(this);
        CastContext.getSharedInstance(this).addAppVisibilityListener(this);
        CastContext.getSharedInstance(this).getSessionManager().addSessionManagerListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMediaRouterButton = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);

        showIntroductoryOverlay();

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CastContext.getSharedInstance(this).removeAppVisibilityListener(this);
        CastContext.getSharedInstance(this).removeCastStateListener(this);
        CastContext.getSharedInstance(this).getSessionManager().removeSessionManagerListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if( CastContext.getSharedInstance(this).getSessionManager().getCurrentCastSession() != null
                && CastContext.getSharedInstance(this).getSessionManager().getCurrentCastSession().getRemoteMediaClient() != null ) {

            RemoteMediaClient remoteMediaClient = CastContext.getSharedInstance(this).getSessionManager().getCurrentCastSession().getRemoteMediaClient();


            if( remoteMediaClient.getMediaInfo() != null &&
                    remoteMediaClient.getMediaInfo().getMetadata() != null
                    && mAdapter.getItem(position).equalsIgnoreCase(
                        remoteMediaClient.getMediaInfo().getMetadata().getString(MediaMetadata.KEY_TITLE))) {

                startActivity(new Intent(this, ExpandedControlsActivity.class));

            } else {
                MediaMetadata metadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

                metadata.putString(MediaMetadata.KEY_TITLE, mAdapter.getItem(position));
                metadata.putString(MediaMetadata.KEY_SUBTITLE, "Subtitle");
                metadata.addImage(new WebImage(Uri.parse(getString(R.string.movie_poster))));
                metadata.addImage(new WebImage(Uri.parse(getString(R.string.movie_poster))));

                MediaInfo mediaInfo = new MediaInfo.Builder(getString(R.string.movie_link))
                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setContentType("videos/mp4")
                        .setMetadata(metadata)
                        .build();


                remoteMediaClient.load(mediaInfo, true, 0);
            }
        } else {
            startActivity(new Intent(this, MovieDetailActivity.class));
        }
    }

    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mMediaRouterButton != null) && mMediaRouterButton.isVisible()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                            MainActivity.this, mMediaRouterButton)
                            .setTitleText("Introduction text")
                            .setOverlayColor(R.color.colorPrimary)
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
                                        @Override
                                        public void onOverlayDismissed() {
                                            mIntroductoryOverlay = null;
                                        }
                                    })
                            .build();
                    mIntroductoryOverlay.show();
                }
            });
        }
    }

    @Override
    public void onSessionStarting(Session session) {
        Log.e("Tuts+", "onSessionsStarting");
    }

    @Override
    public void onSessionStarted(Session session, String s) {
        Log.e("Tuts+", "onSessionStarted");
        invalidateOptionsMenu();
    }

    @Override
    public void onSessionStartFailed(Session session, int i) {
        Log.e("Tuts+", "onSessionStartFailed");
    }

    @Override
    public void onSessionEnding(Session session) {
        Log.e("Tuts+", "onSessionEnding");
    }

    @Override
    public void onSessionEnded(Session session, int i) {
        Log.e("Tuts+", "onSessionEnded");
    }

    @Override
    public void onSessionResuming(Session session, String s) {
        Log.e("Tuts+", "onSessionResuming");
    }

    @Override
    public void onSessionResumed(Session session, boolean b) {
        Log.e("Tuts+", "onSessionResumed");
        invalidateOptionsMenu();
    }

    @Override
    public void onSessionResumeFailed(Session session, int i) {
        Log.e("Tuts+", "onSessionResumeFailed");
    }

    @Override
    public void onSessionSuspended(Session session, int i) {
        Log.e("Tuts+", "onSessionSuspended");
    }

    @Override
    public void onAppEnteredForeground() {
        Log.e("Tuts+", "onAppEnteredForeground");
    }

    @Override
    public void onAppEnteredBackground() {
        Log.e("Tuts+", "onAppEnteredBackground");
    }

    @Override
    public void onCastStateChanged(int newState) {
        Log.e("Tuts+", "onCastStateChanged");

        switch( newState ) {
            case CastState.CONNECTED: {

                break;
            } case CastState.CONNECTING: {

                break;
            } case CastState.NOT_CONNECTED: {

                break;
            } case CastState.NO_DEVICES_AVAILABLE: {

            }
        }

        if (newState != CastState.NO_DEVICES_AVAILABLE) {
            showIntroductoryOverlay();
        }
    }
}
