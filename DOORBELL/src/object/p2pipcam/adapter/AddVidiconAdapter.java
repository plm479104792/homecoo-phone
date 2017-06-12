package object.p2pipcam.adapter;

import object.dbnewgo.client.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AddVidiconAdapter extends BaseAdapter {
	private LayoutInflater inflater;

	public AddVidiconAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return 1;
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
		convertView = inflater.inflate(R.layout.addvidicon_listitem, null);
		return convertView;
	}

}
