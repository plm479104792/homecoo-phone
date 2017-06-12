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
import com.tuwa.smarthome.entity.Theme;
import com.tuwa.smarthome.entity.User;

public class UserDao {

	private Context context;
	private Dao<User, Integer> userDao;
	private DatabaseHelper helper;

	@SuppressWarnings("unchecked")
	public UserDao(Context context) {
		this.context = context;
		try {
			helper = DatabaseHelper.getHelper(context);
			userDao = helper.getDao(User.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加或更新用户
	 * 
	 * @param user
	 */
	public void insertOrUpdateUser(User user) {
		String phonenum = user.getPhonenum();
		if (isUseableLogincode(phonenum)) {   //新用户插入操作
			try {
				userDao.create(user);
				System.out.println("同步服务器user插入操作！");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else {   //更新用户密码
			updateUserByPhonenum(user);
		}

	}

	/**
	 * 验证账号是否已被注册
	 * 
	 * @param username
	 * @return
	 */
	public boolean isUseableLogincode(String phonenum) {
		List<User> userlist = null;
		try {
			userlist = userDao.queryBuilder().where().eq("PHONENUM", phonenum)
					.query();
			return userlist.isEmpty() ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// 获取所有的用户名(快捷登录)
	public List<String> getUsernameAll() {
		List<String> list = new ArrayList<String>();
		try {
			List<User> userlist = userDao
					.queryBuilder()
            		.orderBy("id", false)  //倒序排列
            		.query();
			if (userlist != null) {
				for (int i = 0; i < userlist.size(); i++) {
					list.add(userlist.get(i).getPhonenum());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据用户名和密码返回对应user对象
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public User getUser(String phonenum, String password) {
		try {
			User user = userDao.queryBuilder().where()
					.eq("PHONENUM", phonenum).and()
					.eq("PASSWORD", password).queryForFirst();
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

    //根据手机号注销用户
	public void deleteUserByPhoneno(String phoneno) {
		try {
			DeleteBuilder<User, Integer> deleteBuilder = userDao.deleteBuilder();
			deleteBuilder.where().eq("PHONENUM", phoneno);
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据phonenum更新密码
	 * @param user
	 */
	public void updateUserByPhonenum(User user) {
		String phonenum = user.getPhonenum();
		String password = user.getPassword();
		try {
			UpdateBuilder<User, Integer> updateBuilder = userDao
					.updateBuilder();
			updateBuilder.where().eq("PHONENUM", phonenum);
			updateBuilder.updateColumnValue("PASSWORD", password);
			updateBuilder.update();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据phonenum绑定GatewayNo
	 * @param user
	 */
	public void updateGatewayNoByPhonenum(String phonenum,String gatewayNo) {
		
		try {
			UpdateBuilder<User, Integer> updateBuilder = userDao
					.updateBuilder();
			updateBuilder.where().eq("PHONENUM", phonenum);
			updateBuilder.updateColumnValue("GATEWAY_NO", gatewayNo);
			updateBuilder.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
