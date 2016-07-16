package com.tutsplus.awarenessapi;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.snapshot.BeaconStateResult;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.BeaconState;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        AdapterView.OnItemClickListener{

    private static final List BEACON_TYPE_FILTERS = Arrays.asList(
            BeaconState.TypeFilter.with(
                    "tutsplusawarenessnamespace", //Replace this with your own app's Google project name
                    "nearby") );

    private final static String ACTION_FENCE = "action_fence";

    private final static int REQUEST_PERMISSION_RESULT_CODE = 42;

    private final static String KEY_SITTING_AT_HOME = "sitting_at_home";

    private ListView mListView;
    private String[] mItems;

    private GoogleApiClient mGoogleApiClient;

    private FenceBroadcastReceiver mFenceBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        checkLocationPermission();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();
    }

    private void initViews() {
        mListView = (ListView) findViewById( R.id.list );
        mItems = getResources().getStringArray(R.array.items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mItems );
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    private void detectBeacons() {
        if( !checkLocationPermission() ) {
            return;
        }

        Awareness.SnapshotApi.getBeaconState(mGoogleApiClient, BEACON_TYPE_FILTERS)
                .setResultCallback(new ResultCallback<BeaconStateResult>() {
                    @Override
                    public void onResult(@NonNull BeaconStateResult beaconStateResult) {
                        if (!beaconStateResult.getStatus().isSuccess()) {
                            Log.e("Test", "Could not get beacon state.");
                            return;
                        }
                        BeaconState beaconState = beaconStateResult.getBeaconState();
                        if( beaconState == null ) {
                            Log.e("Tuts+", "beacon state is null");
                        } else {
                            for(BeaconState.BeaconInfo info : beaconState.getBeaconInfo()) {
                                Log.e("Tuts+", new String(info.getContent()));
                            }
                        }
                    }
                });
    }

    private void detectHeadphones() {
        Awareness.SnapshotApi.getHeadphoneState(mGoogleApiClient)
                .setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                    @Override
                    public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                        HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();
                        if (headphoneState.getState() == HeadphoneState.PLUGGED_IN) {
                            Log.e("Tuts+", "Headphones are plugged in.");
                        } else {
                            Log.e("Tuts+", "Headphones are NOT plugged in.");
                        }
                    }
                });
    }

    private void detectActivity() {
        Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        ActivityRecognitionResult result = detectedActivityResult.getActivityRecognitionResult();
                        Log.e("Tuts+", "time: " + result.getTime());
                        Log.e("Tuts+", "elapsed time: " + result.getElapsedRealtimeMillis());
                        Log.e("Tuts+", "Most likely activity: " + result.getMostProbableActivity().toString());

                        for( DetectedActivity activity : result.getProbableActivities() ) {
                            Log.e("Tuts+", "Activity: " + activity.getType() + " Liklihood: " + activity.getConfidence() );
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    private void detectLocation() {
        if( !checkLocationPermission() ) {
            return;
        }

        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        Location location = locationResult.getLocation();

                        Log.e("Tuts+", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());

                        Log.e("Tuts+", "Provider: " + location.getProvider() + " time: " + location.getTime());

                        if( location.hasAccuracy() ) {
                            Log.e("Tuts+", "Accuracy: " + location.getAccuracy());
                        }
                        if( location.hasAltitude() ) {
                            Log.e("Tuts+", "Altitude: " + location.getAltitude());
                        }
                        if( location.hasBearing() ) {
                            Log.e("Tuts+", "Bearing: " + location.getBearing());
                        }
                        if( location.hasSpeed() ) {
                            Log.e("Tuts+", "Speed: " + location.getSpeed());
                        }
                    }
                });
    }

    private void detectNearbyPlaces() {
        if( !checkLocationPermission() ) {
            return;
        }

        Awareness.SnapshotApi.getPlaces(mGoogleApiClient)
                .setResultCallback(new ResultCallback<PlacesResult>() {
                    @Override
                    public void onResult(@NonNull PlacesResult placesResult) {
                        Place place;
                        for( PlaceLikelihood placeLikelihood : placesResult.getPlaceLikelihoods() ) {
                            place = placeLikelihood.getPlace();
                            Log.e("Tuts+", place.getName().toString() + "\n" + place.getAddress().toString() );
                            Log.e("Tuts+", "Rating: " + place.getRating() );
                            Log.e("Tuts+", "Likelihood that the user is here: " + placeLikelihood.getLikelihood() * 100 + "%");
                        }
                    }
                });
    }

    private void detectWeather() {
        if( !checkLocationPermission() ) {
            return;
        }

        Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        Weather weather = weatherResult.getWeather();
                        Log.e("Tuts+", "Temp: " + weather.getTemperature(Weather.FAHRENHEIT));
                        Log.e("Tuts+", "Feels like: " + weather.getFeelsLikeTemperature(Weather.FAHRENHEIT));
                        Log.e("Tuts+", "Dew point: " + weather.getDewPoint(Weather.FAHRENHEIT));
                        Log.e("Tuts+", "Humidity: " + weather.getHumidity() );

                        if( weather.getConditions()[0] == Weather.CONDITION_CLOUDY ) {
                            Log.e("Tuts+", "Looks like there's some clouds out there");
                        }
                    }
                });
    }

    private void createFence() {
        checkLocationPermission();

        AwarenessFence activityFence = DetectedActivityFence.during(DetectedActivityFence.STILL);
        AwarenessFence homeFence = LocationFence.in(39.92, -105.7, 100000, 1000 );

        AwarenessFence sittingAtHomeFence = AwarenessFence.and(homeFence, activityFence);

        Intent intent = new Intent(ACTION_FENCE);
        PendingIntent fencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        mFenceBroadcastReceiver = new FenceBroadcastReceiver();
        registerReceiver(mFenceBroadcastReceiver, new IntentFilter(ACTION_FENCE));

        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        builder.addFence(KEY_SITTING_AT_HOME, sittingAtHomeFence, fencePendingIntent);

        Awareness.FenceApi.updateFences( mGoogleApiClient, builder.build() );
    }

    private boolean checkLocationPermission() {
        if( !hasLocationPermission() ) {
            Log.e("Tuts+", "Does not have location permission granted");
            requestLocationPermission();
            return false;
        }

        return true;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                REQUEST_PERMISSION_RESULT_CODE );
    }


    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_RESULT_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    Log.e("Tuts+", "Location permission denied.");
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if( mItems[position].equalsIgnoreCase( getString(R.string.item_snapshot_headphones ) ) ) {
            detectHeadphones();
        } else if( mItems[position].equalsIgnoreCase( getString(R.string.item_snapshot_location ) ) ) {
            detectLocation();
        } else if( mItems[position].equalsIgnoreCase( getString(R.string.item_snapshot_places ) ) ) {
            detectNearbyPlaces();
        } else if( mItems[position].equalsIgnoreCase( getString(R.string.item_snapshot_weather ) ) ) {
            detectWeather();
        } else if( mItems[position].equalsIgnoreCase( getString(R.string.item_fence ) ) ) {
            createFence();
        } else if( mItems[position].equalsIgnoreCase( getString(R.string.item_snapshot_activity))) {
            detectActivity();
        } else if( mItems[position].equalsIgnoreCase( getString(R.string.item_snapshot_beacons))) {
            detectBeacons();
            //This method will break if you don't have a beacon registered under your google api console
        }
    }

    @Override
    protected void onPause() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(KEY_SITTING_AT_HOME)
                        .build());

        if (mFenceBroadcastReceiver != null) {
            unregisterReceiver(mFenceBroadcastReceiver);
        }

        super.onPause();
    }

    public class FenceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(TextUtils.equals(ACTION_FENCE, intent.getAction())) {
                FenceState fenceState = FenceState.extract(intent);

                if( TextUtils.equals(KEY_SITTING_AT_HOME, fenceState.getFenceKey() ) ) {
                    if( fenceState.getCurrentState() == FenceState.TRUE ) {
                        Log.e("Tuts+", "You've been sitting at home for too long");
                    }
                }
            }
        }
    }
}
