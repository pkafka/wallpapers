package com.sadboy.wallpapers;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;

import com.sadboy.wallpapers.ColorPickerDialog.OnColorChangedListener;

public class BallsSettings  extends PreferenceActivity 
	implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String SHARED_PREFS_BLACKANDWHITE = "BALLS_BW";
	protected static final String SHARED_PREFS_NAME = "BALLS_NAME";
	protected static final String SHARED_PREFS_BACK_COLOR = "BALLS_BACK_COLOR";
	private static final String SHARED_PREFS_RANDOM_COLOR = "BALLS_RANDOM";
	protected static final String SHARED_PREFS_BALL_COLOR = "BALLS_BALL_COLOR";
	private static final String SHARED_PREFS_COUNT = null;

	@Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(
                BallsSettings.SHARED_PREFS_NAME);
        getPreferenceManager()
        .getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);
	    setPreferenceScreen(createPreferenceHierarchy());
    }
   
	private PreferenceScreen createPreferenceHierarchy() {
	    PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
	    try{
	    	
	    CheckBoxPreference togglePref = new CheckBoxPreference(this);
	    togglePref.setKey(BallsSettings.SHARED_PREFS_BLACKANDWHITE);
	    togglePref.setTitle("Black & White");
	    togglePref.setSummary("Choose black & white mode or uncheck for random colors");
	    root.addPreference(togglePref);
	            
	    Preference canvasColor = new Preference(this);
	    canvasColor.setKey("canvas_color_picker");
	    canvasColor.setTitle("Set back color");
	    canvasColor.setSummary("Set the background canvas color");
	    canvasColor.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Paint paint = new Paint();
				new ColorPickerDialog(BallsSettings.this, new OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						SharedPreferences sp =	getSharedPreferences(BallsSettings.SHARED_PREFS_NAME, 0);
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt(BallsSettings.SHARED_PREFS_BACK_COLOR, color);
						editor.commit();
					}
				}, paint.getColor()).show();
				return true;
			}
		});
	    root.addPreference(canvasColor);

		SharedPreferences sp = getSharedPreferences(BallsSettings.SHARED_PREFS_NAME, 0);
	    int def = sp.getInt(BallsSettings.SHARED_PREFS_COUNT, 25);
	    final SeekBarPreference count = 
	    	new SeekBarPreference(BallsSettings.this,
	    						"Count", " Balls", def, 100);
	   count.setTitle("Ball Count");
	   count.setKey(BallsSettings.SHARED_PREFS_COUNT);
	   count.setSummary("Set the number of balls shown (affects performance)");
	   count.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			SharedPreferences sp =	getSharedPreferences(BallsSettings.SHARED_PREFS_NAME, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt(BallsSettings.SHARED_PREFS_COUNT, count.getProgress());
			editor.commit();
			return true;
		}
	   });
	   root.addPreference(count);
	    
	    CheckBoxPreference randomPaint = new CheckBoxPreference(this);
	    randomPaint.setKey(BallsSettings.SHARED_PREFS_RANDOM_COLOR);
	    randomPaint.setTitle("Disable Random Colors");
	    randomPaint.setSummary("A single color from the options below will be used");
	    root.addPreference(randomPaint);
	    
	    Preference paintColor = new Preference(this);
	    paintColor.setKey(BallsSettings.SHARED_PREFS_BALL_COLOR);
	    paintColor.setTitle("Set Ball Color");
	    paintColor.setSummary("Set the ball color");
	    paintColor.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Paint paint = new Paint();
				new ColorPickerDialog(BallsSettings.this, new OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						SharedPreferences sp =	getSharedPreferences(BallsSettings.SHARED_PREFS_NAME, 0);
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt(BallsSettings.SHARED_PREFS_BALL_COLOR, color);
						editor.commit();
					}
				}, paint.getColor()).show();
				return true;
			}
		});
	    root.addPreference(paintColor);
	    paintColor.setDependency(BallsSettings.SHARED_PREFS_RANDOM_COLOR);
	    
	    }catch (Exception ex){
	    	//catching some stupid dependency issue
	    }
	    return root;
	}
	

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
    }
}

