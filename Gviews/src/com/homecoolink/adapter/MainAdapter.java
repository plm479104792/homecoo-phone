package com.homecoolink.adapter;

import java.io.File;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.homecoolink.CallActivity;
import com.homecoolink.R;
import com.homecoolink.SettingListener;
import com.homecoolink.activity.AddContactNextActivity;
import com.homecoolink.activity.AlarmRecordFromDeviceActivity;
import com.homecoolink.activity.ModifyContactActivity;
import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.entity.LocalDevice;
import com.homecoolink.fragment.ContactFrag;
import com.homecoolink.global.Constants;
import com.homecoolink.global.FList;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.ImageUtils;
import com.homecoolink.utils.T;
import com.homecoolink.utils.Utils;
import com.homecoolink.widget.NormalDialog;
import com.homecoolink.widget.NormalDialog.OnButtonOkListener;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;


public class MainAdapter extends BaseAdapter {
	Context context;
	private ContactFrag cf;


	public MainAdapter(Context context, ContactFrag cf) {
		this.context = context;
		this.cf = cf;
	}

	class ViewHolder {
		// private HeaderView head;
		private TextView name;
		private TextView online_state;
		private LinearLayout contact_list_rightarrow;
		// private ImageView login_type;
		private ImageView msgCount;
		private ImageView editcontact;
		private ImageView header_icon_play;
		private ImageView contact_list_defaultpic;
		private RelativeLayout layout_defence_btn;
		private RelativeLayout layout_setting_btn;
		private ImageView image_defence_state;
		private ProgressBar progress_defence;
		private LinearLayout listmask;

		public ImageView getcontact_list_defaultpic() {
			return contact_list_defaultpic;
		}

		public void setcontact_list_defaultpic(ImageView contact_list_defaultpic) {
			this.contact_list_defaultpic = contact_list_defaultpic;
		}

		public ImageView geteditcontact() {
			return editcontact;
		}

		public void seteditcontact(ImageView editcontact) {
			this.editcontact = editcontact;
		}

		public ImageView getMsgCount() {
			return msgCount;
		}

		public void setMsgCount(ImageView msgCount) {
			this.msgCount = msgCount;
		}

		public LinearLayout getcontact_list_rightarrow() {
			return contact_list_rightarrow;
		}

		public void setcontact_list_rightarrow(
				LinearLayout contact_list_rightarrow) {
			this.contact_list_rightarrow = contact_list_rightarrow;
		}

		// public ImageView getLogin_type() {
		// return login_type;
		// }

		// public void setLogin_type(ImageView login_type) {
		// this.login_type = login_type;
		// }

		public LinearLayout getListMask() {
			return listmask;
		}

		public void setListMask(LinearLayout listmask) {
			this.listmask = listmask;
		}

		public TextView getOnline_state() {
			return online_state;
		}

		public void setOnline_state(TextView online_state) {
			this.online_state = online_state;
		}

		//
		// public HeaderView getHead() {
		// return head;
		// }

		// public void setHead(HeaderView head) {
		// this.head = head;
		// }

		public TextView getName() {
			return name;
		}

		public void setName(TextView name) {
			this.name = name;
		}

		public ImageView getHeader_icon_play() {
			return header_icon_play;
		}

		public void setHeader_icon_play(ImageView header_icon_play) {
			this.header_icon_play = header_icon_play;
		}

		public RelativeLayout getLayout_defence_btn() {
			return layout_defence_btn;
		}

		public void setLayout_defence_btn(RelativeLayout layout_defence_btn) {
			this.layout_defence_btn = layout_defence_btn;
		}

		public RelativeLayout getlayout_setting_btn() {
			return layout_setting_btn;
		}

		public void setlayout_setting_btn(RelativeLayout layout_setting_btn) {
			this.layout_setting_btn = layout_setting_btn;
		}

		public ImageView getImage_defence_state() {
			return image_defence_state;
		}

		public void setImage_defence_state(ImageView image_defence_state) {
			this.image_defence_state = image_defence_state;
		}

		public ProgressBar getProgress_defence() {
			return progress_defence;
		}

		public void setProgress_defence(ProgressBar progress_defence) {
			this.progress_defence = progress_defence;
		}

	}

	class ViewHolder2 {
		public TextView name;

		// public ImageView device_type;

		public TextView getName() {
			return name;
		}

		public void setName(TextView name) {
			this.name = name;
		}

