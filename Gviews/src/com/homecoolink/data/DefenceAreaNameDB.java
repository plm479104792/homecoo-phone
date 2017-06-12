package com.homecoolink.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DefenceAreaNameDB {
	public static final String TABLE_NAME = "defenceareaname";

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_ID_DATA_TYPE = "integer PRIMARY KEY AUTOINCREMENT";
	
	public static final String COLUMN_GROUPI = "groupI";
	public static final String COLUMN_GROUPI_DATA_TYPE = "varchar";

	public static final String COLUMN_GROUPJ = "groupJ";
	public static final String COLUMN_GROUPJ_DATA_TYPE = "varchar";

	public static final String COLUMN_GROUPIJ = "groupIJ";
	public static final String COLUMN_GROUPIJ_DATA_TYPE = "varchar";

	public static final String COLUMN_GROUPNAME = "groupName";
	public static final String COLUMN_GROUPNAME_DATA_TYPE = "varchar";

	private SQLiteDatabase myDatabase;

	public DefenceAreaNameDB(SQLiteDatabase myDatabase) {
		this.myDatabase = myDatabase;
	}

	public static String getDeleteTableSQLString() {
		return SqlHelper.formDeleteTableSqlString(TABLE_NAME);
	}

	public static String getCreateTableString() {
		HashMap<String, String> columnNameAndType = new HashMap<String, String>();
		columnNameAndType.put(COLUMN_GROUPI, COLUMN_GROUPI_DATA_TYPE);
		columnNameAndType.put(COLUMN_GROUPJ, COLUMN_GROUPJ_DATA_TYPE);
		columnNameAndType.put(COLUMN_GROUPNAME, COLUMN_GROUPNAME_DATA_TYPE);
		columnNameAndType.put(COLUMN_GROUPIJ, COLUMN_GROUPIJ_DATA_TYPE);

		String mSQLCreateWeiboInfoTable = SqlHelper.formCreateTableSqlString(
				TABLE_NAME, columnNameAndType);
		return mSQLCreateWeiboInfoTable;
	}

	public long insert(DefenceAreaName dan) {
		long isResut = -1;
		if (dan != null) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_GROUPI, dan.groupI);
			values.put(COLUMN_GROUPJ, dan.groupJ);
			values.put(COLUMN_GROUPNAME, dan.groupName);
			values.put(COLUMN_GROUPIJ, dan.groupIJ);
			try {
				isResut = myDatabase.insertOrThrow(TABLE_NAME, null, values);
				
			} catch (SQLiteConstraintException e) {
				e.printStackTrace();
			}
		}
		return isResut;
	}

	public void delete(String groupIJ) {
		myDatabase.delete(TABLE_NAME, COLUMN_GROUPIJ + "=?",
				new String[] { groupIJ });
	}

	public void update(DefenceAreaName dan) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_GROUPI, dan.groupI);
		values.put(COLUMN_GROUPJ, dan.groupJ);
		values.put(COLUMN_GROUPNAME, dan.groupName);
		values.put(COLUMN_GROUPIJ, dan.groupIJ);
		try {// mDBStore.insertOrThrow(TABLE_NAME, null, newInfoValues);
			myDatabase.update(
					TABLE_NAME,
					values,
					COLUMN_GROUPI + "=? and " + COLUMN_GROUPJ + "=? ",
					new String[] { String.valueOf(dan.groupI),
							String.valueOf(dan.groupJ) });
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

	}

	public String GetMinItem(String groupI) {
		String isResut;
		Cursor cursor;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
				+ COLUMN_GROUPI + "=? and " + COLUMN_GROUPIJ + " = 1 order by "
				+ COLUMN_GROUPJ + " asc",
				new String[] { String.valueOf(groupI) });
		if (cursor == null) {
			isResut = "-1"; 
		} else {
			if (cursor.moveToNext()) {

				isResut = cursor
						.getString(cursor.getColumnIndex(COLUMN_GROUPJ));
			} else {
				
				isResut = "-1";
			}
		}
		return isResut;
	}

	public String check(String groupIJ) {
		String isResut;
		Cursor cursor;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
				+ COLUMN_GROUPIJ + "=?",
				new String[] { String.valueOf(groupIJ) });
		if (cursor == null) {
			isResut = "-1"; 
		} else {
			cursor.moveToNext();
			isResut = cursor.getString(cursor.getColumnIndex(COLUMN_GROUPJ));
		}
		return isResut;
	}

	public List<DefenceAreaName> findDefenceAreaNameAll() {
		List<DefenceAreaName> lists = new ArrayList<DefenceAreaName>();
		Cursor cursor = null;
		cursor = myDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);

		if (cursor != null) {
			
			while (cursor.moveToNext()) {
				String groupI = cursor.getString(cursor
						.getColumnIndex(COLUMN_GROUPI));

				String groupJ = cursor.getString(cursor
						.getColumnIndex(COLUMN_GROUPJ));

				String groupIJ = cursor.getString(cursor
						.getColumnIndex(COLUMN_GROUPIJ));

				String groupName = cursor.getString(cursor
						.getColumnIndex(COLUMN_GROUPNAME));

				DefenceAreaName data = new DefenceAreaName();
				data.groupI = groupI;
				data.groupJ = groupJ;
				data.groupName = groupName;
				data.groupIJ = groupIJ;

				lists.add(data);
			}
			cursor.close();
		}
		return lists;
	}

}
