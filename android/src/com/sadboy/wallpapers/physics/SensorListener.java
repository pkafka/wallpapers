package com.sadboy.wallpapers.physics;


import java.util.List;

import min3d.core.Object3d;
import min3d.vos.Number3d;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorListener implements SensorEventListener{

	/*
	 * 
	 * 
Sensor.TYPE_ORIENTATION:
All values are angles in degrees.
values[0]: Azimuth, angle between the magnetic north direction and the Y axis, around the Z axis (0 to 359). 0=North, 90=East, 180=South, 270=West
values[1]: Pitch, rotation around X axis (-180 to 180), with positive values when the z-axis moves toward the y-axis.
values[2]: Roll, rotation around Y axis (-90 to 90), with positive values when the x-axis moves toward the z-axis.
Important note: For historical reasons the roll angle is positive in the clockwise direction (mathematically speaking, it should be positive in the counter-clockwise direction).
Note: This definition is different from yaw, pitch and roll used in aviation where the X axis is along the long side of the plane (tail to nose).
Note: This sensor type exists for legacy reasons, please use getRotationMatrix() in conjunction with remapCoordinateSystem() and getOrientation() to compute these values instead.

Sensor.TYPE_ACCELEROMETER:
All values are in SI units (m/s^2) and measure the acceleration applied to the phone minus the force of gravity.
values[0]: Acceleration minus Gx on the x-axis
values[1]: Acceleration minus Gy on the y-axis
values[2]: Acceleration minus Gz on the z-axis
Examples:
When the device lies flat on a table and is pushed on its left side toward the right, the x acceleration value is positive.
When the device lies flat on a table, the acceleration value is +9.81, which correspond to the acceleration of the device (0 m/s^2) minus the force of gravity (-9.81 m/s^2).
When the device lies flat on a table and is pushed toward the sky with an acceleration of A m/s^2, the acceleration value is equal to A+9.81 which correspond to the acceleration of the device (+A m/s^2) minus the force of gravity (-9.81 m/s^2).
	 * 
	 * 
	 */
	private SensorManager mSensor;
	private Context _context;
	
	float[] _orientation, _accelerometer;
	
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
			_orientation = event.values;
		else if (event.sensor.getType() == event.sensor.TYPE_ACCELEROMETER)
			_accelerometer = event.values;
	}
	
	public float getAzimuth(){
		if (_orientation != null)
			return _orientation[0];
		return 0;
	}
	public float getPitch(){
		if (_orientation != null)
			return _orientation[1];
		return 0;
	}
	public float getRoll(){
		if (_orientation != null)
			return _orientation[2];
		return 0;
	}
	
	public Number3d getAccelerometer(){
		if (_accelerometer != null){
			return new Number3d(((_accelerometer[0] * Object3d.TOUCH_SCALE_FACTOR) * .03f), 
					((_accelerometer[1] * Object3d.TOUCH_SCALE_FACTOR) * .03f),
					0);
		}
		else 
			return null;
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
        	else if (sl.get(i).getType() == Sensor.TYPE_ACCELEROMETER){
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