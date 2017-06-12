package com.cj.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.homecoolink.R;
import com.p2p.core.utils.MD5;

import android.content.Context;
import android.util.Log;

public class NetManager {
	private static String CURRENT_SERVER = "http://cloudlinks.cn:90/";
	private static String GetPhoneCode_URL = CURRENT_SERVER
			+ "Password/GetAccountByPhoneNO.ashx?callback=?";
	private static String CheckPhoneCode_URL = CURRENT_SERVER
			+ "Password/CheckPhoneVKey.ashx?callback=?";
	private static String UpdatePwd_URL = CURRENT_SERVER
			+ "Password/ResetPWD.ashx?callback=?";
	private static String SendEmail_URL = CURRENT_SERVER
			+ "Password/GetAccountByEmail.ashx?callback=?";
	private Context context;

	public NetManager(Context context) {
		this.context = context;
	}

	public JSONObject GetCheckCodeByPhone(String Phoneno) {

		JSONObject jObject = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CallbackType", "1"));
		params.add(new BasicNameValuePair("CountryCode", "86"));
		params.add(new BasicNameValuePair("PhoneNO", Phoneno));

		try {
			jObject = new JSONObject(doPost(params, GetPhoneCode_URL));
			Log.e("my", jObject.toString());
		} catch (Exception e) {

			e.printStackTrace();
		}

		return jObject;
	}

	public JSONObject GetCheckCodeByEmail(String email) {

		JSONObject jObject = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CallbackType", "1"));
		params.add(new BasicNameValuePair("Email", email));
		params.add(new BasicNameValuePair("MailTitle", context.getResources()
				.getString(R.string.forgetpwdactivity_tip1)));
		params.add(new BasicNameValuePair("BodyField1", context.getResources()
				.getString(R.string.forgetpwdactivity_tip2)));
		params.add(new BasicNameValuePair("BodyField2", context.getResources()
				.getString(R.string.forgetpwdactivity_tip3)
				+ "<br>"
				+ context.getResources().getString(
						R.string.forgetpwdactivity_tip4)
				+ "<br>"
				+ context.getResources().getString(
						R.string.forgetpwdactivity_tip5)));
		params.add(new BasicNameValuePair("BodyField3", context.getResources()
				.getString(R.string.forgetpwdactivity_tip6)
				+ "<br>"
				+ context.getResources().getString(
						R.string.forgetpwdactivity_tip7)));
		params.add(new BasicNameValuePair("_", "1436000794510"));
		try {
			jObject = new JSONObject(doPost(params, SendEmail_URL));
			Log.e("my", jObject.toString());
		} catch (Exception e) {

			e.printStackTrace();
		}

		return jObject;
	}

	public JSONObject CheckCodePhoneCode(String Phoneno, String Phonecode,
			String ID, String VKey) {

		JSONObject jObject = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CallbackType", "1"));
		params.add(new BasicNameValuePair("CountryCode", "86"));
		params.add(new BasicNameValuePair("PhoneNO", Phoneno));
		params.add(new BasicNameValuePair("PhoneVerifyCode", Phonecode));
		params.add(new BasicNameValuePair("VKey", VKey));
		params.add(new BasicNameValuePair("ID", ID));

		try {
			jObject = new JSONObject(doPost(params, CheckPhoneCode_URL));
			Log.e("my", jObject.toString());
		} catch (Exception e) {

			e.printStackTrace();
		}

		return jObject;
	}

	public JSONObject UpdatePassWord(String Pwd, String ID, String VKey) {

		JSONObject jObject = null;
		MD5 md = new MD5();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CallbackType", "1"));
		params.add(new BasicNameValuePair("VKey", VKey));
		params.add(new BasicNameValuePair("ID", ID));
		params.add(new BasicNameValuePair("NewPwd", md.getMD5ofStr(Pwd)));
		params.add(new BasicNameValuePair("ReNewPwd", md.getMD5ofStr(Pwd)));

		try {
			jObject = new JSONObject(doPost(params, UpdatePwd_URL));
			Log.e("my", jObject.toString());
		} catch (Exception e) {

			e.printStackTrace();
		}

		return jObject;
	}

	public String doPost(List<NameValuePair> params, String url)
			throws Exception {
		Log.e("my", "current-server:" + CURRENT_SERVER);
		String result = null;
		// 新建HttpPost对象
		HttpPost httpPost = new HttpPost(url);

		// 设置字符集
		HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		// 设置参数实体
		httpPost.setEntity(entity);
		httpPost.setHeader("Referer", "http://cloudlinks.cn/view/ReSetPwd.htm");
		// 获取HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();

		// 连接超时
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		// 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				10000);
		try {
			HttpResponse httpResp = httpClient.execute(httpPost);
			int http_code;
			if ((http_code = httpResp.getStatusLine().getStatusCode()) == 200) {
				result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
				Log.e("my", "original http:" + result);
			} else {
				Log.e("my", "HttpPost方式请求失败:" + http_code);
				// result = "{\"error_code\":998}";
				throw new Exception();
			}
			try {
				result = result.replaceAll("\\?\\(", "").replaceAll("\\)", "");
				JSONObject jObject = new JSONObject(result);
				int error_code = jObject.getInt("error_code");
				if (error_code == 1 || error_code == 29 || error_code == 999) {
					throw new Exception();
				}
			} catch (Exception e) {
				throw new Exception();
			}

		} catch (Exception e) {

			result = "{\"error_code\":999}";

		}

		return result;
	}

}
