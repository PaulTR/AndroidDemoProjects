package com.ptrprograms.fordapplinkaudio.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.ford.syncV4.proxy.SyncProxyALM;
import com.ptrprograms.fordapplinkaudio.R;
import com.ptrprograms.fordapplinkaudio.Service.AppLinkService;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		startSyncProxyService();
    }

	private void startSyncProxyService() {
		boolean isPaired = false;
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

		if( btAdapter != null ) {
			if( btAdapter.isEnabled() && btAdapter.getBondedDevices() != null && !btAdapter.getBondedDevices().isEmpty() ) {
				for( BluetoothDevice device : btAdapter.getBondedDevices() ) {
					if( device.getName() != null && device.getName().contains( getString( R.string.device_name ) ) ) {
						isPaired = true;
						break;
					}
				}
			}

			if( isPaired ) {
				if( AppLinkService.getInstance() == null ) {
					Intent appLinkServiceIntent = new Intent( this, AppLinkService.class );
					startService( appLinkServiceIntent );
				} else {
					SyncProxyALM proxyInstance = AppLinkService.getInstance().getProxy();
					if( proxyInstance == null ) {
						AppLinkService.getInstance().startProxy();
					}
				}
			}
		}
	}

	private void endSyncProxyInstance() {
		if( AppLinkService.getInstance() != null ) {
			SyncProxyALM proxy = AppLinkService.getInstance().getProxy();
			if( proxy != null ) {
				AppLinkService.getInstance().reset();
			} else {
				AppLinkService.getInstance().startProxy();
			}
		}
	}

	@Override
	protected void onDestroy() {
		endSyncProxyInstance();
		super.onDestroy();
	}

}
