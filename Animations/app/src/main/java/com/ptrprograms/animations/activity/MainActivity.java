package com.ptrprograms.animations.activity;

import android.app.Activity;
import android.os.Bundle;
import android.transition.Transition;
import android.view.Window;

import com.ptrprograms.animations.R;
import com.ptrprograms.animations.fragment.SelectionListFragment;


public class MainActivity extends Activity implements Transition.TransitionListener {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        getWindow().requestFeature( Window.FEATURE_CONTENT_TRANSITIONS );
        setContentView( R.layout.activity_main );
        getFragmentManager().beginTransaction().replace( R.id.container, SelectionListFragment.getInstance() ).commit();

        //Can add listeners for the transition state
        getWindow().getEnterTransition().addListener( this );
    }

    @Override
    public void onTransitionStart(Transition transition) {

    }

    @Override
    public void onTransitionEnd(Transition transition) {

    }

    @Override
    public void onTransitionCancel(Transition transition) {

    }

    @Override
    public void onTransitionPause(Transition transition) {

    }

    @Override
    public void onTransitionResume(Transition transition) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( getWindow() != null && getWindow().getEnterTransition() != null )
            getWindow().getEnterTransition().removeListener( this );
    }
}
