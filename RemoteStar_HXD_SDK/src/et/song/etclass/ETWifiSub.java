package et.song.etclass;

public class ETWifiSub {
	private int mID;
	private String mName;
	private int mType;
	private int mResId;

	public ETWifiSub() {

	}

	public void SetID(int id) {
		mID = id;
	}

	public int GetID() {
		return mID;
	}

	public void SetName(String name) {
		mName = name;
	}

	public String GetName() {
		return mName;
	}

	public void SetType(int type) {
		mType = type;
	}

	public int GetType() {
		return mType;
	}

	public void SetRes(int resId) {
		mResId = resId;
	}

	public int GetRes() {
		return mResId;
	}

}
