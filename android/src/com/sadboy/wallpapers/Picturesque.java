
package com.sadboy.wallpapers;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.core.Scene;
import min3d.objectPrimitives.SkyBox;
import min3d.vos.TexEnvxVo;
import min3d.vos.TextureVo;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;

import com.sadboy.wallpapers.physics.SensorListener;


public class Picturesque extends GLWallpaperService {

    
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
    	Min3dRenderer renderer = new Min3dRenderer(getApplicationContext());
    	GLEngine engine = new GLEngine();
    	engine.setRenderer(renderer);
		engine.setRenderMode(GLEngine.RENDERMODE_CONTINUOUSLY);
		return engine;
    }

    private class Min3dRenderer implements GLWallpaperService.Renderer
    	{

        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        
    	min3d.core.Renderer _renderer;
    	public Scene _scene;
    	
    	private SensorListener _sensor;
        
		private float mPreviousX;
		private float mPreviousY;
		
	    TextureVo _cloudTexture;
    	
        public Min3dRenderer(Context c) {

    		Shared.context(getApplicationContext());

    		_sensor = new SensorListener(c);
    		
    		_scene = new Scene();
    		_renderer = new min3d.core.Renderer(_scene, null);
    		Shared.renderer(_renderer);
    		
        }
    	@Override
    	public void onTouchEvent(MotionEvent e) {
          
          float x = e.getX();
          float y = e.getY();
          
          switch (e.getAction()) {
          case MotionEvent.ACTION_MOVE:
              float dx = x - mPreviousX;
              float dy = y - mPreviousY;
              _renderer.mAngleX += dx * TOUCH_SCALE_FACTOR ;
              _renderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
              
              _scene.camera().target.x += dx * TOUCH_SCALE_FACTOR ;
              _scene.camera().target.y += dy * TOUCH_SCALE_FACTOR;
          }
          mPreviousX = x;
          mPreviousY = y;
    	}

        public void onDrawFrame(GL10 gl) {
        	//if (_sensor.matrixIsDirty())
        		//_renderer.matrix(_sensor.matrix());
        	
        	_cloudTexture.offsetU += 0.001f;
        	
        	_renderer.onDrawFrame(gl);
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	_renderer.onSurfaceChanged(gl, width, height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        	_renderer.onSurfaceCreated(gl, config);
    		initScene();
        }

        public void initScene()
        {
        	/**
        	 * Create a new Skybox. The first parameter specifies the size,
        	 * the second parameter specifies the quality (the number of
        	 * segments in each plane)
        	 */
        	SkyBox skyBox = new SkyBox(50, 10);
        	skyBox.addTexture(SkyBox.Face.North, R.drawable.img_kids, "mynorthtexture");
        	skyBox.addTexture(SkyBox.Face.East,  R.drawable.skbx_east,  "myeasttexture");
        	skyBox.addTexture(SkyBox.Face.South, R.drawable.skbx_south, "mysouthtexture");
        	skyBox.addTexture(SkyBox.Face.West,  R.drawable.skbx_west,  "mywesttexture");
        	skyBox.addTexture(SkyBox.Face.Up,    R.drawable.skbx_up,    "myuptexture");
        	skyBox.addTexture(SkyBox.Face.Down,  R.drawable.skbx_down,  "mydowntexture");
        	
        	_cloudTexture = skyBox.addSingleTexture(
        			SkyBox.Face.South, 
        			R.drawable.white_with_alpha_hole, 
        			"clouds");
        	
        	_cloudTexture.textureEnvs.get(0).param = GL10.GL_DECAL; 
    		_cloudTexture.repeatU = true; // .. this is the default, but just to be explicit
        	
        	_scene.addChild(skyBox);
        }
        
       
    }
    
}
