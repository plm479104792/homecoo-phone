package com.tuwa.smarthome.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.tuwa.smarthome.database.DatabaseHelper;
import com.tuwa.smarthome.entity.APPMusic;

public class APPMusicDao {

	private Context context;
	private Dao<APPMusic,Integer> appmusicDao;
	private DatabaseHelper helper;
	
	@SuppressWarnings("unchecked")
	public APPMusicDao(Context context) {
		super();
		this.context = context;
		try {
			helper=DatabaseHelper.getHelper(context);
			appmusicDao=helper.getDao(APPMusic.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @Description: 添加歌曲    每次添加歌曲music之前 先删除之前的music表
	 * @param List<APPMusic>
	 * */
	public void InsertAppMusic(List<APPMusic> list){
		DeleteAppMusic(list.get(0));
		Iterator<APPMusic> iterator=list.iterator();
		while (iterator.hasNext()) {
			APPMusic appMusic = (APPMusic) iterator.next();
			try {
				appmusicDao.create(appMusic);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * @Description:根据gatewayNo 删除appmusic表
	 * */
	public void DeleteAppMusic(APPMusic appMusic){
		DeleteBuilder<APPMusic,Integer> deleteBuilder=appmusicDao.deleteBuilder();
		try {
			deleteBuilder.where().eq("GATEWAY_NO", appMusic.getGatewayNo());
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description:获取muscilist 用于情景设置
	 * @return musiclist
	 * */
	public List<APPMusic> GetAppMusicListByGatewayNo(String gatewayNo){
	
		List<APPMusic> list=new ArrayList<APPMusic>();
		try {
			list=appmusicDao.queryBuilder().where().eq("GATEWAY_NO",gatewayNo).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	
	
}
