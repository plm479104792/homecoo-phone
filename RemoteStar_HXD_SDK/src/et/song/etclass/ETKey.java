package et.song.etclass;

import android.content.ContentValues;
import et.song.db.DBProfile;
import et.song.db.ETDB;
import et.song.face.IOp;
import et.song.tool.ETTool;

public class ETKey implements IOp {
	public static final int ETKEY_STATE_STUDY = 0x00000001;
	public static final int ETKEY_STATE_KNOWN = 0x00000002;
	public static final int ETKEY_STATE_TYPE = 0x00000003;
	public static final int ETKEY_STATE_DIY = 0x00000004;
	public static final int ETKEY_STATE_NET = 0x00000005;

	private int mID;
	private int mDID;
	private String mName;
	private int mResId = 0;
	private float mX;
	private float mY;
	private byte[] mKeyValue;
	private int mKey;
	private int mBrandIndex;
	private int mBrandPos;
	private int mRow;
	private int mState;

	public void SetState(int state) {
		mState = state;
	}

	public int GetState() {
		return mState;
	}

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

	public void SetRes(int resId) {
		mResId = resId;
	}

	public int GetRes() {
		return mResId;
	}

	public void SetX(float x) {
		mX = x;
	}

	public void SetY(float y) {
		mY = y;
	}

	public void SetPos(float x, float y) {
		mX = x;
		mY = y;
	}

	public float GetX() {
		return mX;
	}

	public float GetY() {
		return mY;
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

	public void SetBrandIndex(int brandIndex) {
		mBrandIndex = brandIndex;
	}

	public int GetBrandIndex() {
		return mBrandIndex;
	}

	public void SetBrandPos(int brandPos) {
		mBrandPos = brandPos;
	}

	public int GetBrandPos() {
		return mBrandPos;
	}

	public void SetRow(int row) {
		mRow = row;
	}

	public int GetRow() {
		return mRow;
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
			value.put(DBProfile.TABLE_KEY_FIELD_NAME, GetName());
			value.put(DBProfile.TABLE_KEY_FIELD_STATE, GetState());
			value.put(DBProfile.TABLE_KEY_FIELD_KEYVALUE,
					ETTool.BytesToHexString(GetValue()));
			db.updataData(DBProfile.KEY_TABLE_NAME, value,
					DBProfile.TABLE_KEY_FIELD_ID + " = ?",
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
			keyValue.put(DBProfile.TABLE_KEY_FIELD_DEVICE_ID, GetDID());
			keyValue.put(DBProfile.TABLE_KEY_FIELD_NAME, GetName());
			keyValue.put(DBProfile.TABLE_KEY_FIELD_RES, GetRes());
			keyValue.put(DBProfile.TABLE_KEY_FIELD_X, GetX());
			keyValue.put(DBProfile.TABLE_KEY_FIELD_Y, GetY());
			keyValue.put(DBProfile.TABLE_KEY_FIELD_KEYVALUE,
					ETTool.BytesToHexString(GetValue()));
			keyValue.put(DBProfile.TABLE_KEY_FIELD_KEY, GetKey());
			keyValue.put(DBProfile.TABLE_KEY_FIELD_BRANDINDEX, GetBrandIndex());
			keyValue.put(DBProfile.TABLE_KEY_FIELD_BRANDPOS, GetBrandPos());
			keyValue.put(DBProfile.TABLE_KEY_FIELD_ROW, GetRow());
			keyValue.put(DBProfile.TABLE_KEY_FIELD_STATE, GetState());
			db.insertData(DBProfile.KEY_TABLE_NAME, keyValue);
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
