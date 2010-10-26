
package com.sadboy.wallpapers;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.core.Object3d;
import min3d.core.Scene;
import min3d.objectPrimitives.Sphere;
import min3d.vos.Color4Managed;
import min3d.vos.Light;
import min3d.vos.LightType;
import min3d.vos.Number3d;
import min3d.vos.RenderType;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.MotionEvent;

import com.sadboy.wallpapers.physics.SensorListener;


public class Balls extends GLWallpaperService {

    
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

    
    private class Min3dRenderer implements GLWallpaperService.Renderer ,
    SharedPreferences.OnSharedPreferenceChangeListener {
    	
    	min3d.core.Renderer _renderer;
    	public Scene _scene;
    	
        double _lastDraw;
        SensorListener _sensor;
    	
    	Light _light;
    	
    	//Object3dContainer _room;
    	
        final float PERIM_LEFT = -0.5f;
        final float PERIM_RIGHT = 0.5f;
        final float PERIM_TOP = 0.8f;
        final float PERIM_BOTTOM = -0.8f;
        final float PERIM_NEAR = 3;
        final float PERIM_FAR = -5;
        
        private SharedPreferences _prefs;
		private int _backColor;
		private int _ballColor;
		private int _ballCount;
		private int _style;
		private int _ballSize;
		private float mPreviousX;
		private float mPreviousY;
		private int _theme;
    	
        public Min3dRenderer(Context c) {

    		Shared.context(getApplicationContext());
    		
    		_scene = new Scene();
    		_renderer = new min3d.core.Renderer(_scene);
    		
            _sensor = new SensorListener(c);
            _lastDraw = System.currentTimeMillis();
            
            _prefs = Balls.this.getSharedPreferences(BallsSettings.SHARED_PREFS_NAME, 0);
            _prefs.registerOnSharedPreferenceChangeListener(this);
        	onSharedPreferenceChanged(_prefs, null);
            loadPrefs();
        }

    	@Override
    	public void onTouchEvent(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            switch (e.getAction()) {
    	        case MotionEvent.ACTION_UP:   
    	        	x = x * .001f;
    	        	y = y * .001f;
    	        	for (int i = 0; i < _scene.numChildren(); i++){
    	        		Object3d obj = _scene.getChildAt(i);
    	        		//TODO: make pressure equal to distance from touch
    	        		
    	        		obj.velocity().z = -(e.getPressure() * 100);
    	        		obj.velocity().z = -(e.getPressure() * 100);
    	        	}
    	        	break;
    	        case MotionEvent.ACTION_MOVE:
    	        	float dx = x - mPreviousX;
    	            float dy = y - mPreviousY;
    	        	for (int i = 0; i < _scene.numChildren(); i++){
    	        		Object3d obj = _scene.getChildAt(i);
        	        	obj.rotation().x += dx * Object3d.TOUCH_SCALE_FACTOR;
        	        	obj.rotation().y += dy * Object3d.TOUCH_SCALE_FACTOR;
    	        	}
    	        	break;
            }
	        mPreviousX = x;
	        mPreviousY = y;
    	}

        public void onDrawFrame(GL10 gl) {

        	double time = System.currentTimeMillis();
        	double elapsed = time - _lastDraw;
        	_lastDraw = time;
        	
        	if (elapsed > 1000)
        		elapsed = 60;
        	
        	elapsed = elapsed / 1000;
        	
        	float pitch = _sensor.getPitch();
    		float roll = _sensor.getRoll();
        	
        	for (int i = 0; i < _scene.numChildren(); i++){
        		updateObject(
        				_scene.getChildAt(i), 
        				elapsed, 
        				_sensor.getAccelerometer(),
        				roll,
        				pitch);
        	}
    		
        	_renderer.onDrawFrame(gl);
        }
        
        public void updateObject(Object3d obj, 
        		double elapsed, 
        		Number3d accel, 
        		float roll, 
        		float pitch){
        	
        	//if (obj == _room)
        		//return;
        	
        	//obj.velocity().add(accel);
        	
            if (obj.position().z > 2){
            	obj.velocity().y += (pitch * Object3d.TOUCH_SCALE_FACTOR) * 0.003;
    			obj.velocity().x -= (roll * Object3d.TOUCH_SCALE_FACTOR) * 0.003;
            }
    		
        	obj.updateLocationAndVelocity(elapsed);
    		
        	checkWallCollisions(obj, elapsed);
        	//handleBallBallCollisions();
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
        	_scene.backgroundColor().setAll(_backColor);
        	
    		_scene.lights().add( new Light() );
    		
        	if (_theme != 2){
        		//not rainbow
        	
	        	_light = new Light();
	            _light.type(LightType.POSITIONAL);
	            _light.position.setZ(10);
	            _light.direction.z = -100;
	            _scene.lights().add(_light);
	            
	            if (_theme != 1){
	            	//not b&w
	            	_light.ambient.setAll(_ballColor);
	    			_light.diffuse.setAll(_ballColor);
	            }
            }
            
        	Random r = new Random();
            for (int i = 0; i < _ballCount; i++){
            	
            	Sphere s = new Sphere((float) (_ballSize * .001), 10, 10);
            	
            	if (_style == 1)
            		s.renderType(RenderType.POINTS);
            	else if (_style == 2)
            		s.renderType(RenderType.LINES);
            	else
            		s.renderType(RenderType.TRIANGLES);
            	
            	s.position().x = r.nextFloat();
            	s.position().y = r.nextFloat();
        		s.position().z = r.nextInt(15) * -1;
        		
        		if (_theme == 2){
        			s.colorMaterialEnabled(true);
        			s.vertexColorsEnabled(true);
        		}
        		else{
        			s.vertexColorsEnabled(false);
        		}
        		
        		_scene.addChild(s);
            }
    
            /*
    		IParser parser = Parser.createParser(Parser.Type.OBJ,
    				getResources(), "com.sadboy.wallpapers:raw/room_obj", true);
    		parser.parse();

    		_room = parser.getParsedObject();
    		_room.scale().x = _room.scale().y = _room.scale().z = 1.7f;
    		_scene.addChild(_room);
    		*/
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
        
        boolean testBallWallCollision(Object3d obj, Wall wall){
        	Number3d dir = wallDirection(wall);
        	//Check whether the ball is far enough in the "dir" direction, and whether
        	//it is moving toward the wall
        	
        	switch (wall){
        	case WALL_BOTTOM:
            	return obj.position().y < PERIM_BOTTOM && Number3d.dot(obj.velocity(), dir) > 0;
        	case WALL_TOP:
        		return obj.position().y > PERIM_TOP && Number3d.dot(obj.velocity(), dir) > 0;
        	case WALL_LEFT:
        		return obj.position().x < PERIM_LEFT && Number3d.dot(obj.velocity(), dir) > 0;
        	case WALL_RIGHT:
        		return obj.position().x > PERIM_RIGHT && Number3d.dot(obj.velocity(), dir) > 0;
        	case WALL_NEAR:
        		return obj.position().z > PERIM_NEAR && Number3d.dot(obj.velocity(), dir) > 0;
        	case WALL_FAR:
        		return obj.position().z < PERIM_FAR && Number3d.dot(obj.velocity(), dir) > 0;
        	}
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
	        		
	        		//if (b1 == _room || b2 == _room)
	        			//continue;
	        		
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

        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            loadPrefs();
         }
         
         private void loadPrefs(){
             _backColor = _prefs.getInt(BallsSettings.SHARED_PREFS_BACK_COLOR, Color.BLACK);
             _ballColor = _prefs.getInt(BallsSettings.SHARED_PREFS_BALL_COLOR, Color.WHITE);
             _ballCount = _prefs.getInt(BallsSettings.SHARED_PREFS_COUNT, 25);
             _ballSize = _prefs.getInt(BallsSettings.SHARED_PREFS_SIZE, 150);
             _ballSize = _prefs.getInt(BallsSettings.SHARED_PREFS_SIZE, 150);
             
             try{
            	 _style = Integer.parseInt(_prefs.getString(BallsSettings.SHARED_PREFS_STYLE, "0"));
             }
             catch(Exception ex){ _style = 0;}
             try{
            	 _theme = Integer.parseInt(_prefs.getString(BallsSettings.SHARED_PREFS_THEME, "0"));
             }
             catch(Exception ex){ _style = 0;}
         }
    }
    
    public enum Wall {
    	WALL_LEFT, WALL_RIGHT, WALL_FAR, WALL_NEAR, 
    	WALL_TOP, WALL_BOTTOM
    }
}
