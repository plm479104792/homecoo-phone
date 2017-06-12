package et.song.remotestar;


public class AdapterSlidingItem {
	private String mName = "";
	private String mContext = "";
	private int mRes = 0;
	private int mType = 0;
	private boolean mIsGroup = false;
	private int mGroupPos = 0;
	private int mDevicePos = 0;

	public AdapterSlidingItem() {
		super();
	}

	public AdapterSlidingItem(String name, String context, boolean isGroup, int res, int type, int gpos, int dpos) {
		super();
		mName = name;
		mContext = context;
		mRes = res;
		mType = type;
		mIsGroup = isGroup;
		mGroupPos = gpos;
		mDevicePos = dpos;

	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}



	public String getContext() {
		return mContext;
	}

	public void setContext(String context) {
		mContext = context;
	}


	public void setRes(int res) {
		mRes = res;
	}

	public int getRes() {
		return mRes;
	}

	public void setType(int type) {
		mType = type;
	}

	public int getType() {
		return mType;
	}
	public void setGroupPos(int pos) {
		mGroupPos = pos;
	}

	public int getGroupPos() {
		return mGroupPos;
	}
	public void setDevicePos(int pos) {
		mDevicePos = pos;
	}

	public int getDevicePos() {
		return mDevicePos;
	}
	
	public boolean isGroup(){
		return mIsGroup;
	}
}
