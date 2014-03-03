package com.ptrprograms.gallery.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ptrprograms.gallery.R;
import com.ptrprograms.gallery.model.Image;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageFragment extends Fragment {

    public final static String EXTRA_IMAGE = "image";
    private final static String CAPTION_STATE = "caption_state";

	private ProgressBar mProgress;
    private ImageView mImageView;
    private TextView mCaption;
    private Image mImage;

    public static ImageFragment newInstance( Image image ) {
        ImageFragment f = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable( EXTRA_IMAGE, image );
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_image_detail, container, false );
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if( mCaption == null )
            return;
        outState.putInt( CAPTION_STATE, mCaption.getVisibility() );
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if( mCaption == null || savedInstanceState == null )
            return;
        mCaption.setVisibility( savedInstanceState.getInt( CAPTION_STATE, View.VISIBLE ));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
		mProgress = (ProgressBar) view.findViewById( R.id.progress );
		mImageView = ( ImageView ) view.findViewById( R.id.fragment_image_detail_image );
		mImageView.setVisibility( View.GONE );
		mCaption = ( TextView ) view.findViewById( R.id.fragment_image_detail_caption );
		mCaption.setVisibility( View.GONE );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImage = getArguments().getParcelable( EXTRA_IMAGE );
        if( mImage == null )
            return;

        loadImage( mImage.getLargeImage() );
        loadCaption( mImage.getCaption() );
    }

    private void loadImage( String url ) {
        if( TextUtils.isEmpty( url ) || mImageView == null )
            return;

        Picasso.with( getActivity() ).load( url ).into( mImageView, new Callback() {
			@Override
			public void onSuccess() {
				mImageView.setVisibility( View.VISIBLE );
				mProgress.setVisibility( View.GONE );
				mImageView.setOnClickListener( new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if( mCaption.getVisibility() == View.GONE && !TextUtils.isEmpty( mCaption.getText() ) )
							mCaption.setVisibility( View.VISIBLE );
						else
							mCaption.setVisibility( View.GONE );
					}
				});
			}

			@Override
			public void onError() {

			}
		});
    }

    private void loadCaption( String caption ) {
        if( TextUtils.isEmpty( caption ) || mCaption == null )
            return;

        mCaption.setText( caption );
        mCaption.setVisibility( View.VISIBLE );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Picasso.with( getActivity() ).cancelRequest( mImageView );
    }
}
