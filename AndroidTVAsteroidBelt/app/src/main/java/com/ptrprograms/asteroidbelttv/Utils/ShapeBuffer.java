/*
 * Originally found and modified from here: https://github.com/googlesamples/androidtv-GameController/blob/master/GameControllerSample/src/main/java/com/google/fpl/gamecontroller/ShapeBuffer.java
 */

package com.ptrprograms.asteroidbelttv.Utils;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.ptrprograms.asteroidbelttv.BuildConfig;
import com.ptrprograms.asteroidbelttv.GameView;
import com.ptrprograms.asteroidbelttv.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Handles collecting and rendering of triangles.
 */
public class ShapeBuffer {
    // Number of coordinates per vertex in this array.
    private static final int COORDS_PER_VERTEX = 3;
    // 4 bytes per vertex.
    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

    // Number of color components in each vertex.
    private static final int COLOR_CHANNELS_PER_VERTEX = 4;
    // Each color component is one byte.
    private static final int COLOR_STRIDE = COLOR_CHANNELS_PER_VERTEX;
    private static final int MAX_BUFFER_SIZE = 50000;

    // Names used in the vertex and fragment shaders.
    private static final String POSITION_ATTRIBUTE_NAME = "position";
    private static final String COLOR_ATTRIBUTE_NAME = "color";
    private static final String MVP_MATRIX_UNIFORM_NAME = "mvpMatrix";

    private int mShaderProgram = 0;
    private int mCurrentIndex;
    private final float[] mVertexData;
    private final int[] mColorData;
    private final IntBuffer mColorBuffer;
    private final FloatBuffer mVertexBuffer;

    private int mPositionAttributeHandle;
    private int mColorAttributeHandle;
    private int mMVPMatrixUniformHandle;

    public ShapeBuffer() {
        mVertexData = new float[COORDS_PER_VERTEX * MAX_BUFFER_SIZE];
        // All 4 color channels are packed into a single int, so only need one int per vertex.
        mColorData = new int[MAX_BUFFER_SIZE];
        clear();

        ByteBuffer bb;

        // size = buffer size x4 values per color, x1 bytes per color.
        bb = ByteBuffer.allocateDirect(MAX_BUFFER_SIZE * COLOR_STRIDE);
        bb.order(ByteOrder.nativeOrder());
        mColorBuffer = bb.asIntBuffer();

        // size = buffer size x3 values per coord, x4 bytes per float
        bb = ByteBuffer.allocateDirect(MAX_BUFFER_SIZE * VERTEX_STRIDE);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
    }

