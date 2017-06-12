package object.p2pipcam.adapter;

import java.util.ArrayList;

import object.dbnewgo.client.R;
import object.p2pipcam.bean.CameraParamsBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlarmActivityAdapter extends BaseAdapter {
	private ArrayList<CameraParamsBean> list = null;
	private LayoutInflater inflater;
	private ViewHolder holder;

	public AlarmActivityAdapter(Context context,
			ArrayList<CameraParamsBean> list) {
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.alarmactivity_listitem,
					null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.did = (TextView) convertView.findViewById(R.id.tv_did);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CameraParamsBean bean = list.get(position);
		holder.name.setText(bean.getName());
		holder.did.setText(bean.getDid());
		return convertView;
	}

	public CameraParamsBean getItemCam(int position) {
		return list.get(position);
	}

	private class ViewHolder {
		TextView name;
		TextView did;
	}
}
