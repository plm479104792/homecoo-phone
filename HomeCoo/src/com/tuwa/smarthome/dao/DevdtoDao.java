package com.tuwa.smarthome.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.tuwa.smarthome.database.DatabaseHelper;
import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.Space;
import com.tuwa.smarthome.entity.User;
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.SystemValue;

public class DevdtoDao  {
	 private Context context;  
	    private Dao<Device, Integer>   devdtoDao;  
	    private DatabaseHelper helper;
	    
	    @SuppressWarnings("unchecked")
	  		public DevdtoDao(Context context)  
	  	    {  
	  	        this.context = context;  
	  	        try  
	  	        {  
	  	            helper = DatabaseHelper.getHelper(context);  
	  	          devdtoDao = helper.getDao(Device.class);  
	  	        } catch (SQLException e)  
	  	        {  
	  	            e.printStackTrace();  
	  	        }  
	  	    }  
	    
	    
	    /** 
	     * 增加一个设备
	     * @param  
	     */  
	    public void add(Device devdto)  
	    {    
	    	  try  
		        {  
		        	devdtoDao.create(devdto); 
//		        	System.out.println("新设备入网=========="+devdto.getDeviceTypeId());
		        } catch (SQLException e)  
		        {  
		            e.printStackTrace();  
		        }  
	    	  
	    }  
	    
	    
	    /**
	     * 根据DEVICE_NO对设备的位置和名称更新
	     * @param devdto
	     */
	    public void updateDeviceNameAndSpaceNo(Device devdto)  
	    {  
	    	String deviceNo=devdto.getDeviceNo();
	    	try {
	    		UpdateBuilder<Device, Integer> updateBuilder = devdtoDao.updateBuilder();  
				      updateBuilder.where().eq("DEVICE_NO",deviceNo);
				      updateBuilder.updateColumnValue("DEVICE_NAME", devdto.getDeviceName());
				      updateBuilder.updateColumnValue("SPACE_NO", devdto.getSpaceNo());
				      updateBuilder.updateColumnValue("SPACE_TYPE_ID",devdto.getSpaceTypeId());
				      updateBuilder.update();
	    	  System.out.println("============设备名称和位置更新！=============="+devdto.getSpaceTypeId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    } 
	    
	    /**
	     * 根据devid删除设备
	     * @param devid
	     */
	    public void deleteByDevid(String devid){
	    	 try {
				 DeleteBuilder<Device, Integer> deleteBuilder = devdtoDao.deleteBuilder();  
				 deleteBuilder.where()
					     .eq("DEVICE_NO",devid);
				 deleteBuilder.delete();
			 } catch (SQLException e) {
				e.printStackTrace();
			 }
	    }
	    
	    
	    /**
	     * 同步设备信息成功后，先清空本地数据库中设备的基本信息
	     * @param gatewayno
	     */
	    public void deleteAllByGatewayNo(String gatewayno){
	    	 try {
				 DeleteBuilder<Device, Integer> deleteBuilder = devdtoDao.deleteBuilder();  
				 deleteBuilder.where()
					     .eq("GATEWAY_NO",gatewayno);
				 deleteBuilder.delete();
				 System.out.println("清空了本地的设备信息");
			 } catch (SQLException e) {
				e.printStackTrace();
			 }
	    }

	    /**
	     * 根据devid更新devstate【设备控制更新到本地】
	     * @param devid
	     * @param devstate
	     */
	    public void updateDevStateByDeviceNo(Device devdto){
	    	String devno=devdto.getDeviceNo();
	    	String devstate=devdto.getDeviceStateCmd();
	    	try {
	    		UpdateBuilder<Device, Integer> updateBuilder = devdtoDao.updateBuilder();  
				      updateBuilder.where().eq("DEVICE_NO",devno);
				      updateBuilder.updateColumnValue("DEVICE_STATE_CMD", devstate);
				      updateBuilder.update();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	    
	    
	    /**
	     * 删除设备
	     * @param 
	     */
	    public void deleteDeviceByDeviceno(Device devdto)  
	    {  
	    	String deviceno=devdto.getDeviceNo();
			 try {
				 DeleteBuilder<Device, Integer> deleteBuilder = devdtoDao.deleteBuilder();  
				 deleteBuilder.where()
					     .eq("DEVICE_NO",deviceno);
				 deleteBuilder.delete();
//				 System.out.println("执行了一条设备删除操作！");
			 } catch (SQLException e) {
				e.printStackTrace();
			 }
	    } 
	    
	    /**
	     * 根据网关号查找对应的设备
	     * @param gatewayId
	     * @return
	     */
	    public List<Device> findDevListByGatewayidAndPhonenum(String  gatewayId)  
	    {  
	    	List<Device> devicelist=new ArrayList<Device>();
	        try  
	        {  
	        	devicelist= devdtoDao.queryBuilder().where()
	            		.eq("GATEWAY_NO", gatewayId)
	                    .query();  
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        return devicelist;  
	    } 
	    
	    
	    /**
	     * 根据设备类型查找情景类开关 
	     * @param devtype
	     * @return
	     */
	    public List<Device> findDevBydevtypeAndGatewayid(int devtype,String gatewayid)  
	    {  
	    	List<Device> devicelist=new ArrayList<Device>();
	        try  
	        {  
	        	devicelist=devdtoDao.queryBuilder().where()
	            		.eq("DEVICE_TYPE_ID", devtype).and()
	            		.eq("GATEWAY_NO", gatewayid)
	                    .query();  
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        return devicelist;  
	    } 
	    
	    /**
	     * 根据devid查找设备
	     * @param devid
	     * @return
	     */
	    public Device findDevByDeviceNoAndGatewayNo(String deviceNo,String gatewayId){
	    	 try  
		        {  
	    		  Device device=devdtoDao.queryBuilder().where()
	    				    .eq("GATEWAY_NO", gatewayId).and()
							.eq("DEVICE_NO", deviceNo).queryForFirst();
					return device;
		        } catch (SQLException e)  
		        {  
		            e.printStackTrace();  
		        }  
		        return null;  
	    }
	    
	   
	    public boolean isNewDevByDeviceNo(String  devid,String phonenum){
	    	List<Device>  devslist=null;
	    	 try  
		        {  
	    		 devslist= devdtoDao.queryBuilder().where()
		            		.eq("DEVICE_NO", devid)
		            		.query();
	    		 return devslist.isEmpty() ? true : false;
		        } catch (SQLException e)  
		        {  
		            e.printStackTrace();  
		        }  
		        return false;  
	    }
	    
	    /**
	     * 根据网关号,大类号加载设备列表
	     * @param gatewayId
	     * @return
	     */
	    public List<Device> switchListBygwId(String  gatewayId,int categoryId)  
	    {  
	    	List<Device> devicelist=new ArrayList<Device>();
	        try  
	        {  
	        	devicelist=devdtoDao.queryBuilder().where()
	            		.eq("GATEWAY_NO", gatewayId).and()
	            		.eq("DEVICE_CATEGORY_ID",categoryId).query();  
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        return devicelist;  
	    }
	    /**
	     * 查找网关号所有设备列表
	     * @param gatewayId
	     * @param spaceId
	     * @return
	     */
	public List<Device> deviceListByGatewayId(String  gatewayId)  
	    {  
		  List<Device> devicelist=new ArrayList<Device>();
	        try  
	        {  
	        	devicelist= devdtoDao.queryBuilder().where()
	            		.eq("GATEWAY_NO", gatewayId)
	            		.query();  
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        return devicelist;  
	    }
	    
	/**
	 * 根据Device查找对应防区传感器类设备
	 */
	    public List<Device> findSensorDevicesByAlertypeId(int spaceTypeId)  
	    {  
	    	List<Device> devicelist=new ArrayList<Device>();
	    	String gatewayNo=SystemValue.gatewayid;
	    	int categoryId=SystemValue.SENSOR;
	        try  
	        {  
	        	devicelist= devdtoDao.queryBuilder().where()
	            		.eq("GATEWAY_NO", gatewayNo).and()
	            		.eq("SPACE_TYPE_ID", spaceTypeId).and()
	            		.eq("DEVICE_CATEGORY_ID",categoryId).query();  
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        return devicelist;  
	    }
    
//    public List<Device> findSensorDevicesByAlertypeId(int spaceTypeId)  
//    {  
//    	List<Device> devSensorList=new ArrayList<Device>();
//    	String gatewayNo=SystemValue.gatewayid;
//    	int categoryId=SystemValue.SENSOR;
//        try  
//        {  
//        	devSensorList= devdtoDao.queryBuilder().where()
//            		.eq("GATEWAY_NO", gatewayNo).and()
//            		.eq("DEVICE_CATEGORY_ID",categoryId).query();  
//        } catch (SQLException e)  
//        {  
//            e.printStackTrace();  
//        }  
//        
//        List<Device> devAlertList=new ArrayList<Device>();
//        for(int i=0;i<devSensorList.size();i++){
//        	Device devSensor=devSensorList.get(i);
//        	int devType=devSensor.getDeviceTypeId();
//        	if(devType!=SystemValue.DEV_FANS){
//        		UserSpaceDevice userDevice=new UserSpaceDevDao(null).
//            			findDeviceSpace(SystemValue.phonenum, devSensor.getDeviceNo());
//                int spaceType=userDevice.getSpaceType();
//            	if(spaceType==spaceTypeId){
//            		devAlertList.add(devSensor);
//            	}
//        	}
//        }
//        return devAlertList;  
//    }
    
	    
	    /**
	     * 根据网关查找温湿度
	     * @param gatewayid
	     * @return
	     */
	    public Device findDevTempHumiByGwId(String gatewayid,int devtype){
	    	 try  
		        {  
	    		  Device device=devdtoDao.queryBuilder().where()
	    				    .eq("GATEWAY_NO", gatewayid).and()
							.eq("DEVICE_TYPE_ID",devtype).queryForFirst();
					return device;
		        } catch (SQLException e)  
		        {  
		            e.printStackTrace();  
		        }  
		        return null;  
	    }
}
