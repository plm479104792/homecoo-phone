package et.song.etclass;

import et.song.remote.face.IR;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.STB;


public class ETDeviceSTB extends ETDevice {
	private IR stb = null;
	public ETDeviceSTB() {
		stb = new STB();
	}
	public ETDeviceSTB(int row) {
		stb = new STB();
		for (int i = 0; i < IRKeyValue.STB_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_TYPE);
			key.SetKey(IRKeyValue.STBValue | (i * 2 + 1));
			key.SetRow(row);
			try {
				key.SetValue(stb.Search(row, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
		
	}
	
	public ETDeviceSTB(int row, int col) {
		stb = new STB();
		for (int i = 0; i < IRKeyValue.STB_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(IRKeyValue.STBValue | (i * 2 + 1));
			key.SetBrandIndex(row);
			key.SetBrandPos(col);
			try {
				key.SetValue(stb.Search(row, col, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}

}
