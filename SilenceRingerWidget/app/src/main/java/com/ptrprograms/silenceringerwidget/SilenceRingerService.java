package com.ptrprograms.silenceringerwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.RemoteViews;

/**
 * Created by paulruiz on 11/16/14.
 */
public class SilenceRingerService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        silencePhone();
        RemoteViews views = buildViews();
        updateWidget( views );

        stopSelf();
        return START_NOT_STICKY;
    }

    private RemoteViews buildViews() {
        RemoteViews views = new RemoteViews( getPackageName(), R.layout.widget_silence_ringer );
        PendingIntent silenceIntent = PendingIntent.getService( this, 0, new Intent( this, SilenceRingerService.class ), 0 );
        views.setOnClickPendingIntent( R.id.button_silence, silenceIntent );
        return views;
    }

    private void updateWidget( RemoteViews views ) {
        AppWidgetManager manager = AppWidgetManager.getInstance( this );
        ComponentName widget = new ComponentName( this, SilenceRingerWidget.class );
        manager.updateAppWidget( widget, views );
    }

    private void silencePhone() {
        setPriorityAndSilence();
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep( 1000 );
                } catch( InterruptedException e ) {

                }

                setPriorityAndSilence();

            }
        } ).run();
    }

    private void setPriorityAndSilence() {
        AudioManager audioManager;
        audioManager = (AudioManager) getBaseContext().getSystemService( Context.AUDIO_SERVICE );
        audioManager.setRingerMode( AudioManager.RINGER_MODE_SILENT );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
