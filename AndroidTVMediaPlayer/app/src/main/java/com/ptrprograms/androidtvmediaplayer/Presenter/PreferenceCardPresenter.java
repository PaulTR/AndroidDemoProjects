package com.ptrprograms.androidtvmediaplayer.Presenter;

import android.graphics.Color;
import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ptrprograms.androidtvmediaplayer.R;

/**
 * Created by PaulTR on 7/14/14.
 */
public class PreferenceCardPresenter extends Presenter {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView view = new TextView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(
                parent.getResources().getInteger( R.integer.preference_square_size ),
                parent.getResources().getInteger( R.integer.preference_square_size ) ) );
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setBackgroundColor( parent.getContext().getResources().getColor(R.color.default_background) );
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((TextView) viewHolder.view).setText((String) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }
}
