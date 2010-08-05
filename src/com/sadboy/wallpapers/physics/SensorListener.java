package com.sadboy.wallpapers.physics;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.GestureDetector;

public class SensorListener implements SensorEventListener{

	private SensorManager mSensor;
	private Context _context;
	
	float[] mValues;
	
	public SensorListener(Context c){
		_context = c;
		register();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == event.sensor.TYPE_ORIENTATION)
			mValues = event.values;
	}
	
	public float getAzimuth(){
		if (mValues != null)
			return mValues[0];
		return 0;
	}
	public float getPitch(){
		if (mValues != null)
			return mValues[1];
		return 0;
	}
	public float getRoll(){
		if (mValues != null)
			return mValues[2];
		return 0;
	}

	public void register(){
		mSensor = (SensorManager) _context.getSystemService(
				android.content.Context.SENSOR_SERVICE);
        List<Sensor> sl = mSensor.getSensorList(Sensor.TYPE_ALL);
        
        for (int i = 0; i < sl.size(); i++) {
        	if (sl.get(i).getType() == Sensor.TYPE_ORIENTATION){
        		mSensor.registerListener(SensorListener.this,
	                    sl.get(i),
	                    SensorManager.SENSOR_DELAY_GAME);
        	}
		}
	}
   
    public void unregister() {
    	mSensor.unregisterListener(SensorListener.this);
    }
}
