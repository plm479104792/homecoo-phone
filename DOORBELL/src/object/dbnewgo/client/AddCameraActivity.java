package object.dbnewgo.client;

import java.util.Map;

import object.dbnewgo.client.BridgeService.AddCameraInterface;
import object.p2pipcam.adapter.SearchListAdapter;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.VibratorUtil;
import object.p2pipcam.zxingtwodimensioncode.CaptureActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ��Ӻ��޸������
 * */
public class AddCameraActivity extends BaseActivity implements OnClickListener,
		AddCameraInterface, SensorEventListener {
	// private static final String LOG_TAG = "AddCameraActivity";

	// private Button btnAdd = null;
	// private Button btnCancel = null;
	private EditText devNameEdit = null;
	private EditText userEdit = null;
	private EditText pwdEdit = null;
	private EditText didEdit = null;
	private Button back;
	private Button done;
	private String strName = "";
	private String strUser = "";
	private String strPwd = "";
	private String strOldDID = "";
	private static final int SEARCH_TIME = 3000;
	private int option = ContentCommon.INVALID_OPTION;
	private TextView textViewAddCamera = null;
	private int CameraType = ContentCommon.CAMERA_TYPE_MJPEG;
	private Button btnScanId;
	private Button btnSearchCamera;
	private SearchListAdapter listAdapter = null;
	private ProgressDialog progressdlg = null;
	private boolean isSearched;
	private Button onekey;
	//private SensorManager mSensorManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_camera0102);
		// setEdgeFromLeft();
		findView();
		Intent in = getIntent();
		option = in.getIntExtra(ContentCommon.CAMERA_OPTION,
				ContentCommon.INVALID_OPTION);
		if (option != ContentCommon.INVALID_OPTION) {
			strName = in.getStringExtra(ContentCommon.STR_CAMERA_NAME);
			strOldDID = in.getStringExtra(ContentCommon.STR_CAMERA_ID);
			strUser = in.getStringExtra(ContentCommon.STR_CAMERA_USER);
			strPwd = in.getStringExtra(ContentCommon.STR_CAMERA_PWD);
		} else {
			devNameEdit
					.setText("DoorBell" + (SystemValue.arrayList.size() + 1));
		}
		progressdlg = new ProgressDialog(this);
		progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressdlg.setMessage(getString(R.string.searching_tip));
		listAdapter = new SearchListAdapter(this);
		InitParams();
		BridgeService.setAddCameraInterface(this);
	//	mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//		Toast toast = Toast.makeText(AddCameraActivity.this,
//				R.string.doorbell_add_yao_show, Toast.LENGTH_LONG);
//		ImageView imageView = new ImageView(AddCameraActivity.this);
//		imageView.setImageResource(R.drawable.yaoyiyao);
//		View toastView = toast.getView();
//		LinearLayout linearLayout = new LinearLayout(AddCameraActivity.this);
//		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//		linearLayout.setGravity(Gravity.CENTER);
//		linearLayout.addView(imageView);
//		linearLayout.addView(toastView);
//		toast.setView(linearLayout);
//		toast.setDuration(0);
//		toast.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// showAll(back, "AddCameraActivity");
		super.onResume();
//		mSensorManager.registerListener(this,
//				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				// ����SENSOR_DELAY_UI��SENSOR_DELAY_FASTEST��SENSOR_DELAY_GAME�ȣ�
				// ��ݲ�ͬӦ�ã���Ҫ�ķ�Ӧ���ʲ�ͬ��������ʵ������趨
//				SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void hideSof(EditText edit) {
		// TODO Auto-generated method stub
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}

	@Override
	protected void onPause() {
		// overridePendingTransition(R.anim.out_to_right,
		// R.anim.in_from_left);// �˳�����
		hideSof(didEdit);
		super.onPause();
	}

	private void InitParams() {
		if (option == ContentCommon.EDIT_CAMERA) {
			textViewAddCamera.setText(R.string.edit_camera);
		} else {
			textViewAddCamera.setText(R.string.add_camera);
		}

		if (option != ContentCommon.INVALID_OPTION) {
			devNameEdit.setText(strName);
			userEdit.setText(strUser);
			pwdEdit.setText(strPwd);
			didEdit.setText(strOldDID);
		}
		back.setOnClickListener(this);
		done.setOnClickListener(this);
		btnScanId.setOnClickListener(this);
		btnSearchCamera.setOnClickListener(this);
	}

	@Override
	protected void onStop() {
		progressdlg.dismiss();
//		mSensorManager.unregisterListener(this);
		NativeCaller.StopSearch();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		BridgeService.setAddCameraInterface(null);
		super.onDestroy();
	}

	Runnable updateThread = new Runnable() {

		public void run() {
			NativeCaller.StopSearch();
			progressdlg.dismiss();
			Message msg = updateListHandler.obtainMessage();
			msg.what = 1;
			updateListHandler.sendMessage(msg);
		}
	};

	Handler updateListHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 1) {

				listAdapter.notifyDataSetChanged();
				if (listAdapter.getCount() > 0) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							AddCameraActivity.this);
					dialog.setTitle(getResources().getString(
							R.string.add_search_result));
					dialog.setCancelable(false);
					dialog.setPositiveButton(
							getResources().getString(R.string.refresh),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startSearch();
								}
							});
					dialog.setNegativeButton(
							getResources().getString(R.string.str_cancel),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									isStart = false;
								}
							});
					dialog.setAdapter(listAdapter,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int arg2) {
									Map<String, Object> mapItem = (Map<String, Object>) listAdapter
											.getItemContent(arg2);
									if (mapItem == null) {
										return;
									}

									String strName = (String) mapItem
											.get(ContentCommon.STR_CAMERA_NAME);
									String strDID = (String) mapItem
											.get(ContentCommon.STR_CAMERA_ID);
									String strUser = ContentCommon.DEFAULT_USER_NAME;
									String strPwd = ContentCommon.DEFAULT_USER_PWD;
									// devNameEdit.setText(strName);
									userEdit.setText(strUser);
									pwdEdit.setText(strPwd);
									didEdit.setText(strDID);
									isStart = false;
								}
							});

					dialog.show();
				} else {
					Toast.makeText(AddCameraActivity.this,
							getResources().getString(R.string.add_search_no),
							Toast.LENGTH_LONG).show();
					isSearched = false;// ���û���������������ٴ�����
					isStart = false;
				}
			}
		}
	};

	private void startSearch() {
		listAdapter.ClearAll();
		progressdlg.setMessage(getString(R.string.searching_tip));
		progressdlg.setCancelable(false);
		progressdlg.show();
		new Thread(new SearchThread()).start();
		updateListHandler.postDelayed(updateThread, SEARCH_TIME);
	}

	private boolean isStart = false;

	private void startSearch1() {
		isStart = true;
		listAdapter.ClearAll();
		progressdlg.setMessage(getString(R.string.searching_tip));
		progressdlg.setCancelable(false);
		progressdlg.show();
		new Thread(new SearchThread()).start();
		updateListHandler.postDelayed(updateThread, SEARCH_TIME);
	}

	private class SearchThread implements Runnable {
		@Override
		public void run() {
			Log.d("tag", "startSearch");
			NativeCaller.StartSearch();
		}
	}

	private void findView() {
		// btnAdd = (Button) findViewById(R.id.btnAdd);
		// btnCancel = (Button) findViewById(R.id.btnCancel);

		back = (Button) findViewById(R.id.back);
		done = (Button) findViewById(R.id.done);
		devNameEdit = (EditText) findViewById(R.id.editDevName);
		userEdit = (EditText) findViewById(R.id.editUser);
		pwdEdit = (EditText) findViewById(R.id.editPwd);
		didEdit = (EditText) findViewById(R.id.editDID);
		btnScanId = (Button) findViewById(R.id.scanID);
		btnSearchCamera = (Button) findViewById(R.id.btn_searchCamera);
		textViewAddCamera = (TextView) findViewById(R.id.textview_add_camera);
		onekey = (Button) findViewById(R.id.onekey);
		onekey.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if (id==R.id.onekey) {
			Intent intent1 = new Intent(this, OneSetActivity.class);
			startActivity(intent1);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			AddCameraActivity.this.finish();
		}else if (id==R.id.back) {
			finish();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
		}else if (id==R.id.done) {
			done();
			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
		}else if (id==R.id.scanID) {
			progressdlg.setMessage(getResources().getString(
					R.string.add_twodimensioncode));
			progressdlg.setCancelable(false);
			progressdlg.show();
			Intent intent = new Intent(this, CaptureActivity.class);
			startActivityForResult(intent, 0);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// ���붯��
		}else if (id==R.id.btn_searchCamera) {
			searchCamera();
		}
		
		
		
		
//		switch (v.getId()) {
//		case R.id.onekey:
//			Intent intent1 = new Intent(this, OneSetActivity.class);
//			startActivity(intent1);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			AddCameraActivity.this.finish();
//			break;
//		case R.id.back:
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//			break;
//		case R.id.done:
//			done();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
//			break;
//		case R.id.scanID:
//			progressdlg.setMessage(getResources().getString(
//					R.string.add_twodimensioncode));
//			progressdlg.setCancelable(false);
//			progressdlg.show();
//			Intent intent = new Intent(this, CaptureActivity.class);
//			startActivityForResult(intent, 0);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// ���붯��
//			break;
//		case R.id.btn_searchCamera:
//			searchCamera();
//			break;
//
//		default:
//			break;
//		}
	}

	// ���������ʧ�ķ���
	private void hiddenInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void searchCamera() {
		if (!isSearched) {
			isSearched = true;
			startSearch();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					AddCameraActivity.this);
			dialog.setTitle(getResources()
					.getString(R.string.add_search_result));
			dialog.setCancelable(false);
			dialog.setPositiveButton(
					getResources().getString(R.string.refresh),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							startSearch();

						}
					});
			dialog.setNegativeButton(
					getResources().getString(R.string.str_cancel), null);
			dialog.setAdapter(listAdapter,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg2) {
							Map<String, Object> mapItem = (Map<String, Object>) listAdapter
									.getItemContent(arg2);
							if (mapItem == null) {
								return;
							}

							String strName = (String) mapItem
									.get(ContentCommon.STR_CAMERA_NAME);
							String strDID = (String) mapItem
									.get(ContentCommon.STR_CAMERA_ID);
							String strUser = ContentCommon.DEFAULT_USER_NAME;
							String strPwd = ContentCommon.DEFAULT_USER_PWD;
							// devNameEdit.setText(strName);
							userEdit.setText(strUser);
							pwdEdit.setText(strPwd);
							didEdit.setText(strDID);

						}
					});
			dialog.show();
		}
	}

	private void done() {
		Intent in = new Intent();
		String strDevName = devNameEdit.getText().toString();
		String strUser = userEdit.getText().toString();
		String strPwd = pwdEdit.getText().toString();
		String strDID = didEdit.getText().toString().toUpperCase();
		int si = 0;
		if (strDevName.length() == 0) {
			showToast(R.string.input_camera_name);
			return;
		}

		if (strDID.length() == 0) {
			showToast(R.string.input_camera_id);
			return;
		}
		String str = null;
		if (strDID.length() > 8) {
			str = strDID.substring(0, 8);
		} else {
			str = strDID;
		}
		boolean istrue = false;
		for (int i = 0; i < str.length(); i++) {
			Log.d("tag", "test:" + (str.charAt(i) >= 48)
					+ (str.charAt(i) <= 57) + "  str:" + str);
			if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
				istrue = true;
				break;
			}
		}
		if (!istrue) {
			showToast(R.string.add_camer_invi);
			return;
		}
		for (int i = 0; i < SystemValue.arrayList.size(); i++) {
			if (!strOldDID.endsWith(strDID)
					&& strDID.equalsIgnoreCase(SystemValue.arrayList.get(i)
							.getDid())) {
				showToast(R.string.input_camera_all_include);
				return;
			}
		}

		if (strUser.length() == 0) {
			showToast(R.string.input_camera_user);
			return;
		}
		in.setAction(ContentCommon.STR_CAMERA_INFO_RECEIVER);
		if (option == ContentCommon.INVALID_OPTION) {
			option = ContentCommon.ADD_CAMERA;
		}
		in.putExtra(ContentCommon.CAMERA_OPTION, option);
		if (option != ContentCommon.INVALID_OPTION) {
			in.putExtra(ContentCommon.STR_CAMERA_OLD_ID, strOldDID);
		}
		in.putExtra(ContentCommon.STR_CAMERA_NAME, strDevName);
		in.putExtra(ContentCommon.STR_CAMERA_ID, strDID.replace("-", ""));
		in.putExtra(ContentCommon.STR_CAMERA_USER, strUser);
		in.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd);
		in.putExtra(ContentCommon.STR_CAMERA_TYPE, CameraType);
		sendBroadcast(in);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			didEdit.setText(scanResult);
		}
	}

	/**
	 * BridgeService callback
	 * **/
	@Override
	public void callBackSearchResultData(int cameraType, String strMac,
			String strName, String strDeviceID, String strIpAddr, int port) {
		if (!listAdapter.AddCamera(strMac, strName,
				strDeviceID.replace("-", ""))) {
			return;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		int sensorType = event.sensor.getType();

		// values[0]:X�ᣬvalues[1]��Y�ᣬvalues[2]��Z��
		float[] values = event.values;

		if (sensorType == Sensor.TYPE_ACCELEROMETER) {

			/*
			 * ��Ϊһ��������£���������ֵ������9.8~10֮�䣬ֻ������ͻȻҡ���ֻ��ʱ��˲ʱ���ٶȲŻ�ͻȻ�������١�
			 * ���ԣ�����ʵ�ʲ��ԣ�ֻ�������һ��ļ��ٶȴ���14��ʱ�򣬸ı�����Ҫ�����þ�OK��~~~
			 */
			if ((Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math
					.abs(values[2]) > 14)) {
				if (!isStart) {
					startSearch1();
				}
				VibratorUtil.Vibrate(AddCameraActivity.this, 500);
			}
		}
	}

}