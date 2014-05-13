package com.ptrprograms.staggeredgridview.activities;

import android.app.Activity;
import android.os.Bundle;

import com.etsy.android.grid.StaggeredGridView;
import com.ptrprograms.staggeredgridview.R;
import com.ptrprograms.staggeredgridview.adapters.StaggeredGridAdapter;
import com.ptrprograms.staggeredgridview.utils.ItemGenerator;

public class MainActivity extends Activity {

	StaggeredGridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGridView = (StaggeredGridView) findViewById( R.id.grid_view );
		StaggeredGridAdapter adapter = new StaggeredGridAdapter( this );

		for( int i = 0; i < 30; i++ ) {
			adapter.add( ItemGenerator.getItem(i) );
		}
		mGridView.setAdapter( adapter );
	}

}
