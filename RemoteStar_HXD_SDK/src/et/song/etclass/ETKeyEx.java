package et.song.etclass;

import android.content.ContentValues;
import et.song.db.DBProfile;
import et.song.db.ETDB;
import et.song.face.IOp;
import et.song.tool.ETTool;

public class ETKeyEx implements IOp {
	private int mID;
	private int mDID;
	private String mName;
	private byte[] mKeyValue;
	private int mKey;

	
	public void SetId(int id) {
		mID = id;
	}

	public int GetId() {
		return mID;
	}

	public void SetDID(int id) {
		mDID = id;
	}

	public int GetDID() {
		return mDID;
	}

	public void SetName(String name) {
		mName = name;
	}

	public String GetName() {
		return mName;
	}

	
	public void SetKey(int key) {
		mKey = key;
	}

	public int GetKey() {
		return mKey;
	}

	public void SetValue(byte[] keyValue) {
		mKeyValue = keyValue;
	}

	public byte[] GetValue() {
		return mKeyValue;
	}

	

	@Override
	public void Load(ETDB db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void Update(ETDB db) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

		try {
			ContentValues value = new ContentValues();
			value.put(DBProfile.TABLE_KEYEX_FIELD_NAME, GetName());
			value.put(DBProfile.TABLE_KEYEX_FIELD_KEYVALUE,
					ETTool.BytesToHexString(GetValue()));
			db.updataData(DBProfile.KEYEX_TABLE_NAME, value,
					DBProfile.TABLE_KEYEX_FIELD_ID + " = ?",
					new String[] { String.valueOf(mID) });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void Delete(ETDB db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void Inster(ETDB db) {
		// TODO Auto-generated method stub
		try {
			ContentValues keyValue = new ContentValues();
			keyValue.put(DBProfile.TABLE_KEYEX_FIELD_DEVICE_ID, GetDID());
			keyValue.put(DBProfile.TABLE_KEYEX_FIELD_NAME, GetName());
			keyValue.put(DBProfile.TABLE_KEYEX_FIELD_KEYVALUE,
					ETTool.BytesToHexString(GetValue()));
			keyValue.put(DBProfile.TABLE_KEYEX_FIELD_KEY, GetKey());
			db.insertData(DBProfile.KEYEX_TABLE_NAME, keyValue);
		} catch (Exception ex) {

		}
	}

	@Override
	public int GetCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object GetItem(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ETGroup findGroupByName(ETDB db) {
		// TODO Auto-generated method stub
		return null;
	}
}
