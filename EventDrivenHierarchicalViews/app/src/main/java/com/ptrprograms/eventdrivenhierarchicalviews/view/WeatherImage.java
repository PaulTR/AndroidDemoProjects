package com.ptrprograms.eventdrivenhierarchicalviews.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.ptrprograms.eventdrivenhierarchicalviews.R;
import com.ptrprograms.eventdrivenhierarchicalviews.model.Weather;
import com.ptrprograms.eventdrivenhierarchicalviews.util.Updateable;

/**
 * Created by paulruiz on 12/9/14.
 */
public class WeatherImage extends ImageView implements Updateable {

    public WeatherImage(Context context) {
        super(context);
    }

    public WeatherImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void update( Weather weather ) {
        Log.e( "WeatherImage", "update!" );
        if( weather == null || weather.getWeatherCondition() == null )
            return;

        switch( weather.getWeatherCondition() ) {
            case CLOUDY: {
                setImageResource( R.drawable.cloudy );
                break;
            }
            case FOG: {
                setImageResource( R.drawable.fog );
                break;
            }
            case LIGHTNING: {
                setImageResource( R.drawable.lightning );
                break;
            }
            case RAIN: {
                setImageResource( R.drawable.rain );
                break;
            }
            case SNOW: {
                setImageResource( R.drawable.snow );
                break;
            }
            case SUN: {
                setImageResource( R.drawable.sun );
                break;
            }
        }
    }

}
