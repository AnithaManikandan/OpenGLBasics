package com.anithamani.openglbasics.shapes;

import android.content.Context;
import android.opengl.GLES20;

import com.anithamani.openglbasics.R;
import com.anithamani.openglbasics.glutils.AppRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Textured Cube
 */

public class Texture {

    private float mVertex[] = {
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,

            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f
    };

    private float mTexture[] = {
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f
    };

    private byte mIndices[] = {
            0, 1, 2, 2, 1, 3, //front face
            4, 5, 6, 6, 5, 7, //rear face
            1, 5, 3, 3, 5, 7, //Left face
            0, 4, 2, 2, 4, 6, //right face
            4, 5, 0, 0, 5, 1, //Bottom face
            3, 7, 2, 2, 7, 6 //top face
    };


    private FloatBuffer mVertexBuffer, mCubeTextureCoordBuffer;
    private ByteBuffer mIndexBuffer;
    private int mProgram;
    private int mPositionHandle, mTexCoordHandle;
    private int mMVPMatrixHandle, mTextureUniformHandle;
    private int mTextureDataHandle;

    public Texture(Context context) {

        allocateMemory();
        createProgram(context);
    }

    private void allocateMemory() {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(mVertex).position(0);

        mCubeTextureCoordBuffer = ByteBuffer.allocateDirect(mTexture.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordBuffer.put(mTexture).position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(mIndices.length);
        mIndexBuffer.put(mIndices).position(0);
    }

    private void createProgram(Context context) {
        String vertexShaderCode =
                "uniform mat4 u_MVPMatrix;" +
                        "attribute vec4 a_Position;" +
                        "attribute vec2 a_TexCoordinate;" +

                        "varying vec4 v_Color;" +
                        "varying vec2 v_TexCoordinate;" +

                        "void main(){" +
                        "v_Color = vec4(1,1,1,1);" +
                        "v_TexCoordinate = a_TexCoordinate;" +
                        "gl_Position = u_MVPMatrix * a_Position;" +
                        "} ";


        String fragmentShaderCode =
                "precision mediump float;" +

                        "uniform sampler2D u_Texture;" +
                        "varying vec4 v_Color;" +
                        "varying vec2 v_TexCoordinate;" +

                        "void main(){" +
                        "gl_FragColor = (v_Color * texture2D(u_Texture, v_TexCoordinate));  " +
                        "}";

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, AppRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode));
        GLES20.glAttachShader(
                mProgram, AppRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));
        GLES20.glLinkProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureDataHandle = AppRenderer.loadTexture(context, R.drawable.cube);

    }

    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(
                mTexCoordHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mCubeTextureCoordBuffer);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, mIndices.length, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}
