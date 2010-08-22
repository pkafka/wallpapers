package com.sadboy.wallpapers;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.view.MotionEvent;

import com.sadboy.wallpapers.physics.SensorListener;
import com.sadboy.wallpapers.physics.SimpleProjectile;
import com.sadboy.wallpapers.shapes.Cube;


public class CubeRenderer implements GLWallpaperService.Renderer {
	
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TRACKBALL_SCALE_FACTOR = 36.0f;
    
	private Cube mCube;
    public float mAngleX;
    public float mAngleY;
    private float mPreviousX;
    private float mPreviousY;
    
    SimpleProjectile mProjectile;
    double mLastDraw;
    SensorListener mSensor;
    
    float mDownX, mDownY;
    
    public CubeRenderer(Context c) {
        mCube = new Cube();
        mSensor = new SensorListener(c);
        mLastDraw = System.currentTimeMillis();
        mProjectile = new SimpleProjectile(
    		   0.0, 0.0, -10.0, 0.0, 0.0, 0.0, mLastDraw);
    }

	@Override
	public void onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
	        case MotionEvent.ACTION_MOVE:
	            float dx = x - mPreviousX;
	            float dy = y - mPreviousY;
	            mAngleX += dx * TOUCH_SCALE_FACTOR;
	            mAngleY += dy * TOUCH_SCALE_FACTOR;
	        break;
	        case MotionEvent.ACTION_DOWN:
	    	if (mProjectile.getZ() > -3.5){
	    		mDownX = e.getX();
	    		mDownY = e.getY();
	    	}
	    	break;
	        case MotionEvent.ACTION_UP:    	
	        	if (mProjectile.getZ() > -3.5 && 
	    			e.getX() < mDownX + 30 &&
	    			e.getX() > mDownX - 30 &&
	    			e.getY() < mDownY + 30 &&
	    			e.getY() > mDownY - 30 &&
	    			e.getEventTime() - e.getDownTime() < 1000){
	        		mProjectile.applyVelocity(0.0, 0.0, -(e.getPressure() * 100));
	        	}
	    		break;
        }
        mPreviousX = x;
        mPreviousY = y;
	}
    
    public void onDrawFrame(GL10 gl) {

    	double time = System.currentTimeMillis();
    	double elapsed = time - mLastDraw;
    	mLastDraw = time;
    	
    	if (elapsed > 1000)
    		elapsed = 60;
    	
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
		double y = mProjectile.getY();
		y += (pitch * TOUCH_SCALE_FACTOR) * 0.003;
		
		if (y > 1.5)
			y = 1.5f;
		else if (y < -1.5)
			y = -1.5f;
		else
    		mAngleY -= y;
			
		mProjectile.setY(y);
		
		
		float roll = mSensor.getRoll();
		double x = mProjectile.getX();
		x -= (roll * TOUCH_SCALE_FACTOR) * 0.003;
		
		if (x > 1.5)
			x = 1.5f;
		else if (x < -1.5)
			x = -1.5f;
		else
    		mAngleX += x;
		
		mProjectile.setX(x);
    		
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef((float)mProjectile.getX(), 
        		(float)mProjectile.getY(), 
        		+(float)mProjectile.getZ());
        
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

         gl.glClearColor(0,0,0,0);
         gl.glEnable(GL10.GL_CULL_FACE);
         gl.glShadeModel(GL10.GL_SMOOTH);
         gl.glEnable(GL10.GL_DEPTH_TEST);
    }


}


