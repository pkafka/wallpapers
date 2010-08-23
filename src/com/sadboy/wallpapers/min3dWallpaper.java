
package com.sadboy.wallpapers;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.core.Object3dContainer;
import min3d.core.Renderer;
import min3d.core.Scene;
import min3d.interfaces.ISceneController;
import min3d.objectPrimitives.Box;
import min3d.vos.Light;

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
    	
    	Object3dContainer _cube;
    	
        public Min3dRenderer(Context c) {

    		Shared.context(getApplicationContext());
    		
    		_scene = new Scene();
    		_scene.lights().add( new Light() );

    		_renderer = new min3d.core.Renderer(_scene);
    		
    		_cube = new Box(1,1,1, null, true,true,false);
    		_cube.colorMaterialEnabled(false);
    		_scene.addChild(_cube);
        }

    	@Override
    	public void onTouchEvent(MotionEvent e) {

    	}
        
        public void onDrawFrame(GL10 gl) {
        	//update scene first then call renderer ondrawframe
        	//TODO: update
        	try{
        	_renderer.onDrawFrame(gl);
    		}
    		catch(Exception ex)
    		{
    			Toast.makeText(Shared.context(), ex.getMessage() + "\n" +  ex.getStackTrace()[1], Toast.LENGTH_LONG).show();
    		}
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	try{
        	_renderer.onSurfaceChanged(gl, width, height);
    		}
    		catch(Exception ex)
    		{
    			Toast.makeText(Shared.context(), ex.getMessage() + "\n" +  ex.getStackTrace()[1], Toast.LENGTH_LONG).show();
    		}
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        	try{
        	_renderer.onSurfaceCreated(gl, config);
    		}
    		catch(Exception ex)
    		{
    			Toast.makeText(Shared.context(), ex.getMessage() + "\n" +  ex.getStackTrace()[1], Toast.LENGTH_LONG).show();
    		}
        }
        
    
    }

}