		// public ImageView getDevice_type() {
		// return device_type;
		// }
		//
		// public void setDevice_type(ImageView device_type) {
		// this.device_type = device_type;
		// }

	}

	@Override
	public int getCount() {
		// int size = FList.getInstance().getUnsetPasswordLocalDevices().size();
		return FList.getInstance().size();
	}

	@Override
	public Contact getItem(int position) {
		return FList.getInstance().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (position >= FList.getInstance().size()) {
			return 0;
		} else {
			return 1;
		}

	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	public void updateImage(String threeNum, boolean isGray, ImageView imgv) {
		Bitmap tempBitmap;
		try {

			tempBitmap = ImageUtils.getBitmap(new File(
					"/sdcard/screenshot/tempHead/" + NpcCommon.mThreeNum + "/"
							+ threeNum + ".jpg"), 200, 200);
			// tempBitmap = ImageUtils.roundCorners(tempBitmap,
			// ImageUtils.getScaleRounded(tempBitmap.getWidth()));
			imgv.setImageBitmap(tempBitmap);
		} catch (Exception e) {

			tempBitmap = BitmapFactory.decodeResource(cf.getResources(),
					R.drawable.contact_list_defaultpic);
			// tempBitmap = ImageUtils.roundCorners(tempBitmap,
			// ImageUtils.getScaleRounded(tempBitmap.getWidth()));
			imgv.setImageBitmap(tempBitmap);

		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		// if (position < size1) {
		View view = convertView;
		final ViewHolder holder;
		if (null == view) {
			view = LayoutInflater.from(context).inflate(
					R.layout.list_contact_item3, null);
			holder = new ViewHolder();
			TextView name = (TextView) view.findViewById(R.id.user_name);
			holder.setName(name);
			LinearLayout listmask = (LinearLayout) view
					.findViewById(R.id.listmask);
			holder.setListMask(listmask);
			TextView onlineState = (TextView) view
					.findViewById(R.id.online_state);
			holder.setOnline_state(onlineState);
			// ImageView loginType = (ImageView) view
			// .findViewById(R.id.login_type);
			// holder.setLogin_type(loginType);
			ImageView msgCount = (ImageView) view.findViewById(R.id.mess_new);
			holder.setMsgCount(msgCount);

			ImageView editcontact = (ImageView) view
					.findViewById(R.id.editcontact);
			holder.seteditcontact(editcontact);

			ImageView contact_list_defaultpic = (ImageView) view
					.findViewById(R.id.contact_list_defaultpic);
			holder.setcontact_list_defaultpic(contact_list_defaultpic);

			LinearLayout contact_list_rightarrow = (LinearLayout) view
					.findViewById(R.id.title);
			holder.setcontact_list_rightarrow(contact_list_rightarrow);
			ImageView headerIconPlay = (ImageView) view
					.findViewById(R.id.layout_play_btn);
			holder.setHeader_icon_play(headerIconPlay);

			RelativeLayout layout_defence_btn = (RelativeLayout) view
					.findViewById(R.id.layout_defence_btn);
			holder.setLayout_defence_btn(layout_defence_btn);
			RelativeLayout layout_setting_btn = (RelativeLayout) view
					.findViewById(R.id.layout_setting_btn);
			holder.setlayout_setting_btn(layout_setting_btn);
			ImageView image_defence_state = (ImageView) view
					.findViewById(R.id.image_defence_state);
			holder.setImage_defence_state(image_defence_state);
			ProgressBar progress_defence = (ProgressBar) view
					.findViewById(R.id.progress_defence);
			holder.setProgress_defence(progress_defence);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		final Contact contact = FList.getInstance().get(position);
		int deviceType = contact.contactType;
		if (contact.onLineState == Constants.DeviceState.ONLINE) {
			holder.getListMask().setVisibility(View.GONE);
			holder.geteditcontact().setVisibility(View.VISIBLE);
			
			
			
			
			
			holder.getlayout_setting_btn().setVisibility(View.VISIBLE);
			holder.getHeader_icon_play().setImageDrawable(
					context.getResources().getDrawable(
							R.drawable.contact_list_play));
			updateImage(contact.contactId, false,
					holder.getcontact_list_defaultpic());
			holder.getOnline_state().setText(R.string.online_state);
			holder.getOnline_state().setTextColor(
					context.getResources().getColor(R.color.dialog_title));
			if (contact.contactType == P2PValue.DeviceType.UNKNOWN
					|| contact.contactType == P2PValue.DeviceType.PHONE) {
				holder.getLayout_defence_btn().setVisibility(
						View.GONE);
			} else {
				holder.getLayout_defence_btn().setVisibility(
						View.VISIBLE);
				if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_LOADING) {
					holder.getProgress_defence().setVisibility(
							View.VISIBLE);
					holder.getImage_defence_state().setVisibility(
							View.GONE);
				} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
					holder.getProgress_defence().setVisibility(
							View.GONE);
					holder.getImage_defence_state().setVisibility(
							View.VISIBLE);
					holder.getImage_defence_state().setImageResource(
							R.drawable.contact_list_lock);
				} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
					holder.getProgress_defence().setVisibility(
							View.GONE);
					holder.getImage_defence_state().setVisibility(
							View.VISIBLE);
					holder.getImage_defence_state().setImageResource(
							R.drawable.contact_list_unlock);
				} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET) {
					holder.getProgress_defence().setVisibility(
							View.GONE);
					holder.getImage_defence_state().setVisibility(
							View.VISIBLE);
					holder.getImage_defence_state().setImageResource(
							R.drawable.ic_defence_warning);
				} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
					holder.getProgress_defence().setVisibility(
							View.GONE);
					holder.getImage_defence_state().setVisibility(
							View.VISIBLE);
					holder.getImage_defence_state().setImageResource(
							R.drawable.ic_defence_warning);
				} else if (contact.defenceState == Constants.DefenceState.DEFENCE_NO_PERMISSION) {
					holder.getProgress_defence().setVisibility(
							View.GONE);
					holder.getImage_defence_state().setVisibility(
							View.VISIBLE);
					holder.getImage_defence_state().setImageResource(
							R.drawable.limit);

				}
			}
			holder.geteditcontact().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					LocalDevice localDevice = FList.getInstance()
							.isContactUnSetPassword(contact.contactId);
					if (null != localDevice) {
						NormalDialog dialog = new NormalDialog(
								context,
								context.getResources()
										.getString(
												R.string.confirm_pwdreset_title),
								context.getResources()
										.getString(
												R.string.confirm_pwdreset_content),
								context.getResources().getString(
										R.string.confirm), context
										.getResources().getString(
												R.string.no));
						Contact saveContact = new Contact();
						saveContact.contactId = localDevice.contactId;
						saveContact.contactType = localDevice.type;
						saveContact.messageCount = 0;
						saveContact.activeUser = NpcCommon.mThreeNum;						

						final Intent modify = new Intent();
						modify.setClass(context,
								AddContactNextActivity.class);
						modify.putExtra("isCreatePassword", true);
						modify.putExtra("contact", saveContact);
						String mark = localDevice.address
								.getHostAddress();					
						modify.putExtra("ipFlag", mark.substring(
								mark.lastIndexOf(".") + 1,
								mark.length()));
						dialog.setOnButtonOkListener(new OnButtonOkListener() {

							@Override
							public void onClick() {
								// TODO Auto-generated method stub

								context.startActivity(modify);

								return;
							}
						});
						dialog.showNormalDialog();
						dialog.setCanceledOnTouchOutside(false);
						return;
					}else{
						Intent modify = new Intent();
						modify.setClass(context, ModifyContactActivity.class);
						modify.putExtra("contact", contact);
						modify.putExtra("showpass", "1");
						context.startActivity(modify);
					}
					
					
					
					
					
				}
			});
			holder.getName().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent modify = new Intent();
					modify.setClass(context, ModifyContactActivity.class);
					modify.putExtra("contact", contact);
					modify.putExtra("showpass", "1");
					context.startActivity(modify);
				}
			});
			holder.getHeader_icon_play().setEnabled(true);
		} else {
			holder.geteditcontact().setVisibility(View.GONE);
			holder.getName().setOnClickListener(null);
			holder.geteditcontact().setOnClickListener(null);
			updateImage(contact.contactId, false,
					holder.getcontact_list_defaultpic());
			holder.getListMask().setVisibility(View.VISIBLE);
			holder.getlayout_setting_btn().setVisibility(View.GONE);
			holder.getHeader_icon_play().setImageDrawable(
					context.getResources().getDrawable(
							R.drawable.contact_list_notonline));
			holder.getHeader_icon_play().setEnabled(false);
			holder.getOnline_state().setText(R.string.offline_state);
			holder.getOnline_state().setTextColor(
					context.getResources().getColor(R.color.text_color_gray));
			holder.getLayout_defence_btn().setVisibility(View.GONE);
		}

		// switch (deviceType) {
		// case P2PValue.DeviceType.NPC:
		// holder.getLogin_type().setImageResource(
		// R.drawable.ic_device_type_npc);
		// break;
		// case P2PValue.DeviceType.IPC:
		// holder.getLogin_type().setImageResource(
		// R.drawable.ic_device_type_ipc);
		// break;
		// case P2PValue.DeviceType.PHONE:
		// holder.getLogin_type().setImageResource(
		// R.drawable.ic_device_type_phone);
		// break;
		// case P2PValue.DeviceType.DOORBELL:
		// holder.getLogin_type().setImageResource(
		// R.drawable.ic_device_type_door_bell);
		// break;
		// case P2PValue.DeviceType.UNKNOWN:
		// holder.getLogin_type().setImageResource(
		// R.drawable.ic_device_type_unknown);
		// break;
		// default:
		// holder.getLogin_type().setImageResource(
		// R.drawable.ic_device_type_unknown);
		// break;
		// }
		// if (contact.messageCount > 0) {
		// ImageView msgCount = holder.getMsgCount();
		// msgCount.setVisibility(RelativeLayout.VISIBLE);
		// // if (contact.messageCount > 10) {
		// // msgCount.setText("10+");
		// // } else {
		// // msgCount.setText(contact.messageCount + "");
		// // }
		//
		// } else {
		// holder.getMsgCount().setVisibility(RelativeLayout.GONE);
		// }
		// if(!cf.CheckMessExitst( contact.contactId))
		// {
		// P2PHandler.getInstance().getAlarmRecord(
		// contact.contactId, contact.contactPassword);
		// }
		cf.CheckNewMess(view, contact.contactId);
				
		holder.getName().setText(contact.contactName);
		
		if (deviceType == P2PValue.DeviceType.NPC
				|| deviceType == P2PValue.DeviceType.IPC
				|| deviceType == P2PValue.DeviceType.DOORBELL) {
			holder.getHeader_icon_play().setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {

							LocalDevice localDevice = FList.getInstance()
									.isContactUnSetPassword(contact.contactId);
							if (null != localDevice) {
								NormalDialog dialog = new NormalDialog(
										context,
										context.getResources()
												.getString(
														R.string.confirm_pwdreset_title),
										context.getResources()
												.getString(
														R.string.confirm_pwdreset_content),
										context.getResources().getString(
												R.string.confirm), context
												.getResources().getString(
														R.string.no));
								Contact saveContact = new Contact();
								saveContact.contactId = localDevice.contactId;
								saveContact.contactType = localDevice.type;
								saveContact.messageCount = 0;
								saveContact.activeUser = NpcCommon.mThreeNum;
						

								final Intent modify = new Intent();
								modify.setClass(context,
										AddContactNextActivity.class);
								modify.putExtra("isCreatePassword", true);
								modify.putExtra("contact", saveContact);
								String mark = localDevice.address
										.getHostAddress();
							
								modify.putExtra("ipFlag", mark.substring(
										mark.lastIndexOf(".") + 1,
										mark.length()));
								dialog.setOnButtonOkListener(new OnButtonOkListener() {

									@Override
									public void onClick() {
										// TODO Auto-generated method stub

										context.startActivity(modify);

										return;
									}
								});
								dialog.showNormalDialog();
								dialog.setCanceledOnTouchOutside(false);
								return;
							}
							if (contact.contactId == null
									|| contact.contactId.equals("")) {
								T.showShort(context, R.string.username_error);
								return;
							}
							if (contact.contactPassword == null
									|| contact.contactPassword.equals("")) {
								T.showShort(context, R.string.password_error);
								return;
							}

							Intent monitor = new Intent();
							monitor.setClass(context, CallActivity.class);
							monitor.putExtra("callId", contact.contactId);
							monitor.putExtra("contactName", contact.contactName);
							monitor.putExtra("password",
									contact.contactPassword);
							monitor.putExtra("isOutCall", true);
							monitor.putExtra("type",
									Constants.P2P_TYPE.P2P_TYPE_MONITOR);
							context.startActivity(monitor);
						}

					});
			holder.getHeader_icon_play().setVisibility(View.VISIBLE);
		} else if (deviceType == P2PValue.DeviceType.PHONE) {
			holder.getHeader_icon_play().setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View arg0) {

							if (contact.contactId == null
									|| contact.contactId.equals("")) {
								T.showShort(context, R.string.username_error);
								return;
							}
							Intent call = new Intent();
							call.setClass(context, CallActivity.class);
							call.putExtra("callId", contact.contactId);
							call.putExtra("isOutCall", true);
							call.putExtra("type",
									Constants.P2P_TYPE.P2P_TYPE_CALL);
							context.startActivity(call);

						}

					});

			holder.getHeader_icon_play().setVisibility(View.VISIBLE);
		} else {
			holder.getHeader_icon_play().setOnClickListener(null);
			holder.getHeader_icon_play().setVisibility(View.GONE);
		}
		holder.getlayout_setting_btn().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
					
						// LocalDevice localDevice = FList.getInstance()
						// .isContactUnSetPassword(contact.contactId);
						// if (null != localDevice) {
						// NormalDialog dialog = new NormalDialog(arg0
						// .getContext(), "密码重置", "设备已被重置，是否重新设置密码？",
						// "确定", "取消");
						// Contact saveContact = new Contact();
						// saveContact.contactId = localDevice.contactId;
						// saveContact.contactType = localDevice.type;
						// saveContact.messageCount = 0;
						// saveContact.activeUser = NpcCommon.mThreeNum;
						//
						// final Intent modify = new Intent();
						// modify.setClass(context,
						// AddContactNextActivity.class);
						// modify.putExtra("isCreatePassword", true);
						// modify.putExtra("contact", saveContact);
						// String mark = localDevice.address.getHostAddress();
						// Log.e("343", "mark==" + mark);
						// modify.putExtra("ipFlag", mark.substring(
						// mark.lastIndexOf(".") + 1, mark.length()));
						// dialog.setOnButtonOkListener(new OnButtonOkListener()
						// {
						//
						// @Override
						// public void onClick() {
						// // TODO Auto-generated method stub
						//
						// context.startActivity(modify);
						//
						// return;
						// }
						// });
						// dialog.showNormalDialog();
						// dialog.setCanceledOnTouchOutside(false);
						//
						// } else {
						cf.quickSetting(contact);
						// }

					}

				});
		holder.getcontact_list_rightarrow().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (contact.defenceState == Constants.DefenceState.DEFENCE_NO_PERMISSION) {
							T.showShort(context,
									R.string.insufficient_permissions);
							return;
						}
						Intent go_alarm_record = new Intent(context,
								AlarmRecordFromDeviceActivity.class);
						go_alarm_record.putExtra("contact", contact);
						context.startActivity(go_alarm_record);
					}
				});

		holder.getLayout_defence_btn().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_NET
								|| contact.defenceState == Constants.DefenceState.DEFENCE_STATE_WARNING_PWD) {
							holder.getProgress_defence().setVisibility(
									View.VISIBLE);
							holder.getImage_defence_state().setVisibility(
									View.GONE);
							P2PHandler.getInstance().getDefenceStates(
									contact.contactId, contact.contactPassword);

							FList.getInstance().setIsClickGetDefenceState(
									contact.contactId, true);
						} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_ON) {
							holder.getProgress_defence().setVisibility(
									View.VISIBLE);
							holder.getImage_defence_state().setVisibility(
									View.GONE);
							P2PHandler
									.getInstance()
									.setRemoteDefence(
											contact.contactId,
											contact.contactPassword,
											Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_OFF);
							FList.getInstance().setIsClickGetDefenceState(
									contact.contactId, true);

						} else if (contact.defenceState == Constants.DefenceState.DEFENCE_STATE_OFF) {
							holder.getProgress_defence().setVisibility(
									View.VISIBLE);
							holder.getImage_defence_state().setVisibility(
									View.GONE);
							P2PHandler
									.getInstance()
									.setRemoteDefence(
											contact.contactId,
											contact.contactPassword,
											Constants.P2P_SET.REMOTE_DEFENCE_SET.ALARM_SWITCH_ON);
							FList.getInstance().setIsClickGetDefenceState(
									contact.contactId, true);
						} else if (contact.defenceState == Constants.DefenceState.DEFENCE_NO_PERMISSION) {
							T.showShort(context,
									R.string.insufficient_permissions);
						}

					}

				});

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			
				// LocalDevice localDevice = FList.getInstance()
				// .isContactUnSetPassword(contact.contactId);
				// if (null != localDevice) {
				// Contact saveContact = new Contact();
				// saveContact.contactId = localDevice.contactId;
				// saveContact.contactType = localDevice.type;
				// saveContact.messageCount = 0;
				// saveContact.activeUser = NpcCommon.mThreeNum;
				//
				// Intent modify = new Intent();
				// modify.setClass(context, AddContactNextActivity.class);
				// modify.putExtra("isCreatePassword", true);
				// modify.putExtra("contact", saveContact);
				// String mark = localDevice.address.getHostAddress();
				// Log.e("343", "mark=="+mark);
				// modify.putExtra(
				// "ipFlag",
				// mark.substring(mark.lastIndexOf(".") + 1,
				// mark.length()));
				// context.startActivity(modify);
				// return;
				// }

			
				// cf.showQuickActionBar(arg0, contact);
			}

		});

		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {

				NormalDialog dialog = new NormalDialog(context, context
						.getResources().getString(R.string.delete_contact),
						context.getResources().getString(
								R.string.are_you_sure_delete)
								+ " " + contact.contactId + "?", context
								.getResources().getString(R.string.delete),
						context.getResources().getString(R.string.cancel));
				dialog.rootView = arg0.getRootView();
				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {

					@Override
					public void onClick() {
						P2PHandler.getInstance().getBindAlarmId(
								contact.contactId, contact.contactPassword);
						SettingListener.currentUser = contact.contactId;
						SettingListener.currentPwd = contact.contactPassword;
						FList.getInstance().delete(contact, position, handler);
						DataManager.deleteAlarmRecordByDeviceId(context,
								NpcCommon.mThreeNum, contact.contactId);
						File file = new File(Constants.Image.USER_HEADER_PATH
								+ NpcCommon.mThreeNum + "/" + contact.contactId);
						Utils.deleteFile(file);

					}
				});
				dialog.showDialog();
				return true;
			}

		});
		return view;
		// } else {
		// 
		// View view = convertView;
		// final ViewHolder2 holder2;
		// if (view == null) {
		// view = LayoutInflater.from(context).inflate(
		// R.layout.list_contact_item2, null);
		// holder2 = new ViewHolder2();
		// TextView name = (TextView) view.findViewById(R.id.user_name);
		// holder2.setName(name);
		// // ImageView typeImg = (ImageView) view
		// // .findViewById(R.id.login_type);
		// // holder2.setDevice_type(typeImg);
		// view.setTag(holder2);
		// } else {
		// holder2 = (ViewHolder2) view.getTag();
		// }
		// final LocalDevice localDevice = FList.getInstance()
		// .getUnsetPasswordLocalDevices().get(position - size1);
		// holder2.name.setText(localDevice.getContactId());
		// // switch (localDevice.getType()) {
		// // case P2PValue.DeviceType.NPC:
		// // holder2.device_type
		// // .setImageResource(R.drawable.ic_device_type_npc);
		// // break;
		// // case P2PValue.DeviceType.IPC:
		// // holder2.device_type
		// // .setImageResource(R.drawable.ic_device_type_ipc);
		// // break;
		// // case P2PValue.DeviceType.DOORBELL:
		// // holder2.device_type
		// // .setImageResource(R.drawable.ic_device_type_door_bell);
		// // break;
		// // case P2PValue.DeviceType.UNKNOWN:
		// // holder2.device_type
		// // .setImageResource(R.drawable.ic_device_type_unknown);
		// // break;
		// // default:
		// // holder2.device_type
		// // .setImageResource(R.drawable.ic_device_type_unknown);
		// // break;
		// // }
		// view.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// 
		//
		// Contact saveContact = new Contact();
		// saveContact.contactId = localDevice.contactId;
		// saveContact.contactType = localDevice.type;
		// saveContact.messageCount = 0;
		// saveContact.activeUser = NpcCommon.mThreeNum;
		// Intent modify = new Intent();
		// modify.setClass(context, AddContactNextActivity.class);
		// modify.putExtra("isCreatePassword", true);
		// modify.putExtra("contact", saveContact);
		// String mark = localDevice.address.getHostAddress();
		// modify.putExtra(
		// "ipFlag",
		// mark.substring(mark.lastIndexOf(".") + 1,
		// mark.length()));
		// context.startActivity(modify);
		//
		// }
		// });
		// return view;
		// }
	}

	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			notifyDataSetChanged();
			return true;
		}
	});

	@Override
	public void notifyDataSetChanged() {

		super.notifyDataSetChanged();
	}
}
