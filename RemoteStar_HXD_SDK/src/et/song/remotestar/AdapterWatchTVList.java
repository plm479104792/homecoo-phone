package et.song.remotestar;

import java.util.List;

import et.song.global.ETGlobal;
import et.song.remotestar.hxd.sdk.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterWatchTVList extends BaseAdapter {
	private LayoutInflater mInflater;

	private List<AdapterWatchTVItem> mItems;

	public AdapterWatchTVList(Context context, List<AdapterWatchTVItem> list) {
		mInflater = LayoutInflater.from(context);
		mItems = list;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup par) {
		// Bitmap bitmap = null;
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sliding_menu_item, null);
			holder = new ViewHolder();
			holder.file_res = (ImageView) convertView
					.findViewById(R.id.image_res);
			holder.file_name = ((TextView) convertView
					.findViewById(R.id.text_name));
			holder.file_context = ((TextView) convertView
					.findViewById(R.id.text_context));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.file_res.setImageResource(ETGlobal.mWatchTVImages[mItems.get(
				position).getRes()]);

		holder.file_context.setText(mItems.get(position).getContext());
		holder.file_name.setText(mItems.get(position).getName());
		return convertView;
	}

	private class ViewHolder {
		ImageView file_res;
		TextView file_name;
		TextView file_context;
	}

}