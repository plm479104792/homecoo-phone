package object.p2pipcam.adapter;

import java.util.ArrayList;

import object.dbnewgo.client.R;
import object.p2pipcam.bean.CameraParamsBean;
import object.p2pipcam.system.SystemValue;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * ±à¼­µÄÉãÏñ»úµÄadapter
 * **/
public class CameraEditAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ViewHolder holder;
	private int selectCount = 0;
	public boolean hasSelect = false;

	public CameraEditAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return SystemValue.arrayList.size();
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
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.camera_edit_listitem, null);
			holder = new ViewHolder();
			holder.cbx = (CheckBox) convertView
					.findViewById(R.id.camera_edit_cbx);
			holder.tvID = (TextView) convertView.findViewById(R.id.camera_id);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.camera_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MyCheckListener cbxListener = new MyCheckListener(position);
		holder.cbx.setOnCheckedChangeListener(cbxListener);
		CameraParamsBean bean = SystemValue.arrayList.get(position);
		holder.tvID.setText(bean.getDid());
		holder.tvName.setText(bean.getName());
		boolean selected = bean.isSelected();
		if (selected) {
			holder.cbx.setChecked(true);
		} else {
			holder.cbx.setChecked(false);
		}
		return convertView;
	}

	private class MyCheckListener implements OnCheckedChangeListener {
		private int position;

		public MyCheckListener(int position) {
			this.position = position;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			CameraParamsBean bean = SystemValue.arrayList.get(position);
			if (isChecked) {
				hasSelect = true;
				selectCount++;
				bean.setSelected(true);
			} else {
				if (selectCount > 0) {
					selectCount--;
				}
				if (selectCount == 0) {
					hasSelect = false;
				}
				bean.setSelected(false);
			}
		}

	}

	private class ViewHolder {
		TextView tvName;
		TextView tvID;
		CheckBox cbx;
	}

	public boolean modifyCamera(String old_did, String did, String name,
			String user, String pwd) {
		for (CameraParamsBean bean : SystemValue.arrayList) {
			String id = bean.getDid();
			if (id.equals(old_did)) {
				bean.setName(name);
				bean.setUser(user);
				bean.setDid(did);
				bean.setPwd(pwd);
				bean.setAuthority(false);
				return true;
			}
		}
		return false;
	}

	public void selectAll(boolean flag) {
		for (CameraParamsBean bean : SystemValue.arrayList) {
			bean.setSelected(true);
		}
	}

	public void reverseSelect(boolean flag) {
		int size = SystemValue.arrayList.size();
		for (int i = 0; i < size; i++) {
			CameraParamsBean bean = SystemValue.arrayList.get(i);
			boolean selected = bean.isSelected();
			if (selected) {
				bean.setSelected(false);
			} else {
				bean.setSelected(true);
			}
		}
	}

	public void addCamera(String name, String did, String user, String pwd) {
		if (checkRepeatID(did)) {
			CameraParamsBean bean = new CameraParamsBean();
			bean.setDid(did);
			bean.setName(name);
			bean.setUser(user);
			bean.setPwd(pwd);
			bean.setSelected(false);
			bean.setAuthority(false);
			SystemValue.arrayList.add(bean);
		}
	}

	public ArrayList<String> delCamera() {
		ArrayList<String> temp = new ArrayList<String>();
		for (CameraParamsBean bean : SystemValue.arrayList) {
			boolean selected = bean.isSelected();
			String did = bean.getDid();
			if (selected) {
				temp.add(did);
			}
		}
		for (int i = 0; i < temp.size(); i++) {
			String did = temp.get(i);
			boolean flag = true;
			for (int j = 0; i < SystemValue.arrayList.size() && flag; j++) {
				CameraParamsBean bean = SystemValue.arrayList.get(j);
				String strDid = bean.getDid();
				if (did.equals(strDid)) {
					SystemValue.arrayList.remove(j);
					flag = false;
				}
			}
		}

		return temp;
	}

	public CameraParamsBean getItemCamera(int pos) {
		return SystemValue.arrayList.get(pos);
	}

	private boolean checkRepeatID(String did) {

		for (CameraParamsBean bean : SystemValue.arrayList) {
			String strDid = bean.getDid();
			if (strDid.equals(did)) {
				return false;
			}
		}
		return true;
	}
}
