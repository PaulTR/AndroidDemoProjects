package com.ptrprograms.asteroidbelttv.Utils;

/**
 * Created by PaulTR on 7/20/14.
 */
public class Utils {

    public static float secondsToFrameDelta(float seconds) {
        return seconds * Constants.FRAME_RATE;
    }

    public static float millisToFrameDelta(long milliseconds) {
        return secondsToFrameDelta( (float) milliseconds / 1000.0f );
    }

    public static float vector2DLength(float x, float y) {
        return (float) Math.sqrt(vector2DLengthSquared(x, y));
    }

    public static float vector2DLengthSquared(float x, float y) {
        return x * x + y * y;
    }

    public static boolean isInYPlane( float position, float size ) {
        return Constants.MAP_BOTTOM_COORDINATE - size < position && position < Constants.MAP_TOP_COORDINATE + size;
    }

    public static boolean isOffScreenAboveTop( float position, float size ) {
        return position > Constants.MAP_TOP_COORDINATE + size;
    }

    public static boolean isOffScreenBelowBottom( float position, float size ) {
        return position < Constants.MAP_BOTTOM_COORDINATE - size;
    }

    public static boolean isInXPlane( float position, float size ) {
        return Constants.MAP_LEFT_COORDINATE - size < position && position < Constants.MAP_RIGHT_COORDINATE + size;
    }

    public static boolean isOffScreenToRight( float position, float size ) {
        return position > Constants.MAP_RIGHT_COORDINATE;
    }

    public static boolean isOffScreenToLeft( float position, float size ) {
        return position < Constants.MAP_LEFT_COORDINATE;
    }

    public static class Color {

        public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);
        public static final Color RED = new Color( 1.0f, 0.0f, 0.0f );
        private static final int RED_SHIFT = 0;
        private static final int GREEN_SHIFT = 8;
        private static final int BLUE_SHIFT = 16;
        private static final int ALPHA_SHIFT = 24;

        private int mABGR;

        public Color() {}

        public Color(float red, float green, float blue) {
            mABGR = packNormalizedRGBAToABGR(red, green, blue, 1.0f);
        }

        public int getPackedABGR() {
            return mABGR;
        }

        private static int normalizedColorToInt(float normalizedColor) {
            return (int) (255.0f * normalizedColor);
        }

        private static int packNormalizedRGBAToABGR(float red, float green, float blue,
                                                    float alpha) {
            return packABGR(
                    normalizedColorToInt(red),
                    normalizedColorToInt(green),
                    normalizedColorToInt(blue),
                    normalizedColorToInt(alpha));
        }

        private static int packABGR(int red, int green, int blue, int alpha) {
            return ( red << RED_SHIFT )
                    | ( green << GREEN_SHIFT )
                    | ( blue << BLUE_SHIFT )
                    | ( alpha << ALPHA_SHIFT );
        }


        public void set(float red, float green, float blue, float alpha) {
            mABGR = packNormalizedRGBAToABGR(red, green, blue, alpha);
        }

        public void set(Color other) {
            this.mABGR = other.mABGR;
        }

    }
}
