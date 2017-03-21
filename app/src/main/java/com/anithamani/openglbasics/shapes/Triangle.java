package com.anithamani.openglbasics.shapes;

import android.opengl.GLES20;

import com.anithamani.openglbasics.glutils.AppRenderer;
import com.anithamani.openglbasics.glutils.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Triangle as OpenGL object with vertex coloring
 */

public class Triangle {


    private float[] mVertex = new float[]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    private float[] mColor = new float[]{
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f
    };

    private FloatBuffer mVertexBuffer, mColorBuffer;

    private int mProgramHandle;


    public Triangle() {

        allocateMemory();
        createProgram();
    }

    private void allocateMemory() {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuffer = ByteBuffer.allocateDirect(mColor.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        mVertexBuffer.put(mVertex).position(0);
        mColorBuffer.put(mColor).position(0);

    }

    private void createProgram() {
        String vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPosition;" +
                        "attribute vec4 aColor;" +

                        "varying vec4 vColor;" +

                        "void main() {" +
                        "  vColor = aColor; " +
                        "  gl_Position = uMVPMatrix * aPosition;" +
                        "}";

        String fragmentShaderCode =
                "precision mediump float;" +
                        "varying vec4 vColor;" +
                        "void main() {" +
                        "  gl_FragColor = vColor;" +
                        "}";
        int vertexShader = AppRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = AppRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgramHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramHandle, vertexShader);
        GLES20.glAttachShader(mProgramHandle, fragmentShader);

        GLES20.glLinkProgram(mProgramHandle);

    }

    public void draw(float[] vpMatrix) {
        int positionHandle, colorHandle, mvpMatrixHandle;

        GLES20.glUseProgram(mProgramHandle);

        positionHandle = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, Constants.NO_OF_COORD_IN_VERTEX,
                GLES20.GL_FLOAT, false, Constants.VERTEX_STRIDE, mVertexBuffer);

        colorHandle = GLES20.glGetAttribLocation(mProgramHandle, "aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, Constants.NO_OF_COORD_IN_VERTEX,
                GLES20.GL_FLOAT, false, Constants.VERTEX_STRIDE, mColorBuffer);

        mvpMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, vpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertex.length / Constants.NO_OF_COORD_IN_VERTEX);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
