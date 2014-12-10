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
public class WeatherTemperatureTextView extends TextView implements Updateable {

    public WeatherTemperatureTextView(Context context) {
        super(context);
    }

    public WeatherTemperatureTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherTemperatureTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void update( Weather weather ) {
        Log.e( "WeatherTemperatureTextView", "update!" );
        if( weather == null )
            return;

        setText( weather.getTemperature() + "° F");
    }

}
