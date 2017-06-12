package object.p2pipcam.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
	private Context context;
	private String TAG = "DataBaseHelper";
	/**
	 * Database Name
	 */
	private static final String DATABASE_NAME = "p2p_camera_database";

	/**
	 * Database Version
	 */
	private static final int DATABASE_VERSION = 3;
	private static DataBaseHelper dbHelper;
	private static SQLiteDatabase db;
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "cameralist";// 摄像机列表数据库
	private static final String DATABASW_VIDEOPICTURE_TABLE = "cameravidpic";// 拍照后存储的数据库
	private static final String DATABASE_ALARMLOG_TABLE = "alarmlog";// 报警日志的数据库
	private static final String DATABASE_FIRSTPIC_TABLE = "firstpic";
	/**
	 * Table columns
	 */
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_USER = "user";
	public static final String KEY_PWD = "pwd";
	public static final String KEY_DID = "did";

	public static final String KEY_FILEPATH = "filepath";
	public static final String KEY_CREATETIME = "createtime";
	public static final String KEY_TYPE = "type";

	public static final String KEY_ALARMLOG_CONTENT = "content";
	/**
	 * save video or picture to video_picture_table type
	 * */
	public static final String TYPE_VIDEO = "video";
	public static final String TYPE_PICTURE = "picture";

	/**
	 * create firstpic_table sql statement
	 * 
	 * **/
	private static final String CREATE_FIRSTPIC_TABLE = "create table "
			+ DATABASE_FIRSTPIC_TABLE + "(" + KEY_ID
			+ " integer primary key autoincrement," + KEY_DID
			+ " text not null, " + KEY_FILEPATH + " text not null)";

	/**
	 * create alarmlog_table sql statement
	 * 
	 * **/
	private static final String CREATE_ALARMLOG_TABLE = "create table "
			+ DATABASE_ALARMLOG_TABLE + "(" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_DID
			+ " text not null, " + KEY_ALARMLOG_CONTENT + " text not null, "
			+ KEY_CREATETIME + " text not null);";
	/**
	 * create video_picture_table sql statement
	 * **/
	private static final String CREATE_VIDEO_PICTURE_TABLE = "create table "
			+ DATABASW_VIDEOPICTURE_TABLE + "(" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_DID
			+ " text not null, " + KEY_FILEPATH + " text not null, "
			+ KEY_CREATETIME + " text not null, " + KEY_TYPE
			+ " text not null);";

	/**
	 * Database creation sql statement
	 */
	private static final String CREATE_STUDENT_TABLE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_DID + " text not null, " + KEY_USER
			+ " text not null," + KEY_PWD + " text);";

	public static DataBaseHelper getInstance(Context ctx) {
		if (dbHelper == null) {
			dbHelper = new DataBaseHelper(ctx);
			db = dbHelper.getWritableDatabase();
		}
		return dbHelper;
	}

	private DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Creating student_table: " + CREATE_STUDENT_TABLE);
		Log.i(TAG, "Creating Video_Picture_Table: "
				+ CREATE_VIDEO_PICTURE_TABLE);
		Log.i(TAG, "Creating alarmlog_table: " + CREATE_ALARMLOG_TABLE);
		Log.i(TAG, "Creating Firstpic_table" + CREATE_FIRSTPIC_TABLE);
		db.execSQL(CREATE_STUDENT_TABLE);
		db.execSQL(CREATE_VIDEO_PICTURE_TABLE);
		db.execSQL(CREATE_ALARMLOG_TABLE);
		db.execSQL(CREATE_FIRSTPIC_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	public void close() {
		dbHelper = null;
		db.close();
	}

	/**
	 * This method is used to create/insert new record record.
	 * 
	 * @param name
	 * @param did
	 * @param user
	 * @param pwd
	 * @return
	 */
	public long createCamera(String name, String did, String user, String pwd) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_DID, did);
		initialValues.put(KEY_USER, user);
		initialValues.put(KEY_PWD, pwd);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * This method will delete record.
	 * 
	 * @param rowId
	 * @return boolean
	 */
	public boolean deleteCamera(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
	}

	/**
	 * 删除摄像机的所有信息
	 * */
	public boolean deleteCamera(String did) {
		// 删除第一张图片在数据库中的记录
		int count1 = db.delete(DATABASE_FIRSTPIC_TABLE, KEY_DID + "='" + did
				+ "'", null);
		// 删除图片在数据库中的记录
		int count2 = db.delete(DATABASW_VIDEOPICTURE_TABLE, KEY_DID + "='"
				+ did + "'", null);
		// 删除报警日志在数据库中的记录
		int delete = db.delete(DATABASE_ALARMLOG_TABLE, KEY_DID + "='" + did
				+ "'", null);
		// 删除摄像机在数据库中的记录
		return db.delete(DATABASE_TABLE, KEY_DID + "='" + did + "'", null) > 0;
	}

	/**
	 * This method will return Cursor holding all the records.
	 * 
	 * @return Cursor
	 */
	public Cursor fetchAllCameras() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_NAME,
				KEY_DID, KEY_USER, KEY_PWD }, null, null, null, null, null);
	}

	/**
	 * This method will return Cursor holding the specific record.
	 * 
	 * @param id
	 * @return Cursor
	 * @throws SQLException
	 */
	public Cursor fetchCamera(long id) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ID,
				KEY_NAME, KEY_DID, KEY_USER, KEY_PWD }, KEY_ID + "=" + id,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * 
	 * @param oldaddr
	 * @param oldport
	 * @param name
	 * @param addr
	 * @param port
	 * @param user
	 * @param pwd
	 * @return
	 */
	public boolean updateCamera(String oldDID, String name, String did,
			String user, String pwd) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_DID, did);
		args.put(KEY_USER, user);
		args.put(KEY_PWD, pwd);
		return db.update(DATABASE_TABLE, args, KEY_DID + "='" + oldDID + "'",
				null) > 0;
	}

	public boolean updateCameraUser(String did, String username, String pwd) {
		ContentValues values = new ContentValues();
		values.put(KEY_USER, username);
		values.put(KEY_PWD, pwd);
		return db.update(DATABASE_TABLE, values, KEY_DID + "='" + did + "'",
				null) > 0;

	}

	/**
	 * This Method is used to create/insert new record
	 * **/
	public long createVideoOrPic(String did, String filepath, String type,
			String createtime) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DID, did);
		initialValues.put(KEY_FILEPATH, filepath);
		initialValues.put(KEY_TYPE, type);
		initialValues.put(KEY_CREATETIME, createtime);
		return db.insert(DATABASW_VIDEOPICTURE_TABLE, null, initialValues);
	}

	/**
	 * This Method is used to query all video record from video_picture_table
	 * **/
	public Cursor queryAllVideo(String did) {
		String sql = "select * from " + DATABASW_VIDEOPICTURE_TABLE
				+ " where  " + KEY_TYPE + "='" + TYPE_VIDEO + "' and "
				+ KEY_DID + "='" + did + "' order by " + KEY_FILEPATH + " desc";
		return db.rawQuery(sql, null);
	}

	/**
	 * This Method is used to query all video record from video_picture_table
	 * **/
	public Cursor queryAllPicture(String did) {
		String sql = "select * from " + DATABASW_VIDEOPICTURE_TABLE
				+ " where  " + KEY_TYPE + "='" + TYPE_PICTURE + "' and "
				+ KEY_DID + "='" + did + "'";
		return db.rawQuery(sql, null);
	}

	/**
	 * This Method is used to query video/picture in createtime from
	 * video_picture_table
	 * **/
	public Cursor queryVideoOrPictureByDate(String did, String date, String type) {
		String sql = "select * from " + DATABASW_VIDEOPICTURE_TABLE
				+ " where  " + KEY_TYPE + "='" + TYPE_PICTURE + "' and "
				+ KEY_DID + "='" + did + "' and " + KEY_CREATETIME + "='"
				+ date + "'";
		return db.rawQuery(sql, null);
	}

	/***
	 * This Method is used to delete specific video/picture record from
	 * video_picture_table
	 * */
	public boolean deleteVideoOrPicture(String did, String filePath, String type) {
		return db.delete(DATABASW_VIDEOPICTURE_TABLE, KEY_DID + "=? and "
				+ KEY_FILEPATH + "=? and " + KEY_TYPE + "=?", new String[] {
				did, filePath, type }) > 0;
	}

	/**
	 * This Method is used to delete all video/picture record from
	 * video_picture_table
	 * **/
	public boolean deleteAllVideoOrPicture(String did, String type) {
		return db.delete(DATABASW_VIDEOPICTURE_TABLE, KEY_DID + "=? and "
				+ KEY_TYPE + "=?", new String[] { did, type }) > 0;
	}

	/**
	 * This Method is used to delete all record from video_picture_table
	 * **/
	public boolean deldteAllVideoPicture(String did) {
		return db.delete(DATABASW_VIDEOPICTURE_TABLE, KEY_DID + "=?",
				new String[] { did }) > 0;
	}

	/**
	 * This Method is used to add alarm log to alarmlog_table
	 * **/
	public long insertAlarmLogToDB(String did, String content, String createTime) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DID, did);
		initialValues.put(KEY_ALARMLOG_CONTENT, content);
		initialValues.put(KEY_CREATETIME, createTime);
		return db.insertOrThrow(DATABASE_ALARMLOG_TABLE, null, initialValues);
	}

	/**
	 * This Method is used to query all alarmlog from the specified did
	 * **/
	public Cursor queryAllAlarmLog(String did) {
		String sql = "select * from " + DATABASE_ALARMLOG_TABLE + " where "
				+ KEY_DID + "='" + did + "' order by " + KEY_CREATETIME
				+ " desc";
		return db.rawQuery(sql, null);
	}

	/**
	 * This Method is used to delete one alarmlog with specified time from did
	 * **/
	public boolean delAlarmLog(String did, String createtime) {
		return db.delete(DATABASE_ALARMLOG_TABLE, KEY_DID + "=? and "
				+ KEY_CREATETIME + "=?", new String[] { did, createtime }) > 0;
	}

	public boolean addFirstpic(String did, String filepath) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DID, did);
		initialValues.put(KEY_FILEPATH, filepath);
		return db.insert(DATABASE_FIRSTPIC_TABLE, null, initialValues) > 0;
	}

	public Cursor queryFirstpic(String did) {
		String sql = "select *  from " + DATABASE_FIRSTPIC_TABLE + " where "
				+ KEY_DID + "='" + did + "'";
		return db.rawQuery(sql, null);
	}
}
