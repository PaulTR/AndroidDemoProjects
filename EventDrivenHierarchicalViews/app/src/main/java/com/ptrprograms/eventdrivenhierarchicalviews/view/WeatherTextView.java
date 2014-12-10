package com.ptrprograms.eventdrivenhierarchicalviews.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.ptrprograms.eventdrivenhierarchicalviews.model.Weather;
import com.ptrprograms.eventdrivenhierarchicalviews.util.Updateable;

/**
 * Created by paulruiz on 12/9/14.
 */
public class WeatherTextView extends TextView implements Updateable {

    public WeatherTextView(Context context) {
        super(context);
    }

    public WeatherTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void update( Weather weather ) {
        Log.e( "WeatherTextView", "update!" );

        if( weather == null || weather.getWeatherCondition() == null )
            return;

        switch( weather.getWeatherCondition() ) {
            case CLOUDY: {
                setText( "It's a little cloudy out right now." );
                break;
            }
            case FOG: {
                setText( "Drive carefully, it's foggy out there!" );
                break;
            }
            case LIGHTNING: {
                setText( "It's currently storming outside!" );
                break;
            }
            case RAIN: {
                setText( "It's pretty wet out there." );
                break;
            }
            case SNOW: {
                setText( "Brrrrrrrr. Snow is falling." );
                break;
            }
            case SUN: {
                setText( "Go outside! It's a gorgeous, sunny day." );
                break;
            }
        }
    }

}
