package com.ptrprograms.eventdrivenhierarchicalviews.model;

/**
 * Created by paulruiz on 12/9/14.
 */
public class Weather {
    public int temperature;
    public int windSpeed;
    public WindDirection windDirection;
    public WeatherCondition condition;

    public Weather( int temperature, int windSpeed, WindDirection windDirection, WeatherCondition condition ) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.condition = condition;

    }

    public void setTemperature( int temperature ) {
        this.temperature = temperature;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setWindSpeed( int windSpeed ) {
        this.windSpeed = windSpeed;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindDirection( WindDirection windDirection ) {
        this.windDirection = windDirection;
    }

    public WindDirection getWindDirection() {
        return windDirection;
    }

    public void setWeatherCondition( WeatherCondition condition ) {
        this.condition = condition;
    }

    public WeatherCondition getWeatherCondition() {
        return condition;
    }
}
