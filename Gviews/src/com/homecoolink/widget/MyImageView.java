package com.homecoolink.widget;

import com.homecoolink.utils.ImageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MyImageView extends ImageView{
	private String mPath;
	
	public static final int IMAGE_WIDTH_HEIGHT = 100;
	
	private Bitmap mBitmap;
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		// TODO Auto-generated method stub
		super.onWindowVisibilityChanged(visibility);
		
		if(visibility==View.GONE){
			if(null!=this.mBitmap){
				this.mBitmap.recycle();
				this.mBitmap = null;
			}
		}else{
			if(null!=this.mBitmap){
				this.mBitmap.recycle();
				this.mBitmap = null;
			}
			if(null!=this.mPath){
				
				
				//this.mBitmap = BitmapFactory.decodeFile(this.mPath);
				this.mBitmap = ImageUtils.getBitmap(this.mPath, IMAGE_WIDTH_HEIGHT, IMAGE_WIDTH_HEIGHT);
				this.setImageBitmap(mBitmap);
			}
			
		}
		Log.e("my","onWindowVisibilityChanged:"+visibility);
	}

	public void setImageFilePath(String path){
		this.mPath = path;
		if(null!=this.mBitmap){
			this.mBitmap.recycle();
			this.mBitmap = null;
		}
		
		//this.mBitmap = BitmapFactory.decodeFile(this.mPath);
		this.mBitmap = ImageUtils.getBitmap(this.mPath, IMAGE_WIDTH_HEIGHT, IMAGE_WIDTH_HEIGHT);
		this.setImageBitmap(mBitmap);
	}
	
}
