/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ptrprograms.deathstarwatchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.SurfaceHolder;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't shown. On
 * devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient mode.
 */
public class MyWatchFace extends CanvasWatchFaceService {
    /**
     * Update rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        static final int MSG_UPDATE_TIME = 0;

        Paint mBackgroundPaint;
        Bitmap mBackgroundBitmap;
        boolean mAmbient;
        Time mTime;

        private Paint mFilterPaint;

        private Bitmap[] mHourHandBitmap;
        private Bitmap[] mMinuteHandBitmap;
        private Bitmap[] mSecondsHandBitmap;
        private Bitmap mHrHand;
        private Bitmap mMinHand;
        private Bitmap mSecHand;

        private float mHrDeg;

        private Paint mTickPaint;


        private float mScale;


        /**
         * Handler to update the time once a second in interactive mode.
         */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        boolean mRegisteredTimeZoneReceiver = false;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            mHourHandBitmap = loadBitmaps(R.array.hourHandIds);
            mMinuteHandBitmap = loadBitmaps(R.array.minuteHandIds);
            mSecondsHandBitmap = loadBitmaps( R.array.secondHandIds );

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor( Color.parseColor("black") );

            mTime = new Time();

            mFilterPaint = new Paint();
            mFilterPaint.setFilterBitmap(true);

            initTickPaint();
        }

        private void drawWatchTicks( float centerX, float centerY, Canvas canvas ) {
            float innerTickRadius = centerX - 15;
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

        private void initTickPaint() {
            mTickPaint = new Paint();
            mTickPaint.setARGB(255, 200, 0, 0);
            mTickPaint.setStrokeWidth(2.f);
            mTickPaint.setAntiAlias(true);
        }

        private Bitmap[] loadBitmaps(int arrayId) {
            int[] bitmapIds = getIntArray(arrayId);
            Bitmap[] bitmaps = new Bitmap[bitmapIds.length];
            for (int i = 0; i < bitmapIds.length; i++) {
                Drawable backgroundDrawable = getResources().getDrawable(bitmapIds[i]);
                bitmaps[i] = ((BitmapDrawable) backgroundDrawable).getBitmap();
            }
            return bitmaps;
        }

        private int[] getIntArray(int resId) {
            TypedArray array = getResources().obtainTypedArray(resId);
            int[] rc = new int[array.length()];
            TypedValue value = new TypedValue();
            for (int i = 0; i < array.length(); i++) {
                array.getValue(i, value);
                rc[i] = value.resourceId;
            }
            return rc;
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mBackgroundPaint.setAntiAlias( !mAmbient );
                    mTickPaint.setAntiAlias( !mAmbient );
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mTime.setToNow();

            mHrDeg = ((mTime.hour + (mTime.minute / 60f)) * 30);
            int width = bounds.width();
            int height = bounds.height();

            canvas.save();

            if( !mAmbient ) {
                canvas.drawBitmap(mBackgroundBitmap, 0, 0, mFilterPaint);
            } else {
                canvas.drawRect( 0, 0, bounds.width(), bounds.height(), mBackgroundPaint );
            }

            float centerX = width / 2f;
            float centerY = height / 2f;

            drawWatchTicks( centerX, centerY, canvas );

            if( !mAmbient ) {
                canvas.rotate( mTime.second * 6, centerX, centerY );
                mSecHand = getBitmap( mSecondsHandBitmap );
                canvas.drawBitmap( mSecHand, centerX - mSecHand.getWidth() / 2, centerY - mSecHand.getHeight(), mFilterPaint );
            }


            // Draw the minute hand
            if( !mAmbient )
                canvas.rotate(mTime.minute * 6 - ( mTime.second * 6 ), centerX, centerY );
            else
                canvas.rotate(mTime.minute * 6, centerX, centerY );
            mMinHand = getBitmap(mMinuteHandBitmap);
            canvas.drawBitmap(mMinHand, centerX - mMinHand.getWidth() / 2f,
                    centerY - mMinHand.getHeight(), mFilterPaint);

            // Draw the hour hand
            canvas.rotate(360 - ( mTime.minute * 6) + mHrDeg, centerX, centerY);
            mHrHand = getBitmap(mHourHandBitmap);
            canvas.drawBitmap(mHrHand, centerX - mHrHand.getWidth() / 2f,
                    centerY - mHrHand.getHeight(),
                    mFilterPaint);

            canvas.restore();
        }

        private Bitmap getBitmap(Bitmap[] bitmaps) {
            if (!mAmbient) {
                return bitmaps[0];
            } else {
                return bitmaps[1];
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            // Draw the background.
            if( mBackgroundBitmap == null ) {
                Resources resources = MyWatchFace.this.getResources();
                Drawable backgroundDrawable = resources.getDrawable( R.drawable.deathstar );
                mBackgroundBitmap = Bitmap.createScaledBitmap( ((BitmapDrawable) backgroundDrawable).getBitmap(), width, height, true );
            }

            mScale = ((float) width / 2) / (float) mBackgroundBitmap.getWidth();
            scaleBitmaps(mHourHandBitmap, mScale);
            scaleBitmaps(mMinuteHandBitmap, mScale);
            scaleBitmaps(mSecondsHandBitmap, mScale);
        }

        private void scaleBitmaps(Bitmap[] bitmaps, float scale) {
            for (int i = 0; i < bitmaps.length; i++) {
                bitmaps[i] = scaleBitmap(bitmaps[i], scale);
            }
        }

        private Bitmap scaleBitmap(Bitmap bitmap, float scale) {
            int width = (int) ((float) bitmap.getWidth() * scale);
            int height = (int) ((float) bitmap.getHeight() * scale);
            if (bitmap.getWidth() != width
                    || bitmap.getHeight() != height) {
                return Bitmap.createScaledBitmap(bitmap,
                        width, height, true /* filter */);
            } else {
                return bitmap;
            }
        }
    }
}
