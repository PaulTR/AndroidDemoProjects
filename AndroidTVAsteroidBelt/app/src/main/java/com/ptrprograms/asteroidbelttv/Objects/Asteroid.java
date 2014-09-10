package com.ptrprograms.asteroidbelttv.Objects;

import com.ptrprograms.asteroidbelttv.Utils.Constants;
import com.ptrprograms.asteroidbelttv.Utils.ShapeBuffer;
import com.ptrprograms.asteroidbelttv.Utils.Utils;

/**
 * Created by PaulTR on 7/20/14.
 */
public class Asteroid {

    private float mAsteroidSize;

    private Utils.Color mColor = new Utils.Color();

    private float mVelocityX;
    private float mVelocityY;
    private float mVelocityMultiplier = 0.8f;
    public float mPositionX;
    public float mPositionY;

    public Asteroid( Utils.Color color, float asteroidSize ) {
        this.mColor.set( color );

        mVelocityX = (float) Math.random() * mVelocityMultiplier;
        mVelocityY = (float) Math.random() * mVelocityMultiplier;

        mPositionX = (float) Math.random() * Constants.MAP_RIGHT_COORDINATE;
        mPositionY = (float) Math.random() * Constants.MAP_TOP_COORDINATE;
        mAsteroidSize = asteroidSize;
    }

    public void draw( ShapeBuffer sb ) {
        sb.add2DShape( mPositionX, mPositionY, mColor, getAsteroidVerticies(), mAsteroidSize, mAsteroidSize, 0, 0 );
    }

    private float[] getAsteroidVerticies() {
        float vertices[] = new float[ 361*3 ];
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = 0;

        for(int i =1; i <361; i++){
            vertices[(i * 3)+ 0] = (float) (0.5 * Math.cos((3.14/180) * (float)i ) + vertices[0]);
            vertices[(i * 3)+ 1] = (float) (0.5 * Math.sin((3.14/180) * (float)i ) + vertices[1]);
            vertices[(i * 3)+ 2] = 0;
        }

        return vertices;
    }

    public void update( float delta ) {
        updateAsteroidPosition( delta );
    }

    public void updateAsteroidPosition( float delta ) {
        setPositionX( mPositionX + ( mVelocityX * delta ) );
        setPositionY( mPositionY + ( mVelocityY * delta ) );
    }

    public void setPositionX( float positionX ) {
        if( Constants.MAP_LEFT_COORDINATE - mAsteroidSize < positionX && positionX < Constants.MAP_RIGHT_COORDINATE + mAsteroidSize ) {
            mPositionX = positionX;
        } else if( positionX > Constants.MAP_RIGHT_COORDINATE ) {
            mPositionX = Constants.MAP_LEFT_COORDINATE;
        } else if( positionX < Constants.MAP_LEFT_COORDINATE ) {
            mPositionX = Constants.MAP_RIGHT_COORDINATE;
        }
    }

    public void setPositionY( float positionY ) {
        if( Constants.MAP_BOTTOM_COORDINATE - mAsteroidSize < positionY && positionY < Constants.MAP_TOP_COORDINATE + mAsteroidSize ) {
            mPositionY = positionY;
        } else if( positionY > Constants.MAP_TOP_COORDINATE + mAsteroidSize ) {
            mPositionY = Constants.MAP_BOTTOM_COORDINATE;
        } else if( positionY < Constants.MAP_BOTTOM_COORDINATE - mAsteroidSize ) {
            mPositionY = Constants.MAP_TOP_COORDINATE;
        }
    }
}
