package object.p2pipcam.utils;

import java.util.LinkedList;
import java.util.List;

public class AudioBuffer {
	private List<byte[]> DataBuffer = null;

	public AudioBuffer() {
		// TODO Auto-generated constructor stub
		DataBuffer = new LinkedList<byte[]>();
	}

	public boolean addAudioData(byte[] data) {
		synchronized (this) {
			DataBuffer.add(data);
			return true;
		}
	}

	public byte[] RemoveAudioData() {
		synchronized (this) {
			if (DataBuffer.isEmpty()) {
				return null;
			}
			return DataBuffer.remove(0);
		}
	}

	public void ClearAudioAll() {
		synchronized (this) {
			DataBuffer.clear();
		}
	}
}