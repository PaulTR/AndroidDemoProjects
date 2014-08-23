package com.ptrprograms.iconcolorization.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.widget.ImageView;

import com.ptrprograms.iconcolorization.R;
import com.ptrprograms.iconcolorization.utility.ColorTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class MainActivity extends ActionBarActivity {

    private ImageView mDrawableTransformedImage;
    private ImageView mDrawableUrlTransformedImage;
    private ImageView mDrawableUrlImage;

    private Target ActionBarIconTarget = new Target()
    {
        @Override
        public void onBitmapLoaded( Bitmap bitmap, Picasso.LoadedFrom from )
        {
            getSupportActionBar().setIcon( new BitmapDrawable( getResources(), bitmap ) );
        }

        @Override
        public void onBitmapFailed( Drawable errorDrawable )
        {
            getSupportActionBar().setIcon( R.drawable.ic_launcher );
        }

        @Override
        public void onPrepareLoad( Drawable placeHolderDrawable )
        {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawableTransformedImage = (ImageView) findViewById( R.id.transformed_drawable );
        mDrawableUrlImage = (ImageView) findViewById( R.id.url_drawable );
        mDrawableUrlTransformedImage = (ImageView) findViewById( R.id.transformed_url_drawable );

        Picasso.with( this )
                .load( R.drawable.ic_star )
                .transform( new ColorTransformation(getResources().getColor( R.color.local_drawable_color ) ) )
                .into( mDrawableTransformedImage );

        Picasso.with( this )
                .load( getString( R.string.image_url ) )
                .into( mDrawableUrlImage );

        Picasso.with( this )
                .load( getString( R.string.image_url ) )
                .transform( new ColorTransformation( getResources().getColor( R.color.remote_image_color ) ) )
                .into( mDrawableUrlTransformedImage );

        Picasso.with( this )
                .load( R.drawable.ic_star )
                .transform( new ColorTransformation( getResources().getColor( R.color.action_bar_icon_color ) ) )
                .into( ActionBarIconTarget );
    }

}
