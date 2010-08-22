
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

    
    public class Min3dRenderer implements GLWallpaperService.Renderer, ISceneController {
    	
    	min3d.core.Renderer _renderer;
    	
    	public Scene scene;
    	
    	protected Handler _initSceneHander;
    	protected Handler _updateSceneHander;
    	
    	Object3dContainer _cube;
    	
        public Min3dRenderer(Context c) {
            
    		_initSceneHander = new Handler();
    		_updateSceneHander = new Handler();

    		scene = new Scene(this);
            _renderer = new min3d.core.Renderer(scene);
            
    		Shared.context(getApplicationContext());
    		Shared.renderer(_renderer);
    		
    		scene.lights().add( new Light() );
    		_cube = new Box(1,1,1, null, true,true,false);
    		_cube.colorMaterialEnabled(false);
    		scene.addChild(_cube);
        }

    	@Override
    	public void onTouchEvent(MotionEvent e) {

    	}
        
        public void onDrawFrame(GL10 gl) {
        	_renderer.onDrawFrame(gl);
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	_renderer.onSurfaceChanged(gl, width, height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            _renderer.onSurfaceCreated(gl, config);
        }
        
    	
    	final Runnable _initSceneRunnable = new Runnable() 
    	{
            public void run() {
                onInitScene();
            }
        };
        
    	final Runnable _updateSceneRunnable = new Runnable() 
        {
            public void run() {
                onUpdateScene();
            }
        };

        public void initScene()
    	{
    	}

    	/**
    	 * All manipulation of scene and Object3D instance properties should go here.
    	 * Gets called on every frame, right before drawing.   
    	 */
    	public void updateScene()
    	{
    	}
    	
        /**
         * Called _after_ scene init (ie, after initScene).
         * Unlike initScene(), is thread-safe.
         */
        public void onInitScene()
        {
        }
        
        /**
         * Called _after_ scene init (ie, after initScene).
         * Unlike initScene(), is thread-safe.
         */
        public void onUpdateScene()
        {
        }
        
    	public Handler getInitSceneHandler()
    	{
    		return _initSceneHander;
    	}
    	
    	public Handler getUpdateSceneHandler()
    	{
    		return _updateSceneHander;
    	}

        public Runnable getInitSceneRunnable()
        {
        	return _initSceneRunnable;
        }
    	
        public Runnable getUpdateSceneRunnable()
        {
        	return _updateSceneRunnable;
        }


    }

}