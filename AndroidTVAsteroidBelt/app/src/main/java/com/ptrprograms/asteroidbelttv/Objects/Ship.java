package com.ptrprograms.asteroidbelttv.Objects;

import android.util.Log;

import com.ptrprograms.asteroidbelttv.Particles.BaseParticle;
import com.ptrprograms.asteroidbelttv.Utils.Constants;
import com.ptrprograms.asteroidbelttv.GameView;
import com.ptrprograms.asteroidbelttv.Utils.GamepadController;
import com.ptrprograms.asteroidbelttv.Utils.ShapeBuffer;
import com.ptrprograms.asteroidbelttv.Utils.Utils;

/**
 * Created by PaulTR on 7/20/14.
 */
public class Ship {

    //private GameView mGameView;
    private final GamepadController mController = new GamepadController();

    private static final float[] SHIP_SHAPE = {
            -1.0f,  0.5f,
            -1.0f, -0.5f,
            1.0f,   0.0f
    };

    private static final float SHIP_SIZE = 5.0f;
    private static final float DRAG = 0.05f;
    private static final float MINIMUM_VELOCITY = 0.05f;

    private float mHeadingX = 0.0f;
    private float mHeadingY = 1.0f;
    private float mVelocityX;
    private float mVelocityY;
    private float mVelocityMultiplier = 1.5f;
    private float mPositionX;
    private float mPositionY;
    private float mRespawnTimer = 3.0f;

    private int mFireTimer = 0;

    private boolean mJoystickAiming;

    private static final float BULLET_SPEED_BASEGUN = 2.5f;
    private static final float BULLET_LIFETIME_IN_SECONDS = 5.0f;
    private static final float BULLET_PARTICLE_SIZE = 0.75f;
    private static final float BULLET_PARTICLE_ASPECT_RATIO = 3.0f;
    private static final float BULLET_PARTICLE_INITIAL_POSITION_INCREMENT = 3.0f;
    private float mAimX, mAimY;
    private static final float GUN_FIREDELAY_BASEGUN = 0.25f;
    private float mGunRechargeTimer;


    private Utils.Color mColor = new Utils.Color();

    public Ship( GameView gameView, Utils.Color color) {
        //this.mGameView = gameView;
        this.mHeadingX = 0.0f;
        this.mHeadingY = 1.0f;
        this.mColor.set(color);

        mRespawnTimer = 1.0f;
    }

    public void draw( ShapeBuffer sb) {
        if (!isSpawned()) {
            return;
        }

        sb.add2DShape(mPositionX, mPositionY, mColor, SHIP_SHAPE, SHIP_SIZE, SHIP_SIZE,
                mHeadingX, mHeadingY);

    }

    public void setPositionX( float positionX ) {
        if( Constants.MAP_LEFT_COORDINATE - SHIP_SIZE < positionX && positionX < Constants.MAP_RIGHT_COORDINATE + SHIP_SIZE ) {
            mPositionX = positionX;
        } else if( positionX > Constants.MAP_RIGHT_COORDINATE ) {
            mPositionX = Constants.MAP_LEFT_COORDINATE;
        } else if( positionX < Constants.MAP_LEFT_COORDINATE ) {
            mPositionX = Constants.MAP_RIGHT_COORDINATE;
        }
    }

    public void setPositionY( float positionY ) {
        if( Constants.MAP_BOTTOM_COORDINATE - SHIP_SIZE < positionY && positionY < Constants.MAP_TOP_COORDINATE + SHIP_SIZE ) {
            mPositionY = positionY;
        } else if( positionY > Constants.MAP_TOP_COORDINATE + SHIP_SIZE ) {
            mPositionY = Constants.MAP_BOTTOM_COORDINATE;
        } else if( positionY < Constants.MAP_BOTTOM_COORDINATE - SHIP_SIZE ) {
            mPositionY = Constants.MAP_TOP_COORDINATE;
        }
    }

    public void update(float delta ) {
        if ( !updateStatus( delta ) ) {
            return;
        }

        updateShipPosition( delta );
        checkBulletCollisions();
        handleKeyInput( delta );
    }

    private void checkBulletCollisions() {

    }

    private boolean updateStatus( float delta ) {
        updateSpawningStatus( delta );
        return isSpawned();
    }

