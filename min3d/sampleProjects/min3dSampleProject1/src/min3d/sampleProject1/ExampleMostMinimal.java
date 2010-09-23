package min3d.sampleProject1;

import java.util.Random;

import min3d.core.Object3d;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Sphere;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;
import min3d.vos.LightType;
import min3d.vos.Number3d;
import android.view.MotionEvent;

/**
 * Most minimal example I could think of.
 * 
 * @author Lee
 */
public class ExampleMostMinimal extends RendererActivity
{
	 double _lastDraw;
     
     Random _r;
 	int _count;
     
     private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
     private final float TRACKBALL_SCALE_FACTOR = 36.0f;
     private float mPreviousX;
     private float mPreviousY;

     final float PERIM_LEFT = -150.5f;
     final float PERIM_RIGHT = 150.5f;
     final float PERIM_TOP = 150.8f;
     final float PERIM_BOTTOM = -150.8f;

 	Object3dContainer obj1;
	Object3dContainer _sphere;
 	
 	Light _light;
	Light _lightRed;
 	
 	
 	//touch rotate vars
     float _downX, _downY, _previousX, _previousY;
     
     @Override
    public boolean onTouchEvent(MotionEvent e) {
         float x = e.getX();
         float y = e.getY();
         switch (e.getAction()) {
         case MotionEvent.ACTION_MOVE:
             float dx = x - mPreviousX;
             float dy = y - mPreviousY;
             _sphere.rotation().x += dx * TOUCH_SCALE_FACTOR;
             _sphere.rotation().y += dy * TOUCH_SCALE_FACTOR;
         }
         mPreviousX = x;
         mPreviousY = y;
         _lightRed.isVisible(!_lightRed.isVisible());
         return true;
    }
     
     @Override
    public boolean onTrackballEvent(MotionEvent event) {
    	 for (int i = 0; i < scene.lights().size(); i++) {
			if (scene.lights().get(i).isSpotlight.get()){
		    	 scene.lights().get(i).direction.x = scene.lights().get(i).direction.x + event.getX() * TRACKBALL_SCALE_FACTOR;
		    	 scene.lights().get(i).direction.y = scene.lights().get(i).direction.y + event.getY() * TRACKBALL_SCALE_FACTOR;
		    	 scene.lights().get(i).isSpotlight.setDirtyFlag();
		    	 scene.lights().get(i).isSpotlight.set(true);
			}
		}
		return true;
    }
	
	public void initScene() 
	{
        _lastDraw = System.currentTimeMillis();

        
        _r = new Random();
        
        //scene.lights().add(new Light());
        
        _sphere = new Sphere(1.0f, 20, 15);
		_sphere.vertexColorsEnabled(false);
		//scene.addChild(_sphere);
       
        _light = new Light();
        _light.type(LightType.POSITIONAL);
        _light.position.setZ(10);
        _light.direction.z = -100;
        _light.isSpotlight.set(true);
        _light.velocity.x = 50f;
        _light.velocity.y = 60f;
        scene.lights().add(_light);
        
        Light l = new Light();
        l.type(LightType.POSITIONAL);
        l.position.setZ(10);
        l.direction.z = -100;
        l.isSpotlight.set(true);
        l.velocity.x = 50f;
        l.velocity.y = -40f;
        scene.lights().add(l);
        
        Light l2 = new Light();
        l2.type(LightType.POSITIONAL);
        l2.position.setZ(10);
        l2.direction.z = -100;
        l2.isSpotlight.set(true);
        l2.velocity.x = 50f;
        l2.velocity.y = 30f;
        scene.lights().add(l2);
        
        
        _lightRed = new Light();
		_lightRed.ambient.setAll(0x88110000);
		_lightRed.diffuse.setAll(0xffff0000);
		_lightRed.type(LightType.POSITIONAL); 
		scene.lights().add(_lightRed);
		_lightRed.isVisible(false);
        
		IParser parser = Parser.createParser(Parser.Type.OBJ,
				getResources(), "min3d.sampleProject1:raw/wall_obj", true);
		parser.parse();

		obj1 = parser.getParsedObject();
		obj1.scale().x = obj1.scale().y = obj1.scale().z = 1.7f;
		obj1.position().z += -5;
		scene.addChild(obj1);
		
		
		
	}

	@Override 
	public void updateScene() 
	{
		double time = System.currentTimeMillis();
    	double elapsed = time - _lastDraw;
    	_lastDraw = time;
    	
    	elapsed = elapsed / 1000;
    	
    	 for (int i = 0; i < scene.lights().size(); i++) {
    		 Light l = scene.lights().get(i);
 			if (l.isSpotlight.get()){
 		    	 
 		    	l.updateLocation(elapsed);
 		    	checkWallCollisions(l, elapsed);
 		    	 
 		    	 l.isSpotlight.setDirtyFlag();
 		    	 l.isSpotlight.set(true);
 			}
 		}
		
		_count++;
		short mag = (short)(255 - (_count % 60) * (255/60));
		_lightRed.diffuse.r(mag);
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

public enum Wall {
	WALL_LEFT, WALL_RIGHT, 
	WALL_TOP, WALL_BOTTOM
}
}

