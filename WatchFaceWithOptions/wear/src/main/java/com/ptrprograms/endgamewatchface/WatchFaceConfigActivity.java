package com.ptrprograms.endgamewatchface;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

/**
 * Created by paulruiz on 3/31/15.
 */
public class WatchFaceConfigActivity extends Activity implements
        WearableListView.ClickListener, WearableListView.OnScrollListener {

    private GoogleApiClient mGoogleApiClient;
    private TextView mHeader;
    private TypedArray mImageResourceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_background_selector);

        mImageResourceList = getResources().obtainTypedArray( R.array.background_resource_ids );
        mHeader = (TextView) findViewById( R.id.header );

        WearableListView listView = (WearableListView) findViewById( R.id.background_picker );
        BoxInsetLayout content = (BoxInsetLayout) findViewById( R.id.content );

        content.setOnApplyWindowInsetsListener( new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                if( !insets.isRound() ) {
                    v.setPaddingRelative( (getResources().getDimensionPixelSize( R.dimen.content_padding_start ) ),
                            v.getPaddingTop(),
                            v.getPaddingEnd(),
                            v.getPaddingBottom() );
                }

                return v.onApplyWindowInsets( insets );
            }
        });

        listView.setHasFixedSize( true );
        listView.setClickListener( this );
        listView.addOnScrollListener( this );

        String[] backgrounds = getResources().getStringArray( R.array.background_array );
        listView.setAdapter( new BackgroundListAdapter( backgrounds ) );


    }

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {

                    }

                    @Override
                    public void onConnectionSuspended(int cause) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.e( "Wearable", "On connection failed: " + result.toString() );
                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient == null )
            initGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() )
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

        BackgroundItemViewHolder holder = (BackgroundItemViewHolder) viewHolder;

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WatchFace.DATA_LAYER_PATH);
        putDataMapRequest.getDataMap().putLong( WatchFace.KEY_TIME, new Date().getTime() );
        putDataMapRequest.getDataMap().putInt(WatchFace.KEY_BACKGROUND_POSITION, holder.getPosition());
        PutDataRequest putDataReq = putDataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> result =    Wearable.DataApi.putDataItem( mGoogleApiClient, putDataReq );

        finish();
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    @Override
    public void onScroll(int i) {

    }

    @Override
    public void onAbsoluteScrollChange(int scroll) {
        float newTranslation = Math.min(-scroll, 0);
        mHeader.setTranslationY(newTranslation);
    }

    @Override
    public void onScrollStateChanged(int i) {

    }

    @Override
    public void onCentralPositionChanged(int i) {

    }

    private class BackgroundListAdapter extends WearableListView.Adapter {

        private final String[] mBackgrounds;

        public BackgroundListAdapter( String[] backgrounds ) {
            mBackgrounds = backgrounds;
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position ) {
            return new BackgroundItemViewHolder( new BackgroundItem( viewGroup.getContext() ) );
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position ) {
            BackgroundItemViewHolder holder = (BackgroundItemViewHolder) viewHolder;
            String backgroundName = mBackgrounds[ position ];
            holder.mBackgroundItem.setBackground( backgroundName, position );

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );

            int backgroundPickerItemMargin = (int) getResources().getDimension( R.dimen.background_picker_item_margin );

            if( position == 0 ) {
                layoutParams.setMargins( 0, backgroundPickerItemMargin, 0, 0 );
            } else if( position == mBackgrounds.length - 1 ) {
                layoutParams.setMargins( 0, 0, 0, backgroundPickerItemMargin );
            } else {
                layoutParams.setMargins( 0, 0, 0, 0 );
            }

            holder.itemView.setLayoutParams( layoutParams );
        }

        @Override
        public int getItemCount() {
            return mBackgrounds.length;
        }
    }

    private static class BackgroundItemViewHolder extends WearableListView.ViewHolder {
        private final BackgroundItem mBackgroundItem;

        public BackgroundItemViewHolder( BackgroundItem item ) {
            super( item );
            mBackgroundItem = item;
        }
    }

    private static class BackgroundItem extends LinearLayout implements WearableListView.OnCenterProximityListener {

        public final TextView mLabel;
        private final ImageView mBackground;
        private TypedArray mImageResourceList;

        public BackgroundItem( Context context ) {
            super( context );
            View.inflate( context, R.layout.background_picker_item, this );

            mLabel = (TextView) findViewById( R.id.label );
            mBackground = (ImageView) findViewById( R.id.background );
            mImageResourceList = getResources().obtainTypedArray( R.array.background_icons_resource_ids );

        }

        private void setBackground( String backgroundName, int position ) {
            mLabel.setText( backgroundName );

            mImageResourceList.getResourceId(position, -1);

            mBackground.setImageDrawable( getResources().getDrawable( mImageResourceList.getResourceId( position, -1 ) ) );
        }

        @Override
        public void onCenterPosition(boolean animate) {

        }

        @Override
        public void onNonCenterPosition(boolean animate) {

        }
    }
}