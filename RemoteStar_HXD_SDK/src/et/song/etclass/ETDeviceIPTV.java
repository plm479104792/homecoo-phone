package et.song.etclass;

import et.song.remote.face.IR;
import et.song.remote.face.IRKeyValue;
import et.song.remote.face.IRKeyExValue;
import et.song.remote.instance.IPTV;


public class ETDeviceIPTV extends ETDevice {
	private IR iptv = null;
	public ETDeviceIPTV() {
		iptv = new IPTV();
		for (int i = 0; i < IRKeyValue.IPTV_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_STUDY);
			key.SetKey(IRKeyValue.IPTVValue | (i * 2 + 1));
			key.SetDID(0);
			key.SetBrandIndex(0);
			key.SetBrandPos(0);
			key.SetName("");
			key.SetPos(0, 0);
			key.SetRes(0);
			key.SetRow(0);
			SetKey(key);
		}
		for (int i = 0; i < IRKeyExValue.IPTV_KEYEX_COUNT ; i++){
			ETKeyEx key = new ETKeyEx();
			key.SetKey(IRKeyExValue.IPTVExValue | (i * 2 + 1));
			try {
				key.SetValue(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKeyEx(key);
		}
	}
	public ETDeviceIPTV(int row) {
		iptv = new IPTV();
		for (int i = 0; i < IRKeyValue.IPTV_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_TYPE);
			key.SetKey(IRKeyValue.IPTVValue | (i * 2 + 1));
			key.SetRow(row);
			try {
				key.SetValue(iptv.Search(row, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
		for (int i = 0; i < IRKeyExValue.IPTV_KEYEX_COUNT ; i++){
			ETKeyEx key = new ETKeyEx();
			key.SetKey(IRKeyExValue.IPTVExValue | (i * 2 + 1));
			try {
				key.SetValue(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKeyEx(key);
		}
	}
	
	public ETDeviceIPTV(int row, int col) {
		iptv = new IPTV();
		for (int i = 0; i < IRKeyValue.IPTV_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(IRKeyValue.IPTVValue | (i * 2 + 1));
			key.SetBrandIndex(row);
			key.SetBrandPos(col);
			try {
				key.SetValue(iptv.Search(row, col, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
		for (int i = 0; i < IRKeyExValue.IPTV_KEYEX_COUNT ; i++){
			ETKeyEx key = new ETKeyEx();
			key.SetKey(IRKeyExValue.IPTVExValue | (i * 2 + 1));
			try {
				key.SetValue(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKeyEx(key);
		}
	}

}
