package com.anithamani.openglbasics.shapes;

import android.content.Context;
import android.opengl.GLES20;

import com.anithamani.openglbasics.R;
import com.anithamani.openglbasics.glutils.AppRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Sphere as OpenGL object with vertex, normal, texture coordinate manipulation and texture.
 */

public class Sphere {

    private static final String TAG = "Sphere";

    private int mProgramHandle;

    private int mPositionHandle, mTexCoordHandle, mMVPMatrixHandle, mTextureUniformHandle, mTextureDataHandle;

    private FloatBuffer mVertexBuffer, mTextureCoordBuffer;
    private ShortBuffer mIndexBuffer;

    private float[] mVertex, mNormal, mTexture;
    private short[] indices;

    public Sphere(Context context) {

        getSpherePoints();
        allocateMemory();
        createProgram();
        createHandles(context);
    }


    private void getSpherePoints() {
        float r = 0.75f;
        int ringCount = 50;

        mVertex = new float[(ringCount + 1) * (ringCount + 1) * 3];
        mNormal = new float[(ringCount + 1) * (ringCount + 1) * 3];
        mTexture = new float[(ringCount + 1) * (ringCount + 1) * 2];

        int n = 0, nTex = 0;

        for (float ring = 0f; ring <= ringCount; ring++) {
            for (float band = 0f; band <= ringCount; band++) {

                float theta = ring * (float) Math.PI / (float) ringCount;
                float phi = band * 2f * (float) Math.PI / (float) ringCount;

                mNormal[n] = (float) (Math.sin(theta) * Math.cos(phi));
                mNormal[n + 1] = (float) (Math.cos(theta));
                mNormal[n + 2] = (float) (Math.sin(theta) * Math.sin(phi));

                mVertex[n] = r * mNormal[n];
                mVertex[n + 1] = r * mNormal[n + 1];
                mVertex[n + 2] = r * mNormal[n + 2];

                mTexture[nTex++] = 1 - (band / ringCount);
                mTexture[nTex++] = 1 - (ring / ringCount);

                n += 3;
            }
        }

        List<Integer> indexData = new ArrayList<>();
        for (int i = 0; i <= ringCount; i++) {
            for (int j = 0; j <= ringCount; j++) {

                int first = ((i * (ringCount + 1)) + j);
                int second = (first + ringCount + 1);

                indexData.add(first);
                indexData.add(second);
                indexData.add(first + 1);

                indexData.add(second);
                indexData.add(second + 1);
                indexData.add(first + 1);
            }
        }

        indices = new short[indexData.size()];
        int i = 0;

        for (Integer f : indexData) {
            indices[i++] = f.shortValue(); // Or whatever default you want.
        }
    }

    private void allocateMemory() {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(mVertex).position(0);

        mTextureCoordBuffer = ByteBuffer.allocateDirect(mTexture.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureCoordBuffer.put(mTexture).position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndexBuffer.put(indices).position(0);
    }

    private void createProgram() {

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

        String fragmentShaderCode = "precision mediump float;" +

                "uniform sampler2D u_Texture;" +
                "varying vec4 v_Color;" +
                "varying vec2 v_TexCoordinate;" +

                "void main(){" +
                "gl_FragColor = (v_Color * texture2D(u_Texture, v_TexCoordinate));  " +
                "}";

        mProgramHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramHandle, AppRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode));
        GLES20.glAttachShader(
                mProgramHandle, AppRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));
        GLES20.glLinkProgram(mProgramHandle);
    }

    private void createHandles(Context context) {
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mTextureDataHandle = AppRenderer.loadTexture(context, R.drawable.sphere);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgramHandle);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(
                mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureCoordBuffer);


        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }

}
