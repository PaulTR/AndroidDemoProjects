package com.ptrprograms.fordapplinkaudio.Service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.exception.SyncExceptionCause;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM;
import com.ford.syncV4.proxy.rpc.AddCommandResponse;
import com.ford.syncV4.proxy.rpc.AddSubMenuResponse;
import com.ford.syncV4.proxy.rpc.AlertResponse;
import com.ford.syncV4.proxy.rpc.ChangeRegistrationResponse;
import com.ford.syncV4.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteCommandResponse;
import com.ford.syncV4.proxy.rpc.DeleteFileResponse;
import com.ford.syncV4.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteSubMenuResponse;
import com.ford.syncV4.proxy.rpc.EncodedSyncPDataResponse;
import com.ford.syncV4.proxy.rpc.EndAudioPassThruResponse;
import com.ford.syncV4.proxy.rpc.GenericResponse;
import com.ford.syncV4.proxy.rpc.GetDTCsResponse;
import com.ford.syncV4.proxy.rpc.GetVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.ListFilesResponse;
import com.ford.syncV4.proxy.rpc.OnAudioPassThru;
import com.ford.syncV4.proxy.rpc.OnButtonEvent;
import com.ford.syncV4.proxy.rpc.OnButtonPress;
import com.ford.syncV4.proxy.rpc.OnCommand;
import com.ford.syncV4.proxy.rpc.OnDriverDistraction;
import com.ford.syncV4.proxy.rpc.OnEncodedSyncPData;
import com.ford.syncV4.proxy.rpc.OnHMIStatus;
import com.ford.syncV4.proxy.rpc.OnLanguageChange;
import com.ford.syncV4.proxy.rpc.OnPermissionsChange;
import com.ford.syncV4.proxy.rpc.OnSyncPData;
import com.ford.syncV4.proxy.rpc.OnTBTClientState;
import com.ford.syncV4.proxy.rpc.OnVehicleData;
import com.ford.syncV4.proxy.rpc.PerformAudioPassThruResponse;
import com.ford.syncV4.proxy.rpc.PerformInteractionResponse;
import com.ford.syncV4.proxy.rpc.PutFileResponse;
import com.ford.syncV4.proxy.rpc.ReadDIDResponse;
import com.ford.syncV4.proxy.rpc.ResetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.ScrollableMessageResponse;
import com.ford.syncV4.proxy.rpc.SetAppIconResponse;
import com.ford.syncV4.proxy.rpc.SetDisplayLayoutResponse;
import com.ford.syncV4.proxy.rpc.SetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.SetMediaClockTimerResponse;
import com.ford.syncV4.proxy.rpc.ShowResponse;
import com.ford.syncV4.proxy.rpc.SliderResponse;
import com.ford.syncV4.proxy.rpc.SpeakResponse;
import com.ford.syncV4.proxy.rpc.SubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.SubscribeVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.SyncPDataResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.enums.ButtonName;
import com.ford.syncV4.proxy.rpc.enums.HMILevel;
import com.ford.syncV4.proxy.rpc.enums.TextAlignment;
import com.ptrprograms.fordapplinkaudio.R;

import java.io.IOException;

/**
 * Created by PaulTR on 5/31/14.
 */
public class AppLinkService extends Service implements IProxyListenerALM {

	private static AppLinkService mInstance = null;
	private MediaPlayer mPlayer = null;
	private SyncProxyALM mProxy = null;
	private int mCorrelationId = 0;

