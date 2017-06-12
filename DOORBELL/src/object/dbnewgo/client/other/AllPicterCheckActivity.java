package object.dbnewgo.client.other;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import object.dbnewgo.client.BaseActivity;
import object.dbnewgo.client.R;
import object.dbnewgo.client.ShowLocalPictureActivity;
import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.gridview.PullToRefreshBase.OnRefreshListener;
import object.p2pipcam.gridview.PullToRefreshGridView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AllPicterCheckActivity extends BaseActivity {
	/** sdcard */
	private GridView gridView = null;
	private List<String> listpath = null;// 图片路径集合
	private ProgressDialog dialog = null;
	private GridAdapter adapter = null;
	private ProgressBar progressBar = null;// 标题进度
	private Button button_back = null;
	private static ArrayList<Map<String, Object>> arrayList;
	private AdapterContextMenuInfo info = null;
	private TextView textView = null;
	private PullToRefreshGridView mPullRefreshGridView;

	class MyAsySelect extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(AllPicterCheckActivity.this);
			dialog.setMessage(getResources().getString(
					R.string.local_all_picture_loading));
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			FindImage findImage = new FindImage(
					Environment.getExternalStorageDirectory() + "/IPcamer/pic");
			listpath = findImage.getList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (listpath.size() != 0) {
				adapter = new GridAdapter(AllPicterCheckActivity.this, listpath);
				gridView.setAdapter(adapter);
				gridView.setSelector(R.drawable.grid_select_no);
				textView.setVisibility(View.VISIBLE);
				textView.setText(listpath.size() + "");
				dialog.cancel();
				getDataFromOther();
				progressBar.setVisibility(View.GONE);
			} else {
				dialog.cancel();
				progressBar.setVisibility(View.GONE);
				showToast(R.string.local_all_picture_nopic);
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sdcard_picter);
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.sdcard_gridview);
		gridView = mPullRefreshGridView.getRefreshableView();
		progressBar = (ProgressBar) findViewById(R.id.pict_progressBar1);
		button_back = (Button) findViewById(R.id.back);
		textView = (TextView) findViewById(R.id.picter_sum);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		MyAsySelect asySelect = new MyAsySelect();
		asySelect.execute();
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(AllPicterCheckActivity.this,
						ShowLocalPictureActivity.class);
				intent.putExtra("did", "");
				intent.putExtra("list", arrayList);
				intent.putExtra("date", "");
				intent.putExtra("position", arg2);
				intent.putExtra(ContentCommon.STR_CAMERA_NAME, getResources()
						.getString(R.string.local_all_picture_all));
				startActivityForResult(intent, 1);
			}
		});
		button_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AllPicterCheckActivity.this.finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);
			}
		});
		registerForContextMenu(gridView);
		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				new GetDataTask().execute();
			}
		});
	}

	private class GetDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			if (adapter.count <= listpath.size() - 20) {
				adapter.count += 20;
			} else if (20 > listpath.size() - adapter.count) {
				adapter.count = listpath.size();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			if (listpath.size() == adapter.count) {
				showToast(R.string.local_all_picture_loadsucess);
			}
			adapter.notifyDataSetChanged();
			mPullRefreshGridView.onRefreshComplete();
			super.onPostExecute(arg0);
		}
	}

	private void getDataFromOther() {
		arrayList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < listpath.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			String path = listpath.get(i);
			map.put("path", path);
			map.put("status", 0);
			arrayList.add(map);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {

		menu.setHeaderTitle(getResources().getString(R.string.exit_show));
		menu.add(0, 2, 0, getResources().getString(R.string.exit_qu));
		menu.add(0, 7, 0, getResources().getString(R.string.exit_qu_show));

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == 2) {
			info = (AdapterContextMenuInfo) item.getMenuInfo();

			// SystemValue.checkSDStatu = 1;
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					File apkFile = new File(listpath.get(info.position));
					apkFile.delete();
					arrayList.remove(info.position);
					listpath.remove(info.position);
					return null;
				}

				protected void onPostExecute(Void result) {
					adapter = new GridAdapter(AllPicterCheckActivity.this,
							listpath);
					gridView.setAdapter(adapter);
					textView.setText(listpath.size() + "");
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.delect_sucss), 0)
							.show();

				};

			}.execute();
		}

		return false;
	}

}