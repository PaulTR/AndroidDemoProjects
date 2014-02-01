package com.ptrprograms.daydream.services;

import android.service.dreams.DreamService;
import android.view.ViewGroup;

import com.ptrprograms.daydream.views.QuoteView;

/**
 * Created by PaulTR on 1/29/14.
 */
public class DaydreamService extends DreamService {

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		final QuoteView view = new QuoteView( this );
		view.setLayoutParams( new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ) );

		setContentView( view );
	}
}