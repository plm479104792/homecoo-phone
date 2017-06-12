package com.tuwa.smarthome.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.bool;
import android.widget.EditText;

public class VerifyUtils {

	public static boolean isNull(EditText editText) {
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 账号校验
	 * 
	 * @param text
	 * @账号长度为6-18位
	 */
	public static boolean matchAccount(String text) {
		if (Pattern.compile("^[a-z0-9_-]{5,18}$").matcher(text).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * Ip地址校验
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean matchIpAddress(String ip) {
		Pattern pa = Pattern
				.compile("^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$");
		Matcher ma = pa.matcher(ip);
		boolean b = ma.matches();
		return b;
	}

	/**
	 * 手机号码校验
	 * 
	 * @param text
	 * @return
	 */
	public static boolean matchPhoneno(String text) {
		Pattern p = Pattern
				.compile("^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))//d{8$}");
		// /(^1[3|4|5|7|8][0-9]{9}$)/;
		Matcher m = p.matcher(text);
		return m.matches();
	}

	public static boolean isMobileNO(String mobiles) {
		// Pattern p =
		// Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0,5-9]))\\d{8}$");
		Pattern p = Pattern.compile("^1[3|4|5|7|8][0-9]{9}$");
		Matcher m = p.matcher(mobiles);
		// logger.info(m.matches()+"---");
		return m.matches();
	}

	/**
	 * 校验网关编号
	 * @param gatewayno
	 * @return
	 */
	public static boolean isGatewayNO(String gatewayno) {

		Pattern p = Pattern.compile("^[0-9]{16}$");    //长度为16的数字串
		Matcher m = p.matcher(gatewayno);

		return m.matches();
	}

	/**
	 * 邮箱校验
	 * 
	 * @param text
	 * @return
	 */
	public static boolean matchEmail(String text) {
		if (Pattern.compile("\\w[\\w.-]*@[\\w.]+\\.\\w+").matcher(text)
				.matches()) {
			return true;
		}
		return false;
	}

	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		// logger.info(m.matches()+"---");
		return m.matches();
	}
	
    /**
     * is null or its length is 0
     *
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
     * </pre>
     *
     * @param str str
     * @return if string is null or its size is 0, return true, else return
     * false.
     */
    public static boolean isEmpty(CharSequence str) {

        return (str == null || str.length() == 0);
    }

}
