package com.ptrprograms.animations.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.ptrprograms.animations.R;

/**
 * Created by paulruiz on 8/18/14.
 */
public class CircularRevealActivity extends Activity {

    private ImageView mImageView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_circular_reveal);
        mImageView = (ImageView) findViewById( R.id.image );
        mButton = (Button) findViewById( R.id.button );
        mButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( mImageView.getVisibility() == View.VISIBLE ) {
                    hideImageCircular();
                } else {
                    revealImageCircular();
                }
            }
        });
    }

    private void hideImageCircular() {
        int x = getX();
        int y = getY();
        int radius = getRadius();

        ValueAnimator anim =
                ViewAnimationUtils.createCircularReveal(mImageView, x, y, radius, 0);

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mImageView.setVisibility( View.INVISIBLE );
            }
        });

        anim.start();
    }

    private void revealImageCircular() {
        int x = getX();
        int y = getY();
        int radius = getRadius();

        ValueAnimator anim =
                ViewAnimationUtils.createCircularReveal(mImageView, x, y, 0, radius);

        anim.setDuration( 1000 );
        anim.addListener( new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mImageView.setVisibility( View.VISIBLE );
            }
        });

        anim.start();
    }

    private int getX() {
        return ( mImageView.getLeft() + mImageView.getRight() ) / 2;
    }

    private int getY() {
        return ( mImageView.getTop() + mImageView.getBottom() ) / 2;
    }

    private int getRadius() {
        return mImageView.getWidth();
    }
}
