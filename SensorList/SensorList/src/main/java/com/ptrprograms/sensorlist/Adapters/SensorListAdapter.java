package com.ptrprograms.sensorlist.Adapters;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by PaulTR on 1/26/14.
 */
public class SensorListAdapter extends ArrayAdapter<Sensor> {

	public SensorListAdapter( Context context ) {
		this( context, 0 );
	}

	public SensorListAdapter(Context context, int resource) {
		super(context, resource);
	}

	public SensorListAdapter(Context context, int resource, List<Sensor> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( convertView == null ) {
			convertView = LayoutInflater
					.from( getContext() )
					.inflate( android.R.layout.simple_list_item_1, null );
		}

		( (TextView) convertView ).setText( getItem( position ).getName() );
		return convertView;
	}
}
