/**
 * 获得sd卡图片
 */
package object.dbnewgo.client.other;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import object.dbnewgo.client.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author : 赵耿怀
 * @version ：2012-12-28 下午5:31:55
 */
public class VideoGridAdapter extends BaseAdapter {

	public List<String> all_select = null;
	public Vector<Boolean> mImage = null;
	private List<String> list = null;
	private TextView textView = null;
	private LayoutInflater inflater = null;
	public Vector<Boolean> mImage_bs = new Vector<Boolean>(); // 定义一个向量作为选中与否容器
	private LinearLayout layout = null;
	private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();

	public VideoGridAdapter(Context context, List<String> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		for (int i = 0; i < list.size(); i++) {
			mImage_bs.add(false);
		}
	}

	// 把图片添加到数组
	public void addPhoto(LoadedImage photo) {
		photos.add(photo);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String s1 = list.get(position).substring(
				list.get(position).lastIndexOf("/") + 1);
		String ssid = s1.substring(s1.indexOf("!") + 1);
		String date = s1.substring(0, 10);
		String time = s1.substring(11, 16).replace("_", ":");
		String result = ssid + "   " + date + "  " + time;
		layout = (LinearLayout) inflater.inflate(R.layout.gridviewitemb, null);
		layout.setTag(position);
		textView = (TextView) layout
				.findViewById(R.id.all_pic_check_grid_textView1);
		try {
			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 4;
			// Bitmap bitmap = BitmapFactory.decodeFile(list.get(position)
			// .toString(), options);

			// Bitmap bitmap = PictSet.readBitmapAutoSize(list.get(position)
			// .toString(), width, heigth);
			textView.setText(result);
			// if (!bitmap.isRecycled()) {
			// bitmap.recycle(); // 回收图片所占的内存
			// System.gc(); // 提醒系统及时回收
			// }
		} catch (Exception e) {
		}
		return layout;
	}
}
