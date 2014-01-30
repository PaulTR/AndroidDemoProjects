package com.ptrprograms.daydream.views;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ptrprograms.daydream.BuildConfig;
import com.ptrprograms.daydream.R;
import com.ptrprograms.daydream.models.Quote;
import com.ptrprograms.daydream.services.DaydreamService;
import com.ptrprograms.daydream.utils.GsonRequest;

/**
 * Created by PaulTR on 1/29/14.
 */
public class QuoteView extends TextView  {

	private Quote mQuote;
	private String TAG = DaydreamService.class.getSimpleName();
	private Handler mHandler;

	public QuoteView( Context context ) {
		this( context, null );
	}

	public QuoteView( Context context, AttributeSet attrs ) {
		this( context, attrs, 0 );
	}

	public QuoteView( Context context, AttributeSet attrs, int flags ) {
		super( context, attrs, flags );
		this.setTextColor( getResources().getColor( android.R.color.white ) );

		mHandler = new Handler();
	}

	Runnable mQuoteGenerator = new Runnable() {
		@Override
		public void run() {
			generateQuote();
			mHandler.postDelayed( mQuoteGenerator, 5000 );
		}
	};

	private void generateQuote() {
		GsonRequest<Quote> request = new GsonRequest<Quote>(
				Request.Method.GET,
				getResources().getString( R.string.random_quote_url_json ),
				Quote.class,
				onSuccessListener(),
				onErrorListener() );

		Volley.newRequestQueue( getContext() ).add( request );
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mQuoteGenerator.run();
	}

	@Override
	protected void onDetachedFromWindow() {
		mHandler.removeCallbacks( mQuoteGenerator );
		super.onDetachedFromWindow();
	}

	public Response.Listener onSuccessListener() {
		return new Response.Listener<Quote>() {
			@Override
			public void onResponse(Quote quote) {
				if( quote == null )
					return;

				if( mQuote == null )
					mQuote = new Quote();

				mQuote.setJson_class( quote.getJson_class() );
				mQuote.setLink( quote.getLink() );
				mQuote.setSource( quote.getSource() );
				mQuote.setQuote( quote.getQuote() );
				displayQuote();
			}
		};
	}

	protected Response.ErrorListener onErrorListener()
	{
		return new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse( VolleyError volleyError )
			{
				if( BuildConfig.DEBUG )
					Log.e(TAG, volleyError.toString());
			}
		};
	}

	private void displayQuote() {
		Log.e( TAG, mQuote.getQuote() );
		if( mQuote == null || TextUtils.isEmpty( mQuote.getQuote() ) )
			return;

		this.setText( mQuote.getQuote() );
	}

}
