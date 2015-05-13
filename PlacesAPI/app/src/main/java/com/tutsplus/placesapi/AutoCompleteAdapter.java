package com.tutsplus.placesapi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by paulruiz on 4/12/15.
 */
public class AutoCompleteAdapter extends ArrayAdapter<AutoCompletePlace> {

    private GoogleApiClient mGoogleApiClient;

    public AutoCompleteAdapter( Context context ) {
        super(context, 0);
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        ViewHolder holder;

        if( convertView == null ) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from( getContext() ).inflate( android.R.layout.simple_list_item_1, parent, false  );
            holder.text = (TextView) convertView.findViewById( android.R.id.text1 );
            convertView.setTag( holder );
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText( getItem( position ).getDescription() );

        return convertView;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.mGoogleApiClient = googleApiClient;
    }

    private class ViewHolder {
        TextView text;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() ) {
                    Toast.makeText( getContext(), "Not connected", Toast.LENGTH_SHORT ).show();
                    return null;
                }

                clear();

                displayPredictiveResults( constraint.toString() );

                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }

    private void displayPredictiveResults( String query )
    {
        //Southwest corner to Northeast corner.
        LatLngBounds bounds = new LatLngBounds( new LatLng( 39.906374, -105.122337 ), new LatLng( 39.949552, -105.068779 ) );

        //Filter: https://developers.google.com/places/supported_types#table3
        List<Integer> filterTypes = new ArrayList<Integer>();
        filterTypes.add( Place.TYPE_ESTABLISHMENT );

        Places.GeoDataApi.getAutocompletePredictions( mGoogleApiClient, query, bounds, AutocompleteFilter.create( filterTypes ) )
            .setResultCallback (
                new ResultCallback<AutocompletePredictionBuffer>() {
                    @Override
                    public void onResult( AutocompletePredictionBuffer buffer ) {

                        if( buffer == null )
                            return;

                        if( buffer.getStatus().isSuccess() ) {
                            for( AutocompletePrediction prediction : buffer ) {
                                //Add as a new item to avoid IllegalArgumentsException when buffer is released
                                add( new AutoCompletePlace( prediction.getPlaceId(), prediction.getDescription() ) );
                            }
                        }

                        //Prevent memory leak by releasing buffer
                        buffer.release();
                    }
                }, 60, TimeUnit.SECONDS );
    }
}
