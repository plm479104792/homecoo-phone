package com.p2p.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.LinearLayout.LayoutParams;

import com.p2p.core.GestureDetector.OnZoomInListener;
import com.p2p.core.global.Constants;

public class P2PView extends BaseP2PView {
	static final String TAG = "p2p";
	Context mContext;
	MediaPlayer mPlayer;
	private int mWidth, mHeight;
	boolean isInitScreen = false;
	protected GestureDetector mGestureDetector;
	int deviceType;
	int mWindowWidth, mWindowHeight;
	int  fgFullScreen = 0;
	boolean isInitScale;
	static final boolean SUPPORT_ZOOM =false;
	public P2PView(Context context) {
		super(context);
		this.mContext = context;
		mPlayer = MediaPlayer.getInstance();

	}

	public P2PView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mPlayer = MediaPlayer.getInstance();
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		mWindowWidth = dm.widthPixels;
		mWindowHeight = dm.heightPixels;
	}

	public void initScaleView()
	{
		isInitScale = true;
	}
	
	private void vSetWindow()
	{
		DisplayMetrics dm = new DisplayMetrics();  
		dm = getResources().getDisplayMetrics();  
		mWindowWidth  = dm.widthPixels;
		mWindowHeight = dm.heightPixels;
		
		Log.e("my", "xWidth:" + mWindowWidth + " xHeight:" + mWindowHeight);
		mWidth = mWindowWidth;
		mHeight = mWindowHeight;
		
		if(fgFullScreen == 0)
		{
			if (deviceType == P2PValue.DeviceType.NPC)
			{
			  int Rate,Rate2;
			  Rate = mWidth*1024/mHeight;
			  Rate2= 4*1024/3; 
			  if(Rate > Rate2)
			  {
				  mWidth = mHeight*4/3;
			  }
			  else
			  {
				  mHeight = mWidth*3/4;
			  }
			}
			else
			{
			  int Rate,Rate2;
			  Rate = mWidth*1024/mHeight;
			  Rate2= 16*1024/9; 
			  if(Rate > Rate2)
			  {
				  mWidth = mHeight*16/9;
			  }
			  else
			  {
				  mHeight = mWidth*9/16;
			  }
			}
			
		}
	

		LayoutParams layoutParams = (LayoutParams) getLayoutParams();
		layoutParams.width = mWidth;
		layoutParams.height = mHeight;
		layoutParams.leftMargin = (mWindowWidth - mWidth) / 2;
		layoutParams.topMargin = (mWindowHeight - mHeight) / 2;
		setLayoutParams(layoutParams);
		if (null != mPlayer) {
			MediaPlayer.ChangeScreenSize(mWidth, mHeight, fgFullScreen);
		}
	}
	
	public void updateScreenOrientation()
	{
		vSetWindow();
	}

	public void setCallBack() {
		MediaPlayer.setEglView(this);
		getHolder().addCallback(mSHCallback);
	}

	public void setGestureDetector(GestureDetector gestureDetector) {
		mGestureDetector = gestureDetector;
	}

	public void setDeviceType(int type) {
		this.deviceType = type;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		Log.d(TAG, "onTouchEvent");
		if(SUPPORT_ZOOM)
		{
			if(!(super.onTouchEvent(event)))
			{
				if (mGestureDetector != null) 
			    {
					mGestureDetector.setOnZoomInListener(zoomInListener);
					mGestureDetector.onTouchEvent(event);
				}
			}	
		}
		else
		{
			if (mGestureDetector != null) 
		    {
				mGestureDetector.setOnZoomInListener(zoomInListener);
				mGestureDetector.onTouchEvent(event);
			}
		}

		
		return true;
	}

	public OnZoomInListener zoomInListener = new OnZoomInListener(){

		@Override
		public void onZoom(MotionEvent event) 
		{
			// TODO Auto-generated method stub
			if(SUPPORT_ZOOM)
			{
			 mode = MODE.ZOOM;
			 touchSuper(event);
			}
		}
		
	};
	
	
	public void touchSuper(MotionEvent event){
		super.onTouchEvent(event);
	}
	public void fullScreen() {

        fgFullScreen = 1;
		vSetWindow();
	}

	public void halfScreen() {
        fgFullScreen = 0;
		vSetWindow();
	}

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// startVideo();
			Log.v(TAG, "surfaceChanged()");

			int sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565 by default
			switch (format) {
			case PixelFormat.A_8:
				Log.v(TAG, "pixel format A_8");
				break;
			case PixelFormat.LA_88:
				Log.v(TAG, "pixel format LA_88");
				break;
			case PixelFormat.L_8:
				Log.v(TAG, "pixel format L_8");
				break;
			case PixelFormat.RGBA_4444:
				Log.v(TAG, "pixel format RGBA_4444");
				sdlFormat = 0x85421002; // SDL_PIXELFORMAT_RGBA4444
				break;
			case PixelFormat.RGBA_5551:
				Log.v(TAG, "pixel format RGBA_5551");
				sdlFormat = 0x85441002; // SDL_PIXELFORMAT_RGBA5551
				break;
			case PixelFormat.RGBA_8888:
				Log.v(TAG, "pixel format RGBA_8888");
				sdlFormat = 0x86462004; // SDL_PIXELFORMAT_RGBA8888
				break;
			case PixelFormat.RGBX_8888:
				Log.v(TAG, "pixel format RGBX_8888");
				sdlFormat = 0x86262004; // SDL_PIXELFORMAT_RGBX8888
				break;
			case PixelFormat.RGB_332:
				Log.v(TAG, "pixel format RGB_332");
				sdlFormat = 0x84110801; // SDL_PIXELFORMAT_RGB332
				break;
			case PixelFormat.RGB_565:
				Log.v(TAG, "pixel format RGB_565");
				sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565
				break;
			case PixelFormat.RGB_888:
				Log.v(TAG, "pixel format RGB_888");
				// Not sure this is right, maybe SDL_PIXELFORMAT_RGB24 instead?
				sdlFormat = 0x86161804; // SDL_PIXELFORMAT_RGB888
				break;
			default:
				Log.v(TAG, "pixel format unknown " + format);
				break;
			}
			Log.v(TAG, "Window size:" + w + "x" + h);
			MediaPlayer.onNativeResize(w, h, sdlFormat);
			Log.e("surface", w + ":" + h);
			
			mWidth = w;
			mHeight = h;
			
			if(fgFullScreen == 0)
			{
				if (deviceType == P2PValue.DeviceType.NPC)
				{
				  int Rate,Rate2;
				  Rate = mWidth*1024/mHeight;
				  Rate2= 4*1024/3; 
				  if(Rate > Rate2)
				  {
					  mWidth = mHeight*4/3;
				  }
				  else
				  {
					  mHeight = mWidth*3/4;
				  }
				}
				else
				{
				  int Rate,Rate2;
				  Rate = mWidth*1024/mHeight;
				  Rate2= 16*1024/9; 
				  if(Rate > Rate2)
				  {
					  mWidth = mHeight*16/9;
				  }
				  else
				  {
					  mHeight = mWidth*9/16;
				  }
				}
				
			}
			
			LayoutParams layoutParams = (LayoutParams) getLayoutParams();
			layoutParams.width = mWidth;
			layoutParams.height = mHeight;
			layoutParams.leftMargin = (mWindowWidth - mWidth) / 2;
			layoutParams.topMargin = (mWindowHeight - mHeight) / 2;
			setLayoutParams(layoutParams);

			if (!isInitScreen) {
				isInitScreen = true;
				mPlayer.init(mWidth, mHeight, mWindowWidth);
				Intent start = new Intent();
				start.setAction(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START);
				mContext.sendBroadcast(start);
			}

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
			holder.setKeepScreenOn(true);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			release();
		}
	};

	public synchronized void release() {
		Log.d(TAG, "releasing player");
		if (mPlayer != null) {
			// mPlayer.native_p2p_hungup();
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}

		MediaPlayer.ReleaseOpenGL();
		Log.d(TAG, "released");
	}

	@Override
	protected int getCurrentWidth() {
		// TODO Auto-generated method stub
		
		return mWindowWidth;
	}

	@Override
	protected int getCurrentHeight() {
		// TODO Auto-generated method stub
		return mWindowHeight;
	}


	@Override
	protected void setVideoScale(int x, int y, float scale) 
	{
		// TODO Auto-generated method stub
		
		LayoutParams layoutParams = (LayoutParams) getLayoutParams();
	
		y = mHeight -(layoutParams.topMargin + y);
		
		//Log.e("Gview","zoom" + x + ":" + y+"       "+scale);
			
		MediaPlayer.ZoomView(x,y,scale) ;
	
	 
	}

	@Override
	protected boolean MovePicture(int left, int top) {
		// TODO Auto-generated method stub
		
		Log.e("Gview","move" + left + ":" + (0-top)+"       ");
		if( MediaPlayer.MoveView(left,0-top) == 0)
		{
			return false;
		}
		return true;
		
	}
	
	@Override
	protected void StopMoving() {
		// TODO Auto-generated method stub
		
		Log.e("Gview","stop moving ");
		MediaPlayer.MoveView(0,0);
		
	}
	
	@Override
	public void changeNormalSize(){
		/*
		LayoutParams layoutParams = (LayoutParams) getLayoutParams();
		layoutParams.width = mFixWidth;
		layoutParams.height = mFixHeight;
		layoutParams.leftMargin = (mWindowWidth - mFixWidth) / 2;
		layoutParams.topMargin = (mWindowHeight - mFixHeight) / 2;
		setLayoutParams(layoutParams);
		mPlayer.ChangeScreenSize(mFixWidth, mFixHeight, 0);
		*/
	}

}
