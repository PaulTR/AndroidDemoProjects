package com.ptrprograms.androidtvmediaplayer.Presenter;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ptrprograms.androidtvmediaplayer.Model.Movie;
import com.ptrprograms.androidtvmediaplayer.R;
import com.ptrprograms.androidtvmediaplayer.Util.PicassoImageCardViewTarget;
import com.squareup.picasso.Picasso;


public class CardPresenter extends Presenter {

    static class ViewHolder extends Presenter.ViewHolder {
        private ImageCardView mCardView;
        private PicassoImageCardViewTarget mImageCardViewTarget;

        public ViewHolder( View view ) {
            super( view );
            mCardView = (ImageCardView) view;
            mImageCardViewTarget = new PicassoImageCardViewTarget( mCardView );
        }

        public ImageCardView getCardView() {
            return mCardView;
        }

        protected void updateCardViewImage( Context context, String link ) {
            Picasso.with( context )
                    .load(link)
                    .resize( mCardView.getResources().getInteger( R.integer.card_presenter_width ), mCardView.getResources().getInteger( R.integer.card_presenter_height ) )
                    .centerCrop()
                    .error( R.drawable.default_background )
                    .into( mImageCardViewTarget );
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        ImageCardView cardView = new ImageCardView( parent.getContext() );
        cardView.setFocusable( true );
        cardView.setFocusableInTouchMode( true );
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;

        if ( !TextUtils.isEmpty( movie.getCardImageUrl() ) ) {
            ((ViewHolder) viewHolder).mCardView.setTitleText( movie.getTitle() );
            ((ViewHolder) viewHolder).mCardView.setContentText( movie.getStudio() );
            ((ViewHolder) viewHolder).mCardView.setMainImageDimensions(
                    ( (ViewHolder) viewHolder ).mCardView.getContext().getResources().getInteger( R.integer.card_presenter_width ),
                    ( (ViewHolder) viewHolder ).mCardView.getContext().getResources().getInteger( R.integer.card_presenter_height ) );
            ( (ViewHolder) viewHolder ).updateCardViewImage( ( (ViewHolder) viewHolder ).getCardView().getContext(), movie.getCardImageUrl() );
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
    }

}
