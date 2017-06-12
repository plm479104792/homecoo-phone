package object.p2pipcam.adapter;

import java.util.ArrayList;

import object.dbnewgo.client.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SettingDialogListViewAdapter extends BaseAdapter {
	private ArrayList<String> arrayList;
	private LayoutInflater inflater;

	public SettingDialogListViewAdapter(Context context,
			ArrayList<String> arraylist) {
		this.arrayList = arraylist;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return arrayList.size();
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.settingdialog_listitem,
					null);
			holder = new ViewHolder();
			holder.tv = (TextView) convertView.findViewById(R.id.content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String content = arrayList.get(position);
		holder.tv.setText(content);
		return convertView;
	}

	private class ViewHolder {
		TextView tv;
	}
}
