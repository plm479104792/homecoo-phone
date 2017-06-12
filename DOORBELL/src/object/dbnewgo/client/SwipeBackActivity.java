package object.dbnewgo.client;

import object.dbnewgo.client.R;
import object.p2pipcam.system.SwipeBackLayout;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SwipeBackActivity extends FragmentActivity {

	private SwipeBackLayout mSwipeBackLayout;

	private boolean mOverrideExitAniamtion = true;

	private boolean mIsFinishing;
	private SharedPreferences pre_all_one;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		getWindow().getDecorView().setBackgroundDrawable(null);
		mSwipeBackLayout = new SwipeBackLayout(this);
		initExitPopupWindow_all_show();
		pre_all_one = getSharedPreferences("shix_zhao_pre_all_one",
				Context.MODE_PRIVATE);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mSwipeBackLayout.attachToActivity(this);
	}

	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mSwipeBackLayout.findViewById(id);
	}

	public SwipeBackLayout getSwipeBackLayout() {
		return mSwipeBackLayout;
	}

	public void setSwipeBackEnable(boolean enable) {
		mSwipeBackLayout.setEnableGesture(enable);
	}

	/**
	 * slide from left
	 */
	public void setEdgeFromLeft() {
		final int edgeFlag = SwipeBackLayout.EDGE_LEFT;
		mSwipeBackLayout.setEdgeTrackingEnabled(edgeFlag);
		mSwipeBackLayout.setEdgeSize(450);
	}

	/**
	 * Override Exit Animation
	 * 
	 * @param override
	 */
	public void setOverrideExitAniamtion(boolean override) {
		mOverrideExitAniamtion = override;
	}

	/**
	 * Scroll out contentView and finish the activity
	 */
	public void scrollToFinishActivity() {
		mSwipeBackLayout.scrollToFinishActivity();
	}

	@Override
	public void finish() {
		if (mOverrideExitAniamtion && !mIsFinishing) {
			scrollToFinishActivity();
			mIsFinishing = true;
			return;
		}
		mIsFinishing = false;
		super.finish();
	}

	public void showToast(String content) {
		Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
	}

	public void showToast(int rid) {
		Toast.makeText(this, getResources().getString(rid), Toast.LENGTH_SHORT)
				.show();
	}

	public void showToastLong(int rid) {
		Toast.makeText(this, getResources().getString(rid), Toast.LENGTH_LONG)
				.show();
	}

	public String returnString(int rid) {
		return getResources().getString(rid);
	}

	public static boolean hasSdcard() {

		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/****
	 * ÍË³öÈ·¶¨dialog
	 * */
	public void showSureDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.app);
		builder.setTitle(getResources().getString(R.string.exit)
				+ getResources().getString(R.string.app_name));
		builder.setMessage(R.string.exit_chenxu_show);
		builder.setPositiveButton(R.string.str_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Process.killProcess(Process.myPid());
						finish();
					}
				});
		builder.setNegativeButton(R.string.str_cancel, null);
		builder.show();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1111) {
				popupWindow_re.showAtLocation(buttonBack, Gravity.CENTER, 0, 0);
			}

		};
	};
	private View popv_re;
	private PopupWindow popupWindow_re;
	private ImageView imageView;
	private FrameLayout framelayout;
	private Button buttonBack;

	public void showAll(Button btnBack,String preKey) {
		buttonBack = btnBack;
		Log.d("test", "shix-(pre_all_one.getInt(preKey, 0):"+pre_all_one.getInt(preKey, 0));
		if (pre_all_one.getInt(preKey, 0)==0) {
			SharedPreferences.Editor editor = pre_all_one.edit();
			editor.putInt(preKey, 1);
			editor.commit();
			handler.postDelayed(new MyShowRunal(), 200);
		}
		
	}

	class MyShowRunal implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(1111);
		}

	}

	private void initExitPopupWindow_all_show() {
		LayoutInflater li = LayoutInflater.from(this);
		popv_re = li.inflate(R.layout.popup_all_show, null);
		imageView = (ImageView) popv_re.findViewById(R.id.imageView);
		framelayout = (FrameLayout) popv_re.findViewById(R.id.framelayout);
		popupWindow_re = new PopupWindow(popv_re,
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		popupWindow_re.setAnimationStyle(R.style.AnimationPreview);
		popupWindow_re.setFocusable(true);
		popupWindow_re.setOutsideTouchable(true);
		popupWindow_re.setBackgroundDrawable(new ColorDrawable(0));
		// popupWindow.update();
		imageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Log.d("test", "shix_image_ontou");
				popupWindow_re.dismiss();
				return false;
			}
		});
		popupWindow_re
				.setOnDismissListener(new PopupWindow.OnDismissListener() {

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						popupWindow_re.dismiss();
					}
				});
		popupWindow_re.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow_re.dismiss();
				} else {
					// popupWindow_re.dismiss();
				}
				return false;
			}
		});
	}
}
