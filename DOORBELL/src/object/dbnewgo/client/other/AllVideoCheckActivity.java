package object.dbnewgo.client.other;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import object.dbnewgo.client.BaseActivity;
import object.dbnewgo.client.R;
import object.dbnewgo.client.SwipeBackActivity;
import object.p2pipcam.adapter.AllVideoListAdapter;
import object.p2pipcam.bean.MovieInfo;
import object.p2pipcam.system.SystemValue;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AllVideoCheckActivity extends BaseActivity {
	/** sdcardÉÏÂ¼ÏñÎÄ¼þ */
	private Button button_back = null;
	private TextView textView = null;
	private ListView listView = null;
	private List<MovieInfo> movieInfos = null;
	private AllVideoListAdapter adapter = null;
	private AdapterContextMenuInfo info = null;
	private int position = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sdcard_video);
		//setEdgeFromLeft();
		textView = (TextView) findViewById(R.id.takepic_title);

		button_back = (Button) findViewById(R.id.back);
		listView = (ListView) findViewById(R.id.loaded_act_listview);
		movieInfos = new ArrayList<MovieInfo>();
		Intent intent = this.getIntent();
		position = intent.getIntExtra("zhaogeng", 0);
		textView.setText(SystemValue.arrayList.get(position).getName());
		Object[] cobjs = (Object[]) intent.getSerializableExtra("zhaoxing");
		for (int i = 0; i < cobjs.length; i++) {
			MovieInfo user = (MovieInfo) cobjs[i];
			movieInfos.add(user);
		}
		if (movieInfos.size() == 0) {
			listView.setVisibility(View.GONE);
			showToast(R.string.no_videoing_file);
		}
		adapter = new AllVideoListAdapter(this, movieInfos, position);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String bpath = "file://" + movieInfos.get(arg2).getPath();
				Intent it = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse(bpath);
				it.setDataAndType(uri, "video/avi");
				startActivity(it);

			}
		});

		button_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AllVideoCheckActivity.this.finish();
				overridePendingTransition(R.anim.out_to_right,
						R.anim.in_from_left);
			}
		});
		registerForContextMenu(listView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(getResources().getString(R.string.exit_show));
		menu.add(0, 2, 0, getResources().getString(R.string.exit_qu));
		menu.add(0, 3, 0, getResources().getString(R.string.exit_qu_all));
		menu.add(0, 7, 0, getResources().getString(R.string.exit_qu_show));

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//showAll(button_back, "AllVideoCheckActivity");
		super.onResume();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == 2) {
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			SystemValue.checkSDStatu = 1;
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					File apkFile = new File(movieInfos.get(info.position)
							.getPath());
					boolean b = apkFile.delete();
					movieInfos.remove(info.position);
					return null;
				}

				protected void onPostExecute(Void result) {
					SystemValue.checkSDStatu = 1;
					adapter.notifyDataSetChanged();
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.delect_sucss), 0)
							.show();

				};

			}.execute();
		} else if (item.getItemId() == 3) {
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			SystemValue.checkSDStatu = 1;
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {

					for (int i = movieInfos.size() - 1; i >= 0; i--) {
						File apkFile = new File(movieInfos.get(i).getPath());
						boolean b = apkFile.delete();
						movieInfos.remove(i);
					}
					return null;
				}

				protected void onPostExecute(Void result) {
					if (movieInfos.size() == 0) {
						listView.setVisibility(View.GONE);
					}
					SystemValue.checkSDStatu = 1;
					adapter.notifyDataSetChanged();
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.delect_sucss), 0)
							.show();

				};

			}.execute();
		}

		return false;
	}
}