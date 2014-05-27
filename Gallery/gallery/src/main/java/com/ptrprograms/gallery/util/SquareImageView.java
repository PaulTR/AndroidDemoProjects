package com.ptrprograms.gallery.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView {

    public SquareImageView( final Context context ) {
        super( context );
    }

    public SquareImageView( final Context context, final AttributeSet attrs ) {
        super( context, attrs );
    }

    public SquareImageView( final Context context, final AttributeSet attrs, final int defStyle ) {
        super( context, attrs, defStyle );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dimension = getDefaultSize( getSuggestedMinimumWidth(), widthMeasureSpec );
        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
    }
}