package min3d.sampleProject1;

import java.util.Random;

import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
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
 	Object3dContainer obj2;
 	Object3dContainer obj3;
 	
 	//Light _light;
	Light _lightRed;
 	
 	final float BOX_SIZE = 6.0f;
 	
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
             obj1.rotation().x += dx * TOUCH_SCALE_FACTOR;
             obj1.rotation().y += dy * TOUCH_SCALE_FACTOR;
         }
         mPreviousX = x;
         mPreviousY = y;
         return true;
    }
     
     @Override
    public boolean onTrackballEvent(MotionEvent event) {
    	 for (int i = 0; i < scene.lights().size(); i++) {
			if (scene.lights().get(i).isSpotlight.get()){
		    	 scene.lights().get(i).direction.setX(scene.lights().get(i).direction.getX() + event.getX() * TRACKBALL_SCALE_FACTOR);
		    	 scene.lights().get(i).direction.setY(scene.lights().get(i).direction.getY() + event.getY() * TRACKBALL_SCALE_FACTOR);
		    	 scene.lights().get(i).direction.setZ(scene.lights().get(i).direction.getZ() + event.getY() * TRACKBALL_SCALE_FACTOR);
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
       
        Light l = new Light();
        l.type(LightType.POSITIONAL);
        l.position.setZ(10);
        l.direction.setZ(-10);
        l.isSpotlight.set(true);
        scene.lights().add(l);
        
        _lightRed = new Light();
		_lightRed.ambient.setAll(0x88110000);
		_lightRed.diffuse.setAll(0xffff0000);
		_lightRed.type(LightType.POSITIONAL); 
		scene.lights().add(_lightRed);
		
		IParser parser = Parser.createParser(Parser.Type.OBJ,
				getResources(), "min3d.sampleProject1:raw/room_obj", true);
		parser.parse();

		obj1 = parser.getParsedObject();
		obj1.scale().x = obj1.scale().y = obj1.scale().z = 1.7f;
		scene.addChild(obj1);
		
	}

	@Override 
	public void updateScene() 
	{
		//obj1.rotation().y += 1;
		//obj1.rotation().x += 1;
		
		short mag = (short)(255 - (_count % 60) * (255/60));
		_lightRed.diffuse.r(mag);

		_count++;
	}
	

	
}


