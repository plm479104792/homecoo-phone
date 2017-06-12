package com.tuwa.smarthome.dao;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.tuwa.smarthome.database.DatabaseHelper;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Gateway;
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.SystemValue;

public class UserSpaceDevDao {
	private Context context;
	private Dao<UserSpaceDevice, Integer> userSpaceDevDao;
	private DatabaseHelper helper;

	@SuppressWarnings("unchecked")
	public UserSpaceDevDao(Context context) {
		this.context = context;
		try {
			helper = DatabaseHelper.getHelper(context);
			userSpaceDevDao = helper.getDao(UserSpaceDevice.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    /**
     * 更新user_device_space 关联表
     * @param userSpaceDev
     */
	public void addorUpdate(UserSpaceDevice userSpaceDev) {
		//===更新version_device 时间戳===
//		Version version=SystemValue.getVersion(SystemValue.VERSION_DEVICE);
//		new VersionDao(null).addorUpdateVerson(version);		
		
		String phoneNum = userSpaceDev.getPhonenum();
		String deviceNo = userSpaceDev.getDeviceNo();
		if (isExsitDeviceSpace(phoneNum, deviceNo)) {
			try {
				userSpaceDevDao.create(userSpaceDev);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			String deviceName = userSpaceDev.getDeviceName();
			String spaceNo = userSpaceDev.getSpaceNo();
//			int spaceType=userSpaceDev.getSpaceType();
			
			try {
				UpdateBuilder<UserSpaceDevice, Integer> updateBuilder = userSpaceDevDao
						.updateBuilder();
				updateBuilder.where().eq("PHONENUM", phoneNum).and()
						             .eq("DEVICE_NO", deviceNo);
				updateBuilder.updateColumnValue("DEVICE_NAME", deviceName);
				updateBuilder.updateColumnValue("SPACE_NO", spaceNo);
//				updateBuilder.updateColumnValue("SPACE_TYPE", spaceType);
				updateBuilder.update();
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 验证用户对应的设备是否已经设置
	 * 
	 * @param spacename
	 * @return
	 */
	public boolean isExsitDeviceSpace(String phonenum, String deviceNo) {
		List<UserSpaceDevice> userDeviceList = null;
		try {
			userDeviceList = userSpaceDevDao.queryBuilder().where()
					.eq("PHONENUM", phonenum).and()
					.eq("DEVICE_NO", deviceNo).query();
			return userDeviceList.isEmpty() ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	public UserSpaceDevice findDeviceSpace(String phonenum, String deviceNo) {
		
		try {
			return userSpaceDevDao.queryBuilder().where()
					.eq("PHONENUM", phonenum).and()
					.eq("DEVICE_NO", deviceNo).queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}


    /**
     *根据phonenum获取所有设备关联信息
     * @param phonenum
     * @return
     */
	public List<UserSpaceDevice> getDeviceSpaceByPhonenum(String phonenum) {
		List<UserSpaceDevice> deviceSpacelist = null;
		try {
			deviceSpacelist = userSpaceDevDao.queryBuilder().where()
					.eq("PHONENUM", phonenum).query();
			return deviceSpacelist;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 查找用户在该房间下的设备列表
	 * @param phonenum
	 * @param spaceno
	 * @return
	 */
	public List<UserSpaceDevice> getDeviceListByPhonenumAndSpaceNo(String phonenum,String spaceno) {
		List<UserSpaceDevice> deviceSpacelist = null;
		try {
			deviceSpacelist = userSpaceDevDao.queryBuilder().where()
					.eq("PHONENUM", phonenum).and()
					.eq("SPACE_NO", spaceno).query();
			return deviceSpacelist;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 删除手机号下对应设备的配置信息
	 * @param devdto
	 */
    public void deleteDevSpaceByDeviceno(Device devdto)  
    {  
    	String deviceno=devdto.getDeviceNo();
    	String phonenum=devdto.getPhoneNum();
		 try {
			 DeleteBuilder<UserSpaceDevice, Integer> deleteBuilder = userSpaceDevDao.deleteBuilder();  
			 deleteBuilder.where()
				     .eq("DEVICE_NO",deviceno).and()
				     .eq("PHONENUM",phonenum);
			 deleteBuilder.delete();
		 } catch (SQLException e) {
			e.printStackTrace();
		 }
    } 
    
    /**
     * 注销用户下设备的配置信息 
     * @param phone
     */
    public void deleteDevSpaceByPhoneno(String phoneno)  
    {  
		 try {
			 DeleteBuilder<UserSpaceDevice, Integer> deleteBuilder = userSpaceDevDao.deleteBuilder();  
			 deleteBuilder.where()
				     .eq("PHONENUM",phoneno);
			 deleteBuilder.delete();
		 } catch (SQLException e) {
			e.printStackTrace();
		 }
    }
}
