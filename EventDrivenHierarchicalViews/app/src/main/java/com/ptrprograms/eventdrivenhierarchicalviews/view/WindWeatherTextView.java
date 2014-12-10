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
public class WindWeatherTextView extends TextView implements Updateable {

    public WindWeatherTextView(Context context) {
        super(context);
    }

    public WindWeatherTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindWeatherTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void update( Weather weather ) {
        Log.e( "WindWeatherTextView", "update!" );
        if( weather == null || weather.getWindDirection() == null )
            return;

        if( weather.getWindSpeed() == 0 ) {
            setText( "There's no wind right now." );
        } else {
            setText( "The wind is going " + weather.getWindSpeed() + " MPH in the " + weather.getWindDirection() + " direction" );
        }

    }
}
