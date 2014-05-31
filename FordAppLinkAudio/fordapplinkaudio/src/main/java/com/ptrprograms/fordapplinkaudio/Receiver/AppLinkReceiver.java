package com.ptrprograms.fordapplinkaudio.Receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.ptrprograms.fordapplinkaudio.R;
import com.ptrprograms.fordapplinkaudio.Service.AppLinkService;

/**
 * Created by PaulTR on 5/31/14.
 */
public class AppLinkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if( intent == null || intent.getAction() == null || context == null )
			return;

		BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
		String action = intent.getAction();

		Intent serviceIntent = new Intent( context, AppLinkService.class );
		serviceIntent.putExtras( intent );


		//Should start service
		if( action.compareTo(BluetoothDevice.ACTION_ACL_CONNECTED) == 0 &&
				device != null &&
				device.getName() != null &&
				device.getName().contains( context.getString( R.string.device_name ) ) &&
				AppLinkService.getInstance() == null )
		{
			context.startService(serviceIntent);
		}

		else if( action.equals( Intent.ACTION_BOOT_COMPLETED ) &&
				BluetoothAdapter.getDefaultAdapter() != null &&
				BluetoothAdapter.getDefaultAdapter().isEnabled() ) {
			context.startService(serviceIntent);

		}

		//Should stop service
		else if( action.equals( BluetoothDevice.ACTION_ACL_DISCONNECTED ) &&
				device != null &&
				device.getName() != null &&
				device.getName().contains( context.getString( R.string.device_name ) ) &&
				AppLinkService.getInstance() != null )
		{
			context.stopService( intent );
		}

		else if( action.equals(BluetoothAdapter.ACTION_STATE_CHANGED ) &&
			intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_TURNING_OFF &&
			AppLinkService.getInstance() != null )
		{
			context.stopService( serviceIntent );
		}

		else if( action.equals( AudioManager.ACTION_AUDIO_BECOMING_NOISY ) ) {
			context.stopService( serviceIntent );
		}
	}
}
