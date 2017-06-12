package object.dbnewgo.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import object.dbnewgo.client.BridgeService.IpcamClientInterface;
import object.dbnewgo.client.MainActivity.MainEditInterface;
import object.p2pipcam.adapter.CameraEditAdapter;
import object.p2pipcam.adapter.CameraListAdapter;
import object.p2pipcam.bean.CameraParamsBean;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.DataBaseHelper;
import object.p2pipcam.utils.VibratorUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

@SuppressLint("NewApi")
public class IpcamClientActivity extends BaseActivity implements
		OnClickListener, IpcamClientInterface, OnItemClickListener,
		MainEditInterface {
	private static final String TAG = "IpcamClientActivity";
	private final int SNAPSHOT = 200;
	private static final String STR_DID = "did";
	private static final String STR_MSG_PARAM = "msgparam";
	private CameraInfoReceiver receiver = null;
	public static CameraListAdapter listAdapter = null;
	private DataBaseHelper helper = null;
	private static int cameraStatus;
	private boolean isEdited = false;
	private ListView cameraListView = null;
	private Button btnEdit;
	private CameraEditAdapter editAdapter;
	private LinearLayout delBottomLayout;
	private Button btnSelectAll;
	private Button btnSelectReverse;
	private Button btnDelCamera;
	private int timeTag = 0;
	private int timeOne = 0;
	private int timeTwo = 0;
	private LinearLayout addCameraListHeader;
	private ImageButton imageButtonRefresh;
	private ProgressBar progressBar;
	private ImageButton imageButton_apphome;
	private int screen_width;
	private int screen_height;
	private ProgressDialog progressDialog;
	private Set<String> tags;
	private boolean isCheck = false;

	public static void changerCameraStatus(int status) {
		cameraStatus = status;
	}

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ipcamclient);
		screen_width = getWindowManager().getDefaultDisplay().getWidth();
		screen_height = getWindowManager().getDefaultDisplay().getHeight();
		findView();
		setControlListener();
		listAdapter = new CameraListAdapter(this, this);
		editAdapter = new CameraEditAdapter(this);
		cameraListView.setAdapter(listAdapter);
		helper = DataBaseHelper.getInstance(this);
		// TypedValue mTypedValue = new TypedValue();
		// Resources res = getResources();
		// res.getValue(R.drawable.about, mTypedValue, true);
		initCameraList();
		MainActivity.setMainEditInterface(this);
		BridgeService.setIpcamClientInterface(this);
		NativeCaller.Init();
		new Thread(new StartPPPPThread()).start();
		isCheck = true;
		new Thread() {
			public void run() {
				while (isCheck) {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.d("test", "zhaogenghua0 isCheck");
					int count = listAdapter.getCount();
					for (int i = 0; i < count; i++) {
						CameraParamsBean bean = listAdapter.getOnItem(i);
						if (bean == null) {
							continue;
						}
						if (bean != null) {
							int status = bean.getStatus();
							String did = bean.getDid();
							String pwd = bean.getPwd();
							String user = bean.getUser();
							if (status != ContentCommon.PPPP_STATUS_ON_LINE
									&& status != ContentCommon.PPPP_STATUS_PWD_CUO
									&& status != ContentCommon.PPPP_STATUS_INVALID_ID
									&& status != ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								Log.d("test", "zhaogenghua2 did:" + did);
								NativeCaller.StopPPPP(did);
								 StartPPPP(did, user, pwd);
							}
						}
					}
				}
			};
		}.start();
	}

	class StartPPPPThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				StartCameraPPPP();
			} catch (Exception e) {

			}
		}
	}

	private void StartCameraPPPP() {
		int count = listAdapter.getCount();
		for (int i = 0; i < count; i++) {
			CameraParamsBean bean = listAdapter.getOnItem(i);
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			}
			 StartPPPP(bean.getDid(), bean.getUser(), bean.getPwd() );
		}
	}

	class CamerStatuThread extends Thread {
		String id;
		CameraParamsBean bean1;

		public CamerStatuThread(CameraParamsBean bean) {
			id = bean.getDid();
			bean1 = bean;
		}

		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bean1.getStatus() == ContentCommon.PPPP_STATUS_CONNECTING) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (listAdapter.UpdataCameraStatus(id,
								ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT)) {
							listAdapter.notifyDataSetChanged();
						}
					}
				});
			}
		}
	}

	private void findView() {
		cameraListView = (ListView) findViewById(R.id.listviewCamera);
		imageButton_apphome = (ImageButton) findViewById(R.id.app_home);
		addCameraListHeader = (LinearLayout) findViewById(R.id.addvidicon_listitem);
		imageButtonRefresh = (ImageButton) findViewById(R.id.refresh);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		addCameraListHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent in = new Intent(IpcamClientActivity.this,
						AddCameraActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);

			}
		});
		imageButtonRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				VibratorUtil.Vibrate(IpcamClientActivity.this, 20);
				SystemValue.TAG_CAMERLIST = 0;
				new GetDataTask().execute();
			}
		});
		imageButton_apphome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("tag", "screen_width:" + screen_width
						+ "  screen_height:" + screen_height);

			}
		});
		btnEdit = (Button) findViewById(R.id.main_edit);
		btnSelectAll = (Button) findViewById(R.id.main_selectall);
		btnSelectReverse = (Button) findViewById(R.id.main_selectreverse);
		btnDelCamera = (Button) findViewById(R.id.main_delete_camera);
		delBottomLayout = (LinearLayout) findViewById(R.id.del_bottom_layout);
	}

	private class GetDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
			imageButtonRefresh.setVisibility(View.GONE);
			if (SystemValue.ISRUN == false) {
				Intent intent = new Intent();
				intent.setClass(IpcamClientActivity.this, BridgeService.class);
				startService(intent);
				Log.d("tagx",
						"SystemValue.ISRUN == false--and--server is run to");
				new Thread() {
					public void run() {
						NativeCaller.PPPPInitial(SystemValue.SystemSerVer);
						NativeCaller.PPPPNetworkDetect();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						NativeCaller.Init();
					};
				}.start();

			}
		}

		class StopPPPPThread implements Runnable {
			@Override
			public void run() {
				try {
					Thread.sleep(10);
					StopCameraPPPP();
				} catch (Exception e) {

				}
			}
		}

		private void StopCameraPPPP() {
			int count = listAdapter.getCount();
			for (int i = 0; i < count; i++) {
				CameraParamsBean bean = listAdapter.getOnItem(i);
				if (bean.getStatus() != ContentCommon.PPPP_STATUS_ON_LINE) {
					// NativeCaller.PPPPGetSystemParams(did,
					// ContentCommon.MSG_GET_RESET_USER);

					try {
						Thread.sleep(100);
					} catch (Exception e) {
					}
					NativeCaller.StopPPPP(bean.getDid());
					try {
						Thread.sleep(300);
					} catch (Exception e) {
					}
					// new CamerStatuThread(bean).start();
					 StartPPPP(bean.getDid(), bean.getUser(),
							bean.getPwd() );
				}
			}
		}

		@Override
		protected Void doInBackground(Void... params) {

			new Thread(new StopPPPPThread()).start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			listAdapter.notifyDataSetChanged();
			progressBar.setVisibility(View.GONE);
			imageButtonRefresh.setVisibility(View.VISIBLE);
			super.onPostExecute(result);
		}

	}

	class StopPPPPThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(10);
				StopCameraPPPP();
			} catch (Exception e) {

			}
		}
	}
