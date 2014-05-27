package com.ptrprograms.gallery.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

public class Image implements Parcelable {
    private String largeImage;
    private String thumbNailImage;
    private String caption;

    public Image() {

    }

    public Image( Parcel source ) {
        largeImage = source.readString();
        thumbNailImage = source.readString();
        caption = source.readString();
    }

    public String getLargeImage() {
        return ( largeImage == null ) ? "" : largeImage;
    }

    public void setLargeImage( String largeImage ) {
        if( TextUtils.isEmpty( largeImage ) )
            return;

        this.largeImage = largeImage;
    }

    public String getThumbNailImage() {
        return ( thumbNailImage == null ) ? "" : thumbNailImage;
    }

    public void setThumbNailImage( String thumbNailImage ) {
        if( TextUtils.isEmpty( thumbNailImage ) )
            return;

        this.thumbNailImage = thumbNailImage;
    }

    public String getCaption() {
        return ( caption == null ) ? "" : caption;
    }

    public void setCaption( String caption ) {
        if( TextUtils.isEmpty( caption ) )
            return;

        this.caption = caption;
    }

    public static Creator<Image> CREATOR = new Creator<Image>()
    {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image( source );
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString( largeImage );
        parcel.writeString( thumbNailImage );
        parcel.writeString( caption );
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
