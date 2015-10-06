package com.tutsplus.mapsdemo.activity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;

/**
 * Created by Paul on 9/7/15.
 */
public class PolylineActivity extends BaseMapActivity {

    private static final String polyline = "gsqqFxxu_SyRlTys@npAkhAzY{MsVc`AuHwbB}Lil@}[goCqGe|BnUa`A~MkbG?eq@hRq}@_N}vKdB";

    @Override
    protected void initMapSettings() {
        List<LatLng> decodedPath = PolyUtil.decode(polyline);

        mGoogleMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }
}
