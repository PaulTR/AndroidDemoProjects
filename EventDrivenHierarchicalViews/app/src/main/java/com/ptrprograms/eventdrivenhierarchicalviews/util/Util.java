package com.ptrprograms.eventdrivenhierarchicalviews.util;

import com.ptrprograms.eventdrivenhierarchicalviews.model.WeatherCondition;
import com.ptrprograms.eventdrivenhierarchicalviews.model.WindDirection;

import java.util.List;
import java.util.Random;

/**
 * Created by paulruiz on 12/9/14.
 */
public class Util {
    public static WeatherCondition getRandomWeatherCondition( List<WeatherCondition> conditions, Random random ) {
        return conditions.get( random.nextInt( conditions.size() ) );
    }

    public static WindDirection getRandomWindDirection( List<WindDirection> directions, Random random ) {
        return directions.get( random.nextInt( directions.size() ) );
    }

    public static int getRandomWindSpeed( Random random ) {
        return random.nextInt( 30 );
    }

    //30 to 55 degrees
    public static int getRandomTemperature( Random random ) {
        return random.nextInt( 25 ) + 30;
    }
}
