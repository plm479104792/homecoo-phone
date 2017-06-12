package object.dbnewgo.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import object.dbnewgo.client.BridgeService.DoorBellAlermPush;
import object.dbnewgo.client.BridgeService.SnapShotInterface;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.DataBaseHelper;
import object.p2pipcam.utils.VibratorUtil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListeningActivity extends BaseActivity implements
		SnapShotInterface, OnClickListener, DoorBellAlermPush {
	private SharedPreferences preAudio;

	private ImageView imgView_getup_arrow;
	private AnimationDrawable animArrowDrawable = null;
	private ImageView imgView_getup_arrow1;
	private AnimationDrawable animArrowDrawable1 = null;
	private SharedPreferences preuser;
	private String loginUser, loginPwd;
	private String pushID, pushName, pushTime;
	private Intent intent;
	private ImageView imageView;
	private Bitmap bmp;
	private LinearLayout linearLayout1, linearLayout3;
	private LinearLayout frameLayout;
	int windowsHeight, windowsWight;
	private ImageButton btn_doorbell_ok, btn_doorbell_no;
	int[] location2;
	private int padL, padT, padR, padB, wh;
	private MediaPlayer mediaPlayer;
	private TextView tv_doorbell_call_time;
	private boolean isRunTime = false;
	private final int TIMETAG = 110;
	private final int OTHERLIST = 111;
	private final int TIMEOUT = 112;
	private int it_time = 0;
	private DisplayMetrics metric;
	private DataBaseHelper helper = null;
	private TextView tv_doorbell_name;
	private int isOtherList = 0;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OTHERLIST:
				if (SystemValue.ISPLAY != 2) {
					showToastLong(R.string.doorbell_listing_other);
					if (mediaPlayer != null) {
						if (mediaPlayer.isPlaying()) {
							mediaPlayer.stop();
							try {
								mediaPlayer.prepare();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					SystemValue.ISPLAY = 0;
					SystemValue.isStartRun = false;
					if (pushID != null && pushID.length() > 4) {
						NativeCaller.StartPPPPLivestream(pushID, 25, pushPic);
					}
					helper.insertAlarmLogToDB(pushID, pushIsAlerm + "4",
							pushTime);
					Intent intent = new Intent("back");
					sendBroadcast(intent);
					ListeningActivity.this.finish();
				}

				break;
			case TIMETAG:
				if (isRunTime) {
					tv_doorbell_call_time.setText(getTimeStr(it_time));
				}
				if (it_time >= 59) {
					helper.insertAlarmLogToDB(pushID, pushIsAlerm + "3",
							pushTime);
					isRunTime = false;
					SystemValue.isStartRun = false;
					if (pushID != null && pushID.length() > 4) {
						NativeCaller.StartPPPPLivestream(pushID, 25, pushPic);
					}
					finish();
				}
				break;
			case TIMEOUT:
				helper.insertAlarmLogToDB(pushID, pushIsAlerm + "3", pushTime);
				isRunTime = false;
				SystemValue.isStartRun = false;
				if (pushID != null && pushID.length() > 4) {
					NativeCaller.StartPPPPLivestream(pushID, 25, pushPic);
				}
				finish();
				break;
			default:
				break;
			}
		}
	};

	private String getTimeStr(int time) {
		// TODO Auto-generated method stub
		if (time < 10) {
			return "00:0" + time;
		} else if (time < 60) {
			return "00:" + time;
		} else if (time >= 60 && time < 70) {
			return "01:0" + (time - 60);
		} else if (time >= 70) {
			return "01:" + (time - 60);
		}
		return "00:00";
	}

	class MyTimeThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (isRunTime) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				it_time++;
				mHandler.sendEmptyMessage(TIMETAG);
			}
			super.run();
		}
	}

	WakeLock mWakelock;
	private String doorbell_audio, doorbell_audio_alarm;
	private TextView tv_doorbell_type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDate();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.linsten);
		isOtherList = 0;
		preAudio = getSharedPreferences("shix_zhao_audio", Context.MODE_PRIVATE);
		doorbell_audio = preAudio.getString("doorbell_audio", "default");
		doorbell_audio_alarm = preAudio.getString("doorbell_audio_alarm",
				"default_alarm");

		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "SimpleTimer");
		imgView_getup_arrow = (ImageView) findViewById(R.id.getup_arrow);
		animArrowDrawable = (AnimationDrawable) imgView_getup_arrow
				.getBackground();
		imgView_getup_arrow1 = (ImageView) findViewById(R.id.getup_arrow1);
		animArrowDrawable1 = (AnimationDrawable) imgView_getup_arrow1
				.getBackground();
		imageView = (ImageView) findViewById(R.id.imageView);
		linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
		linearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);
		frameLayout = (LinearLayout) findViewById(R.id.frameLayout);
		btn_doorbell_ok = (ImageButton) findViewById(R.id.btn_doorbell_ok);
		btn_doorbell_no = (ImageButton) findViewById(R.id.btn_doorbell_no);
		tv_doorbell_call_time = (TextView) findViewById(R.id.tv_doorbell_call_time);
		tv_doorbell_name = (TextView) findViewById(R.id.tv_doorbell_name);
		tv_doorbell_type = (TextView) findViewById(R.id.tv_doorbell_type);
		tv_doorbell_name.setText(pushName);
		if (pushIsAlerm == 1) {
			tv_doorbell_type.setText(R.string.doorbell_fangke);
		} else {
			tv_doorbell_type.setText(R.string.doorbell_alerm);
		}
		// btn_doorbell_ok.setOnClickListener(this);
		// btn_doorbell_no.setOnClickListener(this);
		preuser = getSharedPreferences("shix_zhao_wifi", Context.MODE_PRIVATE);
		loginUser = preuser.getString("user", "admin");
		loginPwd = preuser.getString("pwd", "123456");
		BridgeService.setDoorBellAlermPush(this);
		BridgeService.setSnapShotInterface(this);
		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		windowsHeight = metric.heightPixels;
		windowsWight = metric.widthPixels;
		Log.d("test", "shix-windowsHeight:" + windowsHeight + " windowsWight:"
				+ windowsWight);
		wh = (int) (75 * metric.density);
		padT = (int) (windowsHeight - (100 * metric.density) - wh);
		padL = (int) (20 * metric.density);
		padR = (int) (windowsWight - wh - (20 * metric.density));
		padB = (int) (100 * metric.density);
		SystemValue.ISPLAY = 0;

		btn_doorbell_ok.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY;
			boolean isOne = false;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isOne = false;
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					VibratorUtil.Vibrate(ListeningActivity.this, 100);
					if (mediaPlayer != null) {
						if (mediaPlayer.isPlaying()) {
							mediaPlayer.stop();
							try {
								mediaPlayer.prepare();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					break;
				case MotionEvent.ACTION_MOVE:
					int rawX = (int) event.getRawX();
					int rawY = (int) event.getRawY();
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;
					if (dx > 10 || dy > 10 || dx < -10 || dy < -10) {
						btn_doorbell_ok.layout(rawX - v.getWidth() / 2, rawY
								- v.getHeight() / 2, rawX + v.getWidth() / 2,
								rawY + v.getHeight() / 2);
						if (!isOne) {
							imgView_getup_arrow.setVisibility(View.GONE);
							isOne = true;
						}

					}

					break;
				case MotionEvent.ACTION_UP:
					if (windowsWight <= 480) {
						imgView_getup_arrow.setVisibility(View.GONE);
					} else {
						imgView_getup_arrow.setVisibility(View.VISIBLE);
					}

					int dx1 = (int) event.getRawX() - lastX;
					int dy1 = (int) event.getRawY() - lastY;
					btn_doorbell_ok.layout(padL, padT, padL + wh, padT + wh);
					VibratorUtil.Vibrate(ListeningActivity.this, 100);
					if (dx1 > 50 || dy1 > 50 || dx1 < -50 || dy1 < -50) {
						helper.insertAlarmLogToDB(pushID, pushIsAlerm + "2",
								pushTime);
						SystemValue.ISPLAY = 2;
						if (isOtherList == 3) {
							SystemValue.ISPLAY = 0;
							SystemValue.isStartRun = false;
							if (pushID != null && pushID.length() > 4) {
								NativeCaller.StartPPPPLivestream(pushID, 25,
										pushPic);
							}
							helper.insertAlarmLogToDB(pushID,
									pushIsAlerm + "3", pushTime);
							Intent intent = new Intent("back");
							sendBroadcast(intent);
							ListeningActivity.this.finish();
						} else {
							Intent intent = new Intent(ListeningActivity.this,
									ListeningPlayActivity.class);
							intent.putExtra(ContentCommon.STR_CAMERA_ID, pushID);
							intent.putExtra(ContentCommon.STR_CAMERA_NAME,
									pushName);
							intent.putExtra("pushUUID", pushPic);
							intent.putExtra(ContentCommon.STR_CAMERA_USER,
									retrunUser(pushID));
							intent.putExtra(ContentCommon.STR_CAMERA_PWD,
									retrunUserPWD(pushID));
							intent.putExtra(ContentCommon.STR_CAMERA_TYPE, 0);
							if (pushIsAlerm == 1) {
								intent.putExtra("pushTypeInt", 2);
							} else {
								intent.putExtra("pushTypeInt", 3);
							}
							startActivity(intent);
							ListeningActivity.this.finish();
						}

					}
					break;
				default:
					break;
				}
				return false;
			}
		});

		btn_doorbell_no.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY;
			boolean isOne = false;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isOne = false;
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					VibratorUtil.Vibrate(ListeningActivity.this, 100);
					if (mediaPlayer != null) {
						if (mediaPlayer.isPlaying()) {
							mediaPlayer.stop();
							try {
								mediaPlayer.prepare();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					break;
				case MotionEvent.ACTION_MOVE:
					int rawX = (int) event.getRawX();
					int rawY = (int) event.getRawY();
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;
					if (dx > 10 || dy > 10 || dx < -10 || dy < -10) {
						btn_doorbell_no.layout(rawX - v.getWidth() / 2, rawY
								- v.getHeight() / 2, rawX + v.getWidth() / 2,
								rawY + v.getHeight() / 2);
						if (!isOne) {
							imgView_getup_arrow1.setVisibility(View.GONE);
							isOne = true;
						}

					}

					break;
				case MotionEvent.ACTION_UP:
					if (windowsWight <= 480) {
						imgView_getup_arrow1.setVisibility(View.GONE);
					} else {
						imgView_getup_arrow1.setVisibility(View.VISIBLE);
					}
					int dx1 = (int) event.getRawX() - lastX;
					int dy1 = (int) event.getRawY() - lastY;

					btn_doorbell_no.layout(padR, padT, padR + wh, padT + wh);
					VibratorUtil.Vibrate(ListeningActivity.this, 100);
					if (dx1 > 100 || dy1 > 100 || dx1 < -80 || dy1 < -80) {
						SystemValue.ISPLAY = 0;
						SystemValue.isStartRun = false;
						if (pushID != null && pushID.length() > 4) {
							NativeCaller.StartPPPPLivestream(pushID, 25,
									pushPic);
						}
						helper.insertAlarmLogToDB(pushID, pushIsAlerm + "3",
								pushTime);
						Intent intent = new Intent("back");
						sendBroadcast(intent);
						ListeningActivity.this.finish();
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		if (pushIsAlerm == 1) {
			// fangke doorbell_audio,doorbell_audio_alarm;
			if (doorbell_audio.equals("default")) {
				if (mediaPlayer != null) {
					mediaPlayer.stop();
					mediaPlayer.release();
					mediaPlayer = null;
				}
				mediaPlayer = MediaPlayer.create(this,
						R.raw.doorbell_shix_sound);
				mediaPlayer.start();
				mediaPlayer.setLooping(true);
			} else if (doorbell_audio.equals("no")) {

			} else {
				Log.d("test", "doorbell_audio:" + doorbell_audio);
				File file = new File(doorbell_audio);
				if (file.exists()) {
					if (mediaPlayer != null) {
						mediaPlayer.stop();
						mediaPlayer.release();
						mediaPlayer = null;
					}
					Uri uri = Uri.parse(doorbell_audio);
					mediaPlayer = new MediaPlayer();
					try {
						mediaPlayer.setDataSource(this, uri);
						final AudioManager audioManager = (AudioManager) this
								.getSystemService(Context.AUDIO_SERVICE);
						if (audioManager
								.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
							mediaPlayer
									.setAudioStreamType(AudioManager.STREAM_ALARM);
							mediaPlayer.setLooping(true);
						}
						mediaPlayer.prepare();
						mediaPlayer.start();

					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					if (mediaPlayer != null) {
						mediaPlayer.stop();
						mediaPlayer.release();
						mediaPlayer = null;
					}
					mediaPlayer = MediaPlayer.create(this,
							R.raw.doorbell_shix_sound);
					mediaPlayer.start();
					mediaPlayer.setLooping(true);
				}
			}
		} else {
			// alarm

			if (doorbell_audio_alarm.equals("default_alarm")) {
				if (mediaPlayer != null) {
					mediaPlayer.stop();
					mediaPlayer.release();
					mediaPlayer = null;
				}
				mediaPlayer = MediaPlayer.create(this, R.raw.alerm_sos);
				mediaPlayer.start();
				mediaPlayer.setLooping(true);
			} else if (doorbell_audio_alarm.equals("no_alarm")) {

			} else {
				File file = new File(doorbell_audio_alarm);
				if (file.exists()) {
					if (mediaPlayer != null) {
						mediaPlayer.stop();
						mediaPlayer.release();
						mediaPlayer = null;
					}
					Uri uri = Uri.parse(doorbell_audio_alarm);
					mediaPlayer = new MediaPlayer();
					try {
						mediaPlayer.setDataSource(this, uri);
						final AudioManager audioManager = (AudioManager) this
								.getSystemService(Context.AUDIO_SERVICE);
						if (audioManager
								.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
							mediaPlayer
									.setAudioStreamType(AudioManager.STREAM_ALARM);
							mediaPlayer.setLooping(true);
						}
						mediaPlayer.prepare();
						mediaPlayer.start();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {

					if (mediaPlayer != null) {
						mediaPlayer.stop();
						mediaPlayer.release();
						mediaPlayer = null;
					}
					mediaPlayer = MediaPlayer.create(this, R.raw.alerm_sos);
					mediaPlayer.start();
					mediaPlayer.setLooping(true);
				}

			}
		}

		isRunTime = true;
		new MyTimeThread().start();
		helper = DataBaseHelper.getInstance(this);
	}

	private void takePicture() {
		new Thread() {
			public void run() {
				savePicToSDcard();
			}
		}.start();
	}

	private synchronized void savePicToSDcard() {
		FileOutputStream fos = null;
		Log.d("test", "savePicToSDcard");
		try {
			File div = new File(Environment.getExternalStorageDirectory(),
					ContentCommon.SDCARD_PATH + "/picVisitor");
			if (!div.exists()) {
				div.mkdirs();
			}

			String name = pushTime;
			name = name.replace(" ", "_").replace("-", "_").replace(":", "_");
			File file = new File(div, name + ".jpg");
			fos = new FileOutputStream(file);
			if (bmp != null) {
				if (bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos)) {
					fos.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("test", "savePicToSDcard");
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
		}
	}

	private String retrunUser(String did) {

		for (int i = 0; i < SystemValue.arrayList.size(); i++) {
			if (did != null
					&& did.equals(SystemValue.arrayList.get(i).getDid())) {
				Log.d("tag", "testuser:"
						+ SystemValue.arrayList.get(i).getUser());
				return SystemValue.arrayList.get(i).getUser();
			}
		}
		return null;
	}

	private String retrunUserPWD(String did) {

		for (int i = 0; i < SystemValue.arrayList.size(); i++) {
			if (did != null
					&& did.equals(SystemValue.arrayList.get(i).getDid())) {
				Log.d("tag", "testuserpwd:"
						+ SystemValue.arrayList.get(i).getPwd());
				return SystemValue.arrayList.get(i).getPwd();
			}
		}
		return null;
	}

	int pushIsAlerm = 0;
	String pushPic;

	private void getDate() {
		// TODO Auto-generated method stub
		intent = getIntent();
		SystemValue.isStartRun = true;
		if (intent != null) {
			pushID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
			pushName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
			pushTime = intent.getStringExtra("pushDate");
			pushIsAlerm = intent.getIntExtra("pushIsAlerm", 1);
			pushPic = intent.getStringExtra("pushPic");
			if (pushID != null && pushID.length() > 4 && pushPic != null
					&& pushPic.length() > 4) {
				NativeCaller.SnapShot(pushID, pushPic);
			}
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// NotificationManager notificationManager = (NotificationManager) this
		// .getSystemService(NOTIFICATION_SERVICE);
		// notificationManager.cancel(1515);
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		animArrowDrawable.stop();
		animArrowDrawable1.stop();
	}

	private Runnable AnimationDrawableTask = new Runnable() {

		public void run() {
			animArrowDrawable.start();
			animArrowDrawable1.start();
			mHandler.postDelayed(AnimationDrawableTask, 300);
		}
	};

	// KeyguardLock keyguardLock = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(AnimationDrawableTask, 300);
		// ��̬�ı䲼��
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				windowsWight, windowsWight * 3 / 4);
		lp.gravity = Gravity.CENTER;
		frameLayout.setLayoutParams(lp);

		LayoutParams params1 = linearLayout3.getLayoutParams();
		params1.height = (windowsHeight / 7) * 3;
		params1.width = windowsWight;
		int h = metric.heightPixels - (int) (117 * metric.density)
				- windowsWight * 3 / 4 - (int) (25 * metric.density);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				windowsWight, h);
		linearLayout3.setLayoutParams(lp1);
		RelativeLayout.LayoutParams btnLpOk = new RelativeLayout.LayoutParams(
				(int) (75 * metric.density), (int) (75 * metric.density));
		btnLpOk.leftMargin = padL;
		btnLpOk.rightMargin = padR;
		btnLpOk.topMargin = padT;
		btnLpOk.bottomMargin = padB - (int) (25 * metric.density);
		Log.d("test", "padL:" + padL + "  padT:" + padT + "  padR:" + padR
				+ "  padB:" + padB);
		btn_doorbell_ok.setLayoutParams(btnLpOk);
		RelativeLayout.LayoutParams btnLpNo = new RelativeLayout.LayoutParams(
				(int) (75 * metric.density), (int) (75 * metric.density));
		btnLpNo.leftMargin = padR;
		btnLpNo.rightMargin = padL;
		btnLpNo.topMargin = padT;
		btnLpNo.bottomMargin = padB - (int) (25 * metric.density);
		btn_doorbell_no.setLayoutParams(btnLpNo);
		RelativeLayout.LayoutParams imgOk = new RelativeLayout.LayoutParams(
				(int) (70 * metric.density), (int) (20 * metric.density));
		imgOk.leftMargin = padL + (int) (85 * metric.density);
		imgOk.rightMargin = padR - (int) (85 * metric.density);
		imgOk.topMargin = padT + (int) (55 * metric.density) / 2;
		imgView_getup_arrow.setLayoutParams(imgOk);
		RelativeLayout.LayoutParams imgNo = new RelativeLayout.LayoutParams(
				(int) (70 * metric.density), (int) (25 * metric.density));
		imgNo.rightMargin = padL + (int) (85 * metric.density);
		imgNo.leftMargin = padR - (int) (85 * metric.density);
		imgNo.topMargin = padT + (int) (50 * metric.density) / 2;
		imgView_getup_arrow1.setLayoutParams(imgNo);
		VibratorUtil.Vibrate(ListeningActivity.this, 1000);
		final Window win = this.getWindow();
		final WindowManager.LayoutParams params = win.getAttributes();
		params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
		mWakelock.acquire();
		if (windowsWight <= 480) {
			imgView_getup_arrow.setVisibility(View.GONE);
			imgView_getup_arrow1.setVisibility(View.GONE);
		}
		// KeyguardManager keyguardManager = (KeyguardManager)
		// getSystemService(KEYGUARD_SERVICE);
		// keyguardLock = keyguardManager.newKeyguardLock("test");
		if (MainActivity.keyguardLock != null) {
			MainActivity.keyguardLock.disableKeyguard();
		}
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		BridgeService.setDoorBellAlermPush(null);
		BridgeService.setSnapShotInterface(null);
		isOtherList = 0;
		// if (keyguardLock != null) {
		// keyguardLock.reenableKeyguard();
		// }
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				try {
					mediaPlayer.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (bmp != null) {
			bmp.recycle();
			bmp = null;
		}
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		mWakelock.release();
		loginUser = preuser.getString("user", "admin");
		loginPwd = preuser.getString("pwd", "123456");
		isRunTime = false;
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Handler BitMapHandler = new Handler() {
		public void handleMessage(Message msg) {
			imageView.setImageBitmap(bmp);
			takePicture();
		}
	};

	@Override
	public void BSSnapshot(String did, byte[] bImage, int len) {
		// TODO Auto-generated method stub
		Log.d("test", "shix-len:" + len);
		bmp = BitmapFactory.decodeByteArray(bImage, 0, len);
		BitMapHandler.sendEmptyMessage(110);
	}

	@Override
	public void onClick(View v) {
		
		int id=v.getId();
		if (id==R.id.btn_doorbell_no) {
			location2 = new int[2];
			btn_doorbell_ok.getLocationOnScreen(location2);
			Log.d("test", "l:" + location2[0] + "   t1:" + location2[1]
					+ "  r1:" + (location2[0] + btn_doorbell_ok.getWidth())
					+ "  b1" + (location2[1] + btn_doorbell_ok.getHeight()));
		}
		
//		switch (v.getId()) {
		// case R.id.btn_doorbell_ok:
		// Intent intent = null;
		// intent = new Intent();
		// intent.setClass(getApplicationContext(), PlayActivitySport.class);
		// intent.putExtra(ContentCommon.STR_CAMERA_ID, pushID);
		// intent.putExtra(ContentCommon.STR_CAMERA_NAME, pushName);
		// startActivity(intent);
		// break;
		/*******20151211*******/
//		case R.id.btn_doorbell_no:
//			location2 = new int[2];
//			btn_doorbell_ok.getLocationOnScreen(location2);
//			Log.d("test", "l:" + location2[0] + "   t1:" + location2[1]
//					+ "  r1:" + (location2[0] + btn_doorbell_ok.getWidth())
//					+ "  b1" + (location2[1] + btn_doorbell_ok.getHeight()));
//			break;
//		default:
//			break;
//		}
	}

	@Override
	public void CallBackAlermType(int type) {
		// TODO Auto-generated method stub
		if (type == 3) {
			mHandler.sendEmptyMessage(OTHERLIST);
		} else if (type == 4) {
			mHandler.sendEmptyMessage(TIMEOUT);
		}
	}

	@Override
	public void CallBackDoorbellType(String uuid, int type) {
		// TODO Auto-generated method stub
		Log.d("test", "zhaogenghuais uuid:" + uuid + "   type:" + type);
		isOtherList = type;
		if (pushID != null && pushID.length() > 4) {
			NativeCaller.StartPPPPLivestream(pushID, 25, uuid);
		}
	}
}