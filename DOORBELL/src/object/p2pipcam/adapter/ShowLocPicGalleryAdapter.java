package object.p2pipcam.adapter;

import java.util.ArrayList;
import java.util.Map;

import object.dbnewgo.client.R;
import object.p2pipcam.customComponent.MyLocalPicImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;

public class ShowLocPicGalleryAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Map<String, Object>> list;
	private MyLocalPicImageView img;
	private Matrix matrix;

	public ShowLocPicGalleryAdapter(Context context,
			ArrayList<Map<String, Object>> list) {
		this.context = context;
		this.list = list;
		matrix = new Matrix();
		// Àı∑≈‘≠Õº
		matrix.postScale(0.7f, 0.7f);
	}

	@Override
	public int getCount() {
		return list.size();
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
		Map<String, Object> map = list.get(position);
		String path = (String) map.get("path");
		try {
			Bitmap bitmap = getBitmap(path);
			img = new MyLocalPicImageView(context, bitmap.getWidth(),
					bitmap.getHeight());
			img.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			img.setImageBitmap(bitmap);
		} catch (Exception e) {
			Bitmap bitmap = getBitmap(path);
			img = new MyLocalPicImageView(context, 100, 50);
			img.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			img.setImageBitmap(bitmap);
		}
		return img;
	}

	private Bitmap getBitmap(String path) {
		Bitmap btp = BitmapFactory.decodeFile(path);
		if (btp != null) {
			if (btp.getWidth() > 640 && btp.getHeight() > 480) {
				btp = Bitmap.createBitmap(btp, 0, 0, btp.getWidth(),
						btp.getHeight(), matrix, true);
			}
			return btp;
		} else {
			return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.camer_ico), 200, 200,
					true);
		}

	}

	// private Bitmap returnBitmap(Bitmap bitmap) {
	// bitmap = Bitmap.createBitmap(bitmap, 0, 0, 200, 150);
	// bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
	// bitmap.getHeight(), matrix, true);
	// return bitmap;
	// }
}
