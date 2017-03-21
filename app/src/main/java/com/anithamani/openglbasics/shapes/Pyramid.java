package com.anithamani.openglbasics.shapes;

import android.opengl.GLES20;

import com.anithamani.openglbasics.glutils.AppRenderer;
import com.anithamani.openglbasics.glutils.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Pyramid as OpenGL object with face coloring.
 */

public class Pyramid {

    private FloatBuffer mVertexBuffer;


    private float[] mVertex = {
            0.0f, 0.622008459f, 0.0f,
            -0.5f, -0.311004243f, 0.0f,
            0.5f, -0.311004243f, 0.0f,
            0.0f, 0.0f, 0.622008459f};

    private short[][] mDrawOrder = {{1, 0, 2}, {2, 0, 3}, {3, 0, 1}, {1, 2, 3}};

    private float[][] mVertexColors = {
            {1.0f, 0.5f, 0.0f, 1.0f},  // 0. orange
            {1.0f, 0.0f, 1.0f, 1.0f},  // 1. violet
            {0.0f, 1.0f, 0.0f, 1.0f},  // 2. green
            {0.0f, 0.0f, 1.0f, 1.0f}   // 3. blue
    };


    private int mProgramHandle;

    public Pyramid() {
        allocateMemory();
        createProgramHandle();
    }

    private void allocateMemory() {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(mVertex).position(0);
    }

    private void createProgramHandle() {

        String vertexCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPosition;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * aPosition;" +
                        "}";

        String fragmentCode =
                "precision mediump float;" +
                        "uniform vec4 uColor;" +
                        "void main() {" +
                        "  gl_FragColor = uColor;" +
                        "}";


        int vertexShader = AppRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = AppRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode);

        mProgramHandle = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgramHandle, vertexShader);
        GLES20.glAttachShader(mProgramHandle, fragmentShader);
        GLES20.glLinkProgram(mProgramHandle);
    }

    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgramHandle);
        int positionHandle, colorHandle;

        positionHandle = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, Constants.NO_OF_COORD_IN_VERTEX, GLES20.GL_FLOAT, false, Constants.VERTEX_STRIDE, mVertexBuffer);

        for (int face = 0; face < 4; face++) {
            colorHandle = GLES20.glGetUniformLocation(mProgramHandle, "uColor");
            GLES20.glUniform4fv(colorHandle, 1, mVertexColors[face], 0);

            int mVPHandle = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
            GLES20.glUniformMatrix4fv(mVPHandle, 1, false, mvpMatrix, 0);

            ShortBuffer drawOrderBuffer = ByteBuffer.allocateDirect(mDrawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
            drawOrderBuffer.put(mDrawOrder[face]).position(0);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mDrawOrder[face].length, GLES20.GL_UNSIGNED_SHORT,
                    drawOrderBuffer);

        }

        GLES20.glDisableVertexAttribArray(positionHandle);

    }

}