    /**
     * Loads the shaders and looks up uniform and attribute locations.
     *
     * @return true if the shaders are successfully loaded and compiled.
     */
    public boolean loadResources() {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                getRawAsset(R.raw.untextured_vs));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                getRawAsset(R.raw.untextured_fs));

        // Create empty OpenGL Program.
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            // Add the vertex shader to program.
            GLES20.glAttachShader(program, vertexShader);
            // Add the fragment shader to program.
            GLES20.glAttachShader(program, fragmentShader);
            // Create OpenGL program executables.
            GLES20.glLinkProgram(program);
            checkGlError("glLinkProgram");

            // Get handle to vertex shader's position attribute.
            mPositionAttributeHandle = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE_NAME);
            checkGlError("glGetAttribLocation - position");

            // Get handle to vertex shader's color attribute.
            mColorAttributeHandle = GLES20.glGetAttribLocation(program, COLOR_ATTRIBUTE_NAME);
            checkGlError("glGetAttribLocation - color");

            // Get handle to shape's transformation matrix.
            mMVPMatrixUniformHandle = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM_NAME);
            checkGlError("glGetUniformLocation");

            mShaderProgram = program;
        }
        return mShaderProgram != 0;
    }

    /**
     * Returns false if initialization has failed, for example if one of the shaders
     * failed to load.
     */
    public boolean isInitialized() {
        return  mShaderProgram != 0;
    }

    /**
     * Reset the buffer to start collecting new data.
     */
    public void clear() {
        mCurrentIndex = 0;
    }

    public void add2DShape(float centerX, float centerY, Utils.Color color,
                           float[] xyPositionOffsets, float scaleX,
                           float scaleY, float headingX, float headingY) {
        // Normalize the heading vector.
        float magnitude = Utils.vector2DLength(headingX, headingY);
        if (magnitude == 0.0f) {
            headingX = 0.0f;
            headingY = 1.0f;
        } else {
            headingX /= magnitude;
            headingY /= magnitude;
        }

        final int packedABGRColor = color.getPackedABGR();
        for (int i = 0; i < xyPositionOffsets.length - 1; i += 2) {
            final float positionX = xyPositionOffsets[i + 0];
            final float positionY = xyPositionOffsets[i + 1];

            float cx = (scaleX * positionX * headingX - scaleY * positionY * headingY);
            float cy = (scaleX * positionX * headingY + scaleY * positionY * headingX);

            // Compute the x and y positions.
            final int vertexOffset = mCurrentIndex * COORDS_PER_VERTEX;
            mVertexData[vertexOffset + 0] = cx + centerX;
            mVertexData[vertexOffset + 1] = cy + centerY;
            // Always set z to 0.
            mVertexData[vertexOffset + 2] = 0.0f;

            mColorData[mCurrentIndex] = packedABGRColor;
            ++mCurrentIndex;

            // If we're on the first or last point, repeat it for stiching.
            if (i == 0) {
                stitchingHelper();
            }
        }
        stitchingHelper();
    }

    /**
     * Draws all the triangles currently in the buffer.
     *
     * @param mvpMatrix the combined model, view, and projection matrices.
     */
    public void draw(float[] mvpMatrix) {

        if (mCurrentIndex == 0) {
            // Nothing to draw.
            return;
        }

        // Load up our data:
        checkGlError("draw init");

        mVertexBuffer.clear();
        mVertexBuffer.put(mVertexData);
        mVertexBuffer.position(0);

        mColorBuffer.clear();
        mColorBuffer.put(mColorData);
        mColorBuffer.position(0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        checkGlError("draw start");

        // Add program to OpenGL environment.
        GLES20.glUseProgram(mShaderProgram);
        checkGlError("glUseProgram");

        // Enable a handle to the triangle vertices.
        GLES20.glEnableVertexAttribArray(mPositionAttributeHandle);
        // Prepare the triangle coordinate data.
        GLES20.glVertexAttribPointer(mPositionAttributeHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, VERTEX_STRIDE, mVertexBuffer);
        checkGlError("glVertexAttribPointer - vert");

        // Enable a handle to the triangle vertices.
        GLES20.glEnableVertexAttribArray(mColorAttributeHandle);
        // Prepare the color data.
        GLES20.glVertexAttribPointer(mColorAttributeHandle, COLOR_CHANNELS_PER_VERTEX,
                GLES20.GL_UNSIGNED_BYTE, true, COLOR_STRIDE, mColorBuffer);
        checkGlError("glVertexAttribPointer - color");


        // Apply the projection and view transformation.
        GLES20.glUniformMatrix4fv(mMVPMatrixUniformHandle, 1, false, mvpMatrix, 0);
        checkGlError("glUniformMatrix4fv");

        // Draw the buffers!
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mCurrentIndex);
        checkGlError("draw call");

        // Disable vertex arrays.
        GLES20.glDisableVertexAttribArray(mPositionAttributeHandle);
        checkGlError("position attrib arrays disabled");
        GLES20.glDisableVertexAttribArray(mColorAttributeHandle);
        checkGlError("vertex attrib arrays disabled");
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it.
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation Name of the OpenGL call to check.
     */
    private static void checkGlError(String glOperation) {
        if (BuildConfig.DEBUG) {
            int error = GLES20.glGetError();
            if (error != GLES20.GL_NO_ERROR) {
                throw new RuntimeException(glOperation + ": glError " + error);
            }
        }
    }

    /**
     * Loads the given raw resource into a string.
     * @param id the id of the resource to load.
     * @return the given resource as a string.
     */
    private static String getRawAsset(int id) {
        Resources res = GameView.getInstance().getResources();
        return fromStream(res.openRawResource(id));
    }

    /**
     * Parses the given input stream into a string.
     *
     * @param in an InputStream.
     * @return a string.
     */
    private static String fromStream(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }
        } catch (IOException e) {

        }
        return out.toString();
    }

    /**
     * Creates an OpenGl shader object for the given shader source code.
     */
    private static int loadShader(int type, String shaderCode) {
        // Create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER).
        int shader = GLES20.glCreateShader(type);

        // Add the source code to the shader and compile it.
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Duplicates the last point in our vertex list, to aid in stitching triangle strips.
     */
    private void stitchingHelper() {
        // Copy the x, y, and x vertex components.
        mVertexData[3 * mCurrentIndex + 0] = mVertexData[3 * mCurrentIndex + 0 - 3];
        mVertexData[3 * mCurrentIndex + 1] = mVertexData[3 * mCurrentIndex + 1 - 3];
        mVertexData[3 * mCurrentIndex + 2] = mVertexData[3 * mCurrentIndex + 2 - 3];

        // Copy the color component.
        mColorData[mCurrentIndex] = mColorData[mCurrentIndex - 1];

        ++mCurrentIndex;
    }
}
