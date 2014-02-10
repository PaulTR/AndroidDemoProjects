package com.ptrprograms.daydream.views;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ptrprograms.daydream.R;
import com.ptrprograms.daydream.models.Quote;
import com.ptrprograms.daydream.utils.GsonRequest;

/**
 * Created by PaulTR on 1/29/14.
 */
public class QuoteView extends TextView  {

	private Quote mQuote;
	private Handler mHandler;
	private int mNewQuoteDelay = 5000;
	private int mFadeInTime = 1200;
	private int mFadeOutTime = 2000;
	private AlphaAnimation mFadeIn;
	private AlphaAnimation mFadeOut;

	public QuoteView( Context context ) {
		this( context, null );
	}

	public QuoteView( Context context, AttributeSet attrs ) {
		this( context, attrs, 0 );
	}

	public QuoteView( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );

		applyViewStyles();
		initAnimation();
		mHandler = new Handler();
	}

	private void applyViewStyles( ) {
		setTextColor( getResources().getColor( android.R.color.white ) );
		setTextAlignment( TEXT_ALIGNMENT_CENTER );
		setGravity( Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL );
		setTypeface( Typeface.SANS_SERIF );
	}

	private void initAnimation() {
		mFadeIn = new AlphaAnimation( 0.0f, 1.0f );
		mFadeIn.setDuration( mFadeInTime );
		mFadeIn.setFillAfter( true );

		mFadeOut = new AlphaAnimation( 1.0f, 0.0f );
		mFadeOut.setDuration( mFadeOutTime );
		mFadeOut.setFillAfter( true );
		mFadeOut.setAnimationListener( new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				//Do nothing.
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				displayQuote();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				//Do nothing.
			}
		});
	}

	Runnable mQuoteGenerator = new Runnable() {
		@Override
		public void run() {
			generateQuote();
			mHandler.postDelayed( mQuoteGenerator, mNewQuoteDelay );
		}
	};

	private void generateQuote() {
		GsonRequest<Quote> request = new GsonRequest<Quote>(
				Request.Method.GET,
				getResources().getString( R.string.random_quote_url_json ),
				Quote.class,
				onSuccessListener(),
				onErrorListener() );

		Volley.newRequestQueue( getContext() ).add(request);
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
				startAnimation(mFadeOut);
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
				mQuote.setQuote( getResources().getString( R.string.volley_error ) );
				startAnimation(mFadeOut);
			}
		};
	}

	private void displayQuote() {
		if( mQuote == null || TextUtils.isEmpty( mQuote.getQuote() ) )
			return;
		this.setText( mQuote.getQuote() );
		startAnimation( mFadeIn );
	}

}
