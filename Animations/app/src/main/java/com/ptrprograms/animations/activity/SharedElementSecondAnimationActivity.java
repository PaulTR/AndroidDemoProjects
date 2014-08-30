package com.ptrprograms.animations.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.ptrprograms.animations.R;

/**
 * Created by paulruiz on 8/23/14.
 */
public class SharedElementSecondAnimationActivity extends Activity {

    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_element_second_animation);
        mImageView = (ImageView) findViewById( R.id.image );
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //Bitmap bitmap = SharedElementAnimationActivity.sPhotoCache.get( getIntent().getIntExtra("image", R.drawable.ic_launcher) );
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }
}
