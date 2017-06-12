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
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.entity.ThemeInfra;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.SystemValue;

public class ThemeDeviceDao {
	private Context context;  
    private Dao<ThemeDevice, Integer> themestateDao;  
    private DatabaseHelper helper;
    
    @SuppressWarnings("unchecked")
  		public ThemeDeviceDao(Context context)  
  	    {  
  	        this.context = context;  
  	        try  
  	        {  
  	            helper = DatabaseHelper.getHelper(context);  
  	          themestateDao = helper.getDao(ThemeDevice.class);  
  	        } catch (SQLException e)  
  	        {  
  	            e.printStackTrace();  
  	        }  
  	    }  
    
	    /** 
	     * 增加或更新设备联动
	     * @param user 
	     */  
	    public void addOrUpdate(ThemeDevice themestate)  
	    {    String devno=themestate.getDeviceNo();
	         String themeid=themestate.getThemeNo();
	    	
	    	if (isEsistThemestate(devno,themeid)) {  //该情景下设备状态已存在
	    		updateDevstateByThemestate(themestate);
			}else {
		        try  
		        {  
		        	themestateDao.create(themestate); 
		        } catch (SQLException e)  
		        {  
		            e.printStackTrace();  
		        }  
			}
	    	
	    } 
	    
	    //添加红外遥控联动
	    public void addInfraThemeDevice(ThemeDevice themestate)  {
	    	try {
				themestateDao.create(themestate);
			} catch (SQLException e) {
				e.printStackTrace();
			} 
	    }
	    
