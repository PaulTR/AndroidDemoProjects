package com.ptrprograms.maps.interfaces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by PaulTR on 2/9/14.
 */
public interface mapListener {
	public void playServicesUnavailable();
	public void longClickedMap( LatLng latLng );
}
