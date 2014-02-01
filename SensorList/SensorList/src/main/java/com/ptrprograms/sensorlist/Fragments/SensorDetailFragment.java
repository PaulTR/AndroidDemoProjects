package com.ptrprograms.sensorlist.Fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.ptrprograms.sensorlist.R;

/**
 * Created by PaulTR on 1/26/14.
 */
public class SensorDetailFragment extends Fragment implements SensorEventListener {

	private final static String EXTRA_SENSOR_TYPE = "extra_sensor_type";

	private Sensor mSensor;
	private SensorManager mSensorManager;

	private TextView mMaxRange;
	private TextView mMinDelay;
	private TextView mName;
	private TextView mType;
	private TextView mPower;
	private TextView mResolution;
	private TextView mVendor;
	private TextView mVersion;
	private TextView mAccuracy;
	private TextView mValue1;
	private TextView mValue2;
	private TextView mValue3;

	private TableRow mNameRow;
	private TableRow mTypeRow;
	private TableRow mPowerRow;
	private TableRow mResolutionRow;
	private TableRow mVendorRow;
	private TableRow mVersionRow;
	private TableRow mMaxRangeRow;
	private TableRow mMinDelayRow;
	private TableRow mAccuracyRow;
	private TableRow mValue1Row;
	private TableRow mValue2Row;
	private TableRow mValue3Row;

	public static SensorDetailFragment newInstance( Sensor sensor ) {
		SensorDetailFragment mFragment = new SensorDetailFragment();
		Bundle args = new Bundle();
		args.putInt( EXTRA_SENSOR_TYPE , sensor.getType() );
		mFragment.setArguments(args);
		return mFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSensor( savedInstanceState );
	}

	@Override
	public void onStart() {
		super.onStart();
		mSensorManager.registerListener( SensorDetailFragment.this, mSensor, SensorManager.SENSOR_DELAY_UI );
	}

