package object.p2pipcam.utils;

import java.util.LinkedList;
import java.util.List;

public class VideoBuffer {
	private List<byte[]> DataBuffer = null;

	public VideoBuffer() {
		// TODO Auto-generated constructor stub
		DataBuffer = new LinkedList<byte[]>();
	}

	public boolean addData(byte[] data) {
		synchronized (this) {
			DataBuffer.add(data);
			return true;
		}
	}

	public byte[] RemoveData() {
		synchronized (this) {
			if (DataBuffer.isEmpty()) {
				return null;
			}
			return DataBuffer.remove(0);
		}
	}

	public int size() {
		return DataBuffer.size();
	}

	public void ClearAll() {
		synchronized (this) {
			DataBuffer.clear();
		}
	}
}