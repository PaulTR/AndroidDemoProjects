package com.ptrprograms.animations.activity;

import android.app.Activity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Window;

import com.ptrprograms.animations.R;

public class FadeAnimationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition( new Fade() );
        getWindow().setExitTransition( new Fade() );
        setContentView(R.layout.activity_fade_animation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }
}
