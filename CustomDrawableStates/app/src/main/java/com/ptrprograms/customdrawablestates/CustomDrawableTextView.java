package com.ptrprograms.customdrawablestates;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by paulruiz on 1/11/15.
 */
public class CustomDrawableTextView extends TextView {

    protected static int[] STATE_GO = { R.attr.state_go };
    protected static int[] STATE_SLOW_DOWN = { R.attr.state_slow_down };
    protected static int[] STATE_STOP = { R.attr.state_stop };

    private CustomState mState;

    public CustomDrawableTextView(Context context) {
        super(context);
    }

    public CustomDrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void update( CustomState state ) {

        if( state != null )
            mState = state;

        if( CustomState.GO.equals( mState ) ) {
            setText( "GO" );
        } else if( CustomState.SLOW_DOWN.equals( mState ) ) {
            setText( "SLOW DOWN" );
        } else if( CustomState.STOP.equals( mState) ) {
            setText( "STOP" );
        }

        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        if( mState == null )
            return super.onCreateDrawableState(extraSpace);

        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

        if( CustomState.GO.equals( mState ) ) {
            mergeDrawableStates( drawableState, STATE_GO );
            return drawableState;
        } else if( CustomState.SLOW_DOWN.equals( mState ) ) {
            mergeDrawableStates( drawableState, STATE_SLOW_DOWN );
            return drawableState;
        } else if( CustomState.STOP.equals( mState) ) {
            mergeDrawableStates( drawableState, STATE_STOP );
            return drawableState;
        } else {
            return super.onCreateDrawableState(extraSpace);
        }
    }

}
