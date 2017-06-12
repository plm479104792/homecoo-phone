package et.song.etclass;



public class ETDeviceCustom extends ETDevice {
	public ETDeviceCustom() {
		
	}
	public byte[] GetKeyValue(int value) throws Exception {
		ETKey key = GetKeyByValue(value);
		if (key.GetState() == ETKey.ETKEY_STATE_STUDY){
			return Study(key.GetValue());
		}
		return null;
	}
	
}
