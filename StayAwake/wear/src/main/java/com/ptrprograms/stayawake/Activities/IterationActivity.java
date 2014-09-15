package com.ptrprograms.stayawake.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.ptrprograms.stayawake.Models.IterationListItem;
import com.ptrprograms.stayawake.R;
import com.ptrprograms.stayawake.Services.TimerService;
import com.ptrprograms.stayawake.Utils.TimeUtil;

import java.util.ArrayList;

public class IterationActivity extends Activity
        implements AdapterView.OnItemClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    public static final String EXTRA_DURATION_SECONDS = "extra_duration_seconds";
    public static final String ACTION_REMOVE_TIMER = "action_remove_timer";
    public static final String ACTION_SHOW_ALARM = "action_show_alarm";
    public static final String SAVED_STATE_SELECTED_DURATION = "saved_state_selected_duration";

    private static final int MAX_ITERATION_SECONDS = 1200;

    private ListView mListView;
    private ArrayList<IterationListItem> mIterationTimes = new ArrayList<IterationListItem>();
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( getIntent().getIntExtra( EXTRA_DURATION_SECONDS, 0 ) > 0 && getIntent().getIntExtra( EXTRA_DURATION_SECONDS, 0 ) <= MAX_ITERATION_SECONDS ) {
            setupTimer( getIntent().getIntExtra( EXTRA_DURATION_SECONDS, 0 ) * 1000 );
            finish();
            return;
        }

        if( ( getIntent().getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK ) == Intent.FLAG_ACTIVITY_NEW_TASK ) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
            long duration = pref.getLong( SAVED_STATE_SELECTED_DURATION, 0 );
            if( duration != 0 ) {
                setupTimer( duration );
                finish();
                return;
            }
        }

        setupIterationArray();
        setContentView( R.layout.activity_iteration );

        initList();
        initGoogleApiClient();
    }

    private void setupIterationArray() {
        int[] minutes = getResources().getIntArray( R.array.interation_minutes );
        for( int i = 0; i < minutes.length; i++ ) {
            IterationListItem item = new IterationListItem( getResources().getQuantityString( R.plurals.label_minutes, minutes[i], minutes[i] ),
                    minutes[i]  * 60 * 1000 );
            mIterationTimes.add( item );
        }
    }

    private void initList() {
        mListView = (ListView) findViewById( R.id.list_view );
        mListView.setAdapter( new ArrayAdapter<IterationListItem>( this,
                R.layout.simple_list_item, mIterationTimes ) );
        mListView.setOnItemClickListener( this );
    }

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi( Wearable.API )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .build();
    }

    private void setupTimer( long duration ) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from( this );
        notificationManager.cancel( 1 );
        notificationManager.notify( 1, buildNotification( duration ) );
        registerAlarmManager(duration);
        finish();
    }

    private Notification buildNotification( long duration ) {
        Intent removeIntent = new Intent( ACTION_REMOVE_TIMER, null, this, TimerService.class );
        PendingIntent pendingRemoveIntent = PendingIntent.getService( this, 0, removeIntent, PendingIntent.FLAG_UPDATE_CURRENT );

        return new NotificationCompat.Builder( this )
                .setSmallIcon( R.drawable.ic_launcher )
                .setContentTitle( "Stay Awake" )
                .setContentText(TimeUtil.getTimeString( duration ) )
                .setUsesChronometer( true )
                .setLargeIcon( BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher ) )
                .setWhen( System.currentTimeMillis() + duration )
                .addAction( R.drawable.ic_launcher, "Remove Timer", pendingRemoveIntent )
                .setDeleteIntent( pendingRemoveIntent )
                .setLocalOnly( true )
                .build();
    }

    private void registerAlarmManager( long duration ) {
        AlarmManager alarmManager = (AlarmManager) getSystemService( Context.ALARM_SERVICE );
        Intent intent = new Intent( ACTION_SHOW_ALARM, null, this, TimerService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        long time = System.currentTimeMillis() + duration;
        alarmManager.setExact( AlarmManager.RTC_WAKEUP, time, pendingIntent );
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() )
            mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong( SAVED_STATE_SELECTED_DURATION, mIterationTimes.get( position ).getDuration() );
        editor.commit();
        setupTimer( mIterationTimes.get( position ).getDuration() );
    }
}