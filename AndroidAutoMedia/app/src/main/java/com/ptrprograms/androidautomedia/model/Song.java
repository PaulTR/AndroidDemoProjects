package com.ptrprograms.androidautomedia.model;

/**
 * Created by paulruiz on 2/13/15.
 */
public class Song {
    private String uId;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String albumUrl;
    private String thumbnailUrl;

    public Song( String uId, String title, String artist, String album, String genre, String albumUrl, String thumbnailUrl ) {
        this.uId = uId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.albumUrl = albumUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public void setAlbumUrl(String albumUrl) {
        this.albumUrl = albumUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
