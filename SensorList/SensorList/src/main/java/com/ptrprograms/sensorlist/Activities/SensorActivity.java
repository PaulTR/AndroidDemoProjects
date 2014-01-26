package com.ptrprograms.sensorlist.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.ptrprograms.sensorlist.R;
import com.ptrprograms.sensorlist.Fragments.SensorListFragment;

public class SensorActivity extends ActionBarActivity {

	private final String SENSOR_LIST = "SENSOR_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        if (savedInstanceState == null) {
            loadFragment( SENSOR_LIST );
        }
    }

	private void loadFragment( String fragmentName ) {
		if( R.id.container == 0 || TextUtils.isEmpty( fragmentName ) )
			return;

		if( fragmentName == SENSOR_LIST ) {
			getSupportFragmentManager().beginTransaction()
					.add( R.id.container, SensorListFragment.newInstance())
					.commit();
		}
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
