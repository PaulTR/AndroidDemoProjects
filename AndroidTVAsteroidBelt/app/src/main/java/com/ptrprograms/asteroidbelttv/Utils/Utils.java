package com.ptrprograms.asteroidbelttv.Utils;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by PaulTR on 7/20/14.
 */
public class Utils {

    public static final float[] SQUARE_SHAPE = {
            1.0f,  1.0f,
            -1.0f,  1.0f,
            1.0f, -1.0f,
            -1.0f, -1.0f
    };

    public static PointF randDirectionVector() {
        // Pick a random point in a square centered about the origin.
        PointF direction = randPointInRect(new RectF(-1.0f, 1.0f, 1.0f, -1.0f));

        // Turn the chosen point into a direction vector by normalizing it.
        normalizeDirectionVector(direction);

        return direction;
    }

    public static void normalizeDirectionVector(PointF direction) {
        float length = direction.length();
        if (length == 0.0f) {
            direction.set(1.0f, 0.0f);
        } else {
            direction.x /= length;
            direction.y /= length;
        }
    }

    public static PointF randPointInRect(RectF rect) {
        float x = randFloatInRange(rect.left, rect.right);
        float y = randFloatInRange(rect.bottom, rect.top);

        return new PointF(x, y);
    }

    public static float randFloatInRange(float lowerBound, float upperBound) {
        return (float) (Math.random() * (upperBound - lowerBound) + lowerBound);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

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

    public static class Color {

        public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);
        public static final Color RED = new Color( 1.0f, 0.0f, 0.0f );
        private static final int RED_MASK = 0xffffff00;
        private static final int RED_SHIFT = 0;
        private static final int GREEN_MASK = 0xffff00ff;
        private static final int GREEN_SHIFT = 8;
        private static final int BLUE_MASK = 0xff00ffff;
        private static final int BLUE_SHIFT = 16;
        private static final int ALPHA_MASK = 0x00ffffff;
        private static final int ALPHA_SHIFT = 24;

        private int mABGR;

        public Color() {}

        public Color(float red, float green, float blue, float alpha) {
            set(red, green, blue, alpha);
        }
        public Color(float red, float green, float blue) {
            mABGR = packNormalizedRGBAToABGR(red, green, blue, 1.0f);
        }
        public Color(Utils.Color other) {
            set(other);
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
