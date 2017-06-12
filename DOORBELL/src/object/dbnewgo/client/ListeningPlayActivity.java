package object.dbnewgo.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import object.dbnewgo.client.BridgeService.DateTimeInterface;
import object.dbnewgo.client.BridgeService.DoorBellSystemParmInterface;
import object.dbnewgo.client.BridgeService.PlayInterface;
import object.p2pipcam.bean.CameraParamsBean;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.AudioPlayer;
import object.p2pipcam.utils.CustomAudioRecorder;
import object.p2pipcam.utils.CustomBuffer;
import object.p2pipcam.utils.CustomBufferData;
import object.p2pipcam.utils.CustomBufferHead;
import object.p2pipcam.utils.DataBaseHelper;
import object.p2pipcam.utils.TakeVideoThread;
import object.p2pipcam.utils.VibratorUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint({ "Wakelock", "NewApi" })
public class ListeningPlayActivity extends BaseActivity implements
		OnGestureListener, CustomAudioRecorder.AudioRecordResult,
		OnDoubleTapListener, PlayInterface, DateTimeInterface, OnTouchListener,
		DoorBellSystemParmInterface, OnClickListener {
	// SensorEventListener
	private static final String LOG_TAG = "PlayActivity";
	private int pushTypeInt = 0;
	private static final int AUDIO_BUFFER_START_CODE = 0xff00ff;

	private byte[] videodata = null;
	private int videoDataLen = 0;
	private int nVideoWidth = 0;
	private int nVideoHeight = 0;

	private View progressView = null;

	private View ptzOtherSetAnimView;
	private TextView textosd = null;
	private TextView textTimeStamp = null;
	private String strName = null;
	private String strDID = null;

	private View osdView = null;

	private boolean bDisplayFinished = true;

	private CustomBuffer AudioBuffer = null;
	private AudioPlayer audioPlayer = null;
	private CustomAudioRecorder customAudioRecorder = null;
	private boolean bAudioRecordStart = false;
	// add
	private boolean isTakeVideo = false;

	private ImageView vidoeView;
	private DataBaseHelper helper;
	// private View ptzOtherSetView;
	private boolean isTakepic = false;

	private boolean isH264 = false;
	private boolean isPictSave = false;
	private LinearLayout layout_videoing = null;

	private TextView textView_show;
	// private ImageButton button_say;
	// ==============test=================
	private String tzStr = "GMT+08:00";
	private String timeshow = " ";
	private long time, time1;
	private boolean isOneShow = true;
	private int width;
	float density;
	private LinearLayout ly_doorbell_sound, ly_doorbell_video, ly_doorbell_pic,
			ly_doorbell_vq, ly_doorbell_hz, ly_doorbell_cancel;
	private TakeVideoThread takeVideoThread = null;
	private File path = null;
	private long blockSize;

	private TextView tv_cancel_time = null;
	private boolean isCancel = false;
	private int cancelTime = 30;
	private final int CANCELTIME = 1230;
	private final int CANCELTIMEFINISH = 1231;
	private final int RESETTIME = 1232;
	private int position = 0;

	// private AudioManager audioManager;
	// private SensorManager mSensorManager;
	// private Sensor mSensor;
	private FrameLayout framelayoutplay;
	private boolean isF = true;

	class MyCancelTimeThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			istagShow = false;
			while (isCancel) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (isCancel) {
					cancelTime--;
					mHandler.sendEmptyMessage(CANCELTIME);
				}
				if (isCancel && cancelTime == 1) {
					mHandler.sendEmptyMessage(CANCELTIMEFINISH);
				}
			}
			super.run();
		}
	}

	private void returnLastBmp2Home() {
		new Thread() {
			public void run() {
				if (isH264) {
					byte[] rgb = new byte[nVideoWidth * nVideoHeight * 2];
					NativeCaller.YUV4202RGB565(videodata, rgb, nVideoWidth,
							nVideoHeight);
					ByteBuffer buffer = ByteBuffer.wrap(rgb);
					rgb = null;
					mBmp = Bitmap.createBitmap(nVideoWidth, nVideoHeight,
							Bitmap.Config.RGB_565);
					mBmp.copyPixelsFromBuffer(buffer);
				}
				if (mBmp != null) {
					int btmWidth = mBmp.getWidth();
					int btmHeight = mBmp.getHeight();
					float scaleW = ((float) 70) / btmWidth;
					float scaleH = ((float) 50) / btmHeight;
					Matrix matrix = new Matrix();
					matrix.postScale(scaleW, scaleH);
					Bitmap bmp = Bitmap.createBitmap(mBmp, 0, 0, btmWidth,
							btmHeight, matrix, true);
					File div1 = new File(
							Environment.getExternalStorageDirectory(),
							ContentCommon.SDCARD_PATH + "/picid");
					File file = new File(div1, strDID + ".jpg");
					if (file.exists()) {
						Log.d("first_pic", file.delete() + "");
					}
					UpdataBitmp(strDID, bmp);
					saveBmpToSDcard(strDID, bmp);
				}
			}
		}.start();
	}

	private void saveBmpToSDcard(String did, Bitmap bitmap) {
		FileOutputStream fos = null;
		File div = new File(Environment.getExternalStorageDirectory(),
				ContentCommon.SDCARD_PATH + "/picid");
		if (!div.exists()) {
			div.mkdirs();
		}
		try {
			File file = new File(div, did + ".jpg");
			fos = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos)) {
				fos.flush();
				fos.close();
				Cursor cursor = helper.queryFirstpic(did);
				if (cursor.getCount() <= 0) {
					String path = file.getAbsolutePath();
					helper.addFirstpic(did, path);
				}
				if (cursor != null) {
					cursor.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isTakeVideo == true) {
				// showToast(R.string.ptz_takevideo_show);
			} else {

				// showSureDialogPlay(this);
			}
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {

		}
		return super.onKeyDown(keyCode, event);
	}

	private LinearLayout framLayout2;

	private Bitmap mBmp;
	private boolean istagShow = false;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2587:
				showToastLong(R.string.door_play_other_listen);
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {
						returnLastBmp2Home();
						if (pushTypeInt != 3 && pushTypeInt != 4) {
							Intent intent = new Intent("back");
							sendBroadcast(intent);
						}
					};

					@Override
					protected Void doInBackground(Void... params) {
						if (myRender != null) {
							myRender.destroyShaders();
						}
						isTakeVideo = false;
						StopTalk();
						releaseTalk();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}

					protected void onPostExecute(Void result) {
						finish();
						overridePendingTransition(R.anim.out_to_right,
								R.anim.in_from_left);

					};
				}.execute();
				break;
			case 1478:
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {
						returnLastBmp2Home();
						if (pushTypeInt != 3 && pushTypeInt != 4) {
							Intent intent = new Intent("back");
							sendBroadcast(intent);
						}
					};

					@Override
					protected Void doInBackground(Void... params) {
						if (myRender != null) {
							myRender.destroyShaders();
						}
						isTakeVideo = false;
						StopTalk();
						releaseTalk();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}

					protected void onPostExecute(Void result) {
						finish();
						overridePendingTransition(R.anim.out_to_right,
								R.anim.in_from_left);

					};
				}.execute();
				break;
			case RESETTIME:
				if (resettime < 10) {
					resettime = 10;
				}
				cancelTime = resettime;
				break;
			case CANCELTIMEFINISH:
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {

						if (pushTypeInt != 3 && pushTypeInt != 4) {
							Intent intent = new Intent("back");
							sendBroadcast(intent);
						}
					};

					@Override
					protected Void doInBackground(Void... params) {

						if (myRender != null) {
							myRender.destroyShaders();
						}
						isTakeVideo = false;
						StopTalk();
						releaseTalk();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}

					protected void onPostExecute(Void result) {
						finish();
						overridePendingTransition(R.anim.out_to_right,
								R.anim.in_from_left);

					};
				}.execute();
				break;
			case CANCELTIME:
				if ((!istagShow) && (cancelTime > 30 || cancelTime <= 27)) {
					tv_cancel_time.setVisibility(View.VISIBLE);
					istagShow = true;
				}
				if (cancelTime >= 0) {
					tv_cancel_time.setText(cancelTime + "");
				}

				break;
			case 1: // h264
			{

				if (isOneShow) {

					// tv_cancel_time.setVisibility(View.VISIBLE);
					progressView.setVisibility(View.INVISIBLE);
					osdView.setVisibility(View.VISIBLE);
					vidoeView.setVisibility(View.GONE);
					myGlSurfaceView.setVisibility(View.VISIBLE);
					isOneShow = false;
					String get_alarm_config = "get_bell_config.cgi?&"
							+ "loginuse=" + SystemValue.doorBellAdmin
							+ "&loginpas=" + SystemValue.doorBellPass
							+ "&user=" + SystemValue.doorBellAdmin + "&pwd="
							+ SystemValue.doorBellPass;
					NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
					isCancel = true;
					new MyCancelTimeThread().start();
				}
				textTimeStamp.setText(timeshow);
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						width, width * 3 / 4);

				myGlSurfaceView.setLayoutParams(lp);
				myRender.writeSample(videodata, nVideoWidth, nVideoHeight);

				bDisplayFinished = true;
			}
				break;
			case 2: // JPEG
			{
				if (isOneShow) {
					// tv_cancel_time.setVisibility(View.VISIBLE);
					progressView.setVisibility(View.INVISIBLE);
					osdView.setVisibility(View.VISIBLE);
					myGlSurfaceView.setVisibility(View.GONE);
					isOneShow = false;
					String get_alarm_config = "get_bell_config.cgi?&"
							+ "loginuse=" + SystemValue.doorBellAdmin
							+ "&loginpas=" + SystemValue.doorBellPass
							+ "&user=" + SystemValue.doorBellAdmin + "&pwd="
							+ SystemValue.doorBellPass;
					NativeCaller.TransferMessage(strDID, get_alarm_config, 1);
					isCancel = true;
					new MyCancelTimeThread().start();
				}
				textTimeStamp.setText(timeshow);
				if (mBmp != null) {
					mBmp.recycle();
					mBmp = null;
				}
				Log.i("", "videoDataLen = " + videoDataLen);
				if (videoDataLen <= 256 * 1024 && mBmp == null) {
					try {
						mBmp = BitmapFactory.decodeByteArray(videodata, 0,
								videoDataLen);
					} catch (OutOfMemoryError e) {
						mBmp = null;
					}
				}
				if (mBmp == null) {
					Log.d(LOG_TAG, "bmp can't be decode...");
					bDisplayFinished = true;
					return;
				}

				nVideoWidth = mBmp.getWidth();
				nVideoHeight = mBmp.getHeight();
				
				if (isF) {
					returnLastBmp2Home();
					isF = false;
				}

//				mBmp = Bitmap.createScaledBitmap(mBmp, width, width * 3 / 4,
//						true);
				vidoeView.setVisibility(View.VISIBLE);
				vidoeView.setImageBitmap(mBmp);
				if (isTakepic) {
					isTakepic = false;
					takePicture(mBmp);
				}
				bDisplayFinished = true;
			}
				break;

			}

		}

	};

	private void takePicture(final Bitmap bmp) {
		if (isPictSave == false) {
			isPictSave = true;
			new Thread() {
				public void run() {
					savePicToSDcard(bmp);
				}
			}.start();
		} else {
			return;
		}

	}

	private synchronized void savePicToSDcard(Bitmap bmp) {
		String strDate = getStrDate();
		String date = strDate.substring(0, 10);
		int i = 0;
		Cursor cursorpic = helper.queryAllPicture(strDID);
		while (cursorpic.moveToNext()) {
			String filePath = cursorpic.getString(cursorpic
					.getColumnIndex(DataBaseHelper.KEY_FILEPATH));
			String s1 = filePath.substring(filePath.lastIndexOf("/") + 1);
			String date1 = s1.substring(0, 10);
			// Log.d("tag", "cursorpic.getCount():" + cursorpic.getCount() +
			// " i:"
			// + i + "  date:" + date + "  date1:" + date1);
			if (date1.toString().equals(date)) {
				i++;
			}
		}

		if (i >= 500) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					isPictSave = false;
					showToast(R.string.play_take_pic_show);
				}
			});
			return;
		}
		if (cursorpic != null) {
			cursorpic.close();
		}
		Cursor cursor = helper.queryVideoOrPictureByDate(strDID, strDate,
				DataBaseHelper.TYPE_PICTURE);

		int seri = cursor.getCount() + 1;
		FileOutputStream fos = null;
		try {
			File div = new File(Environment.getExternalStorageDirectory(),
					ContentCommon.SDCARD_PATH + "/pic");
			if (!div.exists()) {
				div.mkdirs();
			}
			Log.d("test", "savePicToSDcard playdiv:" + div);
			File file = new File(div, strDate + "_" + "=" + strDID + "!" + seri
					+ "_" + ".jpg");
			fos = new FileOutputStream(file);
			if (bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
				fos.flush();
				helper.createVideoOrPic(strDID, file.getAbsolutePath(),
						DataBaseHelper.TYPE_PICTURE, strDate);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								ListeningPlayActivity.this,
								getResources().getString(
										R.string.ptz_takepic_ok), 0).show();
					}
				});
			}

		} catch (Exception e) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							ListeningPlayActivity.this,
							getResources().getString(R.string.ptz_takepic_fail),
							0).show();
				}
			});

			Log.d("tag", "exception:" + e.getMessage());
			e.printStackTrace();
		} finally {
			isPictSave = false;
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		// if (bmp != null) {
		// bmp.recycle();
		// }
	}

	private String getStrDate() {
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
		String strDate = f.format(d);
		return strDate;
	}

	WakeLock wakeLock = null;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// mSensorManager.registerListener(this, mSensor,
		// SensorManager.SENSOR_DELAY_NORMAL);
		if (wakeLock != null) {
			wakeLock.acquire();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// mSensorManager.unregisterListener(this);
		if (wakeLock != null) {
			wakeLock.release();
		}
		new AsyncTask<Void, Void, Void>() {
			protected void onPreExecute() {

				if (pushTypeInt != 3 && pushTypeInt != 4) {
					// returnLastBmp2Home();
					Intent intent = new Intent("back");
					sendBroadcast(intent);
				}
			};

			@Override
			protected Void doInBackground(Void... params) {

				if (myRender != null) {
					myRender.destroyShaders();
				}
				isTakeVideo = false;
				StopTalk();
				releaseTalk();
				return null;
			}

			protected void onPostExecute(Void result) {
				finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);

			};
		}.execute();
		super.onPause();
	}

	private String pushUUID = "0";
	private SharedPreferences preuser;
	private SharedPreferences pw_state;
	private String lockPwd = "";
	private boolean isOpen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listening_play);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				"MyWakeLock");
		helper = DataBaseHelper.getInstance(this);
		findView();
		InitParams();
		BridgeService.setDateTimeInterface(this);
		BridgeService.setPlayInterface(this);
		BridgeService.setDoorBellSystemParmInterface(this);
		AudioBuffer = new CustomBuffer();
		audioPlayer = new AudioPlayer(AudioBuffer);
		customAudioRecorder = new CustomAudioRecorder(this);
		path = Environment.getExternalStorageDirectory();
		myRender = new MyRender(myGlSurfaceView);
		myGlSurfaceView.setRenderer(myRender);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		cancelTime = 20;
		resettime = 21;
		density = metric.density;
		width = metric.widthPixels;
		// int h = metric.heightPixels - (int) (117 * density) - width * 3 / 4
		// - (int) (25 * density);
		// FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, h);
		// lp.gravity = Gravity.BOTTOM;
		// ptzOtherSetAnimView.setLayoutParams(lp);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			if (width < 480) {
				LayoutParams ps = vidoeView.getLayoutParams();
				ps.height = 200 * 3 / 4;
				ps.width = 200;
				vidoeView.setLayoutParams(ps);
			} else {
				LayoutParams ps = vidoeView.getLayoutParams();
				ps.height = width * 3 / 4;
				vidoeView.setLayoutParams(ps);
			}
		}
		Log.d("test", "----width:" + width);
		doorBellOtherView();
		bAudioRecordStart = true;
		// showToast(R.string.play_talk_show);
		// 04-11 17:55:26.398: E/AndroidRuntime(21882):
		// java.lang.ClassCastException:
		// android.widget.RelativeLayout$LayoutParams cannot be cast to
		// android.widget.FrameLayout$LayoutParams

		StartAudio();
		bAudioStart = true;
		NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_PARAMS);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			DisplayMetrics metric2 = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric2);
			density = metric2.density;
			width = metric2.widthPixels;
			Log.d("test", "----width:" + width);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.BELOW, R.id.framelayoutplay);
			ptzOtherSetAnimView.setLayoutParams(params);

			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.addRule(RelativeLayout.BELOW, R.id.osdlayout);
			framelayoutplay.setPadding(0, 0, 0, 0);
			framelayoutplay.setLayoutParams(params1);
			if (width < 480) {
				LayoutParams ps = vidoeView.getLayoutParams();
				ps.height = 200 * 3 / 4;
				ps.width = 200;
				vidoeView.setLayoutParams(ps);
			} else {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, width * 3 / 4);
				vidoeView.setLayoutParams(lp);
			}

			// ptzOtherSetAnimView
		} else {  //妯睆甯冨眬
			
			DisplayMetrics metric2 = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric2);
			density = metric2.density;
			width = metric2.widthPixels;
			Log.d("test", "----width:" + width);
		

			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.addRule(RelativeLayout.BELOW, R.id.osdlayout);
			framelayoutplay.setPadding(0, 0, 0, 0);
			framelayoutplay.setLayoutParams(params1);
			if (width < 480) {
				LayoutParams ps = vidoeView.getLayoutParams();
				ps.height = 200 * 3 / 4;
				ps.width = 200;
				vidoeView.setLayoutParams(ps);
			} else {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						width* 3 / 4 , width* 3 / 4 );
				vidoeView.setLayoutParams(lp);
			}
			
			
			
			
