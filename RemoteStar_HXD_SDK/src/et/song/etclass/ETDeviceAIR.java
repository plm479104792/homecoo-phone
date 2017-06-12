package et.song.etclass;

import android.content.ContentValues;
import android.database.Cursor;
import et.song.db.DBProfile;
import et.song.db.ETDB;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.AIR;

public class ETDeviceAIR extends ETDevice {
	private AIR mAir;

	public ETDeviceAIR() {
		mAir = new AIR();
	}

	public ETDeviceAIR(int row) {
		mAir = new AIR();
		for (int i = 0; i < IRKeyValue.AIR_KEY_COUNT; i++) {
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_TYPE);
			key.SetKey(IRKeyValue.AIRValue | (i * 2 + 1));
			key.SetRow(row);
			try {
				key.SetValue(mAir.Search(row, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}

	}

	public ETDeviceAIR(int row, int col) {
		mAir = new AIR();
		for (int i = 0; i < IRKeyValue.AIR_KEY_COUNT; i++) {
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(IRKeyValue.AIRValue | (i * 2 + 1));
			key.SetBrandIndex(row);
			key.SetBrandPos(col);
			try {
				key.SetValue(mAir.Search(row, col, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}

	public byte[] GetKeyValue(int value) throws Exception {
		ETKey key = GetKeyByValue(value);
		if (key.GetState() == ETKey.ETKEY_STATE_STUDY) {
			return Study(key.GetValue());
		}
		if (key.GetState() == ETKey.ETKEY_STATE_TYPE) {
			return mAir.Search(key.GetRow(), value);
		}
		if (key.GetState() == ETKey.ETKEY_STATE_KNOWN) {
			return mAir.Search(key.GetBrandIndex(), key.GetBrandPos(), value);
		}
		return null;
	}

	/**
	 * 返回当前温度 16-30
	 * 
	 * @return
	 */
	public byte GetTemp() {
		return mAir.GetTemp();
	}
	public void SetTemp(byte temp) {
		mAir.SetTemp(temp);
	}
	/**
	 * 1~4
	 * 
	 * @return
	 */
	public byte GetWindRate() {
		return mAir.GetWindRate();
	}
	public void SetWindRate(byte rate) {
		mAir.SetWindRate(rate);
	}
	/**
	 * 1~3
	 * 
	 * @return
	 */
	public byte GetWindDir() {
		return mAir.GetWindDir();
	}
	public void SetWindDir(byte dir) {
		mAir.SetWindDir(dir);
	}
	/**
	 * 返回自动风向状态 0:手动 1:自动
	 * 
	 * @return
	 */
	public byte GetAutoWindDir() {
		return mAir.GetAutoWindDir();
	}
	public void SetAutoWindDir(byte dir) {
		mAir.SetAutoWindDir(dir);
	}
	/**
	 * 返回当前模式的装态 1： 2： 3: 4: 5:
	 * 
	 * @return
	 */
	public byte GetMode() {
		return mAir.GetMode();
	}
	public void SetMode(byte mode) {
		mAir.SetMode(mode);
	}
	/**
	 * 返回当前空调状态 0:关机 1:开机
	 * 
	 * @return
	 */
	public byte GetPower() {
		return mAir.GetPower();
	}
	public void SetPower(byte power) {
		mAir.SetPower(power);
	}
	public void Load(ETDB db) {
		super.Load(db);
		try {
			Cursor c = db.queryData2Cursor("select * from "
					+ DBProfile.AIRDEVICE_TABLE_NAME + " where "
					+ DBProfile.TABLE_AIRDEVICE_FIELD_DEVICE_ID + " = " + GetID(), null);
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				int temp = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_AIRDEVICE_FIELD_TEMP));
				this.SetTemp((byte)temp);
				int rate = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_AIRDEVICE_FIELD_RATE));
				this.SetWindRate((byte)rate);
				int dir = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_AIRDEVICE_FIELD_DIR));
				this.SetWindDir((byte)dir);
				
				int autoDir = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_AIRDEVICE_FIELD_AUTO_DIR));
				this.SetAutoWindDir((byte)autoDir);
				
				int mode = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_AIRDEVICE_FIELD_MODE));
				this.SetMode((byte)mode);
				
				int power = c.getInt(c
						.getColumnIndex(DBProfile.TABLE_AIRDEVICE_FIELD_POWER));
				this.SetPower((byte)power);

			}
			c.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void Update(ETDB db) {
		super.Update(db);
		ContentValues value = new ContentValues();
		value.put(DBProfile.TABLE_AIRDEVICE_FIELD_DEVICE_ID, GetID());
		value.put(DBProfile.TABLE_AIRDEVICE_FIELD_TEMP, GetTemp());
		value.put(DBProfile.TABLE_AIRDEVICE_FIELD_RATE, this.GetWindRate());
		value.put(DBProfile.TABLE_AIRDEVICE_FIELD_DIR, this.GetWindDir());
		value.put(DBProfile.TABLE_AIRDEVICE_FIELD_AUTO_DIR,
				this.GetAutoWindDir());
		value.put(DBProfile.TABLE_AIRDEVICE_FIELD_MODE, this.GetMode());
		value.put(DBProfile.TABLE_AIRDEVICE_FIELD_POWER, this.GetPower());
		try {
			db.updataData(DBProfile.AIRDEVICE_TABLE_NAME, value,
					DBProfile.TABLE_AIRDEVICE_FIELD_DEVICE_ID + " = ?",
					new String[] { String.valueOf(GetID()) });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void Delete(ETDB db) {
		// TODO Auto-generated method stub
		try {
			db.deleteData(DBProfile.AIRDEVICE_TABLE_NAME,
					DBProfile.TABLE_AIRDEVICE_FIELD_DEVICE_ID + " = ?",
					new String[] { String.valueOf(GetID()) });
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.Delete(db);
		
	}

	@Override
	public void Inster(ETDB db) {
		// TODO Auto-generated method stub
		super.Inster(db);
		try {
			
			Cursor cursor = db.queryData2Cursor("select count(*) from "
					+ DBProfile.DEVICE_TABLE_NAME + " order by "
					+ DBProfile.TABLE_DEVICE_FIELD_ID + " desc", null);
			cursor.moveToFirst();
			long count = cursor.getLong(0);
			cursor.close();
			if (count == 0)
				return;
			
			Cursor c = db.queryData2Cursor("select * from "
					+ DBProfile.DEVICE_TABLE_NAME + " order by "
					+ DBProfile.TABLE_DEVICE_FIELD_ID + " desc", null);
			c.moveToFirst();
			int id = c
					.getInt(c.getColumnIndex(DBProfile.TABLE_DEVICE_FIELD_ID));
			ContentValues value = new ContentValues();
			value.put(DBProfile.TABLE_AIRDEVICE_FIELD_DEVICE_ID, id);
			value.put(DBProfile.TABLE_AIRDEVICE_FIELD_TEMP, GetTemp());
			value.put(DBProfile.TABLE_AIRDEVICE_FIELD_RATE, this.GetWindRate());
			value.put(DBProfile.TABLE_AIRDEVICE_FIELD_DIR, this.GetWindDir());
			value.put(DBProfile.TABLE_AIRDEVICE_FIELD_AUTO_DIR,
					this.GetAutoWindDir());
			value.put(DBProfile.TABLE_AIRDEVICE_FIELD_MODE, this.GetMode());
			value.put(DBProfile.TABLE_AIRDEVICE_FIELD_POWER, this.GetPower());
			db.insertData(DBProfile.AIRDEVICE_TABLE_NAME, value);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
