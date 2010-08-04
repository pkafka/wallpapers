package com.sadboy.wallpapers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class NotAnApp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		showDialog(0);
	}
	
	void close(){
		finish();
	}

	@Override
    protected Dialog onCreateDialog(int id) {
        return new AlertDialog.Builder(NotAnApp.this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Info")
            .setMessage("INFO: This collection of wallpapers is not meant to be started as an application." +
            		" Go to your system settings live wallpapers and the wallpapers will be there to select. " +
            		"\n" +
            		"The system live wallpapers are usually accessable by pressing 'Menu' button on your home screen and selecting the 'Wallpapers' menu option." +
            		"\n" +
            		"Thanks and hope you enjoy, there will be many more wallpapers added soon.")
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	finish();
                }
            })
            .create();
	}
}
