package com.tuwa.smarthome.dao;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.tuwa.smarthome.database.DatabaseHelper;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Version;

public class VersionDao {
	private Context context;
	private Dao<Version, Integer> versionDao;
	private DatabaseHelper helper;

	@SuppressWarnings("unchecked")
	public VersionDao(Context context) {
		this.context = context;
		try {
			helper = DatabaseHelper.getHelper(context);
			versionDao = helper.getDao(Version.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加一个网关
	 * 
	 * @param user
	 */
	public void addorUpdateVerson(Version version) {
		if (isUseableVersion(version)) {
			try {
				versionDao.create(version);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			updateTimeByVersionType(version);  //如果存在该类型信息则更新版本时间
		}
	}

	/**
	 * 判断对应类型的版本信息是否存在
	 * 
	 * @param version
	 * @return
	 */
	public boolean isExistVersionByPhonenum(Version version) {
		String phonenum = version.getPhonenum();
		int versionType = version.getVersionType();
		List<Version> versionlist = null;
		try {
			versionlist = versionDao.queryBuilder().where()
					.eq("PHONENUM", phonenum).and()
					.eq("VERSION_TYPE",versionType).query();
			return versionlist.isEmpty() ? false : true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public boolean isUseableVersion(Version version) {
		String phonenum = version.getPhonenum();
		int versionType = version.getVersionType();
		List<Version> versionlist = null;
		
		try {
			versionlist = versionDao.queryBuilder().where()
					.eq("VERSION_TYPE", versionType).and()
            		.eq("PHONENUM", phonenum).query();
			return versionlist.isEmpty() ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 分类型更新版本的时间戳
	 * 
	 * @param version
	 */
	public void updateTimeByVersionType(Version version) {
		String phonenum = version.getPhonenum();
		int versionType = version.getVersionType();
		long updateTime = version.getUpdateTime();
		try {
			UpdateBuilder<Version, Integer> updateBuilder = versionDao.updateBuilder();
			updateBuilder.where().eq("PHONENUM", phonenum).and()
					.eq("VERSION_TYPE", versionType);
			updateBuilder.updateColumnValue("UPDATE_TIME", updateTime);
			updateBuilder.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据phonenum和versionType获取版本信息
	 * @param phonenum
	 * @param versionType
	 * @return
	 */
	public Version getVersionByPhonenumAndVersionType(String phonenum,int versionType) {
		Version version = null;
		try {
			version = versionDao.queryBuilder().where()
					.eq("PHONENUM", phonenum).and()
					.eq("VERSION_TYPE", versionType).queryForFirst();
			return version;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 清空对应类型的版本信息
	 * @param versionType
	 */
    public void deleteVersionByVersionType(int versionType){
   	 try {
			 DeleteBuilder<Version, Integer> deleteBuilder = versionDao.deleteBuilder();  
			 deleteBuilder.where()
				     .eq("VERSION_TYPE",versionType);
			 deleteBuilder.delete();
		 } catch (SQLException e) {
			e.printStackTrace();
		 }
   }

}
