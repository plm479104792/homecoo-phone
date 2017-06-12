package com.homecoolink.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public class AlarmRecordDB {
	public static final String TABLE_NAME = "alarm_record";
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

	public static final String COLUMN_ALARM_Status = "alarmStatus";
	public static final String COLUMN_ALARM_Status_DATA_TYPE = "integer";
	private SQLiteDatabase myDatabase;

	public AlarmRecordDB(SQLiteDatabase myDatabase) {
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
		columnNameAndType.put(COLUMN_ALARM_Status,
				COLUMN_ALARM_Status_DATA_TYPE);
		String mSQLCreateWeiboInfoTable = SqlHelper.formCreateTableSqlString(
				TABLE_NAME, columnNameAndType);
		return mSQLCreateWeiboInfoTable;
	}

	public long insert(AlarmRecord alarmRecord) {
		long isResut = -1;
		if (alarmRecord != null) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_DEVICEID, alarmRecord.deviceId);
			values.put(COLUMN_ACTIVE_USER, alarmRecord.activeUser);
			values.put(COLUMN_ALARM_TYPE, alarmRecord.alarmType);
			values.put(COLUMN_ALARM_TIME, alarmRecord.alarmTime);
			values.put(COLUMN_ALARM_GROUP, alarmRecord.group);
			values.put(COLUMN_ALARM_ITEM, alarmRecord.item);
			values.put(COLUMN_ALARM_Status, alarmRecord.alarmStatus);
			try {
				isResut = myDatabase.insertOrThrow(TABLE_NAME, null, values);
			} catch (SQLiteConstraintException e) {
				e.printStackTrace();
			}
		}

		return isResut;
	}

	public long inserts(ArrayList<AlarmRecord> alarmRecords) {
		long isResut = -1;
		if (alarmRecords.size() > 0) {
			myDatabase.beginTransaction();
			try {
				for (AlarmRecord alarmRecord : alarmRecords) {
					if (alarmRecord != null) {
						ContentValues values = new ContentValues();
						values.put(COLUMN_DEVICEID, alarmRecord.deviceId);
						values.put(COLUMN_ACTIVE_USER, alarmRecord.activeUser);
						values.put(COLUMN_ALARM_TYPE, alarmRecord.alarmType);
						values.put(COLUMN_ALARM_TIME, alarmRecord.alarmTime);
						values.put(COLUMN_ALARM_GROUP, alarmRecord.group);
						values.put(COLUMN_ALARM_ITEM, alarmRecord.item);
						values.put(COLUMN_ALARM_Status, alarmRecord.alarmStatus);
						try {
							isResut = myDatabase.insertOrThrow(TABLE_NAME,
									null, values);
						} catch (SQLiteConstraintException e) {
							e.printStackTrace();
						}
					}
				}
				myDatabase.setTransactionSuccessful();
			
			} catch (Exception e) {

				
			}
			finally
			{
				myDatabase.endTransaction();
				
			}
		}

		return isResut;
	}

	private java.util.Date Str2Date(String datestr, String format) {
		if (format == null || format.equals("")) {
			format = "yyyy-MM-dd HH:mm";
		}
		java.util.Date date;
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

		if (datestr.indexOf(":") < 0) {
			datestr += " 00:00";
		}
		try {
			date = sdf.parse(datestr);
		} catch (ParseException e) {

			date = new java.util.Date();
		}

		return date;
	}

	private String contactString = " and (select count(1) from "
			+ ContactDB.TABLE_NAME + " where  " + ContactDB.COLUMN_CONTACT_ID
			+ "=" + TABLE_NAME + "." + COLUMN_DEVICEID + ")>0 ";

	public List<AlarmRecord> findByActiveUserId(String activeUserId) {
		List<AlarmRecord> lists = new ArrayList<AlarmRecord>();
		Cursor cursor = null;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
				+ COLUMN_ACTIVE_USER + "=? " + contactString,
				new String[] { activeUserId });
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
				int status = cursor.getInt(cursor
						.getColumnIndex(COLUMN_ALARM_Status));
				AlarmRecord data = new AlarmRecord();
				data.id = id;
				data.deviceId = deviceId;
				data.alarmType = alarmType;
				data.alarmTime = alarmTime;
				data.activeUser = activeUser;
				data.group = group;
				data.item = item;
				data.alarmStatus = status;
				lists.add(data);
			}
			cursor.close();
		}
		return lists;
	}

	public Boolean Exists(String activeUserId, String dvid, int atype,
			String alartime, int g, int i) {
		Cursor cursor = null;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
				+ COLUMN_ACTIVE_USER + "='" + activeUserId + "' and "
				+ COLUMN_DEVICEID + "='" + dvid + "' and " + COLUMN_ALARM_TYPE
				+ "='" + atype + "' and ( ( julianday('" + alartime
				+ "') -  julianday(datetime(substr(" + COLUMN_ALARM_TIME
				+ ",1,10), 'unixepoch', 'localtime')))*24*60*60<10 and ( julianday('" + alartime
				+ "') -  julianday(datetime(substr(" + COLUMN_ALARM_TIME
				+ ",1,10), 'unixepoch', 'localtime')))*24*60*60>-10  ) and "
				+ COLUMN_ALARM_GROUP + "='" + g + "' and " + COLUMN_ALARM_ITEM
				+ "='" + i + "' " + contactString, new String[] {});
		if (cursor != null) {
			return cursor.moveToNext();
		}
		return false;
	}

	public List<AlarmRecord> findUnReadByActiveUserId(String activeUserId) {
		List<AlarmRecord> lists = new ArrayList<AlarmRecord>();
		Cursor cursor = null;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
				+ COLUMN_ALARM_Status + "=0 and " + COLUMN_ACTIVE_USER + "=? "
				+ contactString, new String[] { activeUserId });
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
				int status = cursor.getInt(cursor
						.getColumnIndex(COLUMN_ALARM_Status));
				AlarmRecord data = new AlarmRecord();
				data.id = id;
				data.deviceId = deviceId;
				data.alarmType = alarmType;
				data.alarmTime = alarmTime;
				data.activeUser = activeUser;
				data.group = group;
				data.item = item;
				data.alarmStatus = status;
				lists.add(data);
			}
			cursor.close();
		}
		return lists;
	}

	public List<AlarmRecord> findByDeviceId(String activeUserId, String dvid) {
		List<AlarmRecord> lists = new ArrayList<AlarmRecord>();
		Cursor cursor = null;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
				+ COLUMN_DEVICEID + "='" + dvid + "' and " + COLUMN_ACTIVE_USER
				+ "=? " + contactString, new String[] { activeUserId });
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
				int status = cursor.getInt(cursor
						.getColumnIndex(COLUMN_ALARM_Status));
				AlarmRecord data = new AlarmRecord();
				data.id = id;
				data.deviceId = deviceId;
				data.alarmType = alarmType;
				data.alarmTime = alarmTime;
				data.activeUser = activeUser;
				data.group = group;
				data.item = item;
				data.alarmStatus = status;
				lists.add(data);
			}
			cursor.close();
		}
		return lists;
	}

	public List<AlarmRecord> findUnReadByDeviceId(String activeUserId,
			String dvid) {
		List<AlarmRecord> lists = new ArrayList<AlarmRecord>();
		Cursor cursor = null;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
				+ COLUMN_ALARM_Status + "=0 and " + COLUMN_DEVICEID + "='"
				+ dvid + "' and " + COLUMN_ACTIVE_USER + "=? " + contactString,
				new String[] { activeUserId });
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
				int status = cursor.getInt(cursor
						.getColumnIndex(COLUMN_ALARM_Status));
				AlarmRecord data = new AlarmRecord();
				data.id = id;
				data.deviceId = deviceId;
				data.alarmType = alarmType;
				data.alarmTime = alarmTime;
				data.activeUser = activeUser;
				data.group = group;
				data.item = item;
				data.alarmStatus = status;
				lists.add(data);
			}
			cursor.close();
		}
		return lists;
	}

	public List<AlarmRecord> findByGroup(String activeUserId, String dvid) {
		List<AlarmRecord> lists = new ArrayList<AlarmRecord>();
		List<AlarmRecord> lists2 = new ArrayList<AlarmRecord>();
		Cursor cursor = null;
		cursor = myDatabase
				.rawQuery(
						"SELECT strftime('%Y-%m-%d',datetime(substr("
								+ COLUMN_ALARM_TIME
								+ " ,1,10), 'unixepoch', 'localtime')) as "
								+ COLUMN_ALARM_TIME
								+ " FROM "
								+ TABLE_NAME
								+ " WHERE "
								+ (dvid == null ? "" : COLUMN_DEVICEID + "='"
										+ dvid + "' and ")
								+ COLUMN_ACTIVE_USER
								+ "=?  "
								+ contactString
								+ " group by strftime('%Y-%m-%d',datetime(substr("
								+ COLUMN_ALARM_TIME
								+ " ,1,10), 'unixepoch', 'localtime')) order by  strftime('%Y-%m-%d',datetime(substr("
								+ COLUMN_ALARM_TIME
								+ " ,1,10), 'unixepoch', 'localtime')) desc",
						new String[] { activeUserId });
//		String[] names = cursor.getColumnNames();
//		for (int i = 0; i < names.length; i++) {
//			Log.e("343", "这个列名是："+names[i]);
//		}
		if (cursor != null) {
			while (cursor.moveToNext()) {
				int id = 0;
				String deviceId = "";
				int alarmType = 0;
				String alarmTime = cursor.getString(0);
				String activeUser = "";
				int group = 0;
				int item = 0;
				int status = 0;

				AlarmRecord data = new AlarmRecord();
				data.id = id;
				data.deviceId = deviceId;
				data.alarmType = alarmType;
				data.alarmTime = alarmTime;
				data.activeUser = activeUser;
				data.group = group;
				data.item = item;
				data.alarmStatus = status;
				
				lists2.add(data);
			}
			cursor.close();
		}

		for (AlarmRecord alarmRecord : lists2) {
			lists.add(alarmRecord);
			cursor = null;
			cursor = myDatabase.rawQuery("SELECT * FROM "
					+ TABLE_NAME
					+ " WHERE "
					+ "strftime('%Y-%m-%d',datetime(substr("
					+ COLUMN_ALARM_TIME
					+ " ,1,10), 'unixepoch', 'localtime'))='"
					+ alarmRecord.alarmTime
					+ "' and "
					+ (dvid == null ? "" : COLUMN_DEVICEID + "='" + dvid
							+ "' and ") + COLUMN_ACTIVE_USER + "=?  "
					+ contactString + " order by  " + COLUMN_ALARM_TIME
					+ " desc", new String[] { activeUserId });
			java.util.Date dtme = Str2Date(alarmRecord.alarmTime, null);
			Calendar calDateA = Calendar.getInstance();
			calDateA.setTime(dtme);
			Calendar calDateB = Calendar.getInstance();
			if (calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
					&& calDateA.get(Calendar.MONTH) == calDateB
							.get(Calendar.MONTH)) {
				if (calDateA.get(Calendar.DAY_OF_MONTH) == calDateB
						.get(Calendar.DAY_OF_MONTH)) {
					alarmRecord.alarmTime = "今天";
				} else if (calDateA.get(Calendar.DAY_OF_MONTH) == calDateB
						.get(Calendar.DAY_OF_MONTH) - 1) {
					alarmRecord.alarmTime = "昨天";
				}
			}
			if (cursor != null) {
				while (cursor.moveToNext()) {
					int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
					String deviceId = cursor.getString(cursor
							.getColumnIndex(COLUMN_DEVICEID));
					int alarmType = cursor.getInt(cursor
							.getColumnIndex(COLUMN_ALARM_TYPE));
					String alarmTime = cursor.getString(cursor
							.getColumnIndex(COLUMN_ALARM_TIME));
					alarmTime = FormatDate(
							new java.util.Date(Long.parseLong(alarmTime)),
							"yyyy-MM-dd HH:mm:ss");
					String activeUser = cursor.getString(cursor
							.getColumnIndex(COLUMN_ACTIVE_USER));
					int group = cursor.getInt(cursor
							.getColumnIndex(COLUMN_ALARM_GROUP));
					int item = cursor.getInt(cursor
							.getColumnIndex(COLUMN_ALARM_ITEM));
					int status = cursor.getInt(cursor
							.getColumnIndex(COLUMN_ALARM_Status));
					AlarmRecord data = new AlarmRecord();
					data.id = id;
					data.deviceId = deviceId;
					data.alarmType = alarmType;
					data.alarmTime = alarmTime;
					data.activeUser = activeUser;
					data.group = group;
					data.item = item;
					data.alarmStatus = status;
					lists.add(data);
				}
				cursor.close();
			}
		}
		return lists;
	}

	public int deleteByActiveUser(String activeUserId) {
		return myDatabase.delete(TABLE_NAME, COLUMN_ACTIVE_USER + "=?",
				new String[] { activeUserId });
	}

	public int deleteById(int id) {
		return myDatabase.delete(TABLE_NAME, COLUMN_ID + "=?",
				new String[] { String.valueOf(id) });
	}

	public int deleteByDeviceId(String activeUserId, String dvid) {
		return myDatabase.delete(
				TABLE_NAME,
				COLUMN_ACTIVE_USER + "=?" + " and " + COLUMN_DEVICEID + "=?",
				new String[] { String.valueOf(activeUserId),
						String.valueOf(dvid) });
	}

	private String FormatDate(java.util.Date date, String format) {
		if (format == null || format.equals("")) {
			format = "yyyy-MM-dd HH:mm";
		}

		return new SimpleDateFormat(format, Locale.getDefault()).format(date);
	}

	public int updateReadById(int id) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_ALARM_Status, 1);
		try {
			return myDatabase.update(TABLE_NAME, values, COLUMN_ID + "=?",
					new String[] { String.valueOf(id) });
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public int updateRead(String  deviceId) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_ALARM_Status, 1);
		try {
			return myDatabase.update(TABLE_NAME, values, deviceId == null ? "1=1" : COLUMN_DEVICEID + "=?",
					 deviceId == null ?new String[] {}:	new String[] { String.valueOf(deviceId) });
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
