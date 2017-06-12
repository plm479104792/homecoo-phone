package object.dbnewgo.client;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

/**
 * ¹ØÓÚÈí¼þ
 * **/
public class AboutActivity extends BaseActivity {
	private static final String LOG_TAG = "AboutActivity";
	private Button btnBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "AboutActivity onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		btnBack = (Button) findViewById(R.id.back);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);
			}
		});
	}
}