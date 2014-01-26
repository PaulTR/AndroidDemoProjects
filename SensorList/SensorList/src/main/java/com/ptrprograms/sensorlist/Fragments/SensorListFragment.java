package com.ptrprograms.sensorlist.Fragments;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.ptrprograms.sensorlist.Adapters.SensorListAdapter;
import com.ptrprograms.sensorlist.R;

/**
 * Created by PaulTR on 1/26/14.
 */
public class SensorListFragment extends ListFragment {

	private SensorListAdapter mAdapter;
	private SensorManager mSensorManager;

	public static SensorListFragment newInstance() {
		SensorListFragment mFragment = new SensorListFragment();

		return mFragment;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mAdapter = new SensorListAdapter( getActivity() );
		setListAdapter(mAdapter);

		mSensorManager = (SensorManager) getActivity().getSystemService( Context.SENSOR_SERVICE );

		for( Sensor sensor : mSensorManager.getSensorList( Sensor.TYPE_ALL ) )
			mAdapter.add( sensor );
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Sensor sensor = (Sensor) getListAdapter().getItem( position );
		SensorDetailFragment mFragment = SensorDetailFragment.newInstance( sensor );
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, mFragment)
				.addToBackStack(null)
				.commit();
	}
}
