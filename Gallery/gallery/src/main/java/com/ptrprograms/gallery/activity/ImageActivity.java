package com.ptrprograms.gallery.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.ptrprograms.gallery.R;
import com.ptrprograms.gallery.model.Image;
import com.ptrprograms.gallery.util.ImageStateViewPager;
import android.support.v4.view.ViewPager;
import java.util.List;

public class ImageActivity extends FragmentActivity {

    public static final String EXTRA_IMAGE_LIST = "imageList";
    public static final String EXTRA_CUR_IMAGE = "curImage";

    private int mCurrentImagePosition = 0;
    private ImageStateViewPager mAdapter;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mViewPager = ( ViewPager ) findViewById( R.id.activity_image_view_pager );

        setupImageList();

        if( mAdapter == null || mViewPager == null )
            return;

        mViewPager.setAdapter( mAdapter );
    }

    private void setupImageList() {
        if( getIntent() == null || getIntent().getExtras() == null )
            return;

        List<Image> tmpList = getIntent().getExtras().getParcelableArrayList( EXTRA_IMAGE_LIST );
        mCurrentImagePosition = getIntent().getExtras().getInt( EXTRA_CUR_IMAGE, 0 );
        mAdapter = new ImageStateViewPager( getSupportFragmentManager(), tmpList );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentImagePosition = savedInstanceState.getInt( EXTRA_CUR_IMAGE, 0 );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCurrentImagePosition = mViewPager.getCurrentItem();
        outState.putInt( EXTRA_CUR_IMAGE, mCurrentImagePosition );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if( mViewPager == null )
            return;

        mViewPager.setCurrentItem( mCurrentImagePosition );
    }
}
