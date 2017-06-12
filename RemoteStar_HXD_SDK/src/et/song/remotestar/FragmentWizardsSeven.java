package et.song.remotestar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.etclass.ETDevice;
import et.song.etclass.ETDeviceCustom;
import et.song.etclass.ETGroup;
import et.song.etclass.ETKey;
import et.song.etclass.ETPage;
import et.song.etclass.ETSave;
import et.song.face.IBack;
import et.song.global.ETGlobal;
import et.song.remotestar.hxd.sdk.R;
import et.song.tool.ETTool;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class FragmentWizardsSeven extends SherlockFragment implements
		OnClickListener, OnLongClickListener, OnTouchListener, IBack {
	private int mGroupIndex;
	private ETDevice mDevice = null;
	private RecvReceiver mReceiver;
	private int lastX, lastY;
	private boolean mIsDel = false;
	private boolean mIsLongPressed = false;
	private View mView = null;
	private SparseArray<ETKey> mKeys = new SparseArray<ETKey>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mGroupIndex = this.getArguments().getInt("group");
		ETGroup group = (ETGroup) ETPage.getInstance(getActivity()).GetItem(
				mGroupIndex);
		mDevice = new ETDeviceCustom();
		mDevice.SetGID(group.GetID());
		mDevice.SetType(DeviceType.DEVICE_REMOTE_CUSTOM);
		mDevice.SetRes(9);
		mDevice.SetName("DIY");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getActivity().setTitle(R.string.str_wizards);

		View view = null;
		view = inflater.inflate(R.layout.fragment_custom, container, false);
		mView = view;
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mReceiver = new RecvReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ETGlobal.BROADCAST_PASS_LEARN);
		filter.addAction(ETGlobal.BROADCAST_APP_BACK);
		getActivity().registerReceiver(mReceiver, filter);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				true);
		getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Nothing to see here.
		menu.clear();
		inflater.inflate(R.menu.menu_custom, menu);
		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("Home", "Home");
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		
		int id=item.getItemId();
		if (id==android.R.id.home) {
			Back();
			return true;
		}else if (id==R.id.menu_add_button) {
			
//		}
//		
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			Back();
//			return true;
//		case R.id.menu_add_button:
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			View addButtonView = mInflater.inflate(R.layout.dialog_add_button,
					null);
			final RadioButton radio_image = (RadioButton) addButtonView
					.findViewById(R.id.radio_image);
			final RadioButton radio_name = (RadioButton) addButtonView
					.findViewById(R.id.radio_text);
			final EditText edit_name = (EditText) addButtonView
					.findViewById(R.id.edit_name);
			final Spinner spinner_image = (Spinner) addButtonView
					.findViewById(R.id.spinner_image);
			spinner_image
					.setAdapter(new ImageAdapter(ETGlobal.mAddButtonImages));

			radio_image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					edit_name.setVisibility(View.GONE);
					spinner_image.setVisibility(View.VISIBLE);
				}

			});
			radio_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					edit_name.setVisibility(View.VISIBLE);
					spinner_image.setVisibility(View.GONE);
				}

			});
			AlertDialog addButtonDialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.str_add_button)
					.setView(addButtonView)
					.setPositiveButton(R.string.str_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									if (radio_image.isChecked()) {
										AddButtonByImage(spinner_image
												.getSelectedItemPosition());

									} else if (radio_name.isChecked()) {
										AddButtonByText(edit_name.getText()
												.toString());
									}
									ShowInfo();
								}
							})
					.setNegativeButton(R.string.str_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).create();
			addButtonDialog.show();
			return true;
		}else if (id==R.id.menu_save) {
			
//		}
//		case R.id.menu_save:
			for (int i = 0; i < mKeys.size(); i++) {
				mDevice.SetKey(mKeys.valueAt(i));
			}
			FragmentDevice fragmentDevice = new FragmentDevice();
			mDevice.Inster(ETDB.getInstance(getActivity()));
			args.putInt("group", mGroupIndex);
			fragmentDevice.setArguments(args);
			transaction.replace(R.id.fragment_container, fragmentDevice);
			transaction.addToBackStack(null);
			transaction.commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		int key = ((Long) v.getTag()).intValue();
		if (key != 0) {
			Intent intentStartLearn = new Intent(
					ETGlobal.BROADCAST_START_LEARN);
			intentStartLearn.putExtra("select", "0");
			intentStartLearn.putExtra("key", key);
			getActivity().sendBroadcast(intentStartLearn);
		}
	}

	public class RecvReceiver extends BroadcastReceiver {
		@SuppressLint({ "InlinedApi", "NewApi" })
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ETGlobal.BROADCAST_PASS_LEARN)) {
				try {
					Log.i("Recv", "Recv");
					String select = intent.getStringExtra("select");
					int key = intent.getIntExtra("key", 0);
					String msg = intent.getStringExtra("msg");
					Log.i("Key",
							String.valueOf(ETTool.HexStringToBytes(msg).length));
					if (select.equals("0")) {
						ETKey k = mKeys.get(key);
						if (k != null) {
							k.SetValue(ETTool.HexStringToBytes(msg));
						}
						// ETKey k = new ETKey();
						// k.SetDID(mDevice.GetID());
						// k.SetBrandIndex(0);
						// k.SetBrandPos(0);
						// k.SetKey(key);
						// k.SetName("");
						// k.SetPos(0, 0);
						// k.SetRes(0);
						// k.SetRow(0);
						// k.SetState(ETKey.ETKEY_STATE_STUDY);
						// k.SetValue(ETTool.HexStringToBytes(msg));
						// mDevice.SetKey(k);
					} else if (select.equals("1")) {

					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (action.equals(ETGlobal.BROADCAST_APP_BACK)) {
				Back();
			}
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		mIsLongPressed = true;
		return true;
	}

	public class ImageAdapter extends BaseAdapter {
		private int drawableIDs[];

		public ImageAdapter(int DrawableIDs[]) {
			drawableIDs = DrawableIDs;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return drawableIDs.length;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return drawableIDs[position];
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LinearLayout view = new LinearLayout(getActivity());
			view.setBackgroundColor(Color.BLACK);
			view.setOrientation(LinearLayout.HORIZONTAL);
			view.setGravity(Gravity.CENTER_VERTICAL);
			ImageView image = new ImageView(getActivity());
			image.setImageResource(drawableIDs[position]);
			image.setLayoutParams(new ViewGroup.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			view.addView(image);
			return view;
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			// mLastTime = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_MOVE:
			int dx = (int) event.getRawX() - lastX;
			int dy = (int) event.getRawY() - lastY;

			int left = v.getLeft() + dx;
			int top = v.getTop() + dy;
			int right = v.getRight() + dx;
			int bottom = v.getBottom() + dy;
			if (left < 0) {
				left = 0;
				right = left + v.getWidth();
			}
			if (right > ETGlobal.W) {
				right = ETGlobal.W;
				left = right - v.getWidth();
			}
			if (top < 0) {
				top = 0;
				bottom = top + v.getHeight();
			}
			if (bottom > ETGlobal.H) {
				bottom = ETGlobal.H;
				top = bottom - v.getHeight();
			}
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			if (mIsLongPressed) {
				if (top < 1 || left < 1 || bottom > ETGlobal.H - 1
						|| right > ETGlobal.W - 1) {
					v.setBackgroundResource(R.drawable.btn_style_red);
					mIsDel = true;
				} else {
					v.setBackgroundResource(R.drawable.btn_style_green);
					mIsDel = false;
				}
				v.layout(left, top, right, bottom);
				RelativeLayout.LayoutParams viewParams = (RelativeLayout.LayoutParams) v
						.getLayoutParams();
				viewParams.leftMargin = left;
				viewParams.topMargin = top;
				ETKey key = mKeys.get(((Long) v.getTag()).intValue());
				key.SetPos(left, top);
			}
			break;
		case MotionEvent.ACTION_UP:
			mIsLongPressed = false;
			if (mIsDel) {
				mIsDel = false;
				v.setVisibility(View.GONE);
				mKeys.remove(((Long) v.getTag()).intValue());
			}
			break;
		}
		return false;
	}

	private void ShowInfo() {
		if (ETSave.getInstance(getActivity()).get("isWizardsSeven").equals("1")) {
			return;
		}
		Dialog alertDialog = new AlertDialog.Builder(getActivity())
				.setMessage(R.string.str_study_start_info_5)
				.setIcon(R.drawable.ic_launcher)
				.setNegativeButton(R.string.str_info_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated
								// method stub
							}
						})
				.setPositiveButton(R.string.str_info_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ETSave.getInstance(getActivity()).put(
										"isWizardsSeven", "1");
							}
						}).create();
		alertDialog.show();
	}

	public void AddButtonByText(String text) {
		try {
			RelativeLayout content = (RelativeLayout) mView
					.findViewById(R.id.content);
			TextView button_custom = new TextView(getActivity());
			button_custom.setOnClickListener(this);
			button_custom.setOnLongClickListener(this);
			button_custom.setOnTouchListener(this);
			RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
					(ETGlobal.W - 80) / 5, (ETGlobal.W - 80) / 7);
			viewParams.leftMargin = 5;
			viewParams.topMargin = 5;
			// viewParams.setMargins(5, 5, 5, 5);
			button_custom.setText(text);
			button_custom.setBackgroundResource(R.drawable.btn_style_green);
			button_custom.setTextColor(Color.WHITE);
			button_custom.setGravity(Gravity.CENTER);
			button_custom.setTag(System.currentTimeMillis());

			ETKey key = new ETKey();
			key.SetKey(((Long) button_custom.getTag()).intValue());
			key.SetPos(5, 5);
			key.SetRes(-1);
			key.SetName(text);
			key.SetBrandIndex(-1);
			key.SetBrandPos(-1);
			key.SetRow(-1);
			key.SetState(ETKey.ETKEY_STATE_STUDY);
			key.SetValue(ETTool.HexStringToBytes(""));
			// mDevice.SetKey(key);
			content.addView(button_custom, viewParams);
			mKeys.put(((Long) button_custom.getTag()).intValue(), key);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void AddButtonByImage(int resID) {
		try {
			RelativeLayout content = (RelativeLayout) mView
					.findViewById(R.id.content);
			ImageView button_custom = new ImageView(getActivity());
			button_custom.setOnClickListener(this);
			button_custom.setOnLongClickListener(this);
			button_custom.setOnTouchListener(this);
			RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
					(ETGlobal.W - 80) / 5, (ETGlobal.W - 80) / 7);
			viewParams.leftMargin = 5;
			viewParams.topMargin = 5;
			// viewParams.setMargins(5, 5, 5, 5);
			button_custom.setImageResource(ETGlobal.mAddButtonImages[resID]);
			// button_custom.setText(ETHtml.GetSpanned(getActivity(),
			// "<img src='"
			// + ETGlobal.mAddButtonImages[resID] + "'/>"));
			// ETButton.setButton(button_custom);
			button_custom.setBackgroundResource(R.drawable.btn_style_green);
			// button_custom.setTextColor(Color.WHITE);
			// button_custom.setGravity(Gravity.CENTER);
			button_custom.setTag(System.currentTimeMillis());
			ETKey key = new ETKey();
			key.SetKey(((Long) button_custom.getTag()).intValue());
			key.SetPos(5, 5);
			key.SetRes(resID);
			key.SetName("");
			key.SetBrandIndex(-1);
			key.SetBrandPos(-1);
			key.SetRow(-1);
			key.SetState(ETKey.ETKEY_STATE_STUDY);
			key.SetValue(ETTool.HexStringToBytes(""));
			// mDevice.SetKey(key);
			content.addView(button_custom, viewParams);
			mKeys.put(((Long) button_custom.getTag()).intValue(), key);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		FragmentWizardsOne fragmentOne = new FragmentWizardsOne();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("group", mGroupIndex);
		fragmentOne.setArguments(args);
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragmentOne);
		// transactionBt.addToBackStack(null);
//		transaction
//				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	}
}
