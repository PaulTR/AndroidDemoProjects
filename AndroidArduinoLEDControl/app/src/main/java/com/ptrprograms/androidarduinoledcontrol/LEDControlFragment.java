package com.ptrprograms.androidarduinoledcontrol;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Paul Trebilcox-Ruiz on 7/27/14.
 */
public class LEDControlFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static final String EXTRA_ACCESSORY = "extra_accessory";
    private static final String ACTION_USB_PERMISSION = "com.ptrprograms.androidarduinoledcontrol.action.USB_PERMISSION";

    private UsbManager mUsbManager;
    private UsbAccessory mUsbAccessory;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;
    private ParcelFileDescriptor mFileDescriptor;

    private FileOutputStream mOutputStream;

    private TextView mRedLEDValueTV;
    private TextView mGreenLEDValueTV;
    private TextView mBlueLEDValueTV;

    private SeekBar mRedLEDSeekBar;
    private SeekBar mGreenLEDSeekBar;
    private SeekBar mBlueLEDSeekBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsbManager = (UsbManager) getActivity().getSystemService( Context.USB_SERVICE );
        mPermissionIntent = PendingIntent.getBroadcast( getActivity(), 0, new Intent( ACTION_USB_PERMISSION ), 0 );
        IntentFilter filter = new IntentFilter( ACTION_USB_PERMISSION );
        filter.addAction( UsbManager.ACTION_USB_ACCESSORY_DETACHED );
        getActivity().registerReceiver( mUsbReceiver, filter );

        if( savedInstanceState != null && savedInstanceState.containsKey( EXTRA_ACCESSORY ) ) {
            mUsbAccessory = savedInstanceState.getParcelable( EXTRA_ACCESSORY );
            openAccessory( mUsbAccessory );
        }

        setRetainInstance( true );

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_main, container );
        mRedLEDValueTV = (TextView) view.findViewById( R.id.red_led_value );
        mRedLEDSeekBar = (SeekBar) view.findViewById( R.id.red_led_seek_bar );
        mRedLEDSeekBar.setOnSeekBarChangeListener( this );

        mGreenLEDValueTV = (TextView) view.findViewById( R.id.green_led_value );
        mGreenLEDSeekBar = (SeekBar) view.findViewById( R.id.green_led_seek_bar );
        mGreenLEDSeekBar.setOnSeekBarChangeListener( this );

        mBlueLEDValueTV = (TextView) view.findViewById( R.id.blue_led_value );
        mBlueLEDSeekBar = (SeekBar) view.findViewById( R.id.blue_led_seek_bar );
        mBlueLEDSeekBar.setOnSeekBarChangeListener( this );

        mRedLEDValueTV.setText( String.valueOf( mRedLEDSeekBar.getProgress() ) );
        mGreenLEDValueTV.setText( String.valueOf( mGreenLEDSeekBar.getProgress() ) );
        mBlueLEDValueTV.setText( String.valueOf( mBlueLEDSeekBar.getProgress() ) );

        return view;
    }

    private void openAccessory(UsbAccessory accessory) {
        if( accessory == null )
            return;

        mFileDescriptor = mUsbManager.openAccessory( accessory );
        if  (mFileDescriptor != null ) {
            mUsbAccessory = accessory;
            FileDescriptor fileDescriptor = mFileDescriptor.getFileDescriptor();
            mOutputStream = new FileOutputStream( fileDescriptor );
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if( mOutputStream != null ) {
            return;
        }

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = ( accessories == null ? null : accessories[0] );
        if( accessory != null ) {
            if( mUsbManager.hasPermission( accessory ) ) {
                openAccessory( accessory );
            } else {
                synchronized( mUsbReceiver ) {
                    if( !mPermissionRequestPending ) {
                        mUsbManager.requestPermission( accessory, mPermissionIntent );
                        mPermissionRequestPending = true;
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        closeAccessory();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver( mUsbReceiver );
        super.onDestroy();
    }

    private void closeAccessory() {
        try {
            if( mFileDescriptor != null ) {
                mFileDescriptor.close();
            }
        } catch ( IOException e ) {
        } finally {
            mFileDescriptor = null;
            mUsbAccessory = null;
        }
    }


    @Override
    public void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState( outState );
        outState.putParcelable( EXTRA_ACCESSORY, mUsbAccessory );
    }

    private void writeToArduin( byte LEDNum, int value ) {
        byte[] buffer = new byte[2];
        if( value > 255 ) {
            value = 255;
        } else if( value < 0 ) {
            value = 0;
        }

        buffer[0] = LEDNum;
        buffer[1] = (byte) value;

        if( mOutputStream != null ) {
            try {
                mOutputStream.write( buffer );
            } catch( IOException e ) {
                Toast.makeText( getActivity(), "Something went wrong when writing to the Arduino", Toast.LENGTH_LONG ).show();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch( seekBar.getId() ) {
            case R.id.red_led_seek_bar: {
                mRedLEDValueTV.setText( String.valueOf( seekBar.getProgress() ) );
                writeToArduin( (byte) 0, seekBar.getProgress() );
                break;
            }
            case R.id.green_led_seek_bar: {
                mGreenLEDValueTV.setText( String.valueOf( seekBar.getProgress() ) );
                writeToArduin( (byte) 1, seekBar.getProgress() );
                break;
            }
            case R.id.blue_led_seek_bar: {
                mBlueLEDValueTV.setText( String.valueOf( seekBar.getProgress() ) );
                writeToArduin( (byte) 2, seekBar.getProgress() );
                break;
            }
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if( ACTION_USB_PERMISSION.equals( action ) ) {
                UsbAccessory accessory = intent.getParcelableExtra( UsbManager.EXTRA_ACCESSORY );
                if( intent.getBooleanExtra( UsbManager.EXTRA_PERMISSION_GRANTED, false ) ) {
                    openAccessory( accessory );
                }

                mPermissionRequestPending = false;
            } else if( UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals( action ) ) {
                UsbAccessory accessory = intent.getParcelableExtra( UsbManager.EXTRA_ACCESSORY );
                if( accessory != null && accessory.equals( mUsbAccessory ) ) {
                    closeAccessory();
                }
            }
        }
    };

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
