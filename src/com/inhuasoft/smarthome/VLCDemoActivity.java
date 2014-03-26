package com.inhuasoft.smarthome;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.videolan.vlc.LibVLC;
import org.videolan.vlc.LibVlcException;

import com.inhuasoft.smarthome.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class VLCDemoActivity extends Activity {
	
	public final static String TAG = "VLC/VideoPlayerActivity";
	
	private LibVLC mLibVLC = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        
        try {
        //	LibVLC.useIOMX(getApplicationContext());
			mLibVLC = LibVLC.getInstance();
		} catch (LibVlcException e) {
			e.printStackTrace();
		}

		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), VideoPlayerActivity.class);
		
		startActivity(intent);
		
    }

}