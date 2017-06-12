package object.dbnewgo.client;

import java.util.ArrayList;
import java.util.Map;

import object.p2pipcam.adapter.ShowLocPicGalleryAdapter;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.customComponent.MyGallery;
import object.p2pipcam.customComponent.MyGallery.MyGalleryEvent;
import object.p2pipcam.utils.DataBaseHelper;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowLocalPictureActivity extends BaseActivity implements
		OnItemSelectedListener, OnClickListener, MyGalleryEvent {
	private String TAG = "ShowLocalPictureActivity";
	public static int screenWidth;
	public static int screenHeight;
	private MyGallery mGallery;
	private String strDID;
	private String strDate;
	private static ArrayList<Map<String, Object>> arrayList;
	private ArrayList<String> delList;
	private TextView mTv_TakeTime;
	private TextView mTv_TakeDate;
	private TextView mTv_Sum;
	private final int DELETE = 0;
	private final int DELETEALL = 1;
	private DataBaseHelper helper;
	private ShowLocPicGalleryAdapter mAdapter;
	private int position;
	private Button btnBack;
	private TextView tvTitle;
	private String strCameraName;
	private RelativeLayout topLayout;
	private RelativeLayout bottomLayout;
	private float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
	private boolean isShowing = false;
	float beforeLenght = 0.0f; // ���������
	float afterLenght = 0.0f; // ���������
	boolean isScale = false;
	float currentScale = 1.0f;// ��ǰͼƬ�����ű���
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				isShowing = false;
				// topLayout.setVisibility(View.GONE);
				// bottomLayout.setVisibility(View.GONE);
				break;

			default:
				break;
			}
		}
	};
	private TextView tvNoPics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.showlocalpicture);
		screenWidth = getWindow().getWindowManager().getDefaultDisplay()
				.getWidth();
		screenHeight = getWindow().getWindowManager().getDefaultDisplay()
				.getHeight();
		helper = DataBaseHelper.getInstance(this);
		findView();
		mAdapter = new ShowLocPicGalleryAdapter(this, arrayList);
		mGallery.setVerticalFadingEdgeEnabled(false);// ȡ����ֱ����߿�
		mGallery.setHorizontalFadingEdgeEnabled(false);// ȡ��ˮƽ����߿�
		mGallery.setAdapter(mAdapter);
		mGallery.setOnItemSelectedListener(this);
		mGallery.setOnCreateContextMenuListener(this);
		mGallery.setMyTouch(this);
		mGallery.setSelection(position);
		tvTitle.setText(strCameraName
				+ getResources().getString(R.string.main_pic));
		mTv_TakeDate.setText(strDate);
		mTv_Sum.setText((position + 1) + "/" + arrayList.size());
		mHandler.sendEmptyMessageDelayed(1, 1500);

		delList = new ArrayList<String>();
	}

	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	// menu.setHeaderTitle(R.string.list_option);
	// menu.add(0, DELETE, 0, R.string.delete_local_picture);
	// menu.add(0, DELETEALL, 0, R.string.delete_local_all_picture);
	// }
	//
	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// AdapterView.AdapterContextMenuInfo menuInfo =
	// (AdapterView.AdapterContextMenuInfo) item
	// .getMenuInfo();
	// int pos = menuInfo.position;
	// switch (item.getItemId()) {
	// case DELETE:
	// showDeletDialog(DELETE, pos);
	// break;
	// case DELETEALL:
	// showDeletDialog(DELETEALL, pos);
	// break;
	//
	// default:
	// break;
	// }
	//
	// return super.onContextItemSelected(item);
	// }

	private void getDataFromOther() {
		Intent intent = getIntent();
		if (intent != null) {
			Log.d("tag", "intent!=null");
			strDID = intent.getStringExtra("did");
			strDate = intent.getStringExtra("date");
			strCameraName = intent
					.getStringExtra(ContentCommon.STR_CAMERA_NAME);
			position = intent.getIntExtra("position", 0);
			arrayList = (ArrayList<Map<String, Object>>) intent
					.getSerializableExtra("list");

		} else {
			Log.d("tag", "intent==null");
		}

	}

	private void findView() {
		mGallery = (MyGallery) findViewById(R.id.showlocalpic_gallery);
		mTv_TakeTime = (TextView) findViewById(R.id.takepic_time);
		mTv_TakeDate = (TextView) findViewById(R.id.takepic_date);
		mTv_Sum = (TextView) findViewById(R.id.picdesc);
		btnBack = (Button) findViewById(R.id.back);
		tvTitle = (TextView) findViewById(R.id.takepic_title);
		// topLayout = (RelativeLayout) findViewById(R.id.top);
		// bottomLayout = (RelativeLayout) findViewById(R.id.bottom);
		tvNoPics = (TextView) findViewById(R.id.localpic_tv_nopic);
		btnBack.setOnClickListener(this);
		if (strCameraName.equals(getResources().getString(
				R.string.local_all_picture_all))) {
			mTv_TakeTime.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.back:
//			Intent data = new Intent();
//			data.putExtra("dellist", delList);
//			setResult(1, data);
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//			break;
//
//		default:
//			break;
//		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent data = new Intent();
			data.putExtra("dellist", delList);
			setResult(1, data);
		}
		return super.onKeyDown(keyCode, event);
	}

	private String getContent(String filePath) {
		String s = filePath.substring(filePath.lastIndexOf("/") + 1);
		String result = s.substring(11, 16).replace("_", ":");
		return result;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		this.position = position;
		Map<String, Object> map = arrayList.get(position);
		String path = (String) map.get("path");
		String content = getContent(path);
		mTv_TakeTime.setText(content);
		mTv_Sum.setText((position + 1) + "/" + arrayList.size());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	// private void showDeletDialog(final int type, final int position) {
	// AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	// dialog.setMessage(R.string.exit_alert);
	// dialog.setPositiveButton(R.string.str_ok,
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// int tempposition = position;
	// switch (type) {
	//
	// case DELETE: {
	// Log.d("tag", "arrayList.size:" + arrayList.size());
	// Map<String, Object> map = arrayList.get(position);
	// String filePath = (String) map.get("path");
	// if (helper.deleteVideoOrPicture(strDID, filePath,
	// DataBaseHelper.TYPE_PICTURE)) {
	// if (deletePicInterface != null) {
	// deletePicInterface.delPic(filePath, false);
	// }
	// File file = new File(filePath);
	// if (file != null) {
	// file.delete();
	// if (1 == arrayList.size()) {
	// mTv_TakeTime
	// .setVisibility(View.INVISIBLE);
	// mTv_Sum.setText(desc + "0");
	// map.clear();
	// arrayList.remove(position);
	// mAdapter.notifyDataSetChanged();
	// } else if (position == arrayList.size() - 1) {
	// Map<String, Object> map2 = arrayList
	// .get(position - 1);
	// String path = (String) map2.get("path");
	// String content = getContent(path);
	// mTv_TakeTime.setText(content);
	// map.clear();
	// arrayList.remove(position);
	// mTv_Sum.setText(desc + arrayList.size()
	// + "/" + tempposition);
	// mAdapter.notifyDataSetChanged();
	// } else {
	// Map<String, Object> map2 = arrayList
	// .get(position + 1);
	// String path = (String) map2.get("path");
	// String content = getContent(path);
	// mTv_TakeTime.setText(content);
	// tempposition += 1;
	// map.clear();
	// arrayList.remove(position);
	// mTv_Sum.setText(desc + arrayList.size()
	// + "/" + tempposition);
	// mAdapter.notifyDataSetChanged();
	// }
	// }
	// }
	// }
	// break;
	// case DELETEALL: {
	// for (int i = 0; i < arrayList.size(); i++) {
	// Map<String, Object> map = arrayList.get(i);
	// String filePath = (String) map.get("path");
	// Log.d(TAG, "filePath:" + filePath);
	// if (helper.deleteVideoOrPicture(strDID,
	// filePath, DataBaseHelper.TYPE_PICTURE)) {
	// Log.d(TAG, "delete all: position:" + i);
	// File file = new File(filePath);
	// if (file != null) {
	// file.delete();
	// }
	// } else {
	// Log.d(TAG, "delete fail:" + i);
	// }
	// map.clear();
	// }
	// if (deletePicInterface != null) {
	// deletePicInterface.delPic("", true);
	// }
	// arrayList.clear();
	// mTv_TakeTime.setVisibility(View.INVISIBLE);
	// mTv_Sum.setText(desc + "0");
	// mAdapter.notifyDataSetChanged();
	//
	// }
	// break;
	// }
	// if (arrayList.size() == 0) {
	// tvNoPics.setVisibility(View.VISIBLE);
	// isShowing = true;
	// topLayout.setVisibility(View.VISIBLE);
	// bottomLayout.setVisibility(View.VISIBLE);
	// }
	// }
	// });
	// dialog.setNegativeButton(R.string.str_cancel,
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	//
	// }
	// });
	// dialog.show();
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (arrayList != null) {
			arrayList.clear();
			arrayList = null;
		}
		if (delList != null) {
			delList.clear();
			delList = null;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration arg0) {
		super.onConfigurationChanged(arg0);
		mAdapter = null;

		mAdapter = new ShowLocPicGalleryAdapter(this, arrayList);
		mGallery.setVerticalFadingEdgeEnabled(false);// ȡ����ֱ����߿�
		mGallery.setHorizontalFadingEdgeEnabled(false);// ȡ��ˮƽ����߿�
		mGallery.setAdapter(mAdapter);
		mGallery.setOnItemSelectedListener(this);
		mGallery.setOnCreateContextMenuListener(this);
		mGallery.setMyTouch(this);
		mGallery.setSelection(position);
	}

	@Override
	public void myTouch(MotionEvent event) {
		Log.d("tgg", "ShowLoc myTouch ");
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			Log.d("tgg", "ShowLoc down ");
			x1 = event.getX();
			y1 = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			Log.d("tgg", "ShowLoc up ");
			if (Math.abs((x1 - x2)) < 25 && Math.abs((y1 - y2)) < 25) {
				Log.d("tgg", "ShowLoc ���� ");
				if (isShowing) {
					Log.d("tgg", "ShowLoc ���� ");
					isShowing = false;
					// topLayout.setVisibility(View.GONE);
					// bottomLayout.setVisibility(View.GONE);
				} else {
					Log.d("tgg", "ShowLoc ��ʾ ");
					isShowing = true;
					// topLayout.setVisibility(View.VISIBLE);
					// bottomLayout.setVisibility(View.VISIBLE);
				}
			} else {
				Log.d("tgg", "ShowLoc up fling ");
			}
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("tgg", "ShowLoc move ");
			x2 = event.getX();
			y2 = event.getY();
			break;
		default:
			Log.d("tgg", "ShowLoc �����¼� ");
			break;
		}
	}

	// ɾ��ͼƬ�Ľӿ�
	public static void setDeletePicInterface(DeletePicInterface dpi) {
		deletePicInterface = dpi;
	}

	private static DeletePicInterface deletePicInterface;

	public interface DeletePicInterface {
		abstract public void delPic(String path, boolean isAll);
	}
}
