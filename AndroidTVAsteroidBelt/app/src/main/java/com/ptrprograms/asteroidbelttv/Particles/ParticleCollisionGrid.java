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


import com.ptrprograms.asteroidbelttv.Utils.Utils;

/**
 * Simple class for spatially partitioning particles into a 2-d grid structure.
 *
 * The world is partitioned into a 2-d grid of zones.  As particles are inserted, they are
 * assigned to one of the zones.  Every frame, the grid must be cleared and the particles
 * reinserted at their updated positions.
 *
 * Proximity queries (getRectPopulation()) can quickly determine the list of particles in
 * a given rectangular section of the world.
 *
 * Assumes that the origin is in the center of the screen.
 */
public class ParticleCollisionGrid {
    // The maximum number of particles that can be added to a single grid square.  If too
    // many particles are added to a single zone, only the first 100 will be tracked.
    private static final int MAX_ENTITIES_PER_ZONE = 100;
    // The maximum number of particles returned for a proximity query (getRectPopulation()).
    private static final int MAX_RETURNED_VALUES = 4000;

    // Row and column sizes in world coordinates.
    private float mColumnWidth, mRowHeight;
    // The index of the last row and column in the grid.
    private int mColumnMax, mRowMax;
    // The total number of zones in the grid.
    private int mZoneCount;
    // Dimensions of the world.
    private float mWorldWidth, mWorldHeight;
    // Each zone has an array of particles.  The zones are stored in row-major order in the first
    // dimension of mZoneArray (e.g. the particles in a given row and column can be found by
    // referencing mZoneArray[row + column * mColumnMax]).
    private final BaseParticle[][] mZoneArray;
    // Keeps track of the number of particles in each zone.  Stored in row-major order,
    // like mZoneArray.
    private final int[] mZonePopulation;

    // To avoid the overhead of allocating a new list for each collision check,
    // this list is allocated once and reused for each query.
    private final BaseParticle[] mReturnValues;

    /**
     * Constructs a new collision grid.
     *
     * The origin of the world is at 0, 0.
     *
     * @param width the total width of the grid.
     * @param height the total height of the grid.
     * @param zoneSize the number of world units in each zone.  Each zone is square, and
     *                 zoneSize is the length of the sides of the square.
     */
    public ParticleCollisionGrid(float width, float height, float zoneSize) {
        mWorldWidth = width;
        mWorldHeight = height;
        mColumnMax = (int) Math.ceil(width / zoneSize);
        mRowMax = (int) Math.ceil(height / zoneSize);
        mColumnWidth = zoneSize;
        mRowHeight = zoneSize;
        mZoneCount = mColumnMax * mRowMax;

        mZoneArray = new BaseParticle[mZoneCount][MAX_ENTITIES_PER_ZONE];
        mZonePopulation = new int[mZoneCount];

        mReturnValues = new BaseParticle[MAX_RETURNED_VALUES];
    }

    /**
     * Removes all the objects from the grid.
     */
    public void clear() {
        for (int i = 0; i < mZoneCount; i++) {
            clearZone(i);
        }
    }

    /**
     * Insert a particle into the grid.
     */
    public void addParticle(BaseParticle particle) {
        // The particles use world coordinates, which need to be converted to grid coordinates
        // before they can be inserted.
        addObjectHelper(particle, worldXToGridX(particle.getPositionX()),
                worldYToGridY(particle.getPositionY()));
    }

    /**
     * Returns an array containing all the particles in the given section of the grid.
     *
     * The returned particles will start at array element 0.  The last valid element
     * will be followed by an element set to null.
     *
     * The returned array will become invalid next time getRectPopulation is called.
     */
    public BaseParticle[] getRectPopulation(float x1, float y1, float x2, float y2) {
        int leftSlot, rightSlot, topSlot, bottomSlot;
        leftSlot = (int) Math.floor(worldXToGridX(x1) / mColumnWidth) - 1;
        if (leftSlot < 0) {
            leftSlot = 0;
        }
        rightSlot = (int) Math.floor(worldXToGridX(x2) / mColumnWidth) + 1;
        if (rightSlot >= mColumnMax) {
            rightSlot = mColumnMax - 1;
        }
        topSlot = (int) Math.floor(worldYToGridY(y1) / mRowHeight) - 1;
        if (topSlot < 0) {
            topSlot = 0;
        }
        bottomSlot = (int) Math.floor(worldYToGridY(y2) / mRowHeight) + 1;
        if (bottomSlot >= mRowMax) {
            bottomSlot = mRowMax - 1;
        }

        int returnedValueCount = 0;

        // Iterate through each zone covered by the given rectangle.
        for (int x = leftSlot; x <= rightSlot; ++x) {
            for (int y = topSlot; y <= bottomSlot; ++y) {
                int currentZone = x + y * mColumnMax;
                // Add all the particles in the zone.
                for (int i = 0; i < mZonePopulation[currentZone]; ++i) {
                    mReturnValues[returnedValueCount] = mZoneArray[currentZone][i];
                    ++returnedValueCount;
                    if (returnedValueCount >= MAX_RETURNED_VALUES - 1) {
                        // Don't have enough room for more hits, so bail out.
                        break;
                    }
                }
            }
        }

        // Set the slot after the last particle to null.
        mReturnValues[returnedValueCount] = null;

        return mReturnValues;
    }

    /**
     * Converts a world coordinate to grid coordinate.
     *
     * The world coordinate system has the origin in the middle of the map and extends
     * mWorldWidth / 2 units to the left and right.  The grid can not store negative values, so
     * world coordinates must be biased by mWorldWidth / 2 so that they are always positive.
     */
    private float worldXToGridX(float worldX) {
        return worldX + mWorldWidth / 2.0f;
    }
    /**
     * Converts a world coordinate to grid coordinate.
     *
     * The world coordinate system has the origin in the middle of the map and extends
     * mWorldHeight / 2 units to the above and below the origin.
     * The grid can not store negative values, so world coordinates must be biased by
     * mWorldHeight / 2 so that they are always positive.
     */
    private float worldYToGridY(float worldY) {
        return worldY + mWorldHeight / 2.0f;
    }

    private void addToZone(BaseParticle particle, int zone) {
        if (mZonePopulation[zone] < MAX_ENTITIES_PER_ZONE) {
            mZoneArray[zone][mZonePopulation[zone]] = particle;
            mZonePopulation[zone]++;
        }
    }

    private void clearZone(int zone) {
        mZonePopulation[zone] = 0;
        // This next part is just to make sure garbage collection can happen.  Not that it should.
        for (int i = 0; i < MAX_ENTITIES_PER_ZONE; i++) {
            mZoneArray[zone][i] = null;
        }
    }

    private void addObjectHelper(BaseParticle particle, float gridX, float gridY) {
        int zone = getZoneOnGrid(gridX, gridY);
        if (zone != -1) {
            addToZone(particle, zone);
        }
    }

    private int getZoneOnGrid(float gridX, float gridY) {
        int gridZoneX = (int) Math.floor(gridX / mColumnWidth);
        int gridZoneY = (int) Math.floor(gridY / mRowHeight);

        if (gridZoneX < 0) {
            gridZoneX = 0;
        }
        if (gridZoneY < 0) {
            gridZoneY = 0;
        }
        if (gridZoneX >= mColumnMax) {
            gridZoneX = mColumnMax - 1;
        }
        if (gridZoneY >= mRowMax) {
            gridZoneY = mRowMax - 1;
        }
        return (gridZoneX + gridZoneY * mColumnMax);
    }
}