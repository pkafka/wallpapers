package com.sadboy.wallpapers;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.sadboy.wallpapers.ColorPickerDialog.OnColorChangedListener;

public class GlowWormSettings extends PreferenceActivity 
	implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(
                GlowWorm.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.glowworm);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                this);
	    setPreferenceScreen(createPreferenceHierarchy());
    }
   

	private PreferenceScreen createPreferenceHierarchy() {
	    PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
	    try{
	    CheckBoxPreference togglePref = new CheckBoxPreference(this);
	    togglePref.setKey(GlowWorm.SHARED_PREFS_BLACKANDWHITE);
	    togglePref.setTitle("Black & White");
	    togglePref.setSummary("Choose black & white mode or uncheck for random paint colors");
	    root.addPreference(togglePref);
	            
	    Preference canvasColor = new Preference(this);
	    canvasColor.setKey("canvas_color_picker");
	    canvasColor.setTitle("Set Canvas Color");
	    canvasColor.setSummary("Set the background canvas color");
	    canvasColor.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Paint paint = new Paint();
				new ColorPickerDialog(GlowWormSettings.this, new OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						SharedPreferences sp =	getSharedPreferences(GlowWorm.SHARED_PREFS_NAME, 0);
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt(GlowWorm.SHARED_PREFS_CANVAS_COLOR, color);
						editor.commit();
					}
				}, paint.getColor()).show();
				return true;
			}
		});
	    root.addPreference(canvasColor);
	    
	    CheckBoxPreference randomPaint = new CheckBoxPreference(this);
	    randomPaint.setKey(GlowWorm.SHARED_PREFS_RANDOM_COLOR);
	    randomPaint.setTitle("Disable Random Colors");
	    randomPaint.setSummary("A single color from the options below will be used");
	    root.addPreference(randomPaint);
	    
	    Preference paintColor = new Preference(this);
	    paintColor.setKey("paint_color_picker");
	    paintColor.setTitle("Set Paint Color");
	    paintColor.setSummary("Set the paint color");
	    paintColor.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Paint paint = new Paint();
				new ColorPickerDialog(GlowWormSettings.this, new OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						SharedPreferences sp =	getSharedPreferences(GlowWorm.SHARED_PREFS_NAME, 0);
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt(GlowWorm.SHARED_PREFS_PAINT_COLOR, color);
						editor.commit();
					}
				}, paint.getColor()).show();
				return true;
			}
		});
	    root.addPreference(paintColor);
	    }catch (Exception ex){
        	Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
