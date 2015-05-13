package com.tutsplus.vectordrawables;

import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;


public class MainActivity extends Activity {

    private ImageView mArrowImageView;
    private ImageView mCpuImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCpuImageView = (ImageView) findViewById( R.id.cpu );
        mArrowImageView = (ImageView) findViewById( R.id.left_right_arrow );

        Drawable drawable = mCpuImageView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }

        mArrowImageView = (ImageView) findViewById( R.id.left_right_arrow );
        drawable = mArrowImageView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }
}
