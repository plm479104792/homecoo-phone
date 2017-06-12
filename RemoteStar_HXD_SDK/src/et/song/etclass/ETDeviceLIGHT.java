package et.song.etclass;

import et.song.remote.face.IR;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.LIGHT;


public class ETDeviceLIGHT extends ETDevice {
	private IR light = null;
	public ETDeviceLIGHT() {
		light = new LIGHT();
		for (int i = 0; i < IRKeyValue.LIGHT_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_STUDY);
			key.SetKey(IRKeyValue.LIGHTValue | (i * 2 + 1));
			try {
				key.SetValue("".getBytes());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}
	public ETDeviceLIGHT(int row) {
		light = new LIGHT();
		for (int i = 0; i < IRKeyValue.LIGHT_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_TYPE);
			key.SetKey(IRKeyValue.LIGHTValue | (i * 2 + 1));
			key.SetRow(row);
			try {
				key.SetValue(light.Search(row, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
		
	}
	
	public ETDeviceLIGHT(int row, int col) {
		light = new LIGHT();
		for (int i = 0; i < IRKeyValue.LIGHT_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(IRKeyValue.LIGHTValue | (i * 2 + 1));
			key.SetBrandIndex(row);
			key.SetBrandPos(col);
			try {
				key.SetValue(light.Search(row, col, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}

}
