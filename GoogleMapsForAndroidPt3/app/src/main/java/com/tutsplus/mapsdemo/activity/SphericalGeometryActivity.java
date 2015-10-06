package com.tutsplus.mapsdemo.activity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.tutsplus.mapsdemo.R;

import java.util.Arrays;

/**
 * Created by Paul on 9/7/15.
 */
public class SphericalGeometryActivity extends BaseMapActivity implements GoogleMap.OnMarkerDragListener {

    private Marker mMarker1;
    private Marker mMarker2;
    private Polyline mPolyline;

    @Override
    protected void initMapSettings() {
        mGoogleMap.setOnMarkerDragListener( this );

        MarkerOptions options = new MarkerOptions();
        options.position( mCenterLocation );
        options.draggable(true);
        options.icon(BitmapDescriptorFactory.defaultMarker());
        mMarker1 = mGoogleMap.addMarker(options);

        options = new MarkerOptions();
        options.position( new LatLng( mCenterLocation.latitude - 0.3, mCenterLocation.longitude + 0.3 ) );
        options.draggable(true);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMarker2 = mGoogleMap.addMarker( options );

        //https://www.google.com/search?q=geodesic
        mPolyline = mGoogleMap.addPolyline(new PolylineOptions().geodesic(true));

        updateLine();
    }

    @Override
    protected float getInitialMapZoomLevel() {
        return 10.0f;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        updateLine();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        updateLine();
    }

    private void updateLine() {
        mPolyline.setPoints(Arrays.asList(mMarker1.getPosition(), mMarker2.getPosition()));
    }

    private void showDistance() {
        double distance = SphericalUtil.computeDistanceBetween( mMarker1.getPosition(), mMarker2.getPosition() );
        if( distance < 1000 ) {
            Toast.makeText(this, String.format( "%4.2f%s", distance, "m" ), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, String.format("%4.3f%s", distance/1000, "km"), Toast.LENGTH_LONG).show();
        }
    }

    private void showHeading() {
        double heading = SphericalUtil.computeHeading( mMarker1.getPosition(), mMarker2.getPosition() );
        Toast.makeText( this, "Heading: " + heading, Toast.LENGTH_LONG ).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_spherical_utils, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_distance:
                showDistance();
                return true;
            case R.id.action_heading: {
                showHeading();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
