package object.p2pipcam.adapter;

import java.util.ArrayList;

import object.dbnewgo.client.R;
import object.p2pipcam.bean.PlayBackBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlayBackAdapter extends BaseAdapter {
	private ArrayList<PlayBackBean> arrayList;
	private LayoutInflater inflater;
	private ViewHolder holder;

	public PlayBackAdapter(Context context) {
		arrayList = new ArrayList<PlayBackBean>();
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return arrayList.size();
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
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.playbacktf_listitem, null);
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PlayBackBean bean = arrayList.get(position);
		holder.tvName.setText(bean.getPath());
		return convertView;
	}

	private class ViewHolder {
		TextView tvName;
	}

	public void addPlayBean(PlayBackBean bean) {
		arrayList.add(bean);
	}

	public PlayBackBean getPlayBean(int position) {
		return arrayList.get(position);
	}
}
