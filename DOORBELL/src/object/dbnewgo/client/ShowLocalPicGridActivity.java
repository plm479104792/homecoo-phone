package object.dbnewgo.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import object.dbnewgo.client.ShowLocalPictureActivity.DeletePicInterface;
import object.p2pipcam.adapter.ShowLocPicGridViewAdapter;
import object.p2pipcam.content.ContentCommon;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ����ͼƬ����ͼ
 * */
public class ShowLocalPicGridActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener,
		DeletePicInterface {
	private String strDID;
	private String strDate;
	private ArrayList<String> aList;
	private Button btnBack;
	private TextView tvTakePicTime;
	private TextView tvSelectSum;
	private TextView tvNoPics;
	private GridView gridView;
	private String strCameraName;
	private ShowLocPicGridViewAdapter mAdapter;
	private Button btnSelectAll;
	private Button btnSelectReverse;
	private Button btnDel;
	public static ArrayList<Map<String, Object>> arrayList;
	private Button btnEdit;
	private LinearLayout layoutDel;
	private int seletNum;
	private boolean isEditing = false;
	private int position = -1;
	public boolean bthread = true;
	private Thread thread = null;
	private ArrayList<Map<String, Object>> delPics = null;
	// private ProgressDialog dialog = null;
	private int tag = 1;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdapter.notifyDataSetChanged();
		}
	};
	private Matrix matrix;

	class MyThread extends Thread {
		@Override
		public void run() {
			for (int i = 0; i < arrayList.size() && bthread == true; i++) {
				Map<String, Object> map = arrayList.get(i);
				String path = (String) map.get("path");
				// Bitmap btp = BitmapFactory.decodeFile(path);
				// int btmWidth = btp.getWidth();
				// int btmHeight = btp.getHeight();
				// float scaleW = ((float) 140) / btmWidth;
				// float scaleH = ((float) 120) / btmHeight;
				// Matrix matrix = new Matrix();
				// matrix.postScale(scaleW, scaleH);
				// Bitmap bt = Bitmap.createBitmap(btp, 0, 0, btmWidth,
				// btmHeight,
				// matrix, true);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 6;
				Bitmap bitmap = returnBitmap(BitmapFactory.decodeFile(path,
						options));
				if (bitmap == null) {
					continue;
				}
				mAdapter.addBitmap(bitmap, path, 0);
				if (bthread == true) {
					handler.sendEmptyMessage(1);
				}
			}
			super.run();
		}
	}

	private Bitmap returnBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		if (bitmap.getWidth() < 650 || bitmap.getHeight() < 490) {
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} else {
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, 640, 480);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}
		return bitmap;
	}

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.showlocalpicgrid);
		// setEdgeFromLeft();
		findView();
		setListener();
		mAdapter = new ShowLocPicGridViewAdapter(this, strDID);
		mAdapter.setMode(1);// ��ʾ��ͼƬ����¼��
		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemLongClickListener(this);
		thread = new MyThread();
		thread.start();
		ShowLocalPictureActivity.setDeletePicInterface(this);
		// dialog = new ProgressDialog(this);
		// dialog.setMessage(getResources().getString(R.string.pict_del_show));
		// dialog.setCancelable(false);
		matrix = new Matrix();
		// ����ԭͼ
		matrix.postScale(0.4f, 0.3f);
		progressDialog = new ProgressDialog(ShowLocalPicGridActivity.this);
		progressDialog.setMessage(getResources().getString(
				R.string.main_show_delecting));
		progressDialog.setCancelable(false);

	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("tag", "onStart  arrayList.size:" + arrayList.size());
		tvTakePicTime.setText(strDate);
		if (arrayList.size() == 0) {
			finish();
		}
	}

	private void setListener() {
		btnBack.setOnClickListener(this);
		btnSelectAll.setOnClickListener(this);
		btnSelectReverse.setOnClickListener(this);
		btnDel.setOnClickListener(this);
		btnEdit.setOnClickListener(this);
	}

	private void findView() {
		btnBack = (Button) findViewById(R.id.back);
		tvTakePicTime = (TextView) findViewById(R.id.tv_time);
		tvSelectSum = (TextView) findViewById(R.id.tv_select_sum);
		gridView = (GridView) findViewById(R.id.gridView1);
		btnSelectAll = (Button) findViewById(R.id.selectall);
		btnSelectReverse = (Button) findViewById(R.id.selectreverse);
		btnDel = (Button) findViewById(R.id.delete);
		tvNoPics = (TextView) findViewById(R.id.localpic_tv_nopic);

		btnEdit = (Button) findViewById(R.id.edit);
		layoutDel = (LinearLayout) findViewById(R.id.del_bottom_layout);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isEditing) {
				if (progressDialog != null) {
					progressDialog.cancel();
				}
				seletNum = 0;
				tvSelectSum.setVisibility(View.GONE);
				isEditing = false;
				layoutDel.setVisibility(View.GONE);
				ArrayList<Map<String, Object>> arrayPics = mAdapter
						.getArrayPics();
				for (int i = 0; i < arrayPics.size(); i++) {
					Map<String, Object> map = arrayPics.get(i);
					map.put("status", 0);
				}
				mAdapter.notifyDataSetChanged();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.back:
//			if (isEditing) {
//				seletNum = 0;
//				tvSelectSum.setVisibility(View.GONE);
//				isEditing = false;
//				layoutDel.setVisibility(View.GONE);
//				ArrayList<Map<String, Object>> arrayPics = mAdapter
//						.getArrayPics();
//				for (int i = 0; i < arrayPics.size(); i++) {
//					Map<String, Object> map = arrayPics.get(i);
//					map.put("status", 0);
//				}
//				mAdapter.notifyDataSetChanged();
//			} else {
//				finish();
//				overridePendingTransition(R.anim.out_to_right,
//						R.anim.in_from_left);
//				// �˳�����
//			}
//			break;
//		case R.id.selectall: {
//			ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();
//			for (int i = 0; i < arrayPics.size(); i++) {
//				Map<String, Object> map = arrayPics.get(i);
//				Map<String, Object> map2 = arrayList.get(i);
//				int status = (Integer) map.get("status");
//				if (status != 1) {
//					map.put("status", 1);
//					map2.put("status", 1);
//				}
//			}
//			seletNum = arrayPics.size();
//			tvSelectSum.setText(String.valueOf(seletNum));
//			mAdapter.notifyDataSetChanged();
//		}
//			break;
//		case R.id.selectreverse: {
//			ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();
//			seletNum = 0;
//			for (int i = 0; i < arrayPics.size(); i++) {
//				Map<String, Object> map = arrayPics.get(i);
//				Map<String, Object> map2 = arrayList.get(i);
//				int status = (Integer) map.get("status");
//				switch (status) {
//				case 0:
//					seletNum++;
//					map2.put("status", 1);
//					map.put("status", 1);
//					break;
//				case 1:
//					map.put("status", 0);
//					map2.put("status", 0);
//					break;
//
//				default:
//					break;
//				}
//			}
//			tvSelectSum.setText(String.valueOf(seletNum));
//			mAdapter.notifyDataSetChanged();
//		}
//			break;
//		case R.id.delete: {
//			Log.d("tag", "delete");
//			seletNum = 0;
//			tvSelectSum.setVisibility(View.GONE);
//			new AsyncTask<Void, Void, Void>() {
//				@Override
//				protected void onPreExecute() {
//					// dialog.show();
//					seletNum = 0;
//					tvSelectSum.setVisibility(View.GONE);
//					if (progressDialog != null) {
//						progressDialog.show();
//					}
//
//					super.onPreExecute();
//				}
//
//				@Override
//				protected Void doInBackground(Void... arg0) {
//					delPics = mAdapter.DelPics();
//					return null;
//				}
//
//				@Override
//				protected void onPostExecute(Void arg0) {
//					// dialog.cancel();
//					tag = 1;
//					if (delPics.size() == 0) {
//						tvNoPics.setVisibility(View.VISIBLE);
//						isEditing = false;
//						layoutDel.setVisibility(View.GONE);
//					} else {
//						boolean flag = true;
//						for (int i = 0; i < delPics.size() && flag; i++) {
//							Map<String, Object> map = delPics.get(i);
//							int status = (Integer) map.get("status");
//							if (status == 1) {
//								flag = false;
//							}
//						}
//						if (flag) {
//							isEditing = false;
//							layoutDel.setVisibility(View.GONE);
//						}
//					}
//					if (progressDialog != null && progressDialog.isShowing()) {
//						progressDialog.cancel();
//					}
//					mAdapter.notifyDataSetChanged();
//					super.onPostExecute(arg0);
//				}
//			}.execute();
//
//		}
//			break;
//		case R.id.edit:
//			if (isEditing) {
//				btnEdit.setText(getResources().getString(R.string.main_edit));
//				layoutDel.setVisibility(View.GONE);
//				isEditing = false;
//			} else {
//				btnEdit.setText(getResources().getString(R.string.done));
//				layoutDel.setVisibility(View.VISIBLE);
//				isEditing = true;
//				layoutDel.setVisibility(View.GONE);
//			}
//			break;
//		default:
//			break;
//		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		super.onPause();
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra("did");
		strDate = intent.getStringExtra("date");
		strCameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
		aList = (ArrayList<String>) intent.getSerializableExtra("list");
		arrayList = new ArrayList<Map<String, Object>>();
		if (aList.size() > 1000) {
			for (int i = 0; i < 1001; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				String path = aList.get(i);
				map.put("path", path);
				map.put("status", 0);
				arrayList.add(map);
			}
		} else {
			for (int i = 0; i < aList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				String path = aList.get(i);
				map.put("path", path);
				map.put("status", 0);
				arrayList.add(map);
			}
		}
		// aList.clear();
		// aList = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Log.d("tag", "onItemClick");
		Log.d("tag", "this.position:" + this.position);
		if (!isEditing) {
			if (this.position != position) {
				this.position = -1;
				Intent intent = new Intent(this, ShowLocalPictureActivity.class);
				intent.putExtra("did", strDID);
				intent.putExtra("list", arrayList);
				intent.putExtra("date", strDate);
				intent.putExtra("position", position);
				intent.putExtra(ContentCommon.STR_CAMERA_NAME, strCameraName);
				startActivityForResult(intent, 1);
			} else {
				this.position = -1;
			}
		} else {
			if (this.position != position) {
				this.position = -1;
				ArrayList<Map<String, Object>> arrayPics = mAdapter
						.getArrayPics();
				Map<String, Object> map = arrayPics.get(position);
				Map<String, Object> map2 = arrayList.get(position);
				int status = (Integer) map.get("status");
				if (status == 0) {
					map.put("status", 1);
					map2.put("status", 1);
					seletNum++;
				} else {
					seletNum--;
					map.put("status", 0);
					map2.put("status", 0);
				}
				tvSelectSum.setText(String.valueOf(seletNum));
				mAdapter.notifyDataSetChanged();
				checkSelect();
			} else {
				this.position = -1;

			}
		}

	}

	private void checkSelect() {
		// ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();
		for (int i = 0; i < arrayList.size(); i++) {
			Map<String, Object> map = arrayList.get(i);
			int status = (Integer) map.get("status");
			Log.d("tag", "checkSelect status:" + status);
			if (status == 1) {
				return;
			}
		}
		tvSelectSum.setVisibility(View.GONE);
		layoutDel.setVisibility(View.GONE);
		isEditing = false;
	}

	@Override
	protected void onDestroy() {
		bthread = false;
		// if (arrayList != null) {
		// arrayList.clear();
		// arrayList = null;
		// }
		super.onDestroy();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		this.position = position;
		layoutDel.setVisibility(View.VISIBLE);
		isEditing = true;
		Log.d("tag", "onItemLongClick");
		ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();
		Map<String, Object> map2 = arrayList.get(position);
		Map<String, Object> map = arrayPics.get(position);
		int status = (Integer) map.get("status");
		Log.d("tag", "status:" + status);
		if (status == 0) {
			seletNum++;
			map.put("status", 1);
			map2.put("status", 1);
		} else {
			seletNum--;
			map.put("status", 0);
			map2.put("status", 0);
		}
		tvSelectSum.setVisibility(View.VISIBLE);
		tvSelectSum.setText(String.valueOf(seletNum));
		mAdapter.notifyDataSetChanged();
		checkSelect();
		return false;
	}

	@Override
	public void delPic(String path, boolean isAll) {
		Log.d("tag", "ShowLocalPicGrid  path:" + path);
		if (!isAll) {
			ArrayList<Map<String, Object>> arrayPics = mAdapter.getArrayPics();
			for (int i = 0; i < arrayPics.size(); i++) {
				Map<String, Object> map = arrayPics.get(i);
				String pa = (String) map.get("path");
				if (path.equals(pa)) {
					map.clear();
					arrayPics.remove(i);
					arrayList.remove(i);
					mAdapter.notifyDataSetChanged();
					return;
				}
			}
		} else {
			mAdapter.clearAll();
			mAdapter.notifyDataSetChanged();
		}
	}
}
