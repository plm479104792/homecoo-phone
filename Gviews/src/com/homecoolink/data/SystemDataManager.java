package com.homecoolink.data;

import java.util.ArrayList;
import java.util.HashMap;

import com.homecoolink.R;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;

public class SystemDataManager {
	private static SystemDataManager manager = null;
	private SystemDataManager(){}
	public synchronized static SystemDataManager getInstance(){
		if(null==manager){
			synchronized(SystemDataManager.class){
				if(null==manager){
					manager = new SystemDataManager();
				}
			}
		}
		return manager;
	}
	

	public ArrayList<HashMap<String,String>> getSysBells(Context context){
		ArrayList<HashMap<String,String>> bells = new ArrayList<HashMap<String,String>>();
		ContentResolver resolver = context.getContentResolver();
		Cursor result = resolver.query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		
		
		HashMap<String,String> bell = new HashMap<String,String>();
		bell.put("bellName","Alarm music");
		bell.put("bellId", R.raw.alarm_music+"");
		bells.add(bell);
		while(result.moveToNext()){
			HashMap<String,String> bell2 = new HashMap<String,String>();
			int bellId = result.getInt(result.getColumnIndex(BaseColumns._ID));
			String bellName = result.getString(result.getColumnIndex(MediaStore.MediaColumns.TITLE));
			bell2.put("bellName", bellName);
			bell2.put("bellId", bellId+"");
			bells.add(bell2);
		}
		result.close();
		return bells;
	}
	
	public ArrayList<HashMap<String,String>> getSdBells(Context context){
		ArrayList<HashMap<String,String>> bells = new ArrayList<HashMap<String,String>>();
		ContentResolver resolver = context.getContentResolver();
		Cursor result = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if(null==result){
			return bells;
		}
		while(result.moveToNext()){
			HashMap<String,String> bell = new HashMap<String,String>();
			int bellId = result.getInt(result.getColumnIndex(BaseColumns._ID));
			String bellName = result.getString(result.getColumnIndex(MediaStore.MediaColumns.TITLE));
			bell.put("bellName", bellName);
			bell.put("bellId", bellId+"");
			bells.add(bell);
		}
		result.close();
		return bells;
	}

	public HashMap<String,String> findSystemBellById(Context context,int bellId){
		HashMap<String,String> data = new HashMap<String,String>();
		ContentResolver resolver = context.getContentResolver();
		Cursor result = resolver.query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, BaseColumns._ID+"=?", new String[]{String.valueOf(bellId)}, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		while(result.moveToNext()){
			data = new HashMap<String,String>();
			String path = result.getString(result.getColumnIndex(MediaStore.MediaColumns.DATA));
			String bellName = result.getString(result.getColumnIndex(MediaStore.MediaColumns.TITLE));
			data.put("path", path);
			data.put("bellName", bellName);
		}
		result.close();
		return data;
	}
	
	
		public HashMap<String,String> findSdBellById(Context context,int bellId){
			HashMap<String,String> data = new HashMap<String,String>();
			ContentResolver resolver = context.getContentResolver();
			Cursor result = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, BaseColumns._ID+"=?", 
					new String[]{String.valueOf(bellId)}, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			while(result.moveToNext()){
				data = new HashMap<String,String>();
				String path = result.getString(result.getColumnIndex(MediaStore.MediaColumns.DATA));
				String bellName = result.getString(result.getColumnIndex(MediaStore.MediaColumns.TITLE));
				data.put("path", path);
				data.put("bellName", bellName);
			}
			result.close();
			return data;
		}
}
