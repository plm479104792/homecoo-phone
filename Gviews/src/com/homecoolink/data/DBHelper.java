package com.homecoolink.data;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.homecoolink.entity.Account;
import com.homecoolink.global.AccountPersist;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.global.NpcCommon;
import com.homecoolink.utils.T;

public class DBHelper extends SQLiteOpenHelper {
	private SQLiteDatabase mDB = null;
	private Context context;
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		
		super(context, name, factory, version);
		
	
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		
		if (null == mDB) {
			mDB = db;
		}
		
		try{
			db.execSQL(MessageDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","db existed");
		}
		
		try{
			db.execSQL(SysMessageDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","db existed");
		}
		
		try{
			db.execSQL(AlarmMaskDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","db existed");
		}
		
		try{
			db.execSQL(AlarmRecordDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","db existed");
		}
		
		try{
			db.execSQL(NearlyTellDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","db existed");
		}
		
		try{
			db.execSQL(ContactDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","db existed");
		}
		
		
		try{
			db.execSQL(DeviceAlarmRecordDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","db existed");
		}
		
		
		try{
			db.execSQL(DefenceAreaNameDB.getCreateTableString());
		}catch(Exception e){
			Log.e("my","db existed");
		}
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("mydb",  "oldversion为:" + oldVersion);
		if(oldVersion<10){
			String recentName = SharedPreferencesManager.getInstance().getData(MyApp.app, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME);
			if(!recentName.equals("")){
				if(recentName.charAt(0)!='0'){
					SharedPreferencesManager.getInstance().putData(MyApp.app, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME_EMAIL, "0"+recentName);
				}else{
					SharedPreferencesManager.getInstance().putData(MyApp.app, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME_EMAIL, recentName);
				}
			}
			
			SharedPreferencesManager.getInstance().putData(MyApp.app, SharedPreferencesManager.SP_FILE_GWELL, SharedPreferencesManager.KEY_RECENTNAME, "");
			SharedPreferencesManager.getInstance().putRecentLoginType(MyApp.app, Constants.LoginType.EMAIL);
			
		}
		
		if(oldVersion<13){
			Account account = AccountPersist.getInstance().getActiveAccountInfo(MyApp.app);
			if(null!=account){
				account.three_number = "0"+account.three_number;
				AccountPersist.getInstance().setActiveAccount(MyApp.app, account);
				NpcCommon.mThreeNum = AccountPersist.getInstance().getActiveAccountInfo(MyApp.app).three_number;
			}
		}
		
		if(oldVersion<21){
			db.execSQL("DROP TABLE IF EXISTS message");
			db.execSQL(MessageDB.getCreateTableString());
			
			db.execSQL("DROP TABLE IF EXISTS sysMsg");
			db.execSQL(SysMessageDB.getCreateTableString());
			
			db.execSQL("DROP TABLE IF EXISTS allarm_mask");
			db.execSQL(AlarmMaskDB.getCreateTableString());
			
			db.execSQL("DROP TABLE IF EXISTS alarm_record");
			db.execSQL(AlarmRecordDB.getCreateTableString());
			
			db.execSQL("DROP TABLE IF EXISTS nearly_tell");
			db.execSQL(NearlyTellDB.getCreateTableString());
			
			
			Cursor cursor = db.rawQuery("SELECT * FROM contant_friends", null);
			List<Contact> lists = new ArrayList<Contact>();
			if(cursor!=null){
	    		while(cursor.moveToNext()){
	    			String contactName = cursor.getString(cursor.getColumnIndex("name"));
	    			String contactId = cursor.getString(cursor.getColumnIndex("threeAccount"));
	    			String contactPassword = cursor.getString(cursor.getColumnIndex("threePwd"));
	    			int contactType = cursor.getInt(cursor.getColumnIndex("device_type"));
	    			int messageCount = cursor.getInt(cursor.getColumnIndex("msgCount"));
	    			String activeUser = cursor.getString(cursor.getColumnIndex("uId"));
	    			Contact data = new Contact();
	    			data.contactName = contactName;
	    			data.contactId = contactId;
	    			data.contactPassword = contactPassword;
	    			data.contactType = contactType;
	    			data.messageCount = messageCount;
	    			if(activeUser.charAt(0)!='0'){
	    				activeUser = "0" + activeUser;
	    			}
	    			data.activeUser = activeUser;
	    			lists.add(data);
	    		}
	    		cursor.close();
	    	}
			db.execSQL("DROP TABLE IF EXISTS contant_friends");
			db.execSQL(ContactDB.getCreateTableString());
			
			for(Contact contact : lists){
				
				ContactDB contactDB = new ContactDB(db);
		    	contactDB.insert(contact);
			}
			
		}
		
		Log.e("mydb",  "oldversion为:" + oldVersion);
		if(oldVersion<38){
			List<AlarmRecord> lists = new ArrayList<AlarmRecord>();
			Cursor cursor = db.rawQuery("SELECT * FROM alarm_record", null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					String deviceId = cursor.getString(cursor
							.getColumnIndex("deviceId"));
					int alarmType = cursor.getInt(cursor
							.getColumnIndex("alarmType"));
					String alarmTime = cursor.getString(cursor
							.getColumnIndex("alarmTime"));
					String activeUser = cursor.getString(cursor
							.getColumnIndex("activeUser"));
					
					AlarmRecord data = new AlarmRecord();
					data.deviceId = deviceId;
					data.alarmType = alarmType;
					data.alarmTime = alarmTime;
					data.activeUser = activeUser;
					data.group = -1;
					data.item = -1;
					data.alarmStatus = 1;
					lists.add(data);
				}
				cursor.close();
			}
			
			db.execSQL("DROP TABLE IF EXISTS alarm_record");
			db.execSQL(AlarmRecordDB.getCreateTableString());
			for(AlarmRecord record : lists){
				
				AlarmRecordDB alarmRecordDB = new AlarmRecordDB(db);
				alarmRecordDB.insert(record);
			}
			
		}
		
		onCreate(db);
	}
}

