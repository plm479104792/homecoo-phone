package object.dbnewgo.client;

import java.util.ArrayList;
import java.util.List;

import object.dbnewgo.client.BridgeService.SDCardInterface;
import object.p2pipcam.bean.SdcardBean;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingSDCardActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener, SDCardInterface {
	private TextView tvSdTotal = null;
	private TextView tvSdRemain = null;
	private TextView tvSdStatus = null;
	// private Button btnFormat = null;
	private CheckBox cbxConverage = null;
	private EditText editRecordLength = null;
	private CheckBox cbxRecordTime = null;
	private Button btnBack = null;
	private Button btnOk = null;
	private final int TIMEOUT = 3000;
	private String strDID = null;// camera id
	// private String cameraName = null;
	private ProgressDialog progressDialog = null;
	private ProgressDialog progressDialog1 = null;
	private boolean successFlag = false;// ��ȡ�����õĽ��
	private final int FAILED = 0;
	private final int SUCCESS = 1;
	private final int PARAMS = 2;
	private Button set_sd_format;
	private int time7, time15, time23;
	private int m_end_time = 23;
	private int m_start_time = 0;
	private RelativeLayout linearLayout_end;
	private LinearLayout linearLayout_start;
	private List<String> list, list1 = null;
	private Spinner spinner_start, spinner_end;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FAILED:
				showToast(R.string.sdcard_set_failed);
				break;
			case SUCCESS:
				showToast(R.string.sdcard_set_success);
				finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);// �˳�����
				break;
			case PARAMS:
				if (ifShow == 1) {
					linearLayout_start.setVisibility(View.VISIBLE);
					linearLayout_end.setVisibility(View.VISIBLE);
				} else {
					linearLayout_start.setVisibility(View.GONE);
					linearLayout_end.setVisibility(View.GONE);
				}
				successFlag = true;
				progressDialog.dismiss();
				tvSdTotal.setText(sdcardBean.getSdtotal() + "M");
				tvSdRemain.setText(sdcardBean.getSdfree() + "M");
				if (sdcardBean.getRecord_sd_status() == 1) {
					tvSdStatus
							.setText(SettingSDCardActivity.this.getResources()
									.getString(R.string.sdcard_inserted));
				} else {
					tvSdStatus.setText(SettingSDCardActivity.this
							.getResources().getString(
									R.string.sdcard_no_inserted));
				}
				if (sdcardBean.getRecord_conver_enable() == 1) {
					cbxConverage.setChecked(true);
				} else {
					cbxConverage.setChecked(false);
				}
				if (sdcardBean.getRecord_time_enable() == 1) {
					cbxRecordTime.setChecked(true);
				} else {
					cbxRecordTime.setChecked(false);
				}
				editRecordLength.setText(sdcardBean.getRecord_timer() + "");
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settingsdcard);
		// setEdgeFromLeft();
		list = new ArrayList<String>();
		list1 = new ArrayList<String>();
		listAdd();
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.sdcard_getparams));
		progressDialog.show();
		sdcardBean = new SdcardBean();
		handler.postDelayed(runnable, TIMEOUT);
		findView();
		setLister();
		BridgeService.setSDCardInterface(this);
		NativeCaller.PPPPGetSystemParams(strDID,
				ContentCommon.MSG_TYPE_GET_RECORD);
	}

	private void listAdd() {
		// TODO Auto-generated method stub
		list.add("00:00");
		list.add("01:00");
		list.add("02:00");
		list.add("03:00");
		list.add("04:00");
		list.add("05:00");
		list.add("06:00");
		list.add("07:00");
		list.add("08:00");
		list.add("09:00");
		list.add("10:00");
		list.add("11:00");
		list.add("12:00");
		list.add("13:00");
		list.add("14:00");
		list.add("15:00");
		list.add("16:00");
		list.add("17:00");
		list.add("18:00");
		list.add("19:00");
		list.add("20:00");
		list.add("21:00");
		list.add("22:00");
		list.add("23:00");

		list1.add("01:00");
		list1.add("02:00");
		list1.add("03:00");
		list1.add("04:00");
		list1.add("05:00");
		list1.add("06:00");
		list1.add("07:00");
		list1.add("08:00");
		list1.add("09:00");
		list1.add("10:00");
		list1.add("11:00");
		list1.add("12:00");
		list1.add("13:00");
		list1.add("14:00");
		list1.add("15:00");
		list1.add("16:00");
		list1.add("17:00");
		list1.add("18:00");
		list1.add("19:00");
		list1.add("20:00");
		list1.add("21:00");
		list1.add("22:00");
		list1.add("23:00");
		list1.add("24:00");

	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		// cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (!successFlag) {
				successFlag = false;
				progressDialog.dismiss();
			}
		}
	};
	private SdcardBean sdcardBean;

	private void setLister() {
		btnBack.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		cbxConverage.setOnCheckedChangeListener(this);
		cbxRecordTime.setOnCheckedChangeListener(this);
		editRecordLength.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				try {
					String result = arg0.toString();
					if (result == null) {
						result = "0";
					}
					int reInt = Integer.parseInt(result);
					if (reInt < 5 || reInt > 120) {
						showToast(R.string.sdcard_range);
					}
					sdcardBean.setRecord_timer(reInt);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});
	}

	private void findView() {
		tvSdTotal = (TextView) findViewById(R.id.tv_sd_total);
		tvSdRemain = (TextView) findViewById(R.id.tv_sd_remain);
		tvSdStatus = (TextView) findViewById(R.id.tv_state);
		// btnFormat = (Button) findViewById(R.id.btn_format);
		cbxConverage = (CheckBox) findViewById(R.id.cbx_coverage);
		editRecordLength = (EditText) findViewById(R.id.edit_record_length);
		cbxRecordTime = (CheckBox) findViewById(R.id.cbx_record_time);
		btnBack = (Button) findViewById(R.id.back);
		btnOk = (Button) findViewById(R.id.ok);
		set_sd_format = (Button) findViewById(R.id.set_sd_format);
		linearLayout_start = (LinearLayout) findViewById(R.id.linearLayout_start);
		linearLayout_end = (RelativeLayout) findViewById(R.id.linearLayout_end);
		set_sd_format.setOnClickListener(this);
		spinner_start = (Spinner) findViewById(R.id.spinner_start);
		spinner_end = (Spinner) findViewById(R.id.spinner_end);
		spinner_start.setAdapter(showSpnner(list));
		spinner_end.setAdapter(showSpnner(list1));
		spinner_start.setSelection(0);
		spinner_end.setSelection(23);
		spinner_start
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						m_start_time = arg2;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		spinner_end
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						m_end_time = arg2 + 1;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

	}

	private ArrayAdapter<String> showSpnner(List<String> list) {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				SettingSDCardActivity.this,
				android.R.layout.simple_spinner_item, list);
		arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return arrayAdapter;
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.back:
//			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);// �˳�����
//			break;
//		case R.id.ok:
//			setSDCardSchedule();
//			// Log.d("test_sd", "m_start_time:" + m_start_time +
//			// "   m_end_time:"
//			// + m_end_time);
//			break;
//		case R.id.set_sd_format:
//			if (sdcardBean.getSdtotal() == 0) {
//				showToast(R.string.set_sd_format_show);
//				return;
//			}
//			new AsyncTask<Void, Void, Void>() {
//				protected void onPreExecute() {
//					NativeCaller.FormatSD(strDID);
//					progressDialog1 = new ProgressDialog(
//							SettingSDCardActivity.this);
//					progressDialog1
//							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//					progressDialog1
//							.setMessage(getString(R.string.set_sd_format_show1));
//					progressDialog1.setCancelable(false);
//					progressDialog1.show();
//				};
//
//				@Override
//				protected Void doInBackground(Void... params) {
//					try {
//						Thread.sleep(20000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					return null;
//				}
//
//				protected void onPostExecute(Void result) {
//					if (progressDialog1 != null && progressDialog1.isShowing()) {
//						progressDialog1.cancel();
//					}
//					Intent intent2 = new Intent("myback");
//					sendBroadcast(intent2);
//					finish();
//					overridePendingTransition(R.anim.out_to_right,
//							R.anim.in_from_left);// �˳�����
//				};
//			}.execute();
//
//			break;
//		default:
//			break;
//		}
	}

	private void setSDCardSchedule() {
		int record_timer = sdcardBean.getRecord_timer();
		if (record_timer > 120 || record_timer < 5) {
			showToast(R.string.sdcard_range);
			return;
		}
		// private int time7, time15, time23;
		if (m_end_time <= m_start_time) {
			showToast(R.string.set_sd_show4);
			return;
		}
		selectTime();
		NativeCaller.PPPPSDRecordSetting(strDID,
				sdcardBean.getRecord_conver_enable(),
				sdcardBean.getRecord_timer(), sdcardBean.getRecord_size(),
				sdcardBean.getRecord_time_enable(), time7, time15, time23,
				time7, time15, time23, time7, time15, time23, time7, time15,
				time23, time7, time15, time23, time7, time15, time23, time7,
				time15, time23);
	}

	private void selectTime() {
		switch (m_end_time) {
		case 1:
			time7 = 0x0000000f;
			time15 = 0x00000000;
			time23 = 0x00000000;
			break;
		case 2:
			switch (m_start_time) {
			case 0:
				time7 = 0x000000ff;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0x000000f0;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;

			default:
				break;
			}
			break;
		case 3:
			switch (m_start_time) {
			case 0:
				time7 = 0x00000fff;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0x00000ff0;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0x00000f00;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 4:
			switch (m_start_time) {
			case 0:
				time7 = 0x0000ffff;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0x0000fff0;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0x0000ff00;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0x0000f000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 5:
			switch (m_start_time) {
			case 0:
				time7 = 0x000fffff;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0x000ffff0;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0x000fff00;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0x000ff000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0x000f0000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}

			break;
		case 6:
			switch (m_start_time) {
			case 0:
				time7 = 0x00ffffff;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0x00fffff0;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0x00ffff00;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0x00fff000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0x00ff0000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0x00f00000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 7:
			switch (m_start_time) {
			case 0:
				time7 = 0x0fffffff;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0x0ffffff0;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0x0fffff00;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0x0ffff000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0x0fff0000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0x0ff00000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 6:
				time7 = 0x0f000000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 8:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0x00000000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 9:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0x0000000f;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0x0000000f;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0x0000000f;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0x0000000f;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0x0000000f;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0x0000000f;
				time23 = 0x00000000;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0x0000000f;
				time23 = 0x00000000;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0x0000000f;
				time23 = 0x00000000;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0x0000000f;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 10:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0x000000ff;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0x000000ff;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0x000000ff;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0x000000ff;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0x000000ff;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0x000000ff;
				time23 = 0x00000000;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0x000000ff;
				time23 = 0x00000000;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0x000000ff;
				time23 = 0x00000000;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0x000000ff;
				time23 = 0x00000000;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0x000000f0;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 11:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0x00000fff;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0x00000fff;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0x00000fff;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0x00000fff;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0x00000fff;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0x00000fff;
				time23 = 0x00000000;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0x00000fff;
				time23 = 0x00000000;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0x00000fff;
				time23 = 0x00000000;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0x00000fff;
				time23 = 0x00000000;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0x00000ff0;
				time23 = 0x00000000;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0x00000f00;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 12:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0x0000ffff;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0x0000ffff;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0x0000ffff;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0x0000ffff;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0x0000ffff;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0x0000ffff;
				time23 = 0x00000000;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0x0000ffff;
				time23 = 0x00000000;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0x0000ffff;
				time23 = 0x00000000;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0x0000ffff;
				time23 = 0x00000000;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0x0000fff0;
				time23 = 0x00000000;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0x0000ff00;
				time23 = 0x00000000;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0x0000f000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 13:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0x000fffff;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0x000fffff;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0x000fffff;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0x000fffff;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0x000fffff;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0x000fffff;
				time23 = 0x00000000;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0x000fffff;
				time23 = 0x00000000;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0x000fffff;
				time23 = 0x00000000;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0x000fffff;
				time23 = 0x00000000;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0x000ffff0;
				time23 = 0x00000000;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0x000fff00;
				time23 = 0x00000000;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0x000ff000;
				time23 = 0x00000000;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0x000f0000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 14:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0x00ffffff;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0x00ffffff;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0x00ffffff;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0x00ffffff;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0x00ffffff;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0x00ffffff;
				time23 = 0x00000000;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0x00ffffff;
				time23 = 0x00000000;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0x00ffffff;
				time23 = 0x00000000;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0x00ffffff;
				time23 = 0x00000000;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0x00fffff0;
				time23 = 0x00000000;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0x00ffff00;
				time23 = 0x00000000;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0x00fff000;
				time23 = 0x00000000;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0x00ff0000;
				time23 = 0x00000000;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0x00f00000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 15:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0x0fffffff;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0x0fffffff;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0x0fffffff;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0x0fffffff;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0x0fffffff;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0x0fffffff;
				time23 = 0x00000000;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0x0fffffff;
				time23 = 0x00000000;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0x0fffffff;
				time23 = 0x00000000;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0x0fffffff;
				time23 = 0x00000000;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0x0ffffff0;
				time23 = 0x00000000;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0x0fffff00;
				time23 = 0x00000000;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0x0ffff000;
				time23 = 0x00000000;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0x0fff0000;
				time23 = 0x00000000;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0x0ff00000;
				time23 = 0x00000000;
				break;
			case 14:
				time7 = 0x00000000;
				time15 = 0x0f000000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 16:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0xffffffff;
				time23 = 0x00000000;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0xffffffff;
				time23 = 0x00000000;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0xffffffff;
				time23 = 0x00000000;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0xffffffff;
				time23 = 0x00000000;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0xffffffff;
				time23 = 0x00000000;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0xffffffff;
				time23 = 0x00000000;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0xffffffff;
				time23 = 0x00000000;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0xffffffff;
				time23 = 0x00000000;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0xffffffff;
				time23 = 0x00000000;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0xfffffff0;
				time23 = 0x00000000;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0xffffff00;
				time23 = 0x00000000;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0xfffff000;
				time23 = 0x00000000;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0xffff0000;
				time23 = 0x00000000;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0xfff00000;
				time23 = 0x00000000;
				break;
			case 14:
				time7 = 0x00000000;
				time15 = 0xff000000;
				time23 = 0x00000000;
				break;
			case 15:
				time7 = 0x00000000;
				time15 = 0xf0000000;
				time23 = 0x00000000;
				break;
			default:
				break;
			}
			break;
		case 17:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0xffffffff;
				time23 = 0x0000000f;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0xffffffff;
				time23 = 0x0000000f;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0xffffffff;
				time23 = 0x0000000f;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0xffffffff;
				time23 = 0x0000000f;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0xffffffff;
				time23 = 0x0000000f;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0xffffffff;
				time23 = 0x0000000f;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0xffffffff;
				time23 = 0x0000000f;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0xffffffff;
				time23 = 0x0000000f;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0xffffffff;
				time23 = 0x0000000f;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0xfffffff0;
				time23 = 0x0000000f;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0xffffff00;
				time23 = 0x0000000f;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0xfffff000;
				time23 = 0x0000000f;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0xffff0000;
				time23 = 0x0000000f;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0xfff00000;
				time23 = 0x0000000f;
				break;
			case 14:
				time7 = 0x00000000;
				time15 = 0xff000000;
				time23 = 0x0000000f;
				break;
			case 15:
				time7 = 0x00000000;
				time15 = 0xf0000000;
				time23 = 0x0000000f;
				break;
			case 16:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0000000f;
				break;
			default:
				break;
			}
			break;
		case 18:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0xffffffff;
				time23 = 0x000000ff;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0xffffffff;
				time23 = 0x000000ff;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0xffffffff;
				time23 = 0x000000ff;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0xffffffff;
				time23 = 0x000000ff;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0xffffffff;
				time23 = 0x000000ff;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0xffffffff;
				time23 = 0x000000ff;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0xffffffff;
				time23 = 0x000000ff;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0xffffffff;
				time23 = 0x000000ff;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0xffffffff;
				time23 = 0x000000ff;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0xfffffff0;
				time23 = 0x000000ff;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0xffffff00;
				time23 = 0x000000ff;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0xfffff000;
				time23 = 0x000000ff;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0xffff0000;
				time23 = 0x000000ff;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0xfff00000;
				time23 = 0x000000ff;
				break;
			case 14:
				time7 = 0x00000000;
				time15 = 0xff000000;
				time23 = 0x000000ff;
				break;
			case 15:
				time7 = 0x00000000;
				time15 = 0xf0000000;
				time23 = 0x000000ff;
				break;
			case 16:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x000000ff;
				break;
			case 17:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x000000f0;
				break;
			default:
				break;
			}
			break;
		case 19:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0xffffffff;
				time23 = 0x00000fff;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0xffffffff;
				time23 = 0x00000fff;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0xffffffff;
				time23 = 0x00000fff;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0xffffffff;
				time23 = 0x00000fff;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0xffffffff;
				time23 = 0x00000fff;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0xffffffff;
				time23 = 0x00000fff;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0xffffffff;
				time23 = 0x00000fff;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0xffffffff;
				time23 = 0x00000fff;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0xffffffff;
				time23 = 0x00000fff;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0xfffffff0;
				time23 = 0x00000fff;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0xffffff00;
				time23 = 0x00000fff;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0xfffff000;
				time23 = 0x00000fff;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0xffff0000;
				time23 = 0x00000fff;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0xfff00000;
				time23 = 0x00000fff;
				break;
			case 14:
				time7 = 0x00000000;
				time15 = 0xff000000;
				time23 = 0x00000fff;
				break;
			case 15:
				time7 = 0x00000000;
				time15 = 0xf0000000;
				time23 = 0x00000fff;
				break;
			case 16:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x00000fff;
				break;
			case 17:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x00000ff0;
				break;
			case 18:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x00000f00;
				break;
			default:
				break;
			}
			break;
		case 20:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0xffffffff;
				time23 = 0x0000ffff;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0xffffffff;
				time23 = 0x0000ffff;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0xffffffff;
				time23 = 0x0000ffff;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0xffffffff;
				time23 = 0x0000ffff;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0xffffffff;
				time23 = 0x0000ffff;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0xffffffff;
				time23 = 0x0000ffff;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0xffffffff;
				time23 = 0x0000ffff;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0xffffffff;
				time23 = 0x0000ffff;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0xffffffff;
				time23 = 0x0000ffff;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0xfffffff0;
				time23 = 0x0000ffff;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0xffffff00;
				time23 = 0x0000ffff;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0xfffff000;
				time23 = 0x0000ffff;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0xffff0000;
				time23 = 0x0000ffff;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0xfff00000;
				time23 = 0x0000ffff;
				break;
			case 14:
				time7 = 0x00000000;
				time15 = 0xff000000;
				time23 = 0x0000ffff;
				break;
			case 15:
				time7 = 0x00000000;
				time15 = 0xf0000000;
				time23 = 0x0000ffff;
				break;
			case 16:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0000ffff;
				break;
			case 17:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0000fff0;
				break;
			case 18:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0000ff00;
				break;
			case 19:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0000f000;
				break;
			default:
				break;
			}
			break;
		case 21:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0xffffffff;
				time23 = 0x000fffff;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0xffffffff;
				time23 = 0x000fffff;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0xffffffff;
				time23 = 0x000fffff;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0xffffffff;
				time23 = 0x000fffff;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0xffffffff;
				time23 = 0x000fffff;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0xffffffff;
				time23 = 0x000fffff;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0xffffffff;
				time23 = 0x000fffff;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0xffffffff;
				time23 = 0x000fffff;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0xffffffff;
				time23 = 0x000fffff;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0xfffffff0;
				time23 = 0x000fffff;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0xffffff00;
				time23 = 0x000fffff;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0xfffff000;
				time23 = 0x000fffff;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0xffff0000;
				time23 = 0x000fffff;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0xfff00000;
				time23 = 0x000fffff;
				break;
			case 14:
				time7 = 0x00000000;
				time15 = 0xff000000;
				time23 = 0x000fffff;
				break;
			case 15:
				time7 = 0x00000000;
				time15 = 0xf0000000;
				time23 = 0x000fffff;
				break;
			case 16:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x000fffff;
				break;
			case 17:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x000ffff0;
				break;
			case 18:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x000fff00;
				break;
			case 19:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x000ff000;
				break;
			case 20:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x000f0000;
				break;
			default:
				break;
			}
			break;
		case 22:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0xffffffff;
				time23 = 0x00ffffff;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0xffffffff;
				time23 = 0x00ffffff;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0xffffffff;
				time23 = 0x00ffffff;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0xffffffff;
				time23 = 0x00ffffff;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0xffffffff;
				time23 = 0x00ffffff;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0xffffffff;
				time23 = 0x00ffffff;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0xffffffff;
				time23 = 0x00ffffff;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0xffffffff;
				time23 = 0x00ffffff;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0xffffffff;
				time23 = 0x00ffffff;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0xfffffff0;
				time23 = 0x00ffffff;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0xffffff00;
				time23 = 0x00ffffff;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0xfffff000;
				time23 = 0x00ffffff;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0xffff0000;
				time23 = 0x00ffffff;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0xfff00000;
				time23 = 0x00ffffff;
				break;
			case 14:
				time7 = 0x00000000;
				time15 = 0xff000000;
				time23 = 0x00ffffff;
				break;
			case 15:
				time7 = 0x00000000;
				time15 = 0xf0000000;
				time23 = 0x00ffffff;
				break;
			case 16:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x00ffffff;
				break;
			case 17:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x00fffff0;
				break;
			case 18:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x00ffff00;
				break;
			case 19:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x00fff000;
				break;
			case 20:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x00ff0000;
				break;
			case 21:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x00f00000;
				break;
			default:
				break;
			}
			break;
		case 23:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0xffffffff;
				time23 = 0x0fffffff;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0xffffffff;
				time23 = 0x0fffffff;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0xffffffff;
				time23 = 0x0fffffff;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0xffffffff;
				time23 = 0x0fffffff;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0xffffffff;
				time23 = 0x0fffffff;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0xffffffff;
				time23 = 0x0fffffff;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0xffffffff;
				time23 = 0x0fffffff;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0xffffffff;
				time23 = 0x0fffffff;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0xffffffff;
				time23 = 0x0fffffff;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0xfffffff0;
				time23 = 0x0fffffff;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0xffffff00;
				time23 = 0x0fffffff;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0xfffff000;
				time23 = 0x0fffffff;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0xffff0000;
				time23 = 0x0fffffff;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0xfff00000;
				time23 = 0x0fffffff;
				break;
			case 14:
				time7 = 0x00000000;
				time15 = 0xff000000;
				time23 = 0x0fffffff;
				break;
			case 15:
				time7 = 0x00000000;
				time15 = 0xf0000000;
				time23 = 0x0fffffff;
				break;
			case 16:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0fffffff;
				break;
			case 17:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0ffffff0;
				break;
			case 18:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0fffff00;
				break;
			case 19:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0ffff000;
				break;
			case 20:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0fff0000;
				break;
			case 21:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0ff00000;
				break;
			case 22:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0x0f000000;
				break;
			default:
				break;
			}
			break;
		case 24:
			switch (m_start_time) {
			case 0:
				time7 = 0xffffffff;
				time15 = 0xffffffff;
				time23 = 0xffffffff;
				break;
			case 1:
				time7 = 0xfffffff0;
				time15 = 0xffffffff;
				time23 = 0xffffffff;
				break;
			case 2:
				time7 = 0xffffff00;
				time15 = 0xffffffff;
				time23 = 0xffffffff;
				break;
			case 3:
				time7 = 0xfffff000;
				time15 = 0xffffffff;
				time23 = 0xffffffff;
				break;
			case 4:
				time7 = 0xffff0000;
				time15 = 0xffffffff;
				time23 = 0xffffffff;
				break;
			case 5:
				time7 = 0xfff00000;
				time15 = 0xffffffff;
				time23 = 0xffffffff;
				break;
			case 6:
				time7 = 0xff000000;
				time15 = 0xffffffff;
				time23 = 0xffffffff;
				break;
			case 7:
				time7 = 0xf0000000;
				time15 = 0xffffffff;
				time23 = 0xffffffff;
				break;
			case 8:
				time7 = 0x00000000;
				time15 = 0xffffffff;
				time23 = 0xffffffff;
				break;
			case 9:
				time7 = 0x00000000;
				time15 = 0xfffffff0;
				time23 = 0xffffffff;
				break;
			case 10:
				time7 = 0x00000000;
				time15 = 0xffffff00;
				time23 = 0xffffffff;
				break;
			case 11:
				time7 = 0x00000000;
				time15 = 0xfffff000;
				time23 = 0xffffffff;
				break;
			case 12:
				time7 = 0x00000000;
				time15 = 0xffff0000;
				time23 = 0xffffffff;
				break;
			case 13:
				time7 = 0x00000000;
				time15 = 0xfff00000;
				time23 = 0xffffffff;
				break;
			case 14:
				time7 = 0x00000000;
				time15 = 0xff000000;
				time23 = 0xffffffff;
				break;
			case 15:
				time7 = 0x00000000;
				time15 = 0xf0000000;
				time23 = 0xffffffff;
				break;
			case 16:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0xffffffff;
				break;
			case 17:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0xfffffff0;
				break;
			case 18:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0xffffff00;
				break;
			case 19:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0xfffff000;
				break;
			case 20:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0xffff0000;
				break;
			case 21:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0xfff00000;
				break;
			case 22:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0xff000000;
				break;
			case 23:
				time7 = 0x00000000;
				time15 = 0x00000000;
				time23 = 0xf0000000;
			default:
				break;
			}
			break;

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
//		switch (v.getId()) {
//		case R.id.cbx_coverage:
//			if (isChecked) {
//				sdcardBean.setRecord_conver_enable(1);
//			} else {
//				sdcardBean.setRecord_conver_enable(0);
//			}
//			break;
//		case R.id.cbx_record_time:
//			if (isChecked) {
//				sdcardBean.setRecord_time_enable(1);
//				linearLayout_start.setVisibility(View.VISIBLE);
//				linearLayout_end.setVisibility(View.VISIBLE);
//			} else {
//				sdcardBean.setRecord_time_enable(0);
//				linearLayout_start.setVisibility(View.GONE);
//				linearLayout_end.setVisibility(View.GONE);
//			}
//			break;
//		default:
//			break;
//		}

	}

	private int ifShow = 0;

	@Override
	public void callBackRecordSchParams(String did, int record_cover_enable,
			int record_timer, int record_size, int record_time_enable,
			int record_schedule_sun_0, int record_schedule_sun_1,
			int record_schedule_sun_2, int record_schedule_mon_0,
			int record_schedule_mon_1, int record_schedule_mon_2,
			int record_schedule_tue_0, int record_schedule_tue_1,
			int record_schedule_tue_2, int record_schedule_wed_0,
			int record_schedule_wed_1, int record_schedule_wed_2,
			int record_schedule_thu_0, int record_schedule_thu_1,
			int record_schedule_thu_2, int record_schedule_fri_0,
			int record_schedule_fri_1, int record_schedule_fri_2,
			int record_schedule_sat_0, int record_schedule_sat_1,
			int record_schedule_sat_2, int record_sd_status, int sdtotal,
			int sdfree) {
		Log.d("tag", "sdtotal:" + sdtotal);
		ifShow = record_time_enable;
		sdcardBean.setDid(did);
		sdcardBean.setRecord_conver_enable(record_cover_enable);
		sdcardBean.setRecord_timer(record_timer);
		sdcardBean.setRecord_size(record_size);
		sdcardBean.setRecord_time_enable(record_time_enable);
		sdcardBean.setRecord_sd_status(record_sd_status);
		sdcardBean.setSdtotal(sdtotal);
		sdcardBean.setSdfree(sdfree);
		handler.sendEmptyMessage(PARAMS);
	}

	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
			int result) {
		Log.d("tag", "result:" + result + " paramType:" + paramType);
		if (strDID.equals(did)) {
			handler.sendEmptyMessage(result);
		}
	}
}