	public static AppLinkService getInstance() {
		return mInstance;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if( intent != null &&
			BluetoothAdapter.getDefaultAdapter() != null &&
			BluetoothAdapter.getDefaultAdapter().isEnabled() ) {
			startProxy();
		}

		return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	@Override
	public void onDestroy() {
		removeSyncProxy();
		mInstance = null;
		super.onDestroy();
	}

	private void removeSyncProxy() {
		if( mProxy == null )
			return;

		try {
			mProxy.dispose();
		} catch( SyncException e ) {}

		mProxy = null;
	}

	public void startProxy() {
		if( mProxy == null ) {
			try {
				mProxy = new SyncProxyALM( this, getString( R.string.display_title ), true, getString( R.string.app_link_id ) );
			} catch( SyncException e ) {
				if( mProxy == null ) {
					stopSelf();
				}
			}
		}
	}

	public SyncProxyALM getProxy() {
		return mProxy;
	}

	public void reset() {
		if( mProxy != null ) {
			try {
				mProxy.resetProxy();
			} catch( SyncException e ) {
				if( mProxy == null )
					stopSelf();
			}
		} else {
			startProxy();
		}
	}

	@Override
	public void onOnHMIStatus(OnHMIStatus onHMIStatus) {
		switch( onHMIStatus.getSystemContext() ) {
			case SYSCTXT_MAIN:
			case SYSCTXT_VRSESSION:
			case SYSCTXT_MENU:
				break;
			default:
				return;
		}

		switch( onHMIStatus.getAudioStreamingState() ) {
			case AUDIBLE: {
				playAudio();
				break;
			}
			case NOT_AUDIBLE: {
				stopAudio();
				break;
			}
		}

		if( mProxy == null )
			return;

		if( onHMIStatus.getHmiLevel().equals( HMILevel.HMI_FULL ) && onHMIStatus.getFirstRun() ) {
			//setup app with SYNC
			try {
				mProxy.show( "Welcome to Paul's", "Ford AppLink Demo", TextAlignment.CENTERED, mCorrelationId++ );
			} catch( SyncException e ) {}
			subscribeToButtons();
		}
	}

	private void playAudio() {
		String url = "http://173.231.136.91:8060/";
		if( mPlayer == null )
			mPlayer = new MediaPlayer();

		try {
			mProxy.show("Loading...", "", TextAlignment.CENTERED, mCorrelationId++);
		} catch( SyncException e ) {}

		mPlayer.reset();
		mPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
		try {
			mPlayer.setDataSource(url);
			mPlayer.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared( MediaPlayer mediaPlayer ) {
					mediaPlayer.start();
					try {
						mProxy.show("Playing online audio", "", TextAlignment.CENTERED, mCorrelationId++);
					} catch( SyncException e ) {}
				}
			});
			mPlayer.prepare();
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}
	}

	private void stopAudio() {
		if( mPlayer == null )
			return;
		mPlayer.pause();
		try {
			mProxy.show("Press OK", "to play audio", TextAlignment.CENTERED, mCorrelationId++);
		} catch( SyncException e ) {}
	}

	private void subscribeToButtons() {
		if( mProxy == null )
			return;

		try {
			mProxy.subscribeButton( ButtonName.OK, mCorrelationId++ );
		} catch( SyncException e ) {}
	}

	@Override
	public void onOnButtonPress(OnButtonPress notification) {
		if( ButtonName.OK == notification.getButtonName() ) {
			if( mPlayer != null ) {
				if( mPlayer.isPlaying() ) {
					stopAudio();
				} else {
					playAudio();
				}
			}
		}
	}

	@Override
	public void onProxyClosed(String s, Exception e) {
		SyncException syncException = (SyncException) e;
		if( syncException.getSyncExceptionCause() != SyncExceptionCause.SYNC_PROXY_CYCLED  &&
			syncException.getSyncExceptionCause() != SyncExceptionCause.BLUETOOTH_DISABLED ) {
			reset();
		}
	}

	@Override
	public void onError(String s, Exception e) {

	}

	@Override
	public void onGenericResponse(GenericResponse genericResponse) {

	}

	@Override
	public void onOnCommand(OnCommand onCommand) {

	}

	@Override
	public void onAddCommandResponse(AddCommandResponse addCommandResponse) {

	}

	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse addSubMenuResponse) {

	}

	@Override
	public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse createInteractionChoiceSetResponse) {

	}

	@Override
	public void onAlertResponse(AlertResponse alertResponse) {

	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse deleteCommandResponse) {

	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse deleteInteractionChoiceSetResponse) {

	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse deleteSubMenuResponse) {

	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse performInteractionResponse) {

	}

	@Override
	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse resetGlobalPropertiesResponse) {

	}

	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse setGlobalPropertiesResponse) {

	}

	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse setMediaClockTimerResponse) {

	}

	@Override
	public void onShowResponse(ShowResponse showResponse) {

	}

	@Override
	public void onSpeakResponse(SpeakResponse speakResponse) {

	}

	@Override
	public void onOnButtonEvent(OnButtonEvent onButtonEvent) {

	}

	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse subscribeButtonResponse) {

	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse unsubscribeButtonResponse) {

	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange onPermissionsChange) {

	}

	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse subscribeVehicleDataResponse) {

	}

	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse unsubscribeVehicleDataResponse) {

	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse getVehicleDataResponse) {

	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse readDIDResponse) {

	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse getDTCsResponse) {

	}

	@Override
	public void onOnVehicleData(OnVehicleData onVehicleData) {

	}

	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse performAudioPassThruResponse) {

	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse endAudioPassThruResponse) {

	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru onAudioPassThru) {

	}

	@Override
	public void onPutFileResponse(PutFileResponse putFileResponse) {

	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse deleteFileResponse) {

	}

	@Override
	public void onListFilesResponse(ListFilesResponse listFilesResponse) {

	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse setAppIconResponse) {

	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse scrollableMessageResponse) {

	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse changeRegistrationResponse) {

	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse setDisplayLayoutResponse) {

	}

	@Override
	public void onOnLanguageChange(OnLanguageChange onLanguageChange) {

	}

	@Override
	public void onSliderResponse(SliderResponse sliderResponse) {

	}

	@Override
	public void onOnDriverDistraction(OnDriverDistraction onDriverDistraction) {

	}

	@Override
	public void onEncodedSyncPDataResponse(EncodedSyncPDataResponse encodedSyncPDataResponse) {

	}

	@Override
	public void onSyncPDataResponse(SyncPDataResponse syncPDataResponse) {

	}

	@Override
	public void onOnEncodedSyncPData(OnEncodedSyncPData onEncodedSyncPData) {

	}

	@Override
	public void onOnSyncPData(OnSyncPData onSyncPData) {

	}

	@Override
	public void onOnTBTClientState(OnTBTClientState onTBTClientState) {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
