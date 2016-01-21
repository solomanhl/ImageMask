package com.soloman.imagemask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MaskImage2 extends ImageView {
	String SDPATH;
	public String mImageSource_fromSD;
	public int mMaskSourceID;
	int mImageSource = 0;
	int mMaskSource = 0;
	RuntimeException mException;

	public MaskImage2(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.MaskImage, 0, 0);
//		mImageSource = a.getResourceId(R.styleable.MaskImage_image, 0);
//		mMaskSource = a.getResourceId(R.styleable.MaskImage_mask, 0);
		mImageSource = mMaskSourceID;

		if (mImageSource_fromSD == "" || mMaskSource == 0) {
			mException = new IllegalArgumentException(
					a.getPositionDescription()
							+ ": The content attribute is required and must refer to a valid image.");
		}

		if (mException != null)
			throw mException;
		/**
		 * ��Ҫ����ʵ��
		 */
		// ��ȡͼƬ����Դ�ļ�
		Bitmap original = BitmapFactory.decodeFile(mImageSource_fromSD);
		// ��ȡ���ֲ�ͼƬ
		Bitmap mask = BitmapFactory.decodeResource(getResources(), mMaskSource);
		Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),
				Config.ARGB_8888);
		// �����ֲ��ͼƬ�ŵ�������
		Canvas mCanvas = new Canvas(result);// ����һ�����ִ�С�Ļ���
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// �����
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));// �����ظ��Ĳ��֣���ʾ�����
		mCanvas.drawBitmap(original, 0, 0, null);// ��ԴͼƬ
		mCanvas.drawBitmap(mask, 0, 0, paint);// ������ͼƬ
		paint.setXfermode(null);
		setImageBitmap(result);
		setScaleType(ScaleType.CENTER);

		//����ϳɵ���Ƭ
		saveBitmap(result, "S_CameraAddMask_AddMask.png");
		
		a.recycle();
	}

	/** ���淽�� */
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