//			DisplayMetrics metric1 = new DisplayMetrics();
//			getWindowManager().getDefaultDisplay().getMetrics(metric1);
//			density = metric1.density;
//			width = metric1.widthPixels;
			
			//瑙嗛甯冨眬璁剧疆
			LayoutParams ps = framelayoutplay.getLayoutParams();
			ps.height=width* 3 / 4;
			ps.width= width* 3 / 4;
//			ps.height = (int) (width - (300 * metric1.density)) * 3 / 4;
//			ps.width = (int) (width - (300 * metric1.density));
			framelayoutplay.setLayoutParams(ps);
//			
////			framelayoutplay.setPadding((int) (10 * metric1.density), 0, 0,
////					(int) (10 * metric.density));

			//鍔熻兘甯冨眬璁剧疆
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.framelayoutplay);
			params.addRule(RelativeLayout.BELOW, R.id.osdlayout);
			ptzOtherSetAnimView.setLayoutParams(params);
		}
		// audioManager = (AudioManager) this
		// .getSystemService(Context.AUDIO_SERVICE);
		// mSensorManager = (SensorManager)
		// getSystemService(Context.SENSOR_SERVICE);
		// mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		preuser = getSharedPreferences("shix_zhao_user", Context.MODE_PRIVATE);
		lockPwd = preuser.getString(strDID + "lockpwd", "123456");
		pw_state = getSharedPreferences("pw_state", Context.MODE_PRIVATE);
		isOpen = pw_state.getBoolean("pw_state", true);
		initExitPopupWindow_Group();
	}

	private void doorBellOtherView() {
		shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		ly_doorbell_sound = (LinearLayout) findViewById(R.id.ly_doorbell_sound);
		ly_doorbell_sound.setOnTouchListener(this);
		ly_doorbell_video = (LinearLayout) findViewById(R.id.ly_doorbell_video);
		ly_doorbell_video.setOnTouchListener(this);
		ly_doorbell_pic = (LinearLayout) findViewById(R.id.ly_doorbell_pic);
		ly_doorbell_pic.setOnTouchListener(this);
		ly_doorbell_vq = (LinearLayout) findViewById(R.id.ly_doorbell_vq);
		ly_doorbell_vq.setOnTouchListener(this);
		ly_doorbell_hz = (LinearLayout) findViewById(R.id.ly_doorbell_hz);
		ly_doorbell_hz.setOnTouchListener(this);
		ly_doorbell_cancel = (LinearLayout) findViewById(R.id.ly_doorbell_cancel);
		ly_doorbell_cancel.setOnTouchListener(this);
	}

	private void getDataFromOther() {
		Intent in = getIntent();
		if (in != null) {
			strName = in.getStringExtra(ContentCommon.STR_CAMERA_NAME);
			strDID = in.getStringExtra(ContentCommon.STR_CAMERA_ID);
			pushTypeInt = in.getIntExtra("pushTypeInt", 4);
			pushUUID = in.getStringExtra("pushUUID");
			if (pushUUID == null || pushUUID.length() < 1) {
				// pushUUID = "0";
				int i = (int) (Math.random() * 900) + 100;
				pushUUID = i + "";
			}
			if (strDID.length() > 3) {
				if (pushTypeInt == 3 || pushTypeInt == 4) {
					NativeCaller.StartPPPPLivestream(strDID, 20, pushUUID);
				} else {
					NativeCaller.StartPPPPLivestream(strDID, 22, pushUUID);
				}
			}
		}
	}

	private void InitParams() {
		textosd.setText(strName);
		// cameraName.setText(strName);
	}

	// 06-27 15:37:09.664: D/PlayActivity(7576): return bDisplayFinished

	private void StartAudio() {
		synchronized (this) {
			bAudioStart = true;
			AudioBuffer.ClearAll();
			audioPlayer.AudioPlayStart();
			NativeCaller.PPPPStartAudio(strDID);
			System.out.println("======寮�鐩戝惉===StartAudio()====");
		}
	}

	private void StopAudio() {
		synchronized (this) {
			bAudioStart = false;
			audioPlayer.AudioPlayStop();
			AudioBuffer.ClearAll();
			NativeCaller.PPPPStopAudio(strDID);
		}
	}

	private void StartTalk() {
		if (customAudioRecorder != null) {
			customAudioRecorder.StartRecord();
			NativeCaller.PPPPStartTalk(strDID);
			System.out.println("=========寮�璇磋瘽==StartTalk==");
		}
	}

	private void releaseTalk() {
		if (customAudioRecorder != null) {
			customAudioRecorder.releaseRecord();
		}

	}

	private void StopTalk() {
		if (customAudioRecorder != null) {
			customAudioRecorder.StopRecord();
			NativeCaller.PPPPStopTalk(strDID);
		}
	}

	protected void setResolution(int Resolution) {
		Log.d("tag", "setResolution resolution:" + Resolution);
		NativeCaller.PPPPCameraControl(strDID, 0, Resolution);
		NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	private TextView textViewVideoing;

	private void findView() {
		layout_videoing = (LinearLayout) findViewById(R.id.video_lu_linear);
		// if (play_four_tag == 1) {
		// checkBoxHS.setVisibility(View.GONE);
		// }
		myGlSurfaceView = (GLSurfaceView) findViewById(R.id.myhsurfaceview);
		textViewVideoing = (TextView) findViewById(R.id.textTimevideoing);
		ptzOtherSetAnimView = findViewById(R.id.ptz_othersetview_anim);
		// button_say = (ImageButton) findViewById(R.id.button_say);

		// button_say.setOnTouchListener(this);
		// button_say.setVisibility(View.GONE);
		vidoeView = (ImageView) findViewById(R.id.vedioview);
		progressView = (View) findViewById(R.id.progressLayout);
		textosd = (TextView) findViewById(R.id.textosd);
		textTimeStamp = (TextView) findViewById(R.id.textTimeStamp);
		// cameraName = (TextView) findViewById(R.id.cameraName);

		osdView = (View) findViewById(R.id.osdlayout);

		tv_cancel_time = (TextView) findViewById(R.id.tv_cancel_time);

		framelayoutplay = (FrameLayout) findViewById(R.id.framelayoutplay);

	}

	private Bitmap getFirstPic(String did) {
		Cursor cursor = helper.queryFirstpic(did);
		String filepath = null;
		if (cursor.getCount() > 0) {
			cursor.moveToNext();
			filepath = cursor.getString(cursor
					.getColumnIndex(DataBaseHelper.KEY_FILEPATH));
		}
		if (cursor != null) {
			cursor.close();
		}
		if (filepath != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(filepath);
			return bitmap;

		} else {
			return null;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			DisplayMetrics metric = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric);
			density = metric.density;
			width = metric.widthPixels;
			Log.d("test", "----width:" + width);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.BELOW, R.id.framelayoutplay);
			ptzOtherSetAnimView.setLayoutParams(params);

			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.addRule(RelativeLayout.BELOW, R.id.osdlayout);
			framelayoutplay.setPadding(0, 0, 0, 0);
			framelayoutplay.setLayoutParams(params1);
			if (width < 480) {
				LayoutParams ps = vidoeView.getLayoutParams();
				ps.height = 200 * 3 / 4;
				ps.width = 200;
				vidoeView.setLayoutParams(ps);
			} else {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, width * 3 / 4);
				vidoeView.setLayoutParams(lp);
			}

			// ptzOtherSetAnimView
		} else {
			DisplayMetrics metric = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric);
			density = metric.density;
			width = metric.widthPixels;
			LayoutParams ps = framelayoutplay.getLayoutParams();
			ps.height = (int) (width - (10 * metric.density)) * 3 / 4;
			ps.width = (int) (width - (10 * metric.density));
			framelayoutplay.setLayoutParams(ps);
			framelayoutplay.setPadding(0, 0, 0, 0);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.framelayoutplay);
			params.addRule(RelativeLayout.BELOW, R.id.osdlayout);
			ptzOtherSetAnimView.setLayoutParams(params);
		}
	}

	private boolean existSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isDown = false;
	private boolean isSecondDown = false;
	private float x1 = 0;
	private float x2 = 0;
	private float y1 = 0;
	private float y2 = 0;

	// private float ZOOMMAX=2f;
	// private float ZOOMMIN=0.5f;
	// private float ZOOMMultiple=1.0f;
	// private float currentWidth=480.0f;

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private int mode = NONE;
	private float oldDist;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	float mMaxZoom = 2.0f;
	float mMinZoom = 0.3125f;
	float originalScale;
	float baseValue;
	protected Matrix mBaseMatrix = new Matrix();
	protected Matrix mSuppMatrix = new Matrix();
	private Matrix mDisplayMatrix = new Matrix();
	private final float[] mMatrixValues = new float[9];

	protected void zoomTo(float scale, float centerX, float centerY) {
		Log.d("zoomTo", "zoomTo scale:" + scale);
		if (scale > mMaxZoom) {
			scale = mMaxZoom;
		} else if (scale < mMinZoom) {
			scale = mMinZoom;
		}

		float oldScale = getScale();
		float deltaScale = scale / oldScale;
		Log.d("deltaScale", "deltaScale:" + deltaScale);
		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
	}

	protected Matrix getImageViewMatrix() {
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}

	protected float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}

	protected float getScale() {
		return getScale(mSuppMatrix);
	}

	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		return mMatrixValues[whichValue];
	}

	private float spacing(MotionEvent event) {
		try {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		} catch (Exception e) {
		}
		return 0;
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// private boolean isArrowShowing=false;
	// end===================================================================
	@Override
	public boolean onDown(MotionEvent e) {
		Log.d("tag", "onDown");
		return false;
	}

	private final int MINLEN = 80;

	// private TextView cameraName;

	// private RelativeLayout topbg;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		// if(true){
		// return false;
		// }

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public void AudioRecordData(byte[] data, int len) {
		if (bAudioRecordStart && len > 0) {
			NativeCaller.PPPPTalkAudioData(strDID, data, len);
		}
	}

	private String getDateTime() {
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String strDate = f.format(d);
		Log.d("tag", "record strDate:" + strDate);
		return strDID + "!" + "LOD_" + strDate;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	private MyRender myRender = null;
	private GLSurfaceView myGlSurfaceView = null;

	// private Bitmap resultBmp;

	@Override
	protected void onDestroy() {
		if (popupWindow_group != null && popupWindow_group.isShowing()) {
			popupWindow_group.dismiss();
			popupWindow_group = null;
		}
		isCancel = true;
		isOneShow = true;
		SystemValue.isStartRun = false;
		if (pushTypeInt == 3 || pushTypeInt == 4) {
			NativeCaller.StartPPPPLivestream(strDID, 21, pushUUID);
		} else {
			NativeCaller.StartPPPPLivestream(strDID, 24, pushUUID);
		}
		StopAudio();
		NativeCaller.StartPPPPLivestream(strDID, 42, pushUUID);
		if (takeVideoThread != null) {
			takeVideoThread.stopThread();
		}
		BridgeService.setPlayInterface(null);
		BridgeService.setDoorBellSystemParmInterface(null);
		SystemValue.ISPLAY = 0;
		if (mBmp != null) {
			mBmp.recycle();
			mBmp = null;
		}
		super.onDestroy();
	}

	/***
	 * BridgeService callback
	 * 
	 * **/
	@Override
	public void callBackCameraParamNotify(String did, int resolution,
			int brightness, int contrast, int hue, int saturation, int flip,
			int fram) {
		// Log.d(LOG_TAG, "CameraParamNotify...did:" + did + " brightness: "
		// + brightness + " resolution: " + resolution + " contrast: "
		// + contrast + " hue: " + hue + " saturation: " + saturation
		// + " flip: " + flip + "fram:" + fram);
		// Log.d("tag", "contrast:" + contrast + " brightness:" + brightness);

		Message msg = new Message();
		msg.what = 3;
		mHandler.sendMessage(msg);

	}

	private int isH264Data = 0;

	/***
	 * BridgeService callback
	 * 
	 * **/
	@Override
	public void callBaceVideoData(String did, byte[] videobuf, int h264Data,
			int len, int width, int height, int tim) {
		Log.d(LOG_TAG, "Call VideoData...h264Data: " + h264Data + " len: "
				+ len + " videobuf len: " + videobuf.length);
		if (!did.endsWith(strDID)) {
			return;
		}
		if (!bDisplayFinished) {
			Log.d(LOG_TAG, "return bDisplayFinished");
			return;
		}
		
		if (len > 256 * 1024) {
			return;
		}
		isH264Data = h264Data;
		this.time = tim;
		time1 = time * 1000;
		bDisplayFinished = false;

		videodata = videobuf;
		videoDataLen = len;
		Message msg = new Message();
		if (h264Data == 1) { // H264
			nVideoWidth = width;
			nVideoHeight = height;
			if (isTakepic) {
				isTakepic = false;
				byte[] rgb = new byte[width * height * 2];
				NativeCaller.YUV4202RGB565(videobuf, rgb, width, height);
				ByteBuffer buffer = ByteBuffer.wrap(rgb);
				mBmp = Bitmap
						.createBitmap(width, height, Bitmap.Config.RGB_565);
				mBmp.copyPixelsFromBuffer(buffer);
				takePicture(mBmp);
			}
			isH264 = true;
			msg.what = 1;

		} else { // MJPEG
			msg.what = 2;
			if (isTakeVideo && takeVideoThread != null) {
				takeVideoThread
						.addVideo(videobuf, 0, nVideoWidth, nVideoHeight);
			}
		}
		// tagPlayFour++;
		// if (play_four_tag == 1) {
		// if (tagPlayFour == 3) {
		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//
		// textosd.setVisibility(View.VISIBLE);
		// // ScrollView.LayoutParams lp = new
		// // ScrollView.LayoutParams(
		// // WindowManager.LayoutParams.MATCH_PARENT,
		// // WindowManager.LayoutParams.MATCH_PARENT);
		// // lp.gravity = Gravity.CENTER;
		// // ptzPlatform.setLayoutParams(lp);
		// }
		// });
		// }
		// }
		timeshow = setDeviceTime(time1, tzStr);
		mHandler.sendMessage(msg);
	}

	/***
	 * BridgeService callback
	 * 
	 * **/

	@Override
	public void callBackMessageNotify(String did, int msgType, int param) {
		Log.d("tag", "MessageNotify did: " + did + " msgType: " + msgType
				+ " param: " + param);
		if (msgType != ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
			return;
		}
		if (!did.equals(strDID)) {
			return;
		}
		mHandler.sendEmptyMessage(1478);
	}

	/***
	 * BridgeService callback
	 * 
	 * **/
	@Override
	public void callBackAudioData(byte[] pcm, int len) {
		// Log.d(LOG_TAG, "AudioData: len :+ " + len);
		if (isTakeVideo && takeVideoThread != null) {
			takeVideoThread.addAudio(pcm);
		}
		if (!audioPlayer.isAudioPlaying()) {
			return;
		}
		CustomBufferHead head = new CustomBufferHead();
		CustomBufferData data = new CustomBufferData();
		head.length = len;
		head.startcode = AUDIO_BUFFER_START_CODE;
		data.head = head;
		data.data = pcm;
		AudioBuffer.addData(data);
	}

	/***
	 * BridgeService callback
	 * 
	 * **/

	@Override
	public void callBackH264Data(String did, byte[] h264, int type, int size) {

		if (!did.endsWith(strDID)) {
			Log.d("testTakeVideo", "!did.endsWith(strDID)" + "   did:" + did
					+ "  strDID:" + strDID);
			return;
		}
		if (isTakeVideo && takeVideoThread != null) {
			takeVideoThread.addVideo(h264, type, nVideoWidth, nVideoHeight);
		}
	}

	@Override
	public void callBackDatetimeParams(String did, int now, int tz,
			int ntp_enable, String ntp_svr) {
		// TODO Auto-generated method stub
		setTimeZone(tz);
		Log.d("tag", "timestr:" + tzStr + "  tz:" + tz);
	}

	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
			int result) {
		// TODO Auto-generated method stub

	}

	private String setDeviceTime(long millisutc, String tz) {

		TimeZone timeZone = TimeZone.getTimeZone(tz);
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis(millisutc);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String months = "";
		if (month < 10) {
			months = "0" + month;
		} else {
			months = String.valueOf(month);
		}
		String strDay = "";
		if (day < 10) {
			strDay = "0" + day;
		} else {
			strDay = String.valueOf(day);
		}
		String strHour = "";
		if (hour < 10) {
			strHour = "0" + hour;
		} else {
			strHour = String.valueOf(hour);
		}
		String strMinute = "";
		if (minute < 10) {
			strMinute = "0" + minute;
		} else {
			strMinute = String.valueOf(minute);
		}
		String strSecond = "";
		if (second < 10) {
			strSecond = "0" + second;
		} else {
			strSecond = String.valueOf(second);
		}
		// return strWeek + "," + day + " " + strMonth + year + " " + strHour
		// + ":" + strMinute + ":" + strSecond + "    UTC";
		return year + "-" + months + "-" + strDay + "   " + strHour + ":"
				+ strMinute + ":" + strSecond;
	}

	private void setTimeZone(int tz) {
		switch (tz) {
		case 39600:
			tzStr = "GMT-11:00";
			break;
		case 36000:
			tzStr = "GMT-10:00";
			break;
		case 32400:
			tzStr = "GMT-09:00";
			break;
		case 28800:
			tzStr = "GMT-08:00";
			break;
		case 25200:
			tzStr = "GMT-07:00";
			break;
		case 21600:
			tzStr = "GMT-06:00";
			break;
		case 18000:
			tzStr = "GMT-05:00";
			break;
		case 14400:
			tzStr = "GMT-04:00";
			break;
		case 12600:
			tzStr = "GMT-03:30";
			break;
		case 10800:
			tzStr = "GMT-03:00";
			break;
		case 7200:
			tzStr = "GMT-02:00";
			break;
		case 3600:
			tzStr = "GMT-01:00";
			break;
		case 0:
			tzStr = "GMT";
			break;
		case -3600:
			tzStr = "GMT+01:00";
			break;
		case -7200:
			tzStr = "GMT+02:00";
			break;
		case -10800:
			tzStr = "GMT+03:00";
			break;
		case -12600:
			tzStr = "GMT+03:30";
			break;
		case -14400:
			tzStr = "GMT+04:00";
			break;
		case -16200:
			tzStr = "GMT+04:30";
			break;
		case -18000:
			tzStr = "GMT+05:00";
			break;
		case -19800:
			tzStr = "GMT+05:30";
			break;

		case -21600:
			tzStr = "GMT+06:00";
			break;
		case -25200:
			tzStr = "GMT+07:00";
			break;
		case -28800:
			tzStr = "GMT+08:00";
			break;
		case -32400:
			tzStr = "GMT+09:00";
			break;
		case -34200:
			tzStr = "GMT+09:30";
			break;
		case -36000:
			tzStr = "GMT+10:00";
			break;
		case -39600:
			tzStr = "GMT+11:00";
			break;
		case -43200:
			tzStr = "GMT+12:00";
			break;
		default:
			break;
		}
	};

	public int getCameraParamsBean(String did) {
		CameraParamsBean bean = new CameraParamsBean();
		for (int i = 0; i < SystemValue.arrayList.size(); i++) {
			if (did.endsWith(SystemValue.arrayList.get(i).getDid())) {
				return i;
			}
		}
		return -1;
	}

	// private Vibrator mVibrator01; // ???????????????

	// ly_doorbell_sound, ly_doorbell_video, ly_doorbell_pic,
	// ly_doorbell_vq, ly_doorbell_hz, ly_doorbell_cancel;
	private boolean bl_doorbell_sound = false;
	Animation shake = null;
	private boolean isVGA = false;
	private StatFs stat;
	private long availableBlocks;
	private boolean bAudioStart = true;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Log.d("test", "MotionEvent:" + event.getAction());
		int upid=v.getId();
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			Log.d("test", "zhaogenghuai1");
			
//			if (id==R.id.button_say) {     //銆愪慨鏀广�
		if (upid==R.id.ly_doorbell_sound) {
				StopTalk();
				ly_doorbell_sound.setBackgroundResource(R.drawable.sound_normal);
//				button_say.setBackgroundResource(R.drawable.listen_talk_noomal);
//				button_say.setVisibility(View.GONE);
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {
					};

					@Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}

					protected void onPostExecute(Void result) {
						StartAudio();
						NativeCaller.StartPPPPLivestream(strDID, 41, pushUUID);
//						button_say.setVisibility(View.VISIBLE);
					};
				}.execute();
			}else if (upid==R.id.ly_doorbell_pic) {
				ly_doorbell_pic.setBackgroundResource(R.drawable.pic_normal);
			}else if (upid==R.id.ly_doorbell_vq) {
				if (pushTypeInt == 3) {
					showToast(R.string.ip_dev_lock_pwd_alarm);
				} else {
					if (popupWindow_group != null) {
						stringBuffer.replace(0, stringBuffer.length(), "");
						editText_GroupName_pass.setText("");
						popupWindow_group.showAtLocation(textTimeStamp,
								Gravity.CENTER, 0, 0);
					}
				}
				ly_doorbell_vq
						.setBackgroundResource(R.drawable.btn_doorbell_sou_normal);
			}
			
		case MotionEvent.ACTION_DOWN:
			int did=v.getId();
