package com.ptrprograms.animations.activity;

import android.app.Activity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Window;

import com.ptrprograms.animations.R;

public class SlidingAnimationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition( new Slide() );
        getWindow().setExitTransition( new Slide() );
        setContentView(R.layout.activity_sliding_animation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }
}
