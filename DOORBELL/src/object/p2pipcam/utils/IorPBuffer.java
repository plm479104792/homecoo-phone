package object.p2pipcam.utils;

import java.util.LinkedList;
import java.util.List;

public class IorPBuffer {
	private List<Integer> DataBuffer = null;

	public IorPBuffer() {
		// TODO Auto-generated constructor stub
		DataBuffer = new LinkedList<Integer>();
	}

	public boolean addData(int IorP) {
		synchronized (this) {
			DataBuffer.add(IorP);
			return true;
		}
	}

	public int RemoveData() {
		synchronized (this) {
			if (DataBuffer.isEmpty()) {
				return 0;
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