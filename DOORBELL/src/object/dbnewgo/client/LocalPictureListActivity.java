package object.dbnewgo.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import object.p2pipcam.adapter.LocalPictureAdapter;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.utils.DataBaseHelper;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 显示一台摄像机本地图片的列表
 * **/
public class LocalPictureListActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {
	private String TAG = "LocalPictureActivity";
	private String strDID;
	private String cameraName;
	private DataBaseHelper helper;
	private List<String> groupList;
	private Map<String, ArrayList<String>> childMap;
	private ListView mListView;
	private TextView tvNoPicture;
	private TextView tvCameraName;
	private Button back;
	private int wh;
	private boolean isFirstStart = false;
	private LocalPictureAdapter mAdapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 2:
				mAdapter.setOver(true);
				break;
			}
			mAdapter.notifyDataSetChanged();
		}
	};

	// @Override
	// protected void onPause() {
	// overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);//
	// 退出动画
	// super.onPause();
	// }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindowManager().getDefaultDisplay().getHeight();
		wh = width > height ? height : width;
		setContentView(R.layout.local_picture);
		// setEdgeFromLeft();
		helper = DataBaseHelper.getInstance(this);
		groupList = new ArrayList<String>();
		childMap = new HashMap<String, ArrayList<String>>();
		findView();
		tvCameraName.setText(cameraName);
		if (hasSdcard()) {
			initData();
		}
		mAdapter = new LocalPictureAdapter(this, groupList, childMap, wh / 5);
		mAdapter.setMode(1);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		Log.d("tag", "onCreate end");
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (isFirstStart) {
			if (hasSdcard()) {
				initData();
			}
			mAdapter.updateGroup(groupList);
			mAdapter.updateChild(childMap);
			mAdapter.initBmp();
		} else {
			isFirstStart = true;
		}
		if (groupList.size() > 0) {
			Log.d("tag", "groupList.size():" + groupList.size());
			tvNoPicture.setVisibility(View.GONE);

			// initBmp();
		} else {
			mListView.setVisibility(View.GONE);
			tvNoPicture.setVisibility(View.VISIBLE);
		}

	}

	private void findView() {
		mListView = (ListView) findViewById(R.id.localpic_listview);
		tvNoPicture = (TextView) findViewById(R.id.localpic_tv_nopic);
		tvCameraName = (TextView) findViewById(R.id.tv_title);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
	}

	private void getDataFromOther() {

		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
		Log.d("tag", "strDID:" + strDID + " cameraName:" + cameraName);
	}

	private void initData() {
		groupList.clear();
		childMap.clear();
		Cursor cursor = helper.queryAllPicture(strDID);
		int i = 0;
		while (cursor.moveToNext()) {
			i++;
			String filePath = cursor.getString(cursor
					.getColumnIndex(DataBaseHelper.KEY_FILEPATH));
			File file = null;
			try {
				file = new File(filePath);
				if (file == null || !file.exists()) {
					boolean delResult = helper.deleteVideoOrPicture(strDID,
							filePath, DataBaseHelper.TYPE_PICTURE);
					Log.d(TAG, "delResult:" + delResult);
					continue;
				}
			} catch (Exception e) {

			}
			String s1 = filePath.substring(filePath.lastIndexOf("/") + 1);
			String date = s1.substring(0, 10);
			if (!groupList.contains(date)) {
				groupList.add(date);
				ArrayList<String> list = new ArrayList<String>();
				list.add(filePath);
				childMap.put(date, list);
			} else {
				childMap.get(date).add(filePath);
			}

		}
		Collections.sort(groupList, new Comparator<String>() {

			@Override
			public int compare(String object1, String object2) {
				return object2.compareTo(object1);
			}
		});
		if (cursor != null) {
			cursor.close();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// showAll(back, "LocalPictureListActivity");
		super.onResume();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String date = groupList.get(position);
		ArrayList<String> arrayList = childMap.get(date);
		Intent intent = new Intent(this, ShowLocalPicGridActivity.class);
		intent.putExtra("did", strDID);
		intent.putExtra("list", arrayList);
		intent.putExtra("date", date);
		intent.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	public void onClick(View arg0) {
		finish();
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		groupList.clear();
		groupList = null;
		childMap.clear();
		childMap = null;
		mAdapter = null;
	}
}
