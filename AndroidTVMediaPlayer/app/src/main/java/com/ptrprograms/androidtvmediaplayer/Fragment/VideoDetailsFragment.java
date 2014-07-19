package com.ptrprograms.androidtvmediaplayer.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ptrprograms.androidtvmediaplayer.Activity.PlayerActivity;
import com.ptrprograms.androidtvmediaplayer.Model.Movie;
import com.ptrprograms.androidtvmediaplayer.Presenter.CardPresenter;
import com.ptrprograms.androidtvmediaplayer.Presenter.DetailsDescriptionPresenter;
import com.ptrprograms.androidtvmediaplayer.R;
import com.ptrprograms.androidtvmediaplayer.Util.PicassoBackgroundManagerTarget;
import com.ptrprograms.androidtvmediaplayer.Util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class VideoDetailsFragment extends DetailsFragment {

    public static final String EXTRA_MOVIE = "extra_movie";
    public static final String EXTRA_SHOULD_AUTO_START = "extra_should_auto_start";

    private static final int ACTION_WATCH = 1;

    private Movie mSelectedMovie;

    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedMovie = (Movie) getActivity().getIntent().getSerializableExtra( EXTRA_MOVIE );

        initBackground();
        new DetailRowBuilderTask().execute( mSelectedMovie );

    }

    private void initBackground() {
        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget( backgroundManager );

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        if( mSelectedMovie != null && !TextUtils.isEmpty( mSelectedMovie.getBackgroundImageUrl() ) ) {
            try {
                updateBackground(new URI(mSelectedMovie.getBackgroundImageUrl()));
            } catch (URISyntaxException e) { }
        }
    }

    protected void updateBackground(URI uri) {
        if( uri.toString() == null ) {
            try {
                uri = new URI("");
            } catch( URISyntaxException e ) {}
        }

        Picasso.with(getActivity())
                .load( uri.toString() )
                .error( getResources().getDrawable( R.drawable.default_background ) )
                .resize( mMetrics.widthPixels, mMetrics.heightPixels )
                .into( mBackgroundTarget );
    }



    private class DetailRowBuilderTask extends AsyncTask<Movie, Integer, DetailsOverviewRow> {
        @Override
        protected DetailsOverviewRow doInBackground( Movie... movies ) {
            mSelectedMovie = movies[0];
            DetailsOverviewRow row = null;
            try {
                row = new DetailsOverviewRow( mSelectedMovie );
                Bitmap poster = Picasso.with( getActivity() )
                        .load( mSelectedMovie.getCardImageUrl() )
                        .resize(Utils.dpToPx( getActivity().getResources().getInteger( R.integer.detail_thumbnail_square_size ), getActivity().getApplicationContext() ),
                                Utils.dpToPx( getActivity().getResources().getInteger( R.integer.detail_thumbnail_square_size ), getActivity().getApplicationContext() ) )
                        .centerCrop()
                        .get();
                row.setImageBitmap( getActivity(), poster );
            } catch ( IOException e ) {
                getActivity().finish();
                return null;
            } catch( NullPointerException e ) {
                getActivity().finish();
                return null;
            }

            row.addAction( new Action( ACTION_WATCH, getResources().getString(
                    R.string.watch ), getResources().getString( R.string.watch_subtext) ) );

            return row;
        }

        @Override
        protected void onPostExecute( DetailsOverviewRow detailRow ) {
            if( detailRow == null )
                return;

            ClassPresenterSelector ps = new ClassPresenterSelector();
            DetailsOverviewRowPresenter dorPresenter =
                    new DetailsOverviewRowPresenter( new DetailsDescriptionPresenter() );
            // set detail background and style
            dorPresenter.setBackgroundColor( getResources().getColor( R.color.detail_background ) );
            dorPresenter.setStyleLarge( true );
            dorPresenter.setOnActionClickedListener( new OnActionClickedListener() {
                @Override
                public void onActionClicked( Action action ) {
                    if (action.getId() == ACTION_WATCH ) {
                        Intent intent = new Intent( getActivity(), PlayerActivity.class );
                        intent.putExtra( EXTRA_MOVIE, mSelectedMovie );
                        intent.putExtra( EXTRA_SHOULD_AUTO_START, true );
                        startActivity( intent );
                    }
                }
            });

            ps.addClassPresenter( DetailsOverviewRow.class, dorPresenter );
            ps.addClassPresenter( ListRow.class,
                    new ListRowPresenter() );


            ArrayObjectAdapter adapter = new ArrayObjectAdapter( ps );
            adapter.add( detailRow );
            loadRelatedMedia( adapter );
            setAdapter( adapter );
        }

        private void loadRelatedMedia( ArrayObjectAdapter adapter ) {

            String json = Utils.loadJSONFromResource( getActivity(), R.raw.movies );
            Gson gson = new Gson();
            Type collection = new TypeToken<ArrayList<Movie>>(){}.getType();
            List<Movie> movies = gson.fromJson( json, collection );
            List<Movie> related = new ArrayList<Movie>();
            for( Movie movie : movies ) {
                if( movie.getCategory().equals( mSelectedMovie.getCategory() ) ) {
                    related.add( movie );
                }
            }

            if( related.isEmpty() )
                return;

            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter( new CardPresenter() );
            for( Movie movie : related ) {
                listRowAdapter.add( movie );
            }

            HeaderItem header = new HeaderItem( 0, "Related", null );
            adapter.add( new ListRow( header, listRowAdapter ) );
        }

    }

}
