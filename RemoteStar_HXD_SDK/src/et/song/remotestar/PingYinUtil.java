package et.song.remotestar;


import java.io.UnsupportedEncodingException;

import et.song.jni.ETPyin;
import et.song.jni.ETPyinEx;


public class PingYinUtil {
	public static String getPingYin(String inputString) {
		try {
			return ETPyin.Pyin(inputString, ETPyin.ETPYIN_ALLLETTER);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ETPyinEx.Pyin(inputString);
		//return ETPyinEx.Pyin(inputString);
	}

}
