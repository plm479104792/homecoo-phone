package com.homecoolink.adapter;

import com.homecoolink.R;
import com.homecoolink.data.Contact;
import com.homecoolink.fragment.ImageFrag;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VideoContactAdapter extends BaseAdapter {

	private Context mcontext;
	private LayoutInflater mlif;

	ImageFrag ifFrag = null;

	public VideoContactAdapter(Context context, ImageFrag ifrag) {
		this.mcontext = context;
		this.mlif = LayoutInflater.from(mcontext);
		ifFrag = ifrag;

	}

	public void ChangeData() {
		FList.getInstance().searchLocalDevice();
		FList.getInstance().updateOnlineState();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// int size = FList.getInstance().getUnsetPasswordLocalDevices().size();
		return FList.getInstance().size();
	}

	@Override
	public Contact getItem(int position) {
		return FList.getInstance().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (position >= FList.getInstance().size()) {
			return 0;
		} else {
			return 1;
		}

	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e("343", "====");
		final Contact contact = FList.getInstance().get(position);
		View view = convertView;
		ViewHolder holder = null;
		if (null == view) {
			holder = new ViewHolder();
			view = mlif.inflate(R.layout.video_spinner_deviceitem, null);
			holder.setTName((TextView) view.findViewById(R.id.time_group_title));
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();

		}
		TextView tView = holder.getTName();
		tView.setText(contact.contactName);
		Drawable leftDrawable;
		Resources res = mcontext.getResources();

		if (contact.onLineState == Constants.DeviceState.ONLINE) {
			leftDrawable = res.getDrawable(R.drawable.video_contact_online);
			leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
					leftDrawable.getMinimumHeight());
			tView.setCompoundDrawables(leftDrawable, null, null, null);
			ColorStateList csl = res
					.getColorStateList(R.color.dialog_title);
			tView.setTextColor(csl);
		} else {
			leftDrawable = res.getDrawable(R.drawable.video_contact_offline);
			leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
					leftDrawable.getMinimumHeight());
			tView.setCompoundDrawables(leftDrawable, null, null, null);
			ColorStateList csl = res
					.getColorStateList(R.color.gray);
			tView.setTextColor(csl);
		}
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ifFrag.SetSelectedDev(contact);
				ifFrag.vfiles = null;
			}
		});
		return view;
	}

	class ViewHolder {

		private TextView tName;

		public TextView getTName() {
			return tName;
		}

		public void setTName(TextView tName) {
			this.tName = tName;
		}
	}

}
