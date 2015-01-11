package com.ptrprograms.customdrawablestates;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private List<CustomState> mCustomStates;
    private Handler mHandler;
    private CustomDrawableTextView mCustomTextView;
    private int mCurrentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        mCustomTextView = (CustomDrawableTextView) findViewById( R.id.custom_view );
    }

    private void initData() {
        mCustomStates = new ArrayList<>();
        mCustomStates.add( CustomState.GO );
        mCustomStates.add( CustomState.SLOW_DOWN );
        mCustomStates.add( CustomState.STOP );
    }

    private void startSimulation() {
        mHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if( mCurrentItem >= mCustomStates.size() )
                    mCurrentItem = 0;

                mCustomTextView.update( mCustomStates.get( mCurrentItem++ ) );

                if( mHandler != null )
                    mHandler.postDelayed( this, 3000 );
            }
        };

        runnable.run();
    }

    private void stopSimulation() {
        mHandler = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSimulation();
    }

    @Override
    protected void onPause() {
        stopSimulation();
        super.onPause();
    }
}
