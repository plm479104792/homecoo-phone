package com.homecoolink.data;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public class DeviceAlarmRecordDB {
	public static final String TABLE_NAME = "device_alarm_record";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_ID_DATA_TYPE = "integer PRIMARY KEY AUTOINCREMENT";

	public static final String COLUMN_DEVICEID = "deviceId";
	public static final String COLUMN_DEVICEID_DATA_TYPE = "varchar";

	public static final String COLUMN_ACTIVE_USER = "activeUser";
	public static final String COLUMN_ACTIVE_USER_DATA_TYPE = "varchar";

	public static final String COLUMN_ALARM_TYPE = "alarmType";
	public static final String COLUMN_ALARM_TYPE_DATA_TYPE = "integer";

	public static final String COLUMN_ALARM_TIME = "alarmTime";
	public static final String COLUMN_ALARM_TIME_DATA_TYPE = "varchar";

	public static final String COLUMN_ALARM_GROUP = "alarmGroup";
	public static final String COLUMN_ALARM_GROUP_DATA_TYPE = "integer";
	
	public static final String COLUMN_ALARM_ITEM = "alarmItem";
	public static final String COLUMN_ALARM_ITEM_DATA_TYPE = "integer";
	private SQLiteDatabase myDatabase;

	public DeviceAlarmRecordDB(SQLiteDatabase myDatabase) {
		this.myDatabase = myDatabase;
	}

	public static String getDeleteTableSQLString() {
		return SqlHelper.formDeleteTableSqlString(TABLE_NAME);
	}
	public static String getCreateTableString() {
		HashMap<String, String> columnNameAndType = new HashMap<String, String>();
		columnNameAndType.put(COLUMN_ID, COLUMN_ID_DATA_TYPE);
		columnNameAndType.put(COLUMN_DEVICEID, COLUMN_DEVICEID_DATA_TYPE);
		columnNameAndType.put(COLUMN_ACTIVE_USER, COLUMN_ACTIVE_USER_DATA_TYPE);
		columnNameAndType.put(COLUMN_ALARM_TYPE, COLUMN_ALARM_TYPE_DATA_TYPE);
		columnNameAndType.put(COLUMN_ALARM_TIME, COLUMN_ALARM_TIME_DATA_TYPE);
		columnNameAndType.put(COLUMN_ALARM_GROUP, COLUMN_ALARM_GROUP_DATA_TYPE);
		columnNameAndType.put(COLUMN_ALARM_ITEM, COLUMN_ALARM_ITEM_DATA_TYPE);
		String mSQLCreateWeiboInfoTable = SqlHelper.formCreateTableSqlString(
				TABLE_NAME, columnNameAndType);
		return mSQLCreateWeiboInfoTable;
	}
	public long insert(DeviceAlarmRecord alarmRecord) {
		long isResut = -1;
		if (alarmRecord != null) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_DEVICEID, alarmRecord.deviceId);
			values.put(COLUMN_ALARM_TYPE, alarmRecord.alarmType);
			values.put(COLUMN_ALARM_TIME, alarmRecord.alarmTime);
			values.put(COLUMN_ALARM_GROUP, alarmRecord.group);
			values.put(COLUMN_ALARM_ITEM, alarmRecord.item);
			try {
				isResut = myDatabase.insertOrThrow(TABLE_NAME, null, values);
			} catch (SQLiteConstraintException e) {
				e.printStackTrace();
			}
		}

		return isResut;
	}
	public ArrayList<DeviceAlarmRecord> findByDeviceId(String contactId) {
		ArrayList<DeviceAlarmRecord> lists = new ArrayList<DeviceAlarmRecord>();
		Cursor cursor = null;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
				+ COLUMN_DEVICEID + "=?", new String[] { contactId });
		if (cursor != null) {
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
				String deviceId = cursor.getString(cursor
						.getColumnIndex(COLUMN_DEVICEID));
				int alarmType = cursor.getInt(cursor
						.getColumnIndex(COLUMN_ALARM_TYPE));
				String alarmTime = cursor.getString(cursor
						.getColumnIndex(COLUMN_ALARM_TIME));
				String activeUser = cursor.getString(cursor
						.getColumnIndex(COLUMN_ACTIVE_USER));
				int group = cursor.getInt(cursor
						.getColumnIndex(COLUMN_ALARM_GROUP));
				int item = cursor.getInt(cursor
						.getColumnIndex(COLUMN_ALARM_ITEM));
				DeviceAlarmRecord data = new DeviceAlarmRecord();
				data.deviceId = deviceId;
				data.alarmType = alarmType;
				data.alarmTime = alarmTime;
				data.group = group;
				data.item = item;
				lists.add(data);
			}
			cursor.close();
		}
		return lists;
	}

	public int deleteByDeviceId(String deviceId) {
		return myDatabase.delete(TABLE_NAME, COLUMN_DEVICEID + "=?",
				new String[] { deviceId });
	}

	public int deleteById(int id) {
		return myDatabase.delete(TABLE_NAME, COLUMN_ID + "=?",
				new String[] { String.valueOf(id) });
	}
}
