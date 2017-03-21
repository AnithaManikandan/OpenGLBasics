package com.anithamani.openglbasics.glutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.anithamani.openglbasics.shapes.Cube;
import com.anithamani.openglbasics.shapes.Pyramid;
import com.anithamani.openglbasics.shapes.Sphere;
import com.anithamani.openglbasics.shapes.Square;
import com.anithamani.openglbasics.shapes.Texture;
import com.anithamani.openglbasics.shapes.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * <b>OpenGL SurfaceView Renderer</b> controls what is drawn within that view and is responsible for making OpenGL calls to render a frame.
 */
public class AppRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "AppRenderer";

    private Context mContext;

    private Triangle mTriangle;
    private Square mSquare;
    private Pyramid mPyramid;
    private Cube mCube;
    private Sphere mSphere;
    private Texture mTexture;

    private float[] mAngle = {1, 0, 0};

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float[] mRotationMatrixX = new float[16], mRotationMatrixY = new float[16];

    public enum SHAPES {TRIANGLE, SQUARE, PYRAMID, CUBE, SPHERE, TEXTURE}

    private SHAPES mCurrentShape = SHAPES.TRIANGLE;

    public AppRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);


        float eyeX = 0.0f, eyeY = 0.0f, eyeZ = -3f;
        float lookX = 0.0f, lookY = 0.0f, lookZ = 0.0f;
        float upX = 0.0f, upY = 1.0f, upZ = 0.0f;
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        mTriangle = new Triangle();
        mSquare = new Square();
        mPyramid = new Pyramid();
        mCube = new Cube();
        mSphere = new Sphere(mContext);
        mTexture = new Texture(mContext);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 7);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] scratch = new float[16];

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        Matrix.setRotateM(mRotationMatrixX, 0, mAngle[Constants.X_AXIS], 0, 1.0f, 0);
        Matrix.setRotateM(mRotationMatrixY, 0, mAngle[Constants.Y_AXIS], 1.0f, 0, 0);

        Matrix.multiplyMM(scratch, 0, mRotationMatrixX, 0, mRotationMatrixY, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, scratch, 0);

        if (mCurrentShape == SHAPES.TRIANGLE) {
            mTriangle.draw(scratch);
        } else if (mCurrentShape == SHAPES.SQUARE) {
            mSquare.draw(scratch);
        } else if (mCurrentShape == SHAPES.PYRAMID) {
            mPyramid.draw(scratch);
        } else if (mCurrentShape == SHAPES.CUBE) {
            mCube.draw(scratch);
        } else if (mCurrentShape == SHAPES.SPHERE) {
            mSphere.draw(scratch);
        } else if (mCurrentShape == SHAPES.TEXTURE) {
            mTexture.draw(scratch);
        }
    }


    public static int loadShader(int type, String shaderCode) {

        int shaderHandle = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shaderHandle, shaderCode);
        GLES20.glCompileShader(shaderHandle);


        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            Log.d(TAG, "Load Texture Failed \n" + "Compilation\n" + GLES20.glGetShaderInfoLog(shaderHandle));
            GLES20.glDeleteShader(shaderHandle);
            shaderHandle = 0;
        }
        return shaderHandle;
    }


    public static int loadTexture(Context context, int resourceId) {
        int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public void setAngle(float[] angle) {
        this.mAngle = angle;
    }

    public void setCurrentShape(SHAPES mCurrentShape) {
        this.mCurrentShape = mCurrentShape;
    }

}