//01-07 02:38:33.042: A/art(4760): art/runtime/check_jni.cc:65] JNI DETECTED ERROR IN APPLICATION: native code passing in reference to invalid stack indirect reference table or invalid reference: 0xbe85e1a0

	private void StopCameraPPPP() {
		int count = listAdapter.getCount();
		for (int i = 0; i < count; i++) {
			CameraParamsBean bean = listAdapter.getOnItem(i);
			NativeCaller.StopPPPP(bean.getDid());
			try {
				Thread.sleep(300);
			} catch (Exception e) {
			}
			// new CamerStatuThread(bean).start();
		 StartPPPP(bean.getDid(), bean.getUser(), bean.getPwd() );
		}
	}

	private void setControlListener() {
		cameraListView.setOnItemClickListener(this);
		btnEdit.setOnClickListener(this);
		btnSelectAll.setOnClickListener(this);
		btnSelectReverse.setOnClickListener(this);
		btnDelCamera.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		VibratorUtil.Vibrate(IpcamClientActivity.this, 20);
		int id=v.getId();
		if (id==R.id.main_edit) {
			int count = listAdapter.getCount();
			if (isEdited) {
				isEdited = false;
				addCameraListHeader.setVisibility(View.VISIBLE);

				delBottomLayout.setVisibility(View.GONE);
				btnEdit.setText(getResources().getString(R.string.main_edit));
				cameraListView.setAdapter(listAdapter);

			} else {
				if (count > 0) {
					addCameraListHeader.setVisibility(View.GONE);

					isEdited = true;
					delBottomLayout.setVisibility(View.VISIBLE);
					btnEdit.setText(getResources().getString(R.string.done));
					cameraListView.setAdapter(editAdapter);
				} else {
					showToast(R.string.main_plea_addcam);
				}
			}
		}else if (id==R.id.main_selectall) {
			editAdapter.selectAll(true);
			editAdapter.notifyDataSetChanged();
		}else if (id==R.id.main_selectreverse) {
			editAdapter.reverseSelect(false);
			editAdapter.notifyDataSetChanged();
		}else if (id==R.id.main_delete_camera) {
			if (editAdapter.hasSelect) {
				showDelSureDialog();
			}
		}
		
//		switch (v.getId()) {
//		case R.id.main_edit:
//			int count = listAdapter.getCount();
//			if (isEdited) {
//				isEdited = false;
//				addCameraListHeader.setVisibility(View.VISIBLE);
//
//				delBottomLayout.setVisibility(View.GONE);
//				btnEdit.setText(getResources().getString(R.string.main_edit));
//				cameraListView.setAdapter(listAdapter);
//
//			} else {
//				if (count > 0) {
//					addCameraListHeader.setVisibility(View.GONE);
//
//					isEdited = true;
//					delBottomLayout.setVisibility(View.VISIBLE);
//					btnEdit.setText(getResources().getString(R.string.done));
//					cameraListView.setAdapter(editAdapter);
//				} else {
//					showToast(R.string.main_plea_addcam);
//				}
//			}
//			break;
//		case R.id.main_selectall:
//			editAdapter.selectAll(true);
//			editAdapter.notifyDataSetChanged();
//			break;
//		case R.id.main_selectreverse:
//			editAdapter.reverseSelect(false);
//			editAdapter.notifyDataSetChanged();
//			break;
//		case R.id.main_delete_camera:
//			if (editAdapter.hasSelect) {
//				showDelSureDialog();
//			}
//			break;
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2) {// playActivity
			if (resultCode == 2) {
				String did = data.getStringExtra("did");
				showPlayLastBmp(did);
			}
		}
	}

	private void showPlayLastBmp(final String did) {
		new Thread() {
			public void run() {
				File div = new File(Environment.getExternalStorageDirectory(),
						"ipcam/pic");
				File file = new File(div, did + ".jpg");
				String filepath = file.getAbsolutePath();
				Bitmap bitmap = BitmapFactory.decodeFile(filepath);
				if (listAdapter.UpdateCameraImage(did, bitmap)) {
					PPPPMsgHandler.sendEmptyMessage(SNAPSHOT);
				}
			}
		}.start();

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

	private String did;
	private Handler PPPPMsgHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bd = msg.getData();
			int msgParam = bd.getInt(STR_MSG_PARAM);
			int msgType = msg.what;
			did = bd.getString(STR_DID);
			Log.d("test", "did==" + did + "  msgType=" + msgType);
			switch (msgType) {
			case 10234:
				listAdapter.notifyDataSetChanged();
				break;
			case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
				Intent intent = new Intent("camera_status_change");
				intent.putExtra(ContentCommon.STR_CAMERA_ID, did);
				intent.putExtra(ContentCommon.STR_PPPP_STATUS, msgParam);
				sendBroadcast(intent);

				if (listAdapter.UpdataCameraStatus(did, msgParam)) {
					listAdapter.notifyDataSetChanged();
					if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
						// NativeCaller.PPPPGetSystemParams(did,
						// ContentCommon.MSG_TYPE_GET_PARAMS);
						// new Thread() {
						// public void run() {
						//
						// try {
						// Thread.sleep(500);
						// } catch (InterruptedException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
						// NativeCaller.PPPPCameraControl(did, 0, 1);
						// try {
						// Thread.sleep(500);
						// } catch (InterruptedException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
						// NativeCaller.PPPPCameraControl(did, 13, 500);
						// };
						// }.start();
					}
					if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID
							|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED
							|| msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE
							|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT
							|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER
							|| msgParam == ContentCommon.PPPP_STATUS_USER_LOGIN
							|| msgParam == ContentCommon.PPPP_STATUS_PWD_CUO) {
						NativeCaller.StopPPPP(did);
					}
				}
				break;
			case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
				Log.d("shix", "shix:" + msgParam);
				if (listAdapter.UpdataCameraType(did, msgParam)) {
					// listAdapter.notifyDataSetChanged();
				}
				break;
			case SNAPSHOT:
				listAdapter.notifyDataSetChanged();
				break;
			}
		}
	};

	private void showDelSureDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.del_alert);
		builder.setPositiveButton(R.string.str_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						new AsyncTask<Void, Void, Void>() {
							protected void onPreExecute() {
								progressDialog = new ProgressDialog(
										IpcamClientActivity.this);
								progressDialog
										.setMessage(getResources().getString(
												R.string.main_show_delecting));
								progressDialog.setCancelable(false);
								progressDialog.show();
							};

							@Override
							protected Void doInBackground(Void... params) {
								ArrayList<String> didList = editAdapter
										.delCamera();
								Intent intent = new Intent(
										"del_add_modify_camera");
								for (int i = 0; i < didList.size(); i++) {
									String did = didList.get(i);
									if (delCameraFromdb(did)) {
										listAdapter.delCamera(did);
										NativeCaller.StopPPPP(did);
										listAdapter.notifyDataSetChanged();
										intent.putExtra(
												ContentCommon.STR_CAMERA_ID,
												did);
										intent.putExtra("type",
												ContentCommon.DEL_CAMERA);
										sendBroadcast(intent);
									}
								}

								return null;
							}

							protected void onPostExecute(Void result) {
								progressDialog.cancel();
								if (editAdapter.getCount() == 0) {
									cameraListView.setVisibility(View.GONE);
								}
								editAdapter.notifyDataSetChanged();
							};
						}.execute();
					}
				});
		builder.setNegativeButton(R.string.str_cancel, null);
		builder.show();
	}

	private synchronized boolean delCameraFromdb(String did) {
		boolean bRes = false;
		if (helper.deleteCamera(did)) {
			bRes = true;
		}
		return bRes;
	}

	private synchronized boolean UpdataCamera2db(String oldDID, String name,
			String did, String user, String pwd) {
		boolean bRes = false;
		if (helper.updateCamera(oldDID, name, did, user, pwd)) {
			bRes = true;
		}
		return bRes;
	}

	private synchronized void addCamera2db(String name, String did,
			String user, String pwd) {
		helper.createCamera(name, did, user, pwd);
	}

	private void initCameraList() {
		Cursor cursor = helper.fetchAllCameras();
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cameraListView.setVisibility(View.VISIBLE);
			} else {
				cameraListView.setVisibility(View.GONE);
			}
			while (cursor.moveToNext()) {
				String name = cursor.getString(1);
				String did = cursor.getString(2);
				String user = cursor.getString(3);
				String pwd = cursor.getString(4);
				listAdapter.AddCamera(name, did, user, pwd);
			}
			listAdapter.notifyDataSetChanged();
		}
		if (cursor != null) {
			cursor.close();
		}
	}

	@Override
	protected void onResume() {
		Log.d("IpcamClientActivity", "onResume()");
		SystemValue.TAG_CAMERLIST = 0;
		super.onResume();
		// if (SystemValue.arrayList.size() > 0) {
		// tags = new HashSet<String>();
		// for (int i = 0; i < SystemValue.arrayList.size(); i++) {
		// tags.add(SystemValue.arrayList.get(i).getDid().replace("-", ""));
		// }
		// JPushInterface.setAliasAndTags(this, "shix", tags);
		// }
		SystemValue.TAG_CAMERLIST = 0;
		listAdapter.notifyDataSetChanged();
		editAdapter.notifyDataSetChanged();
		if (SystemValue.arrayList.size() > 0) {
			cameraListView.setVisibility(View.VISIBLE);
		} else {
			cameraListView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onRestart() {

		Log.d("IpcamClientActivity", "zhao-onRestart():"
				+ SystemValue.arrayList.size());
		super.onRestart();
	}

	class CameraInfoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("other".equals(action)) {
				listAdapter.sendCameraStatus();
			} else {
				int option = intent.getIntExtra(ContentCommon.CAMERA_OPTION,
						ContentCommon.INVALID_OPTION);
				if (option == ContentCommon.INVALID_OPTION)
					return;
				String strOldDID = "";
				final String name = intent
						.getStringExtra(ContentCommon.STR_CAMERA_NAME);
				final String did = intent
						.getStringExtra(ContentCommon.STR_CAMERA_ID);
				final String user = intent
						.getStringExtra(ContentCommon.STR_CAMERA_USER);
				final String pwd = intent
						.getStringExtra(ContentCommon.STR_CAMERA_PWD);

				if (option == ContentCommon.EDIT_CAMERA) {
					strOldDID = intent
							.getStringExtra(ContentCommon.STR_CAMERA_OLD_ID);
					if (UpdataCamera2db(strOldDID, name, did, user, pwd)) {
						if (listAdapter.UpdateCamera(strOldDID, name, did,
								user, pwd)) {
							NativeCaller.PPPPGetSystemParams(did,
									ContentCommon.MSG_GET_RESET_USER);
							NativeCaller.PPPPGetSystemParams(did,
									ContentCommon.MSG_TYPE_GET_PARAMS);
							listAdapter.notifyDataSetChanged();
							NativeCaller.StopPPPP(did);
							 StartPPPP(did, user, pwd);
						}
						editAdapter.modifyCamera(strOldDID, did, name, user,
								pwd);
						Intent intentChange = new Intent(
								"del_add_modify_camera");
						intentChange
								.putExtra("type", ContentCommon.EDIT_CAMERA);
						intentChange.putExtra(ContentCommon.STR_CAMERA_ID, did);
						intentChange.putExtra("olddid", strOldDID);
						intentChange.putExtra("name", name);
						sendBroadcast(intentChange);
					}
				} else if (option == ContentCommon.CHANGE_CAMERA_USER) {
					strOldDID = intent
							.getStringExtra(ContentCommon.STR_CAMERA_OLD_ID);
					if (listAdapter.UpdateCamera(strOldDID, name, did, user,
							pwd)) {
						NativeCaller.PPPPGetSystemParams(did,
								ContentCommon.MSG_GET_RESET_USER);
						listAdapter.notifyDataSetChanged();
						NativeCaller.StopPPPP(did);
						 StartPPPP(did, user, pwd );
						if (editAdapter.modifyCamera(strOldDID, did, name,
								user, pwd)) {
							editAdapter.notifyDataSetChanged();
						}
					}
				} else {
					if (listAdapter.getCount() < 10) {
						if (listAdapter.AddCamera(name, did, user, pwd)) {
							Log.d("test", "zhao-guangbo");
							cameraListView.setVisibility(View.VISIBLE);
							listAdapter.notifyDataSetChanged();
							 StartPPPP(did, user, pwd );
							new Thread() {
								public void run() {
									addCamera2db(name, did, user, pwd);
									editAdapter.addCamera(name, did, user, pwd);
									Intent intentAdd = new Intent(
											"del_add_modify_camera");
									intentAdd.putExtra("type",
											ContentCommon.ADD_CAMERA);
									intentAdd.putExtra(
											ContentCommon.STR_CAMERA_ID, did);
									intentAdd.putExtra("name", name);
									sendBroadcast(intentAdd);
								}
							}.start();
						}
					} else {
						showToast(R.string.add_camer_no_add);
					}
				}
			}
		}
	}

	@Override
	protected void onStart() {
		Log.d("IpcamClientActivity", "onStart()");
		super.onStart();
		if (receiver == null) {
			receiver = new CameraInfoReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ContentCommon.STR_CAMERA_INFO_RECEIVER);
			filter.addAction("back");
			filter.addAction("other");
			registerReceiver(receiver, filter);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCheck = false;
		MainActivity.setMainEditInterface(null);
		if (helper != null) {
			helper = null;
		}
		SystemValue.TAG_CAMERLIST = 0;
		unregisterReceiver(receiver);
	}

	public void showSetting(int position) {
		VibratorUtil.Vibrate(IpcamClientActivity.this, 20);
		final CameraParamsBean bean = listAdapter.getItemCamera(position);
		int status = bean.getStatus();
		SystemValue.doorBellAdmin = bean.getUser();
		SystemValue.doorBellPass = bean.getPwd();

		if (status == ContentCommon.PPPP_STATUS_ON_LINE) {
			// boolean authority = bean.isAuthority();
			// if (authority) {
			Intent intent = new Intent(IpcamClientActivity.this,
					SettingActivity.class);
			intent.putExtra(ContentCommon.STR_CAMERA_ID, bean.getDid());
			intent.putExtra(ContentCommon.STR_CAMERA_NAME, bean.getName());
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			// } else {
			// showToast(R.string.main_not_administrator);
			// }
		} else {
			showToast(R.string.main_setting_prompt);
		}
	}

	@Override
	public void BSMsgNotifyData(String did, int type, int param) {
		Log.d(TAG, "type:" + type + " param:" + param);
		Bundle bd = new Bundle();
		Message msg = PPPPMsgHandler.obtainMessage();
		msg.what = type;
		bd.putInt(STR_MSG_PARAM, param);
		bd.putString(STR_DID, did);
		msg.setData(bd);
		PPPPMsgHandler.sendMessage(msg);
	}

	@Override
	public void BSSnapshotNotify(String did, byte[] bImage, int len) {
		Bitmap bmp = BitmapFactory.decodeByteArray(bImage, 0, len);
		if (bmp == null) {
			Log.d(TAG, "bmp can't be decode...");
			return;
		}
		if (listAdapter.UpdateCameraImage(did, bmp)) {
			PPPPMsgHandler.sendEmptyMessage(SNAPSHOT);
		}
	}

	@Override
	public void callBackUserParams(String did, String user1, String pwd1,
			String user2, String pwd2, String user3, String pwd3) {
		Log.d("user", "user1:" + user1 + "   pwd1" + pwd1 + "\n" + "user2:"
				+ user2 + "   pwd2" + pwd2 + "\n" + "user3:" + user3
				+ "   pwd3" + pwd3);
		listAdapter.upadeUserAuthority(did, user3, pwd3);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// if (position == 0) {
		// Intent in = new Intent(IpcamClientActivity.this,
		// AddCameraActivity.class);
		// startActivity(in);
		// overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		// Log.d("tag", "header");
		// return;
		// }
		if (isEdited) {
			if (editAdapter != null) {
				CameraParamsBean bean = editAdapter.getItemCamera(position);
				String name = bean.getName();
				String did = bean.getDid();
				String user = bean.getUser();
				String pwd = bean.getPwd();
				Intent in = new Intent(IpcamClientActivity.this,
						AddCameraActivity.class);
				in.putExtra(ContentCommon.CAMERA_OPTION,
						ContentCommon.EDIT_CAMERA);
				in.putExtra(ContentCommon.STR_CAMERA_NAME, name);
				in.putExtra(ContentCommon.STR_CAMERA_ID, did);
				in.putExtra(ContentCommon.STR_CAMERA_USER, user);
				in.putExtra(ContentCommon.STR_CAMERA_PWD, pwd);
				in.putExtra("pushTypeInt", 3);
				startActivity(in);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		} else {
			CameraParamsBean bean = listAdapter.getOnItem(position);
			Log.d("test", "00000000");
			if (bean == null) {
				Log.d("test", "111111");
				return;
			}
			int status = bean.getStatus();
			Log.d("test", "22222222");
			if (status == ContentCommon.PPPP_STATUS_INVALID_ID
					|| status == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT
					|| status == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE
					|| status == ContentCommon.PPPP_STATUS_CONNECT_FAILED
					|| status == ContentCommon.PPPP_STATUS_USER_LOGIN
					|| status == ContentCommon.PPPP_STATUS_PWD_CUO) {
				Log.d("test", "33333333");
				Log.d("test", "55555555");
				String did = bean.getDid();
				String user = bean.getUser();
				String pwd = bean.getPwd();
				NativeCaller.StopPPPP(did);
				 StartPPPP(did, user, pwd );
				return;
			}
			Log.d("test", "4444444444");
			if (status != ContentCommon.PPPP_STATUS_ON_LINE) {

				return;
			}

			// ///////////////////////////////////////////////////////////////////////////
			String did = bean.getDid();
			String name = bean.getName();
			String user = bean.getUser();
			String pwd = bean.getPwd();
			int p2pMode = bean.getMode();

			Intent in = new Intent(IpcamClientActivity.this,
					ListeningPlayActivity.class);
			in.putExtra(ContentCommon.STR_CAMERA_TYPE,
					ContentCommon.CAMERA_TYPE_MJPEG);
			in.putExtra(ContentCommon.STR_STREAM_TYPE,
					ContentCommon.MJPEG_SUB_STREAM);
			in.putExtra(ContentCommon.STR_CAMERA_NAME, name);
			in.putExtra(ContentCommon.STR_CAMERA_ID, did);
			in.putExtra(ContentCommon.STR_CAMERA_USER, user);
			in.putExtra(ContentCommon.STR_CAMERA_PWD, pwd);
			in.putExtra("modep", p2pMode);
			SystemValue.doorBellAdmin = user;
			SystemValue.doorBellPass = pwd;
			SystemValue.ISPLAY = 0;
			startActivityForResult(in, 2);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			// /////////////////////////////////////////////////////////////////////
			// i++;
			// Intent intent = null;
			// Log.d("tag", "alarm name:" + "");
			// intent = new Intent(this, ListeningActivity.class);
			// intent.putExtra(ContentCommon.STR_CAMERA_ID, did);
			// intent.putExtra(ContentCommon.STR_CAMERA_NAME, name);
			// intent.putExtra("pushIsAlerm", 2);
			// intent.putExtra("pushDate", "test"+i);
			// intent.putExtra(ContentCommon.STR_CAMERA_USER, user);
			// intent.putExtra(ContentCommon.STR_CAMERA_PWD, pwd);
			// intent.putExtra(ContentCommon.STR_CAMERA_TYPE, 0);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// this.startActivity(intent);
		}

	}

	// int i = 0;
	@Override
	public void callBackDoorBellController(String did, int index1) {
		// TODO Auto-generated method stub
		Log.d("test", "shix-doorbell-did1:" + did);
		for (int i = 0; i < SystemValue.arrayList.size(); i++) {
			if (did.equals(SystemValue.arrayList.get(i).getDid())) {
				SystemValue.arrayList.get(i).setIndex1(index1);
				Log.d("test", "shix-doorbell-index1"
						+ SystemValue.arrayList.get(i).getIndex1());
			}
		}
		PPPPMsgHandler.sendEmptyMessage(10234);
	}

	@Override
	public boolean EditOnclick(Button btn_edit, boolean isEdited) {
		// TODO Auto-generated method stub
		int count = listAdapter.getCount();
		if (isEdited) {
			isEdited = false;
			addCameraListHeader.setVisibility(View.VISIBLE);

			delBottomLayout.setVisibility(View.GONE);
			btn_edit.setText(getResources().getString(R.string.main_edit));
			cameraListView.setAdapter(listAdapter);

		} else {
			if (count > 0) {
				addCameraListHeader.setVisibility(View.GONE);

				isEdited = true;
				delBottomLayout.setVisibility(View.VISIBLE);
				btn_edit.setText(getResources().getString(R.string.done));
				cameraListView.setAdapter(editAdapter);
			} else {
				showToast(R.string.main_plea_addcam);
			}
		}
		this.isEdited = isEdited;
		return isEdited;
	}

 

}