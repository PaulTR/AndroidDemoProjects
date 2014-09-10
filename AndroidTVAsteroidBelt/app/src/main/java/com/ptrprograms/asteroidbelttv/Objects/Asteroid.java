package com.ptrprograms.asteroidbelttv.Objects;

import com.ptrprograms.asteroidbelttv.Utils.Constants;
import com.ptrprograms.asteroidbelttv.Utils.ShapeBuffer;
import com.ptrprograms.asteroidbelttv.Utils.Utils;

/**
 * Created by PaulTR on 7/20/14.
 */
public class Asteroid {

    public float mAsteroidSize;

    private Utils.Color mColor = new Utils.Color();

    private float mVelocityX;
    private float mVelocityY;
    private float mVelocityMultiplier = 0.8f;
    public float mPositionX;
    public float mPositionY;

    public Asteroid( Utils.Color color, float asteroidSize ) {
        mColor.set( color );

        setRandomStartingPosition();
        setRandomVelocities();

        mAsteroidSize = asteroidSize;
    }

    public Asteroid( Utils.Color color, float asteroidSize, float positionX, float positionY ) {
        mPositionX = positionX;
        mPositionY = positionY;

        setRandomVelocities();

        mAsteroidSize = asteroidSize;

        mColor.set( color );
    }

    public void setRandomVelocities() {
        setVelocityX( (float) Math.random() );
        setVelocityY( (float) Math.random() );
    }

    public void setRandomStartingPosition() {
        mPositionX = (float) Math.random() * Constants.MAP_RIGHT_COORDINATE;
        if( Math.random() < 0.5 )
            mPositionX = -mPositionX;
        mPositionY = (float) Math.random() * Constants.MAP_TOP_COORDINATE;
        if( Math.random() < 0.5 )
            mPositionY = -mPositionY;
    }

    public void setVelocityX( float vel ) {
        mVelocityX = vel * mVelocityMultiplier;
    }

    public void setVelocityY( float vel ) {
        mVelocityY = vel * mVelocityMultiplier;
    }

    public void draw( ShapeBuffer sb ) {
        sb.add2DShape( mPositionX, mPositionY, mColor, getAsteroidVerticies(), mAsteroidSize, mAsteroidSize, 0, 0 );
    }

    private float[] getAsteroidVerticies() {
        float vertices[] = new float[ 361*3 ];
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = 0;

        for(int i = 1; i < 361; i++){
            vertices[(i * 3)] = (float) ( Math.cos((3.14/180) * (float)i ) + vertices[0]);
            vertices[(i * 3)+ 1] = (float) ( Math.sin((3.14/180) * (float)i ) + vertices[1]);
            vertices[(i * 3)+ 2] = 0;
        }

        return vertices;
    }

    public void update( float delta ) {
        setPositionX( mPositionX + ( mVelocityX * delta ) );
        setPositionY( mPositionY + ( mVelocityY * delta ) );
    }

    public void setPositionX( float position ) {
        if( Utils.isInXPlane( position, mAsteroidSize ) ) {
            mPositionX = position;
        } else if( Utils.isOffScreenToRight( position, mAsteroidSize ) ) {
            mPositionX = Constants.MAP_LEFT_COORDINATE;
        } else if( Utils.isOffScreenToLeft( position, mAsteroidSize ) ) {
            mPositionX = Constants.MAP_RIGHT_COORDINATE;
        }
    }

    public void setPositionY( float position ) {
        if( Utils.isInYPlane( position, mAsteroidSize ) ) {
            mPositionY = position;
        } else if( Utils.isOffScreenAboveTop( position, mAsteroidSize ) ) {
            mPositionY = Constants.MAP_BOTTOM_COORDINATE;
        } else if( Utils.isOffScreenBelowBottom( position, mAsteroidSize ) ) {
            mPositionY = Constants.MAP_TOP_COORDINATE;
        }
    }
}
