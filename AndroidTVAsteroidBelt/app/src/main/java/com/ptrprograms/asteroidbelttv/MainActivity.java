package com.ptrprograms.asteroidbelttv;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class MainActivity extends Activity {

    private GameView mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGame = new GameView( this );
        setContentView( mGame );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGame.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGame.onPause();
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return mGame.handleMotionEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mGame.handleKeyEvent(event);
    }
}
