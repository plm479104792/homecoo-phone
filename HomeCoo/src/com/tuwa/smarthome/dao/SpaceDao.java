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
import com.tuwa.smarthome.entity.UserSpaceDevice;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.SystemValue;

public class SpaceDao {
	private Context context;
	private Dao<Space, Integer> spaceTypeDao;
	private DatabaseHelper helper;

	@SuppressWarnings("unchecked")
	public SpaceDao(Context context) {
		this.context = context;
		try {
			helper = DatabaseHelper.getHelper(context);
			spaceTypeDao = helper.getDao(Space.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加一个空间
	 * 
	 * @param user
	 */
	public void add(Space space) {
		String spaceNo = space.getSpaceNo();
		if (isUseableSpace(spaceNo)) {
			try {
				spaceTypeDao.create(space);
				System.out.println("执行了一条空间插入操作！");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("该房间名称已经存在！");
		}

	}

	// /**
	// * 更新空间
	// *
	// * @param spaceType
	// */
	// public void update(Space spaceType) {
	// try {
	// spaceTypeDao.createOrUpdate(spaceType);
	// System.out.println("执行了一条空间更新操作！");
	//
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * 删除空间
	 * 
	 * @param spaceType
	 */
	public void deleteSpaceBySpaceNo(Space space) {
		String spaceNo = space.getSpaceNo();
		try {
			DeleteBuilder<Space, Integer> deleteBuilder = spaceTypeDao
					.deleteBuilder();
			deleteBuilder.where().eq("SPACE_NO", spaceNo);
			deleteBuilder.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 验证空间表中是否已有此空间名称
	 * 
	 * @param spacename
	 * @return
	 */
	public boolean isUseableSpace(String phoneSpaceId) {
		List<Space> spacelist = null;
		try {
			spacelist = spaceTypeDao.queryBuilder().where()
					.eq("SPACE_NO", phoneSpaceId).query();
//			System.out.println("=====空间列表中=====" + spacelist.size());
			return spacelist.isEmpty() ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 根据gatewayid判断空间名称是否存在
	 * 
	 * @param wgid
	 * @param spacename
	 * @return
	 */
	public boolean isUseableSpaceByWgid(String spacename) {
		List<Space> spacelist = null;
		String phonenum = SystemValue.phonenum;
		try {
			spacelist = spaceTypeDao.queryBuilder().where()
					.eq("PHONE_NUM", phonenum)
					.and().eq("SPACE_NAME", spacename).query();
//			System.out.println("=====空间列表中=====" + spacelist.size());
			return spacelist.isEmpty() ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 根据空间名称返回空间对象
	 * 
	 * @param spacename
	 * @return
	 */
	public String getSpacenameBySpaceNo(String spaceNo) {
		Space space = null;
		try {
			space = spaceTypeDao.queryBuilder().where().eq("SPACE_NO", spaceNo)
					.queryForFirst();
			if (space != null) {
				return space.getSpaceName();
			}else{
				return "位置待定";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据网关id返回空间列表
	 * 
	 * @param gatewayId
	 * @return
	 */
	public List<Space> getSpaceByPhonenum(String phonenum) {
		List<Space> spacelist = new ArrayList<Space>();
		try {
			spacelist = spaceTypeDao.queryBuilder().where()
					.eq("PHONE_NUM", phonenum).query();
			return spacelist;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return spacelist;
	}

    /**
     * 注销用户下空间信息
     * @param phone
     */
    public void deleteSpaceByPhoneno(String phoneno)  
    {  
		 try {
			 DeleteBuilder<Space, Integer> deleteBuilder = spaceTypeDao.deleteBuilder();  
			 deleteBuilder.where()
				     .eq("PHONE_NUM",phoneno);
			 deleteBuilder.delete();
//			 System.out.println("清空了用户的房间信息！");
		 } catch (SQLException e) {
			e.printStackTrace();
		 }
    }
}
