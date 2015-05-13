package com.tutsplus.placesapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;

    private int PLACE_PICKER_REQUEST = 1;
    private AutoCompleteAdapter mAdapter;

    private TextView mTextView;
    private AutoCompleteTextView mPredictTextView;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mTextView = (TextView) findViewById( R.id.textview );

        mPredictTextView = (AutoCompleteTextView) findViewById( R.id.predicttextview );
        mAdapter = new AutoCompleteAdapter( this );
        mPredictTextView.setAdapter( mAdapter );

        mPredictTextView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoCompletePlace place = (AutoCompletePlace) parent.getItemAtPosition( position );
                findPlaceById( place.getId() );
            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder( this )
                .enableAutoManage( this, 0, this )
                .addApi( Places.GEO_DATA_API )
                .addApi( Places.PLACE_DETECTION_API )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mAdapter.setGoogleApiClient( null );
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void findPlaceById( String id ) {
        if( TextUtils.isEmpty( id ) || mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
            return;

       Places.GeoDataApi.getPlaceById( mGoogleApiClient, id ) .setResultCallback( new ResultCallback<PlaceBuffer>() {
           @Override
           public void onResult(PlaceBuffer places) {
               if( places.getStatus().isSuccess() ) {
                   Place place = places.get( 0 );
                   displayPlace( place );
                   mPredictTextView.setText( "" );
                   mAdapter.clear();
               }

               //Release the PlaceBuffer to prevent a memory leak
               places.release();
           }
       } );
    }

    private void guessCurrentPlace() {
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace( mGoogleApiClient, null );
        result.setResultCallback( new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult( PlaceLikelihoodBuffer likelyPlaces ) {

                PlaceLikelihood placeLikelihood = likelyPlaces.get( 0 );
                String content = "";
                if( placeLikelihood != null && placeLikelihood.getPlace() != null && !TextUtils.isEmpty( placeLikelihood.getPlace().getName() ) )
                    content = "Most likely place: " + placeLikelihood.getPlace().getName() + "\n";
                if( placeLikelihood != null )
                    content += "Percent change of being there: " + (int) ( placeLikelihood.getLikelihood() * 100 ) + "%";
                mTextView.setText( content );

                likelyPlaces.release();
            }
        });
    }

    private void displayPlacePicker() {
        if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult( builder.build( getApplicationContext() ), PLACE_PICKER_REQUEST );
        } catch ( GooglePlayServicesRepairableException e ) {
                Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
        } catch ( GooglePlayServicesNotAvailableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
        }
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK ) {
            displayPlace( PlacePicker.getPlace( data, this ) );
        }
    }

    private void displayPlace( Place place ) {
        if( place == null )
            return;

        String content = "";
        if( !TextUtils.isEmpty( place.getName() ) ) {
            content += "Name: " + place.getName() + "\n";
        }
        if( !TextUtils.isEmpty( place.getAddress() ) ) {
            content += "Address: " + place.getAddress() + "\n";
        }
        if( !TextUtils.isEmpty( place.getPhoneNumber() ) ) {
            content += "Phone: " + place.getPhoneNumber();
        }

        mTextView.setText( content );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();

        if( id == R.id.action_place_picker ) {
            displayPlacePicker();
            return true;
        } else if( id == R.id.action_guess_current_place ) {
            guessCurrentPlace();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onConnected( Bundle bundle ) {
        if( mAdapter != null )
            mAdapter.setGoogleApiClient( mGoogleApiClient );
    }

    @Override
    public void onConnectionSuspended( int i ) {

    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult ) {

    }
}
