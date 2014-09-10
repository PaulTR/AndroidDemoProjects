/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ptrprograms.asteroidbelttv.Particles;

import com.ptrprograms.asteroidbelttv.Utils.Constants;
import com.ptrprograms.asteroidbelttv.Utils.ShapeBuffer;
import com.ptrprograms.asteroidbelttv.Utils.Utils;

/**
 * Base class used to handle particle effects.
 */
public class BaseParticle {
    public static final int PARTICLE_TYPE_NORMAL = 0;
    // Rocket particles have different collision behavior, produce a trail of exhaust, and
    // have an acceleration.
    public static final int PARTICLE_TYPE_ROCKET = 1;

    // Particles fade in and out over a 1 second period.
    private static final float FADE_FRAME_COUNT = 60.0f;
    private static final float FADE_DELTA_PER_FRAME = 1.0f / FADE_FRAME_COUNT;

    // The max velocity of rocket particles.
    private static final float ROCKET_MAX_SPEED_SQUARED = 6.0f * 6.0f;
    // Rocket acceleration per frame (expressed as a percentage increase increase over the
    // rocket's current speed).
    private static final float ROCKET_ACCELERATION = 0.05f;

    // Particles with an active frame count of 0 are not drawn or updated.
    private float mActiveFrameCountRemaining = 0.0f;

    // Either PARTICLE_TYPE_NORMAL or PARTICLE_TYPE_ROCKET.
    private int mParticleType;
    // Screen-space position of the center of the particle.
    private float mPositionX, mPositionY;
    // The distance this particle moves each frame.
    private float mVelocityX, mVelocityY;
    // Scales the coordinates Utils.SQUARE_SHAPE to create larger or smaller particles.
    public static float mSize;
    // The color of the particle.
    private final Utils.Color mColor = new Utils.Color();
    // A value between 0 and 1 used to scale the alpha value of the color.  As particles fade
    // in and out, the alpha value in mColor changes.  When a particle is not in the process of
    // fading, its transparency will be set to mMaxAlpha.
    private float mMaxAlpha;
    // The ratio of the particle's width to height.  Particles that have an aspect ratio other
    // than 1.0 will automatically rotate to point in the direction they are traveling.
    private float mAspectRatio;
    // The id of the Spaceship that owns this particle.  Used for bullet particles to award
    // points.
    // If true, the particle will become inactive as soon as its center passes outside the
    // bounds of the world.
    private boolean mDieOffscreen;

    private final Utils.Color mCurrentColor = new Utils.Color();

    /**
     * Returns a particle to its default state.
     *
     * This function is called to initialize newly spawned particles.
     *
     * @param lifetimeFrameCount the total number of frames this particle will be active.
     */
    public void reset(float lifetimeFrameCount) {
        this.mActiveFrameCountRemaining = lifetimeFrameCount;
        this.mParticleType = PARTICLE_TYPE_NORMAL;
        this.mPositionX = 0.0f;
        this.mPositionY = 0.0f;
        this.mVelocityX = 0.0f;
        this.mVelocityY = 0.0f;
        this.mSize = 1.0f;
        this.mColor.set(1.0f, 1.0f, 1.0f, 1.0f);
        this.mMaxAlpha = 1.0f;
        this.mAspectRatio = 1.0f;
        this.mDieOffscreen = true;
        // Set newly created particles to be transparent so that particles will not be visible
        // until they have had update() called on them.  This avoids ordering issues that can
        // occur when particles are created during the update phase of the frame.
    }

    public void setParticleType(int particleType) {
        this.mParticleType = particleType;
    }

    public void setPosition(float x, float y) {
        this.mPositionX = x;
        this.mPositionY = y;
    }
    public float getPositionX() {
        return mPositionX;
    }
    public float getPositionY() {
        return mPositionY;
    }

    public void setSpeed(float speedX, float speedY) {
        this.mVelocityX = speedX;
        this.mVelocityY = speedY;
    }

    public void setSize(float size) {
        this.mSize = size;
    }
    public float getSize() {
        return mSize;
    }

