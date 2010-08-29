package com.sadboy.wallpapers;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.core.Object3dContainer;
import min3d.core.Scene;
import min3d.objectPrimitives.Box;
import min3d.objectPrimitives.Sphere;
import min3d.vos.Light;
import min3d.vos.LightType;
import min3d.vos.Number3d;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.Toast;

import com.sadboy.wallpapers.physics.SensorListener;

public class DiscoWallpaper extends GLWallpaperService {

	
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
    	DiscoRenderer renderer = new DiscoRenderer(getApplicationContext());
    	GLEngine engine = new GLEngine();
    	engine.setRenderer(renderer);
		engine.setRenderMode(GLEngine.RENDERMODE_CONTINUOUSLY);
		return engine;
    }

    
    public class DiscoRenderer implements GLWallpaperService.Renderer {
    	

    	private final float DEGREE = (float)(Math.PI/180);
    	
    	min3d.core.Renderer _renderer;
    	public Scene _scene;
    	
        double _lastDraw;
        SensorListener _sensor;
        
    	Light _lightRed;
    	Light _lightGreen;
    	Light _lightBlue;

    	Object3dContainer _sphere;
    	

    	int _count = 0;
    	
        public DiscoRenderer(Context c) {

    		Shared.context(getApplicationContext());
    		
    		_scene = new Scene();
    		_renderer = new min3d.core.Renderer(_scene);
    		
            _sensor = new SensorListener(c);
            _lastDraw = System.currentTimeMillis();
        }

    	@Override
    	public void onTouchEvent(MotionEvent e) {
          
    	}
        
        public void onDrawFrame(GL10 gl) {

        	_lightRed.position.setAll(0, +0.9f, 2.5f);
    		float x = (float)(Math.sin(_count%360 * DEGREE) * -1.5f);
    		float z = (float)(Math.cos(_count%360 * DEGREE) * 2.25f);
    		_lightGreen.position.setAll(x, 0, z);
    		_lightBlue.position.setAll(0, -0.9f, 2.5f);

    		// Red light pulses by changing diffuse red property
    		
    		short mag = (short)(255 - (_count % 60) * (255/60));
    		_lightRed.diffuse.r(mag);

    		// Green light blinks on and off using isVisible() property
    		
    		if (_count % 30 == 0) {
    			_lightGreen.isVisible(true);
    		}
    		else if (_count % 30 == 20) {
    			_lightGreen.isVisible(false);
    		}
    		
    		_count++;
    		if (_count > 360)
    			_count = 0;

    		_sphere.rotation().y = _count;
    		
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
        	_scene.camera().position.setAll(0,0,5);

    		_lightRed = new Light();
    		_lightRed.ambient.setAll(0x88110000);
    		_lightRed.diffuse.setAll(0xffff0000);
    		_lightRed.type(LightType.POSITIONAL); // looks nicer, especially with multiple lights interacting with each other
    		_scene.lights().add(_lightRed);

    		_lightGreen = new Light();
    		_lightGreen.ambient.setAll(0x88001100);
    		_lightGreen.diffuse.setAll(0xff00ff00);
    		_lightGreen.type(LightType.POSITIONAL);
    		_scene.lights().add(_lightGreen);

    		_lightBlue = new Light();
    		_lightBlue.ambient.setAll(0x88000011);
    		_lightBlue.diffuse.setAll(0xff0000ff);
    		_lightBlue.type(LightType.POSITIONAL); 
    		_scene.lights().add(_lightBlue);

    		_sphere = new Sphere(1.0f, 20, 15);
    		_sphere.vertexColorsEnabled(false);
    		_scene.addChild(_sphere);

    		_count = 0;
    	}
        

    }
}