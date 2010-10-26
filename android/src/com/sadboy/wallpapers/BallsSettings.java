package com.sadboy.wallpapers;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.sadboy.wallpapers.ColorPickerDialog.OnColorChangedListener;

public class BallsSettings  extends PreferenceActivity 
	implements SharedPreferences.OnSharedPreferenceChangeListener {

	protected static final String SHARED_PREFS_NAME = "BALLS_NAME";
	protected static final String SHARED_PREFS_BACK_COLOR = "BALLS_BACK_COLOR";
	protected static final String SHARED_PREFS_BALL_COLOR = "BALLS_BALL_COLOR";
	protected static final String SHARED_PREFS_COUNT = "BALLS_COUNT";
	protected static final String SHARED_PREFS_SIZE = "BALLS_SIZE";
	protected static final String SHARED_PREFS_STYLE = "BALLS_STYLE";
	protected static final String SHARED_PREFS_THEME = "BALLS_THEME";

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
	    	
    	// List preference
        ListPreference listPref = new ListPreference(this);
        listPref.setEntries(R.array.render_style);
        listPref.setEntryValues(R.array.entryvalues_style);
        listPref.setDialogTitle("Style");
        listPref.setKey(SHARED_PREFS_STYLE);
        listPref.setTitle("Render Style");
        listPref.setSummary("Sets the style of the balls");
        root.addPreference(listPref);
        
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, 0);
	    int def = sp.getInt(BallsSettings.SHARED_PREFS_COUNT, 25);
	    final SeekBarPreference count = 
	    	new SeekBarPreference(BallsSettings.this,
	    						"Count", " Balls", def, 100);
	   count.setTitle("Ball Count");
	   count.setKey(SHARED_PREFS_COUNT);
	   count.setSummary("Set the number of balls shown (affects performance)");
	   root.addPreference(count);
	   
	    int sdef = sp.getInt(BallsSettings.SHARED_PREFS_SIZE, 150);
	    final SeekBarPreference size = 
	    	new SeekBarPreference(BallsSettings.this,
	    						"Size", " ", sdef, 1000);
	    size.setTitle("Ball Size");
	    size.setKey(SHARED_PREFS_SIZE);
	    size.setSummary("Set the size of balls shown (affects performance)");
	   root.addPreference(size);
        
        
	    PreferenceCategory cat = new PreferenceCategory(this);
	    root.addPreference(cat);
	    
	    ListPreference theme = new ListPreference(this);
	    theme.setEntries(R.array.render_theme);
	    theme.setEntryValues(R.array.entryvalues_theme);
	    theme.setDialogTitle("Theme");
	    theme.setKey(SHARED_PREFS_THEME);
	    theme.setTitle("Color Theme");
	    theme.setSummary("Preset color theme (overrides other color settings)");
        cat.addPreference(theme);
	    
	    
	    Preference paintColor = new Preference(this);
	    paintColor.setKey(SHARED_PREFS_BALL_COLOR);
	    paintColor.setTitle("Set Ball Color");
	    paintColor.setSummary("Set the ball color");
	    paintColor.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Paint paint = new Paint();
				new ColorPickerDialog(BallsSettings.this, new OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						SharedPreferences sp =	getSharedPreferences(SHARED_PREFS_NAME, 0);
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt(SHARED_PREFS_BALL_COLOR, color);
						editor.commit();
					}
				}, paint.getColor()).show();
				return true;
			}
		});
	    cat.addPreference(paintColor);
	    
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
						SharedPreferences sp =	getSharedPreferences(SHARED_PREFS_NAME, 0);
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt(SHARED_PREFS_BACK_COLOR, color);
						editor.commit();
					}
				}, paint.getColor()).show();
				return true;
			}
		});
	    cat.addPreference(canvasColor);
	   
	    
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

