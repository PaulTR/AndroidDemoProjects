package com.ptrprograms.androidtvmediaplayer.Presenter;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.ptrprograms.androidtvmediaplayer.Model.Movie;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;

        if (movie != null) {
            viewHolder.getTitle().setText(movie.getTitle());
            viewHolder.getSubtitle().setText(movie.getStudio());
            viewHolder.getBody().setText(movie.getDescription());
        }
    }
}
