package com.ptrprograms.wearcustomwatchface;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;

public class WatchFaceActivity extends Activity {

    public static final String SHARED_PREFERENCE = "shared_preference";

    private ImageView mBackground;
    private TextClock mClock;
    private LinearLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_face);

        mBackground = (ImageView) findViewById( R.id.watch_background );
        mContainer = (LinearLayout) findViewById( R.id.watch_container );
        mClock = (TextClock) findViewById( R.id.watch_time );
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBackground.setImageDrawable( null );
        mClock.setTextColor( getResources().getColor( android.R.color.white ) );
        mContainer.setBackgroundColor( getResources().getColor( android.R.color.black ) );
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences( SHARED_PREFERENCE, Context.MODE_PRIVATE );

        if( pref.contains( SettingsActivity.SHARED_PREFERENCE_SCHOOL ) ) {
            String schoolCode = pref.getString( SettingsActivity.SHARED_PREFERENCE_SCHOOL, "" );
            loadSchoolWatchFace( schoolCode );
        } else {
            mBackground.setImageResource( R.drawable.bulldog_wallpaper );
            mClock.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            mContainer.setBackgroundColor(getResources().getColor( android.R.color.white ) );
        }
    }

    private void loadSchoolWatchFace( String schoolCode ) {
        if( "cuboulder".equals( schoolCode ) ) {
            mBackground.setImageResource( R.drawable.cuboulder_wallpaper );
            mClock.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        } else if( "fsu".equals( schoolCode ) ) {
            mBackground.setImageResource( R.drawable.floridastate_wallpaper );
            mClock.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if( "ucsc".equals( schoolCode ) ) {
            mBackground.setImageResource( R.drawable.bananaslugs_logo );
            mClock.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        } else if( "berkeley".equals( schoolCode ) ) {
            mBackground.setImageResource( R.drawable.berkeley_wallpaper );
            mClock.setTextColor(getResources().getColor(android.R.color.holo_orange_light ) );
        } else {
            //Default to Fresno
            mBackground.setImageResource( R.drawable.bulldog_wallpaper );
            mClock.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        mContainer.setBackgroundColor(getResources().getColor(android.R.color.white));
    }

}
