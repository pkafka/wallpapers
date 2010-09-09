package min3d.sampleProject1;

import java.util.Random;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Sphere;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;
import min3d.vos.LightType;
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

     final float PERIM_LEFT = -0.5f;
     final float PERIM_RIGHT = 0.5f;
     final float PERIM_TOP = 0.8f;
     final float PERIM_BOTTOM = -0.8f;
     final float PERIM_NEAR = 3;
     final float PERIM_FAR = -20;

 	Object3dContainer obj1;
	Object3dContainer _sphere;
 	
	/* Spot positioning */
	private float xSpotDir = 0.0f;
	private float ySpotDir = 0.0f;
	/* Spot animation */
	private int sensSpot = 1;
	private float radius = 0.02f;
	private float teta;
 	
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
		    	 scene.lights().get(i).direction.setX(scene.lights().get(i).direction.getX() + event.getX() * TRACKBALL_SCALE_FACTOR);
		    	 scene.lights().get(i).direction.setY(scene.lights().get(i).direction.getY() + event.getY() * TRACKBALL_SCALE_FACTOR);
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
        _light.direction.setZ(-100);
        _light.isSpotlight.set(true);
        scene.lights().add(_light);
        
        
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
    	
    	
    	//Rotation of the spot direction
		teta += 360.0f*elapsed;
		if(teta >= 2*Math.PI) {
			teta = (float)(teta%(2*Math.PI));
		}

		radius += sensSpot*1.0f*(time - _lastDraw)/5000;
		if(radius > 1.5f) {
			sensSpot = -1;
		}
		else if(radius <= 0.0f) {
			sensSpot = 1;
		}

		xSpotDir = (float)(radius*Math.cos(teta));
		ySpotDir = (float)(radius*Math.sin(teta));
		
		//_light.direction.setX(xSpotDir);
		//_light.direction.setY(ySpotDir);
		
		//_light.isSpotlight.set(true);
		//_light.isSpotlight.setDirtyFlag();
    	
		//obj1.rotation().y += 1;
		//obj1.rotation().x += 2;
		
		_count++;
		short mag = (short)(255 - (_count % 60) * (255/60));
		_lightRed.diffuse.r(mag);
	}
	

	
}


