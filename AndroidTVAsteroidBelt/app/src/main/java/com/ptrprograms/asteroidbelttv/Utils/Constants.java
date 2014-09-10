package com.ptrprograms.asteroidbelttv.Utils;

/**
 * Created by PaulTR on 7/20/14.
 */
public class Constants {

    public static final int WORLD_WIDTH = 1280 / 2;
    public static final int WORLD_HEIGHT = 720 / 2;

    private static final int MAP_WALL_THICKNESS = 8;

    public static final int MAP_WIDTH = WORLD_WIDTH - ( 2 * MAP_WALL_THICKNESS );
    public static final int MAP_HEIGHT = WORLD_HEIGHT - ( 2 * MAP_WALL_THICKNESS );

    public static final int WORLD_TOP_COORDINATE = WORLD_HEIGHT / 2;
    public static final int WORLD_BOTTOM_COORDINATE = -WORLD_HEIGHT / 2;
    public static final int WORLD_LEFT_COORDINATE = -WORLD_WIDTH / 2;
    public static final int WORLD_RIGHT_COORDINATE = WORLD_WIDTH / 2;

    public static final int MAP_TOP_COORDINATE = MAP_HEIGHT / 2;
    public static final int MAP_BOTTOM_COORDINATE = -MAP_HEIGHT / 2;
    public static final int MAP_LEFT_COORDINATE = -MAP_WIDTH / 2;
    public static final int MAP_RIGHT_COORDINATE = MAP_WIDTH / 2;

    public static final float WORLD_NEAR_PLANE = -1.0f;
    public static final float WORLD_FAR_PLANE = 1.0f;
    public static final float WORLD_ASPECT_RATIO = (float) WORLD_WIDTH / (float) WORLD_HEIGHT;

    public static final float FRAME_RATE = 60.0f;

    public static final float ASTEROID_SIZE_LARGE = 15.0f;
    public static final float ASTEROID_SIZE_MEDIUM = ASTEROID_SIZE_LARGE / 2;
    public static final float ASTEROID_SIZE_SMALL = ASTEROID_SIZE_MEDIUM / 2;

}
