
package com.sadboy.wallpapers;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import min3d.Shared;
import min3d.core.Object3dContainer;
import min3d.core.Renderer;
import min3d.core.Scene;
import min3d.interfaces.ISceneController;
import min3d.objectPrimitives.Box;
import min3d.objectPrimitives.Sphere;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;
import min3d.vos.LightType;

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
    	
    	private Object3dContainer objModel;
    	
        public Min3dRenderer(Context c) {

    		Shared.context(getApplicationContext());
    		
    		_scene = new Scene();
    		_renderer = new min3d.core.Renderer(_scene);
        }

    	@Override
    	public void onTouchEvent(MotionEvent e) {

    	}
        
        public void onDrawFrame(GL10 gl) {

    		objModel.rotation().x++;
    		objModel.rotation().z++;
    		
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
    		_scene.lights().add(new Light());
    		
    		IParser parser = Parser.createParser(Parser.Type.OBJ,
    				getResources(), "com.sadboy.wallpapers:raw/die_obj", false);
    		parser.parse();

    		objModel = parser.getParsedObject();
    		objModel.scale().x = objModel.scale().y = objModel.scale().z = .7f;
    		_scene.addChild(objModel);
    	}
        
    
    }

}