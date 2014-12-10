package com.ptrprograms.eventdrivenhierarchicalviews.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ptrprograms.eventdrivenhierarchicalviews.model.Weather;
import com.ptrprograms.eventdrivenhierarchicalviews.util.Updateable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulruiz on 12/9/14.
 */
public class UpdateableLinearLayout extends LinearLayout implements Updateable {

    public List<Updateable> mUpdateableViews;

    public UpdateableLinearLayout(Context context) {
        this(context, null);
    }

    public UpdateableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUpdateableViews = findTopLevelUpdateables( this );
    }

    public List<Updateable> findTopLevelUpdateables( ViewGroup view ) {
        ArrayList<Updateable> results = new ArrayList<Updateable>();

        int childCount = view.getChildCount();
        for( int i = 0; i < childCount; i++ ) {
            results = findTopLevelUpdateables( view.getChildAt(i), results );
        }
        return results;
    }

    protected ArrayList<Updateable> findTopLevelUpdateables( View view,
                                                            ArrayList<Updateable> results ) {

        if( ( view instanceof ViewGroup ) && !( view instanceof Updateable ) ) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                findTopLevelUpdateables(viewGroup.getChildAt(i), results);
            }
        }

        Updateable result = (view != null && view instanceof Updateable) ? (Updateable) view : null;
        if( result != null ) {
            results.add( result );
        }
        results.trimToSize();
        return results;
    }

    @Override
    public void update( Weather weather ) {
        Log.e("UpdateableLinearLayout", "Update!" );
        if( weather != null && mUpdateableViews != null && !mUpdateableViews.isEmpty() ) {
            for( Updateable view : mUpdateableViews ) {
                view.update( weather );
            }
        }
    }
}
