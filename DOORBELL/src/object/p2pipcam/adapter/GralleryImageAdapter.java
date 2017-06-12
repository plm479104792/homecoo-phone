package object.p2pipcam.adapter;

import object.dbnewgo.client.R;
import object.p2pipcam.bean.CameraParamsBean;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.system.SystemValue;
import object.p2pipcam.utils.DataBaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GralleryImageAdapter extends BaseAdapter {
	private DataBaseHelper helper = null;
	private Context context;
	private String dids;
	private LayoutInflater listContainer = null;

	public final class CameraListItem {
		public ImageView imgSnapShot;
		public TextView devName;
		public TextView devStatus;
	}

	public GralleryImageAdapter(Context context) {
		// TODO Auto-generated constructor stub
		helper = DataBaseHelper.getInstance(context);
		this.context = context;
		listContainer = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return SystemValue.arrayList.get(position%SystemValue.arrayList.size());
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position%SystemValue.arrayList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		synchronized (this) {
			CameraListItem cameraListItem = null;
			if (convertView == null) {
				cameraListItem = new CameraListItem();
				convertView = listContainer.inflate(
						R.layout.camera_list_item_grallery, null);

				cameraListItem.imgSnapShot = (ImageView) convertView
						.findViewById(R.id.imgSnapshot);
				cameraListItem.devName = (TextView) convertView
						.findViewById(R.id.cameraDevName);
				cameraListItem.devStatus = (TextView) convertView
						.findViewById(R.id.textPPPPStatus);
				convertView.setTag(cameraListItem);
			} else {
				cameraListItem = (CameraListItem) convertView.getTag();
			}
			CameraParamsBean bean = SystemValue.arrayList.get(position%SystemValue.arrayList.size());
			cameraListItem.devName.setText(bean.getName());
			Bitmap bmp2 = bean.getBmp();
			if (bmp2 != null) {
				Drawable drawable = new BitmapDrawable(bmp2);
				cameraListItem.imgSnapShot.setBackgroundDrawable(drawable);
			} else {
				Bitmap bitmap = getFirstPic(bean.getDid());
				if (bitmap != null) {
					Drawable dra = new BitmapDrawable(bitmap);
					cameraListItem.imgSnapShot.setBackgroundDrawable(dra);
				} else {
					cameraListItem.imgSnapShot
							.setBackgroundResource(R.drawable.vidicon);
				}
			}
			int status = bean.getStatus();
			dids = bean.getDid();
			int resid;
			switch (status) {
			case ContentCommon.PPPP_STATUS_CONNECTING:
				resid = R.string.pppp_status_connecting;
				break;
			case ContentCommon.PPPP_STATUS_CONNECT_FAILED:
				resid = R.string.pppp_status_connect_failed;
				break;
			case ContentCommon.PPPP_STATUS_DISCONNECT:
				resid = R.string.pppp_status_disconnect;
				break;
			case ContentCommon.PPPP_STATUS_INITIALING:
				resid = R.string.pppp_status_initialing;
				break;
			case ContentCommon.PPPP_STATUS_INVALID_ID:
				resid = R.string.pppp_status_invalid_id;
				break;
			case ContentCommon.PPPP_STATUS_ON_LINE:
				resid = R.string.pppp_status_online;
				break;
			case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE:
				resid = R.string.device_not_on_line;
				break;
			case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT:
				resid = R.string.pppp_status_connect_timeout;
				break;
			case ContentCommon.PPPP_STATUS_CONNECT_ERRER:
				resid = R.string.pppp_status_connect_log_errer;
				break;
			default:
				resid = R.string.pppp_status_unknown;
			}
			cameraListItem.devStatus.setText(resid);
		}
		return convertView;
	}

	private Bitmap getFirstPic(String did) {
		Cursor cursor = helper.queryFirstpic(did);
		String filepath = null;
		if (cursor.getCount() > 0) {
			cursor.moveToNext();
			filepath = cursor.getString(cursor
					.getColumnIndex(DataBaseHelper.KEY_FILEPATH));
		}
		if (cursor != null) {
			cursor.close();
		}
		if (filepath != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(filepath);
			return bitmap;

		} else {
			return null;
		}

	}
}
