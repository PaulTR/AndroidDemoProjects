package com.ptrprograms.randomkittymuzei;

/**
 * Created by PaulTR on 2/15/14.
 */
import android.content.Intent;
import android.net.Uri;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MuzeiImageGenerator extends RemoteMuzeiArtSource {

	private static final int ROTATE_TIME_MILLIS = 60 * 60 * 1000; //Every hour a new kitty!
	private static final String ACCEPTED_FILE_EXTENSION = "jpg";
	private static final String BASE_URL = "http://thecatapi.com/api/images/get?format=src&type=jpg&size=full&category=space";
	private static final String NAME = "RandomKittyMuzei";

	public MuzeiImageGenerator() {
		super( NAME );
	}

	@Override
	protected void onTryUpdate( int reason ) throws RetryException {
		String link;
		try {
			do {
				link = getLink();
			} while( !imageHasAcceptableExtension( link ) );
		} catch( Exception e ) {
			throw new RetryException();
		}

		setMuzeiImage( link );
	}

	private boolean imageHasAcceptableExtension( String link ) {
		return link.substring( link.length() - 3 ).equals( ACCEPTED_FILE_EXTENSION );
	}

	private void setMuzeiImage( String link ) {
		publishArtwork(new Artwork.Builder()
				.title(getApplication().getResources().getString(R.string.title))
				.imageUri(Uri.parse(link))
				.viewIntent( new Intent(Intent.ACTION_VIEW, Uri.parse( link ) ) )
				.build() );

		scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
	}

	public String getLink() throws IOException {

		String link = BASE_URL;
		URL url = new URL( link );
		HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
		ucon.setInstanceFollowRedirects( false );
		URL secondURL = new URL( ucon.getHeaderField( "Location" ) );

		return secondURL.toString();
	}
}

