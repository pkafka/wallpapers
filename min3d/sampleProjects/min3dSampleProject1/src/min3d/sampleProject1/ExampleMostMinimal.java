package min3d.sampleProject1;

import java.util.Random;

import android.view.MotionEvent;

import min3d.Shared;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Box;
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
     final float PERIM_FAR = -50;

 	Box _obj1;
 	
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
        
        //scene.lights().add( new Light() );
        
        _light = new Light();
        _light.type(LightType.POSITIONAL);
        _light.position.setZ(10);
        _light.direction.setZ(-10);
        _light.isSpotlight.set(true);
        scene.lights().add(_light);
    	
    	_obj1 = new Box(2.5f, 2.5f, 2.5f); 
		_obj1.vertexColorsEnabled(false);
		_obj1.position().z += -2;
		scene.addChild(_obj1);

	}

	int count;
	@Override 
	public void updateScene() 
	{
		count++;
		
		_obj1.rotation().x = count;
		_obj1.rotation().y = count;
	}
	

	
}


