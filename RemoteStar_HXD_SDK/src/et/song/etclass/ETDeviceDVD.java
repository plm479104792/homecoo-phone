package et.song.etclass;

import et.song.remote.face.IR;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.DVD;


public class ETDeviceDVD extends ETDevice {
	private IR dvd = null;
	public ETDeviceDVD() {
		dvd = new DVD();
	}
	public ETDeviceDVD(int row) {
		dvd = new DVD();
		for (int i = 0; i < IRKeyValue.DVD_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_TYPE);
			key.SetKey(IRKeyValue.DVDValue | (i * 2 + 1));
			key.SetRow(row);
			try {
				key.SetValue(dvd.Search(row, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
		
	}
	
	public ETDeviceDVD(int row, int col) {
		dvd = new DVD();
		for (int i = 0; i < IRKeyValue.DVD_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(IRKeyValue.DVDValue | (i * 2 + 1));
			key.SetBrandIndex(row);
			key.SetBrandPos(col);
			try {
				key.SetValue(dvd.Search(row, col, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}

}
