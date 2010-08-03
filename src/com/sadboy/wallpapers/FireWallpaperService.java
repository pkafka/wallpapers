package com.sadboy.wallpapers;


public class FireWallpaperService extends GLWallpaperService {

	private FireRenderer renderer;
	
	@Override
	public Engine onCreateEngine() {
		renderer = new FireRenderer();
		return new GLEngine() {
			{
				setRenderer(renderer);
				setRenderMode(RENDERMODE_CONTINUOUSLY);
			}
		};
	}

}
