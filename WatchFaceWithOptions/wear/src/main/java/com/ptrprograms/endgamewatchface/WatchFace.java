package com.ptrprograms.endgamewatchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by paulruiz on 3/29/15.
 */
public class WatchFace extends CanvasWatchFaceService {

    private static final String SHARED_PREFERENCE_POSITION = "shared_preference_position";
    public static final String DATA_LAYER_PATH = "/watchface";
    public static final String KEY_BACKGROUND_POSITION = "key_background_position";
    public static final String KEY_TIME = "key_time";

    public static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);


    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataApi.DataListener,
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        private static final int MSG_UPDATE_TIME = 0;

        private boolean mMute;
        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mLowBitAmbient;

        private Time mTime;

        private Bitmap mBackgroundBitmap;
        private Bitmap mBackgroundScaledBitmap;
        private Paint mHourPaint;
        private Paint mMinutePaint;
        private Paint mSecondPaint;
        private Paint mTickPaint;

        /** Handler to update the time once a second in interactive mode. */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                invalidate();
                switch ( message.what ) {
                    case MSG_UPDATE_TIME:
                        if( shouldTimerBeRunning() ) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive( Context context, Intent intent ) {
                mTime.clear( intent.getStringExtra( "time-zone" ) );
                mTime.setToNow();
            }
        };

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder( WatchFace.this )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            initWatchFaceStyle();
            initBackground();
            initHourPaint();
            initMinutePaint();
            initSecondPaint();
            initTickPaint();

            mTime = new Time();
        }

        private void initWatchFaceStyle() {
            setWatchFaceStyle( new WatchFaceStyle.Builder( WatchFace.this )
                    .setCardPeekMode( WatchFaceStyle.PEEK_MODE_SHORT )
                    .setBackgroundVisibility( WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE )
                    .setShowSystemUiTime( false )
                    .build() );
        }

        private void initBackground() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            TypedArray typedArray = getResources().obtainTypedArray( R.array.background_resource_ids );
            initBackground( typedArray.getResourceId( preferences.getInt( SHARED_PREFERENCE_POSITION, 0 ), 0 ) );
            typedArray.recycle();
        }

        private void initBackground( int resId ) {
            Resources resources = WatchFace.this.getResources();
            Drawable backgroundDrawable = resources.getDrawable( resId );
            mBackgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();

            mBackgroundScaledBitmap = null;
        }

        private void initHourPaint() {
            mHourPaint = new Paint();
            mHourPaint.setARGB(255, 0, 0, 0);
            mHourPaint.setStrokeWidth(5.f);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        private void initMinutePaint() {
            mMinutePaint = new Paint();
            mMinutePaint.setARGB(255, 0, 0, 0);
            mMinutePaint.setStrokeWidth(3.f);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);
        }

        private void initSecondPaint() {
            mSecondPaint = new Paint();
            mSecondPaint.setARGB(255, 255, 0, 0);
            mSecondPaint.setStrokeWidth(2.f);
            mSecondPaint.setAntiAlias(true);
            mSecondPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        private void initTickPaint() {
            mTickPaint = new Paint();
            mTickPaint.setARGB(100, 255, 255, 255);
            mTickPaint.setStrokeWidth(2.f);
            mTickPaint.setAntiAlias(true);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages( MSG_UPDATE_TIME );
            if( mRegisteredTimeZoneReceiver )
                unregisterReceiver();

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Wearable.DataApi.removeListener(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }

            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged( Bundle properties ) {
            super.onPropertiesChanged( properties );
            mLowBitAmbient = properties.getBoolean( PROPERTY_LOW_BIT_AMBIENT, false );
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();

            invalidate();
        }

        @Override
        public void onAmbientModeChanged( boolean inAmbientMode ) {
            super.onAmbientModeChanged( inAmbientMode );

            if ( mLowBitAmbient ) {
                boolean antiAlias = !inAmbientMode;
                mHourPaint.setAntiAlias( antiAlias );
                mMinutePaint.setAntiAlias( antiAlias );
                mSecondPaint.setAntiAlias( antiAlias );
                mTickPaint.setAntiAlias( antiAlias );
            }

            invalidate();

            // Whether the timer should be running depends on whether we're in ambient mode (as well
            // as whether we're visible), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onInterruptionFilterChanged( int interruptionFilter ) {
            super.onInterruptionFilterChanged( interruptionFilter );
            boolean inMuteMode = ( interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE );
            if ( mMute != inMuteMode ) {
                mMute = inMuteMode;
                mHourPaint.setAlpha(inMuteMode ? 100 : 255);
                mMinutePaint.setAlpha(inMuteMode ? 100 : 255);
                mSecondPaint.setAlpha(inMuteMode ? 80 : 255);
                invalidate();
            }
        }

        @Override
        public void onDraw( Canvas canvas, Rect bounds ) {
            mTime.setToNow();

            int width = bounds.width();
            int height = bounds.height();

            if ( mBackgroundScaledBitmap == null
                    || mBackgroundScaledBitmap.getWidth() != width
                    || mBackgroundScaledBitmap.getHeight() != height ) {
                mBackgroundScaledBitmap = Bitmap.createScaledBitmap( mBackgroundBitmap, width, height, true );
            }

            canvas.drawBitmap( mBackgroundScaledBitmap, 0, 0, null );

            float centerX = width / 2.0f;
            float centerY = height / 2.0f;

            drawWatchTicks( centerX, centerY, canvas );

            float secRot = mTime.second / 30f * (float) Math.PI;
            int minutes = mTime.minute;
            float minRot = minutes / 30f * (float) Math.PI;
            float hrRot = ((mTime.hour + (minutes / 60f)) / 6f ) * (float) Math.PI;

            float secLength = centerX - 20;
            float minLength = centerX - 40;
            float hrLength = centerX - 80;

            float minX = (float) Math.sin(minRot) * minLength;
            float minY = (float) -Math.cos(minRot) * minLength;
            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY, mMinutePaint);

            float hrX = (float) Math.sin(hrRot) * hrLength;
            float hrY = (float) -Math.cos(hrRot) * hrLength;
            canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY, mHourPaint);

            if (!isInAmbientMode()) {
                float secX = (float) Math.sin(secRot) * secLength;
                float secY = (float) -Math.cos(secRot) * secLength;
                canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, mSecondPaint);
            }
        }

        private void drawWatchTicks( float centerX, float centerY, Canvas canvas ) {
            float innerTickRadius = centerX - 10;
            for (int tickIndex = 0; tickIndex < 12; tickIndex++) {
                float tickRot = (float) (tickIndex * Math.PI * 2 / 12);
                float innerX = (float) Math.sin(tickRot) * innerTickRadius;
                float innerY = (float) -Math.cos(tickRot) * innerTickRadius;
                float outerX = (float) Math.sin(tickRot) * centerX;
                float outerY = (float) -Math.cos(tickRot) * centerX;
                canvas.drawLine(centerX + innerX, centerY + innerY,
                        centerX + outerX, centerY + outerY, mTickPaint);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if( visible ) {
                if( mGoogleApiClient != null && !mGoogleApiClient.isConnected() )
                    mGoogleApiClient.connect();

                registerReceiver();

                mTime.clear( TimeZone.getDefault().getID() );
                mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            updateTimer();
        }

        private void registerReceiver() {
            if ( mRegisteredTimeZoneReceiver ) {
                return;
            }

            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter( Intent.ACTION_TIMEZONE_CHANGED );
            WatchFace.this.registerReceiver( mTimeZoneReceiver, filter );
        }

        private void unregisterReceiver() {
            if ( !mRegisteredTimeZoneReceiver ) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            WatchFace.this.unregisterReceiver( mTimeZoneReceiver );
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages( MSG_UPDATE_TIME );
            if ( shouldTimerBeRunning() ) {
                mUpdateTimeHandler.sendEmptyMessage( MSG_UPDATE_TIME );
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        @Override
        public void onConnected(Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, this);
        }


        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {

            for( DataEvent event : dataEvents ) {
                if( event.getType() == DataEvent.TYPE_CHANGED ) {
                    DataItem item = event.getDataItem();
                    if( item.getUri().getPath().compareTo( DATA_LAYER_PATH ) == 0 ) {
                        DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                        int selectedBackgroundPosition = dataMap.getInt(KEY_BACKGROUND_POSITION);
                        TypedArray typedArray = getResources().obtainTypedArray( R.array.background_resource_ids );
                        initBackground( typedArray.getResourceId( selectedBackgroundPosition, 0 ) );
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        preferences.edit().putInt( SHARED_PREFERENCE_POSITION, selectedBackgroundPosition ).commit();
                        typedArray.recycle();
                        invalidate();
                    }
                }
            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    }

}
