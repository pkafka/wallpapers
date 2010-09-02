
package com.sadboy.wallpapers;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.core.Object3d;
import min3d.core.Scene;
import min3d.objectPrimitives.Sphere;
import min3d.vos.Light;
import min3d.vos.Number3d;
import android.content.Context;
import android.view.MotionEvent;

import com.sadboy.wallpapers.physics.SensorListener;


public class Balls extends GLWallpaperService {

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

    	Sphere _obj1;
    	Sphere _obj2;
    	Sphere _obj3;
    	Sphere _obj4;
    	
    	Light _lightRed;
    	Light _lightGreen;
    	Light _lightBlue;
    	
    	final float BOX_SIZE = 6.0f;
    	
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
    	            _obj1.rotation().x += dx * Object3d.TOUCH_SCALE_FACTOR;
    	            _obj1.rotation().y += dy * Object3d.TOUCH_SCALE_FACTOR;
    	        break;
    	        case MotionEvent.ACTION_DOWN:
    	    	if (_obj1.position().z > -3.5){
    	    		_downX = e.getX();
    	    		_downY = e.getY();
    	    	}
    	    	break;
    	        case MotionEvent.ACTION_UP:    	
    	        	if (_obj1.position().z > -3.5 && 
    	    			e.getX() < _downX + 30 &&
    	    			e.getX() > _downX - 30 &&
    	    			e.getY() < _downY + 30 &&
    	    			e.getY() > _downY - 30 &&
    	    			e.getEventTime() - e.getDownTime() < 1000){
    	        		_obj1.velocity().z = -(e.getPressure() * 100);
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
        	
        	for (int i = 0; i < _scene.numChildren(); i++)
        		updateObject(_scene.getChildAt(i), elapsed, _sensor.getAccelerometer());
        
        	_renderer.onDrawFrame(gl);
        }
        
        public void updateObject(Object3d obj, double elapsed, Number3d accel){
        	
        	//obj.velocity().add(accel);
        	
        	obj.updateLocationAndVelocity(elapsed);
             
        	float pitch = _sensor.getPitch();
        	obj.position().y += (pitch * Object3d.TOUCH_SCALE_FACTOR) * 0.003;
    		
    		float roll = _sensor.getRoll();
    		obj.position().x -= (roll * Object3d.TOUCH_SCALE_FACTOR) * 0.003;
    		
        	checkWallCollisions(obj, elapsed);
        	
        	handleBallBallCollisions();
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
        	
        	_obj1 = new Sphere(.3f, 20, 15, false, false, true); 
    		_obj1.vertexColorsEnabled(true);
    		_obj1.position().z = -15;
    		_obj1.position().x = 0.5f;
    		_obj1.position().y = 1.0f;
    		_scene.addChild(_obj1);
    		
    		_obj2 = new Sphere(.3f, 20, 15, false, false, true); 
    		_obj2.vertexColorsEnabled(true);
    		_obj2.position().z = -5;
    		_scene.addChild(_obj2);
    		
    		_obj3 = new Sphere(.3f, 20, 15, false, false, true); 
    		_obj3.vertexColorsEnabled(true);
    		_obj3.position().z = -12;
    		_obj3.position().x = .1f;
    		_scene.addChild(_obj3);
    		
    		_obj4 = new Sphere(.3f, 20, 15, false, false, true); 
    		_obj4.vertexColorsEnabled(true);
    		_obj4.position().z = -5;
    		_obj4.position().x = 1.1f;
    		_scene.addChild(_obj4);
    	}
        
        Number3d wallDirection(Wall wall) {
        	switch (wall) {
        		case WALL_LEFT:
        			return new Number3d(-1, 0, 0);
        		case WALL_RIGHT:
        			return new Number3d(1, 0, 0);
        		case WALL_FAR:
        			return new Number3d(0, 0, -1);
        		case WALL_NEAR:
        			return new Number3d(0, 0, 1);
        		case WALL_TOP:
        			return new Number3d(0, 1, 0);
        		case WALL_BOTTOM:
        			return new Number3d(0, -1, 0);
        		default:
        			return new Number3d(0, 0, 0);
        	}
        }
        
        void checkWallCollisions(Object3d obj, double elapsed){
        	checkWallCollision(obj, Wall.WALL_BOTTOM, elapsed);
        	checkWallCollision(obj, Wall.WALL_FAR, elapsed);
        	checkWallCollision(obj, Wall.WALL_LEFT, elapsed);
        	checkWallCollision(obj, Wall.WALL_NEAR, elapsed);
        	checkWallCollision(obj, Wall.WALL_RIGHT, elapsed);
        	checkWallCollision(obj, Wall.WALL_TOP, elapsed);
        }
        void checkWallCollision(Object3d obj, Wall wall, double elapsed){
        	if (testBallWallCollision(obj, wall)){

        		//Make the ball reflect off of the wall
	        	Number3d dir = wallDirection(wall);
	            dir.normalize();
	            
	            float objDot = Number3d.dot(obj.velocity(), dir);
	            dir.multiply(objDot);
	            dir.multiply(2f);
	            obj.velocity().subtract(dir);
	            
	            Object3d.applyImpactCORSolid(obj);
        	}
        }
        
        boolean testBallWallCollision(Object3d obj, Wall wall){
        	Number3d dir = wallDirection(wall);
        	//Check whether the ball is far enough in the "dir" direction, and whether
        	//it is moving toward the wall
        	return Number3d.dot(obj.position(), dir) + 
        			obj.radius() > BOX_SIZE / 2 && Number3d.dot(obj.velocity(), dir) > 0;
        }
        
      //Returns whether two balls are colliding
        boolean testBallBallCollision(Object3d obj1, Object3d obj2) {
        	//Check whether the balls are close enough
        	float r = obj1.radius() + obj2.radius();
        	Number3d obj1Clone = Number3d.subtract(obj1.position(), obj2.position());
        	obj1Clone.subtract(obj2.position());
        	if (obj1Clone.magnitudeSquared() < r * r) {
        		//Check whether the balls are moving toward each other
        		Number3d netVelocity = Number3d.subtract(obj1.velocity(), obj2.velocity());
        		Number3d displacement = Number3d.subtract(obj1.position(), obj2.position());
        		return Number3d.dot(netVelocity, displacement) < 0;
        	}
        	else
        		return false;
        }

        //Handles all ball-ball collisions
        void handleBallBallCollisions() {

        	for (int i = 0; i < _scene.numChildren(); i++)
        		for (int j = 0; j < _scene.numChildren(); j++) {
        		
        			if (i == j)
        				continue;
        			
	        		Object3d b1 = _scene.getChildAt(i);
	        		Object3d b2 = _scene.getChildAt(j);
	        		if (testBallBallCollision(b1, b2)) {
	        			//Make the balls reflect off of each other
	        		
	        			Number3d displacement = Number3d.subtract(b1.position(), b2.position());
	        			displacement.normalize();
	        			float objDot = Number3d.dot(b1.velocity(), displacement);
	    	            displacement.multiply(objDot);
	    	            displacement.multiply(2f);
	    	            b1.velocity().subtract(displacement);
	    	            
	    	            displacement = Number3d.subtract(b1.position(), b2.position());
	        			displacement.normalize();
	        			objDot = Number3d.dot(b2.velocity(), displacement);
	    	            displacement.multiply(objDot);
	    	            displacement.multiply(2f);
	    	            b2.velocity().subtract(displacement);
	        		}
        	}
        }
    }
    
    public enum Wall {
    	WALL_LEFT, WALL_RIGHT, WALL_FAR, WALL_NEAR, 
    	WALL_TOP, WALL_BOTTOM
    }
}
