package et.song.etclass;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.database.Cursor;

import et.song.db.DBProfile;
import et.song.db.ETDB;
import et.song.device.DeviceType;
import et.song.face.IOp;
import et.song.global.ETGlobal;

public class ETGroup implements IOp {
	private int mID;
	private String mName;
	private String mDevid;
	private String mGatewayid;
	private int mType;
	private int mResId;

	public List<ETDevice> mDeviceList;

	public ETGroup() {
		mDeviceList = new ArrayList<ETDevice>();
	}

	public void SetID(int id) {
		mID = id;
	}

	public int GetID() {
		return mID;
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
	

	public String getmDevid() {
		return mDevid;
	}

	public void setmDevid(String mDevid) {
		this.mDevid = mDevid;
	}

	public String getmGatewayid() {
		return mGatewayid;
	}

	public void setmGatewayid(String mGatewayid) {
		this.mGatewayid = mGatewayid;
	}

	@Override
	public void Load(ETDB db) {
		// TODO Auto-generated method stub
		mDeviceList.clear();
		try {

			Cursor cursor = db.queryData2Cursor("select count(*) from "
					+ DBProfile.GROUP_TABLE_NAME, null);
			cursor.moveToFirst();
			long count = cursor.getLong(0);
			cursor.close();
			if (count == 0) {

				// if (c.getCount() == 0) {
				// for (int i = 0; i < 9; i++) {
				// ETDevice device = new ETDevice();
				// device.SetName(String.valueOf(i));
				// device.SetType(i);
				// device.SetRes(i);
				// mDeviceList.add(device);
				// }
			} else {
				Cursor c = db.queryData2Cursor("select * from "
						+ DBProfile.DEVICE_TABLE_NAME + " where "
						+ DBProfile.TABLE_DEVICE_FIELD_GROUP_ID + " = " + mID,
						null);

				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					ETDevice device = null;
					int _id = c.getInt(c
							.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_ID));
					int _gid = c
							.getInt(c
									.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_GROUP_ID));
					String _name = c.getString(c
							.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_NAME));
					int _type = c.getInt(c
							.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_TYPE));

					int _res = c.getInt(c
							.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_RES));
					device = ETDevice.Builder(_type);
					device.SetID(_id);
					device.SetGID(_gid);
					device.SetName(_name);
					device.SetType(_type);
					device.SetRes(_res);
					device.Load(db);
					mDeviceList.add(device);
				}
				c.close();
			}
			ETDevice device = new ETDevice();
			device.SetID(0);
			device.SetName("");
			device.SetType(DeviceType.DEVICE_ADD);
			device.SetRes(ETGlobal.mDeviceImages.length - 1);
			mDeviceList.add(device);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void Update(ETDB db) {
		// TODO Auto-generated method stub
		ContentValues groupValue = new ContentValues();
		groupValue.put(DBProfile.TABLE_GROUP_FIELD_NAME, GetName());
		
		try {
			db.updataData(DBProfile.GROUP_TABLE_NAME, groupValue,
					DBProfile.TABLE_GROUP_FIELD_ID + " = ?",
					new String[] { String.valueOf(mID) });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public int GetCount() {
		// TODO Auto-generated method stub
		return mDeviceList.size();
	}

	@Override
	public Object GetItem(int i) {
		if (mDeviceList.size()>0) {
			return mDeviceList.get(i);
		}else {
			return null;
		}
		
	}

	@Override
	public void Delete(ETDB db) {
		// TODO Auto-generated method stub
		try {
			for (ETDevice device : mDeviceList) {
				device.Delete(db);
			}
			db.deleteData(DBProfile.GROUP_TABLE_NAME,
					DBProfile.TABLE_GROUP_FIELD_ID + " = ?",
					new String[] { String.valueOf(mID) });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void Inster(ETDB db) {
		System.out.println("group插入数据库");
		try {
			ContentValues groupValue = new ContentValues();
			groupValue.put(DBProfile.TABLE_GROUP_FIELD_NAME, GetName());
			groupValue.put(DBProfile.TABLE_GROUP_FIELD_GATEWAYID,getmGatewayid());
			groupValue.put(DBProfile.TABLE_GROUP_FIELD_DEVID, getmDevid());
			groupValue.put(DBProfile.TABLE_GROUP_FIELD_RES, GetRes());
			groupValue.put(DBProfile.TABLE_GROUP_FIELD_TYPE, GetType());
			db.insertData(DBProfile.GROUP_TABLE_NAME, groupValue);
			
			Cursor cursor = db.queryData2Cursor("select count(*) from "
					+ DBProfile.GROUP_TABLE_NAME + " order by "
					+ DBProfile.TABLE_GROUP_FIELD_ID + " desc", null);
			cursor.moveToFirst();
			long count = cursor.getLong(0);
			cursor.close();
			if (count == 0)
				return;
			Cursor c = db.queryData2Cursor("select * from "
					+ DBProfile.GROUP_TABLE_NAME + " order by "
					+ DBProfile.TABLE_GROUP_FIELD_ID + " desc", null);

			c.moveToFirst();
			int id = c.getInt(c.getColumnIndex(DBProfile.TABLE_GROUP_FIELD_ID));
			for (int i = 0; i < mDeviceList.size(); i++) {
				ETDevice device = mDeviceList.get(i);
				device.SetGID(id);
				ContentValues deviceValue = new ContentValues();
				deviceValue.put(DBProfile.TABLE_DEVICE_FIELD_GROUP_ID,
						device.GetGID());
				deviceValue.put(DBProfile.TABLE_DEVICE_FIELD_NAME,
						device.GetName());
				deviceValue.put(DBProfile.TABLE_DEVICE_FIELD_RES,
						device.GetRes());
				deviceValue.put(DBProfile.TABLE_DEVICE_FIELD_TYPE,
						device.GetType());
				db.insertData(DBProfile.KEY_TABLE_NAME, deviceValue);
				device.Inster(db);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public ETGroup findGroupByName(ETDB db) {
		String name=GetName();
		String wgid=getmGatewayid();
		String devid=getmDevid();
		try {
			Cursor c = db.queryData2Cursor("select * from "
					+ DBProfile.GROUP_TABLE_NAME + " where "
//					+ DBProfile.TABLE_GROUP_FIELD_NAME + " = " +"'"+name+"'"+" and "
					+DBProfile.TABLE_GROUP_FIELD_GATEWAYID+ " = " +"'"+wgid+"'"+" and "
					+ DBProfile.TABLE_GROUP_FIELD_DEVID + " = " +"'"+devid+"'"
					,
					null);
			System.out.println("位置"+GetName()+"wgid:"+wgid+"devid:"+devid+"数量为"+c.getCount());
			if (c.getCount()!=0) {
				c.moveToFirst();
				ETGroup group = new ETGroup();
				int _id = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_GROUP_FIELD_ID));
				String _name = c.getString(c
						.getColumnIndex(DBProfile.TABLE_GROUP_FIELD_NAME));
				int _type = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_GROUP_FIELD_TYPE));
				int _res = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_GROUP_FIELD_RES));
				group.SetID(_id);
				group.SetName(_name);
				group.SetType(_type);
				group.SetRes(_res);
				return group;
			}
		
		} catch (Exception e) {
			System.out.println("=====查找数据抛出异常！=====");
			e.printStackTrace();
		}
		
		return null;
	}


}
