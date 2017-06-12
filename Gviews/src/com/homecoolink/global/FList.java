package com.homecoolink.global;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.homecoolink.data.Contact;
import com.homecoolink.data.DataManager;
import com.homecoolink.entity.LocalDevice;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.shake.ShakeManager;

public class FList {
	private static FList manager = null;
	private static List<Contact> lists = null;
	private static HashMap<String, Contact> maps = new HashMap<String, Contact>();
	private static List<LocalDevice> localdevices = new ArrayList<LocalDevice>();
	private static List<LocalDevice> tempLocalDevices = new ArrayList<LocalDevice>();

	public FList() {
		if (null != lists) {
			lists.clear();
		}

		if (null != localdevices) {
			localdevices.clear();
		}

		manager = this;
		lists = DataManager.findContactByActiveUser(MyApp.app,
				NpcCommon.mThreeNum);
		maps.clear();
		for (Contact contact : lists) {
			maps.put(contact.contactId, contact);
		}
	}

	public static FList getInstance() {
		return manager;
	}

	public List<Contact> list() {
		return FList.lists;
	}

	public HashMap<String, Contact> map() {
		return FList.maps;
	}

	public Contact get(int position) {
		if (position >= lists.size()) {
			return null;
		} else {
			return lists.get(position);
		}
	}

	public int getType(String threeNum) {
		Contact contact = maps.get(threeNum);
		if (null == contact) {
			return P2PValue.DeviceType.UNKNOWN;
		} else {
			return contact.contactType;
		}

	}

	public void setType(String threeNum, int type) {
		Contact contact = maps.get(threeNum);
		if (null != contact) {
			contact.contactType = type;
			DataManager.updateContact(MyApp.app, contact);
		}
	}

	public int getState(String threeNum) {
		Contact contact = maps.get(threeNum);
		if (null == contact) {
			return Constants.DeviceState.OFFLINE;
		} else {
			return contact.onLineState;
		}
	}

	public void setState(String threeNum, int state) {
		Contact contact = maps.get(threeNum);
		if (null != contact) {
			contact.onLineState = state;
		}
	}

	public void setDefenceState(String threeNum, int state) {
		Contact contact = maps.get(threeNum);
		if (null != contact) {
			contact.defenceState = state;
		}
	}

	public void setIsClickGetDefenceState(String threeNum, boolean bool) {
		Contact contact = maps.get(threeNum);
		if (null != contact) {
			contact.isClickGetDefenceState = bool;
		}
	}

	public int size() {
		return lists.size();
	}

	public void sort() {
		Collections.sort(lists);
	}

	public void delete(Contact contact, int position, Handler handler) {
		maps.remove(contact.contactId);
		lists.remove(position);
		DataManager.deleteContactByActiveUserAndContactId(MyApp.app,
				NpcCommon.mThreeNum, contact.contactId);
		handler.sendEmptyMessage(0);

		Intent refreshNearlyTell = new Intent();
		refreshNearlyTell
				.setAction(Constants.Action.ACTION_REFRESH_NEARLY_TELL);
		MyApp.app.sendBroadcast(refreshNearlyTell);
	}

	public void insert(Contact contact) {
		Log.e("flist", "insert");
		DataManager.insertContact(MyApp.app, contact);
		lists.add(contact);
		maps.put(contact.contactId, contact);
		String[] contactIds = new String[] { contact.contactId };
		P2PHandler.getInstance().getFriendStatus(contactIds);
	}

	public void update(Contact contact) {
		int i = 0;
		for (Contact u : lists) {
			if (u.contactId.equals(contact.contactId)) {
				lists.set(i, contact);
				break;
			}
			i++;
		}

		maps.put(contact.contactId, contact);
		DataManager.updateContact(MyApp.app, contact);
	}

	public Contact isContact(String contactId) {
		return maps.get(contactId);
	}

	public synchronized void updateOnlineState() {

		FList flist = FList.getInstance();
		if (flist.size() <= 0) {
			Intent friends = new Intent();
			friends.setAction(Constants.Action.GET_FRIENDS_STATE);
			MyApp.app.sendBroadcast(friends);
			return;
		}

		String[] contactIds = new String[flist.size()];
		List<Contact> lists = flist.list();
		int i = 0;
		for (Contact contact : lists) {
			contactIds[i] = contact.contactId;
			i++;
		}
		P2PHandler.getInstance().getFriendStatus(contactIds);
	}

