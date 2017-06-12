package et.song.etclass;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

import et.song.db.DBProfile;
import et.song.db.ETDB;
import et.song.face.IOp;

public class ETWifiDevice implements IOp {
	private int mID;
	private String mName;
	private int mType;
	private int mResId;
	private String mUId;
	private String mSSID;
	private String mPWD;
	private int mIsWan;
	private String mIp;
	private int mPort;
	public List<ETWifiSub> mWifiSubList;

	public ETWifiDevice() {
		mWifiSubList = new ArrayList<ETWifiSub>();
	}

	public void SetID(int id) {
		mID = id;
	}

	public int GetID() {
		return mID;
	}

	public void SetUID(String id) {
		mUId = id;
	}

	public String GetUID() {
		return mUId;
	}
	
	public void SetSSID(String ssid) {
		mSSID = ssid;
	}

	public String GetSSID() {
		return mSSID;
	}
	public void SetPWD(String pwd) {
		mPWD = pwd;
	}

	public String GetPWD() {
		return mPWD;
	}
	

	public void SetName(String name) {
		mName = name;
	}

	public String GetName() {
		return mName;
	}

	public void SetType(int type) {
		mType = type;
	}

	public int GetType() {
		return mType;
	}

	public void SetRes(int resId) {
		mResId = resId;
	}

	public int GetRes() {
		return mResId;
	}
	

	public void SetPort(int port) {
		mPort = port;
	}

	public int GetPort() {
		return mPort;
	}


	public void SetIP(String ip) {
		mIp = ip;
	}

	public String GetIP() {
		return mIp;
	}
	

	public void SetWan(int isWan) {
		mIsWan = isWan;
	}

	public int GetWan() {
		return mIsWan;
	}
	
	@Override
	public void Load(ETDB db) {
		// TODO Auto-generated method stub
		mWifiSubList.clear();

	}

	@Override
	public void Update(ETDB db) {
		// TODO Auto-generated method stub

	}

	@Override
	public int GetCount() {
		// TODO Auto-generated method stub
		return mWifiSubList.size();
	}

	@Override
	public Object GetItem(int i) {
		// TODO Auto-generated method stub
		return mWifiSubList.get(i);
	}

	@Override
	public void Delete(ETDB db) {
		// TODO Auto-generated method stub
		db.deleteData(DBProfile.WIFIDEVICE_TABLE_NAME,
				DBProfile.TABLE_WIFIDEVICE_FIELD_ID + "=?", new String[] {String.valueOf(this.mID)});
	}

	@Override
	public void Inster(ETDB db) {
		// TODO Auto-generated method stub
		ContentValues Value = new ContentValues();
		Value.put(DBProfile.TABLE_WIFIDEVICE_FIELD_NAME,
				mName);
		Value.put(DBProfile.TABLE_WIFIDEVICE_FIELD_RES, mResId);
		Value.put(DBProfile.TABLE_WIFIDEVICE_FIELD_TYPE, mType);
		Value.put(DBProfile.TABLE_WIFIDEVICE_FIELD_UID, mUId);
		Value.put(DBProfile.TABLE_WIFIDEVICE_FIELD_SSID, mSSID);
		Value.put(DBProfile.TABLE_WIFIDEVICE_FIELD_PWD, mPWD);
		Value.put(DBProfile.TABLE_WIFIDEVICE_FIELD_WAN, mIsWan);
		Value.put(DBProfile.TABLE_WIFIDEVICE_FIELD_PORT, mPort);
		Value.put(DBProfile.TABLE_WIFIDEVICE_FIELD_IP, mIp);
		db.insertData(DBProfile.WIFIDEVICE_TABLE_NAME, Value);
	}

	@Override
	public ETGroup findGroupByName(ETDB db) {
		// TODO Auto-generated method stub
		return null;
	}

}
