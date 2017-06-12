package et.song.etclass;

import et.song.remote.face.IR;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.POWER;


public class ETDevicePower extends ETDevice {
	private IR dc = null;
	public ETDevicePower() {
		dc = new POWER();
		for (int i = 0; i < IRKeyValue.POWER_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_STUDY);
			key.SetKey(IRKeyValue.POWERValue | (i * 2 + 1));
			try {
				key.SetValue("".getBytes());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}
	public ETDevicePower(int row) {
		dc = new POWER();
		for (int i = 0; i < IRKeyValue.POWER_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_TYPE);
			key.SetKey(IRKeyValue.POWERValue | (i * 2 + 1));
			key.SetRow(row);
			try {
				key.SetValue(dc.Search(row, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}
	
	public ETDevicePower(int row, int col) {
		dc = new POWER();
		for (int i = 0; i < IRKeyValue.POWER_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(IRKeyValue.POWERValue | (i * 2 + 1));
			key.SetBrandIndex(row);
			key.SetBrandPos(col);
			try {
				key.SetValue(dc.Search(row, col, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}

}
