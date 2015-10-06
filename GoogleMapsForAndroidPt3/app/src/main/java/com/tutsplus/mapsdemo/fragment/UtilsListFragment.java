package com.tutsplus.mapsdemo.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tutsplus.mapsdemo.R;
import com.tutsplus.mapsdemo.activity.ClusterMarkerActivity;
import com.tutsplus.mapsdemo.activity.HeatMapActivity;
import com.tutsplus.mapsdemo.activity.PolylineActivity;
import com.tutsplus.mapsdemo.activity.SphericalGeometryActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Paul on 9/7/15.
 */
public class UtilsListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_1 );
        String[] items = getResources().getStringArray( R.array.list_items );
        adapter.addAll( new ArrayList( Arrays.asList(items) ) );
        setListAdapter( adapter );
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String item = ( (TextView) v ).getText().toString();
        if( getString( R.string.item_clustering ).equalsIgnoreCase( item ) ) {
            startActivity( new Intent( getActivity(), ClusterMarkerActivity.class ) );
        } else if( getString( R.string.item_heat_map ).equalsIgnoreCase( item ) ) {
            startActivity( new Intent( getActivity(), HeatMapActivity.class ) );
        } else if( getString( R.string.item_polylines ).equalsIgnoreCase( item ) ) {
            startActivity( new Intent( getActivity(), PolylineActivity.class ) );
        } else if( getString( R.string.item_spherical_geometry ).equalsIgnoreCase( item ) ) {
            startActivity( new Intent( getActivity(), SphericalGeometryActivity.class ) );
        }
    }
}
