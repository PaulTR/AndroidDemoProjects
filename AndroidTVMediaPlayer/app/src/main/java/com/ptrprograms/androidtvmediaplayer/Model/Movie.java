package com.ptrprograms.androidtvmediaplayer.Model;

import java.io.Serializable;

public class Movie implements Serializable {
    private String title;
    private String description;
    private String studio;
    private String videoUrl;
    private String category;
    private String cardImageUrl;
    private String backgroundImageUrl;

    public Movie() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio( String studio ) {
        this.studio = studio;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl( String videoUrl ) {
        this.videoUrl = videoUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory( String category ) {
        this.category = category;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl( String cardImageUrl ) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl( String backgroundImageUrl ) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    @Override
    public String toString() {
        return "Movie {" +
                    "title=\'" + title + "\'" +
                    ", description=\'" + description + "\'" +
                    ", studio=\'" + studio + "\'" +
                    ", videoUrl=\'" + videoUrl + "\'" +
                    ", category=\'" + category + "\'" +
                    ", cardImageUrl=\'" + cardImageUrl + "\'" +
                    ", backgroundImageUrl=\'" + backgroundImageUrl + "\'" +
                "}";
    }
}
