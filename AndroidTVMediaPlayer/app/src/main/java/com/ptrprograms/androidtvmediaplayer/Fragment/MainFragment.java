package com.ptrprograms.androidtvmediaplayer.Fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.Row;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ptrprograms.androidtvmediaplayer.Activity.DetailsActivity;
import com.ptrprograms.androidtvmediaplayer.Bonus.SlothActivity;
import com.ptrprograms.androidtvmediaplayer.Presenter.CardPresenter;
import com.ptrprograms.androidtvmediaplayer.Model.Movie;
import com.ptrprograms.androidtvmediaplayer.Presenter.PreferenceCardPresenter;
import com.ptrprograms.androidtvmediaplayer.R;
import com.ptrprograms.androidtvmediaplayer.Util.Utils;

public class MainFragment extends BrowseFragment {

    private List<Movie> mMovies = new ArrayList<Movie>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadData();
        initUI();
        loadRows();
        setupEventListeners();
    }

    private void loadData() {
        String json = Utils.loadJSONFromResource( getActivity(), R.raw.movies );
        Gson gson = new Gson();
        Type collection = new TypeToken<ArrayList<Movie>>(){}.getType();
        mMovies = gson.fromJson( json, collection );
    }

    private void loadRows() {

        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter( new ListRowPresenter() );
        CardPresenter cardPresenter = new CardPresenter();

        List<String> categories = getCategories();
        if( categories == null || categories.isEmpty() )
            return;

        for( String category : categories ) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter( cardPresenter );
            for( Movie movie : mMovies ) {
                if( category.equalsIgnoreCase( movie.getCategory() ) )
                    listRowAdapter.add( movie );
            }
            if( listRowAdapter.size() > 0 ) {
                HeaderItem header = new HeaderItem( rowsAdapter.size() - 1, category, null );
                rowsAdapter.add( new ListRow( header, listRowAdapter ) );
            }
        }

        setupPreferences( rowsAdapter );
        setAdapter( rowsAdapter );

    }

    private void setupPreferences( ArrayObjectAdapter adapter ) {

        HeaderItem gridHeader = new HeaderItem( adapter.size(), "Preferences", null );
        PreferenceCardPresenter mGridPresenter = new PreferenceCardPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter( mGridPresenter );
        gridRowAdapter.add( getResources().getString( R.string.sloth ) );
        adapter.add( new ListRow( gridHeader, gridRowAdapter ) );

    }

    private List<String> getCategories() {
        if( mMovies == null )
            return null;

        List<String> categories = new ArrayList<String>();
        for( Movie movie : mMovies ) {
            if( !categories.contains( movie.getCategory() ) ) {
                categories.add( movie.getCategory() );
            }
        }

        return categories;
    }

    private void setBackground() {

        BackgroundManager backgroundManager = BackgroundManager.getInstance( getActivity() );
        backgroundManager.attach( getActivity().getWindow() );
        backgroundManager.setDrawable( getResources().getDrawable( R.drawable.default_background ) );
    }

    private void initUI() {
        setTitle( getString( R.string.browse_title ) );
        setHeadersState( HEADERS_ENABLED );

        //Back button goes to the fast lane, rather than home screen
        setHeadersTransitionOnBackEnabled( true );

        setBrandColor( getResources().getColor( R.color.fastlane_background ) );
        setSearchAffordanceColor( getResources().getColor( R.color.search_button_color ) );
        setBackground();
    }

    private void setupEventListeners() {
        setOnItemClickedListener( getDefaultItemClickedListener() );
        setOnSearchClickedListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected OnItemClickedListener getDefaultItemClickedListener() {
        return new OnItemClickedListener() {
            @Override
            public void onItemClicked( Object item, Row row ) {
                if( item instanceof Movie ) {
                    Movie movie = (Movie) item;
                    Intent intent = new Intent( getActivity(), DetailsActivity.class );
                    intent.putExtra( VideoDetailsFragment.EXTRA_MOVIE, movie );
                    startActivity( intent );
                } else if( item instanceof String ) {
                    if( ((String) item).equalsIgnoreCase( getString( R.string.sloth ) ) ) {
                        Intent intent = new Intent( getActivity(), SlothActivity.class );
                        startActivity( intent );
                    }
                }
            }
        };
    }

}
