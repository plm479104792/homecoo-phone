package object.p2pipcam.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import object.dbnewgo.client.R;
import object.p2pipcam.nativecaller.NativeCaller;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LocalVideoListActivityAdapter extends BaseAdapter {

	private LayoutInflater inflator;
	private List<String> groupList;
	private Map<String, ArrayList<String>> childMap;
	private int mode = 0;// 是图片还是录像1：图片，2：录像
	private int width;
	private ViewHolder holder = null;
	private boolean isOver = false;
	private HashMap<String, Bitmap> mapBmp = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
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

	public LocalVideoListActivityAdapter(Context context,
			List<String> groupList, Map<String, ArrayList<String>> childMap,
			int wh) {
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
					List<String> list = childMap.get(time);
					Bitmap bitmap = getBitmap(list.get(0));
					mapBmp.put(time, bitmap);
					handler.sendEmptyMessage(1);
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
			holder.playvideo = (ImageView) convertView
					.findViewById(R.id.playvideo);
			holder.pBar = (ProgressBar) convertView
					.findViewById(R.id.progressBar1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.playvideo.setVisibility(View.VISIBLE);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, width);
		holder.img.setLayoutParams(lp);
		holder.pBar.setVisibility(View.GONE);
		String time = groupList.get(position);
		List<String> list = childMap.get(time);
		holder.time.setText(time);
		holder.sum.setText(list.size() + "");
		Bitmap bitmap = mapBmp.get(time);
		if (bitmap != null) {
			holder.img.setImageBitmap(bitmap);
		}
		return convertView;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	private Bitmap getBitmap(String path) {
		File file = new File(path);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			// 读录像的文件头
			byte[] header = new byte[4];
			in.read(header);
			int fType = byteToInt(header);
			Log.d("tag", "fType:" + fType);
			switch (fType) {
			case 1: {// h264
				byte[] sizebyte = new byte[4];
				byte[] typebyte = new byte[4];
				byte[] timebyte = new byte[4];
				in.read(sizebyte);
				in.read(typebyte);
				in.read(timebyte);
				int length = byteToInt(sizebyte);
				int bIFrame = byteToInt(typebyte);
				int time = byteToInt(timebyte);
				byte[] h264byte = new byte[length];
				in.read(h264byte);
				byte[] yuvbuff = new byte[720 * 1280 * 3 / 2];
				int[] wAndh = new int[2];
				int result = NativeCaller.DecodeH264Frame(h264byte, 1, yuvbuff,
						length, wAndh);
				if (result > 0) {
					Log.d("tag", "h264解析成功");
					int width = wAndh[0];
					int height = wAndh[1];
					Log.d("tag", "width:" + width + " height:" + height);
					byte[] rgb = new byte[width * height * 2];
					NativeCaller.YUV4202RGB565(yuvbuff, rgb, width, height);
					ByteBuffer buffer = ByteBuffer.wrap(rgb);
					Bitmap bitmap = Bitmap.createBitmap(width, height,
							Bitmap.Config.RGB_565);
					bitmap.copyPixelsFromBuffer(buffer);
					Matrix matrix = new Matrix();
					float scaleX = ((float) width) / bitmap.getWidth();
					float scaleY = ((float) width) / bitmap.getHeight();
					matrix.postScale(scaleX, scaleY);
					return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
							bitmap.getHeight(), matrix, true);
				} else {
					Log.d("tag", "h264解析失败");
					return null;
				}
			}
			case 2: {// jpg
				byte[] lengthBytes = new byte[4];
				byte[] timeBytes = new byte[4];
				in.read(lengthBytes);
				in.read(timeBytes);
				int time = byteToInt(timeBytes);
				int length = byteToInt(lengthBytes);
				byte[] contentBytes = new byte[length];
				in.read(contentBytes);
				Bitmap btp = BitmapFactory.decodeByteArray(contentBytes, 0,
						contentBytes.length);
				if (btp != null) {
					Matrix matrix = new Matrix();
					float scaleX = ((float) width) / btp.getWidth();
					float scaleY = ((float) width) / btp.getHeight();
					matrix.postScale(scaleX, scaleY);
					return Bitmap.createBitmap(btp, 0, 0, btp.getWidth(),
							btp.getHeight(), matrix, true);
				} else {
					return null;
				}
			}
			default:
				return null;
			}

		} catch (Exception e) {

		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	private class ViewHolder {
		ImageView img;
		TextView time;
		TextView sum;
		ImageView playvideo;
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
