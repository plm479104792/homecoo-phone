package et.song.etclass;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;

import et.song.db.DBProfile;
import et.song.db.ETDB;
import et.song.face.IOp;
import et.song.global.ETGlobal;

public class ETPage implements IOp {
	private static List<ETGroup> mGroupList = null;
	private static ETPage instance = null;

	private ETPage() {
		mGroupList = new ArrayList<ETGroup>();
	}

	public static final ETPage getInstance(Context context) {
		if (instance == null) {
			instance = new ETPage();
		}
		return instance;
	}

	@Override
	public void Load(ETDB db) {
		// TODO Auto-generated method stub
		mGroupList.clear();
		try {
			Cursor cursor = db.queryData2Cursor("select count(*) from "
					+ DBProfile.GROUP_TABLE_NAME, null);
			cursor.moveToFirst();
			long count = cursor.getLong(0);
			cursor.close();
			if (count == 0) {
//				for (int i = 0; i < ETGlobal.mGroupTypes.length; i++) {
//				ETGroup group = new ETGroup();
//				group.SetID(1);
//				group.SetName(String.valueOf(0));
//				group.SetType(0);
//				group.SetRes(0);
//				group.Inster(db);
//				group.Load(db);
//				mGroupList.add(group);
//				}
			} else {
				Cursor c = db.queryData2Cursor("select * from "
						+ DBProfile.GROUP_TABLE_NAME, null);
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					ETGroup group = new ETGroup();
					int _id = c.getInt(c
							.getColumnIndex(DBProfile.TABLE_GROUP_FIELD_ID));
					String _name = c.getString(c
							.getColumnIndex(DBProfile.TABLE_GROUP_FIELD_NAME));
					String _devid = c.getString(c
							.getColumnIndex(DBProfile.TABLE_GROUP_FIELD_DEVID));
					int _type = c.getInt(c
							.getColumnIndex(DBProfile.TABLE_GROUP_FIELD_TYPE));
					int _res = c.getInt(c
							.getColumnIndex(DBProfile.TABLE_GROUP_FIELD_RES));
					group.SetID(_id);
					group.SetName(_name);
					group.setmDevid(_devid);
					group.SetType(_type);
					group.SetRes(_res);
					group.Load(db);
					mGroupList.add(group);
				}
				c.close();
			}
			//默认的添加位置图标
//			ETGroup group = new ETGroup();
//			group.SetID(0);
//			group.SetName("");
//			group.SetType(ETGlobal.ETGROUP_TYPE_ADD);
//			group.SetRes(ETGlobal.mGroupImages.length - 1);
//			mGroupList.add(group);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void Update(ETDB db) {
		// TODO Auto-generated method stub

	}

	@Override
	public int GetCount() {
		// TODO Auto-generated method stub
		return mGroupList.size();
	}

	@Override
	public Object GetItem(int i) {
		// TODO Auto-generated method stub

		return mGroupList.get(i);
	}

	@Override
	public void Delete(ETDB db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Inster(ETDB db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ETGroup findGroupByName(ETDB db) {
		// TODO Auto-generated method stub
		return null;
	}

}