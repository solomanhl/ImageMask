package com.soloman.imagemask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CameraAddMask extends Activity implements OnClickListener {

	private Button bntTakePic;
	private Button bntEnter;
	private Button bntCancel;
	private SurfaceView surfaceView;
	private FrameLayout fraShadeTop;
	private FrameLayout fraShadeBottom;
	private Camera camera;
	private Camera.Parameters parameters = null;
	private WindowManager mWindowManager;
	private int windowWidth;// 获取手机屏幕宽度
	private int windowHight;// 获取手机屏幕高度
	private float density;//屏幕密度
	private int photoWidth = 1280;//预览和保存的宽度
	private int photoHeight = 720;
	private String savePath = "/finger/";
	private Bundle bundle = null;// 声明一个Bundle对象，用来存储数据
	private int IS_TOOK = 0;// 是否已经拍照 ,0为否

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cameraaddmask);
		init();
		getActionBar().hide();

	}

	@SuppressWarnings("deprecation")
	private void init() {
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		windowWidth = mWindowManager.getDefaultDisplay().getWidth();
		windowHight = mWindowManager.getDefaultDisplay().getHeight();
		fraShadeTop = (FrameLayout) findViewById(R.id.fra_shade_top);
		fraShadeBottom = (FrameLayout) findViewById(R.id.fra_shade_bottom);
		RelativeLayout.LayoutParams topParams = (RelativeLayout.LayoutParams) fraShadeTop
				.getLayoutParams();
		topParams.width = windowWidth;
		topParams.height = (windowHight - windowWidth) / 2;
		fraShadeTop.setLayoutParams(topParams);
		fraShadeTop.getBackground().setAlpha(200);
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）

		RelativeLayout.LayoutParams bottomParams = (RelativeLayout.LayoutParams) fraShadeBottom
				.getLayoutParams();
		bottomParams.width = windowWidth;
		bottomParams.height = (windowHight - windowWidth) / 2;
		fraShadeBottom.setLayoutParams(bottomParams);
		fraShadeBottom.getBackground().setAlpha(200);

		// 按钮
		bntTakePic = (Button) findViewById(R.id.bnt_takepicture);
		bntEnter = (Button) findViewById(R.id.bnt_enter);
		bntCancel = (Button) findViewById(R.id.bnt_cancel);

		bntTakePic.setVisibility(View.VISIBLE);
		bntEnter.setVisibility(View.INVISIBLE);
		bntCancel.setVisibility(View.INVISIBLE);
		bntTakePic.setOnClickListener(this);
		bntEnter.setOnClickListener(this);
		bntCancel.setOnClickListener(this);

		// 照相机预览的空间
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView.getHolder().setFixedSize(windowWidth, windowWidth); // 设置Surface分辨率
		surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
		surfaceView.getHolder().addCallback(new SurfaceCallback());// 为SurfaceView的句柄添加一个回调函数
	}

	/**
	 * 三个按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bnt_takepicture:
			// 拍照
			if (camera != null) {
				camera.takePicture(null, null, new MyPictureCallback());
			}
			break;

		case R.id.bnt_enter:
			if (bundle == null) {
				Toast.makeText(getApplicationContext(), "bundle null",
						Toast.LENGTH_SHORT).show();
			} else {
				try {
					if (isHaveSDCard())
						saveToSDCard(bundle.getByteArray("bytes"));
					else
						saveToRoot(bundle.getByteArray("bytes"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				finish();
			}
			break;
		case R.id.bnt_cancel:
			bntTakePic.setVisibility(View.VISIBLE);
			bntCancel.setVisibility(View.INVISIBLE);
			bntEnter.setVisibility(View.INVISIBLE);
			if (camera != null) {
				IS_TOOK = 0;
				camera.startPreview();
			}
			break;
		}
	}

	/**
	 * 检验是否有SD卡
	 * 
	 * @true or false
	 */
	public static boolean isHaveSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 重构照相类
	 * 
	 * @author
	 * 
	 */
	private final class MyPictureCallback implements PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				bundle = new Bundle();
				bundle.putByteArray("bytes", data); // 将图片字节数据保存在bundle当中，实现数据交换

				// saveToSDCard(data); // 保存图片到sd卡中
				Toast.makeText(getApplicationContext(), "success",
						Toast.LENGTH_SHORT).show();
				bntTakePic.setVisibility(View.INVISIBLE);
				bntCancel.setVisibility(View.VISIBLE);
				bntEnter.setVisibility(View.VISIBLE);
				// camera.startPreview(); // 拍完照后，重新开始预览
				IS_TOOK = 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Bitmap rotate(Bitmap b, int deg) {
		Matrix m = new Matrix();
		m.setRotate(deg, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
		float targetX, targetY;
		if (deg == 90 ) {
			targetX = b.getHeight();
			targetY = 0;
		} else {
			targetX = b.getHeight();
			targetY = b.getWidth();
		}

		final float[] values = new float[9];
		m.getValues(values);

		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];

		m.postTranslate(targetX - x1, targetY - y1);

		Bitmap bm1 = Bitmap.createBitmap(b.getHeight(), b.getWidth(),
				Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(b, m, paint);
		return bm1;
	}
	
	/**
	 * 将拍下来的照片存放在SD卡中
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void saveToSDCard(byte[] data) {
		// 剪切为正方形
		try {
			Bitmap b = byteToBitmap(data);
			//逆时针旋转90度
			b = rotate(b, 90);
			//这里得到的b是预设拍照分辨率的一半
//			Bitmap bitmap = Bitmap.createBitmap(b, 0, 0, windowWidth, windowWidth);
			int startY = 0;
			startY = fraShadeTop.getHeight() * b.getHeight() / windowHight;
			Bitmap bitmap = Bitmap.createBitmap(b, 0, startY, b.getWidth(), b.getWidth());
			// 生成文件
			// Date date = new Date();
			// SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			// //
			// 格式化时间
			// String filename = format.format(date) + ".jpg";
			String filename = "S_CameraAddMask.png";
			File fileFolder = new File(
					Environment.getExternalStorageDirectory() + "/");
//			if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
//				fileFolder.mkdir();
//			}
			File pngFile = new File(fileFolder, filename);
			if (pngFile.exists()) {
				pngFile.delete();
			}
			FileOutputStream outputStream = new FileOutputStream(pngFile); // 文件输出流
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
			outputStream.flush();

			// out.close();
			// outputStream.write(data); // 写入sd卡中
			outputStream.close(); // 关闭输出流
			Intent intent = new Intent();
			intent.putExtra("path", Environment.getExternalStorageDirectory()
					+ savePath + filename);
			setResult(1, intent);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
     * 
     */
	public void saveToRoot(byte[] data) throws IOException {
		// 剪切为正方形
		Bitmap b = byteToBitmap(data);
		Bitmap bitmap = Bitmap.createBitmap(b, 0, 0, windowWidth, windowWidth);
		// 生成文件
		// Date date = new Date();
		// SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); //
		// 格式化时间
		// String filename = format.format(date) + ".png";
		String filename = "S_CameraAddMask.png";
		File fileFolder = new File(Environment.getRootDirectory() + "/");
//		if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
//			fileFolder.mkdir();
//		}
		File pngFile = new File(fileFolder, filename);
		FileOutputStream outputStream = new FileOutputStream(pngFile); // 文件输出流
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		outputStream.flush();

		// out.close();
		// outputStream.write(data); // 写入sd卡中
		outputStream.close(); // 关闭输出流
		Intent intent = new Intent();
		intent.putExtra("path", Environment.getExternalStorageDirectory()
				+ filename);
		setResult(1, intent);
	}

	/**
	 * 把图片byte流编程bitmap
	 * 
	 * @param data
	 * @return
	 */
	private Bitmap byteToBitmap(byte[] data) {
		Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		int i = 0;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				b = BitmapFactory
						.decodeByteArray(data, 0, data.length, options);
				break;
			}
			i += 1;
		}
		return b;

	}

	/**
	 * 重构相机照相回调类
	 * 
	 * @author pc
	 * 
	 */
	private final class SurfaceCallback implements Callback {

		@SuppressWarnings("deprecation")
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			try{
				//参考SDK中的API，获取相机的参数：
				Camera.Parameters parameters = camera.getParameters(); 
				//获取预览的各种分辨率
				List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes(); 
				//获取摄像头支持的各种分辨率
				List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes(); 
				
				parameters.setPictureFormat(PixelFormat.JPEG);  // 设置图片格式
				parameters.setPreviewSize(photoWidth, photoHeight); // 设置预览大小
//				parameters.setPreviewFrameRate(5); // 设置每秒显示4帧
				parameters.setPictureSize(photoWidth, photoHeight); // 设置保存的图片尺寸
//				parameters.setJpegQuality(80); // 设置照片质量
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
				camera.setParameters(parameters);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			try {
				camera = Camera.open(); // 打开摄像头
				camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
				camera.setDisplayOrientation(getPreviewDegree(CameraAddMask.this));
				camera.startPreview(); // 开始预览
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if (camera != null) {
				camera.release(); // 释放照相机
				camera = null;
			}
		}

	}

	/**
	 * 物理按键事件
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA: // 按下拍照按钮
			if (camera != null && event.getRepeatCount() == 0) {
				// 拍照
				// 注：调用takePicture()方法进行拍照是传入了一个PictureCallback对象——当程序获取了拍照所得的图片数据之后
				// ，PictureCallback对象将会被回调，该对象可以负责对相片进行保存或传入网络
				camera.takePicture(null, null, new MyPictureCallback());
			}
		case KeyEvent.KEYCODE_BACK:
			if (IS_TOOK == 0)
				finish();
			else {
				// camera.startPreview();
				bntCancel.performClick();
				return false;
			}

			break;

		}

		return super.onKeyDown(keyCode, event);
	}

	// 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
	public static int getPreviewDegree(Activity activity) {
		// 获得手机的方向
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degree = 0;
		// 根据手机的方向计算相机预览画面应该选择的角度
		switch (rotation) {
		case Surface.ROTATION_0:
			degree = 90;
			break;
		case Surface.ROTATION_90:
			degree = 0;
			break;
		case Surface.ROTATION_180:
			degree = 270;
			break;
		case Surface.ROTATION_270:
			degree = 180;
			break;
		}
		return degree;
	}

	/**
	 * 通过文件地址获取文件的bitmap
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */

	public static Bitmap getBitmapByPath(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

}