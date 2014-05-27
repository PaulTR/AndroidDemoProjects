package com.ptrprograms.flashlight;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private boolean mFlashlightOn = false;
	private Camera mCamera;
	private Camera.Parameters mParameters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initFlashlightButton();
	}

	private void initFlashlightButton() {
		ToggleButton button = (ToggleButton) findViewById( R.id.button_flashlight );
		button.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if( getPackageManager().hasSystemFeature( PackageManager.FEATURE_CAMERA_FLASH ) )
				{
					if( mFlashlightOn )
						deactivateFlashlight();
					else
						activateFlashlight();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( mCamera == null )
			return;

		mCamera.release();
	}

	private void deactivateFlashlight()
	{
		if( mCamera == null || mParameters == null )
			return;

		mParameters = mCamera.getParameters();
		mParameters.setFlashMode( Camera.Parameters.FLASH_MODE_OFF );
		mCamera.setParameters( mParameters );
		mFlashlightOn = false;
	}

	private void activateFlashlight()
	{
		if( mCamera == null )
			mCamera = Camera.open();

		mParameters = mCamera.getParameters();
		mParameters.setFlashMode( Camera.Parameters.FLASH_MODE_TORCH );
		mCamera.setParameters(mParameters);
		mFlashlightOn = true;
	}
}
