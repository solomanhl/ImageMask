package com.soloman.imagemask;

import android.app.Application;
import android.util.DisplayMetrics;
import android.util.Log;

public class App extends Application{
	public float density; //ÆÁÄ»ÃÜ¶È0.75 / 1.0 /1.25/ 1.5/2.0 /3.0
	
	@Override
	public void onCreate() {
		Log.d("GlobalVar", "[ExampleApplication] onCreate");
		super.onCreate();

		getDeviceInfo();
	}
	
	private void getDeviceInfo() {
		// TODO Auto-generated method stub
			DisplayMetrics metric = new DisplayMetrics();
			density = metric.density; // ÆÁÄ»ÃÜ¶È0.75 / 1.0 /1.25/ 1.5/2.0 /3.0

	}
}
