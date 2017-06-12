package object.dbnewgo.client;

import java.io.File;
import java.util.List;

import object.dbnewgo.client.other.FindImage;
import object.p2pipcam.adapter.AlarmLogAdapter;
import object.p2pipcam.bean.AlarmLogBean;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.DataBaseHelper;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AlarmLogActivity extends BaseActivity implements OnClickListener {
	private Button btnBack;
	private ListView listView;
	private DataBaseHelper helper = null;
	private AlarmLogAdapter adapter;
	private String did;
	private String camName;
	private TextView tvNoLog;
	private int play_tag_log = 0;
	private LinearLayout linearLayout_buttom;
	private Button button_cancel, button_ok;
	private TextView tv_camera_setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alarmlog);
		// setEdgeFromLeft();
		findView();
		setListener();
		helper = DataBaseHelper.getInstance(this);
		adapter = new AlarmLogAdapter(this, this);
		listView.setAdapter(adapter);
		getDataFromOther();
		if (adapter.getCount() > 0) {
			listView.setVisibility(View.VISIBLE);
			tvNoLog.setVisibility(View.GONE);
		} else {
			listView.setVisibility(View.GONE);
			tvNoLog.setVisibility(View.VISIBLE);
		}
		if (play_tag_log == 1) {
			linearLayout_buttom.setVisibility(View.VISIBLE);
			btnBack.setVisibility(View.GONE);
		}
		initExitPopupWindow_re();
		tv_camera_setting.setText(camName + " "
				+ getResources().getString(R.string.alerm_log));
	}

	// 07-09 09:30:40.609: D/tag(1379): createTime:2014-07-09 09:13:13

	public void showPic(int position, String name, String con, String time,
			String name1) {
		name1 = name1.replace(" ", "_").replace("-", "_").replace(":", "_");
		String pathTest = Environment.getExternalStorageDirectory() + "/"
				+ ContentCommon.SDCARD_PATH + "/picVisitor/" + name1 + ".jpg";
		if (con.equals("12") || con.equals("22")) {
			con = getResources().getString(R.string.doorbell_alerm_ok);
		} else if (con.equals("13") || con.equals("23")) {
			con = getResources().getString(R.string.doorbell_alerm_no);
		} else if (con.equals("14") || con.equals("24")) {
			con = getResources().getString(R.string.doorbell_listing_other);
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			Bitmap bitmap = BitmapFactory.decodeFile(pathTest, options);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			} else {
				imageView.setImageResource(R.drawable.vidicon);
			}

		} catch (Exception e) {
			// TODO: handle exception
			imageView.setImageResource(R.drawable.vidicon);
		}
		textView.setText(name + "  " + time + "  " + con);
		popupWindow_re.showAtLocation(btnBack, Gravity.CENTER, 0, 0);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// showAll(btnBack, "AlarmLogActivity");
		super.onResume();
	}

	private View popv_re;
	private PopupWindow popupWindow_re;
	private TextView textView;
	private ImageView imageView;
	private FrameLayout framelayout;

	public void initExitPopupWindow_re() {
		LayoutInflater li = LayoutInflater.from(this);
		popv_re = li.inflate(R.layout.popup_list, null);
		textView = (TextView) popv_re.findViewById(R.id.textView);
		imageView = (ImageView) popv_re.findViewById(R.id.imageView);
		framelayout = (FrameLayout) popv_re.findViewById(R.id.framelayout);
		popupWindow_re = new PopupWindow(popv_re,
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		popupWindow_re.setAnimationStyle(R.style.AnimationPreview);
		popupWindow_re.setFocusable(true);
		popupWindow_re.setOutsideTouchable(true);
		popupWindow_re.setBackgroundDrawable(new ColorDrawable(0));
		// popupWindow.update();
		imageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Log.d("test", "shix_image_ontou");
				popupWindow_re.dismiss();
				return false;
			}
		});
		popupWindow_re
				.setOnDismissListener(new PopupWindow.OnDismissListener() {

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						popupWindow_re.dismiss();
					}
				});
		popupWindow_re.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow_re.dismiss();
				} else {
					// popupWindow_re.dismiss();
				}
				return false;
			}
		});
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		did = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		camName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
		play_tag_log = intent.getIntExtra("play_tag_log", 0);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				initData();
			}
		});

	}

	private void initData() {
		adapter.clearAllAlarmLog();
		Cursor cursor = helper.queryAllAlarmLog(did);
		int count = 0;
		int countDele = 0;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				count++;
				if (count <= 30) {
					String createTime = cursor.getString(cursor
							.getColumnIndex(DataBaseHelper.KEY_CREATETIME));
					String content = cursor
							.getString(cursor
									.getColumnIndex(DataBaseHelper.KEY_ALARMLOG_CONTENT));
					Log.d("tag", "createTime:" + createTime);
					AlarmLogBean alarmLogBean = new AlarmLogBean();
					alarmLogBean.setContent(content);
					alarmLogBean.setCreatetime(createTime);
					alarmLogBean.setCamName(camName);
					adapter.addAlarmLog(alarmLogBean);
				} else {
					String createTime = cursor.getString(cursor
							.getColumnIndex(DataBaseHelper.KEY_CREATETIME));
					String content = cursor
							.getString(cursor
									.getColumnIndex(DataBaseHelper.KEY_ALARMLOG_CONTENT));
					helper.delAlarmLog(did, createTime);
					String name1 = createTime;
					name1 = name1.replace(" ", "_").replace("-", "_")
							.replace(":", "_");
					File delFile = new File(
							Environment.getExternalStorageDirectory() + "/"
									+ ContentCommon.SDCARD_PATH
									+ "/picVisitor/" + name1 + ".jpg");
					if (delFile.exists()) {
						boolean b = delFile.delete();
						Log.e("tagdel", "if delect====" + b);
					} else {
						Log.e("tagdel", "if delect====no");
					}
					countDele++;
					if (countDele == 3) {
						break;
					}

				}
			}
			cursor.close();
			adapter.notifyDataSetChanged();
		}
	}

	private void setListener() {
		btnBack.setOnClickListener(this);
		button_cancel.setOnClickListener(this);
		button_ok.setOnClickListener(this);
	}

	private void findView() {
		btnBack = (Button) findViewById(R.id.back);
		listView = (ListView) findViewById(R.id.listView1);
		tvNoLog = (TextView) findViewById(R.id.no_log);
		button_cancel = (Button) findViewById(R.id.aler_cancel);
		button_ok = (Button) findViewById(R.id.aler_ok);
		linearLayout_buttom = (LinearLayout) findViewById(R.id.alarm_bottom_layout);
		tv_camera_setting = (TextView) findViewById(R.id.tv_camera_setting);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		getDataFromOther();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter = null;
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(1514);
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.back:
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//			break;
//		case R.id.aler_cancel:
//			NotificationManager notificationManager = (NotificationManager) this
//					.getSystemService(NOTIFICATION_SERVICE);
//			notificationManager.cancel(1514);
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//			break;
//		case R.id.aler_ok:
//			if (SystemValue.ISRUN == false) {
//				showToastLong(R.string.aler_ok_show);
//			} else {
//				NotificationManager notificationManager1 = (NotificationManager) this
//						.getSystemService(NOTIFICATION_SERVICE);
//				notificationManager1.cancel(1514);
//				finish();
//				Intent in = new Intent(AlarmLogActivity.this,
//						ListeningPlayActivity.class);
//				in.putExtra(ContentCommon.STR_CAMERA_TYPE,
//						ContentCommon.CAMERA_TYPE_MJPEG);
//				in.putExtra(ContentCommon.STR_STREAM_TYPE,
//						ContentCommon.MJPEG_SUB_STREAM);
//				in.putExtra(ContentCommon.STR_CAMERA_NAME, camName);
//				in.putExtra(ContentCommon.STR_CAMERA_ID, did);
//				in.putExtra(ContentCommon.STR_CAMERA_USER, retrunUser(did));
//				in.putExtra(ContentCommon.STR_CAMERA_PWD, retrunUserPWD(did));
//				in.putExtra(ContentCommon.STR_CAMERA_TYPE, 0);
//				startActivity(in);
//				overridePendingTransition(R.anim.in_from_right,
//						R.anim.out_to_left);
//
//			}
//			break;
//		default:
//			break;
//		}
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
}
