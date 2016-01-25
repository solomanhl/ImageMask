package com.soloman.imagemask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class MaskImage2 extends ImageView {
	String SDPATH;
	public String mImageSource_fromSD;
	public int mMaskSourceID;
//	private int mImageSource2 = 0;
	private int mMaskSource2 = 0;
	RuntimeException mException;

	public MaskImage2(Context context, AttributeSet attrs) {
		super(context, attrs);
		mMaskSourceID = R.drawable.mask_image;
		mImageSource_fromSD = Environment.getExternalStorageDirectory() + "/S_CameraAddMask_Origin.png";
		
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaskImage2, 0, 0);
//		mImageSource2 = a.getResourceId(R.styleable.MaskImage2_image2, 0);
		mMaskSource2 = a.getResourceId(R.styleable.MaskImage2_mask2, 0);
//		mMaskSource2 = mMaskSourceID;

		if (mImageSource_fromSD == "" || mMaskSource2 == 0) {
			mException = new IllegalArgumentException(
					a.getPositionDescription()
							+ ": The content attribute is required and must refer to a valid image.");
		}

		if (mException != null)
			throw mException;
		/**
		 * 主要代码实现
		 */
		Options ops = new Options();
		ops.inScaled = true;
		
		// 获取遮罩层图片
		Bitmap mask = BitmapFactory.decodeResource(getResources(), mMaskSource2, ops);
		// 获取图片的资源文件,放后面，可以使用得到的mask的ops
		Bitmap original = BitmapFactory.decodeFile(mImageSource_fromSD, ops);
		Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Config.ARGB_8888);//这里实际大小又是乘1.5？
		
		
		// 将遮罩层的图片放到画布中
		Canvas mCanvas = new Canvas(result);// 创建一个遮罩大小的画布
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// 抗锯齿
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));// 叠加重复的部分，显示下面的
		mCanvas.drawBitmap(original, 0, 0, null);// 放源图片
		mCanvas.drawBitmap(mask, 0, 0, paint);// 放遮罩图片
		paint.setXfermode(null);
		setImageBitmap(result);
		setScaleType(ScaleType.CENTER);

		//保存合成的照片
		saveBitmap(result, "S_CameraAddMask_AddMask");
		
		a.recycle();
	}

	/** 保存方法 */
	public void saveBitmap(Bitmap bm, String picName) {
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		File f = new File(SDPATH, picName + ".png");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
