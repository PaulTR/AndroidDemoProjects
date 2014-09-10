/*
 * Originally found and modified from here: https://github.com/googlesamples/androidtv-GameController/blob/master/GameControllerSample/src/main/java/com/google/fpl/gamecontroller/GamepadController.java
 */

package com.ptrprograms.asteroidbelttv.Utils;

import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Handles input events from game pad controllers (includes joystick and button inputs).
 */
public class GamepadController {

    // The buttons on the game pad.
    public static final int BUTTON_A = 0;
    public static final int BUTTON_B = 1;
    public static final int BUTTON_X = 2;
    public static final int BUTTON_Y = 3;
    public static final int BUTTON_R1 = 4;
    public static final int BUTTON_R2 = 5;
    public static final int BUTTON_L1 = 6;
    public static final int BUTTON_L2 = 7;
    public static final int BUTTON_COUNT = 8;

    // The axes for joystick movement.
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_COUNT = 2;

    // Game pads usually have 2 joysticks.
    public static final int JOYSTICK_1 = 0;
    public static final int JOYSTICK_2 = 1;
    public static final int JOYSTICK_COUNT = 2;

    // Keep track of button states for the current and previous frames.
    protected static final int FRAME_INDEX_CURRENT = 0;
    protected static final int FRAME_INDEX_PREVIOUS = 1;
    protected static final int FRAME_INDEX_COUNT = 2;

    // Positions of the two joysticks.
    private final float mJoystickPositions[][];
    // The button states for the current and previous frames.
    private final boolean mButtonState[][];
    // The device that we are tuned to.
    private int mDeviceId = -1;

    public static final float JOYSTICK_MOVEMENT_THRESHOLD = 0.1f;

    public GamepadController() {
        mButtonState = new boolean[BUTTON_COUNT][FRAME_INDEX_COUNT];
        mJoystickPositions = new float[JOYSTICK_COUNT][AXIS_COUNT];
        resetState();
    }

    /**
     * Returns the controller to its default state.
     *
     * The histories for all joystick movements and button presses is also reset.
     */
    private void resetState() {
        for (int button = 0; button < BUTTON_COUNT; ++button) {
            for (int frame = 0; frame < FRAME_INDEX_COUNT; ++frame) {
                mButtonState[button][frame] = false;
            }
        }
        for (int joystick = 0; joystick < JOYSTICK_COUNT; ++joystick) {
            for (int axis = 0; axis < AXIS_COUNT; ++axis) {
                mJoystickPositions[joystick][axis] = 0.0f;
            }
        }
    }

    /**
     * Returns the position of a joystick along a single axis.
     *
     * @param joystickIndex One of: JOYSTICK_1 or JOYSTICK_2.
     * @param axis One of: AXIS_X or AXIS_Y.
     * @return A value in the range -1 to 1, inclusive, where 0 represents the joystick's
     *          center position.
     */
    public float getJoystickPosition(int joystickIndex, int axis) {
        return mJoystickPositions[joystickIndex][axis];
    }

    /**
     * Returns true if the given button is currently pressed.
     *
     * @param buttonId One of: BUTTON_A, BUTTON_B, BUTTON_X, or BUTTON_Y.
     * @return true if the given button is currently pressed.
     */
    public boolean isButtonDown(int buttonId) {
        return mButtonState[buttonId][FRAME_INDEX_CURRENT];
    }

    public void setDeviceId(int newId) {
        if (newId != mDeviceId) {
            mDeviceId = newId;
            if (newId != -1) {
                // Reset our button and axis state when a new physical device is attached.
                resetState();
            }
        }
    }

    /**
     * Updates the tracked state values of this controller in response to a motion input event.
     */
    public void handleMotionEvent(MotionEvent motionEvent) {
        mJoystickPositions[JOYSTICK_1][AXIS_X] = motionEvent.getAxisValue(MotionEvent.AXIS_X);
        mJoystickPositions[JOYSTICK_1][AXIS_Y] = motionEvent.getAxisValue(MotionEvent.AXIS_Y);

        // The X and Y axes of the second joystick on a controller are mapped to the
        // MotionEvent AXIS_Z and AXIS_RZ values, respectively.
        mJoystickPositions[JOYSTICK_2][AXIS_X] = motionEvent.getAxisValue(MotionEvent.AXIS_Z);
        mJoystickPositions[JOYSTICK_2][AXIS_Y] = motionEvent.getAxisValue(MotionEvent.AXIS_RZ);
    }

    /**
     * Updates the tracked state values of this controller in response to a key input event.
     */
    public void handleKeyEvent(KeyEvent keyEvent) {
        boolean keyIsDown = keyEvent.getAction() == KeyEvent.ACTION_DOWN;

        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BUTTON_A) {
            mButtonState[BUTTON_A][FRAME_INDEX_CURRENT] = keyIsDown;
        } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BUTTON_B) {
            mButtonState[BUTTON_B][FRAME_INDEX_CURRENT] = keyIsDown;
        } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BUTTON_X) {
            mButtonState[BUTTON_X][FRAME_INDEX_CURRENT] = keyIsDown;
        } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BUTTON_Y) {
            mButtonState[BUTTON_Y][FRAME_INDEX_CURRENT] = keyIsDown;
        } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BUTTON_R1 ) {
            mButtonState[BUTTON_R1][FRAME_INDEX_CURRENT] = keyIsDown;
        } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BUTTON_R2 ) {
            mButtonState[BUTTON_R2][FRAME_INDEX_CURRENT] = keyIsDown;
        } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BUTTON_L1 ) {
            mButtonState[BUTTON_L1][FRAME_INDEX_CURRENT] = keyIsDown;
        } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BUTTON_L2 ) {
            mButtonState[BUTTON_L2][FRAME_INDEX_CURRENT] = keyIsDown;
        }
    }
}
