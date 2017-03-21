package com.anithamani.openglbasics.shapes;

import android.opengl.GLES20;

import com.anithamani.openglbasics.glutils.AppRenderer;
import com.anithamani.openglbasics.glutils.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Square as OpenGL object with uniform coloring
 */

public class Square {

    private FloatBuffer mVertexBuffer;
    private ShortBuffer mDrawOrderBuffer;

    private float[] mSquareCoord = new float[]{
            -0.5f, 0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f, 0.5f, 0.0f}; // top right


    private float[] mVertexColor = new float[]{1f, 1f, 0f, 1f};

    private short[] mDrawOrder = {0, 1, 2, 0, 2, 3};


    private int mProgramHandle = 0, mPositionHandle, mColorHandle, mVPHandle;


    public Square() {
        allocateMemory();
        createProgramHandle();
    }


    private void allocateMemory() {
        mVertexBuffer = ByteBuffer.allocateDirect(mSquareCoord.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(mSquareCoord).position(0);

        mDrawOrderBuffer = ByteBuffer.allocateDirect(mDrawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        mDrawOrderBuffer.put(mDrawOrder).position(0);
    }

    private void createProgramHandle() {

        String vertexCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPosition;" +

                        "void main(){" +
                        "gl_Position = uMVPMatrix * aPosition;" +
                        "}";
        String fragmentCode =
                "precision mediump float;" +
                        "uniform vec4 aColor;" +
                        "void main(){" +
                        "gl_FragColor = aColor;" +
                        "}";

        int vertexShader = AppRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = AppRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode);

        mProgramHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramHandle, vertexShader);
        GLES20.glAttachShader(mProgramHandle, fragmentShader);
        GLES20.glLinkProgram(mProgramHandle);
    }

    public void draw(float[] vpMatrix) {

        GLES20.glUseProgram(mProgramHandle);

        createAttributeHandlers();
        GLES20.glUniformMatrix4fv(mVPHandle, 1, false, vpMatrix, 0);
        GLES20.glUniform4fv(mColorHandle, 1, mVertexColor, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, mDrawOrderBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }

    private void createAttributeHandlers() {
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, Constants.NO_OF_COORD_IN_VERTEX, GLES20.GL_FLOAT, false, Constants.VERTEX_STRIDE, mVertexBuffer);

        mVPHandle = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        mColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "aColor");
    }


}
