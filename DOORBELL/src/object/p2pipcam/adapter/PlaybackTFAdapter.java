package object.p2pipcam.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import object.dbnewgo.client.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaybackTFAdapter extends BaseExpandableListAdapter {
	private LayoutInflater inflater;
	private List<String> groupList;
	private Map<String, List<String>> childMap;

	public PlaybackTFAdapter(Context context) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groupList = new ArrayList<String>();
		this.childMap = new HashMap<String, List<String>>();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {

		return childMap.get(groupList.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		CViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.playbacktf_childlistitem,
					null);
			holder = new CViewHolder();
			holder.content = (TextView) convertView
					.findViewById(R.id.tv_datetime);
			convertView.setTag(holder);
		} else {
			holder = (CViewHolder) convertView.getTag();
		}

		String path = childMap.get(groupList.get(groupPosition)).get(
				childPosition);

		holder.content.setText(path);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childMap.get(groupList.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GViewHolder holder = null;
		if (convertView == null) {
			holder = new GViewHolder();
			convertView = inflater.inflate(R.layout.playbacktf_grouplistitem,
					null);
			holder.tvdate = (TextView) convertView.findViewById(R.id.tv_date);
			holder.tvsum = (TextView) convertView.findViewById(R.id.tv_sum);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (GViewHolder) convertView.getTag();
		}
		if (isExpanded) {
			holder.img.setImageResource(R.drawable.arrowdown);
		} else {
			holder.img.setImageResource(R.drawable.arrow);
		}
		String key = groupList.get(groupPosition);
		List<String> childList = childMap.get(key);
		holder.tvdate.setText(key);
		if (childList != null) {
			holder.tvsum.setText(String.valueOf(childList.size()));
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void addGroupAndChild(String group, String path) {
		if (!groupList.contains(group)) {
			groupList.add(group);
			ArrayList<String> list = new ArrayList<String>();
			list.add(path);
			childMap.put(group, list);
		} else {
			childMap.get(group).add(path);
		}
	}

	public String getChildFilePath(int gposition, int cposition) {
		String key = groupList.get(gposition);
		List<String> list = childMap.get(key);
		return list.get(cposition);
	}

	public String[] getGroupTitleAndSum(int position) {
		String[] s = new String[2];
		String key = groupList.get(position);
		s[0] = key;
		List<String> list = childMap.get(key);
		s[1] = String.valueOf(list.size());
		return s;
	}

	private class GViewHolder {
		TextView tvdate;
		TextView tvsum;
		ImageView img;
	}

	private class CViewHolder {

		// ImageView img;
		TextView content;
	}
}