	  //更新红外遥控联动
	    public void updateInfraDevstateByThemestate(ThemeInfra formerInfra,ThemeInfra themeinfra){
	    	 String themeNo=formerInfra.getThemeNo();
	 	     String formerDeviceNo=formerInfra.getDeviceNo();
	 		 String formerDevstate=formerInfra.getDeviceStateCmd();
	 		
	    	 String devid=themeinfra.getDeviceNo();
	         String devstate=themeinfra.getDeviceStateCmd();
	    	try {
	    		UpdateBuilder<ThemeDevice, Integer> updateBuilder = themestateDao.updateBuilder();  
				      updateBuilder.where()
			                 .eq("DEVICE_NO", formerDeviceNo).and()
		                     .eq("DEVICE_STATE_CMD", formerDevstate).and()  
						     .eq("THEME_NO",themeNo);
				      updateBuilder.updateColumnValue("DEVICE_NO", devid);
				      updateBuilder.updateColumnValue("DEVICE_STATE_CMD", devstate);
				      updateBuilder.update();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
	    	// ===更新version_device 时间戳===
//			Version version = SystemValue.getVersion(SystemValue.VERSION_SCENE);
//			new VersionDao(null).addorUpdateVerson(version);
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
				DeleteBuilder<ThemeDevice, Integer> deleteBuilder = themestateDao.deleteBuilder();
				deleteBuilder.where().eq("THEME_NO", themeNo).and()
				                     .eq("DEVICE_NO", deviceNo).and()
				                     .eq("DEVICE_STATE_CMD", stateCmd);
				deleteBuilder.delete();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	    
	    
	    
	    /**
	     * 判断themeLink表中是否devno对应的联动信息
	     * @param id
	     * @param themeid
	     * @return
	     */
	    public boolean isEsistThemestate(String devno,String themeid){
	    	List<ThemeDevice> list=null;
	    	 try {
	    		 list=themestateDao.queryBuilder().where()
	    				  .eq("DEVICE_NO", devno).and()
	    				  .eq("THEME_NO",themeid).query();
	    		  return list.isEmpty() ? false : true;
			 } catch (SQLException e) {
				e.printStackTrace();
			 }
	    	return true;
	    }
	    
	    /**
	     * 判断该红外码在网关下是否存在
	     * @param devno
	     * @param themeid
	     * @return
	     */
	    public boolean isEsistInfraThemestate(ThemeDevice themeDevice){
	    	List<ThemeDevice> list=null;
	    	String devno=themeDevice.getDeviceNo();
	    	String devState=themeDevice.getDeviceStateCmd();
	    	String gatewayNo=themeDevice.getGatewayNo();
	    	 try {
	    		 list=themestateDao.queryBuilder().where()
	    				  .eq("DEVICE_NO", devno).and()
	    				  .eq("DEVICE_STATE_CMD", devState).and()
	    				  .eq("GATEWAY_NO",gatewayNo).query();
	    		  return list.isEmpty() ? false : true;
			 } catch (SQLException e) {
				e.printStackTrace();
			 }
	    	return true;
	    }
	    
	    

	    
	    /**
	     * 根据id和themeid更新devstate
	     * @param themestate
	     */
	    public void updateDevstateByThemestate(ThemeDevice themestate){
	    	 String devid=themestate.getDeviceNo();
	         String themeid=themestate.getThemeNo();
	         String devstate=themestate.getDeviceStateCmd();
	    	try {
	    		UpdateBuilder<ThemeDevice, Integer> updateBuilder = themestateDao.updateBuilder();  
				      updateBuilder.where()
			                 .eq("DEVICE_NO", devid).and()
						     .eq("THEME_NO",themeid);
				      updateBuilder.updateColumnValue("DEVICE_STATE_CMD", devstate);
				      updateBuilder.update();
	    		
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
//	    	// ===更新version_device 时间戳===
//			Version version = SystemValue.getVersion(SystemValue.VERSION_SCENE);
//			new VersionDao(null).addorUpdateVerson(version);
	    }
	    
	    /**
	     * 根据硬件情景识别号删除关联状态
	     * @param themestate
	     */
	    public void deleteThemeDeviceByThemeDeviceNo(String themeDeviceNo){
	    	try {
	    		DeleteBuilder<ThemeDevice, Integer> deleteBuilder = themestateDao.deleteBuilder();  
	    		       deleteBuilder.where().eq("THEME_DEVICE_NO",themeDeviceNo);
	    		       deleteBuilder.delete();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
//	    	// ===更新version_device 时间戳===
//			Version version = SystemValue.getVersion(SystemValue.VERSION_SCENE);
//			new VersionDao(null).addorUpdateVerson(version);
	    }
	    
	    /**
	     * 根据情景号删除关联的设备
	     * @param themestate
	     */
	    public void deleteThemeDeviceByThemeNo(ThemeDevice themestate){
	    	 String devid=themestate.getDeviceNo();
	         String themeid=themestate.getThemeNo();
	    	try {
	    		DeleteBuilder<ThemeDevice, Integer> deleteBuilder = themestateDao.deleteBuilder();  
	    		       deleteBuilder.where()
			                 .eq("DEVICE_NO", devid).and()
						     .eq("THEME_NO",themeid);
	    		       deleteBuilder.delete();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
	    	// ===更新version_device 时间戳===
//			Version version = SystemValue.getVersion(SystemValue.VERSION_SCENE);
//			new VersionDao(null).addorUpdateVerson(version);
	    }
	    
	    
	    /**
	     * 根据THEME_NO删除该情景号下所有关联的设备
	     * @param themestate
	     */
	    public void deleteThemeDeviceAllByThemeNo(String themeNo){
	    	try {
	    		DeleteBuilder<ThemeDevice, Integer> deleteBuilder = themestateDao.deleteBuilder();  
	    		       deleteBuilder.where()
						     .eq("THEME_NO",themeNo);
	    		       deleteBuilder.delete();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
//	    	// ===更新version_device 时间戳===
//			Version version = SystemValue.getVersion(SystemValue.VERSION_SCENE);
//			new VersionDao(null).addorUpdateVerson(version);
	    }
	    
	    
	    /**
	     * 根据情景号查找对应情景下的设备的状态
	     * @param gatewayId
	     * @return
	     */
	    public List<ThemeDevice> findDevstateBythemeNo(String themeno)  
	    { 
	    	List<ThemeDevice>  themeDevList=new ArrayList<ThemeDevice>();
	        try  
	        {  
	        	themeDevList= themestateDao.queryBuilder().where().eq("THEME_NO", themeno).query();  
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        return themeDevList;  //屏蔽空指针异常
	    } 
	    
	    /**
	     * 安防类设备根据硬件情景识别码查找联动的devstate
	     * @param themeDeviceno
	     * @return
	     */
	    public List<ThemeDevice> findDevstateBythemeDeviceNo(String themeDeviceno)  
	    {  
	    	List<ThemeDevice>  themeDevList=new ArrayList<ThemeDevice>();
	        try  
	        {  
	        	themeDevList= themestateDao.queryBuilder().where()
	        		   .eq("THEME_DEVICE_NO", themeDeviceno).query();  
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        return themeDevList;  
	    } 
	    
	    /**
	     * 根据gatewayNo查找所有的情景联动
	     * @param gatewayNo
	     * @return
	     */
	    public List<ThemeDevice> findThemeDeviceByGatewayNo(String gatewayNo){
	    	List<ThemeDevice>  themeDevList=new ArrayList<ThemeDevice>();
	    	 try {
	    		 themeDevList=themestateDao.queryBuilder().where()
	    				  .eq("GATEWAY_NO",gatewayNo).query();
			 } catch (SQLException e) {
				e.printStackTrace();
			 }
	    	return themeDevList;
	    }
	    
	    
		/**
		 * 清空当前网关下对应的情景联动
		 * @param gatewayNo
		 */
		public void deleteAllByGatewayNo(String gatewayNo) {
			
			try {
				DeleteBuilder<ThemeDevice, Integer> deleteBuilder = themestateDao.deleteBuilder();
				deleteBuilder.where().eq("GATEWAY_NO", gatewayNo);
				deleteBuilder.delete();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

}
