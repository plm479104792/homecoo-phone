package object.dbnewgo.client;

import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingApActivity extends BaseActivity {
	private EditText editTextSsid, editTextPwd;
	private Button button_back, button_done;
	private String strDID = null;// camera id
	private String cameraName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getDataFromOther();
		setContentView(R.layout.settingap);
		NativeCaller.PPPPGetSystemParams(strDID, ContentCommon.MSG_TYPE_GET_AP);
		Log.d("shix", "strDID=="+strDID);
		editTextPwd = (EditText) findViewById(R.id.ap_pwd);
		editTextSsid = (EditText) findViewById(R.id.ap_ssid);
		button_back = (Button) findViewById(R.id.user_cancel);
		button_done = (Button) findViewById(R.id.user_ok);
		button_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);

			}
		});
		button_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (editTextSsid.getText().toString().length() == 0
						|| editTextSsid.getText().toString() == null
						|| editTextPwd.getText().toString().length() == 0
						|| editTextPwd.getText().toString() == null) {
					showToastLong(R.string.setting_ap_show);
					return;
				}

			}
		});
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
		cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
	}
}
