package et.song.remotestar;


public class AdapterWatchTVItem {
	private String mName = "";
	private String mContext = "";
	private int mRes = 0;
	private int mID = 0;
	private int mDID = 0;
	private String mValue = "";
	private boolean mIsOK = false; 
	private boolean mIsSelect = false;
	public AdapterWatchTVItem() {
		super();
	}
	public AdapterWatchTVItem(int id, String name, String context, int res, int did, boolean isOK, boolean isSelect) {
		super();
		mName = name;
		mContext = context;
		mRes = res;
		mIsOK = isOK;
		mIsSelect = isSelect;
		mDID = did;
		mID = id;
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
	public void setDID(int pos) {
		mDID = pos;
	}
	public int getDID() {
		return mDID;
	}
	public void setID(int id) {
		mID = id;
	}
	public int getID() {
		return mID;
	}
	
	public void setValue(String value) {
		mValue = value;
	}
	public String getValue() {
		return mValue;
	}
	public boolean isOK()
	{
		return mIsOK;
	}
	public void setOK(boolean ok)
	{
		mIsOK = ok;
	}
	public boolean isSelect()
	{
		return mIsSelect;
	}
	public void setSelect(boolean select)
	{
		mIsSelect = select;
	}
}
