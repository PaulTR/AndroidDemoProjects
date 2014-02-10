package com.ptrprograms.maps.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.ptrprograms.maps.R;
import com.ptrprograms.maps.fragments.PTRMapFragment;
import com.ptrprograms.maps.fragments.PlayServicesUnavailableFragmentDialog;
import com.ptrprograms.maps.interfaces.mapListener;

public class MainActivity extends ActionBarActivity implements mapListener {

	PTRMapFragment fragment;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
		initialize();
    }

	protected void initialize() {
		setupGoogleMapFragment();
	}

	private void setupGoogleMapFragment() {
		fragment = PTRMapFragment.newInstance();
		getSupportFragmentManager()
				.beginTransaction()
				.add( R.id.container, fragment )
				.commit();
	}

	@Override
	public void playServicesUnavailable() {
		new PlayServicesUnavailableFragmentDialog().show( getSupportFragmentManager(), "" );
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void longClickedMap( LatLng latLng ) {
		fragment.addMarker( latLng );
	}

}
