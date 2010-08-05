

package com.sadboy.wallpapers.test;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.sadboy.wallpapers.physics.SensorListener;
import com.sadboy.wallpapers.physics.SimpleProjectile;


public class TouchRotateActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

class TouchSurfaceView extends GLSurfaceView {


    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TRACKBALL_SCALE_FACTOR = 36.0f;
    private CubeRenderer mRenderer;
    
    public TouchSurfaceView(Context context) {
        super(context);
        mRenderer = new CubeRenderer(context);
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
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dx = x - mRenderer.mPreviousX;
            float dy = y - mRenderer.mPreviousY;
            mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
            mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
        }
        mRenderer.mPreviousX = x;
        mRenderer.mPreviousY = y;
        return true;
    }

    private class CubeRenderer implements GLSurfaceView.Renderer {
    	
        private Cube mCube;
        public float mAngleX;
        public float mAngleY;
        private float mPreviousX;
        private float mPreviousY;
        
        SimpleProjectile mProjectile;
        double mLastDraw;
        SensorListener mSensor;
        
        public CubeRenderer(Context c) {
            mCube = new Cube();
            mSensor = new SensorListener(c);
            mLastDraw = System.currentTimeMillis();
            mProjectile = new SimpleProjectile(
        		   0.0, 0.0, -50.0, 0.0, 0.0, 0.0, mLastDraw);
        }
        
        public void touch(MotionEvent e){
        	if (mProjectile.getZ() > -3.5 && 
        			e.getAction() == MotionEvent.ACTION_DOWN){
        		mProjectile.applyVelocity(0.0, 0.0, -15.0);
        	}
        }
        
        float mX;
        float mY;
        public void onDrawFrame(GL10 gl) {

        	double time = System.currentTimeMillis();
        	double elapsed = time - mLastDraw;
        	mLastDraw = time;
        	
        	mProjectile.updateLocationAndVelocity(elapsed / 1000);
        	
        	if (mProjectile.getZ() >= -3.0 && 
        			mProjectile.getVz() > 0.0){
        		mProjectile.applyColission();
        		if (mProjectile.getVz() > -1.0 && 
        				mProjectile.getVz() < 1.0){
        			mProjectile.stop();	
        		}
        	}
        	
    		float pitch = mSensor.getPitch();
    		mY += (pitch * TOUCH_SCALE_FACTOR) * 0.003;
    		mAngleY -= mY;
    		
    		if (mY > 1.5)
    			mY = 1.5f;
    		else if (mY < -1.5)
    			mY = -1.5f;
    		
    		
    		float roll = mSensor.getRoll();
    		mX -= (roll * TOUCH_SCALE_FACTOR) * 0.003;
    		mAngleX -= mX;
    		
    		if (mX > 1.5)
    			mX = 1.5f;
    		else if (mX < -1.5)
    			mX = -1.5f;
	    		
        	
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(mX, mY, (float)mProjectile.getZ());
            
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
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100);
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

    class Gest extends SimpleOnGestureListener{
    	@Override
    	public boolean onSingleTapUp(MotionEvent e) {
    		// TODO Auto-generated method stub
    		return super.onSingleTapUp(e);
    	}
    }

}



