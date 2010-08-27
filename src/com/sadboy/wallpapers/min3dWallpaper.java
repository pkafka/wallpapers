
package com.sadboy.wallpapers;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.core.Object3d;
import min3d.core.Object3dContainer;
import min3d.core.Renderer;
import min3d.core.Scene;
import min3d.interfaces.ISceneController;
import min3d.objectPrimitives.Box;
import min3d.objectPrimitives.Sphere;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;
import min3d.vos.LightType;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Toast;

import com.sadboy.wallpapers.physics.SensorListener;
import com.sadboy.wallpapers.physics.SimpleProjectile;
import com.sadboy.wallpapers.shapes.Cube;


public class Min3dWallpaper extends GLWallpaperService {

    public static final String SHARED_PREFS_NAME="wallpaper_settings";

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

    
    public class Min3dRenderer implements GLWallpaperService.Renderer {
    	
    	min3d.core.Renderer _renderer;
    	public Scene _scene;
    	
        double _lastDraw;
        SensorListener _sensor;

    	Object3dContainer _obj;
    	
    	//touch rotate vars
        float _downX, _downY, _previousX, _previousY;
    	
        public Min3dRenderer(Context c) {

    		Shared.context(getApplicationContext());
    		
    		_scene = new Scene();
    		_renderer = new min3d.core.Renderer(_scene);
    		
            _sensor = new SensorListener(c);
            _lastDraw = System.currentTimeMillis();
        }

    	@Override
    	public void onTouchEvent(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            switch (e.getAction()) {
    	        case MotionEvent.ACTION_MOVE:
    	            float dx = x - _previousX;
    	            float dy = y - _previousY;
    	            _obj.rotation().x += dx * Object3d.TOUCH_SCALE_FACTOR;
    	            _obj.rotation().y += dy * Object3d.TOUCH_SCALE_FACTOR;
    	        break;
    	        case MotionEvent.ACTION_DOWN:
    	    	if (_obj.position().z > -3.5){
    	    		_downX = e.getX();
    	    		_downY = e.getY();
    	    	}
    	    	break;
    	        case MotionEvent.ACTION_UP:    	
    	        	if (_obj.position().z > -3.5 && 
    	    			e.getX() < _downX + 30 &&
    	    			e.getX() > _downX - 30 &&
    	    			e.getY() < _downY + 30 &&
    	    			e.getY() > _downY - 30 &&
    	    			e.getEventTime() - e.getDownTime() < 1000){
    	        		_obj.velocity().z = -(e.getPressure() * 100);
    	        	}
    	    		break;
            }
            _previousX = x;
            _previousY = y;
    	}
        
        public void onDrawFrame(GL10 gl) {

        	double time = System.currentTimeMillis();
        	double elapsed = time - _lastDraw;
        	_lastDraw = time;
        	
        	if (elapsed > 1000)
        		elapsed = 60;
        	
        	elapsed = elapsed / 1000;
        	
        	_obj.updateLocationAndVelocity(elapsed);
        	
        	if (_obj.position().z >= -3.0 && 
        			_obj.velocity().z > 0.0){
        		_obj.applyColission();
        		if (_obj.velocity().z > -1.0 && 
        				_obj.velocity().z < 1.0){
        			_obj.stop();	
        		}
        	}
        	
        	float pitch = _sensor.getPitch();
        	_obj.position().y += (pitch * Object3d.TOUCH_SCALE_FACTOR) * 0.003;
    		
    		if (_obj.position().y > 1.5)
    			_obj.position().y = 1.5f;
    		else if (_obj.position().y < -1.5)
    			_obj.position().y = -1.5f;
    		else
        		_obj.rotation().y -= _obj.position().y;
    			
    		float roll = _sensor.getRoll();
    		_obj.position().x -= (roll * Object3d.TOUCH_SCALE_FACTOR) * 0.003;
    		
    		if (_obj.position().x > 1.5)
    			_obj.position().x = 1.5f;
    		else if (_obj.position().x < -1.5)
    			_obj.position().x = -1.5f;
    		else
    			_obj.rotation().x -= _obj.position().x;
    		
    		
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
        	_scene.lights().add( new Light() );
        	_obj = new Sphere(1.0f, 20, 15, false, false, true); 
    		_obj.colorMaterialEnabled(false);
    		_obj.position().z = -15;
    		_scene.addChild(_obj);
    	}
        
    
    }

}