/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sadboy.wallpapers.test;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.sadboy.wallpapers.physics.SimpleProjectile;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.text.format.Time;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Wrapper activity demonstrating the use of {@link GLSurfaceView}, a view
 * that uses OpenGL drawing into a dedicated surface.
 *
 * Shows:
 * + How to redraw in response to user input.
 */
public class TouchRotateActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create our Preview view and set it as the content of our
        // Activity
        mGLSurfaceView = new TouchSurfaceView(this);
        setContentView(mGLSurfaceView);
        mGLSurfaceView.requestFocus();
        mGLSurfaceView.setFocusableInTouchMode(true);
    }

    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
    }

    private GLSurfaceView mGLSurfaceView;
}

/**
 * Implement a simple rotation control.
 *
 */
class TouchSurfaceView extends GLSurfaceView {


    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TRACKBALL_SCALE_FACTOR = 36.0f;
    private CubeRenderer mRenderer;
    private float mPreviousX;
    private float mPreviousY;
    
    
    public TouchSurfaceView(Context context) {
        super(context);
        mRenderer = new CubeRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override public boolean onTrackballEvent(MotionEvent e) {
        mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
        mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
        requestRender();
        return true;
    }

    @Override public boolean onTouchEvent(MotionEvent e) {
    	float p = e.getPressure();
    	//Toast.makeText(getContext(), Float.toString(p), Toast.LENGTH_SHORT).show();
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dx = x - mPreviousX;
            float dy = y - mPreviousY;
            mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
            mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
            requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }


    /**
     * Render a cube.
     */
    private class CubeRenderer implements GLSurfaceView.Renderer {
    	

        private Cube mCube;
        public float mAngleX;
        public float mAngleY;
        
        SimpleProjectile mProjectile;
        double mLastDraw;
        
        public CubeRenderer() {
            mCube = new Cube();
            mLastDraw = System.currentTimeMillis();
            mProjectile = new SimpleProjectile(
        		   0.0, 0.0, 0.0, 0.0, 35.0, 0.0, mLastDraw);
        }
        
        public void onDrawFrame(GL10 gl) {

        	double time = System.currentTimeMillis();
        	mProjectile.updateLocationAndVelocity(time - mLastDraw);
        	mLastDraw = time;
        	
        	double z = mProjectile.getZ();
        	if (mProjectile.getZ() >= 0.0)
        	{
        		String b = "Break";
        	}

            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    	    
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -3.0f);
            //gl.glTranslatef(0, 0, (float)mProjectile.getZ());
            //gl.glRotatef(mAngleX, 0, 1, 0);
            //gl.glRotatef(mAngleY, 1, 0, 0);

            mCube.draw(gl);
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
             _width = width;
     	    _height = height;

     	    gl.glMatrixMode(GL10.GL_PROJECTION);
    	    float ratio = _width / _height;
    	    GLU.gluPerspective(gl, 45.0f, ratio, 1.0f, 20.0f); 
        }

        

        private float _width = 320f;
        private float _height = 480f;
        
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        	 gl.glMatrixMode(GL10.GL_PROJECTION);
     	    float ratio = _width / _height;
     	    GLU.gluPerspective(gl, 45.0f, ratio, 1.0f, 20.0f); 
    	    
    	    gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                    GL10.GL_FASTEST);
    	    gl.glEnable(GL10.GL_DEPTH_TEST);
    	 
    	    // clipping wall color
    	    gl.glClearColor(0f, 0.5f, 0f, 1.0f);
    	 
    	    gl.glEnable(GL10.GL_CULL_FACE);
    	    gl.glFrontFace(GL10.GL_CCW);
    	    gl.glCullFace(GL10.GL_FRONT);
    	    gl.glShadeModel(GL10.GL_SMOOTH);
        }
    }

    

}



