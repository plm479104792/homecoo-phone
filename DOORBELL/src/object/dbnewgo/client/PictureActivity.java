package object.dbnewgo.client;

import java.io.File;

import object.p2pipcam.adapter.PictureActivityAdapter;
import object.p2pipcam.bean.CameraParamsBean;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.DataBaseHelper;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ����Tab�е�ͼƬ����������ֻ�ͼƬ��Զ��ͼƬ
 * */
public class PictureActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	private Button bntPhone;
	private Button bntRemote;
	private ListView listView;
	private MyStatusBroadCast broadcast;
	private PictureActivityAdapter mAdapter;
	private DataBaseHelper helper;
	private boolean isFirst = true;
	private int timeTag = 0;
	private int timeOne = 0;
	private int timeTwo = 0;
	// private View view = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 2:
				mAdapter.setOver(true);
				break;
			case 3:
				// ֪ͨ�����棬���ı�ý�������״̬
				Intent intent = new Intent("other");
				sendBroadcast(intent);
				return;
			}
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("tag", "PictureActivity  onCreate");
		setContentView(R.layout.pictureactivity);
		findView();
		setListener();
		helper = DataBaseHelper.getInstance(this);
		// initCamera();

		mAdapter = new PictureActivityAdapter(this, SystemValue.arrayList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		handler.sendEmptyMessageDelayed(3, 1000);

		broadcast = new MyStatusBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("camera_status_change");
		registerReceiver(broadcast, filter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (SystemValue.arrayList.size() == 0) {
			listView.setVisibility(View.GONE);
		} else {
			listView.setVisibility(View.VISIBLE);
		}
		if (hasSdcard()) {
			initBmpAndSum();
		} else {
			showToast(R.string.local_picture_show_sd);
		}
	}

	private void initBmpAndSum() {
		new Thread() {
			public void run() {
				synchronized (PictureActivity.this) {

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < SystemValue.arrayList.size(); i++) {
						CameraParamsBean bean = SystemValue.arrayList.get(i);
						String did = bean.getDid();
						Cursor cursor = helper.queryAllPicture(did);
						int sum = cursor.getCount();
						Bitmap bitmap = null;
						isFirst = true;
						while (cursor.moveToNext()) {
							String filePath = cursor
									.getString(cursor
											.getColumnIndex(DataBaseHelper.KEY_FILEPATH));
							File file = new File(filePath);
							if (file == null || !file.exists()) {
								sum--;
								boolean delResult = helper
										.deleteVideoOrPicture(did, filePath,
												DataBaseHelper.TYPE_PICTURE);
								Log.d("tag", "delResult:" + delResult);
							} else {
								if (isFirst) {
									BitmapFactory.Options options = new BitmapFactory.Options();
									options.inSampleSize = 5;
									bitmap = BitmapFactory.decodeFile(filePath,
											options);
									isFirst = false;
								}
							}
						}
						if (cursor != null) {
							cursor.close();
						}
						bean.setBmp(bitmap);
						bean.setSum_pic(sum);
						handler.sendEmptyMessage(1);
					}
					handler.sendEmptyMessage(2);

				}
			}
		}.start();
	}

	private void setListener() {
		bntPhone.setOnClickListener(this);
		bntRemote.setOnClickListener(this);
	}

	private void findView() {
		bntPhone = (Button) findViewById(R.id.picture_phone);
		bntRemote = (Button) findViewById(R.id.picture_remote);
		listView = (ListView) findViewById(R.id.piclistview);
		TextView tv = (TextView) findViewById(R.id.tv_phonepic);
		// view = LayoutInflater.from(this).inflate(
		// R.layout.check_all_loct_pict_head, null);
		// listView.addHeaderView(view);

		Button btn1 = (Button) findViewById(R.id.btn1);
		Button btn2 = (Button) findViewById(R.id.btn2);
		bntPhone.setVisibility(View.GONE);
		bntRemote.setVisibility(View.GONE);
		btn1.setVisibility(View.GONE);
		btn2.setVisibility(View.GONE);
		tv.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.picture_phone:
////			bntPhone.setBackgroundResource(R.drawable.checktopleft_pressed);
////			bntRemote.setBackgroundResource(R.drawable.checkright_normal);
//			mAdapter.setMode(mAdapter.PHONE);
//			break;
//		case R.id.picture_remote:
////			bntRemote.setBackgroundResource(R.drawable.checkright_pressed);
////			bntPhone.setBackgroundResource(R.drawable.checkleft_normal);
//			mAdapter.setMode(mAdapter.REMOTE);
//			break;
//		default:
//			break;
//		}
//		mAdapter.notifyDataSetChanged();
	}

	// ע��㲥�����������״̬�ĸı�
	private class MyStatusBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("camera_status_change".equals(action)) {
				mAdapter.notifyDataSetChanged();
			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		// if (position == 0) {
		// Intent intent = new Intent(PictureActivity.this,
		// AllPicterCheckActivity.class);
		// startActivity(intent);
		// overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		// return;
		// }
		CameraParamsBean bean = SystemValue.arrayList.get(position);
		String did = bean.getDid();
		String name = bean.getName();
		int status = bean.getStatus();
		int mode = mAdapter.getMode();
		switch (mode) {
		case 1:// �ֻ�
			Intent intentpic = new Intent(this, LocalPictureListActivity.class);
			intentpic.putExtra(ContentCommon.STR_CAMERA_NAME, name);
			intentpic.putExtra(ContentCommon.STR_CAMERA_ID, did);
			startActivity(intentpic);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case 2:// Զ��
			if (status == ContentCommon.PPPP_STATUS_ON_LINE) {

			} else {
				showToast(R.string.remote_pic_offline);
			}
			break;

		default:
			break;

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Intent intent = new Intent(ContentCommon.MAIN_KEY_MENU);
			sendBroadcast(intent);
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(ContentCommon.MAIN_KEY_BACK);
			sendBroadcast(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcast);

	}
}
