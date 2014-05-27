package com.ptrprograms.gallery.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.ptrprograms.gallery.fragment.ImageFragment;
import com.ptrprograms.gallery.model.Image;

import java.util.List;

public class ImageStateViewPager extends FragmentStatePagerAdapter {

    private List<Image> mImageList;

    public ImageStateViewPager( FragmentManager fm ) {
        super( fm );
    }

    public ImageStateViewPager( FragmentManager fm, List<Image> imageList ) {
        super( fm );

        setImageList( imageList );
    }

    public void setImageList( List<Image> imageList ) {
        if( imageList == null )
            return;

        mImageList = imageList;
    }

    @Override
    public Fragment getItem( int position ) {
        return( position < 0 || position > ( mImageList.size() - 1 ) ) ? null :
                ImageFragment.newInstance(mImageList.get(position));
    }

    @Override
    public int getCount() {
        return ( mImageList == null ) ? 0 : mImageList.size();
    }
}
