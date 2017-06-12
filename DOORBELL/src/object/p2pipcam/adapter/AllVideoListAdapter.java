package object.p2pipcam.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import object.dbnewgo.client.R;
import object.p2pipcam.bean.MovieInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AllVideoListAdapter extends BaseAdapter {

	private List<MovieInfo> plays = null;
	private TextView textView_name = null;
	private TextView textView_camer = null;
	private TextView textView_time = null;
	private TextView textView_size = null;
	private LayoutInflater inflater = null;
	private LinearLayout layout = null;
	private int posi = 0;

	public AllVideoListAdapter(Context context, List<MovieInfo> list,
			int position) {
		this.plays = list;
		this.inflater = LayoutInflater.from(context);
		this.posi = position;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return plays.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		arg1 = inflater.inflate(R.layout.all_load_list_item, null);
		textView_name = (TextView) arg1.findViewById(R.id.file_name);
		layout = (LinearLayout) arg1.findViewById(R.id.loaded_linear_camer);
		textView_time = (TextView) arg1.findViewById(R.id.file_time);
		textView_size = (TextView) arg1.findViewById(R.id.file_size);
		textView_camer = (TextView) arg1.findViewById(R.id.file_camer);
		if (posi == 0) {
			layout.setVisibility(View.VISIBLE);
			textView_camer.setText(plays.get(arg0).getCamerName());
		} else {
			layout.setVisibility(View.GONE);
			textView_camer.setVisibility(View.GONE);
		}

		textView_name.setText(plays.get(arg0).getVideoName());

		textView_size.setText(plays.get(arg0).getSize());
		textView_time.setText(getDateTime(plays.get(arg0).getDate()));
		return arg1;
	}

	private String getDateTime(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd  HH:mm:ss");
		return dateFormat.format(date);

	}
}
