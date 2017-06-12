package com.tuwa.smarthome.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.tuwa.smarthome.database.DatabaseHelper;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.ThemeInfra;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.SystemValue;

public class ThemeInfraDao {
	private Context context;
	private Dao<ThemeInfra, Integer> themeInfraDao;
	private DatabaseHelper helper;

	@SuppressWarnings("unchecked")
	public ThemeInfraDao(Context context) {
		this.context = context;
		try {
			helper = DatabaseHelper.getHelper(context);
			themeInfraDao = helper.getDao(ThemeInfra.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加一个空间
	 * 
	 * @param user
	 */
	public void addThemeInfra(ThemeInfra themeinfra,Theme theme) {
	
		if (isUseableThemeInfra(themeinfra)) {
			try {
				themeInfraDao.create(themeinfra);
				
				ThemeDevice themeDevice=new ThemeDevice();
				themeDevice.setThemeNo(theme.getThemeNo());
				themeDevice.setDeviceNo(themeinfra.getDeviceNo());  //红外转发器编号
				themeDevice.setThemeType(theme.getThemeType());
				themeDevice.setThemeState(theme.getThemeState());
//				themeDevice.setInfraTypeId(themeinfra.getInfraTypeId());
				themeDevice.setDeviceStateCmd(themeinfra.getDeviceStateCmd());
				themeDevice.setGatewayNo(themeinfra.getGatewayNo());
				
				new ThemeDeviceDao(null).addInfraThemeDevice(themeDevice);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("特定设备的红外码已经存在，不做任何操作！");
//			updateThemeInfra(themeinfra);
		}

	}

	/**
	 * 判断红外遥控对应设备的命令是否已存在
	 * @param themeinfra
	 * @return
	 */
	public boolean isUseableThemeInfra(ThemeInfra themeinfra) {
		String themeNo=themeinfra.getThemeNo();
		String deviceNo=themeinfra.getDeviceNo();
		String stateCmd=themeinfra.getDeviceStateCmd();
		List<ThemeInfra> infralist = null;
		try {
			infralist = themeInfraDao.queryBuilder().where()
					.eq("THEME_NO", themeNo).and()
					.eq("DEVICE_NO", deviceNo).and()
					.eq("DEVICE_STATE_CMD", stateCmd)
					.query();
			return infralist.isEmpty() ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 更新了红外控制操作
	 * 
	 * @param themeinfra
	 */
   public void updateThemeInfra(ThemeInfra formerInfra,ThemeInfra themeinfra){
	    
	    String themeNo=formerInfra.getThemeNo();
	    String formerDeviceNo=formerInfra.getDeviceNo();
		String formerDevstate=formerInfra.getDeviceStateCmd();
	   
		String deviceNo=themeinfra.getDeviceNo();
		String devstate=themeinfra.getDeviceStateCmd();
		String infraCtrName=themeinfra.getInfraControlName();
    	try {
    		UpdateBuilder<ThemeInfra, Integer> updateBuilder = themeInfraDao.updateBuilder();  
			      updateBuilder.where()
		                     .eq("THEME_NO", themeNo).and()
		                     .eq("DEVICE_NO", formerDeviceNo).and()
		                     .eq("DEVICE_STATE_CMD", formerDevstate);
			      updateBuilder.updateColumnValue("DEVICE_NO", deviceNo);
			      updateBuilder.updateColumnValue("DEVICE_STATE_CMD", devstate);
			      updateBuilder.updateColumnValue("INFRA_CRL_NAME", infraCtrName);
			      updateBuilder.update();
    		System.out.println("更新了红外表控制操作！！！"+formerDevstate+"==="+devstate);
    		System.out.println("更新的情景号为"+themeNo);
    		
    		new ThemeDeviceDao(null).updateInfraDevstateByThemestate(formerInfra,themeinfra); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
//	    	// ===更新version_device 时间戳===
//			Version version = SystemValue.getVersion(SystemValue.VERSION_SCENE);
//			new VersionDao(null).addorUpdateVerson(version);
    }

	/**
	 * 根据情景号加载红外控制列表
	 * @param themeNo
	 * @return
	 */
	public List<ThemeInfra> getThemeInfraByThemeNo(String themeNo) {
		List<ThemeInfra> infralist = new ArrayList<ThemeInfra>();
		try {
			infralist = themeInfraDao.queryBuilder().where()
					.eq("THEME_NO", themeNo).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return infralist;
	}
	
	/**
	 * 删除某个红外操作类型
	 * @param themeinfra
	 */
	public void deleteThemeInfra(ThemeInfra themeinfra) {
		String themeNo=themeinfra.getThemeNo();
		String deviceNo=themeinfra.getDeviceNo();
		String stateCmd=themeinfra.getDeviceStateCmd();
		try {
			DeleteBuilder<ThemeInfra, Integer> deleteBuilder = themeInfraDao.deleteBuilder();
			deleteBuilder.where().eq("THEME_NO", themeNo).and()
			                     .eq("DEVICE_NO", deviceNo).and()
			                     .eq("DEVICE_STATE_CMD", stateCmd);
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
