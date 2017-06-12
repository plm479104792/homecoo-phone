package et.song.etclass;

import et.song.remote.face.IR;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.DC;


public class ETDeviceDC extends ETDevice {
	private IR dc = null;
	public ETDeviceDC() {
		dc = new DC();
		for (int i = 0; i < IRKeyValue.DC_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_STUDY);
			key.SetKey(IRKeyValue.DCValue | (i * 2 + 1));
			try {
				key.SetValue("".getBytes());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}
	public ETDeviceDC(int row) {
		dc = new DC();
		for (int i = 0; i < IRKeyValue.DC_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_TYPE);
			key.SetKey(IRKeyValue.DCValue | (i * 2 + 1));
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
	
	public ETDeviceDC(int row, int col) {
		dc = new DC();
		for (int i = 0; i < IRKeyValue.DC_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(IRKeyValue.DCValue | (i * 2 + 1));
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
