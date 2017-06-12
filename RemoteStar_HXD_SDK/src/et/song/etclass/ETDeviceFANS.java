package et.song.etclass;

import et.song.remote.face.IR;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.FANS;


public class ETDeviceFANS extends ETDevice {
	private IR fans = null;
	public ETDeviceFANS() {
		fans = new FANS();
	}
	public ETDeviceFANS(int row) {
		fans = new FANS();
		for (int i = 0; i < IRKeyValue.FANS_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_TYPE);
			key.SetKey(IRKeyValue.FANSValue | (i * 2 + 1));
			key.SetRow(row);
			try {
				key.SetValue(fans.Search(row, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
		
	}
	
	public ETDeviceFANS(int row, int col) {
		fans = new FANS();
		for (int i = 0; i < IRKeyValue.FANS_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(IRKeyValue.FANSValue | (i * 2 + 1));
			key.SetBrandIndex(row);
			key.SetBrandPos(col);
			try {
				key.SetValue(fans.Search(row, col, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}

}
