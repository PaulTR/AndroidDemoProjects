package com.ptrprograms.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.ptrprograms.gallery.R;
import com.squareup.picasso.Picasso;

public class GalleryGridAdapter extends ArrayAdapter<String> {

    private int mLayout;

    public GalleryGridAdapter( Context context, int layoutId ) {
        super( context, layoutId );
        mLayout = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageHolder mImageHolder = null;

        if( convertView == null ) {
            mImageHolder = new ImageHolder();
            LayoutInflater inflater = LayoutInflater.from( getContext() );
            convertView = inflater.inflate( mLayout, null );
            mImageHolder.mGridImage = (ImageView) convertView.findViewById( R.id.grid_view_item );

            if( mImageHolder.mGridImage == null )
                return null;

            convertView.setTag( mImageHolder );
        } else
            mImageHolder = ( ImageHolder ) convertView.getTag();

        if( mImageHolder == null || mImageHolder.mGridImage == null )
            return null;

        mImageHolder.mGridImage.setVisibility( View.GONE );
        setupGridImage( mImageHolder, position );

        return convertView;
    }

    private void setupGridImage( ImageHolder holder, int position ) {

        String imageUrl = getItem( position );

        holder.mGridImage.setImageDrawable( null );
        holder.mGridImage.setVisibility( View.VISIBLE );
        Picasso.with(getContext()).load( imageUrl ).into( holder.mGridImage );
    }

    private class ImageHolder {
        ImageView mGridImage;
    }
}
