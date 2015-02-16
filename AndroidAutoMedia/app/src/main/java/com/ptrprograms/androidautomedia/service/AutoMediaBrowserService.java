package com.ptrprograms.androidautomedia.service;

import android.graphics.BitmapFactory;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.media.MediaBrowserService;
import android.text.TextUtils;

import com.ptrprograms.androidautomedia.R;
import com.ptrprograms.androidautomedia.model.Song;
import com.ptrprograms.androidautomedia.util.SongGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulruiz on 2/13/15.
 */
public class AutoMediaBrowserService extends MediaBrowserService {

    private static final String BROWSEABLE_ROOT = "root";
    private static final String BROWSEABLE_ROCK = "Rock";
    private static final String BROWSEABLE_JAZZ = "Jazz";
    private static final String BROWSEABLE_CAJUN = "Cajun";
    private static final String CURRENT_MEDIA_POSITION = "current_media_position";

    private MediaSession mMediaSession;
    private MediaSession.Token mMediaSessionToken;

    private List<Song> mSongs;

    private MediaPlayer mMediaPlayer;

    private MediaSession.Callback mMediaSessionCallback = new MediaSession.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();

            toggleMediaPlaybackState( true );
            playMedia( PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ).getInt( CURRENT_MEDIA_POSITION, 0 ), null );
        }

        //This is called when the pause button is pressed, or when onPlayFromMediaId is called in
        //order to pause any currently playing media
        @Override
        public void onPause() {
            super.onPause();

            toggleMediaPlaybackState( false );
            pauseMedia();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);

            initMediaMetaData( mediaId );
            toggleMediaPlaybackState( true );
            playMedia( 0, mediaId );
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mSongs = SongGenerator.generateSongs();

        initMediaSession();
    }

    private void initMediaSession() {
        mMediaSession = new MediaSession( this, "Android Auto Audio Demo" );
        mMediaSession.setActive( true );
        mMediaSession.setCallback( mMediaSessionCallback );

        mMediaSessionToken = mMediaSession.getSessionToken();
        setSessionToken( mMediaSessionToken );
    }

    private void initMediaMetaData( String id ) {

        for( Song song : mSongs ) {
            if( !TextUtils.isEmpty( song.getuId() ) && song.getuId().equalsIgnoreCase( id ) ) {
                MediaMetadata.Builder builder = new MediaMetadata.Builder();

                if( !TextUtils.isEmpty( song.getTitle() ) )
                    builder.putText( MediaMetadata.METADATA_KEY_TITLE, song.getTitle() );

                if( !TextUtils.isEmpty( song.getArtist() ) )
                    builder.putText( MediaMetadata.METADATA_KEY_ARTIST, song.getArtist() );

                if( !TextUtils.isEmpty( song.getGenre() ) )
                    builder.putText( MediaMetadata.METADATA_KEY_GENRE, song.getGenre() );

                if( !TextUtils.isEmpty( song.getAlbum() ) )
                    builder.putText( MediaMetadata.METADATA_KEY_ALBUM, song.getAlbum() );

                if( !TextUtils.isEmpty( song.getAlbumUrl() ) )
                    builder.putText( MediaMetadata.METADATA_KEY_ALBUM_ART_URI, song.getAlbumUrl() );

                mMediaSession.setMetadata( builder.build() );
            }
        }
    }

    private void toggleMediaPlaybackState( boolean playing ) {
        PlaybackState playbackState;
        if( playing ) {
            playbackState = new PlaybackState.Builder()
                    .setActions( PlaybackState.ACTION_PLAY_PAUSE | PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS )
                    .setState( PlaybackState.STATE_PLAYING, 0, 1 )
                    .build();
        } else {
            playbackState = new PlaybackState.Builder()
                    .setActions( PlaybackState.ACTION_PLAY_PAUSE )
                    .setState(PlaybackState.STATE_PAUSED, 0, 1)
                    .build();
        }

        mMediaSession.setPlaybackState( playbackState );
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        //Can do package and other validation to determine if calling app has access to media items
        //in this service. If not, return null.
        return new BrowserRoot(BROWSEABLE_ROOT, null);
    }

    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowser.MediaItem>> result) {

        List<MediaBrowser.MediaItem> items = getMediaItemsById( parentId );
        if( items != null ) {
            result.sendResult( items );
        }

    }

    private List<MediaBrowser.MediaItem> getMediaItemsById( String id ) {
        List<MediaBrowser.MediaItem> mediaItems = new ArrayList<MediaBrowser.MediaItem>();
        if( BROWSEABLE_ROOT.equalsIgnoreCase( id ) ) {
            mediaItems.add( generateBrowseableMediaItemByGenre(BROWSEABLE_CAJUN) );
            mediaItems.add( generateBrowseableMediaItemByGenre(BROWSEABLE_JAZZ) );
            mediaItems.add( generateBrowseableMediaItemByGenre(BROWSEABLE_ROCK) );
        } else if( !TextUtils.isEmpty( id ) ) {
            return getPlayableMediaItemsByGenre( id );
        }

        return mediaItems;
    }

    private List<MediaBrowser.MediaItem> getPlayableMediaItemsByGenre( String genre ) {
        if( TextUtils.isEmpty( genre ) )
            return null;

        List<MediaBrowser.MediaItem> mediaItems = new ArrayList();

        for( Song song : mSongs ) {
            if( !TextUtils.isEmpty( song.getGenre() ) && genre.equalsIgnoreCase( song.getGenre() ) ) {
                mediaItems.add( generatePlayableMediaItem( song ) );
            }
        }
        return mediaItems;
    }


    private MediaBrowser.MediaItem generateBrowseableMediaItemByGenre( String genre ) {
        MediaDescription.Builder mediaDescriptionBuilder = new MediaDescription.Builder();
        mediaDescriptionBuilder.setMediaId( genre );
        mediaDescriptionBuilder.setTitle( genre );
        mediaDescriptionBuilder.setIconBitmap( BitmapFactory.decodeResource( getResources(), R.drawable.folder ) );

        return new MediaBrowser.MediaItem( mediaDescriptionBuilder.build(), MediaBrowser.MediaItem.FLAG_BROWSABLE );
    }

    private MediaBrowser.MediaItem generatePlayableMediaItem( Song song ) {
        if( song == null )
            return null;

        MediaDescription.Builder mediaDescriptionBuilder = new MediaDescription.Builder();
        mediaDescriptionBuilder.setMediaId( song.getuId() );

        if( !TextUtils.isEmpty( song.getTitle() ) )
            mediaDescriptionBuilder.setTitle( song.getTitle() );

        if( !TextUtils.isEmpty( song.getArtist() ) )
            mediaDescriptionBuilder.setSubtitle( song.getArtist() );

        if( !TextUtils.isEmpty( song.getThumbnailUrl() ) )
            mediaDescriptionBuilder.setIconUri( Uri.parse( song.getThumbnailUrl() ) );

        return new MediaBrowser.MediaItem( mediaDescriptionBuilder.build(), MediaBrowser.MediaItem.FLAG_PLAYABLE );
    }

    private void playMedia( int position, String id ) {
        if( mMediaPlayer != null )
            mMediaPlayer.reset();

        //Should check id to determine what to play in a real app
        int songId = getApplicationContext().getResources().getIdentifier("geoff_ledak_dust_array_preview", "raw", getApplicationContext().getPackageName());
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), songId);

        if( position > 0 )
            mMediaPlayer.seekTo( position );
        mMediaPlayer.start();

    }

    private void pauseMedia() {
        if( mMediaPlayer != null ) {
            mMediaPlayer.pause();
            PreferenceManager.getDefaultSharedPreferences( this ).edit().putInt( CURRENT_MEDIA_POSITION,
                    mMediaPlayer.getCurrentPosition() ).commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if( mMediaPlayer != null ) {
            pauseMedia();
            mMediaPlayer.release();
            PreferenceManager.getDefaultSharedPreferences( this ).edit().putInt( CURRENT_MEDIA_POSITION,
                    0 ).commit();
        }

    }
}
