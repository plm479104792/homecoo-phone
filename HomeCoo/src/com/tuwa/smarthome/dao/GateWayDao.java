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
import com.tuwa.smarthome.entity.ThemeDevice;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;

public class GateWayDao {
	    private Context context; 
	    private Dao<Gateway, Integer> gatewayDao;  
	    private DatabaseHelper helper; 
	    
	    @SuppressWarnings("unchecked")
		public GateWayDao(Context context)  
	    {  
	        this.context = context;  
	        try  
	        {  
	            helper = DatabaseHelper.getHelper(context);  
	            gatewayDao = helper.getDao(Gateway.class);  
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	    } 
	    
	    /** 
	     * 增加一个网关
	     * @param user 
	     */  
	    public void add(Gateway gateway)  
	    {  
	        try  
	        {  
	            gatewayDao.create(gateway); 
//	            System.out.println("执行了一条插入网关操作！");
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	  
	    } 
	    
	    public void delete(Gateway gateway)  
	    {  
	        try  
	        {  
	            gatewayDao.delete(gateway); 
//	            System.out.println("执行了一条删除网关操作！");
	        } catch (SQLException e)  
	        {  
	            e.printStackTrace();  
	        }  
	    } 
	    
	    /**
	     * 根据GatewayNo删除网关
	     * @param gateway
	     */
	    public void deleteGatewayByGatewayNo(Gateway gateway) 
	    {
	    	String gatewayNo=gateway.getGatewayNo();
	    	try {
	    		DeleteBuilder<Gateway , Integer> deleteBuilder = gatewayDao.deleteBuilder();  
	    		      deleteBuilder.where()
						     .eq("GATEWAY_NO",gatewayNo);
	    		      deleteBuilder.delete();
//	    	  System.out.println("============网关删除！==============");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    /** 
	     * 根据userid和wgid更新网关IP
	     * @param user 
	     */  
	    public void updateGateWayByGatewayNo(Gateway gateway)  
	    {   
	    	String gatewayNo=gateway.getGatewayNo();
	    	String Ip=gateway.getGatewayIp();
	    	String wgpwd=gateway.getGatewayPwd();
	    	try {
	    		UpdateBuilder<Gateway , Integer> updateBuilder = gatewayDao.updateBuilder();  
				      updateBuilder.where()
						     .eq("GATEWAY_NO",gatewayNo);
				      updateBuilder.updateColumnValue("GATEWAY_IP", Ip);
				      updateBuilder.updateColumnValue("GATEWAY_PWD",wgpwd);
				      updateBuilder.update();
//	    	  System.out.println("============网关Ip和密码更新！=============="+wgpwd+gatewayNo+Ip);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    
	    
        /**
         * 根据网关编号查找网关信息
         * @param gatewayNo
         * @return
         */
	    public Gateway  getGatewayByGatewayNo(String gatewayNo){
	    	Gateway gateway=null;
	    	 try {
	    		 gateway= gatewayDao.queryBuilder().where()
	    				  .eq("GATEWAY_NO",gatewayNo).queryForFirst();
	    		  return gateway;
			 } catch (SQLException e) {
				e.printStackTrace();
			 }
	    	return null;
	    }
	    
}
