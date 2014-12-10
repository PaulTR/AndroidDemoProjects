package com.ptrprograms.eventdrivenhierarchicalviews.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.ptrprograms.eventdrivenhierarchicalviews.R;
import com.ptrprograms.eventdrivenhierarchicalviews.model.Weather;
import com.ptrprograms.eventdrivenhierarchicalviews.model.WeatherCondition;
import com.ptrprograms.eventdrivenhierarchicalviews.model.WindDirection;
import com.ptrprograms.eventdrivenhierarchicalviews.util.Util;
import com.ptrprograms.eventdrivenhierarchicalviews.view.UpdateableLinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity {

    private List<Weather> mWeather;
    private Handler mHandler;
    private UpdateableLinearLayout mRootView;
    private int mCurrentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = (UpdateableLinearLayout) findViewById( R.id.root );

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSimulation();
    }

    @Override
    protected void onPause() {
        stopSimulation();
        super.onPause();
    }

    private void initData() {
        if( mWeather == null )
            mWeather = new ArrayList<Weather>();

        Random random = new Random();
        
        List<WeatherCondition> conditions = Collections.unmodifiableList( Arrays.asList( WeatherCondition.values() ) );
        List<WindDirection> directions = Collections.unmodifiableList( Arrays.asList( WindDirection.values() ) );

        for( int i = 0; i < 10; i++ ) {
            mWeather.add( new Weather(
                    Util.getRandomTemperature( random ),
                    Util.getRandomWindSpeed( random ),
                    Util.getRandomWindDirection( directions, random ),
                    Util.getRandomWeatherCondition( conditions, random ) ) );
        }
    }

    private void startSimulation() {
        mHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if( mCurrentItem >= mWeather.size() )
                    mCurrentItem = 0;

                mRootView.update( mWeather.get( mCurrentItem++ ) );

                if( mHandler != null )
                    mHandler.postDelayed( this, 3000 );
            }
        };

        runnable.run();
    }

    private void stopSimulation() {
        mHandler = null;
    }
}
