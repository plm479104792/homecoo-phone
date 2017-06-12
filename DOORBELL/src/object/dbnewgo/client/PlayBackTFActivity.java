package object.dbnewgo.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import object.dbnewgo.client.BridgeService.PlayBackTFInterface;
import object.p2pipcam.adapter.PlaybackTFAdapter;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayBackTFActivity extends BaseActivity implements
		OnClickListener, OnGroupClickListener, OnChildClickListener,
		OnGroupCollapseListener, OnGroupExpandListener, OnScrollListener,
		PlayBackTFInterface {
	private Button btnBack = null;
	private TextView tvNoVideo = null;
	private ProgressDialog progressDialog = null;
	private int TIMEOUT = 8000;
	private final int PARAMS = 1;
	private boolean successFlag = false;
	private long startTime = 0;
	private long endTime = 0;
	private String strName = null;
	private String strDID = null;
	private TextView tvTitle = null;
	private ExpandableListView exListView = null;
	private PlaybackTFAdapter mExAdapter = null;
	private int indicatorGroupHeight;
	private int the_group_expand_position = -1;
	private int count_expand = 0;
	private Map<Integer, Integer> expandGroup = new HashMap<Integer, Integer>();// �򿪵�groupλ��
	private boolean isFirstOn = false;
	private int nPageCount;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PARAMS:
				successFlag = true;
				if (progressDialog.isShowing()) {
					progressDialog.cancel();
				}
				mExAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	};
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				progressDialog.dismiss();
				if (mExAdapter.getGroupCount() > 0) {
					exListView.setVisibility(View.VISIBLE);
					tvNoVideo.setVisibility(View.GONE);
				} else {
					exListView.setVisibility(View.VISIBLE);
					tvNoVideo.setVisibility(View.GONE);
				}
			}
		}
	};
	private RelativeLayout floatHeaderView = null;
	private ImageView headerImg = null;
	private TextView headerTitle = null;
	private TextView headerSum = null;

	@Override
	protected void onPause() {
		// overridePendingTransition(R.anim.out_to_right,
		// R.anim.in_from_left);// �˳�����
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// showAll(btnBack, "PlayBackTFActivity");
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.playbacktf);
		// setEdgeFromLeft();
		findView();
		View v = new View(this);
		exListView.addHeaderView(v);
		mExAdapter = new PlaybackTFAdapter(this);
		exListView.setAdapter(mExAdapter);
		exListView.setGroupIndicator(null);

		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.remote_video_getparams));
		progressDialog.show();
		mHandler.postDelayed(runnable, TIMEOUT);
		setListener();
		tvTitle.setText(strName);
		initDate();
		BridgeService.setPlayBackTFInterface(this);
		NativeCaller.PPPPGetSDCardRecordFileList(strDID, 0, 128);
	}

	private void initDate() {
		int byear = 0;
		int bmonth = 0;
		int bday = 0;

		Calendar calendar = Calendar.getInstance();
		int eyear = calendar.get(Calendar.YEAR);
		int emonth = calendar.get(Calendar.MONTH);
		int eday = calendar.get(Calendar.DAY_OF_MONTH);
		if (eday == 1) {// �ϸ��µ����һ��
			Calendar ca2 = new GregorianCalendar(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH) - 1, 1);
			byear = ca2.get(Calendar.YEAR);
			bmonth = ca2.get(Calendar.MONTH);
			bday = ca2.getActualMaximum(Calendar.DAY_OF_MONTH);
		} else {
			byear = eyear;
			bmonth = emonth;
			bday = eday - 1;
		}
		Calendar bca = new GregorianCalendar(byear, bmonth, bday);
		Calendar eca = new GregorianCalendar(eyear, emonth, eday);
		Date bdate = bca.getTime();
		Date edate = eca.getTime();
		startTime = bdate.getTime();
		endTime = edate.getTime();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String strDateBegin = f.format(bdate);
		String strDateEnd = f.format(edate);
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		String strPwd = intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
		String strUser = intent.getStringExtra(ContentCommon.STR_CAMERA_USER);
		Log.d("tag", "PlayBackTFActivity  strName:" + strName + " strDID:"
				+ strDID + " strPwd:" + strPwd + " strUser:" + strUser);
	}

	private void setListener() {
		// listView.setOnItemClickListener(this);
		btnBack.setOnClickListener(this);
		exListView.setOnGroupCollapseListener(this);
		exListView.setOnGroupExpandListener(this);
		exListView.setOnGroupClickListener(this);
		exListView.setOnChildClickListener(this);
		exListView.setOnScrollListener(this);
		progressDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}

		});

		floatHeaderView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				floatHeaderView.setVisibility(View.GONE);
				exListView.collapseGroup(the_group_expand_position);
				exListView.setSelectedGroup(the_group_expand_position);
			}
		});
	}

	private void findView() {
		exListView = (ExpandableListView) findViewById(R.id.listview);
		btnBack = (Button) findViewById(R.id.back);
		tvNoVideo = (TextView) findViewById(R.id.no_video);
		tvTitle = (TextView) findViewById(R.id.tv_title);

		floatHeaderView = (RelativeLayout) findViewById(R.id.floatHeaderView);
		headerImg = (ImageView) findViewById(R.id.header_img);
		headerTitle = (TextView) findViewById(R.id.header_tv_date);
		headerSum = (TextView) findViewById(R.id.header_tv_sum);
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.back:
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//			break;
//		default:
//			break;
//		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onGroupClick(ExpandableListView arg0, View v, int position,
			long arg3) {
		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView arg0, View arg1,
			int groupPosition, int childPosition, long arg4) {
		String filepath = mExAdapter.getChildFilePath(groupPosition,
				childPosition);
		Intent intent = new Intent(this, PlayBackActivity.class);
		intent.putExtra("did", strDID);
		intent.putExtra("filepath", filepath);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		return false;
	}

	@Override
	public void onGroupCollapse(int groupPosition) {

		expandGroup.remove(groupPosition);
		exListView.setSelectedGroup(groupPosition);
		count_expand = expandGroup.size();
		Log.d("tag", "count_expand:" + count_expand);
	}

	@Override
	public void onGroupExpand(int groupPosition) {
		the_group_expand_position = groupPosition;
		Log.d("tag", "the_group_expand_position:" + the_group_expand_position);
		expandGroup.put(groupPosition, groupPosition);
		Log.d("tag", "expandGroup.size:" + expandGroup.size());
		count_expand = expandGroup.size();
		Log.d("tag", "count_expand:" + count_expand);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.d("tag", "onScroll: 1");
		// ��ֹ����,������ֻ��һ����Ŀ����һֱ������,����Ŀ����ͬʱ���ֵ�����
		if (firstVisibleItem == 0) {
			floatHeaderView.setVisibility(View.GONE);
		}
		Log.d("tag", "onScroll: 2");
		// ���ƻ���ʱTextView����ʾ������
		int npos = view.pointToPosition(0, 0);
		if (npos != AdapterView.INVALID_POSITION) {
			Log.d("tag", "onScroll: 3");
			long pos = exListView.getExpandableListPosition(npos);
			int childPos = ExpandableListView.getPackedPositionChild(pos);
			final int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			if (childPos == AdapterView.INVALID_POSITION) {
				Log.d("tag", "onScroll: 4");
				View groupView = exListView.getChildAt(npos
						- exListView.getFirstVisiblePosition());
				indicatorGroupHeight = groupView.getHeight();
			}
			Log.d("tag", "onScroll: 5");
			if (indicatorGroupHeight == 0) {
				Log.d("tag", "onScroll: 6");
				return;
			}
			Log.d("tag", "onScroll: 7");
			if (count_expand > 0) {

				Log.d("tag", "onScroll: 8");
				the_group_expand_position = groupPos;
				String[] ss = mExAdapter
						.getGroupTitleAndSum(the_group_expand_position);
				headerTitle.setText(ss[0]);
				headerSum.setText(ss[1]);
				if (the_group_expand_position != groupPos
						|| !exListView.isGroupExpanded(groupPos)) {
					floatHeaderView.setVisibility(View.GONE);
					Log.d("tag", "onScroll: 9");
				} else {
					Log.d("tag", "onScroll: 10");
					floatHeaderView.setVisibility(View.VISIBLE);
				}
			}
			Log.d("tag", "onScroll: 11");
			if (count_expand == 0) {
				Log.d("tag", "onScroll: 12");
				floatHeaderView.setVisibility(View.GONE);
			}
		}
		Log.d("tag", "onScroll: 13");
		if (the_group_expand_position == -1) {
			Log.d("tag", "onScroll: 14");
			return;
		}
		Log.d("tag", "onScroll: 15");
		int showHeight = getHeight();
		MarginLayoutParams layoutParams = (MarginLayoutParams) floatHeaderView
				.getLayoutParams();
		layoutParams.topMargin = -(indicatorGroupHeight - showHeight);
		floatHeaderView.setLayoutParams(layoutParams);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	private int getHeight() {
		int showHeight = indicatorGroupHeight;
		int nEndPos = exListView.pointToPosition(0, indicatorGroupHeight);
		if (nEndPos != AdapterView.INVALID_POSITION) {
			long pos = exListView.getExpandableListPosition(nEndPos);
			int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			if (groupPos != the_group_expand_position) {
				View viewNext = exListView.getChildAt(nEndPos
						- exListView.getFirstVisiblePosition());
				showHeight = viewNext.getTop();
			}
		}
		return showHeight;
	}

	/**
	 * BridgeService callback
	 * */
	@Override
	public void callBackRecordFileSearchResult(String did, String filename,
			int size, int nPageCount2, int bEnd) {
		Log.d("tag", "CallBack_RecordFileSearchResult");
		nPageCount = nPageCount2;
		if (strDID.equals(did)) {
			if (!isFirstOn) {
				isFirstOn = true;
				if (nPageCount > 1) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							for (int i = 0; i < nPageCount - 1; i++) {
								NativeCaller.PPPPGetSDCardRecordFileList(
										strDID, i + 1, 128);
							}
						}
					});
				}

			}
			String year = filename.substring(0, 4);
			String month = filename.substring(4, 6);
			String date = filename.substring(6, 8);
			String group = year + "-" + month + "-" + date;
			mExAdapter.addGroupAndChild(group, filename);
			if (bEnd == 1) {
				mHandler.sendEmptyMessage(PARAMS);
			}
		}
	}

}
