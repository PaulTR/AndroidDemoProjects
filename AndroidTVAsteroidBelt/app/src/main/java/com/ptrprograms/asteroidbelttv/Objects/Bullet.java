package com.ptrprograms.asteroidbelttv.Objects;

import com.ptrprograms.asteroidbelttv.Utils.Constants;
import com.ptrprograms.asteroidbelttv.Utils.ShapeBuffer;
import com.ptrprograms.asteroidbelttv.Utils.Utils;

/**
 * Created by PaulTR on 7/20/14.
 */
public class Bullet {

    private Utils.Color mColor = new Utils.Color();

    private float mBulletSize = 1.0f;
    private float mVelocityX;
    private float mVelocityY;
    public float mPositionX;
    public float mPositionY;
    public int mLifeTimer = 5 * 60;

    public Bullet( Utils.Color color, float shotDx, float shotDy, float positionX, float positionY ) {
        this.mColor.set( color );

        mVelocityX = shotDx;
        mVelocityY = shotDy;

        mPositionX = positionX;
        mPositionY = positionY;
    }

    public void draw( ShapeBuffer sb ) {
        sb.add2DShape( mPositionX, mPositionY, mColor, getBulletVerticies(), mBulletSize, mBulletSize, 0, 0 );
    }

    public float[] getBulletVerticies() {
        float vertices[] = new float[ 361*3 ];
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = 0;

        for(int i =1; i <361; i++){
            vertices[(i * 3)] = (float) (0.5 * Math.cos((3.14/180) * (float)i ) + vertices[0]);
            vertices[(i * 3)+ 1] = (float) (0.5 * Math.sin((3.14/180) * (float)i ) + vertices[1]);
            vertices[(i * 3)+ 2] = 0;
        }

        return vertices;
    }

    public void update( float delta ) {
        if( mLifeTimer > 0 ) {
            updateBulletPosition(delta);
            mLifeTimer--;
        }
    }

    public void updateBulletPosition( float delta ) {
        setPositionX( mPositionX + ( mVelocityX * delta ) );
        setPositionY( mPositionY + ( mVelocityY * delta ) );
    }

    public void setPositionX( float positionX ) {
        if( Constants.MAP_LEFT_COORDINATE - mBulletSize < positionX && positionX < Constants.MAP_RIGHT_COORDINATE + mBulletSize ) {
            mPositionX = positionX;
        } else if( positionX > Constants.MAP_RIGHT_COORDINATE ) {
            mPositionX = Constants.MAP_LEFT_COORDINATE;
        } else if( positionX < Constants.MAP_LEFT_COORDINATE ) {
            mPositionX = Constants.MAP_RIGHT_COORDINATE;
        }
    }

    public void setPositionY( float positionY ) {
        if( Constants.MAP_BOTTOM_COORDINATE - mBulletSize < positionY && positionY < Constants.MAP_TOP_COORDINATE + mBulletSize ) {
            mPositionY = positionY;
        } else if( positionY > Constants.MAP_TOP_COORDINATE + mBulletSize ) {
            mPositionY = Constants.MAP_BOTTOM_COORDINATE;
        } else if( positionY < Constants.MAP_BOTTOM_COORDINATE - mBulletSize ) {
            mPositionY = Constants.MAP_TOP_COORDINATE;
        }
    }

}
