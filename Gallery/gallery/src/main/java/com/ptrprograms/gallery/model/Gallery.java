package com.ptrprograms.gallery.model;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class Gallery {

    private String description;
    private List<Image> images = new ArrayList<Image>();

    public Gallery() {

    }

    public String getDescription() {
        return ( description == null ) ? "" : description;
    }

    public void setDescription( String newDescription ) {
        if( TextUtils.isEmpty(newDescription) )
            return;

        description = newDescription;
    }

    public void setImages( List<Image> newImages ) {
        if( newImages == null || newImages.size() == 0 )
            return;

        for( Image image : newImages )
            this.images.add( image );
    }

    public List<Image> getImages() {
        return images;
    }

}
