package com.ptrprograms.asteroidbelttv;

import android.content.Context;
import android.hardware.input.InputManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.hardware.input.InputManager.InputDeviceListener;
import android.opengl.Matrix;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.ptrprograms.asteroidbelttv.Objects.Asteroid;
import com.ptrprograms.asteroidbelttv.Objects.Bullet;
import com.ptrprograms.asteroidbelttv.Objects.Ship;
import com.ptrprograms.asteroidbelttv.Utils.Constants;
import com.ptrprograms.asteroidbelttv.Utils.ShapeBuffer;
import com.ptrprograms.asteroidbelttv.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by PaulTR on 7/20/14.
 */
public class GameView extends GLSurfaceView implements GLSurfaceView.Renderer, InputDeviceListener {

    private static GameView mInstance;

    private ShapeBuffer mShapeBuffer;

    private Ship mShip;

    private long mLastUpdateTimeMillis;

    private int mWindowWidth;
    private int mWindowHeight;
    private List<Asteroid> mAsteroids;
    public List<Bullet> mBullets;
    private int mLevel = 1;

    private final float[] mMVPMatrix = new float[16];

    public GameView(Context context) {
        super( context );

        setEGLContextClientVersion( 2 );
        this.setRenderer( this );
        this.requestFocus();

        mInstance = this;

        mLastUpdateTimeMillis = System.currentTimeMillis();

        mShip = new Ship();

        InputManager inputManager = (InputManager) context.getSystemService( Context.INPUT_SERVICE );
        inputManager.registerInputDeviceListener( this, null );
        mAsteroids = new ArrayList<Asteroid>();
        mBullets = new ArrayList<Bullet>();
        initLevel();
    }

    public static GameView getInstance() {
        return mInstance;
    }

    private void initLevel() {
        mShip.reset();
        if( mBullets != null ) {
            ListIterator<Bullet> iter = mBullets.listIterator();
            while( iter.hasNext() ) {
                iter.next();
                iter.remove();
            }
        }
        for( int i = 0; i < mLevel + 2; i++ ) {
            mAsteroids.add( new Asteroid(Utils.Color.RED, Constants.ASTEROID_SIZE_LARGE));
        }
    }

