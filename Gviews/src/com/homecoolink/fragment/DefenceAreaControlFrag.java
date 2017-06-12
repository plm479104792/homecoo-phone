package com.homecoolink.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homecoolink.R;
import com.homecoolink.SettingListener;
import com.homecoolink.activity.MainControlActivity;
import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.data.DefenceAreaName;
import com.homecoolink.entity.DefenceObject;
import com.homecoolink.global.Constants;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.P2PHandler;


public class DefenceAreaControlFrag extends BaseFragment implements
		OnClickListener {
	private Context mContext;
	private Contact contact;
	private boolean isRegFilter = false;
	private LayoutInflater lif;
	RelativeLayout change_defence_area1, change_defence_area2,
			change_defence_area3, change_defence_area4, change_defence_area5,
			change_defence_area6, change_defence_area7, change_defence_area8,
			change_defence_area9;
	LinearLayout defence_area_content1, defence_area_content2,
			defence_area_content3, defence_area_content4,
			defence_area_content5, defence_area_content6,
			defence_area_content7, defence_area_content8,
			defence_area_content9;
	ProgressBar progressBar_defence_area1, progressBar_defence_area2,
			progressBar_defence_area3, progressBar_defence_area4,
			progressBar_defence_area5, progressBar_defence_area6,
			progressBar_defence_area7, progressBar_defence_area8,
			progressBar_defence_area9;

	NormalDialog dialog_loading;
	ImageView arrow1, arrow2, arrow3, arrow4, arrow5, arrow6, arrow7, arrow8,
			arrow9;

	ImageView fenge1, fenge2, fenge3, fenge4, fenge5, fenge6, fenge7, fenge8,
			fenge9;

	ImageButton imgbtn1, imgbtn2, imgbtn3, imgbtn4, imgbtn5, imgbtn6, imgbtn7,
			imgbtn8, imgbtn9;

	public ArrayList<String[]> tatol;
	int current_group;
	int current_item;
	int current_type;

	boolean is_one_active = false;
	boolean is_two_active = false;
	boolean is_three_active = false;
	boolean is_four_active = false;
	boolean is_five_active = false;
	boolean is_six_active = false;
	boolean is_seven_active = false;
	boolean is_eight_active = false;
	boolean is_nine_active = false;
	private static final int EXPAND_OR_SHRINK = 0x11;
	private static final int END_EXPAND_OR_SHRINK = 0x12;
	private static final int TEST1 = 0x13;
	private String[][] totals;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		totals = new String[][] {
				{ "00", "01", "02", "03", "04", "05", "06", "07" },
				{ "10", "11", "12", "13", "14", "15", "16", "17" },
				{ "20", "21", "22", "23", "24", "25", "26", "27" },
				{ "30", "31", "32", "33", "34", "35", "36", "37" },
				{ "40", "41", "42", "43", "44", "45", "46", "47" },
				{ "50", "51", "52", "53", "54", "55", "56", "57" },
				{ "60", "61", "62", "63", "64", "65", "66", "67" },
				{ "70", "71", "72", "73", "74", "75", "76", "77" },
				{ "80", "81", "82", "83", "84", "85", "86", "87" } };
		mContext = MainControlActivity.mContext;
		contact = (Contact) getArguments().getSerializable("contact");
		lif = inflater;
		View view = inflater.inflate(R.layout.fragment_defence_area_control,
				container, false);
		initComponent(view);
		regFilter();

		P2PHandler.getInstance().getDefenceArea(contact.contactId,
				contact.contactPassword);

		return view;
	}

	public void initComponent(View view) {
		change_defence_area1 = (RelativeLayout) view
				.findViewById(R.id.change_defence_area1);
		defence_area_content1 = (LinearLayout) view
				.findViewById(R.id.defence_area_content1);
		progressBar_defence_area1 = (ProgressBar) view
				.findViewById(R.id.progressBar_defence_area1);
		arrow1 = (ImageView) view.findViewById(R.id.change_defence_area_arrow1);
		imgbtn1 = (ImageButton) view.findViewById(R.id.add_btn1);

		change_defence_area2 = (RelativeLayout) view
				.findViewById(R.id.change_defence_area2);
		defence_area_content2 = (LinearLayout) view
				.findViewById(R.id.defence_area_content2);
		progressBar_defence_area2 = (ProgressBar) view
				.findViewById(R.id.progressBar_defence_area2);
		arrow2 = (ImageView) view.findViewById(R.id.change_defence_area_arrow2);
		imgbtn2 = (ImageButton) view.findViewById(R.id.add_btn2);

		change_defence_area3 = (RelativeLayout) view
				.findViewById(R.id.change_defence_area3);
		defence_area_content3 = (LinearLayout) view
				.findViewById(R.id.defence_area_content3);
		progressBar_defence_area3 = (ProgressBar) view
				.findViewById(R.id.progressBar_defence_area3);
		arrow3 = (ImageView) view.findViewById(R.id.change_defence_area_arrow3);
		imgbtn3 = (ImageButton) view.findViewById(R.id.add_btn3);

		change_defence_area4 = (RelativeLayout) view
				.findViewById(R.id.change_defence_area4);
		defence_area_content4 = (LinearLayout) view
				.findViewById(R.id.defence_area_content4);
		progressBar_defence_area4 = (ProgressBar) view
				.findViewById(R.id.progressBar_defence_area4);
		arrow4 = (ImageView) view.findViewById(R.id.change_defence_area_arrow4);
		imgbtn4 = (ImageButton) view.findViewById(R.id.add_btn4);

		change_defence_area5 = (RelativeLayout) view
				.findViewById(R.id.change_defence_area5);
		defence_area_content5 = (LinearLayout) view
				.findViewById(R.id.defence_area_content5);
		progressBar_defence_area5 = (ProgressBar) view
				.findViewById(R.id.progressBar_defence_area5);
		arrow5 = (ImageView) view.findViewById(R.id.change_defence_area_arrow5);
		imgbtn5 = (ImageButton) view.findViewById(R.id.add_btn5);

		change_defence_area6 = (RelativeLayout) view
				.findViewById(R.id.change_defence_area6);
		defence_area_content6 = (LinearLayout) view
				.findViewById(R.id.defence_area_content6);
		progressBar_defence_area6 = (ProgressBar) view
				.findViewById(R.id.progressBar_defence_area6);
		arrow6 = (ImageView) view.findViewById(R.id.change_defence_area_arrow6);
		imgbtn6 = (ImageButton) view.findViewById(R.id.add_btn6);

		change_defence_area7 = (RelativeLayout) view
				.findViewById(R.id.change_defence_area7);
		defence_area_content7 = (LinearLayout) view
				.findViewById(R.id.defence_area_content7);
		progressBar_defence_area7 = (ProgressBar) view
				.findViewById(R.id.progressBar_defence_area7);
		arrow7 = (ImageView) view.findViewById(R.id.change_defence_area_arrow7);
		imgbtn7 = (ImageButton) view.findViewById(R.id.add_btn7);

		change_defence_area8 = (RelativeLayout) view
				.findViewById(R.id.change_defence_area8);
		defence_area_content8 = (LinearLayout) view
				.findViewById(R.id.defence_area_content8);
		progressBar_defence_area8 = (ProgressBar) view
				.findViewById(R.id.progressBar_defence_area8);
		arrow8 = (ImageView) view.findViewById(R.id.change_defence_area_arrow8);
		imgbtn8 = (ImageButton) view.findViewById(R.id.add_btn8);

		change_defence_area9 = (RelativeLayout) view
				.findViewById(R.id.change_defence_area9);
		defence_area_content9 = (LinearLayout) view
				.findViewById(R.id.defence_area_content9);
		progressBar_defence_area9 = (ProgressBar) view
				.findViewById(R.id.progressBar_defence_area9);
		arrow9 = (ImageView) view.findViewById(R.id.change_defence_area_arrow9);
		imgbtn9 = (ImageButton) view.findViewById(R.id.add_btn9);

		fenge1 = (ImageView) view.findViewById(R.id.fenge1);
		fenge2 = (ImageView) view.findViewById(R.id.fenge2);
		fenge3 = (ImageView) view.findViewById(R.id.fenge3);
		fenge4 = (ImageView) view.findViewById(R.id.fenge4);
		fenge5 = (ImageView) view.findViewById(R.id.fenge5);
		fenge6 = (ImageView) view.findViewById(R.id.fenge6);
		fenge7 = (ImageView) view.findViewById(R.id.fenge7);
		fenge8 = (ImageView) view.findViewById(R.id.fenge8);
		fenge9 = (ImageView) view.findViewById(R.id.fenge9);

		imgbtn1.setOnClickListener(this);
		imgbtn2.setOnClickListener(this);
		imgbtn3.setOnClickListener(this);
		imgbtn4.setOnClickListener(this);
		imgbtn5.setOnClickListener(this);
		imgbtn6.setOnClickListener(this);
		imgbtn7.setOnClickListener(this);
		imgbtn8.setOnClickListener(this);
		imgbtn9.setOnClickListener(this);

	}

	public void regFilter() {
		IntentFilter filter = new IntentFilter();

		filter.addAction(Constants.P2P.ACK_RET_SET_DEFENCE_AREA);
		filter.addAction(Constants.P2P.ACK_RET_GET_DEFENCE_AREA);
		filter.addAction(Constants.P2P.ACK_RET_CLEAR_DEFENCE_AREA);
		filter.addAction(Constants.P2P.RET_CLEAR_DEFENCE_AREA);
		filter.addAction(Constants.P2P.RET_SET_DEFENCE_AREA);
		filter.addAction(Constants.P2P.RET_GET_DEFENCE_AREA);
		filter.addAction(Constants.P2P.RET_DEVICE_NOT_SUPPORT);

		mContext.registerReceiver(mReceiver, filter);
		isRegFilter = true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.P2P.RET_GET_DEFENCE_AREA)) {
				ArrayList<int[]> data = (ArrayList<int[]>) intent
						.getSerializableExtra("data");
				initData(data);

				showDefence_area1();

			} else if (intent.getAction().equals(
					Constants.P2P.RET_SET_DEFENCE_AREA)) {
				if (null != dialog_loading) {
					dialog_loading.dismiss();
					dialog_loading = null;
				}
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.DEFENCE_AREA_SET.SETTING_SUCCESS) {
					if (current_type == Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR) {
						// grayButton(current_group, current_item, "");
						
						bl = true;
						DefenceAreaName dan = new DefenceAreaName();
						dan.groupI = current_group + "";
						dan.groupJ = current_item + "";
						dan.groupIJ = "1";
						dan.groupName = "";
						DataManager.upDefenceAreaName(mContext, dan);
						clearDivWithTag();
						T.showShort(mContext, R.string.clear_success);
					} else {
						// lightButton(current_group, current_item, "");
						
						
						bl = true;

						switch (current_group) {
						case 0:
							TextView reName1 = (TextView) defence_area_content1
									.findViewWithTag(
											current_group + "" + current_item)
									.findViewById(R.id.one1_rename_btn);
							DefenceObject defenceObject1 = (DefenceObject) reName1
									.getTag();
							defenceObject1.isVisible = true;
							reName1.setVisibility(View.VISIBLE);

							DefenceAreaName dAreaName1 = new DefenceAreaName();
							dAreaName1.groupI = current_group + "";
							dAreaName1.groupJ = current_item + "";
							dAreaName1.groupIJ = 0 + "";
							dAreaName1.groupName = "";

							DataManager.upDefenceAreaName(mContext, dAreaName1);

							break;
						case 1:
							TextView reName2 = (TextView) defence_area_content2
									.findViewWithTag(
											current_group + "" + current_item)
									.findViewById(R.id.one1_rename_btn);
							DefenceObject defenceObject2 = (DefenceObject) reName2
									.getTag();
							defenceObject2.isVisible = true;
							reName2.setVisibility(View.VISIBLE);

							DefenceAreaName dAreaName2 = new DefenceAreaName();
							dAreaName2.groupI = current_group + "";
							dAreaName2.groupJ = current_item + "";
							dAreaName2.groupIJ = 0 + "";
							dAreaName2.groupName = "";
							DataManager.upDefenceAreaName(mContext, dAreaName2);

							break;
						case 2:
							TextView reName3 = (TextView) defence_area_content3
									.findViewWithTag(
											current_group + "" + current_item)
									.findViewById(R.id.one1_rename_btn);
							DefenceObject defenceObject3 = (DefenceObject) reName3
									.getTag();
							defenceObject3.isVisible = true;
							reName3.setVisibility(View.VISIBLE);

							DefenceAreaName dAreaName3 = new DefenceAreaName();
							dAreaName3.groupI = current_group + "";
							dAreaName3.groupJ = current_item + "";
							dAreaName3.groupIJ = 0 + "";
							dAreaName3.groupName = "";
							DataManager.upDefenceAreaName(mContext, dAreaName3);
							break;
						case 3:

							TextView reName4 = (TextView) defence_area_content4
									.findViewWithTag(
											current_group + "" + current_item)
									.findViewById(R.id.one1_rename_btn);
							DefenceObject defenceObject4 = (DefenceObject) reName4
									.getTag();
							defenceObject4.isVisible = true;
							reName4.setVisibility(View.VISIBLE);

							DefenceAreaName dAreaName4 = new DefenceAreaName();
							dAreaName4.groupI = current_group + "";
							dAreaName4.groupJ = current_item + "";
							dAreaName4.groupIJ = 0 + "";
							dAreaName4.groupName = "";
							DataManager.upDefenceAreaName(mContext, dAreaName4);
							break;
						case 4:
							TextView reName5 = (TextView) defence_area_content5
									.findViewWithTag(
											current_group + "" + current_item)
									.findViewById(R.id.one1_rename_btn);
							DefenceObject defenceObject5 = (DefenceObject) reName5
									.getTag();
							defenceObject5.isVisible = true;
							reName5.setVisibility(View.VISIBLE);

							DefenceAreaName dAreaName5 = new DefenceAreaName();
							dAreaName5.groupI = current_group + "";
							dAreaName5.groupJ = current_item + "";
							dAreaName5.groupIJ = 0 + "";
							dAreaName5.groupName = "";
							DataManager.upDefenceAreaName(mContext, dAreaName5);

							break;
						case 5:
							TextView reName6 = (TextView) defence_area_content6
									.findViewWithTag(
											current_group + "" + current_item)
									.findViewById(R.id.one1_rename_btn);
							DefenceObject defenceObject6 = (DefenceObject) reName6
									.getTag();
							defenceObject6.isVisible = true;
							reName6.setVisibility(View.VISIBLE);

							DefenceAreaName dAreaName6 = new DefenceAreaName();
							dAreaName6.groupI = current_group + "";
							dAreaName6.groupJ = current_item + "";
							dAreaName6.groupIJ = 0 + "";
							dAreaName6.groupName = "";
							DataManager.upDefenceAreaName(mContext, dAreaName6);

							break;
						case 6:
							TextView reName7 = (TextView) defence_area_content7
									.findViewWithTag(
											current_group + "" + current_item)
									.findViewById(R.id.one1_rename_btn);
							DefenceObject defenceObject7 = (DefenceObject) reName7
									.getTag();
							defenceObject7.isVisible = true;
							reName7.setVisibility(View.VISIBLE);

							DefenceAreaName dAreaName7 = new DefenceAreaName();
							dAreaName7.groupI = current_group + "";
							dAreaName7.groupJ = current_item + "";
							dAreaName7.groupIJ = 0 + "";
							dAreaName7.groupName = "";
							DataManager.upDefenceAreaName(mContext, dAreaName7);

							break;
						case 7:
							TextView reName8 = (TextView) defence_area_content8
									.findViewWithTag(
											current_group + "" + current_item)
									.findViewById(R.id.one1_rename_btn);
							DefenceObject defenceObject8 = (DefenceObject) reName8
									.getTag();
							defenceObject8.isVisible = true;
							reName8.setVisibility(View.VISIBLE);

							DefenceAreaName dAreaName8 = new DefenceAreaName();
							dAreaName8.groupI = current_group + "";
							dAreaName8.groupJ = current_item + "";
							dAreaName8.groupIJ = 0 + "";
							dAreaName8.groupName = "";
							DataManager.upDefenceAreaName(mContext, dAreaName8);

							break;
						case 8:
							TextView reName9 = (TextView) defence_area_content9
									.findViewWithTag(
											current_group + "" + current_item)
									.findViewById(R.id.one1_rename_btn);
							DefenceObject defenceObject9 = (DefenceObject) reName9
									.getTag();
							defenceObject9.isVisible = true;
							reName9.setVisibility(View.VISIBLE);

							DefenceAreaName dAreaName9 = new DefenceAreaName();
							dAreaName9.groupI = current_group + "";
							dAreaName9.groupJ = current_item + "";
							dAreaName9.groupIJ = 0 + "";
							dAreaName9.groupName = "";
							DataManager.upDefenceAreaName(mContext, dAreaName9);
							break;
						}
						T.showShort(mContext, R.string.learning_success);
					}

				} else if (result == 30) {
					// grayButton(current_group, current_item, "");
				
					bl = true;
					clearDivWithTag();
					DefenceAreaName dan = new DefenceAreaName();
					dan.groupI = current_group + "";
					dan.groupJ = current_item + "";
					dan.groupIJ = "1";
					dan.groupName = "";
					DataManager.upDefenceAreaName(mContext, dan);

					T.showShort(mContext, R.string.clear_success);
				} else if (result == 32) {
					int group = intent.getIntExtra("group", -1);
					int item = intent.getIntExtra("item", -1);
					Log.e("my", "group:" + group + " item:" + item);
					T.showShort(
							mContext,
							Utils.getDefenceAreaByGroup(mContext, group)
									+ ":"
									+ (item + 1)
									+ " "
									+ mContext.getResources().getString(
											R.string.channel)
									+ " "
									+ mContext.getResources().getString(
											R.string.has_been_learning));
				} else if (result == 41) {
					Intent back = new Intent();
					back.setAction(Constants.Action.REPLACE_MAIN_CONTROL);
					mContext.sendBroadcast(back);
					T.showShort(mContext,
							R.string.device_unsupport_defence_area);

				} else {
					T.showShort(mContext, R.string.operator_error);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_CLEAR_DEFENCE_AREA)) {
				if (null != dialog_loading) {
					dialog_loading.dismiss();
					dialog_loading = null;
				}
				int result = intent.getIntExtra("result", -1);
				if (result == 0) {

					
					int i = intent.getIntExtra("defenceAreaNum", -1);
					if (i != -1) {
						switch (i) {
						case 0:
							// TextView tv = (TextView) defence_area_content1
							// .findViewWithTag(
							// current_group + "" + current_item)
							// .findViewById(R.id.one1_rename_btn);

							for (int j = 0; j < defence_area_content1
									.getChildCount(); j++) {
								RelativeLayout rl = (RelativeLayout) defence_area_content1
										.getChildAt(j);
								DefenceObject defenceObject = (DefenceObject) (((TextView) rl
										.findViewById(R.id.one1_rename_btn))
										.getTag());
								if (defenceObject.isVisible == false) {
									bl = true;
									break;
								}

							}
							for (int j2 = 0; j2 < 8; j2++) {
								DefenceAreaName dan = new DefenceAreaName();
								dan.groupI = 0 + "";
								dan.groupJ = j2 + "";
								dan.groupIJ = "1";
								dan.groupName = "";
								DataManager.upDefenceAreaName(mContext, dan);
							}
							defence_area_content1.removeAllViews();
							break;
						case 1:
							for (int j = 0; j < defence_area_content2
									.getChildCount(); j++) {
								RelativeLayout rl2 = (RelativeLayout) defence_area_content2
										.getChildAt(j);
								DefenceObject defenceObject = (DefenceObject) (((TextView) rl2
										.findViewById(R.id.one1_rename_btn))
										.getTag());
								if (defenceObject.isVisible == false) {
									bl = true;
									break;
								}

							}
							for (int j2 = 0; j2 < 8; j2++) {

								DefenceAreaName dan = new DefenceAreaName();
								dan.groupI = 1 + "";
								dan.groupJ = j2 + "";
								dan.groupIJ = "1";
								dan.groupName = "";
								DataManager.upDefenceAreaName(mContext, dan);
							}
							defence_area_content2.removeAllViews();
							break;
						case 2:
							for (int j = 0; j < defence_area_content3
									.getChildCount(); j++) {
								RelativeLayout rl3 = (RelativeLayout) defence_area_content3
										.getChildAt(j);
								DefenceObject defenceObject = (DefenceObject) (((TextView) rl3
										.findViewById(R.id.one1_rename_btn))
										.getTag());
								if (defenceObject.isVisible == false) {
									bl = true;
									break;
								}

							}
							for (int j2 = 0; j2 < 8; j2++) {
								DefenceAreaName dan = new DefenceAreaName();
								dan.groupI = 2 + "";
								dan.groupJ = j2 + "";
								dan.groupIJ = "1";
								dan.groupName = "";
								DataManager.upDefenceAreaName(mContext, dan);
							}
							defence_area_content3.removeAllViews();
							break;
						case 3:
							for (int j = 0; j < defence_area_content4
									.getChildCount(); j++) {
								RelativeLayout rl4 = (RelativeLayout) defence_area_content4
										.getChildAt(j);
								DefenceObject defenceObject = (DefenceObject) (((TextView) rl4
										.findViewById(R.id.one1_rename_btn))
										.getTag());
								if (defenceObject.isVisible == false) {
									bl = true;
									break;
								}

							}
							for (int j2 = 0; j2 < 8; j2++) {
								DefenceAreaName dan = new DefenceAreaName();
								dan.groupI = 3 + "";
								dan.groupJ = j2 + "";
								dan.groupIJ = "1";
								dan.groupName = "";
								DataManager.upDefenceAreaName(mContext, dan);
							}
							defence_area_content4.removeAllViews();
							break;
						case 4:
							for (int j = 0; j < defence_area_content5
									.getChildCount(); j++) {
								RelativeLayout rl5 = (RelativeLayout) defence_area_content5
										.getChildAt(j);
								DefenceObject defenceObject = (DefenceObject) (((TextView) rl5
										.findViewById(R.id.one1_rename_btn))
										.getTag());
								if (defenceObject.isVisible == false) {
									bl = true;
									break;
								}

							}
							for (int j2 = 0; j2 < 8; j2++) {
								DefenceAreaName dan = new DefenceAreaName();
								dan.groupI = 4 + "";
								dan.groupJ = j2 + "";
								dan.groupIJ = "1";
								dan.groupName = "";
								DataManager.upDefenceAreaName(mContext, dan);
							}
							defence_area_content5.removeAllViews();
							break;
						case 5:
							for (int j = 0; j < defence_area_content6
									.getChildCount(); j++) {
								RelativeLayout rl6 = (RelativeLayout) defence_area_content6
										.getChildAt(j);
								DefenceObject defenceObject = (DefenceObject) (((TextView) rl6
										.findViewById(R.id.one1_rename_btn))
										.getTag());
								if (defenceObject.isVisible == false) {
									bl = true;
									break;
								}

							}
							for (int j2 = 0; j2 < 8; j2++) {
								DefenceAreaName dan = new DefenceAreaName();
								dan.groupI = 5 + "";
								dan.groupJ = j2 + "";
								dan.groupIJ = "1";
								dan.groupName = "";
								DataManager.upDefenceAreaName(mContext, dan);
							}
							defence_area_content6.removeAllViews();
							break;
						case 6:
							for (int j = 0; j < defence_area_content7
									.getChildCount(); j++) {
								RelativeLayout rl7 = (RelativeLayout) defence_area_content7
										.getChildAt(j);
								DefenceObject defenceObject = (DefenceObject) (((TextView) rl7
										.findViewById(R.id.one1_rename_btn))
										.getTag());
								if (defenceObject.isVisible == false) {
									bl = true;
									break;
								}

							}
							for (int j2 = 0; j2 < 8; j2++) {
								DefenceAreaName dan = new DefenceAreaName();
								dan.groupI = 6 + "";
								dan.groupJ = j2 + "";
								dan.groupIJ = "1";
								dan.groupName = "";
								DataManager.upDefenceAreaName(mContext, dan);
							}
							defence_area_content7.removeAllViews();
							break;
						case 7:
							for (int j = 0; j < defence_area_content8
									.getChildCount(); j++) {
								RelativeLayout rl8 = (RelativeLayout) defence_area_content8
										.getChildAt(j);
								DefenceObject defenceObject = (DefenceObject) (((TextView) rl8
										.findViewById(R.id.one1_rename_btn))
										.getTag());
								if (defenceObject.isVisible == false) {
									bl = true;
									break;
								}

							}
							for (int j2 = 0; j2 < 8; j2++) {
								DefenceAreaName dan = new DefenceAreaName();
								dan.groupI = 7 + "";
								dan.groupJ = j2 + "";
								dan.groupIJ = "1";
								dan.groupName = "";
								DataManager.upDefenceAreaName(mContext, dan);
							}
							defence_area_content8.removeAllViews();
							break;
						case 8:
							for (int j = 0; j < defence_area_content9
									.getChildCount(); j++) {
								RelativeLayout rl9 = (RelativeLayout) defence_area_content9
										.getChildAt(j);
								DefenceObject defenceObject = (DefenceObject) (((TextView) rl9
										.findViewById(R.id.one1_rename_btn))
										.getTag());
								if (defenceObject.isVisible == false) {
									bl = true;
									break;
								}

							}
							for (int j2 = 0; j2 < 8; j2++) {
								DefenceAreaName dan = new DefenceAreaName();
								dan.groupI = 8 + "";
								dan.groupJ = j2 + "";
								dan.groupIJ = "1";
								dan.groupName = "";
								DataManager.upDefenceAreaName(mContext, dan);
							}
							defence_area_content9.removeAllViews();
							break;

						}
						SettingListener.defenceAreaNum = -1;
					}
					T.showShort(mContext, R.string.clear_success);
				} else {
					T.showShort(mContext, R.string.operator_error);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.RET_DEVICE_NOT_SUPPORT)) {
				Log.e("343", intent.getAction());
				if (null != dialog_loading) {
					dialog_loading.dismiss();
					dialog_loading = null;
				}
				T.showShort(mContext, R.string.not_support);
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_GET_DEFENCE_AREA)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:get defence area");
					P2PHandler.getInstance().getDefenceArea(contact.contactId,
							contact.contactPassword);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_SET_DEFENCE_AREA)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:set defence area");
					P2PHandler.getInstance().setDefenceAreaState(
							contact.contactId, contact.contactPassword,
							current_group, current_item, current_type);
				}
			} else if (intent.getAction().equals(
					Constants.P2P.ACK_RET_CLEAR_DEFENCE_AREA)) {
				int result = intent.getIntExtra("result", -1);
				if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
					Intent i = new Intent();
					i.setAction(Constants.Action.CONTROL_SETTING_PWD_ERROR);
					mContext.sendBroadcast(i);
				} else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
					Log.e("my", "net error resend:clear defence area");
					P2PHandler.getInstance().clearDefenceAreaState(
							contact.contactId, contact.contactPassword,
							current_group);
				}
			}
		}

		public void clearDivWithTag() {
			Log.e("343", "current_group=" + current_group + "  current_item="
					+ current_item);
			switch (current_group) {
			case 0:
				defence_area_content1.removeView(defence_area_content1
						.findViewWithTag(current_group + "" + current_item));
				break;
			case 1:
				defence_area_content2.removeView(defence_area_content2
						.findViewWithTag(current_group + "" + current_item));
				break;
			case 2:
				defence_area_content3.removeView(defence_area_content3
						.findViewWithTag(current_group + "" + current_item));
				break;
			case 3:
				defence_area_content4.removeView(defence_area_content4
						.findViewWithTag(current_group + "" + current_item));
				break;
			case 4:
				defence_area_content5.removeView(defence_area_content5
						.findViewWithTag(current_group + "" + current_item));
				break;
			case 5:
				defence_area_content6.removeView(defence_area_content6
						.findViewWithTag(current_group + "" + current_item));
				break;
			case 6:
				defence_area_content7.removeView(defence_area_content7
						.findViewWithTag(current_group + "" + current_item));
				break;
			case 7:
				defence_area_content8.removeView(defence_area_content8
						.findViewWithTag(current_group + "" + current_item));
				break;
			case 8:
				defence_area_content9.removeView(defence_area_content9
						.findViewWithTag(current_group + "" + current_item));
				break;
			}
		}
	};


	public void initData(ArrayList<int[]> data) {
		List<DefenceAreaName> lists = DataManager
				.findDefenceAreaNameAll(getActivity());
		HashMap<String, String> danMap = new HashMap<String, String>();
		for (int i = 0; i < lists.size(); i++) {
			DefenceAreaName dan = lists.get(i);
			
			danMap.put(dan.groupI + "" + dan.groupJ, dan.groupName);
		}
		for (int i = 0; i < data.size(); i++) {
			int[] status = data.get(i);
			for (int j = 0; j < status.length; j++) {
				String strIJ = i + "" + j;
				int statuString = status[j];
				String strName = danMap.get(strIJ);
				
				if (strName == null) {
					DefenceAreaName dan = new DefenceAreaName();
					dan.groupI = i + "";
					dan.groupJ = j + "";
					dan.groupIJ = statuString + "";
					dan.groupName = "";
					DataManager.insertDefenceAreaName(mContext, dan);
					if (statuString != 1) {
						lightButton(i, j, "").setVisibility(View.VISIBLE);
					}
				} else {
					DefenceAreaName dan = new DefenceAreaName();
					dan.groupI = i + "";
					dan.groupJ = j + "";
					dan.groupIJ = statuString + "";
					dan.groupName = strName;
					DataManager.upDefenceAreaName(mContext, dan);
					if (statuString != 1) {
						lightButton(i, j, strName).setVisibility(View.VISIBLE);
					}
				}

			}
		}
	}

	
	public TextView lightButton(final int i, final int j, String groupName) {
		RelativeLayout rl = (RelativeLayout) lif.inflate(
				R.layout.sharecf_defencearea_rl, null);
		rl.setTag(i + "" + j);
		TextView item_num = (TextView) rl.findViewById(R.id.one1_num);
		
		item_num.setText(j + 1 + ".");
		TextView item = (TextView) rl.findViewById(R.id.one1);
		TextView item_code = (TextView) rl.findViewById(R.id.one1_code_btn);
		TextView item_rename = (TextView) rl.findViewById(R.id.one1_rename_btn);
		String[] list = new String[] { i + "", j + "" };
		DefenceObject defenceObject = new DefenceObject();
		defenceObject.tv = item;
		defenceObject.strings = list;
		defenceObject.isVisible = false;

		item_rename.setTag(defenceObject);
		item_rename.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DefenceObject defenceObject = (DefenceObject) (v.getTag());
				TextView tv = defenceObject.tv;
				String name_str = tv.getText().toString();

				NormalDialog nd;
				if ("".equals(name_str) || null == name_str) {
					nd = new NormalDialog(
							getActivity(),
							"",
							"",
							Utils.getResString(mContext, R.string.defence_tip3),
							Utils.getResString(mContext, R.string.defence_tip4),
							Utils.getResString(mContext, R.string.defence_tip5));
				} else {

					nd = new NormalDialog(
							getActivity(),
							name_str,
							"",
							Utils.getResString(mContext, R.string.defence_tip3),
							Utils.getResString(mContext, R.string.defence_tip4),
							Utils.getResString(mContext, R.string.defence_tip5));
				}

				nd.showReNameDialog(v);
			}
		});

		item_code.setTag(list);
		item_code.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				study(i, j);
			}
		});
		if ("".equals(groupName)) {
			
		} else {
			item.setText(groupName);
		}
		// String[] list = new String[] { i + "", j + "" };
		// item.setTag(list);
		// if (null != item) {
		// item.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// study(i, j);
		// }
		//
		// });
		// item.setOnLongClickListener(new OnLongClickListener() {
		//
		// @Override
		// public boolean onLongClick(View v) {
		// // TODO Auto-generated method stub
		// TextView tv = (TextView) v;
		// String name_str = tv.getText().toString();
		// NormalDialog nd;
		// if ("".equals(name_str) || null == name_str) {
		// nd = new NormalDialog(getActivity(), "", "",
		// "对当前选择防区重新命名", "确定", "取消");
		// } else {
		// nd = new NormalDialog(getActivity(), name_str, "",
		// "对当前选择防区重新命名", "确定", "取消");
		// }
		// nd.showReNameDialog(v);
		// return true;
		// }
		// });
		// }
		rl.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				clear(i, j);
				return true;
			}
		});
		switch (i) {
		case 0:
			defence_area_content1.addView(rl);
			break;
		case 1:
			defence_area_content2.addView(rl);
			break;
		case 2:
			defence_area_content3.addView(rl);
			break;
		case 3:
			defence_area_content4.addView(rl);
			break;
		case 4:
			defence_area_content5.addView(rl);
			break;
		case 5:
			defence_area_content6.addView(rl);
			break;
		case 6:
			defence_area_content7.addView(rl);
			break;
		case 7:
			defence_area_content8.addView(rl);
			break;
		case 8:
			defence_area_content9.addView(rl);
			break;

		}
		return item_rename;
	}


	public void grayButton(final int i, final int j, String groupName) {
		RelativeLayout rl = (RelativeLayout) lif.inflate(
				R.layout.sharecf_defencearea_rl, null);
		rl.setTag(i + "" + j);
		TextView item = (TextView) rl.findViewById(R.id.one1);
		TextView item_code = (TextView) rl.findViewById(R.id.one1_code_btn);
		// TextView item_rename = (TextView)
		// rl.findViewById(R.id.one1_rename_btn);
		if ("".equals(groupName)) {
			
		} else {
			item.setText(groupName);
		}
		String[] list = new String[] { i + "", j + "" };
		item_code.setTag(list);
		item_code.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				study(i, j);
			}
		});

		rl.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				clear(i, j);
				return true;
			}
		});
	
		switch (i) {
		case 0:
			defence_area_content1.addView(rl);
			break;
		case 1:
			defence_area_content2.addView(rl);
			break;
		case 2:
			defence_area_content3.addView(rl);
			break;
		case 3:
			defence_area_content4.addView(rl);
			break;
		case 4:
			defence_area_content5.addView(rl);
			break;
		case 5:
			defence_area_content6.addView(rl);
			break;
		case 6:
			defence_area_content7.addView(rl);
			break;
		case 7:
			defence_area_content8.addView(rl);
			break;
		case 8:
			defence_area_content9.addView(rl);
			break;

		}
	}

	public Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
			case EXPAND_OR_SHRINK:
				int group1 = msg.arg1;
				int length = msg.arg2;
				LinearLayout item = getContent(group1);
				LinearLayout.LayoutParams params = (LayoutParams) item
						.getLayoutParams();
				params.height = length;
				item.setLayoutParams(params);
				break;
			case END_EXPAND_OR_SHRINK:
				int group2 = msg.arg1;
				// if (group2 == 8) {
				// RelativeLayout bar = getBar(group2);
				// bar.setBackgroundResource(R.drawable.tiao_bg_bottom);
				// }
				switch (group2) {
				case 0:
					fenge1.setVisibility(View.GONE);
					break;
				case 1:
					fenge2.setVisibility(View.GONE);
					break;
				case 2:
					fenge3.setVisibility(View.GONE);
					break;
				case 3:
					fenge4.setVisibility(View.GONE);
					break;
				case 4:
					fenge5.setVisibility(View.GONE);
					break;
				case 5:
					fenge6.setVisibility(View.GONE);
					break;
				case 6:
					fenge7.setVisibility(View.GONE);
					break;
				case 7:
					fenge8.setVisibility(View.GONE);
					break;
				case 8:
					fenge9.setVisibility(View.GONE);
					break;
				}
				break;
			case TEST1:
				int group3 = msg.arg1;
				int length3 = msg.arg2;
				LinearLayout item3 = getContent(group3);

				LinearLayout.LayoutParams params3 = (LayoutParams) item3
						.getLayoutParams();

				params3.height = (item3.getHeight() - length3);
				item3.setLayoutParams(params3);
				break;
			}

			return false;
		}
	});

	public void shrinkItem(final int i) {
		switch (i) {
		case 0:
			defence_area_content1.setVisibility(View.GONE);
			arrow1.setImageResource(R.drawable.alarm_area_arrow_right);
			imgbtn1.setVisibility(View.GONE);
			fenge1.setVisibility(View.GONE);
			break;
		case 1:
			defence_area_content2.setVisibility(View.GONE);
			arrow2.setImageResource(R.drawable.alarm_area_arrow_right);
			imgbtn2.setVisibility(View.GONE);
			fenge2.setVisibility(View.GONE);
			break;
		case 2:
			defence_area_content3.setVisibility(View.GONE);
			arrow3.setImageResource(R.drawable.alarm_area_arrow_right);
			imgbtn3.setVisibility(View.GONE);
			fenge3.setVisibility(View.GONE);
			break;
		case 3:
			defence_area_content4.setVisibility(View.GONE);
			arrow4.setImageResource(R.drawable.alarm_area_arrow_right);
			imgbtn4.setVisibility(View.GONE);
			fenge4.setVisibility(View.GONE);
			break;
		case 4:
			defence_area_content5.setVisibility(View.GONE);
			arrow5.setImageResource(R.drawable.alarm_area_arrow_right);
			imgbtn5.setVisibility(View.GONE);
			fenge5.setVisibility(View.GONE);
			break;
		case 5:
			defence_area_content6.setVisibility(View.GONE);
			arrow6.setImageResource(R.drawable.alarm_area_arrow_right);
			imgbtn6.setVisibility(View.GONE);
			fenge6.setVisibility(View.GONE);
			break;
		case 6:
			defence_area_content7.setVisibility(View.GONE);
			arrow7.setImageResource(R.drawable.alarm_area_arrow_right);
			imgbtn7.setVisibility(View.GONE);
			fenge7.setVisibility(View.GONE);
			break;
		case 7:
			defence_area_content8.setVisibility(View.GONE);
			arrow8.setImageResource(R.drawable.alarm_area_arrow_right);
			imgbtn8.setVisibility(View.GONE);
			fenge8.setVisibility(View.GONE);
			break;
		case 8:
			defence_area_content9.setVisibility(View.GONE);
			arrow9.setImageResource(R.drawable.alarm_area_arrow_right);
			imgbtn9.setVisibility(View.GONE);
			fenge9.setVisibility(View.GONE);
			break;
		}

		if (this.getIsActive(i)) {
			return;
		} else {
			setActive(i, true);
		}
		new Thread() {
			@Override
			public void run() {
				// int length = (int) mContext.getResources().getDimension(
				// R.dimen.defen_area_expand_view_height);
				// int length = defence_area_content1.getHeight();
				//
				// while (length > 0) {
				// length = length - 10;
				// Message msg = new Message();
				// msg.what = EXPAND_OR_SHRINK;
				// msg.arg1 = i;
				// msg.arg2 = length;
				// mHandler.sendMessage(msg);
				// Utils.sleepThread(20);
				// }
				//
				// Message end = new Message();
				// end.what = END_EXPAND_OR_SHRINK;
				// end.arg1 = i;
				// mHandler.sendMessage(end);
				setActive(i, false);
				RelativeLayout item = getBar(i);
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						expandItem(i);
					}

				});

			}
		}.start();
	}

	public void expandItem(final int i) {
		
		switch (i) {
		case 0:
			fenge1.setVisibility(View.VISIBLE);
			arrow1.setImageResource(R.drawable.alarm_area_arrow_bottom);
			defence_area_content1.setVisibility(View.VISIBLE);
			imgbtn1.setVisibility(View.VISIBLE);
			break;
		case 1:
			fenge2.setVisibility(View.VISIBLE);
			arrow2.setImageResource(R.drawable.alarm_area_arrow_bottom);
			defence_area_content2.setVisibility(View.VISIBLE);
			imgbtn2.setVisibility(View.VISIBLE);
			break;
		case 2:
			fenge3.setVisibility(View.VISIBLE);
			arrow3.setImageResource(R.drawable.alarm_area_arrow_bottom);
			defence_area_content3.setVisibility(View.VISIBLE);
			imgbtn3.setVisibility(View.VISIBLE);
			break;
		case 3:
			fenge4.setVisibility(View.VISIBLE);
			arrow4.setImageResource(R.drawable.alarm_area_arrow_bottom);
			defence_area_content4.setVisibility(View.VISIBLE);
			imgbtn4.setVisibility(View.VISIBLE);
			break;
		case 4:
			fenge5.setVisibility(View.VISIBLE);
			arrow5.setImageResource(R.drawable.alarm_area_arrow_bottom);
			defence_area_content5.setVisibility(View.VISIBLE);
			imgbtn5.setVisibility(View.VISIBLE);
			break;
		case 5:
			fenge6.setVisibility(View.VISIBLE);
			arrow6.setImageResource(R.drawable.alarm_area_arrow_bottom);
			defence_area_content6.setVisibility(View.VISIBLE);
			imgbtn6.setVisibility(View.VISIBLE);
			break;
		case 6:
			fenge7.setVisibility(View.VISIBLE);
			arrow7.setImageResource(R.drawable.alarm_area_arrow_bottom);
			defence_area_content7.setVisibility(View.VISIBLE);
			imgbtn7.setVisibility(View.VISIBLE);
			break;
		case 7:
			fenge8.setVisibility(View.VISIBLE);
			arrow8.setImageResource(R.drawable.alarm_area_arrow_bottom);
			defence_area_content8.setVisibility(View.VISIBLE);
			imgbtn8.setVisibility(View.VISIBLE);
			break;
		case 8:
			fenge9.setVisibility(View.VISIBLE);
			arrow9.setImageResource(R.drawable.alarm_area_arrow_bottom);
			defence_area_content9.setVisibility(View.VISIBLE);
			imgbtn9.setVisibility(View.VISIBLE);
			break;
		}

		if (this.getIsActive(i)) {
			return;
		} else {
			this.setActive(i, true);
		}

		final RelativeLayout item = getBar(i);
		// if(i==8){
		// item.setBackgroundResource(R.drawable.tiao_bg_center);
		// }

		new Thread() {
			@Override
			public void run() {
				// int length = 0;

				// int total = (int) mContext.getResources().getDimension(
				// R.dimen.defen_area_expand_view_height);
				// int total = 0;
				// final int[] totals = new int[defence_area_content1
				// .getChildCount()];
				// Log.e("343",
				// "展开项的子项个数=" + defence_area_content1.getChildCount());
				// for (int j = 0; j < defence_area_content1.getChildCount();
				// j++) {
				// View view = defence_area_content1.getChildAt(j);
				// int w = View.MeasureSpec.makeMeasureSpec(0,
				// View.MeasureSpec.UNSPECIFIED);
				// int h = View.MeasureSpec.makeMeasureSpec(0,
				// View.MeasureSpec.UNSPECIFIED);
				// view.measure(w, h);
				// int height = view.getMeasuredHeight();
				// int width = view.getMeasuredWidth();
				// totals[j] = height;
				// Log.e("343", j + "=" + totals[j]);
				// }
				// for (int k = 0; k < totals.length; k++) {
				// total += totals[k];
				// }
				//
				// while (length < total) {
				// length = length + 10;
				// Message msg = new Message();
				// msg.what = EXPAND_OR_SHRINK;
				// msg.arg1 = i;
				// msg.arg2 = length;
				// mHandler.sendMessage(msg);
				// Utils.sleepThread(20);
				// }
				setActive(i, false);
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						shrinkItem(i);
					}

				});

			}
		}.start();
	}

	public void showDefence_area1() {
		progressBar_defence_area1.setVisibility(View.GONE);
		progressBar_defence_area2.setVisibility(View.GONE);
		progressBar_defence_area3.setVisibility(View.GONE);
		progressBar_defence_area4.setVisibility(View.GONE);
		progressBar_defence_area5.setVisibility(View.GONE);
		progressBar_defence_area6.setVisibility(View.GONE);
		progressBar_defence_area7.setVisibility(View.GONE);
		progressBar_defence_area8.setVisibility(View.GONE);
		progressBar_defence_area9.setVisibility(View.GONE);
		arrow1.setVisibility(View.VISIBLE);
		arrow2.setVisibility(View.VISIBLE);
		arrow3.setVisibility(View.VISIBLE);
		arrow4.setVisibility(View.VISIBLE);
		arrow5.setVisibility(View.VISIBLE);
		arrow6.setVisibility(View.VISIBLE);
		arrow7.setVisibility(View.VISIBLE);
		arrow8.setVisibility(View.VISIBLE);
		arrow9.setVisibility(View.VISIBLE);

		for (int i = 0; i < 9; i++) {
			RelativeLayout item = this.getBar(i);
			final int group = i;

			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					expandItem(group);
				}
			});
			item.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub

					NormalDialog dialog = new NormalDialog(mContext, mContext
							.getResources().getString(R.string.clear_code),
							mContext.getResources().getString(
									R.string.clear_code_prompt), mContext
									.getResources().getString(R.string.ensure),
							mContext.getResources().getString(R.string.cancel));

					dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

						@Override
						public void onClick() {
							// TODO Auto-generated method stub
							SettingListener.defenceAreaNum = group;
							if (null == dialog_loading) {
								dialog_loading = new NormalDialog(mContext,
										mContext.getResources().getString(
												R.string.clearing), "", "", "");
								dialog_loading
										.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
							}
							dialog_loading.showDialog();
							current_group = group;
							current_type = Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR_GROUP;
							P2PHandler.getInstance().clearDefenceAreaState(
									contact.contactId, contact.contactPassword,
									group);
						}
					});

					dialog.showNormalDialog();
					dialog.setCanceledOnTouchOutside(false);

					return false;
				}

			});
		}
	}


	public boolean bl = true;

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.add_btn1){
			testA(defence_area_content1,"0");
		}else if(id==R.id.add_btn2){
			testA(defence_area_content2,"1");
		}else if(id==R.id.add_btn3){
			testA(defence_area_content3,"2");
		}else if(id==R.id.add_btn4){
			testA(defence_area_content4,"3");
		}else if(id==R.id.add_btn5){
			testA(defence_area_content5,"4");
		}else if(id==R.id.add_btn6){
			testA(defence_area_content6,"5");
		}else if(id==R.id.add_btn7){
			testA(defence_area_content7,"6");
		}else if(id==R.id.add_btn8){
			testA(defence_area_content8,"7");
		}else if(id==R.id.add_btn9){
			testA(defence_area_content9,"8");
		}
		
		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.add_btn1:
