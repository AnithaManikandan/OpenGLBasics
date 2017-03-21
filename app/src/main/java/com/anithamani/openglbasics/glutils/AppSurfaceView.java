package com.anithamani.openglbasics.glutils;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

/**
 * <b>GL SurfaceView</b> is a view container for graphics drawn with OpenGL.
 */

public class AppSurfaceView extends GLSurfaceView {


    private float TOUCH_SCALE_FACTOR = 180.0f / 320;

    private final AppRenderer mRenderer;
    private float mPreviousX;
    private float mPreviousY;
    private float[] mAngle = new float[3];

    public AppSurfaceView(Context context) {
        this(context, null);
    }

    public AppSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        TOUCH_SCALE_FACTOR = displayMetrics.density;


        mRenderer = new AppRenderer(getContext());

        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX(), y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (mRenderer != null) {
                        float deltaX = (x - mPreviousX) / TOUCH_SCALE_FACTOR / 2f;
                        float deltaY = (y - mPreviousY) / TOUCH_SCALE_FACTOR / 2f;

                        mAngle[Constants.X_AXIS] += deltaX;
                        mAngle[Constants.Y_AXIS] += deltaY;
                    }
                }
                mRenderer.setAngle(mAngle);

                requestRender();
        }


        mPreviousX = x;
        mPreviousY = y;

        return true;
    }

    public AppRenderer getRenderer() {
        return mRenderer;
    }


}
