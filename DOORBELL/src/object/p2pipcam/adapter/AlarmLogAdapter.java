package object.p2pipcam.adapter;

import java.util.ArrayList;

import object.dbnewgo.client.AlarmLogActivity;
import object.dbnewgo.client.R;
import object.p2pipcam.bean.AlarmLogBean;
import object.p2pipcam.content.ContentCommon;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class AlarmLogAdapter extends BaseAdapter {
	private ArrayList<AlarmLogBean> arrayList;
	private Context context;
	private LayoutInflater inflater;
	private AlarmLogActivity alarmLogActivity;

	public AlarmLogAdapter(Context context, AlarmLogActivity alarmLogActivity) {
		arrayList = new ArrayList<AlarmLogBean>();
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.alarmLogActivity = alarmLogActivity;
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
			convertView = inflater.inflate(R.layout.alarmlog_listitem, null);
			holder = new ViewHolder();
			holder.content = (TextView) convertView
					.findViewById(R.id.alarm_log_content);
			holder.createTime = (TextView) convertView
					.findViewById(R.id.alarm_log_time);
			holder.button_check_pic = (ImageButton) convertView
					.findViewById(R.id.button_check_pic);
			holder.tv_type = (TextView) convertView
					.findViewById(R.id.alarm_log_left);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AlarmLogBean alarmLogBean = arrayList.get(position);

		String name1 = alarmLogBean.getCreatetime();
		name1 = name1.replace(" ", "_").replace("-", "_").replace(":", "_");
		String pathTest = Environment.getExternalStorageDirectory() + "/"
				+ ContentCommon.SDCARD_PATH + "/picVisitor/" + name1 + ".jpg";
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			Bitmap bitmap = BitmapFactory.decodeFile(pathTest, options);
			if (bitmap != null) {
				holder.button_check_pic.setImageBitmap(bitmap);
			} else {
				holder.button_check_pic.setImageResource(R.drawable.vidicon);
			}

		} catch (Exception e) {
			// TODO: handle exception
			holder.button_check_pic.setImageResource(R.drawable.vidicon);
		}

		MyOnClickListener myOnTouchListener = new MyOnClickListener(position);
		holder.button_check_pic.setOnClickListener(myOnTouchListener);

		String content = alarmLogBean.getContent();
		if (content.length() != 2) {
			content = "13";
		}
		int type = Integer.parseInt(content.substring(0, 1));
		int calltype = Integer.parseInt(content.substring(1));
		if (type == 1) {
			holder.tv_type.setText(R.string.doorbell_fangke);
		} else {
			holder.tv_type.setText(R.string.doorbell_alerm);
		}
		if (calltype == 2) {
			holder.content.setTextColor(0xff000000);
			holder.content.setText(R.string.doorbell_alerm_ok);
		} else if (calltype == 3) {
			holder.content.setTextColor(0xffEA5145);
			holder.content.setText(R.string.doorbell_alerm_no);
		} else if (calltype == 4) {
			holder.content.setTextColor(0xff6badf6);
			holder.content.setText(R.string.doorbell_listing_other);
		}
		holder.createTime.setText(alarmLogBean.getCreatetime());
		MyOnLongListener onLongListener = new MyOnLongListener(position);
		holder.content.setOnLongClickListener(onLongListener);
		return convertView;
	}

	private class MyOnClickListener implements OnClickListener {
		private int position;
		AlarmLogBean alarmLogBean1;

		// private Map<String, Object> mapItem;
		// public ImageButton imgBtn;

		public MyOnClickListener(int position) {
			this.position = position;
			alarmLogBean1 = arrayList.get(position);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			alarmLogActivity.showPic(position, alarmLogBean1.getCamName(),
					alarmLogBean1.getContent(), alarmLogBean1.getCreatetime(),
					alarmLogBean1.getCreatetime());
		}
	}

	public void addAlarmLog(AlarmLogBean alarmLogBean) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).getCreatetime()
					.equals(alarmLogBean.getCreatetime())) {
				return;
			}
		}
		arrayList.add(alarmLogBean);
	}

	public void clearAllAlarmLog() {
		arrayList.clear();
	}

	private class MyOnLongListener implements OnLongClickListener {
		private int position;

		public MyOnLongListener(int position) {
			this.position = position;
		}

		@Override
		public boolean onLongClick(View v) {
			// Toast.makeText(context, "position:" + position, 0).show();
			return false;
		}

	}

	// private View.OnCreateContextMenuListener contextMenuListener = new
	// OnCreateContextMenuListener() {
	//
	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// AdapterView.AdapterContextMenuInfo info;
	// try {
	// info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	// if (info == null) {
	// return;
	// }
	// } catch (Exception e) {
	// }
	// menu.add(0, 1, 0, "É¾³ý");
	// }
	// };

	private class ViewHolder {
		TextView content;
		TextView createTime;
		TextView tv_type;
		ImageButton button_check_pic;
	}
}
