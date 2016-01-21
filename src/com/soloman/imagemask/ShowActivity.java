package com.soloman.imagemask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

public class ShowActivity extends Activity {
	
	private MaskImage2 mi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageaddmask2);
		
		mi = (MaskImage2) findViewById(R.id.imageview_id2);
		mi.mImageSource = R.drawable.mask_image;
		mi.mImageSource_fromSD = Environment.getExternalStorageDirectory() + "/S_CameraAddMask_Origin.png";
	}
}
