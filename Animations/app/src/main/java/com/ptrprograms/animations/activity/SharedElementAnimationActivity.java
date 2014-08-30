package com.ptrprograms.animations.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.ptrprograms.animations.R;

import java.io.ByteArrayOutputStream;

public class SharedElementAnimationActivity extends Activity implements View.OnClickListener {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_shared_element_animation);

        Button button = (Button) findViewById( R.id.button );
        button.setOnClickListener( this );
        mImageView = (ImageView) findViewById( R.id.image );
        mImageView.setImageDrawable( getDrawable( R.drawable.ic_launcher ) );

    }

    @Override
    public void onClick(View view) {
        if( view.getId() == R.id.button ) {
            Intent intent = new Intent( this, SharedElementSecondAnimationActivity.class );

            ((ViewGroup) mImageView.getParent()).setTransitionGroup( false );

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ( (BitmapDrawable) mImageView.getDrawable() ).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
            intent.putExtra( "image", stream.toByteArray() );

            ActivityOptions options;

            try {
                options = ActivityOptions.makeSceneTransitionAnimation( this, mImageView, "image" );
            } catch( NullPointerException e ) {
                Log.e( "SharedElementAnimationChangeBoundsActivity", "Did you set your ViewNames in the layout file?" );
                return;
            }

            if( options == null ) {
                Log.e("sharedelementanimation", "Options is null. Did you set ");
            } else {
                startActivity(intent, options.toBundle());
            }
        }
    }
}
