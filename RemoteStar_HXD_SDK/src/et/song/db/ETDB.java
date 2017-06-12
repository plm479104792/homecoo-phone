package et.song.db;

import java.lang.reflect.Method;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class ETDB {

	private DBHelper dbHelper;
	public static ETDB instance = null;
	private SQLiteDatabase sqliteDatabase;

	public class DBHelper extends SQLiteOpenHelper {
		private static final int DATABASE_VERSION = 8;

		private final static String TABLE_GROUP_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ DBProfile.GROUP_TABLE_NAME
				+ " ("
				+ DBProfile.TABLE_GROUP_FIELD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBProfile.TABLE_GROUP_FIELD_NAME
				+ " TEXT,"
				+ DBProfile.TABLE_GROUP_FIELD_DEVID
				+ " TEXT,"
				+ DBProfile.TABLE_GROUP_FIELD_GATEWAYID
				+ " TEXT,"
				+ DBProfile.TABLE_GROUP_FIELD_TYPE
				+ " INTEGER,"
				+ DBProfile.TABLE_GROUP_FIELD_RES + " INTEGER" + ");";

		private final static String TABLE_DEVICE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ DBProfile.DEVICE_TABLE_NAME
				+ " ("
				+ DBProfile.TABLE_DEVICE_FIELD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBProfile.TABLE_DEVICE_FIELD_GROUP_ID
				+ " INTEGER,"
				+ DBProfile.TABLE_DEVICE_FIELD_NAME
				+ " TEXT,"
				+ DBProfile.TABLE_DEVICE_FIELD_TYPE
				+ " INTEGER,"
				+ DBProfile.TABLE_DEVICE_FIELD_RES + " INTEGER"
				// + DBProfile.TABLE_DEVICE_FIELD_INDEX
				// + " INTEGER,"
				// + DBProfile.TABLE_DEVICE_FIELD_POS
				// + " INTEGER"
				+ ");";

		private final static String TABLE_KEY_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ DBProfile.KEY_TABLE_NAME
				+ " ("
				+ DBProfile.TABLE_KEY_FIELD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBProfile.TABLE_KEY_FIELD_DEVICE_ID
				+ " INTEGER,"
				+ DBProfile.TABLE_KEY_FIELD_NAME
				+ " TEXT,"
				+ DBProfile.TABLE_KEY_FIELD_RES
				+ " INTEGER,"
				+ DBProfile.TABLE_KEY_FIELD_X
				+ " FLOAT,"
				+ DBProfile.TABLE_KEY_FIELD_Y
				+ " FLOAT,"
				+ DBProfile.TABLE_KEY_FIELD_KEYVALUE
				+ " TEXT,"
				+ DBProfile.TABLE_KEY_FIELD_KEY
				+ " INTEGER,"
				+ DBProfile.TABLE_KEY_FIELD_BRANDINDEX
				+ " INTEGER,"
				+ DBProfile.TABLE_KEY_FIELD_BRANDPOS
				+ " INTEGER,"
				+ DBProfile.TABLE_KEY_FIELD_ROW
				+ " INTEGER,"
				+ DBProfile.TABLE_KEY_FIELD_STATE + " INTEGER" + ");";

		private final static String TABLE_KEYEX_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ DBProfile.KEYEX_TABLE_NAME
				+ " ("
				+ DBProfile.TABLE_KEYEX_FIELD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBProfile.TABLE_KEYEX_FIELD_DEVICE_ID
				+ " INTEGER,"
				+ DBProfile.TABLE_KEYEX_FIELD_NAME
				+ " TEXT,"
				+ DBProfile.TABLE_KEYEX_FIELD_KEYVALUE
				+ " TEXT,"
				+ DBProfile.TABLE_KEYEX_FIELD_KEY
				+ " INTEGER" + ");";
		
		private final static String TABLE_WIFIDEVICE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ DBProfile.WIFIDEVICE_TABLE_NAME
				+ " ("
				+ DBProfile.TABLE_WIFIDEVICE_FIELD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBProfile.TABLE_WIFIDEVICE_FIELD_NAME
				+ " TEXT,"
				+ DBProfile.TABLE_WIFIDEVICE_FIELD_UID
				+ " TEXT,"
				+ DBProfile.TABLE_WIFIDEVICE_FIELD_SSID
				+ " TEXT,"
				+ DBProfile.TABLE_WIFIDEVICE_FIELD_PWD
				+ " TEXT,"
				+ DBProfile.TABLE_WIFIDEVICE_FIELD_WAN
				+ " INTEGER,"
				+ DBProfile.TABLE_WIFIDEVICE_FIELD_IP
				+ " TEXT,"
				+ DBProfile.TABLE_WIFIDEVICE_FIELD_PORT
				+ " INTEGER,"
				+ DBProfile.TABLE_WIFIDEVICE_FIELD_TYPE
				+ " INTEGER,"
				+ DBProfile.TABLE_WIFIDEVICE_FIELD_RES + " INTEGER" + ");";
		
		private final static String TABLE_WIFIDIRECT_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ DBProfile.WIFIDIRECT_TABLE_NAME
				+ " ("
				+ DBProfile.TABLE_WIFIDIRECT_FIELD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBProfile.TABLE_WIFIDIRECT_FIELD_IP
				+ " TEXT,"
				+ DBProfile.TABLE_WIFIDIRECT_FIELD_PORT
				+ " INTEGER,"
				+ DBProfile.TABLE_WIFIDIRECT_FIELD_RES + " INTEGER" + ");";

		private final static String TABLE_AIRDEVICE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ DBProfile.AIRDEVICE_TABLE_NAME
				+ " ("
				+ DBProfile.TABLE_AIRDEVICE_FIELD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBProfile.TABLE_AIRDEVICE_FIELD_DEVICE_ID
				+ " INTEGER,"
				+ DBProfile.TABLE_AIRDEVICE_FIELD_TEMP
				+ " INTEGER,"
				+ DBProfile.TABLE_AIRDEVICE_FIELD_RATE
				+ " INTEGER,"
				+ DBProfile.TABLE_AIRDEVICE_FIELD_DIR
				+ " INTEGER,"
				+ DBProfile.TABLE_AIRDEVICE_FIELD_AUTO_DIR
				+ " INTEGER,"
				+ DBProfile.TABLE_AIRDEVICE_FIELD_MODE
				+ " INTEGER,"
				+ DBProfile.TABLE_AIRDEVICE_FIELD_POWER + " INTEGER" + ");";

		private final static String TABLE_WATCHTV_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ DBProfile.WATCHTV_TABLE_NAME
				+ " ("
				+ DBProfile.TABLE_WATCHTV_FIELD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBProfile.TABLE_WATCHTV_FIELD_DEVICE_ID
				+ " INTEGER,"
				+ DBProfile.TABLE_WATCHTV_FIELD_NAME
				+ " TEXT,"
				+ DBProfile.TABLE_WATCHTV_FIELD_CONTEXT
				+ " TEXT,"
				+ DBProfile.TABLE_WATCHTV_FIELD_RES
				+ " INTEGER,"
				+ DBProfile.TABLE_WATCHTV_FIELD_VALUE
				+ " INTEGER,"
				+ DBProfile.TABLE_WATCHTV_FIELD_ISOK + " INTEGER" + ");";
		
		private final static String TABLE_WATCHTV_UPDATE = "ALTER TABLE "
				+ DBProfile.WATCHTV_TABLE_NAME + " ADD COLUMN "
				+ DBProfile.TABLE_WATCHTV_FIELD_ISSELECT + " INTEGER" + ";";
		
		private final static String TABLE_WATCHTV_UPDATE_1 = "ALTER TABLE "
				+ DBProfile.WATCHTV_TABLE_NAME + " ADD COLUMN "
				+ DBProfile.TABLE_WATCHTV_FIELD_VALUE_EX + " TEXT" + ";";
		
		private final static String TABLE_WATCHTV_DELETE = "DROP TABLE IF EXISTS " 
				+ DBProfile.WATCHTV_TABLE_NAME  + ";";
		
		private final static String TABLE_GROUP_DELETE = "DROP TABLE IF EXISTS " 
				+ DBProfile.GROUP_TABLE_NAME  + ";";
		
		DBHelper(Context context) {
			super(context, DBProfile.DBNAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_GROUP_CREATE);
			db.execSQL(TABLE_DEVICE_CREATE);
			db.execSQL(TABLE_KEY_CREATE);
			db.execSQL(TABLE_KEYEX_CREATE);
			db.execSQL(TABLE_WIFIDEVICE_CREATE);
			db.execSQL(TABLE_WIFIDIRECT_CREATE);
			db.execSQL(TABLE_AIRDEVICE_CREATE);
			db.execSQL(TABLE_WATCHTV_CREATE);
			db.execSQL(TABLE_WATCHTV_UPDATE);
			db.execSQL(TABLE_WATCHTV_UPDATE_1);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion < 5){
				db.execSQL(TABLE_WATCHTV_DELETE);
				db.execSQL(TABLE_WATCHTV_CREATE);
				db.execSQL(TABLE_WATCHTV_UPDATE);
				db.execSQL(TABLE_WATCHTV_UPDATE_1);
				oldVersion = 5;
			}
			if (oldVersion < 6){
				db.execSQL(TABLE_WIFIDIRECT_CREATE);
				oldVersion = 6;
			}
			if (oldVersion < 7){
				db.execSQL(TABLE_KEYEX_CREATE);
				oldVersion = 7;
			}
			
			if (oldVersion<8) {
				db.execSQL(TABLE_GROUP_DELETE);
				db.execSQL(TABLE_GROUP_CREATE);
				oldVersion=8;
			}
		}
	}

	private ETDB(Context context) {
		dbHelper = new DBHelper(context);
		sqliteDatabase = dbHelper.getReadableDatabase();
	}

	/***
	 * ��ȡ�������ʵ��
	 * 
	 * @param context
	 *            �����Ķ���
	 * @return
	 */
	public static final ETDB getInstance(Context context) {
		if (instance == null)
			instance = new ETDB(context);
		return instance;
	}

	/**
	 * �ر����ݿ�
	 */
	public void close() {
		if (sqliteDatabase.isOpen())
			sqliteDatabase.close();
		if (dbHelper != null)
			dbHelper.close();
		if (instance != null)
			instance = null;
	}

	/**
	 * ��������
	 * 
	 * @param sql
	 *            ִ�и��²�����sql���
	 * @param bindArgs
	 *            sql����еĲ���,������˳���Ӧռλ��˳��
	 * @return result ���������¼���кţ�������id�޹�
	 */
	public Long insertDataBySql(String sql, String[] bindArgs) throws Exception {
		long result = 0;
		if (sqliteDatabase.isOpen()) {
			SQLiteStatement statement = sqliteDatabase.compileStatement(sql);
			if (bindArgs != null) {
				int size = bindArgs.length;
				for (int i = 0; i < size; i++) {
					// ��������ռλ���󶨣���Ӧ
					statement.bindString(i + 1, bindArgs[i]);
				}
				result = statement.executeInsert();
				statement.close();
			}
		} else {
			Log.i("info", "���ݿ��ѹر�");
		}
		return result;
	}

	/**
	 * ��������
	 * 
	 * @param table
	 *            ����
	 * @param values
	 *            Ҫ���������
	 * @return result ���������¼���кţ�������id�޹�
	 */
	public Long insertData(String table, ContentValues values) {
		long result = 0;
		if (sqliteDatabase.isOpen()) {
			result = sqliteDatabase.insert(table, null, values);
		}
		return result;
	}

	/**
	 * ��������
	 * 
	 * @param sql
	 *            ִ�и��²�����sql���
	 * @param bindArgs
	 *            sql����еĲ���,������˳���Ӧռλ��˳��
	 */
	public void updateDataBySql(String sql, String[] bindArgs) throws Exception {
		if (sqliteDatabase.isOpen()) {
			SQLiteStatement statement = sqliteDatabase.compileStatement(sql);
			if (bindArgs != null) {
				int size = bindArgs.length;
				for (int i = 0; i < size; i++) {
					statement.bindString(i + 1, bindArgs[i]);
				}
				statement.execute();
				statement.close();
			}
		} else {
			Log.i("info", "���ݿ��ѹر�");
		}
	}

	/**
	 * ��������
	 * 
	 * @param table
	 *            ����
	 * @param values
	 *            ��ʾ���µ�����
	 * @param whereClause
	 *            ��ʾSQL������������ֵ����
	 * @param whereArgs
	 *            ��ʾռλ����ֵ
	 * @return
	 */
	public int updataData(String table, ContentValues values,
			String whereClause, String[] whereArgs) {
		int result = 0;
		if (sqliteDatabase.isOpen()) {
			result = sqliteDatabase.update(table, values, whereClause,
					whereArgs);
		}
		return result;
	}

	/**
	 * ɾ������
	 * 
	 * @param sql
	 *            ִ�и��²�����sql���
	 * @param bindArgs
	 *            sql����еĲ���,������˳���Ӧռλ��˳��
	 */
	public void deleteDataBySql(String sql, String[] bindArgs) throws Exception {
		if (sqliteDatabase.isOpen()) {
			SQLiteStatement statement = sqliteDatabase.compileStatement(sql);
			if (bindArgs != null) {
				int size = bindArgs.length;
				for (int i = 0; i < size; i++) {
					statement.bindString(i + 1, bindArgs[i]);
				}
				Method[] mm = statement.getClass().getDeclaredMethods();
				for (Method method : mm) {
					Log.i("info", method.getName());
					/**
					 * ����鿴�Ƿ��ܻ�ȡexecuteUpdateDelete���� �鿴Դ���֪
					 * executeUpdateDelete��public�ķ��������Ǻ������������Բ��ܱ����ã�
					 * ���÷���ò��ֻ����root�Ժ�Ļ����ϲ��ܵ��ã�С���ǿ��ԣ���������ȴ���У����Ի��ǲ����á�
					 */
				}
				statement.execute();
				statement.close();
			}
		} else {
			Log.i("info", "���ݿ��ѹر�");
		}
	}

	/**
	 * ɾ������
	 * 
	 * @param table
	 *            ����
	 * @param whereClause
	 *            ��ʾSQL������������ֵ����
	 * @param whereArgs
	 *            ��ʾռλ����ֵ
	 * @return
	 */
	public int deleteData(String table, String whereClause, String[] whereArgs) {
		int result = 0;
		if (sqliteDatabase.isOpen()) {
			result = sqliteDatabase.delete(table, whereClause, whereArgs);
		}
		return result;
	}

	/**
	 * ��ѯ����
	 * 
	 * @param searchSQL
	 *            ִ�в�ѯ������sql���
	 * @param selectionArgs
	 *            ��ѯ����
	 * @return ���ز�ѯ���α꣬�ɶ��������в�������Ҫ�Լ��ر��α�
	 */
	public Cursor queryData2Cursor(String sql, String[] selectionArgs)
			throws Exception {
		if (sqliteDatabase.isOpen()) {
			Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);
			if (cursor != null) {
				// cursor.moveToFirst();

				return cursor;
			}
		}
		return null;
	}

	public void exe(String _sql) {
		sqliteDatabase.execSQL(_sql);
	}

}