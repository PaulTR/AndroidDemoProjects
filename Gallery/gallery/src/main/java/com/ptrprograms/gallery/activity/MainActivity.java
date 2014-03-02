package com.ptrprograms.gallery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ptrprograms.gallery.R;
import com.ptrprograms.gallery.adapter.GalleryGridAdapter;
import com.ptrprograms.gallery.model.Gallery;
import com.ptrprograms.gallery.model.Image;
import com.ptrprograms.gallery.util.GsonRequest;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private GridView mGridView;
    private final String TAG = MainActivity.class.getSimpleName();
    private Gallery mGallery;
    private GalleryGridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGallery = new Gallery();
        mAdapter = new GalleryGridAdapter( getApplicationContext(), R.layout.view_gallery_grid_image );
        mGridView = ( GridView ) findViewById( R.id.activity_main_grid_view );
		loadFeed();
    }

	private void loadFeed() {
		String feedUrl = getString( R.string.feed_url );
		GsonRequest<Gallery> request = new GsonRequest<Gallery>( Request.Method.GET, feedUrl,
				Gallery.class, successListener(), errorListener() );
		Volley.newRequestQueue( getApplicationContext() ).add( request );
	}

    public Response.Listener successListener()
    {
        return new Response.Listener<Gallery>()
        {
            @Override
            public void onResponse( Gallery gallery ) {
                mGallery.setImages( gallery.getImages() );
                mGallery.setDescription( gallery.getDescription() );
                setupUI();
            }
        };
    }

    protected Response.ErrorListener errorListener()
    {
        return new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse( VolleyError volleyError )
            {
                Log.e( TAG, volleyError.getMessage() );
            }
        };
    }

    private void setupUI() {
        loadImages();
        setupGridView();
        mGridView.setAdapter( mAdapter );
    }

    private void loadImages() {
        if( mGallery == null || mAdapter == null )
            return;

        for( Image image : mGallery.getImages() )
        {
            if( TextUtils.isEmpty( image.getThumbNailImage()) )
                continue;
            mAdapter.add( image.getThumbNailImage() );
        }
    }

    private void setupGridView() {
     mGridView.setOnItemClickListener( new AdapterView.OnItemClickListener()
     {
         @Override
         public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
             Intent intent = new Intent( getApplicationContext(), ImageActivity.class );
             intent.putExtra( ImageActivity.EXTRA_IMAGE_LIST, (ArrayList) mGallery.getImages() );
             intent.putExtra( ImageActivity.EXTRA_CUR_IMAGE, position );
             startActivity( intent );
         }
     });
    }
}
