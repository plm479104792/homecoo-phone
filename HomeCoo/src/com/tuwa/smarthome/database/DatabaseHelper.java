package com.tuwa.smarthome.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tuwa.smarthome.entity.APPMusic;
import com.tuwa.smarthome.entity.APPThemeMusic;
import com.tuwa.smarthome.entity.AlarmMessage;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.ThemeInfra;
import com.tuwa.smarthome.entity.User;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.entity.Version;

public class DatabaseHelper  extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "smarthome.db";  
   
	 private Map<String, Dao> daos = new HashMap<String, Dao>();  
	 
    private DatabaseHelper(Context context)  
    {  
        super(context, DATABASE_NAME, null, 35);    
    }  
    //在数据库中创建表
    @Override  
    public void onCreate(SQLiteDatabase database,  
            ConnectionSource connectionSource)  
    {  
        try  
        {  
        	TableUtils.createTableIfNotExists(connectionSource, User.class);  
            TableUtils.createTableIfNotExists(connectionSource, Gateway.class); 
            TableUtils.createTableIfNotExists(connectionSource, Space.class); 
         
            TableUtils.createTableIfNotExists(connectionSource, Device.class);
            TableUtils.createTableIfNotExists(connectionSource, Theme.class); 
            TableUtils.createTableIfNotExists(connectionSource, ThemeDevice.class); 
            TableUtils.createTableIfNotExists(connectionSource, UserSpaceDevice.class); 
            TableUtils.createTableIfNotExists(connectionSource, Version.class);
            TableUtils.createTableIfNotExists(connectionSource, AlarmMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, ThemeInfra.class);
            // 创建music  thememusic表    2016-06-19  by xiaobai
            TableUtils.createTableIfNotExists(connectionSource, APPMusic.class);
            TableUtils.createTableIfNotExists(connectionSource, APPThemeMusic.class);
            
        } catch (SQLException e)  
        {  
            e.printStackTrace();  
        }  
    }  
    //在数据库中已经存在表，则通过升级数据库版本号创建数据库
    @Override  
    public void onUpgrade(SQLiteDatabase database,  
            ConnectionSource connectionSource, int oldVersion, int newVersion)  
    {  
        try  
        {  
        	TableUtils.dropTable(connectionSource, User.class, true);
        	TableUtils.dropTable(connectionSource, Gateway.class, true);
        	TableUtils.dropTable(connectionSource, Space.class, true);
        	
        	TableUtils.dropTable(connectionSource, Device.class, true);
        	TableUtils.dropTable(connectionSource, Theme.class, true); 
        	TableUtils.dropTable(connectionSource, ThemeDevice.class, true); 
        	TableUtils.dropTable(connectionSource, UserSpaceDevice.class, true); 
        	TableUtils.dropTable(connectionSource, Version.class, true); 
        	TableUtils.dropTable(connectionSource, AlarmMessage.class, true); 
        	TableUtils.dropTable(connectionSource, ThemeInfra.class, true); 
        	
        	//更新表
        	TableUtils.dropTable(connectionSource, APPMusic.class, true);
        	TableUtils.dropTable(connectionSource, APPThemeMusic.class, true);
        	
            onCreate(database, connectionSource);  
           
        } catch (SQLException e)  
        {  
            e.printStackTrace();  
        }  
    }  
  
    private static DatabaseHelper instance;  
  
    /** 
     * 单例获取该Helper 
     *  
     * @param context 
     * @return 
     */  
    public static synchronized DatabaseHelper getHelper(Context context)  
    {  
        if (instance == null)  
        {  
            synchronized (DatabaseHelper.class)  
            {  
                if (instance == null)  
                    instance = new DatabaseHelper(context);  
            }  
        }  
  
        return instance;  
    }  
  
    
    public synchronized Dao getDao(Class clazz) throws SQLException  
    {  
        Dao dao = null;  
        String className = clazz.getSimpleName();  
  
        if (daos.containsKey(className))  
        {  
            dao = daos.get(className);  
        }  
        if (dao == null)  
        {  
            dao = super.getDao(clazz);  
            daos.put(className, dao);  
        }  
        return dao;  
    }  
    
    
  
    /** 
     * 释放资源 
     */  
    @Override  
    public void close()  
    {  
        super.close();  
        
        for (String key : daos.keySet())  
        {  
            Dao dao = daos.get(key);  
            dao = null;  
        }  
    }  
}
