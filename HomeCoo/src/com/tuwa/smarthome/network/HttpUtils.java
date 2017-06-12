package com.tuwa.smarthome.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tuwa.smarthome.entity.Index;
import com.tuwa.smarthome.entity.Result;
import com.tuwa.smarthome.entity.Weather;
import com.tuwa.smarthome.entity.Weather_data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class HttpUtils {
	
	public static String getURl(String location) {
		//B95329fb7fdda1e32ba3e3a245193146
		String url = "http://api.map.baidu.com/telematics/v3/weather?location="
				+ location + "&output=json&ak=8dDFTDA0m3WHUkCce9aRZbxI";
		//ak 需要在百度上申请     http://lbsyun.baidu.com/apiconsole/key  (服务器)
		return url;
	}

//  http://api.map.baidu.com/telematics/v3/weather?location="瑞昌"&output=json&ak=8dDFTDA0m3WHUkCce9aRZbxI  "
	
	public static String getJsonStr(String url) {
		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream in = response.getEntity().getContent();

				return getResult(in);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap getImage(String imageUrl) {
		HttpGet get = new HttpGet(imageUrl);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream in = response.getEntity().getContent();
				Bitmap bm = BitmapFactory.decodeStream(in);
				return bm;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getJsonStr2(String url) {
		URL url2 = null;
		try {
			url2 = new URL(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) url2.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");

			if (conn.getResponseCode() == 200) {
				InputStream in = conn.getInputStream();
				return getResult(in);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getResult(InputStream in) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		try {
			while ((len = in.read(b)) != -1) {
				bos.write(b, 0, len);
				bos.flush();
			}
			// System.out.println(new String(bos.toByteArray(), "utf-8"));
			return new String(bos.toByteArray(), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Weather fromJson2(String jsonStr) {
		// Weather obj = JSON.parseObject(jsonStr, Weather.class);
		// return obj;
		return null;
	}

	public static Weather fromJson(String jsonStr) {
		try {
			if (jsonStr!=null) {
				JSONObject obj = new JSONObject(jsonStr);
				String error = obj.getString("error");
				String status = obj.getString("status");
				String date = obj.getString("date");
			
			// 根据具体的error值返回不同的信息，这儿做了统一处理，返回null;
			if (Integer.parseInt(error) != 0) {
				return null;
			} else {
				Weather wea = new Weather();
				wea.setError(error);
				wea.setStatus(status);
				wea.setDate(date);
				// results信息
				List<Result> results = new ArrayList<Result>();
				JSONArray rArr = obj.getJSONArray("results");
				for (int i = 0; i < rArr.length(); i++) {
					JSONObject rObj = rArr.getJSONObject(i);
					Result res = new Result();
					res.setCurrentCity(rObj.getString("currentCity"));
					res.setPm25(rObj.getString("pm25"));
					// 根据具体的error值返回不同的信息，这儿做了统一处理，返回null;
					List<Index> index = new ArrayList<Index>();
					JSONArray iArr = rObj.getJSONArray("index");
					for (int j = 0; j < iArr.length(); j++) {
						JSONObject iObj = iArr.getJSONObject(i);
						Index ind = new Index();
						ind.setTitle(iObj.getString("title"));
						ind.setZs(iObj.getString("zs"));
						ind.setTipt(iObj.getString("tipt"));
						ind.setDes(iObj.getString("des"));
						index.add(ind);
					}
					res.setIndex(index);
					// results信息
					List<Weather_data> weather_data = new ArrayList<Weather_data>();
					JSONArray wArr = rObj.getJSONArray("weather_data");
					for (int j = 0; j < wArr.length(); j++) {
						JSONObject wObj = wArr.getJSONObject(j);
						Weather_data wd = new Weather_data();
						wd.setDate(wObj.getString("date"));
						wd.setDayPictureUrl(wObj.getString("dayPictureUrl"));
						wd.setNightPictureUrl(wObj.getString("nightPictureUrl"));
						wd.setWeather(wObj.getString("weather"));
						wd.setWind(wObj.getString("wind"));
						wd.setTemperature(wObj.getString("temperature"));
						weather_data.add(wd);
					}
					res.setWeather_data(weather_data);
					res.setIndex(index);
					results.add(res);
				}
				wea.setResults(results);
				return wea;
			}
		  }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Map<String, Object>> toListMap(Result r) {
		//从result中获取到天气信息，并将image转化为bitmap存入list
		return null;
	}
}
