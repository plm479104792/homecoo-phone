package com.tuwa.smarthome.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.tuwa.smarthome.database.DatabaseHelper;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.SystemValue;

public class ThemeDao {
	private Context context;
	private Dao<Theme, Integer> themeDao;
	private DatabaseHelper helper;

	@SuppressWarnings("unchecked")
	public ThemeDao(Context context) {
		this.context = context;
		try {
			helper = DatabaseHelper.getHelper(context);
			themeDao = helper.getDao(Theme.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据themeid判断情景是否存在
	 * 
	 * @param themeId
	 * @return
	 */
	public boolean isUseableTheme(String themeId) {
		List<Theme> list = null;
		try {
			list = themeDao.queryBuilder().where()
					.eq("THEME_NO", themeId)
					.query();
			return list.isEmpty() ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 安防类情景，根据deviceno判断情景是否存在
	 * @param deviceno
	 * @return
	 */
	public boolean isUseableThemeByDeviceNo(String deviceno) {
		List<Theme> list = null;
		try {
			list = themeDao.queryBuilder().where()
					.eq("DEVICE_NO", deviceno)
					.query();
			return list.isEmpty() ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 增加一个空间
	 * 
	 * @param user
	 */
	public void addOrUpdateTheme(Theme theme) {
		String themeNo=theme.getThemeNo();
        if(isUseableTheme(themeNo)){
        	try {
    			themeDao.create(theme);
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
        }else{
        	updateThemeNameByThemeNo(theme);
        }
		
	}


	/**
	 * 根据themeid删除情景
	 * 
	 * @param theme
	 */
	public void deleteByThemeNo(Theme theme) {
		String themeNo = theme.getThemeNo();
		try {
			DeleteBuilder<Theme, Integer> deleteBuilder = themeDao
					.deleteBuilder();
			deleteBuilder.where().eq("THEME_NO", themeNo);
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 清空当前网关下的自定义情景
	 * @param gatewayNo
	 */
	public void deleteAllByGatewayNo(String gatewayNo) {
		
		try {
			DeleteBuilder<Theme, Integer> deleteBuilder = themeDao.deleteBuilder();
			deleteBuilder.where().eq("GATEWAY_NO", gatewayNo);
			deleteBuilder.where().eq("THEME_TYPE", 4);
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	// 根据硬件设备识别码删除设备
	public void deleteThemeByDeviceNo(String deviceno) {
		try {
			DeleteBuilder<Theme, Integer> deleteBuilder = themeDao
					.deleteBuilder();
			deleteBuilder.where().eq("DEVICE_NO", deviceno);
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 根据gateway_id查找对应的情景
	 * 
	 * @param userId
	 * @return
	 */
	public List<Theme> themeListByGatewayNo(String gatewayId) {
		List<Theme> themelist=new ArrayList<Theme>();
		try {
			themelist=themeDao.queryBuilder().where()
					.eq("GATEWAY_NO", gatewayId)
					.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return themelist;
	}
	

	/**
	 * gatewayNo根据网关号查询安防情景
	 * @param gatewayNo
	 * @return
	 */
	public List<Theme> findThemeSensorListByGatewayNo(String gatewayNo) {
		List<Theme> themelist=new ArrayList<Theme>();
		try {
			themelist= themeDao.queryBuilder().where()
					.eq("GATEWAY_NO", gatewayNo).and()
					.eq("THEME_TYPE", SystemValue.SCENE_TRIGGER)
					.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return themelist;
	}
	
	/**
	 * 根据设备识别码和状态查找情景情景设备
	 * @param deviceNo
	 * @param state
	 * @return
	 */
	public Theme findThemeByDeviceNoAndState(String deviceNo,String state) {
		String gatewayNo=SystemValue.gatewayid;
		try {
			return themeDao.queryBuilder().where()
					.eq("GATEWAY_NO", gatewayNo).and()
					.eq("DEVICE_NO", deviceNo).and()
					.eq("THEME_STATE", state)
					.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 根据userid和devstrid更新安防设备情景themeid
	 * 
	 * @param theme
	 */
	public void updateThemeNameByThemeNo(Theme theme) {
		String themeNo=theme.getThemeNo();
		String themeName = theme.getThemeName();
		try {
			UpdateBuilder<Theme, Integer> updateBuilder = themeDao
					.updateBuilder();
			updateBuilder.where().eq("THEME_NO", themeNo);
			updateBuilder.updateColumnValue("THEME_NAME",themeName);
			updateBuilder.update();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
