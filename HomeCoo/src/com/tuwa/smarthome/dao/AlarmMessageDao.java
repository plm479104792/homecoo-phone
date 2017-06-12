package com.tuwa.smarthome.dao;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.tuwa.smarthome.database.DatabaseHelper;
import com.tuwa.smarthome.entity.AlarmMessage;
import com.tuwa.smarthome.entity.Device;

public class AlarmMessageDao {
	private Context context;
	private Dao<AlarmMessage, Integer> alarmMessageDao;
	private DatabaseHelper helper;

	@SuppressWarnings("unchecked")
	public AlarmMessageDao(Context context) {
		this.context = context;
		try {
			helper = DatabaseHelper.getHelper(context);
			alarmMessageDao = helper.getDao(AlarmMessage.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加一个空间
	 * 
	 * @param user
	 */
	public void add(AlarmMessage message) {
	
		try {
			alarmMessageDao.create(message);
//			System.out.println("执行了一条报警插入操作！");
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		long num=getAlarmMessageNum();
		if(num>30){
			AlarmMessage msg;
			try {
				msg = alarmMessageDao.queryBuilder().queryForFirst();
				alarmMessageDao.delete(msg);
//				System.out.println("每当数据库中AlarmMessage超过30条，删除第一条数据！");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取数据库中AlarmMessage数量
	 * @return
	 */
	public long getAlarmMessageNum(){
		long num = 0;
		try {
			num = alarmMessageDao.countOf();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;
		
	}
	
	/**
	 * 查找对应网关下的安防报警西欧阿西
	 * @param gatewayId
	 * @return
	 */
    public List<AlarmMessage> findAlarmMsgByGatewayid(String  gatewayId)  
    {  
        try  
        {  
            return alarmMessageDao.queryBuilder()
            		.orderBy("id", false).where()
            		.eq("GATEWAY_NO", gatewayId)
                    .query();  
        } catch (SQLException e)  
        {  
            e.printStackTrace();  
        }  
        return null;  
    } 


}
