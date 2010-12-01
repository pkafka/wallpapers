

package com.sadboy.wallpapers.paid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;


public class Splatter extends WallpaperService
{

	public static final String SHARED_PREFS_NAME="splatterSettings";
	public static final String SHARED_PREFS_CANVAS_COLOR="canvas_color";
	public static final String SHARED_PREFS_PAINT_COLOR="paint_color";
	public static final String SHARED_PREFS_RANDOM_COLOR="random_color";
	public static final String SHARED_PREFS_BLACKANDWHITE="black_and_white";
	
    @Override
    public Engine onCreateEngine() {
        return new PaintEngine();
    }

    class PaintEngine extends Engine
    implements SharedPreferences.OnSharedPreferenceChangeListener {
    	
        private SharedPreferences mPrefs;
        private boolean mBlackAndWhite;
        private int mCanvasColor;
        private int mPaintColor;
        private boolean mNoRandomColors;
        
        private MaskFilter  mEmboss;
        private MaskFilter  mBlur;
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        private float mX, mY, mXOffset = 0, mYOffset = 0;
     
        int mHeight;
        int mWidth;
        
        Bitmap[] mDrawables;

        private final Paint mPaint = new Paint();
        
        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        private static final float TOUCH_TOLERANCE = 4;
        
        private PathEffect[] mEffects;
        private float mPhase;
        
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
            
            mEffects = new PathEffect[2];
            mEffects[0] = null;     // no effect
            mEffects[1] = new CornerPathEffect(10);
           
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
           
            mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                                           0.4f, 6, 3.5f);

            mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
            
            mPaint.setXfermode(null);
            mPaint.setAlpha(0xFF);
            mPaint.setMaskFilter(mEmboss);
            
            loadDrawables();
            
            mPrefs = Splatter.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
            mPrefs.registerOnSharedPreferenceChangeListener(this);
        	onSharedPreferenceChanged(mPrefs, null);
            loadPrefs();
        }

        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
           loadPrefs();
        }
        
        private void loadPrefs(){
            mCanvasColor = mPrefs.getInt(SHARED_PREFS_CANVAS_COLOR, Color.BLACK);
            mPaintColor = mPrefs.getInt(SHARED_PREFS_PAINT_COLOR, Color.WHITE);
            mBlackAndWhite = mPrefs.getBoolean(SHARED_PREFS_BLACKANDWHITE, false);
            mNoRandomColors = mPrefs.getBoolean(SHARED_PREFS_RANDOM_COLOR, false);
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
        
        void loadDrawables(){
        	mDrawables = new Bitmap[17];
            addBitmap(0, getResources().openRawResource(R.drawable.splatter_01));
            addBitmap(1, getResources().openRawResource(R.drawable.splatter_02));
            addBitmap(2, getResources().openRawResource(R.drawable.splatter_03));
            addBitmap(3, getResources().openRawResource(R.drawable.splatter_04));
            addBitmap(4, getResources().openRawResource(R.drawable.splatter_05));
            addBitmap(5, getResources().openRawResource(R.drawable.splatter_06));
            addBitmap(6, getResources().openRawResource(R.drawable.splatter_07));
            addBitmap(7, getResources().openRawResource(R.drawable.splatter_08));
            addBitmap(8, getResources().openRawResource(R.drawable.splatter_09));
            addBitmap(9, getResources().openRawResource(R.drawable.splatter_10));
            addBitmap(10, getResources().openRawResource(R.drawable.splatter_11));
            addBitmap(11, getResources().openRawResource(R.drawable.splatter_01_large));
            addBitmap(12, getResources().openRawResource(R.drawable.splatter_02_large));
            addBitmap(13, getResources().openRawResource(R.drawable.splatter_03_large));
            addBitmap(14, getResources().openRawResource(R.drawable.splatter_04_large));
            addBitmap(15, getResources().openRawResource(R.drawable.splatter_05_large));
            addBitmap(16, getResources().openRawResource(R.drawable.splatter_06_large));
        }
        
        void addBitmap(int index, InputStream is){
        	BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm;
            
            opts.inJustDecodeBounds = true;
            bm = BitmapFactory.decodeStream(is, null, opts);
            opts.inJustDecodeBounds = false;   
            //opts.inSampleSize = -3;       
            bm = BitmapFactory.decodeStream(is, null, opts);

            mDrawables[index] = bm.extractAlpha();

            try {
				is.close();
			} catch (IOException e) {
			}
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
            mPath.reset();
        }
        
        int mLastX;
        int mLastY;
        int mTapCount;
        long mTapTime;
        
        private void setLastTap(float x, float y){
        	mLastX = (int)x;
    		mLastY = (int)y;
    		mTapCount++;
    		mTapTime = System.currentTimeMillis();
        }
        
        private boolean IsClearScreen(float x, float y){
        	if (mTapCount == 0){
        		setLastTap(x, y);
        	}
        	else if (mLastX < x + 30 && 
        				mLastX > x - 30 &&
        				mLastY < y + 30 &&
        				mLastY > y - 30 &&
        				System.currentTimeMillis() - mTapTime < 750){
        			mTapCount = 0;

        			setCanvasAndBitmat(null, mBitmap.getWidth(), mBitmap.getHeight());
        			
            		mTapCount = 0;
        			setLastTap(x, y);
        			drawFrame();
        			Toast.makeText(getApplicationContext(), "Double Tap Cleared", Toast.LENGTH_SHORT).show();
        			return true;
        	}
        	else{
        		mTapCount = 0;
        		setLastTap(x, y);
        	}
        	return false;
        }
        
        @Override
        public void onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                	if (IsClearScreen(x, y))
                		return;
                    touch_start(x, y);
                    drawFrame();
                    break;
                case MotionEvent.ACTION_MOVE:
                	mLastX = (int)x - 50;
            		mLastY = (int)y - 50;
                    touch_move(x, y);
                    drawFrame();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
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
                if (c != null) {
                	
                	c.drawColor(mCanvasColor);
                	
                	if (mBlackAndWhite){
                		if (System.currentTimeMillis() % 2 == 0){
	                		c.drawColor(Color.BLACK);
	                		mPaint.setColor(Color.WHITE);
                		}
                		else{
                			c.drawColor(Color.WHITE);
                			mPaint.setColor(Color.BLACK);
                		}
                	}
                	else if (!mNoRandomColors){
                		mPaint.setColor(Color.rgb(mRandom.nextInt(256), 
                			mRandom.nextInt(256), 
                			mRandom.nextInt(256)));
                	}
                	else {
                		mPaint.setColor(mPaintColor);
                	}
                	
                    mCanvas.drawBitmap(mDrawables[mRandom.nextInt(mDrawables.length)], 
                    		mLastX, mLastY, mPaint);
                    c.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
                }
            }
                catch (Exception ex){
                	Toast.makeText(getApplicationContext(), "Exception:" + ex.getStackTrace()[0] + ex.getStackTrace()[1], Toast.LENGTH_LONG).show();
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }
        }



    }
}