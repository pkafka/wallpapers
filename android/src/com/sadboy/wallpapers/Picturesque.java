
package com.sadboy.wallpapers;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.core.Scene;
import min3d.objectPrimitives.SkyBox;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;


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

    private class Min3dRenderer implements GLWallpaperService.Renderer,
    	SensorEventListener{

        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        
    	min3d.core.Renderer _renderer;
    	public Scene _scene;
    	
    	private SensorManager _sensor;
        
    	final int matrix_size = 16;
        float[] Rf = new float[matrix_size];
        float[] If = new float[matrix_size];   
        float[] outR = new float[matrix_size];
        float[] valuesAccel = new float[3];
        float[] valuesMag = new float[3];	
        float[] values = new float[3];
		private float mPreviousX;
		private float mPreviousY;
    	
        public Min3dRenderer(Context c) {

    		Shared.context(getApplicationContext());
    		
    		_scene = new Scene();
    		_renderer = new min3d.core.Renderer(_scene, null);
    		Shared.renderer(_renderer);
            
            registerListeners();
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
          }
          mPreviousX = x;
          mPreviousY = y;
          
    	}

        public void onDrawFrame(GL10 gl) {
        	_renderer.onDrawFrame(gl);
        }
        
    	private void registerListeners(){
    		_sensor = (SensorManager) getSystemService(SENSOR_SERVICE);
            List<Sensor> sl = _sensor.getSensorList(Sensor.TYPE_ALL);
            
            //TODO:only wire up needed sensors
            for (int i = 0; i < sl.size(); i++) {
            	if (sl.get(i).getType() == Sensor.TYPE_ORIENTATION ||
            			sl.get(i).getType() == Sensor.TYPE_MAGNETIC_FIELD ||
            			sl.get(i).getType() == Sensor.TYPE_ACCELEROMETER){
    	        	_sensor.registerListener(Min3dRenderer.this,
    	                    sl.get(i),
    	                    SensorManager.SENSOR_DELAY_GAME);
            	}
    		}
    	}
    	
    	 public void remapAndSetMatrix(){
    	    	SensorManager.getOrientation(Rf, values);
    	    	_renderer.matrix(Rf.clone());
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

        	skyBox.addTexture(SkyBox.Face.North, R.drawable.skbx_north, "mynorthtexture");
        	skyBox.addTexture(SkyBox.Face.East,  R.drawable.skbx_east,  "myeasttexture");
        	skyBox.addTexture(SkyBox.Face.South, R.drawable.skbx_south, "mysouthtexture");
        	skyBox.addTexture(SkyBox.Face.West,  R.drawable.skbx_west,  "mywesttexture");
        	skyBox.addTexture(SkyBox.Face.Up,    R.drawable.skbx_up,    "myuptexture");
        	skyBox.addTexture(SkyBox.Face.Down,  R.drawable.skbx_down,  "mydowntexture");

        	_scene.addChild(skyBox);
        }

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			Sensor s = event.sensor;
			
			switch (s.getType()) {
	        case Sensor.TYPE_MAGNETIC_FIELD:
	        	valuesMag = event.values.clone();
	            break;
	        case Sensor.TYPE_ACCELEROMETER:
	        	valuesAccel = event.values.clone();
	        	break;
	        case Sensor.TYPE_ORIENTATION:
	        	if (SensorManager.getRotationMatrix(Rf, If, valuesAccel, valuesMag)){
	        		remapAndSetMatrix();
	 			}
	        	break;
			}
		}
        
       
    }
    
}