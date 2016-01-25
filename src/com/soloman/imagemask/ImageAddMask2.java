package com.soloman.imagemask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

public class ImageAddMask2 extends Activity {
	
	private MaskImage2 mi;
	private ImageView imageview1, imageview2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageaddmask2);
		
//		mi = (MaskImage2) findViewById(R.id.imageview_id2);
//		mi.mMaskSourceID = R.drawable.mask_image;
//		mi.mImageSource_fromSD = Environment.getExternalStorageDirectory() + "/S_CameraAddMask_Origin.png";
		
		findView();
	}

	private void findView() {
		// TODO Auto-generated method stub
		imageview1 = (ImageView) findViewById(R.id.imageview1);
		imageview2 = (ImageView) findViewById(R.id.imageview2);
		
		Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/S_CameraAddMask_Origin.png");
		imageview1.setImageBitmap(bm);
//		imageview2.setImageResource(R.drawable.mask_image);
	}
}
