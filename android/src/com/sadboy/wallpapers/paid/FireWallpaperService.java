package com.sadboy.wallpapers.paid;


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
