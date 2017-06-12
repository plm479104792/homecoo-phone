package object.dbnewgo.client;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import object.dbnewgo.client.other.AllVideoCheckActivity;
import object.p2pipcam.adapter.VideoActivityAdapter;
import object.p2pipcam.bean.CameraParamsBean;
import object.p2pipcam.bean.MovieInfo;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.system.SystemValue;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class VideoActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	private Button bntPhone;
	private Button bntRemote;
	private ListView listView;
	// private View view = null;
	private MyStatusBroadCast broadcast;
	private VideoActivityAdapter mAdapter;

	private int timeTag = 0;
	private int timeOne = 0;
	private int timeTwo = 0;
	private int tag = 0;
	private List<MovieInfo> playList = null;
	private Map<String, List<MovieInfo>> maps = null;

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
			default:
				break;
			}
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("TAG", "onCreate");
		setContentView(R.layout.videosactivity);

		findView();
		setListener();
		mAdapter = new VideoActivityAdapter(this, SystemValue.arrayList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		handler.sendEmptyMessageDelayed(3, 1000);
		broadcast = new MyStatusBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("camera_status_change");
		registerReceiver(broadcast, filter);
		SystemValue.checkSDStatu = 1;

	}

	@Override
	protected void onResume() {
		// Log.d("TAG", "onResume()");
		// Log.d("TAG", "SystemValue.checkSDStatu==" +
		// SystemValue.checkSDStatu);
		if (SystemValue.arrayList.size() == 0) {
			listView.setVisibility(View.GONE);
		} else {
			listView.setVisibility(View.VISIBLE);
		}
		if (SystemValue.checkSDStatu == 1) {
			new AsyncTask<Void, Void, Void>() {
				protected void onPreExecute() {
					playList = new ArrayList<MovieInfo>();
					maps = new HashMap<String, List<MovieInfo>>();
				};

				@Override
				protected Void doInBackground(Void... params) {
					File div = new File(
							Environment.getExternalStorageDirectory(),
							ContentCommon.SDCARD_PATH + "/video");
					if (!div.exists()) {
						div.mkdirs();
					}
					getVideoFile(div);
					for (int i = 0; i < SystemValue.arrayList.size(); i++) {
						List<MovieInfo> infos = new ArrayList<MovieInfo>();
						for (int j = 0; j < playList.size(); j++) {
							if (SystemValue.arrayList.get(i).getDid()
									.equals(playList.get(j).getCamerName())) {
								infos.add(playList.get(j));
							}
						}
						SystemValue.arrayList.get(i).setSum(infos.size());
						maps.put(SystemValue.arrayList.get(i).getDid(), infos);
						Log.d("TAG", infos.size() + "");
					}
					return null;
				}

				protected void onPostExecute(Void result) {
					SystemValue.checkSDStatu = 0;
					mAdapter.notifyDataSetChanged();
					if (playList.size() == 0) {
						Toast.makeText(
								VideoActivity.this,
								getResources().getString(
										R.string.no_videoing_file), 0).show();
					}

					for (int i = 0; i < playList.size(); i++) {
						Log.d("TAG", "camerName="
								+ playList.get(i).getCamerName()
								+ "   videoName="
								+ playList.get(i).getVideoName() + "  path="
								+ playList.get(i).getPath());
					}

				};
			}.execute();

		}
		super.onResume();
	}

	private void setListener() {
		bntPhone.setOnClickListener(this);
		bntRemote.setOnClickListener(this);
	}

	private void findView() {

		bntPhone = (Button) findViewById(R.id.picture_phone);
		bntRemote = (Button) findViewById(R.id.picture_remote);
		listView = (ListView) findViewById(R.id.piclistview);
		// view = LayoutInflater.from(this).inflate(
		// R.layout.check_all_loct_video_head, null);
		// listView.addHeaderView(view);

	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.picture_phone:
//			// if (tag == 1) {
//			// view.setPadding(0, 1 * view.getHeight(), 0, 0);
//			// view.setVisibility(View.VISIBLE);
//			// }
//			tag = 0;
////			bntPhone.setBackgroundResource(R.drawable.checktopleft_pressed);
////			bntRemote.setBackgroundResource(R.drawable.checkright_normal);
//			mAdapter.setMode(mAdapter.PHONE);
//			break;
//		case R.id.picture_remote:
//			tag = 1;
//			// view.setPadding(0, -1 * view.getHeight(), 0, 0);
//			// view.setVisibility(View.GONE);
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
				Log.d("tag", "camera_status_change");
			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		CameraParamsBean bean = null;
		String did = null;
		String name = null;
		String pwd = null;
		String user = null;
		int status = 0;
		int mode = 0;
		if (tag == 0) {
			// if (position == 0) {
			// Intent intent = new Intent(VideoActivity.this,
			// AllVideoCheckActivity.class);
			// intent.putExtra("zhaoxing", playList.toArray());
			// intent.putExtra("zhaogeng", 0);
			// startActivity(intent);
			// overridePendingTransition(R.anim.in_from_right,
			// R.anim.out_to_left);
			// return;
			// } else {
			Intent intent = new Intent(VideoActivity.this,
					AllVideoCheckActivity.class);
			List<MovieInfo> lists = maps.get(SystemValue.arrayList
					.get(position).getDid());
			if (lists != null) {
				intent.putExtra("zhaoxing", lists.toArray());
			}
			intent.putExtra("zhaogeng", position);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			// }
		} else if (tag == 1) {
			bean = SystemValue.arrayList.get(position);
			did = bean.getDid();
			name = bean.getName();
			pwd = bean.getPwd();
			user = bean.getUser();
			status = bean.getStatus();
			mode = mAdapter.getMode();
			if (mode == 2) {
				if (status == ContentCommon.PPPP_STATUS_ON_LINE) {
					Intent intentVid = new Intent(this,
							PlayBackTFActivity.class);
					intentVid.putExtra(ContentCommon.STR_CAMERA_NAME, name);
					intentVid.putExtra(ContentCommon.STR_CAMERA_ID, did);
					intentVid.putExtra(ContentCommon.STR_CAMERA_PWD, pwd);
					intentVid.putExtra(ContentCommon.STR_CAMERA_USER, user);
					startActivity(intentVid);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				} else {
					showToast(R.string.remote_video_offline);
				}
			}
		}
	}

	public static int byteToInt(byte[] b) {

		int s = 0;
		int s0 = b[0] & 0xff;// ���λ
		int s1 = b[1] & 0xff;
		int s2 = b[2] & 0xff;
		int s3 = b[3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return s;
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

	private void getVideoFile(File file) {

		file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				String name = file.getName();
				int i = name.lastIndexOf(".");
				if (i != -1) {
					name = name.substring(i);
					if (name.equalsIgnoreCase(".avi")) {
						String ss = file.getName();
						MovieInfo mi = new MovieInfo();
						mi.setDisplayName(ss);
						mi.setPath(file.getAbsolutePath());
						mi.setDate(new Date(file.lastModified()));
						mi.setCamerName(ss.substring(0, ss.indexOf("!")));
						mi.setVideoName(ss.substring(ss.indexOf("!") + 1));
						mi.setSize(getSize(file.length()));
						Log.d("test", "test-sd:size:" + file.length()
								+ " name:" + ss.substring(ss.indexOf("!") + 1));
						playList.add(mi);
						return true;
					}
				} else if (file.isDirectory()) {
					getVideoFile(file);
				}
				return false;
			}
		});
	}

	private String getSize(long size) {
		if (size <= 1024) {
			return size + "B";
		} else if (size > 1024 && size <= 1024 * 1024) {
			return size / 1024 + "KB";
		} else if (size > 1024 * 1024 && size <= 1024 * 1024 * 1024) {
			return size / (1024 * 1024) + "MB";
		} else if (size > 1024 * 1024 * 1024 * 1024) {
			return size / (1024 * 1024 * 1024) + "G";
		} else {
			return size + "B";
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcast);

	}
}