    public boolean isSpawned() {
        return ( mRespawnTimer <= 0.0f );
    }

    public boolean isActive() {
        //return mController.isActive();
        return true;
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

    private void updateShipPosition(float frameDelta) {
        float newHeadingX = mController.getJoystickPosition(GamepadController.JOYSTICK_1,
                GamepadController.AXIS_X);
        float newHeadingY = mController.getJoystickPosition(GamepadController.JOYSTICK_1,
                GamepadController.AXIS_Y);

        float magnitude = Utils.vector2DLength(newHeadingX, newHeadingY);
        if (magnitude > GamepadController.JOYSTICK_MOVEMENT_THRESHOLD) {
            // Normalize the direction vector.
            mHeadingX = newHeadingX / magnitude;
            mHeadingY = -newHeadingY / magnitude;

            // Compute the new speed.
            mVelocityX = newHeadingX;
            mVelocityY = -newHeadingY;

            mVelocityX *= mVelocityMultiplier;
            mVelocityY *= mVelocityMultiplier;

            if (magnitude > 1.0f) {
                // Limit the max speed to "1".  If the movement joystick is moved less than
                // 1 unit from the center, the ship will move less than it's maximum speed.
                // If the joystick moves more than 1 unit from the center, dividing by
                // magnitude will limit the speed of the ship, but keep the direction of moment
                // correct.
                mVelocityX /= magnitude;
                mVelocityY /= magnitude;
            }
        }

        setPositionX(mPositionX + mVelocityX * frameDelta);
        setPositionY(mPositionY + mVelocityY * frameDelta);

        // Use drag so that the ship will coast to a stop after the movement controller
        // is released.
        mVelocityX *= 1.0f - frameDelta * DRAG;
        mVelocityY *= 1.0f - frameDelta * DRAG;
        if (Utils.vector2DLength(mVelocityX, mVelocityY) < MINIMUM_VELOCITY) {
            mVelocityX = 0.0f;
            mVelocityY = 0.0f;
        }
    }

    private void handleKeyInput( float delta ) {
        if( mFireTimer > 0 )
            mFireTimer--;
        if ( mController.isButtonDown(GamepadController.BUTTON_X) && mFireTimer == 0 ) {
            mFireTimer = 20;
            calculateAimDirection();
            fireGun();
        }
    }

    private void fireGun() {
        fireBullets(1, 0, BULLET_SPEED_BASEGUN, GUN_FIREDELAY_BASEGUN);

    }

    protected void fireBullets(int bulletCount, float spreadArc, float speed,
                               float recharge) {
        mGunRechargeTimer = recharge;

        for (int i = 0; i < bulletCount; ++i) {
            float angleDegrees;
            if (bulletCount > 1) {
                // Compute this bullet's position along the spread arc.
                angleDegrees =
                        -spreadArc / 2.0f + (float) i * spreadArc / ((float) bulletCount - 1.0f);
            } else {
                // Single bullets are always fired along the aiming direction.
                angleDegrees = 0;
            }
            float angleRadians = (float) Math.toRadians(angleDegrees);
            float angleSin = (float) Math.sin(angleRadians);
            float angleCos = (float) Math.cos(angleRadians);

            float shotDx = mAimX * angleCos - mAimY * angleSin;
            float shotDy = mAimX * angleSin + mAimY * angleCos;
            BaseParticle myShot = GameView.getShots().spawnParticle(BULLET_LIFETIME_IN_SECONDS);
            if (myShot != null) {
                myShot.setPosition(mPositionX, mPositionY);
                myShot.setSpeed(shotDx * speed, shotDy * speed);
                myShot.setColor(mColor);
                myShot.setSize(BULLET_PARTICLE_SIZE);
                myShot.setAspectRatio(BULLET_PARTICLE_ASPECT_RATIO);

                // Offset the bullet's starting position a few steps ahead of our position.
                myShot.incrementPosition(BULLET_PARTICLE_INITIAL_POSITION_INCREMENT);
            }
        }
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
            mJoystickAiming = true;
        } else {
            // The firing joystick is not being used, so fire any shots in the direction
            // the player is currently traveling.
            mAimX = mHeadingX;
            mAimY = mHeadingY;
            mJoystickAiming = false;
        }
    }

    public GamepadController getController() {
        return mController;
    }

}
