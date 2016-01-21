package com.soloman.imagemask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btn_imageMask, btn_cameraMask, btn_cameraMask2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findView();
		setOnclickListener();
	}

	private void setOnclickListener() {
		// TODO Auto-generated method stub
		btn_imageMask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("info", "btn_imageMask onClicked");
				Intent it = new Intent(MainActivity.this, ImageAddMask.class);
				startActivity(it);
			}
		});

		btn_cameraMask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("info", "btn_cameraMask onClicked");
				Intent it = new Intent(MainActivity.this, CameraAddMask.class);
				startActivity(it);
			}
		});
		
		btn_cameraMask2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("info", "btn_cameraMask2 onClicked");
				Intent it = new Intent(MainActivity.this, CameraAddMask2.class);
				startActivity(it);
			}
		});
	}

	private void findView() {
		// TODO Auto-generated method stub
		btn_imageMask = (Button) findViewById(R.id.btn_imageMask);
		btn_cameraMask = (Button) findViewById(R.id.btn_cameraMask);
		btn_cameraMask2 = (Button) findViewById(R.id.btn_cameraMask2);
	}
}
