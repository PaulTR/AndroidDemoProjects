package com.ptrprograms.navigationdrawer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by PaulTR on 5/12/14.
 */
public class NumberListFragment extends ListFragment {

	public static Fragment getInstance() {
		NumberListFragment fragment = new NumberListFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayAdapter<String> adapter = new ArrayAdapter( getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>() );
		for( int i = 0; i < 30; i++ ) {
			adapter.add( Integer.toString( i + 1 ) );
		}
		setListAdapter( adapter );
	}
}
