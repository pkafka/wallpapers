
package com.sadboy.wallpapers;


public class CubeWallpaper extends GLWallpaperService {

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
    	CubeRenderer renderer = new CubeRenderer(getApplicationContext(), false);
    	GLEngine engine = new GLEngine();
    	engine.setRenderer(renderer);
		engine.setRenderMode(GLEngine.RENDERMODE_WHEN_DIRTY);
		renderer.setEngine(engine);
		return engine;
    }
    
    


}
