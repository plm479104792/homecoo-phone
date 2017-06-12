package et.song.etclass;

import et.song.remote.face.IR;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.PJT;


public class ETDevicePJT extends ETDevice {
	private IR pjt = null;
	public ETDevicePJT() {
		pjt = new PJT();
	}
	public ETDevicePJT(int row) {
		pjt = new PJT();
		for (int i = 0; i < IRKeyValue.PJT_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_TYPE);
			key.SetKey(IRKeyValue.PJTValue | (i * 2 + 1));
			key.SetRow(row);
			try {
				key.SetValue(pjt.Search(row, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
		
	}
	
	public ETDevicePJT(int row, int col) {
		pjt = new PJT();
		for (int i = 0; i < IRKeyValue.PJT_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(IRKeyValue.PJTValue | (i * 2 + 1));
			key.SetBrandIndex(row);
			key.SetBrandPos(col);
			try {
				key.SetValue(pjt.Search(row, col, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}

}
