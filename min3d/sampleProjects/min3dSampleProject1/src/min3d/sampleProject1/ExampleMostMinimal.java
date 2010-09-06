package min3d.sampleProject1;

import java.util.Random;

import android.view.MotionEvent;

import min3d.Shared;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Box;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;
import min3d.vos.LightType;

/**
 * Most minimal example I could think of.
 * 
 * @author Lee
 */
public class ExampleMostMinimal extends RendererActivity
{
	 double _lastDraw;
     
     Random _r;
     

     final float PERIM_LEFT = -0.5f;
     final float PERIM_RIGHT = 0.5f;
     final float PERIM_TOP = 0.8f;
     final float PERIM_BOTTOM = -0.8f;
     final float PERIM_NEAR = 3;
     final float PERIM_FAR = -20;

 	Object3dContainer obj1;
 	Object3dContainer obj2;
 	Object3dContainer obj3;
 	
 	Light _light;
 	
 	final float BOX_SIZE = 6.0f;
 	
 	//touch rotate vars
     float _downX, _downY, _previousX, _previousY;
     
     @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (event.getY() > 250)
    		_light.position.setZ(_light.position.getZ() + 1);
    	else
    		_light.position.setZ(_light.position.getZ() - 1);

    	 return super.onTouchEvent(event);
    }
	
	public void initScene() 
	{
        _lastDraw = System.currentTimeMillis();
        
        _r = new Random();
        
        scene.lights().add(new Light());
       
        _light = new Light();
        _light.type(LightType.POSITIONAL);
        _light.position.setZ(10);
        _light.direction.setZ(-10);
        _light.isSpotlight.set(true);
        scene.lights().add(_light);
		
		IParser parser = Parser.createParser(Parser.Type.OBJ,
				getResources(), "min3d.sampleProject1:raw/wall_obj", true);
		parser.parse();

		obj1 = parser.getParsedObject();
		obj1.scale().x = obj1.scale().y = obj1.scale().z = 1.7f;
		scene.addChild(obj1);
		
		parser = Parser.createParser(Parser.Type.OBJ,
				getResources(), "min3d.sampleProject1:raw/wall_obj", true);
		parser.parse();
		
		obj2 = parser.getParsedObject();
		//obj2.rotation().x += 85;
		obj2.position().x = .5f;
		obj2.scale().x = obj2.scale().y = obj2.scale().z = 1.7f;
		scene.addChild(obj2);
		
		parser = Parser.createParser(Parser.Type.OBJ,
				getResources(), "min3d.sampleProject1:raw/wall_obj", true);
		parser.parse();
		
		obj3 = parser.getParsedObject();
		//obj3.rotation().x -= 185;
		obj3.position().x = .5f;
		obj3.scale().x = obj3.scale().y = obj3.scale().z = 1.7f;
		scene.addChild(obj3);
	}

	int count;
	@Override 
	public void updateScene() 
	{
		obj1.rotation().y += 1;
		obj2.rotation().y += 1;
		obj3.rotation().y += 1;
	}
	

	
}


