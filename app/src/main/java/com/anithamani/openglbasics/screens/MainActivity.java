package com.anithamani.openglbasics.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.anithamani.openglbasics.R;
import com.anithamani.openglbasics.glutils.AppRenderer;
import com.anithamani.openglbasics.glutils.AppSurfaceView;


public class MainActivity extends AppCompatActivity {

    private AppSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGLSurfaceView = new AppSurfaceView(this);
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.triangle:
                mGLSurfaceView.getRenderer().setCurrentShape(AppRenderer.SHAPES.TRIANGLE);
                mGLSurfaceView.requestRender();
                return true;
            case R.id.square:
                mGLSurfaceView.getRenderer().setCurrentShape(AppRenderer.SHAPES.SQUARE);
                mGLSurfaceView.requestRender();
                return true;
            case R.id.pyramid:
                mGLSurfaceView.getRenderer().setCurrentShape(AppRenderer.SHAPES.PYRAMID);
                mGLSurfaceView.requestRender();
                return true;
            case R.id.cube:
                mGLSurfaceView.getRenderer().setCurrentShape(AppRenderer.SHAPES.CUBE);
                mGLSurfaceView.requestRender();
                return true;
            case R.id.sphere:
                mGLSurfaceView.getRenderer().setCurrentShape(AppRenderer.SHAPES.SPHERE);
                mGLSurfaceView.requestRender();
                return true;
            case R.id.texture:
                mGLSurfaceView.getRenderer().setCurrentShape(AppRenderer.SHAPES.TEXTURE);
                mGLSurfaceView.requestRender();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
