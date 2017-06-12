package et.song.remotestar;


public class AdapterPYinItem {
	private String mName = "";
	private String mPyin = "";
	private int mPos = 0;

	public AdapterPYinItem() {
		super();
	}

	public AdapterPYinItem(String name, String pyin, int pos) {
		super();
		mName = name;
		mPyin = pyin;
		mPos = pos;

	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}



	public String getPyin() {
		return mPyin;
	}

	public void setPyin(String pyin) {
		mPyin = pyin;
	}


	public void setPos(int pos) {
		mPos = pos;
	}

	public int getPos() {
		return mPos;
	}

}
