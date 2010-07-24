

package com.sadboy.wallpapers;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.opengl.Visibility;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;


public class FingerPaint extends WallpaperService {

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
        return new PaintEngine();
    }

    class PaintEngine extends Engine {
    	
        private MaskFilter  mEmboss;
        private MaskFilter  mBlur;
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        private float mX, mY, mXOffset = 0, mYOffset = 0;

        private final Paint mPaint = new Paint();
        
        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        private static final float TOUCH_TOLERANCE = 4;
        
        private boolean mVisible;
        final Random mRandom = new Random();

        PaintEngine() {
        	
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(12);
            mPaint.setMaskFilter(mEmboss);
            
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
           
            mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                                           0.4f, 6, 3.5f);

            mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
            
            mPaint.setXfermode(null);
            mPaint.setAlpha(0xFF);
            mPaint.setMaskFilter(mEmboss);
        }
        
        private void setCanvasAndBitmat(Bitmap toCopy, int width, int height){
        	int toSet = 0;
        	if (width >= height)
        		toSet = width;
        	else
        		toSet = height;
        	
        	if (toCopy != null)
        		mBitmap = Bitmap.createBitmap(toCopy, 0, 0, toSet, toSet);
        	else
        		mBitmap = Bitmap.createBitmap(toSet, toSet, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            drawFrame();
        }

        int mHeight;
        int mWidth;
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            if (mHeight != height || mWidth != width)
            {
                //Toast.makeText(getApplicationContext(), "changed:" + width + " " + height, Toast.LENGTH_SHORT).show();
            	mHeight = height;
            	mWidth = width;
            	setCanvasAndBitmat(mBitmap, width, height);
            	drawFrame();
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
        }
        
        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x + mXOffset;
            mY = y + mYOffset;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }
        
        @Override
        public void onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    drawFrame();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    drawFrame();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    drawFrame();
                    break;
            }
        }
        
        void drawFrame() {
        	
        	if (!mVisible)
        		return;
        	
            final SurfaceHolder holder = getSurfaceHolder();
            if (holder == null)
            	return;
            
            if (mPaint == null)
            	return;

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null && mBitmap != null) {
                	
                	c.drawColor(Color.rgb(mRandom.nextInt(256), 
                			mRandom.nextInt(256), 
                			mRandom.nextInt(256)));
                	
                    mPaint.setColor(Color.rgb(mRandom.nextInt(256), 
                			mRandom.nextInt(256), 
                			mRandom.nextInt(256)));
                    
                    mPaint.setStrokeWidth(mRandom.nextInt(50));
                    
                    c.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
                    
                    c.drawPath(mPath, mPaint);
                }
            }
                catch (Exception ex){
                	//Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }
        }

        public void colorChanged(int color) {
            mPaint.setColor(color);
        }


    }
}