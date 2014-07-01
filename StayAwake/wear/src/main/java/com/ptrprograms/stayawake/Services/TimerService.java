package com.ptrprograms.stayawake.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.ptrprograms.stayawake.Activities.IterationActivity;
import com.ptrprograms.stayawake.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PaulTR on 6/29/14.
 */
public class TimerService extends IntentService
{

    public TimerService() {
        super( "TimerService" );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if( IterationActivity.ACTION_SHOW_ALARM.equals( action ) ) {
            showAlarm();
        } else if( IterationActivity.ACTION_REMOVE_TIMER.equals( action ) ) {
            removeAlarm();
        }
    }

    private void showAlarm() {
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate( getResources().getInteger( R.integer.vibration_duration ) );
        Timer timer = new Timer();
        timer.schedule( new TimerTask() {
            @Override
            public void run() {
                v.cancel();
            }
        }, 2000 );
        Intent intent = new Intent( this, IterationActivity.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity( intent );
    }

    private void removeAlarm() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from( this );
        notificationManager.cancel( 1 );

        AlarmManager alarmManager = (AlarmManager) getSystemService( Context.ALARM_SERVICE );

        Intent intent = new Intent( IterationActivity.ACTION_SHOW_ALARM, null, this, TimerService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        alarmManager.cancel( pendingIntent );

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong( IterationActivity.SAVED_STATE_SELECTED_DURATION, 0 );
        editor.apply();
    }
}
