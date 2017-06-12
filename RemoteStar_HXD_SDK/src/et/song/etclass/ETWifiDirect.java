package et.song.etclass;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

import et.song.db.DBProfile;
import et.song.db.ETDB;
import et.song.face.IOp;

public class ETWifiDirect implements IOp {
	private int mID;
	private int mResId;
	private String mIp;
	private int mPort;
	public List<ETWifiSub> mWifiSubList;

	public ETWifiDirect() {
		mWifiSubList = new ArrayList<ETWifiSub>();
	}

	public void SetID(int id) {
		mID = id;
	}

	public int GetID() {
		return mID;
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
		db.deleteData(DBProfile.WIFIDIRECT_TABLE_NAME,
				DBProfile.TABLE_WIFIDIRECT_FIELD_ID + "=?", new String[] {String.valueOf(this.mID)});
	}

	@Override
	public void Inster(ETDB db) {
		// TODO Auto-generated method stub
		ContentValues Value = new ContentValues();
		Value.put(DBProfile.TABLE_WIFIDIRECT_FIELD_RES, mResId);
		Value.put(DBProfile.TABLE_WIFIDIRECT_FIELD_PORT, mPort);
		Value.put(DBProfile.TABLE_WIFIDIRECT_FIELD_IP, mIp);
		db.insertData(DBProfile.WIFIDIRECT_TABLE_NAME, Value);
	}

	@Override
	public ETGroup findGroupByName(ETDB db) {
		// TODO Auto-generated method stub
		return null;
	}

}