////			LinearLayout ll1 = defence_area_content1;
////			String str1 = getDefenceAreaContentCount(ll1);
////			Log.e("343", str1 + "===");
////			if (bl) {
////				if ("1".equals(str1)) {
////					creatLightButton("0");
////				} else {
////					T.showLong(mContext,
////							Utils.getResString(mContext, R.string.defence_tip1));
////				}
////			} else {
////				T.showLong(mContext,
////						Utils.getResString(mContext, R.string.defence_tip2));
////			}
////
////			bl = false;
//			testA(defence_area_content1,"0");
//			break;
//		case R.id.add_btn2:
////			LinearLayout ll2 = defence_area_content2;
////			String str2 = getDefenceAreaContentCount(ll2);
////			Log.e("343", "str2=" + str2);
////			if ("0".equals(str2)) {
////				T.showLong(mContext,
////						Utils.getResString(mContext, R.string.defence_tip1));
////			} else if ("1".equals(str2)) {
////				if (bl) {
////					creatLightButton("1");
////				} else {
////					if (bl) {
////						T.showLong(mContext, Utils.getResString(mContext,
////								R.string.defence_tip1));
////					} else {
////						T.showLong(mContext, Utils.getResString(mContext,
////								R.string.defence_tip2));
////					}
////				}
////			}
////			bl = false;
//			testA(defence_area_content2,"1");
//			break;
//		case R.id.add_btn3:
////			LinearLayout ll3 = defence_area_content3;
////			String str3 = getDefenceAreaContentCount(ll3);
////			if (bl) {
////				if ("1".equals(str3)) {
////					creatLightButton("2");
////				} else {
////					T.showLong(mContext,
////							Utils.getResString(mContext, R.string.defence_tip1));
////
////				}
////			} else {
////				T.showLong(mContext,
////						Utils.getResString(mContext, R.string.defence_tip2));
////			}
////
////			bl = false;
//			testA(defence_area_content3,"2");
//			break;
//		case R.id.add_btn4:
////			LinearLayout ll4 = defence_area_content4;
////			String str4 = getDefenceAreaContentCount(ll4);
////			if (bl) {
////				if ("1".equals(str4)) {
////					creatLightButton("3");
////				} else {
////					T.showLong(mContext,
////							Utils.getResString(mContext, R.string.defence_tip1));
////
////				}
////			} else {
////				T.showLong(mContext,
////						Utils.getResString(mContext, R.string.defence_tip2));
////			}
////			bl = false;
//			testA(defence_area_content4,"3");
//
//			break;
//		case R.id.add_btn5:
////			LinearLayout ll5 = defence_area_content5;
////			String str5 = getDefenceAreaContentCount(ll5);
////			if (bl) {
////				if ("1".equals(str5)) {
////					creatLightButton("4");
////				} else {
////					T.showLong(mContext,
////							Utils.getResString(mContext, R.string.defence_tip1));
////
////				}
////			} else {
////				T.showLong(mContext,
////						Utils.getResString(mContext, R.string.defence_tip2));
////			}
////			bl = false;
//			testA(defence_area_content5,"4");
//			break;
//		case R.id.add_btn6:
////			LinearLayout ll6 = defence_area_content6;
////			String str6 = getDefenceAreaContentCount(ll6);
////			if (bl) {
////				if ("1".equals(str6)) {
////					creatLightButton("5");
////				} else {
////					T.showLong(mContext,
////							Utils.getResString(mContext, R.string.defence_tip1));
////
////				}
////			} else {
////				T.showLong(mContext,
////						Utils.getResString(mContext, R.string.defence_tip2));
////			}
////			bl = false;
//			testA(defence_area_content6,"5");
//			break;
//		case R.id.add_btn7:
////			LinearLayout ll7 = defence_area_content7;
////			String str7 = getDefenceAreaContentCount(ll7);
////			if (bl) {
////
////				if ("1".equals(str7)) {
////					creatLightButton("6");
////				} else {
////					T.showLong(mContext,
////							Utils.getResString(mContext, R.string.defence_tip1));
////
////				}
////			} else {
////				T.showLong(mContext,
////						Utils.getResString(mContext, R.string.defence_tip2));
////			}
////			bl = false;
//			testA(defence_area_content7,"6");
//
//			break;
//		case R.id.add_btn8:
////			LinearLayout ll8 = defence_area_content8;
////			String str8 = getDefenceAreaContentCount(ll8);
////			if (bl) {
////				if ("1".equals(str8)) {
////					creatLightButton("7");
////				} else {
////					T.showLong(mContext,
////							Utils.getResString(mContext, R.string.defence_tip1));
////
////				}
////			} else {
////				T.showLong(mContext,
////						Utils.getResString(mContext, R.string.defence_tip2));
////			}
////			bl = false;
//			testA(defence_area_content8,"7");
//			break;
//		case R.id.add_btn9:
////			LinearLayout ll9 = defence_area_content9;
////			String str9 = getDefenceAreaContentCount(ll9);
////			if (bl) {
////				if ("1".equals(str9)) {
////					creatLightButton("8");
////				} else {
////					T.showLong(mContext,
////							Utils.getResString(mContext, R.string.defence_tip1));
////
////				}
////			} else {
////				T.showLong(mContext,
////						Utils.getResString(mContext, R.string.defence_tip2));
////			}
////			bl = false;
//			testA(defence_area_content9,"8");
//			break;
//		}
	}

	public void testA(LinearLayout ll,String Group){
		LinearLayout contantll = ll;
		String isCrate = getDefenceAreaContentCount(contantll);
		
		
		int contant = contantll.getChildCount();
		if (contant!=0) {
			TextView tv;
			boolean bll = false;
			for (int i = 0; i < contant; i++) {
				tv = (TextView) contantll.getChildAt(i).findViewById(R.id.one1_rename_btn);
				if (tv.getVisibility()!=View.VISIBLE) {
					bll = true;
					break;
				}				
			}
			if (bll) {
				T.showLong(mContext, Utils.getResString(mContext,
						R.string.defence_tip2));
				return;
			}
		}
		
		
		
//		TextView reName8 = (TextView) defence_area_content8
//				.findViewWithTag(
//						current_group + "" + current_item)
//				.findViewById(R.id.one1_rename_btn);
//		if (reName8.getVisibility()==View.VISIBLE) {
//			T.showLong(mContext, "当前已学习");
//		}else {
//			T.showShort(mContext, "存在未学习的当前的="+current_group+"=="+current_item);
//		}		
		
		if ("0".equals(isCrate)) {
			T.showLong(mContext,
					Utils.getResString(mContext, R.string.defence_tip1));
		} else if ("1".equals(isCrate)) {
//			if (bl) {
				creatLightButton(Group);
//			} 
//			else {
//				if (bl) {
//					T.showLong(mContext, Utils.getResString(mContext,
//							R.string.defence_tip1));
//				} 
//				else {
//					T.showLong(mContext, Utils.getResString(mContext,
//							R.string.defence_tip2));
//				}
//			}
		}
//		bl = false;
	}
	
	
	

	public void creatLightButton(String GroupI) {
		// ArrayList<String> strs1 = new ArrayList<String>();
		// for (int i = 0; i < totals[Integer.parseInt(GroupI)].length; i++) {
		// strs1.add(totals[Integer.parseInt(GroupI)][i]);
		// }
		// List<DefenceAreaName> llall = DataManager
		// .findDefenceAreaNameAll(mContext);
		// for (int i = 0; i < llall.size(); i++) {
		// DefenceAreaName defenceAreaName = llall.get(i);
		// if (GroupI.equals(defenceAreaName.groupI)) {
		//
		// for (int j = 0; j < strs1.size(); j++) {
		// String string = strs1.get(j);
		// Log.e("343", string + "==" + defenceAreaName.groupIJ);
		// Log.e("343", "看看这个东西==" + defenceAreaName.groupIJ);
		// if (string.equals(defenceAreaName.groupIJ)) {
		// Log.e("343", "找到一样的了，进行移除工作");
		// strs1.remove(string);
		// Log.e("343", "移除后的大小==" + strs1.size());
		// }
		// }
		// }
		// ;
		// }
		// for (int i = 0; i < strs1.size(); i++) {
		// Log.e("343", "没有被移除的要赋值的==" + strs1.get(i));
		// }
		// if (strs1.size() > 0) {
		// Log.e("343",
		// "i="
		// + Integer.parseInt(GroupI)
		// + ""
		// + "j="
		// + Integer.parseInt((String) strs1.get(0)
		// .subSequence(1, 2)));
		// }

		lightButton(
				Integer.parseInt(GroupI),
				Integer.parseInt(DataManager.getMinDefenceAreaItem(mContext,
						GroupI)), "").setVisibility(View.GONE);
	}

	public String getDefenceAreaContentCount(LinearLayout ll) {
		String result = "";
		if (ll.getChildCount() < 8) {

			result = "1";

		} else if (ll.getChildCount() == 8) {
			bl = true;
			result = "0";
		}
		return result;
	}


	public void study(final int group, final int item) {

		NormalDialog dialog = new NormalDialog(mContext, mContext
				.getResources().getString(R.string.learing_code), mContext
				.getResources().getString(R.string.learing_code_prompt),
				mContext.getResources().getString(R.string.ensure), mContext
						.getResources().getString(R.string.cancel));

		dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				if (null == dialog_loading) {
					dialog_loading = new NormalDialog(mContext, mContext
							.getResources().getString(R.string.studying), "",
							"", "");
					dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				}
				dialog_loading.showDialog();
				current_type = Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_LEARN;
				current_group = group;
				current_item = item;
				P2PHandler
						.getInstance()
						.setDefenceAreaState(
								contact.contactId,
								contact.contactPassword,
								group,
								item,
								Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_LEARN);
			}
		});

		dialog.showNormalDialog();
		dialog.setCanceledOnTouchOutside(false);
	}


	public void clear(final int group, final int item) {

		NormalDialog dialog = new NormalDialog(mContext, mContext
				.getResources().getString(R.string.clear_code), mContext
				.getResources().getString(R.string.clear_code_prompt), mContext
				.getResources().getString(R.string.ensure), mContext
				.getResources().getString(R.string.cancel));

		dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				if (null == dialog_loading) {
					dialog_loading = new NormalDialog(mContext, mContext
							.getResources().getString(R.string.clearing), "",
							"", "");
					dialog_loading.setStyle(NormalDialog.DIALOG_STYLE_LOADING);
				}
				dialog_loading.showDialog();
				current_type = Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR;
				current_group = group;
				current_item = item;
				P2PHandler
						.getInstance()
						.setDefenceAreaState(
								contact.contactId,
								contact.contactPassword,
								group,
								item,
								Constants.P2P_SET.DEFENCE_AREA_SET.DEFENCE_AREA_TYPE_CLEAR);
			}
		});

		dialog.showNormalDialog();
		dialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (isRegFilter) {
			mContext.unregisterReceiver(mReceiver);
			isRegFilter = false;
		}
	}

	public RelativeLayout getBar(int group) {
		switch (group) {
		case 0:
			return this.change_defence_area1;
		case 1:
			return this.change_defence_area2;
		case 2:
			return this.change_defence_area3;
		case 3:
			return this.change_defence_area4;
		case 4:
			return this.change_defence_area5;
		case 5:
			return this.change_defence_area6;
		case 6:
			return this.change_defence_area7;
		case 7:
			return this.change_defence_area8;
		case 8:
			return this.change_defence_area9;
		}
		return null;
	}

	public LinearLayout getContent(int group) {
		switch (group) {
		case 0:
			return this.defence_area_content1;
		case 1:
			return this.defence_area_content2;
		case 2:
			return this.defence_area_content3;
		case 3:
			return this.defence_area_content4;
		case 4:
			return this.defence_area_content5;
		case 5:
			return this.defence_area_content6;
		case 6:
			return this.defence_area_content7;
		case 7:
			return this.defence_area_content8;
		case 8:
			return this.defence_area_content9;
		}
		return null;
	}

	// public TextView getKeyBoard(int group, int item) {
	// switch (group) {
	// case 0:
	// if (item == 0) {
	//
	// return this.one1;
	// } else if (item == 1) {
	// return this.one2;
	// } else if (item == 2) {
	// return this.one3;
	// } else if (item == 3) {
	// return this.one4;
	// } else if (item == 4) {
	// return this.one5;
	// } else if (item == 5) {
	// return this.one6;
	// } else if (item == 6) {
	// return this.one7;
	// } else if (item == 7) {
	// return this.one8;
	// }
	// break;
	// case 1:
	// if (item == 0) {
	// return this.two1;
	// } else if (item == 1) {
	// return this.two2;
	// } else if (item == 2) {
	// return this.two3;
	// } else if (item == 3) {
	// return this.two4;
	// } else if (item == 4) {
	// return this.two5;
	// } else if (item == 5) {
	// return this.two6;
	// } else if (item == 6) {
	// return this.two7;
	// } else if (item == 7) {
	// return this.two8;
	// }
	// break;
	// case 2:
	// if (item == 0) {
	// return this.three1;
	// } else if (item == 1) {
	// return this.three2;
	// } else if (item == 2) {
	// return this.three3;
	// } else if (item == 3) {
	// return this.three4;
	// } else if (item == 4) {
	// return this.three5;
	// } else if (item == 5) {
	// return this.three6;
	// } else if (item == 6) {
	// return this.three7;
	// } else if (item == 7) {
	// return this.three8;
	// }
	// break;
	// case 3:
	// if (item == 0) {
	// return this.four1;
	// } else if (item == 1) {
	// return this.four2;
	// } else if (item == 2) {
	// return this.four3;
	// } else if (item == 3) {
	// return this.four4;
	// } else if (item == 4) {
	// return this.four5;
	// } else if (item == 5) {
	// return this.four6;
	// } else if (item == 6) {
	// return this.four7;
	// } else if (item == 7) {
	// return this.four8;
	// }
	// break;
	// case 4:
	// if (item == 0) {
	// return this.five1;
	// } else if (item == 1) {
	// return this.five2;
	// } else if (item == 2) {
	// return this.five3;
	// } else if (item == 3) {
	// return this.five4;
	// } else if (item == 4) {
	// return this.five5;
	// } else if (item == 5) {
	// return this.five6;
	// } else if (item == 6) {
	// return this.five7;
	// } else if (item == 7) {
	// return this.five8;
	// }
	// break;
	// case 5:
	// if (item == 0) {
	// return this.six1;
	// } else if (item == 1) {
	// return this.six2;
	// } else if (item == 2) {
	// return this.six3;
	// } else if (item == 3) {
	// return this.six4;
	// } else if (item == 4) {
	// return this.six5;
	// } else if (item == 5) {
	// return this.six6;
	// } else if (item == 6) {
	// return this.six7;
	// } else if (item == 7) {
	// return this.six8;
	// }
	// break;
	// case 6:
	// if (item == 0) {
	// return this.seven1;
	// } else if (item == 1) {
	// return this.seven2;
	// } else if (item == 2) {
	// return this.seven3;
	// } else if (item == 3) {
	// return this.seven4;
	// } else if (item == 4) {
	// return this.seven5;
	// } else if (item == 5) {
	// return this.seven6;
	// } else if (item == 6) {
	// return this.seven7;
	// } else if (item == 7) {
	// return this.seven8;
	// }
	// break;
	// case 7:
	// if (item == 0) {
	// return this.eight1;
	// } else if (item == 1) {
	// return this.eight2;
	// } else if (item == 2) {
	// return this.eight3;
	// } else if (item == 3) {
	// return this.eight4;
	// } else if (item == 4) {
	// return this.eight5;
	// } else if (item == 5) {
	// return this.eight6;
	// } else if (item == 6) {
	// return this.eight7;
	// } else if (item == 7) {
	// return this.eight8;
	// }
	// break;
	// case 8:
	// if (item == 0) {
	// return this.nine1;
	// } else if (item == 1) {
	// return this.nine2;
	// } else if (item == 2) {
	// return this.nine3;
	// } else if (item == 3) {
	// return this.nine4;
	// } else if (item == 4) {
	// return this.nine5;
	// } else if (item == 5) {
	// return this.nine6;
	// } else if (item == 6) {
	// return this.nine7;
	// } else if (item == 7) {
	// return this.nine8;
	// }
	// break;
	// }
	// return null;
	// }

	public boolean getIsActive(int group) {
		switch (group) {
		case 0:
			return this.is_one_active;
		case 1:
			return this.is_two_active;
		case 2:
			return this.is_three_active;
		case 3:
			return this.is_four_active;
		case 4:
			return this.is_five_active;
		case 5:
			return this.is_six_active;
		case 6:
			return this.is_seven_active;
		case 7:
			return this.is_eight_active;
		case 8:
			return this.is_nine_active;
		}
		return true;
	}

	public void setActive(int group, boolean bool) {
		switch (group) {
		case 0:
			this.is_one_active = bool;
			break;
		case 1:
			this.is_two_active = bool;
			break;
		case 2:
			this.is_three_active = bool;
			break;
		case 3:
			this.is_four_active = bool;
			break;
		case 4:
			this.is_five_active = bool;
			break;
		case 5:
			this.is_six_active = bool;
			break;
		case 6:
			this.is_seven_active = bool;
			break;
		case 7:
			this.is_eight_active = bool;
			break;
		case 8:
			this.is_nine_active = bool;
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent it = new Intent();
		it.setAction(Constants.Action.CONTROL_BACK);
		mContext.sendBroadcast(it);
	}
}
