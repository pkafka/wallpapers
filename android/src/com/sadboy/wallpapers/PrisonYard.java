
package com.sadboy.wallpapers;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.core.Object3dContainer;
import min3d.core.Scene;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;
import min3d.vos.LightType;
import min3d.vos.Number3d;
import android.content.Context;
import android.view.MotionEvent;


public class PrisonYard extends GLWallpaperService {

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
    
    private class Min3dRenderer implements GLWallpaperService.Renderer {
    	
    	min3d.core.Renderer _renderer;
    	public Scene _scene;
        double _lastDraw;

        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private final float TRACKBALL_SCALE_FACTOR = 36.0f;
        private float mPreviousX;
        private float mPreviousY;
        
        final float PERIM_LEFT = -100.5f;
        final float PERIM_RIGHT = 100.5f;
        final float PERIM_TOP = 100.8f;
        final float PERIM_BOTTOM = -100.8f;
        
    	/* Spot animation */
    	private int sensSpot = 1;
    	private float radius = 0.02f;
    	private float teta;
    	
    	Light _lightRed;
    	Light _lightBlue;
    	Light _lightMain;
     	Object3dContainer obj1;
     	int _count;
    	
        public Min3dRenderer(Context c) {
    		Shared.context(getApplicationContext());
    		_scene = new Scene();
    		_renderer = new min3d.core.Renderer(_scene);
            _lastDraw = System.currentTimeMillis();
        }

    	@Override
    	public void onTouchEvent(MotionEvent e) {
    		if (e.getAction() == MotionEvent.ACTION_UP)
    		{
    			_lightRed.isVisible(false);
    			//_lightBlue.isVisible(false);
    		}
    		else
    		{
    			_lightRed.isVisible(true);
    			//_lightBlue.isVisible(true);
    		}
    		mPreviousX = e.getX();
    		mPreviousY = e.getY();
    	}
    	
        
        public void onDrawFrame(GL10 gl) {
        	
    		double time = System.currentTimeMillis();
        	double elapsed = time - _lastDraw;
        	_lastDraw = time;
        	
        	elapsed = elapsed / 1000;
        	
        	 for (int i = 0; i < _scene.lights().size(); i++) {
        		 Light l = _scene.lights().get(i);
     			if (l.isSpotlight.get()){
     		    	 
     		    	l.updateLocation(elapsed);
     		    	checkWallCollisions(l, elapsed);
     		    	 
     		    	 l.isSpotlight.setDirtyFlag();
     		    	 l.isSpotlight.set(true);
     			}
     		}
    		
    		_count++;
    		if (_count > 1000)
    			_count = 0;
    		
    		short mag = (short)(255 - (_count % 60) * (255/60));
    		_lightRed.diffuse.r(mag);
    		//_lightBlue.diffuse.r((short) (mag / 2));
        
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
            _lastDraw = System.currentTimeMillis();
            
            _lightMain = new Light();
            _lightMain.type(LightType.POSITIONAL);
            _lightMain.position.setZ(10);
            _lightMain.direction.z = -100;
            _lightMain.isSpotlight.set(true);
            //_scene.lights().add(_lightMain);

            Random r = new Random();
            for (int i = 0; i < 3; i++){
            	Light l = new Light();
                l.type(LightType.POSITIONAL);
                l.position.setZ(10);
                l.direction.z = -100;
                l.isSpotlight.set(true);
                l.velocity.x = r.nextInt(75);
                l.velocity.y = r.nextInt(75);
                _scene.lights().add(l);
            }
            for (int i = 0; i < 3; i++){
            	Light l = new Light();
                l.type(LightType.POSITIONAL);
                l.position.setZ(10);
                l.direction.z = -100;
                l.isSpotlight.set(true);
                l.velocity.x = r.nextInt(75) * -1;
                l.velocity.y = r.nextInt(75) * -1;
                _scene.lights().add(l);
            }
            
            _lightRed = new Light();
    		_lightRed.ambient.setAll(0x88110000);
    		_lightRed.diffuse.setAll(0xffff0000);
    		_lightRed.type(LightType.POSITIONAL); 
    		_scene.lights().add(_lightRed);
    		_lightRed.isVisible(false);
    		
    		/*
    		_lightBlue = new Light();
    		_lightBlue.ambient.setAll(0x88000011);
    		_lightBlue.diffuse.setAll(0xff0000ff);
    		_lightBlue.type(LightType.POSITIONAL); 
    		_scene.lights().add(_lightBlue);
    		_lightBlue.isVisible(false);
    		*/
            
    		IParser parser = Parser.createParser(Parser.Type.OBJ,
    				getResources(), "com.sadboy.wallpapers:raw/wall_obj", true);
    		parser.parse();

    		obj1 = parser.getParsedObject();
    		obj1.scale().x = obj1.scale().y = obj1.scale().z = 2.7f;
    		obj1.position().z += -15;
    		_scene.addChild(obj1);
    	}
        
        Number3d wallDirection(Wall wall) {
        	switch (wall) {
        		case WALL_LEFT:
        			return new Number3d(-1, 0, 0);
        		case WALL_RIGHT:
        			return new Number3d(1, 0, 0);
        		case WALL_TOP:
        			return new Number3d(0, 1, 0);
        		case WALL_BOTTOM:
        			return new Number3d(0, -1, 0);
        		default:
        			return new Number3d(0, 0, 0);
        	}
        }
        
        void checkWallCollisions(Light obj, double elapsed){
        	checkWallCollision(obj, Wall.WALL_BOTTOM, elapsed);
        	checkWallCollision(obj, Wall.WALL_LEFT, elapsed);
        	checkWallCollision(obj, Wall.WALL_RIGHT, elapsed);
        	checkWallCollision(obj, Wall.WALL_TOP, elapsed);
        }
        void checkWallCollision(Light obj, Wall wall, double elapsed){
        	if (testBallWallCollision(obj, wall)){

        		//Make the ball reflect off of the wall
            	Number3d dir = wallDirection(wall);
                dir.normalize();
                
                float objDot = Number3d.dot(obj.velocity, dir);
                dir.multiply(objDot);
                dir.multiply(2f);
                obj.velocity.subtract(dir);
        	}
        }
        
        boolean testBallWallCollision(Light obj, Wall wall){
        	Number3d dir = wallDirection(wall);
        	//Check whether the ball is far enough in the "dir" direction, and whether
        	//it is moving toward the wall
        	
        	switch (wall){
        	case WALL_BOTTOM:
            	return obj.direction.y < PERIM_BOTTOM && Number3d.dot(obj.velocity, dir) > 0;
        	case WALL_TOP:
        		return obj.direction.y > PERIM_TOP && Number3d.dot(obj.velocity, dir) > 0;
        	case WALL_LEFT:
        		return obj.direction.x < PERIM_LEFT && Number3d.dot(obj.velocity, dir) > 0;
        	case WALL_RIGHT:
        		return obj.direction.x > PERIM_RIGHT && Number3d.dot(obj.velocity, dir) > 0;
        	}
        	return false;
        }

    }
    public enum Wall {
    	WALL_LEFT, WALL_RIGHT, 
    	WALL_TOP, WALL_BOTTOM
    }
}
