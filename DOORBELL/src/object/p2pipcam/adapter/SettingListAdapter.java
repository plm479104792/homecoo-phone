package object.p2pipcam.adapter;

import java.util.ArrayList;
import java.util.List;

import object.dbnewgo.client.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingListAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private static final String LOG_TAG = "SettingListAdapter";

	private LayoutInflater listContainer = null;
	@SuppressWarnings("unused")
	private Context context = null;
	private List<String> listItems = new ArrayList<String>();

	public final class SettingListItem {
		public TextView SettingName;
		public ImageView SettingImg;
	}

	public SettingListAdapter(Context ct, List<String> listItems) {
		context = ct;
		this.listItems = listItems;
		listContainer = LayoutInflater.from(ct);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		SettingListItem settingListItem = null;
		if (convertView == null) {
			settingListItem = new SettingListItem();
			convertView = listContainer.inflate(R.layout.setting_list_item,
					null);
			settingListItem.SettingName = (TextView) convertView
					.findViewById(R.id.settingName);
			settingListItem.SettingImg = (ImageView) convertView
					.findViewById(R.id.settingImg);
			convertView.setTag(settingListItem);
		} else {
			settingListItem = (SettingListItem) convertView.getTag();
		}
		if (position == 0) {
			convertView
					.setBackgroundResource(R.drawable.listitem_pressed_top_corner_selector);
		} else if (position == listItems.size() - 1) {
			convertView
					.setBackgroundResource(R.drawable.listitem_pressed_bottom_corner_selector);
		} else {
			convertView
					.setBackgroundResource(R.drawable.listitem_pressed_selector);
		}
		settingListItem.SettingName.setText((String) listItems.get(position));
		return convertView;
	}

	public void ClearAll() {
		listItems.clear();
	}

}