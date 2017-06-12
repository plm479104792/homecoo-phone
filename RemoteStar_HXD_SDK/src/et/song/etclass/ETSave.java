package et.song.etclass;

import android.content.Context;
import android.content.SharedPreferences;

public class ETSave{
	private SharedPreferences mSave;
	private static ETSave instance = null;

	private ETSave(Context activity) {
		mSave = activity.getSharedPreferences("info", 0);
	}

	public static final ETSave getInstance(Context context) {
		if (instance == null) {
			instance = new ETSave(context);
		}
		return instance;
	}

	public void put(String key, String value)
	{
		mSave.edit().putString(key, value).commit();
	}
	public String get(String key){
		return  mSave.getString(key, "");
	}
}