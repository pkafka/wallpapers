

package com.sadboy.wallpapers.test;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.sadboy.wallpapers.physics.SimpleProjectile;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

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
    	mRenderer.touch(e);
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
        		   0.0, 0.0, -50.0, 0.0, 0.0, 0.0, mLastDraw);
        }
        
        public void touch(MotionEvent e){
        	if (mProjectile.getZ() > -4.0 && 
        			e.getAction() == MotionEvent.ACTION_DOWN){
        		mProjectile.applyVelocity(0.0, 0.0, -10.0);
        	}
        }
        
        public void onDrawFrame(GL10 gl) {

        	double time = System.currentTimeMillis();
        	double elapsed = time - mLastDraw;
        	mLastDraw = time;
        	
        	mProjectile.updateLocationAndVelocity(elapsed / 1000);
        	
        	if (mProjectile.getZ() >= -3.0){
        		mProjectile.applyColission();
        		if (mProjectile.getVz() < -1.0)
        			mProjectile.stop();
        	}
        	
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, (float)mProjectile.getZ());
            gl.glRotatef(mAngleX, 0, 1, 0);
            gl.glRotatef(mAngleY, 1, 0, 0);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            mCube.draw(gl);
           
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	gl.glViewport(0, 0, width, height);
        	
    	    float ratio = (float) width / height;
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 50);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
             gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                     GL10.GL_FASTEST);

             gl.glClearColor(1,1,1,1);
             gl.glEnable(GL10.GL_CULL_FACE);
             gl.glShadeModel(GL10.GL_SMOOTH);
             gl.glEnable(GL10.GL_DEPTH_TEST);
        }
    }

    

}



