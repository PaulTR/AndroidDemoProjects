package com.ptrprograms.asteroidbelttv.Objects;

import com.ptrprograms.asteroidbelttv.GameView;
import com.ptrprograms.asteroidbelttv.Utils.Constants;
import com.ptrprograms.asteroidbelttv.Utils.GamepadController;
import com.ptrprograms.asteroidbelttv.Utils.ShapeBuffer;
import com.ptrprograms.asteroidbelttv.Utils.Utils;

/**
 * Created by PaulTR on 7/20/14.
 */
public class Ship {

    private final GamepadController mController = new GamepadController();

    private static final float SHIP_SIZE = 5.0f;
    private static final float DRAG = 0.05f;
    private static final float MINIMUM_VELOCITY = 0.05f;
    private static final int FIRE_REFRESH_TIMER = 10;//frames

    private float mHeadingX;
    private float mHeadingY;
    private float mVelocityX;
    private float mVelocityY;
    private float mVelocityMultiplier = 1.5f;
    private float mPositionX;
    private float mPositionY;
    private float mRespawnTimer = 3.0f;

    private int mFireTimer = 0;

    private float mAimX, mAimY;


    private Utils.Color mColor = Utils.Color.WHITE;

    public Ship() {
        reset();
    }

    public void reset() {
        setVelocity(0.0f, 0.0f);
        setPosition( 0.0f, 0.0f );
        setHeadingX( 0.0f );
        setHeadingY( 1.0f );
    }

    public void draw( ShapeBuffer sb) {
        if (!isSpawned()) {
            return;
        }

        sb.add2DShape( mPositionX, mPositionY, mColor, getShipVerticies(), SHIP_SIZE, SHIP_SIZE,
                mHeadingX, mHeadingY);

    }

    private float[] getShipVerticies() {
        float verticies[] = {
                -1.0f, 0.5f,
                -1.0f, -0.5f,
                1.0f, 0.0f
        };

        return verticies;
    }

    public void setVelocity( float velX, float velY ) {
        setVelocityX( velX );
        setVelocityY( velY );
    }

    public void setVelocityX( float vel ) {
        mVelocityX = vel * mVelocityMultiplier;
    }

    public void setVelocityY( float vel ) {
        mVelocityY = vel * mVelocityMultiplier;
    }

    public void setPosition( float posX, float posY ) {
        setPositionX( posX );
        setPositionY( posY );
    }

    public void setPositionX( float position ) {
        if( Utils.isInXPlane( position, SHIP_SIZE ) ) {
            mPositionX = position;
        } else if( Utils.isOffScreenToRight(position, SHIP_SIZE) ) {
            mPositionX = Constants.MAP_LEFT_COORDINATE;
        } else if( Utils.isOffScreenToLeft( position, SHIP_SIZE ) ) {
            mPositionX = Constants.MAP_RIGHT_COORDINATE;
        }
    }

    public void setPositionY( float position ) {
        if( Utils.isInYPlane( position, SHIP_SIZE ) ) {
            mPositionY = position;
        } else if( Utils.isOffScreenAboveTop( position, SHIP_SIZE ) ) {
            mPositionY = Constants.MAP_BOTTOM_COORDINATE;
        } else if( Utils.isOffScreenBelowBottom( position, SHIP_SIZE ) ) {
            mPositionY = Constants.MAP_TOP_COORDINATE;
        }
    }

    public void update(float delta ) {
        if ( !updateStatus( delta ) ) {
            return;
        }

        updateShipPosition( delta );
        handleKeyInput( delta );
    }

    private boolean updateStatus( float delta ) {
        updateSpawningStatus( delta );
        return isSpawned();
    }

    public boolean isSpawned() {
        return ( mRespawnTimer <= 0.0f );
    }

    private void updateSpawningStatus( float delta ) {
        if (mRespawnTimer > 0.0f) {
            mRespawnTimer -= delta;
            if (mRespawnTimer <= 0.0f) {
                // Time to respawn.
                mRespawnTimer = 0.0f;

                // Spawn in middle
                setPositionX( ( Constants.MAP_LEFT_COORDINATE + Constants.MAP_RIGHT_COORDINATE ) / 2 );
                setPositionY( ( Constants.MAP_TOP_COORDINATE + Constants.MAP_BOTTOM_COORDINATE ) / 2 );
            }
        }
    }

    //Determines velocity by getting direction and magnitude from the joystick controller
    private void updateShipPosition(float frameDelta) {
        float newHeadingX = mController.getJoystickPosition(GamepadController.JOYSTICK_1,
                GamepadController.AXIS_X);
        float newHeadingY = mController.getJoystickPosition(GamepadController.JOYSTICK_1,
                GamepadController.AXIS_Y);

        float magnitude = Utils.vector2DLength(newHeadingX, newHeadingY);

        //Joystick being used at least a little bit
        if (magnitude > GamepadController.JOYSTICK_MOVEMENT_THRESHOLD) {
            //Get the heading divided by how much the joystick is being used
            mHeadingX = newHeadingX / magnitude;
            mHeadingY = -newHeadingY / magnitude;

            setVelocity( newHeadingX, -newHeadingY );

            if (magnitude > 1.0f) {
                //Sets a cap velocity
                mVelocityX /= magnitude;
                mVelocityY /= magnitude;
            }
        }

        setPosition( mPositionX + mVelocityX * frameDelta, mPositionY + mVelocityY * frameDelta );

        // Use drag so that the ship will coast to a stop after the movement controller
        // is released.
        mVelocityX *= 1.0f - frameDelta * DRAG;
        mVelocityY *= 1.0f - frameDelta * DRAG;
        if (Utils.vector2DLength(mVelocityX, mVelocityY) < MINIMUM_VELOCITY) {
            setVelocity( 0.0f, 0.0f );
        }
    }

    private void handleKeyInput( float delta ) {
        if( mFireTimer > 0 ) {
            mFireTimer--;
            return;
        }
        if ( mController.isButtonDown( GamepadController.BUTTON_X ) && mFireTimer == 0 ) {
            mFireTimer = FIRE_REFRESH_TIMER;
            calculateAimDirection();
            fireGun();
        }
    }

    private void fireGun() {
        //Bullets angle is based on angle of the ship + speed of the ship in a given direction
        GameView.getInstance().mBullets.add(new Bullet(mColor, mAimX + mVelocityX, mAimY + mVelocityY, mPositionX, mPositionY));
    }

    protected void calculateAimDirection() {
        mAimX = mController.getJoystickPosition(GamepadController.JOYSTICK_2,
                GamepadController.AXIS_X);
        mAimY = -mController.getJoystickPosition(GamepadController.JOYSTICK_2,
                GamepadController.AXIS_Y);
        float magnitude = Utils.vector2DLength(mAimX, mAimY);

        if (magnitude > 0.1f ) {
            // Normalize the direction vector.
            mAimX /= magnitude;
            mAimY /= magnitude;
        } else {
            // The firing joystick is not being used, so fire any shots in the direction
            // the player is currently traveling.
            mAimX = mHeadingX;
            mAimY = mHeadingY;
        }
    }

    public void setHeadingX( float heading ) {
        mHeadingX = heading;
    }

    public void setHeadingY( float heading ) {
        mHeadingY = heading;
    }

    public GamepadController getController() {
        return mController;
    }

}