	public void getDefenceState() {
		new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < FList.lists.size(); i++) {
					Contact contact = FList.lists.get(i);
					if ((contact.contactType == P2PValue.DeviceType.DOORBELL
							|| contact.contactType == P2PValue.DeviceType.IPC || contact.contactType == P2PValue.DeviceType.NPC)) {

						P2PHandler.getInstance().getDefenceStates(
								contact.contactId, contact.contactPassword);

					}
				}
			}
		}.start();

	}

//	public void getPermissions(){
//		new Thread(){
//			public void run(){
//				for(int i=0;i<manager.lists.size();i++){
//					Contact contact = manager.lists.get(i);
//					if(contact.contactType==P2PValue.DeviceType.IPC){
//						P2PHandler.getInstance().checkPassword(contact.contactId, contact.contactPassword);
//					}
//				}
//			}
//		}.start(); 
//	}

	public synchronized void searchLocalDevice() {
		ShakeManager.getInstance().setSearchTime(5000);
		ShakeManager.getInstance().setHandler(mHandler);
		if (ShakeManager.getInstance().shaking()) {
			tempLocalDevices.clear();
		}
	}

	public void updateLocalDeviceWithLocalFriends() {
		List<LocalDevice> removeList = new ArrayList<LocalDevice>();
		for (LocalDevice localDevice : localdevices) {
			if (null != manager.isContact(localDevice.getContactId())) {
				removeList.add(localDevice);
			}
		}

		for (LocalDevice localDevice : removeList) {
			localdevices.remove(localDevice);
		}
	}

	public List<LocalDevice> getLocalDevices() {
		return FList.localdevices;
	}

	public List<LocalDevice> getUnsetPasswordLocalDevices() {
		List<LocalDevice> datas = new ArrayList<LocalDevice>();

		for (LocalDevice device : FList.localdevices) {
			int flag = device.flag;
			if (flag == Constants.DeviceFlag.UNSET_PASSWORD
					&& null == this.isContact(device.contactId)) {
				datas.add(device);
			}
		}
		return datas;
	}

	public List<LocalDevice> getSetPasswordLocalDevices() {
		List<LocalDevice> datas = new ArrayList<LocalDevice>();
		for (LocalDevice device : FList.localdevices) {
			int flag = device.flag;
			if (flag == Constants.DeviceFlag.ALREADY_SET_PASSWORD
					&& null == this.isContact(device.contactId)) {
				datas.add(device);
			}
		}
		return datas;
	}

	public LocalDevice isContactUnSetPassword(String contactId) {
		if (null == this.isContact(contactId)) {
			return null;
		}

		for (LocalDevice device : FList.localdevices) {
			if (device.contactId.equals(contactId)) {
				if (device.flag == Constants.DeviceFlag.UNSET_PASSWORD) {
					return device;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	public void updateLocalDeviceFlag(String contactId, int flag) {
		for (LocalDevice device : FList.localdevices) {
			if (device.contactId.equals(contactId)) {
				device.flag = flag;
				return;
			}
		}
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case ShakeManager.HANDLE_ID_SEARCH_END:
				localdevices.clear();
				for (LocalDevice localDevice : tempLocalDevices) {
					Log.e("my", "localDevice:" + localDevice.contactId);
					localdevices.add(localDevice);
				}
				// updateLocalDeviceWithLocalFriends();
				Intent i = new Intent();
				i.setAction(Constants.Action.LOCAL_DEVICE_SEARCH_END);
				MyApp.app.sendBroadcast(i);
				break;
			case ShakeManager.HANDLE_ID_RECEIVE_DEVICE_INFO:
				Bundle bundle = msg.getData();
				String id = bundle.getString("id");
				String name = bundle.getString("name");
				int flag = bundle.getInt("flag",
						Constants.DeviceFlag.ALREADY_SET_PASSWORD);
				int type = bundle.getInt("type", P2PValue.DeviceType.UNKNOWN);

				InetAddress address = (InetAddress) bundle
						.getSerializable("address");
				LocalDevice localDevice = new LocalDevice();
				localDevice.setContactId(id);
				localDevice.setFlag(flag);
				localDevice.setType(type);
				localDevice.setAddress(address);

				if (!tempLocalDevices.contains(localDevice)) {
					tempLocalDevices.add(localDevice);
				}
				break;

			}
			return false;
		}
	});
}