    public void setColor(Utils.Color color) {
        this.mColor.set(color);
    }
    public Utils.Color getColor() {
        return mColor;
    }

    public void setMaxAlpha(float maxAlpha) {
        this.mMaxAlpha = maxAlpha;
    }

    public void setAspectRatio(float aspectRatio) {
        this.mAspectRatio = aspectRatio;
    }

    public void setDieOffscreen(boolean dieOffscreen) {
        this.mDieOffscreen = dieOffscreen;
    }

    public void update(float frameDelta) {


        incrementPosition(frameDelta);
        mActiveFrameCountRemaining -= frameDelta;

        // Update the particle's alpha every frame.

        if (mDieOffscreen) {
            if (!inWorld(mPositionX, mPositionY)) {
                // The particle is outside the screen, so kill it.
                mActiveFrameCountRemaining = 0.0f;
            }
        }
        // Special update for rockets.
        if (mParticleType == PARTICLE_TYPE_ROCKET) {
            handleRocketUpdate(frameDelta);
        }

        mCurrentColor.set(mColor);
    }

    public static boolean inWorld(float x, float y) {
        return x >= Constants.WORLD_LEFT_COORDINATE
                && x <= Constants.WORLD_RIGHT_COORDINATE
                && y >= Constants.WORLD_BOTTOM_COORDINATE
                && y <= Constants.WORLD_TOP_COORDINATE;
    }

    protected void handleRocketUpdate(float frameDelta) {
        float clampedX = Utils.clamp(mPositionX, Constants.MAP_LEFT_COORDINATE,
                Constants.MAP_RIGHT_COORDINATE);
        float clampedY = Utils.clamp(mPositionY, Constants.MAP_BOTTOM_COORDINATE,
                Constants.MAP_TOP_COORDINATE);
        if (clampedX != mPositionX || clampedY != mPositionY) {
            // The rocket hit the edge of the map, so make it explode.
            mPositionX = clampedX;
            mPositionY = clampedY;
            handleCollision();
        }

        // The rocket particle will accelerate up to a maximum speed.
        float currentSpeedSquared = Utils.vector2DLengthSquared(mVelocityX, mVelocityY);
        if (currentSpeedSquared <= ROCKET_MAX_SPEED_SQUARED) {
            mVelocityX *= ROCKET_ACCELERATION * frameDelta + 1.0f;
            mVelocityY *= ROCKET_ACCELERATION * frameDelta + 1.0f;
        }

    }

    /**
     * Advance the particle's position by the given number of frames.
     */
    public void incrementPosition(float frameDelta) {
        mPositionX += mVelocityX * frameDelta;
        mPositionY += mVelocityY * frameDelta;
    }

    public void draw(ShapeBuffer sb) {
        float headingX = 0.0f, headingY = 0.0f;
        if (mAspectRatio != 1.0f) {
            // Non-square particles point in the direction they are moving.
            headingX = mVelocityX;
            headingY = mVelocityY;
        }

        sb.add2DShape(
                mPositionX, mPositionY,
                mCurrentColor,
                Utils.SQUARE_SHAPE,
                mSize * mAspectRatio, mSize,
                headingX, headingY);
    }

    public void handleCollision() {
        /*
        if (mParticleType == PARTICLE_TYPE_ROCKET) {
            // Add the shrapnel particles to the "shots" layer so that they will be checked
            // for collisions with other players.
            GameState.getInstance().getShots().spawnShrapnelExplosion(mPositionX, mPositionY,
                    mColor, 0.5f, 1.5f, mOwnerId, 100);
        } else {
            // Create a little bit of "smoke" when the particle hits something.
            GameState.getInstance().getExplosions().spawnRingBurst(mPositionX, mPositionY, mColor,
                    0.15f, 0.75f, 5);
        }
        */
        mActiveFrameCountRemaining = 0.0f;
    }

    public boolean isActive() {
        return mActiveFrameCountRemaining > 0.0f;
    }
}