//			if (did==R.id.button_say) {  銆愪慨鏀广�
//		  if (did==R.id.ly_doorbell_sound) { 
//				VibratorUtil.Vibrate(ListeningPlayActivity.this, 100);
//				StopAudio();
//				NativeCaller.StartPPPPLivestream(strDID, 42, pushUUID);
//				StartTalk();
//				ly_doorbell_sound.setBackgroundResource(R.drawable.sound_press);
//				button_say.setBackgroundResource(R.drawable.listen_talk_press);
//			}else 
				if (did==R.id.ly_doorbell_sound) {
				VibratorUtil.Vibrate(ListeningPlayActivity.this, 100);
				ly_doorbell_sound.startAnimation(shake);

				if (bAudioStart) {
					// isMcriophone = false;
                    System.out.println("鍙瀵硅缁撴潫===========");
					ly_doorbell_sound
							.setBackgroundResource(R.drawable.sound_press);
					StopAudio();
					NativeCaller.StartPPPPLivestream(strDID, 42, pushUUID);
					StartTalk();
				} else {
					ly_doorbell_sound
							.setBackgroundResource(R.drawable.sound_normal);
					showToast(R.string.play_talk_show);
					StartAudio();
					 System.out.println("鍙瀵硅寮�===========");
					NativeCaller.StartPPPPLivestream(strDID, 41, pushUUID);
				}

			}else if (did==R.id.ly_doorbell_video) {
				VibratorUtil.Vibrate(ListeningPlayActivity.this, 100); // ??100ms
				Log.d("test", "zhao1");
				ly_doorbell_video.startAnimation(shake);
				ly_doorbell_video.setOnTouchListener(null);
				if (hasSdcard()) {
					if (isTakeVideo) {
						isTakeVideo = false;
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected void onPreExecute() {
								// TODO Auto-generated method stub
								takeVideoThread.stopThread();
								super.onPreExecute();
							}

							@Override
							protected Void doInBackground(Void... params) {
								// TODO Auto-generated method stub

								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								// TODO Auto-generated method stub
								ly_doorbell_video
										.setBackgroundResource(R.drawable.video_normal);
								layout_videoing.setVisibility(View.INVISIBLE);
								ly_doorbell_video
										.setOnTouchListener(ListeningPlayActivity.this);
								super.onPostExecute(result);
							}
						}.execute();
					} else {
						stat = new StatFs(path.getPath());
						blockSize = stat.getBlockSize();
						availableBlocks = stat.getAvailableBlocks();
						if ((availableBlocks * blockSize) / (1024 * 1024) < 50) {
							showToastLong(R.string.sd_card_size_show);
							break;
						}
						isTakeVideo = true;

						layout_videoing.setVisibility(View.VISIBLE);
						SystemValue.checkSDStatu = 1;

						new AsyncTask<Void, Void, Void>() {

							@Override
							protected void onPreExecute() {
								// TODO Auto-generated method stub
								takeVideoThread = new TakeVideoThread(
										isH264Data, strDID, 15, nVideoWidth,
										nVideoHeight);
								takeVideoThread.start();
								super.onPreExecute();
							}

							@Override
							protected Void doInBackground(Void... params) {
								// TODO Auto-generated method stub
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								// TODO Auto-generated method stub

								ly_doorbell_video
										.setBackgroundResource(R.drawable.video_press);
								ly_doorbell_video
										.setOnTouchListener(ListeningPlayActivity.this);
								super.onPostExecute(result);
							}
						}.execute();
					}
				}
			}else if (did==R.id.ly_doorbell_pic) {
				VibratorUtil.Vibrate(ListeningPlayActivity.this, 100); // ??100ms
				ly_doorbell_pic.setBackgroundResource(R.drawable.pic_press);
				ly_doorbell_pic.startAnimation(shake);

				if (hasSdcard()) {
					if (existSdcard()) {
						isTakepic = true;
					} else {
						showToast(R.string.ptz_takepic_save_fail);
					}
				} else {
					showToast(R.string.local_picture_show_sd);
				}
			}else if (did==R.id.ly_doorbell_vq) {
				ly_doorbell_vq.startAnimation(shake);
				VibratorUtil.Vibrate(ListeningPlayActivity.this, 100); // ??100ms
				ly_doorbell_vq
				.setBackgroundResource(R.drawable.btn_doorbell_suo_press);
			}else if (did==R.id.ly_doorbell_hz) {
				ly_doorbell_hz.startAnimation(shake);
				VibratorUtil.Vibrate(ListeningPlayActivity.this, 100); // ??100ms
				if (pushTypeInt == 3 || pushTypeInt == 4) {
					NativeCaller.StartPPPPLivestream(strDID, 26, pushUUID);
				} else {
					NativeCaller.StartPPPPLivestream(strDID, 27, pushUUID);
				}
			}else if (did==R.id.ly_doorbell_cancel) {
				ly_doorbell_cancel.startAnimation(shake);   //闇囧姩
				VibratorUtil.Vibrate(ListeningPlayActivity.this, 100); // ??100ms
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() {
						returnLastBmp2Home();
						if (pushTypeInt != 3 && pushTypeInt != 4) {
							Intent intent = new Intent("back");
							sendBroadcast(intent);
						}
					};

					@Override
					protected Void doInBackground(Void... params) {
						if (myRender != null) {
							myRender.destroyShaders();
						}
						isTakeVideo = false;
						StopTalk();
						releaseTalk();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}

					protected void onPostExecute(Void result) {
						finish();
						overridePendingTransition(R.anim.out_to_right,
								R.anim.in_from_left);

					};
				}.execute();
			}
	   }
		return true;
}

	private View popv_group;
	private PopupWindow popupWindow_group;
	private Button btn_create, btn_cancel;
	private EditText editText_GroupName_pass;
	private Button button_1, button_2, button_3, button_4, button_5, button_6,
			button_7, button_8, button_9, button_0, button_reset, button_delet;

	public void initExitPopupWindow_Group() {
		stringBuffer = new StringBuffer();
		LayoutInflater li = LayoutInflater.from(this);
		popv_group = li.inflate(R.layout.popup_create_did, null);
		btn_create = (Button) popv_group
				.findViewById(R.id.popup_create_group_create);
		btn_cancel = (Button) popv_group
				.findViewById(R.id.popup_create_group_cancel);
		editText_GroupName_pass = (EditText) popv_group
				.findViewById(R.id.popup_create_group_edittext_pass);
		button_1 = (Button) popv_group.findViewById(R.id.button_1);
		button_1.setOnClickListener(this);

		button_2 = (Button) popv_group.findViewById(R.id.button_2);
		button_2.setOnClickListener(this);

		button_3 = (Button) popv_group.findViewById(R.id.button_3);
		button_3.setOnClickListener(this);

		button_4 = (Button) popv_group.findViewById(R.id.button_4);
		button_4.setOnClickListener(this);

		button_5 = (Button) popv_group.findViewById(R.id.button_5);
		button_5.setOnClickListener(this);

		button_6 = (Button) popv_group.findViewById(R.id.button_6);
		button_6.setOnClickListener(this);

		button_7 = (Button) popv_group.findViewById(R.id.button_7);
		button_7.setOnClickListener(this);

		button_8 = (Button) popv_group.findViewById(R.id.button_8);
		button_8.setOnClickListener(this);

		button_9 = (Button) popv_group.findViewById(R.id.button_9);
		button_9.setOnClickListener(this);

		button_0 = (Button) popv_group.findViewById(R.id.button_0);
		button_0.setOnClickListener(this);

		button_reset = (Button) popv_group.findViewById(R.id.button_reset);
		button_reset.setOnClickListener(this);

		button_delet = (Button) popv_group.findViewById(R.id.button_delet);
		button_delet.setOnClickListener(this);

		btn_create.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		popupWindow_group = new PopupWindow(popv_group,
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		// popupWindow_group.setAnimationStyle(R.style.AnimationPreview);
		popupWindow_group.setFocusable(true);
		popupWindow_group.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		popupWindow_group
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popupWindow_group.setOutsideTouchable(true);
		popupWindow_group.setBackgroundDrawable(new ColorDrawable(0));
		// popupWindow.update();
		popupWindow_group
				.setOnDismissListener(new PopupWindow.OnDismissListener() {

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						popupWindow_group.dismiss();
					}
				});
		popupWindow_group.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow_group.dismiss();
				}
				return false;
			}
		});
	}

	private void UpdataBitmp(String did, Bitmap bitmap) {
		for (int i = 0; i < SystemValue.arrayList.size(); i++) {
			if (did != null
					&& did.equals(SystemValue.arrayList.get(i).getDid())) {
				SystemValue.arrayList.get(i).setBmp(bitmap);
			}
		}
	}

	private int resettime = 30;

	@Override
	public void callBackDoorBellSystemParm(String did, int bell_on,
			int bell_audio, int bell_mode, int max_watch, int max_talk,
			int max_wait) {
		// TODO Auto-generated method stub
		Log.e("test", "callBackDoorBellSystemParm: did:" + did + "  bell_on:"
				+ bell_on + "   bell_audio:" + bell_audio + "  bell_mode:"
				+ bell_mode + "  max_watch:" + max_watch + "  max_talk:"
				+ max_talk + "  max_wait:" + max_wait);
		if (pushTypeInt == 3 || pushTypeInt == 4) {
			cancelTime = max_watch;
			resettime = max_watch;
		} else {
			cancelTime = max_talk;
			resettime = max_talk;
		}
	}

	@Override
	public void CallBackAlermType(String did, int type) {
		// TODO Auto-generated method stub
		Log.e("test", "CallBackAlermType:" + type);
		if (!did.equals(strDID)) {
			return;
		}
		if (type == 7) {
			mHandler.sendEmptyMessage(RESETTIME);
		}
		if (type == 3) {
			mHandler.sendEmptyMessage(2587);
		}
		// else if (type == 6) {
		// mHandler.sendEmptyMessage(CANCELTIMEFINISH);
		// }
	}

	private StringBuffer stringBuffer;

	@Override
	public void onClick(View arg0) {VibratorUtil.Vibrate(ListeningPlayActivity.this, 50);
	
	int id=arg0.getId();
	if (id==R.id.button_reset) {
		stringBuffer.replace(0, stringBuffer.length(), "");
		editText_GroupName_pass.setText(stringBuffer.toString().trim());
	}else if (id==R.id.button_delet) {
		if (stringBuffer.length() - 1 >= 0) {
			stringBuffer.delete(stringBuffer.length() - 1,
					stringBuffer.length());
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}
	}else if (id==R.id.button_1) {
		if (stringBuffer.length() < 6) {
			stringBuffer.append("1");
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}
	}else if (id==R.id.button_2) {
		if (stringBuffer.length() < 6) {
			stringBuffer.append("2");
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}
	}else if (id==R.id.button_3) {
		if (stringBuffer.length() < 6) {
			stringBuffer.append("3");
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}
	}else if (id==R.id.button_4) {
		if (stringBuffer.length() < 6) {
			stringBuffer.append("4");
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}
	}else if (id==R.id.button_5) {
		if (stringBuffer.length() < 6) {
			stringBuffer.append("5");
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}
	}else if (id==R.id.button_6) {
		if (stringBuffer.length() < 6) {
			stringBuffer.append("6");
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}
	}else if (id==R.id.button_7) {
		if (stringBuffer.length() < 6) {
			stringBuffer.append("7");
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}

	}else if (id==R.id.button_8) {
		if (stringBuffer.length() < 6) {
			stringBuffer.append("8");
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}

	}else if (id== R.id.button_9) {
		if (stringBuffer.length() < 6) {
			stringBuffer.append("9");
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}

	}else if (id==R.id.button_0) {
		if (stringBuffer.length() < 6) {
			stringBuffer.append("0");
			editText_GroupName_pass.setText(stringBuffer.toString().trim());
		}
	}else if (id==R.id.popup_create_group_create) {
		String pwd11 = editText_GroupName_pass.getText().toString().trim();
		if (pwd11.length() < 6) {
			showToast(R.string.lock_pwd_setting_show4);
			return;
		}
		if (!lockPwd.equals(pwd11)) {
			showToast(R.string.lock_pwd_setting_show7);
			return;
		}
		if (strDID != null && strDID.length() > 3) {

			NativeCaller.StartPPPPLivestream(strDID, 23, pushUUID);
		}
		popupWindow_group.dismiss();
	}else if (id==R.id.popup_create_group_cancel) {
		popupWindow_group.dismiss();
	}
	
	
	
//	switch (arg0.getId()) {
//	// button_1, button_2, button_3, button_4, button_5, button_6,
//	// button_7, button_8, button_9, button_0, button_reset, button_delet;
//	case R.id.button_reset:
//		stringBuffer.replace(0, stringBuffer.length(), "");
//		editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		break;
//	case R.id.button_delet:
//		if (stringBuffer.length() - 1 >= 0) {
//			stringBuffer.delete(stringBuffer.length() - 1,
//					stringBuffer.length());
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//		break;
//	case R.id.button_1:
//		if (stringBuffer.length() < 6) {
//			stringBuffer.append("1");
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//		break;

//	case R.id.button_2:
//		if (stringBuffer.length() < 6) {
//			stringBuffer.append("2");
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//		break;

//	case R.id.button_3:
//		if (stringBuffer.length() < 6) {
//			stringBuffer.append("3");
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//
//		break;

//	case R.id.button_4:
//		if (stringBuffer.length() < 6) {
//			stringBuffer.append("4");
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//
//		break;

//	case R.id.button_5:
//		if (stringBuffer.length() < 6) {
//			stringBuffer.append("5");
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//		break;

//	case R.id.button_6:
//		if (stringBuffer.length() < 6) {
//			stringBuffer.append("6");
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//
//		break;

//	case R.id.button_7:
//		if (stringBuffer.length() < 6) {
//			stringBuffer.append("7");
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//
//		break;

//	case R.id.button_8:
//		if (stringBuffer.length() < 6) {
//			stringBuffer.append("8");
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//
//		break;

//	case R.id.button_9:
//		if (stringBuffer.length() < 6) {
//			stringBuffer.append("9");
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//
//		break;

//	case R.id.button_0:
//		if (stringBuffer.length() < 6) {
//			stringBuffer.append("0");
//			editText_GroupName_pass.setText(stringBuffer.toString().trim());
//		}
//
//		break;

//	case R.id.popup_create_group_create:
//		String pwd11 = editText_GroupName_pass.getText().toString().trim();
//		if (pwd11.length() < 6) {
//			showToast(R.string.lock_pwd_setting_show4);
//			return;
//		}
//		if (!lockPwd.equals(pwd11)) {
//			showToast(R.string.lock_pwd_setting_show7);
//			return;
//		}
//		if (strDID != null && strDID.length() > 3) {
//
//			NativeCaller.StartPPPPLivestream(strDID, 23, pushUUID);
//		}
//		popupWindow_group.dismiss();
//		break;
//	case R.id.popup_create_group_cancel:
//		popupWindow_group.dismiss();
//		break;
//	default:
//		break;
//	}
	}

	// @Override
	// public void onSensorChanged(SensorEvent event) {
	// // TODO Auto-generated method stub
	// float range = event.values[0];
	// if (range == mSensor.getMaximumRange()) {
	// Toast.makeText(this, "????", Toast.LENGTH_SHORT).show();
	// //audioManager.setMode(AudioManager.MODE_NORMAL);
	// audioManager.setSpeakerphoneOn(true);
	// audioManager.setMode(AudioManager.STREAM_MUSIC);
	// } else {
	// Toast.makeText(this, "?????", Toast.LENGTH_SHORT).show();
	// audioManager.setSpeakerphoneOn(false);
	// audioManager.setMode(AudioManager.MODE_IN_CALL);
	// }
	// }
	//
	// @Override
	// public void onAccuracyChanged(Sensor sensor, int accuracy) {
	// // TODO Auto-generated method stub
	//
	// }
}