	@Override
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener( this );
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if( hidden )
			mSensorManager.unregisterListener( this );
	}

	private void initSensor( Bundle savedInstanceState ) {
		int type = 0;

		if( getArguments() != null && getArguments().containsKey( EXTRA_SENSOR_TYPE )) {
			type = getArguments().getInt( EXTRA_SENSOR_TYPE );
		}
		if( savedInstanceState != null && savedInstanceState.containsKey( EXTRA_SENSOR_TYPE )) {
			type = savedInstanceState.getInt( EXTRA_SENSOR_TYPE );
		}

		mSensorManager = (SensorManager) getActivity().getSystemService( Context.SENSOR_SERVICE );
		mSensor = mSensorManager.getDefaultSensor( type );
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViews( view );
		updateRelevantFields();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate( R.layout.fragment_sensor_display, container, false );
	}

	private void initViews( View view ) {
		mNameRow = (TableRow) view.findViewById( R.id.sensor_name_row );
		mName = (TextView) view.findViewById( R.id.sensor_name_value );

		mTypeRow = (TableRow) view.findViewById( R.id.sensor_type_row );
		mType = (TextView) view.findViewById( R.id.sensor_type_value );

		mPowerRow = (TableRow) view.findViewById( R.id.sensor_power_row );
		mPower = (TextView) view.findViewById( R.id.sensor_power_value );

		mResolutionRow = (TableRow) view.findViewById( R.id.sensor_resolution_row );
		mResolution = (TextView) view.findViewById( R.id.sensor_resolution_value );

		mVendorRow = (TableRow) view.findViewById( R.id.sensor_vendor_row );
		mVendor = (TextView) view.findViewById( R.id.sensor_vendor_value );

		mVersionRow = (TableRow) view.findViewById( R.id.sensor_version_row );
		mVersion = (TextView) view.findViewById( R.id.sensor_version_value );

		mMaxRange = (TextView) view.findViewById( R.id.sensor_max_range_value );
		mMaxRangeRow = (TableRow) view.findViewById( R.id.sensor_max_range_row );

		mMinDelay = (TextView) view.findViewById( R.id.sensor_min_delay_value );
		mMinDelayRow = (TableRow) view.findViewById( R.id.sensor_min_delay_row );

		mAccuracy = (TextView) view.findViewById( R.id.sensor_accuracy_value );
		mAccuracyRow = (TableRow) view.findViewById( R.id.sensor_accuracy_row );

		mValue1 = (TextView) view.findViewById( R.id.sensor_value1_value );
		mValue1Row = (TableRow) view.findViewById( R.id.sensor_value1_row );

		mValue2 = (TextView) view.findViewById( R.id.sensor_value2_value );
		mValue2Row = (TableRow) view.findViewById( R.id.sensor_value2_row );

		mValue3 = (TextView) view.findViewById( R.id.sensor_value3_value );
		mValue3Row = (TableRow) view.findViewById( R.id.sensor_value3_row );
	}

	private void hideAllRows() {
		if( mNameRow != null )
			mNameRow.setVisibility( View.GONE );
		if( mTypeRow != null )
			mTypeRow.setVisibility( View.GONE );
		if( mPowerRow != null )
			mPowerRow.setVisibility( View.GONE );
		if( mResolutionRow != null )
			mResolutionRow.setVisibility( View.GONE );
		if( mVendorRow != null )
			mVendorRow.setVisibility( View.GONE );
		if( mVersionRow != null )
			mVersionRow.setVisibility( View.GONE );
		if( mMaxRangeRow != null )
			mMaxRangeRow.setVisibility( View.GONE );
		if( mMinDelayRow != null )
			mMinDelayRow.setVisibility( View.GONE );
		if( mAccuracyRow != null )
			mAccuracyRow.setVisibility( View.GONE );
		if( mValue1Row != null )
			mValue1Row.setVisibility( View.GONE );
		if( mValue2Row != null )
			mValue2Row.setVisibility( View.GONE );
		if( mValue3Row != null )
			mValue3Row.setVisibility( View.GONE );
	}

	private void updateRelevantFields() {
		hideAllRows();

		if( mSensor == null )
			return;

		populateNameField( mSensor.getName() );
		populateTypeField( mSensor.getType() );
		populatePowerField( mSensor.getPower() );
		populateResolutionRow( mSensor.getResolution() );
		populateVendorField( mSensor.getVendor() );
		populateVersionRow( mSensor.getVersion() );
		populateMaxRangeRow( mSensor.getMaximumRange() );
		populateMinDelayRow( mSensor.getMinDelay() );
	}

	private void populateMinDelayRow( int minDelay ) {
		if( mMinDelayRow == null || mMinDelay == null )
			return;

		mMinDelay.setText( String.valueOf( minDelay ) );
		mMinDelayRow.setVisibility( View.VISIBLE );
	}

	private void populateMaxRangeRow( float maximumRange ) {
		if( mMaxRangeRow == null || mMaxRange == null )
			return;

		mMaxRange.setText( String.valueOf( maximumRange ) );
		mMaxRangeRow.setVisibility( View.VISIBLE );
	}

	private void populateNameField( String name ) {
		if( TextUtils.isEmpty( name ) || mNameRow == null || mName == null )
			return;

		mName.setText( name );
		mNameRow.setVisibility( View.VISIBLE );
	}

	private void populateTypeField( int type ) {
		if( type == 0 || mTypeRow == null || mType == null )
			return;

		String typeName;

		switch( type ) {
			case Sensor.TYPE_ACCELEROMETER: {
				typeName = "Accelerometer";
				break;
			}
			case Sensor.TYPE_AMBIENT_TEMPERATURE: {
				typeName = "Ambient Temperature";
				break;
			}
			case Sensor.TYPE_GAME_ROTATION_VECTOR: {
				typeName = "Game Rotation Vector";
				break;
			}
			case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR: {
				typeName = "Geomagnetic Rotation Vector";
				break;
			}
			case Sensor.TYPE_GRAVITY: {
				typeName = "Gravity";
				break;
			}
			case Sensor.TYPE_GYROSCOPE: {
				typeName = "Gyroscope";
				break;
			}
			case Sensor.TYPE_GYROSCOPE_UNCALIBRATED: {
				typeName = "Uncalibrated Gyroscope";
				break;
			}
			case Sensor.TYPE_LIGHT: {
				typeName = "Light";
				break;
			}
			case Sensor.TYPE_LINEAR_ACCELERATION: {
				typeName = "Linear Acceleration";
				break;
			}
			case Sensor.TYPE_MAGNETIC_FIELD: {
				typeName = "Magnetic Field";
				break;
			}
			case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED: {
				typeName = "Uncalibrated Magnetic Field";
				break;
			}
			case Sensor.TYPE_PRESSURE: {
				typeName = "Pressure";
				break;
			}
			case Sensor.TYPE_PROXIMITY: {
				typeName = "Proximity";
				break;
			}
			case Sensor.TYPE_RELATIVE_HUMIDITY: {
				typeName = "Relative Humidity";
				break;
			}
			case Sensor.TYPE_ROTATION_VECTOR: {
				typeName = "Rotation Vector";
				break;
			}
			case Sensor.TYPE_SIGNIFICANT_MOTION: {
				typeName = "Significant Motion";
				break;
			}
			case Sensor.TYPE_STEP_COUNTER: {
				typeName = "Step Counter";
				break;
			}
			case Sensor.TYPE_STEP_DETECTOR: {
				typeName = "Step Detector";
				break;
			}
			default: {
				typeName = "Other";
			}
		}
		mType.setText( typeName );
		mTypeRow.setVisibility( View.VISIBLE );
	}

	private void populatePowerField( float power ) {
		if( mPowerRow == null || mPower == null )
			return;

		mPower.setText( String.valueOf( power ) );
		mPowerRow.setVisibility( View.VISIBLE );
	}

	private void populateResolutionRow( float resolution ) {
		if( mResolutionRow == null || mResolution == null )
			return;

		mResolution.setText( String.valueOf( resolution ) );
		mResolutionRow.setVisibility( View.VISIBLE );
	}

	private void populateVendorField( String vendor ) {
		if( mVendorRow == null || mVendor == null )
			return;

		mVendor.setText( vendor );
		mVendorRow.setVisibility( View.VISIBLE );
	}

	private void populateVersionRow( float version ) {
		if( mVersionRow == null || mVersion == null )
			return;

		mVersion.setText( String.valueOf( version ) );
		mVersionRow.setVisibility( View.VISIBLE );
	}

	private void populateAccuracyRow( int accuracy ) {
		if( mAccuracy == null || mAccuracyRow == null )
			return;
		String accuracyText = "";

		switch( accuracy ) {
			case SensorManager.SENSOR_STATUS_ACCURACY_HIGH: {
				accuracyText = "High";
				break;
			}
			case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM: {
				accuracyText = "Medium";
				break;
			}
			case SensorManager.SENSOR_STATUS_ACCURACY_LOW: {
				accuracyText = "Low";
				break;
			}
			default: {
				accuracyText = "Unreliable";
			}
		}

		mAccuracy.setText( accuracyText );
		mAccuracyRow.setVisibility( View.VISIBLE );
	}

	private void populateValue1Row( float value ) {
		if( mValue1Row == null || mValue1 == null )
			return;

		mValue1.setText( String.valueOf( value ) );
		mValue1Row.setVisibility( View.VISIBLE );
	}

	private void populateValue2Row( float value ) {
		if( mValue2Row == null || mValue2 == null )
			return;

		mValue2.setText( String.valueOf( value ) );
		mValue2Row.setVisibility( View.VISIBLE );
	}

	private void populateValue3Row( float value ) {
		if( mValue3Row == null || mValue3 == null )
			return;

		mValue3.setText( String.valueOf( value ) );
		mValue3Row.setVisibility( View.VISIBLE );
	}

	@Override
	public void onSensorChanged( SensorEvent sensorEvent ) {
		if( sensorEvent == null || sensorEvent.values == null )
			return;

		populateValue1Row( sensorEvent.values[0] );
		populateValue2Row( sensorEvent.values[1] );
		populateValue3Row( sensorEvent.values[2] );

	}


	@Override
	public void onAccuracyChanged( Sensor sensor, int accuracy ) {
		populateAccuracyRow( accuracy );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		if( mSensor == null || outState == null )
			return;
		outState.putInt( EXTRA_SENSOR_TYPE, mSensor.getType() );
	}
}
