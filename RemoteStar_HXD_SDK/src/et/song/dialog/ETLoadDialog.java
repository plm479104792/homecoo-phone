package et.song.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import et.song.remotestar.hxd.sdk.R;

public class ETLoadDialog extends Dialog {
	Context mContext;

	public ETLoadDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		init();
	}

	public ETLoadDialog(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		LinearLayout contentView = new LinearLayout(mContext);
		contentView.setMinimumHeight(48);
		contentView.setGravity(Gravity.CENTER);
		contentView.setOrientation(LinearLayout.HORIZONTAL);

		ImageView image = new ImageView(mContext);
		image.setImageResource(android.R.drawable.stat_notify_sync_noanim);
		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.rotate_repeat);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		image.setAnimation(anim);

		contentView.addView(image);
		setContentView(contentView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return true;
	}
}
