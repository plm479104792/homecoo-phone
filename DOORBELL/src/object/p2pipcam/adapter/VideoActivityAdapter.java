package object.p2pipcam.adapter;

import java.util.ArrayList;

import object.dbnewgo.client.R;
import object.p2pipcam.bean.CameraParamsBean;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.system.SystemValue;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VideoActivityAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ViewHolder holder;
	public int mode = 1;
	public final int PHONE = 1;
	public final int REMOTE = 2;
	private boolean isOver = false;

	public VideoActivityAdapter(Context context,
			ArrayList<CameraParamsBean> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return SystemValue.arrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.video_only_listitem, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.id = (TextView) convertView.findViewById(R.id.tv_did);
			holder.status = (TextView) convertView.findViewById(R.id.tv_status);
			holder.number = (TextView) convertView
					.findViewById(R.id.tv_pic_num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CameraParamsBean bean = SystemValue.arrayList.get(arg0);
		int sum = bean.getSum();

		holder.number.setText("(" + sum + ")");
		String did = bean.getDid();
		String name = bean.getName();
		int status = bean.getStatus();
		int resid;
		switch (status) {// ÉãÏñ»úµÄ×´Ì¬
		case ContentCommon.PPPP_STATUS_CONNECTING:
			resid = R.string.pppp_status_connecting;
			break;
		case ContentCommon.PPPP_STATUS_CONNECT_FAILED:
			resid = R.string.pppp_status_connect_failed;
			break;
		case ContentCommon.PPPP_STATUS_DISCONNECT:
			resid = R.string.pppp_status_disconnect;
			break;
		case ContentCommon.PPPP_STATUS_INITIALING:
			resid = R.string.pppp_status_initialing;
			break;
		case ContentCommon.PPPP_STATUS_INVALID_ID:
			resid = R.string.pppp_status_invalid_id;
			break;
		case ContentCommon.PPPP_STATUS_ON_LINE:
			resid = R.string.pppp_status_online;
			break;
		case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE:
			resid = R.string.device_not_on_line;
			break;
		case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT:
			resid = R.string.pppp_status_connect_timeout;
			break;
		case ContentCommon.PPPP_STATUS_CONNECT_ERRER:
			resid = R.string.pppp_status_connect_log_errer;
			break;
		case ContentCommon.PPPP_STATUS_USER_LOGIN:
			resid = R.string.pppp_status_connect_user_login;
			break;
		case ContentCommon.PPPP_STATUS_PWD_CUO:
			resid = R.string.pppp_status_connect_pwd_cuo;
			break;
		default:
			resid = R.string.pppp_status_unknown;
		}
		holder.status.setText(context.getResources().getString(resid));
		holder.id.setText(did);
		holder.name.setText(name);
		switch (mode) {
		case PHONE:
			holder.number.setVisibility(View.VISIBLE);
			holder.status.setVisibility(View.GONE);
			break;
		case REMOTE:
			holder.number.setVisibility(View.GONE);
			holder.status.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
		return convertView;
	}

	public void setOver(boolean over) {
		isOver = over;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	private class ViewHolder {
		TextView name;
		TextView id;
		TextView status;
		TextView number;
	}
}
