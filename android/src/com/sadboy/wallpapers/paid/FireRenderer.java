package com.sadboy.wallpapers.paid;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class FireRenderer implements GLSurfaceView.Renderer, GLWallpaperService.Renderer {

	private Fire fire;
	
	private int surfaceWidth;
	
	private int surfaceHeight;
	
	public FireRenderer() {
		fire = new Fire();
	}
	
	@Override
	public void onDrawFrame(GL10 gl10) {
		fire.performFrame((GL11) gl10, surfaceWidth, surfaceHeight);
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		fire.initialize(width, height, (GL11) gl10);
		
		surfaceWidth = width;
		surfaceHeight = height;
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglconfig) {
		if (! (gl10 instanceof GL11Ext)) {
			throw new RuntimeException("GL11Ext not supported");
		}
	}

	@Override
	public void onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

}