    private void update( float delta ) {
        if( mAsteroids.isEmpty() ) {
            mLevel++;
            initLevel();
            return;
        }

        mShip.update( delta );
        for( Asteroid asteroid : mAsteroids ) {
            asteroid.update( delta );
        }

        ListIterator<Bullet> bulletIter = mBullets.listIterator();
        ListIterator<Asteroid> asteroidIter = mAsteroids.listIterator();
        Bullet bullet;
        Asteroid asteroid;

        while( bulletIter.hasNext() ) {
            bullet = bulletIter.next();
            bullet.update( delta );
            if( bullet.mLifeTimer == 0 ) {
                bulletIter.remove();
            }
        }

        while( asteroidIter.hasNext() ) {
            asteroid = asteroidIter.next();
            bulletIter = mBullets.listIterator();
            while( bulletIter.hasNext() ) {
                bullet = bulletIter.next();
                if ((bullet.mPositionX > asteroid.mPositionX && bullet.mPositionX < (asteroid.mPositionX + (2 * asteroid.mAsteroidSize))
                        && (bullet.mPositionY > asteroid.mPositionY && bullet.mPositionY < (asteroid.mPositionY + (2 * asteroid.mAsteroidSize))))) {
                    bulletIter.remove();
                    asteroidIter.remove();
                    if (asteroid.mAsteroidSize == Constants.ASTEROID_SIZE_LARGE) {
                        asteroidIter.add(new Asteroid(Utils.Color.RED, Constants.ASTEROID_SIZE_MEDIUM, asteroid.mPositionX, asteroid.mPositionY));
                        asteroidIter.add(new Asteroid(Utils.Color.RED, Constants.ASTEROID_SIZE_MEDIUM, asteroid.mPositionX, asteroid.mPositionY));
                    } else if (asteroid.mAsteroidSize == Constants.ASTEROID_SIZE_MEDIUM) {
                        asteroidIter.add(new Asteroid(Utils.Color.RED, Constants.ASTEROID_SIZE_SMALL, asteroid.mPositionX, asteroid.mPositionY));
                        asteroidIter.add(new Asteroid(Utils.Color.RED, Constants.ASTEROID_SIZE_SMALL, asteroid.mPositionX, asteroid.mPositionY));
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {

    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {

    }

    @Override
    public void onInputDeviceChanged(int deviceId) {

    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // The ShapeBuffer creates OpenGl resources, so don't create it until after the
        // primary rendering surface has been created.
        mShapeBuffer = new ShapeBuffer();
        mShapeBuffer.loadResources();
    }

    /**
     * Here we do our drawing
     */
    @Override
    public void onDrawFrame(GL10 unused) {
        // Clear the screen to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Don't try to draw if the shape buffer failed to initialize.
        if (!mShapeBuffer.isInitialized()) {
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();

        // Compute frame delta.  frameDelta = # of "ideal" frames that have occurred since the
        // last update.  "ideal" assumes a constant frame-rate (60 FPS or 16.7 milliseconds per
        // frame).  Since the delta doesn't depend on the "real" frame-rate, the animations always
        // run at the same wall clock speed, regardless of what the real refresh rate is.
        //
        // frameDelta was used instead of a time delta in order to make the values passed
        // to update easier to understand when debugging the code.  For example, a frameDelta
        // of "1.5" means that one and a half hypothetical frames have passed since the last
        // update.  In wall time this would be 25 milliseconds or 0.025 seconds.
        float frameDelta = Utils.millisToFrameDelta(currentTimeMillis - mLastUpdateTimeMillis);

        update(frameDelta);
        draw();
        mLastUpdateTimeMillis = currentTimeMillis;
    }

    /**
     * If the surface changes, reset the view size.
     */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Make sure the window dimensions are never 0.
        mWindowWidth = Math.max(width, 1);
        mWindowHeight = Math.max(height, 1);
    }

    public void draw() {
        mShapeBuffer.clear();
        mShip.draw(mShapeBuffer);
        for( Asteroid asteroid : mAsteroids ) {
            asteroid.draw( mShapeBuffer );
        }
        for( Bullet bullet : mBullets ) {
            bullet.draw( mShapeBuffer );
        }

        updateViewportAndProjection();

        mShapeBuffer.draw( mMVPMatrix );
    }

    private void updateViewportAndProjection() {
        float viewportAspectRatio = 1.0f;
        if ((mWindowWidth > 0) && (mWindowHeight > 0)) {
            viewportAspectRatio = (float) mWindowWidth / (float) mWindowHeight;
        }
        float viewportWidth = (float) mWindowWidth;
        float viewportHeight = (float) mWindowHeight;
        float viewportOffsetX = 0.0f;
        float viewportOffsetY = 0.0f;

        if ( Constants.WORLD_ASPECT_RATIO > viewportAspectRatio ) {
            // Our window is taller than the ideal aspect ratio needed to accommodate the world
            // without stretching.
            // Reduce the viewport height to match the aspect ratio of the world.  The world
            // will fill the whole width of the screen, but have some empty space on the top and
            // bottom of the screen.
            viewportHeight = viewportWidth / Constants.WORLD_ASPECT_RATIO;
            // Center the viewport on the screen.
            viewportOffsetY = ((float) mWindowHeight - viewportHeight) / 2.0f;
        } else if (viewportAspectRatio > Constants.WORLD_ASPECT_RATIO) {
            // Our window is wider than the ideal aspect ratio needed to accommodate the world
            // without stretching.
            // Reduce the viewport width to match the aspect ratio of the world.  The world
            // will fill the whole height of the screen, but have some empty space on the
            // left and right of the screen.
            viewportWidth = viewportHeight * Constants.WORLD_ASPECT_RATIO;
            // Center the viewport on the screen.
            viewportOffsetX = ((float) mWindowWidth - viewportWidth) / 2.0f;
        }

        Matrix.orthoM( mMVPMatrix, 0,
                Constants.WORLD_LEFT_COORDINATE,
                Constants.WORLD_RIGHT_COORDINATE,
                Constants.WORLD_BOTTOM_COORDINATE,
                Constants.WORLD_TOP_COORDINATE,
                Constants.WORLD_NEAR_PLANE,
                Constants.WORLD_FAR_PLANE );
        GLES20.glViewport((int) viewportOffsetX, (int) viewportOffsetY,
                (int) viewportWidth, (int) viewportHeight);
    }

    public boolean handleMotionEvent(MotionEvent motionEvent) {
        if ( mShip != null ) {
            mShip.getController().setDeviceId( motionEvent.getDeviceId() );
            mShip.getController().handleMotionEvent(motionEvent);
            return true;
        }
        return false;
    }

    public boolean handleKeyEvent(KeyEvent keyEvent) {
        if ( mShip != null) {
            mShip.getController().setDeviceId( keyEvent.getDeviceId() );
            mShip.getController().handleKeyEvent(keyEvent);
            return true;
        }
        return false;
    }
}
