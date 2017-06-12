package com.homecoolink.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.data.Contact;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.ImageUtils;


public class RecordAdapter extends BaseAdapter {
	Context context;
	public List<String> list = new ArrayList<String>();
	public Contact contact = null;
	public static Date startTime;

	public RecordAdapter() {

	}


	public RecordAdapter(Context context, List<String> list) {
		this.context = context;
		this.list = list;
	}

	class ViewHolder {
		public TextView record_name;

		public TextView getRecord_name() {
			return record_name;
		}

		public void setRecord_name(TextView record_name) {
			this.record_name = record_name;
		}

		public ImageView img;

		public ImageView getImg() {
			return img;
		}

		public void setImg(ImageView img) {
			this.img = img;
		}

		public ImageView playbtn;

		public ImageView getPlayBtn() {
			return playbtn;
		}

		public void setPlayBtn(ImageView playbtn) {
			this.playbtn = playbtn;
		}

	}

	@Override
	public int getCount() {
		try {
			// TODO Auto-generated method stub
			Log.e("getCount", "length=" + list.size());
			return list.size();
		} catch (Exception e) {
			return 0;
		}
		
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updateImage(String threeNum, boolean isGray, ImageView imgv) {
		Bitmap tempBitmap;
		try {

			tempBitmap = ImageUtils.getBitmap(new File(
					"/sdcard/screenshot/tempHead/" + NpcCommon.mThreeNum + "/"
							+ threeNum + ".jpg"), 200, 200);

			// tempBitmap = ImageUtils.roundCorners(tempBitmap,
			// ImageUtils.getScaleRounded(tempBitmap.getWidth()));
			imgv.setImageBitmap(tempBitmap);
		} catch (Exception e) {

			tempBitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.video_list_defaultpic);
			// tempBitmap = ImageUtils.roundCorners(tempBitmap,
			// ImageUtils.getScaleRounded(tempBitmap.getWidth()));
			imgv.setImageBitmap(tempBitmap);

		}
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = arg1;
		final ViewHolder holder;
		if (null == view) {
			view = LayoutInflater.from(context).inflate(
					R.layout.list_videobrowser_item, null);
			holder = new ViewHolder();
			holder.setRecord_name((TextView) view.findViewById(R.id.tv_time));
			holder.setImg((ImageView) view
					.findViewById(R.id.video_list_defaultpic));
			holder.setPlayBtn((ImageView) view
					.findViewById(R.id.layout_play_btn));
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.getRecord_name().setText(
				DoName(list.get(arg0).substring(6, list.get(arg0).length())));
		updateImage(contact.contactId, false, holder.getImg());
		holder.getPlayBtn().setImageDrawable(
				context.getResources().getDrawable(R.drawable.video_list_play));
		return view;
	}

	private String DoName(String str) {
		String resString = "";
		String[] timeStrings = str.split("_");
		String dateString = timeStrings[0];
		String timeString = timeStrings[1];
		String lenString = timeStrings[2];
		String[] tstrs = timeString.split(":");
		int hours = Integer.parseInt(tstrs[0]);
		if (hours > 0 && hours < 12) {
			resString += context.getResources()
					.getString(R.string.time_static1);
		} else {
			resString += resString += context.getResources().getString(
					R.string.time_static2);

		}
		resString += tstrs[0] + ":" + tstrs[1];
		resString += " ";
		String[] lstrs = lenString.split("\\(");
		int length = Integer.parseInt(lstrs[1].replace("S)", ""));
		int se = length % 60;
		int min = (length / 60) % 60;
		int hour = (length / 60) / 60;
		resString += hour + ":" + min + ":" + se;
		return resString;
	}

	public String getLastItem() {
		if (list.size() > 0) {
			String lastTime = list.get(list.size() - 1).substring(6, 22);
			lastTime = lastTime.replace("_", " ");
			Log.e("lastTime", lastTime);
			return lastTime;
		} else {
			return "";
		}
	}

	public static void setStartTime(Date startTime) {
		RecordAdapter.startTime = startTime;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public void upLoadData(List<String> loadData) {
		Log.e("listsize", "old_list_size" + list.size());
		Log.e("loaddate", "loaddata_size" + loadData.size());
		if (loadData.size() <= 0) {
			return;
		}
		List<String> removeList = new ArrayList<String>();
		List<String> addList = new ArrayList<String>();
		for (String str : loadData) {
			for (String s : list) {
				if (str.equals(s)) {
					removeList.add(str);
				}
				Log.e("adddate", s + "--");
			}
			addList.add(str);
			Log.e("adddate", str);
		}
		Log.e("removelist", "removelist" + removeList.size());
		addList.removeAll(removeList);
		Log.e("removelist", "removelist" + addList.size());
		list.addAll(addList);
		Log.e("listsize", "list_size--" + list.size());
		for (String st : list) {
			Log.e("datas", "data" + st);
		}
		this.notifyDataSetChanged();
	}

	public List<String> getList() {
		return this.list;
	}

	public void loadData() {
		this.notifyDataSetChanged();
	}

}
