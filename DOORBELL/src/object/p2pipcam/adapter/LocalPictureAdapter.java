package object.p2pipcam.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import object.dbnewgo.client.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LocalPictureAdapter extends BaseAdapter {
	private LayoutInflater inflator;
	private List<String> groupList;
	private Map<String, ArrayList<String>> childMap;
	private int mode = 0;// 是图片还是录像1：图片，2：录像
	private int width;
	private ViewHolder holder = null;
	private HashMap<String, Bitmap> mapBmp = null;
	private boolean isOver = false;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				isOver = true;
				break;

			default:
				break;
			}
			notifyDataSetChanged();
		}
	};

	public LocalPictureAdapter(Context context, List<String> groupList,
			Map<String, ArrayList<String>> childMap, int wh) {
		inflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groupList = groupList;
		this.childMap = childMap;
		this.width = wh;
		mapBmp = new HashMap<String, Bitmap>();
		initBmp();

	}

	public void initBmp() {
		new Thread() {
			public void run() {
				int size = groupList.size();
				for (int i = 0; i < size; i++) {
					String time = groupList.get(i);
					ArrayList<String> list = childMap.get(time);
					Bitmap btp = BitmapFactory.decodeFile(list.get(0));
					if (btp != null) {
						Matrix matrix = new Matrix();
						float scaleX = ((float) width) / btp.getWidth();
						float scaleY = ((float) width) / btp.getHeight();
						matrix.postScale(scaleX, scaleY);
						Bitmap bmp = Bitmap.createBitmap(btp, 0, 0,
								btp.getWidth(), btp.getHeight(), matrix, true);
						mapBmp.put(time, bmp);
						handler.sendEmptyMessage(1);
					}
				}
				handler.sendEmptyMessage(2);
			}
		}.start();
	}

	@Override
	public int getCount() {
		return groupList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflator
					.inflate(R.layout.localpicture_listitem, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.sum = (TextView) convertView.findViewById(R.id.sum);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.pBar = (ProgressBar) convertView
					.findViewById(R.id.progressBar1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, width);
		holder.img.setLayoutParams(lp);
		String time = groupList.get(position);
		List<String> list = childMap.get(time);
		holder.time.setText(time);
		holder.sum.setText(list.size() + "");
		Bitmap bmp = mapBmp.get(time);
		if (bmp != null) {
			holder.pBar.setVisibility(View.GONE);
			holder.img.setImageBitmap(bmp);
		}
		if (isOver) {
			holder.pBar.setVisibility(View.GONE);
		}

		return convertView;
	}

	public void setMode(int mode) {
		this.mode = mode;

	}

	public void setOver(boolean flag) {
		isOver = flag;
	}

	public void addBmp(String key, Bitmap bmp) {
		mapBmp.put(key, bmp);
	}

	private class ViewHolder {
		ImageView img;
		TextView time;
		TextView sum;
		ProgressBar pBar;
	}

	public void updateGroup(List<String> groupList) {
		this.groupList = groupList;
	}

	public void updateChild(Map<String, ArrayList<String>> childMap) {
		this.childMap = childMap;
	}

	public static byte[] intToByte(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8;// 向右移8位
		}
		return b;
	}

	public static int byteToInt(byte[] b) {

		int s = 0;
		int s0 = b[0] & 0xff;// 最低位
		int s1 = b[1] & 0xff;
		int s2 = b[2] & 0xff;
		int s3 = b[3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return s;
	}
}
