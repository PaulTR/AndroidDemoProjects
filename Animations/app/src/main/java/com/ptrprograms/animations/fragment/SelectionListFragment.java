package com.ptrprograms.animations.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ptrprograms.animations.activity.CircularRevealActivity;
import com.ptrprograms.animations.activity.ExplodeAnimationActivity;
import com.ptrprograms.animations.activity.FadeAnimationActivity;
import com.ptrprograms.animations.activity.SharedElementAnimationActivity;
import com.ptrprograms.animations.activity.SlidingAnimationActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulruiz on 8/18/14.
 */
public class SelectionListFragment extends ListFragment {

    private final String CATEGORY_CIRCULAR_REVEAL = "Circular Reveal";
    private final String CATEGORY_ACTIVITY_TRANSITION_EXPLODE = "Activity Transition: Explode";
    private final String CATEGORY_ACTIVITY_TRANSITION_SLIDE = "Activity Transition: Slide";
    private final String CATEGORY_ACTIVITY_TRANSITION_FADE = "Activity Transition: Fade";
    private final String CATEGORY_SHARED_ELEMENT_TRANSITION_CHANGE_BOUNDS = "Shared Element Transition: Change Bounds";

    private ArrayAdapter<String> mAdapter;

    public static SelectionListFragment getInstance() {
        SelectionListFragment fragment = new SelectionListFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_1, getCategories() );
        setListAdapter(mAdapter);
    }

    private List<String> getCategories() {
        List<String> categories = new ArrayList<String>();

        categories.add( CATEGORY_CIRCULAR_REVEAL );
        categories.add( CATEGORY_ACTIVITY_TRANSITION_EXPLODE );
        categories.add( CATEGORY_ACTIVITY_TRANSITION_SLIDE );
        categories.add( CATEGORY_ACTIVITY_TRANSITION_FADE );
        categories.add( CATEGORY_SHARED_ELEMENT_TRANSITION_CHANGE_BOUNDS );

        return categories;
    }

    @Override
    public void onPause() {
        super.onPause();
        getListView().setDividerHeight( 0 );
    }

    @Override
    public void onResume() {
        super.onResume();
        getListView().setDividerHeight( 1 );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick( ListView l, View v, int position, long id ) {
        super.onListItemClick( l, v, position, id );
        getActivity().getWindow().setExitTransition( null );
        getActivity().getWindow().setEnterTransition( null );

        String text = ( (TextView) v ).getText().toString();
        Intent intent = null;
        if( text.equalsIgnoreCase( CATEGORY_CIRCULAR_REVEAL ) ) {
            intent = new Intent( getActivity(), CircularRevealActivity.class );
        }
        else if( text.equalsIgnoreCase( CATEGORY_ACTIVITY_TRANSITION_EXPLODE ) ) {
            getActivity().getWindow().setExitTransition( new Explode() );
            intent = new Intent( getActivity(), ExplodeAnimationActivity.class );
        }
        else if( text.equalsIgnoreCase( CATEGORY_ACTIVITY_TRANSITION_SLIDE ) ) {
            getActivity().getWindow().setExitTransition( new Slide() );
            intent = new Intent( getActivity(), SlidingAnimationActivity.class );
        }
        else if( text.equalsIgnoreCase( CATEGORY_ACTIVITY_TRANSITION_FADE ) ) {
            getActivity().getWindow().setExitTransition( new Fade() );
            intent = new Intent( getActivity(), FadeAnimationActivity.class );
        }
        else if( text.equalsIgnoreCase( CATEGORY_SHARED_ELEMENT_TRANSITION_CHANGE_BOUNDS ) ) {
            intent = new Intent( getActivity(), SharedElementAnimationActivity.class );
        }

        if( intent != null )
            startActivity( intent );
    }
}
