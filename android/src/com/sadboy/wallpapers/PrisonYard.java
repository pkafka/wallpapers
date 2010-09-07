
package com.sadboy.wallpapers;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.core.Object3d;
import min3d.core.Object3dContainer;
import min3d.core.Scene;
import min3d.objectPrimitives.Sphere;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;
import min3d.vos.Number3d;
import android.content.Context;
import android.view.MotionEvent;

import com.sadboy.wallpapers.physics.SensorListener;


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

        final float PERIM_LEFT = -0.5f;
        final float PERIM_RIGHT = 0.5f;
        final float PERIM_TOP = 0.8f;
        final float PERIM_BOTTOM = -0.8f;
        final float PERIM_NEAR = 3;
        final float PERIM_FAR = -50;
    	
    	
        public Min3dRenderer(Context c) {
    		Shared.context(getApplicationContext());
    		_scene = new Scene();
    		_renderer = new min3d.core.Renderer(_scene);
            _lastDraw = System.currentTimeMillis();
        }

    	@Override
    	public void onTouchEvent(MotionEvent e) {
           
    	}
        
        public void onDrawFrame(GL10 gl) {

        
        	_renderer.onDrawFrame(gl);
        }
        
        public void updateObject(Object3d obj, double elapsed, Number3d accel){
        
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
        	IParser parser = Parser.createParser(Parser.Type.OBJ,
    				getResources(), "com.sadboy.wallpapers:raw/room_obj", true);
    		parser.parse();

    		Object3dContainer obj1 = parser.getParsedObject();
    		obj1.scale().x = obj1.scale().y = obj1.scale().z = 1.7f;
    		_scene.addChild(obj1);
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

    }
    
    public enum Wall {
    	WALL_LEFT, WALL_RIGHT, WALL_FAR, WALL_NEAR, 
    	WALL_TOP, WALL_BOTTOM
    }
}
