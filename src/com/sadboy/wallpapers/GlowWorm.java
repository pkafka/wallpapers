package com.sadboy.wallpapers;

import java.io.IOException;
import java.io.InputStream;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class GlowWorm extends WallpaperService {


	public static final String SHARED_PREFS_NAME="fadeaway_settings";
	public static final String SHARED_PREFS_CANVAS_COLOR="canvas_color";
	public static final String SHARED_PREFS_PAINT_COLOR="paint_color";
	public static final String SHARED_PREFS_RANDOM_COLOR="random_color";
	public static final String SHARED_PREFS_BLACKANDWHITE="black_and_white";
	
	private SharedPreferences mPrefs;
    private boolean mBlackAndWhite;
    private int mCanvasColor;
    private int mPaintColor;
    private boolean mNoRandomColors;
	
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

    class PaintEngine extends Engine
    implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static final int FADE_MSG = 1;
        private static final int FADE_DELAY = 175;
        boolean mFading;
        
        private static final int FADE_ALPHA = 0x06;
        private static final int MAX_FADE_STEPS = 256/FADE_ALPHA + 4;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private final Rect mRect = new Rect();
        private final Paint mPaint;
        private final Paint mFadePaint;
        private boolean mCurDown;
        private int mCurX;
        private int mCurY;
        private float mCurPressure;
        private float mCurSize;
        private int mCurWidth;
        private int mFadeSteps = MAX_FADE_STEPS;
        
        int mHeight;
        int mWidth;
        boolean mVisible;
        SharedPreferences mPrefs;
        Rect mSurfaceFrame;
        
        PaintEngine(){
        	mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setARGB(255, 255, 255, 255);
            mFadePaint = new Paint();
            mFadePaint.setDither(true);
            mFadePaint.setARGB(FADE_ALPHA, 0, 0, 0);
            startFading();     
            
            mPrefs = GlowWorm.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
            mPrefs.registerOnSharedPreferenceChangeListener(this);
        	onSharedPreferenceChanged(mPrefs, null);
            loadPrefs();
        }
        
        void startFading() {
            mHandler.removeMessages(FADE_MSG);
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(FADE_MSG), FADE_DELAY);
        }
        
        void stopFading() {
            mHandler.removeMessages(FADE_MSG);
        }
        
        private Handler mHandler = new Handler() {
            @Override public void handleMessage(Message msg) {
                switch (msg.what) {
                    // Upon receiving the fade pulse, we have the view perform a
                    // fade and then enqueue a new message to pulse at the desired
                    // next time.
                    case FADE_MSG: {
                        fade();
                        mHandler.sendMessageDelayed(
                                mHandler.obtainMessage(FADE_MSG), FADE_DELAY);
                        break;
                    }
                    default:
                        super.handleMessage(msg);
                }
            }
        };
        
        public void clear() {
            if (mCanvas != null) {
                mPaint.setARGB(0xff, 0, 0, 0);
                mCanvas.drawPaint(mPaint);
                drawFrame();
                mFadeSteps = MAX_FADE_STEPS;
            }
        }
        
        public void fade() {
            if (mCanvas != null && mFadeSteps < MAX_FADE_STEPS) {
                mCanvas.drawPaint(mFadePaint);
                drawFrame();
                mFadeSteps++;
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
                	
                	if (mBitmap != null) 
                        c.drawBitmap(mBitmap, 0, 0, mPaint);
                }
            }
            catch (Exception ex){
            	Toast.makeText(getApplicationContext(), "Exception:" + ex.getStackTrace()[0] + ex.getStackTrace()[1], Toast.LENGTH_LONG).show();
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }
        }
        
        @Override
        public void onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            mCurDown = action == MotionEvent.ACTION_DOWN
                    || action == MotionEvent.ACTION_MOVE;
            int N = event.getHistorySize();
            for (int i=0; i<N; i++) {
                //Log.i("TouchPaint", "Intermediate pointer #" + i);
                drawPoint(event.getHistoricalX(i), event.getHistoricalY(i),
                        event.getHistoricalPressure(i),
                        event.getHistoricalSize(i));
            }
            drawPoint(event.getX(), event.getY(), event.getPressure(),
                    event.getSize());
        }
        
        private void drawPoint(float x, float y, float pressure, float size) {
            //Log.i("TouchPaint", "Drawing: " + x + "x" + y + " p="
            //        + pressure + " s=" + size);
            mCurX = (int)x;
            mCurY = (int)y;
            mCurPressure = pressure;
            mCurSize = size;
            mCurWidth = (int)(mCurSize*(mWidth/3));
            if (mCurWidth < 1) mCurWidth = 1;
            if (mCurDown && mBitmap != null) {
                int pressureLevel = (int)(mCurPressure*255);
                mPaint.setARGB(pressureLevel, 255, 255, 255);
                mCanvas.drawCircle(mCurX, mCurY, mCurWidth, mPaint);
                mRect.set(mCurX-mCurWidth-2, mCurY-mCurWidth-2,
                        mCurX+mCurWidth+2, mCurY+mCurWidth+2);
                drawFrame();
            }
            mFadeSteps = 0;
        }
    	
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			loadPrefs();
		}
         
         private void loadPrefs(){
             mCanvasColor = mPrefs.getInt(SHARED_PREFS_CANVAS_COLOR, Color.BLACK);
             mPaintColor = mPrefs.getInt(SHARED_PREFS_PAINT_COLOR, Color.WHITE);
             mBlackAndWhite = mPrefs.getBoolean(SHARED_PREFS_BLACKANDWHITE, false);
             mNoRandomColors = mPrefs.getBoolean(SHARED_PREFS_RANDOM_COLOR, true);
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
         public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
             super.onSurfaceChanged(holder, format, width, height);
             mSurfaceFrame = holder.getSurfaceFrame();
             if (mHeight != height || mWidth != width)
             {
             	mHeight = height;
             	mWidth = width;
             	setCanvasAndBitmat(mBitmap, width, height);
             	drawFrame();
             }
         }
         
         @Override
         public void onDestroy() {
        	 stopFading();
             super.onDestroy();
         }

         @Override
         public void onVisibilityChanged(boolean visible) {
             mVisible = visible;
             if (!visible)
            	 stopFading();
             else
            	 startFading();
             drawFrame();
         }
    	
    }

}
