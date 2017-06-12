package com.homecoolink.utils;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;

import com.homecoolink.R;
import com.homecoolink.data.SharedPreferencesManager;
import com.homecoolink.data.SysMessage;
import com.homecoolink.global.Constants;
import com.homecoolink.global.MyApp;
import com.homecoolink.widget.NormalDialog;
import com.p2p.core.update.UpdateManager;

public class Utils {
	public static String[] getMsgInfo(SysMessage msg, Context context) {

		if (msg.msgType == SysMessage.MESSAGE_TYPE_ADMIN) {

			String title = context.getResources().getString(
					R.string.system_administrator);
			String content = msg.msg;
			return new String[] { title, content };
		} else {
			return null;
		}

	}

	public static boolean hasDigit(String content) {
		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches())
			flag = true;
		return flag;
	}

	// yyyy-MM-dd HH:mm:ss ->>>>>>> yyyy-MM-dd HH:mm
	public static String ConvertTimeByString(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(time);
			sdf.applyPattern("yyyy-MM-dd HH:mm");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sdf.format(date);
	}

	public static String ConvertTimeByLong(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date(time);
		return sdf.format(date);
	}

	public static String getDefenceAreaByGroup(Context context, int group) {
		switch (group) {
		case 0:
			return context.getResources().getString(R.string.remote);
		case 1:
			return context.getResources().getString(R.string.hall);
		case 2:
			return context.getResources().getString(R.string.window);
		case 3:
			return context.getResources().getString(R.string.balcony);
		case 4:
			return context.getResources().getString(R.string.bedroom);
		case 5:
			return context.getResources().getString(R.string.kitchen);
		case 6:
			return context.getResources().getString(R.string.courtyard);
		case 7:
			return context.getResources().getString(R.string.door_lock);
		case 8:
			return context.getResources().getString(R.string.other);
		default:
			return "";
		}
	}


	public static String getResString(Context context,int stringId) {
		return context.getResources().getString(stringId);
	}

	public static Bitmap montageBitmap(Bitmap frame, Bitmap src, int x, int y) {
		int w = src.getWidth();
		int h = src.getHeight();
		Bitmap sizeFrame = Bitmap.createScaledBitmap(frame, w, h, true);

		Bitmap newBM = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBM);
		canvas.drawBitmap(src, 0, 0, null);
		canvas.drawBitmap(sizeFrame, 0, 0, null);
		return newBM;
	}

	public static boolean isZh(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("zh"))
			return true;
		else
			return false;
	}

	public static void upDate(final Context context) {
		new Thread() {
			@Override
			public void run() {

				boolean isUpdate;
				boolean isOk = false;
				try {
					Timestamp time = new Timestamp(System.currentTimeMillis());
					String recent_checkTime = SharedPreferencesManager
							.getInstance()
							.getData(
									context,
									SharedPreferencesManager.SP_FILE_GWELL,
									SharedPreferencesManager.KEY_UPDATE_CHECKTIME);
					if (recent_checkTime.equals("")) {
						isOk = true;
					} else {
						Timestamp recentTime = Timestamp
								.valueOf(recent_checkTime);
						long now = time.getTime();
						long last = recentTime.getTime();
						if ((now - last) > (1000 * 60 * 60 * 24)) {
							isOk = true;
						}
					}
					if (!isOk) {
						return;
					}
					isUpdate = UpdateManager.getInstance().checkUpdate();
					if (isUpdate && isOk) {
						SharedPreferencesManager.getInstance().putData(context,
								SharedPreferencesManager.SP_FILE_GWELL,
								SharedPreferencesManager.KEY_UPDATE_CHECKTIME,
								time.toString());
						Intent i = new Intent(Constants.Action.ACTION_UPDATE);
						String data = "";
						if (Utils.isZh(MyApp.app)) {
							data = UpdateManager.getInstance()
									.getUpdateDescription();
						} else {
							data = UpdateManager.getInstance()
									.getUpdateDescription_en();
						}
						i.putExtra("updateDescription", data);
						context.sendBroadcast(i);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void showPromptDialog(Context context, int title, int content) {
		NormalDialog dialog = new NormalDialog(context, context.getResources()
				.getString(title), context.getResources().getString(content),
				"", "");
		dialog.setStyle(NormalDialog.DIALOG_STYLE_PROMPT);
		dialog.showDialog();
	}

	public static String intToIp(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xff) + "."
				+ ((ip >> 16) & 0xff) + "." + ((ip >> 24) & 0xff);
	}

	public static HashMap getHash(String string) {
		try {
			HashMap map = new HashMap<String, String>();
			String[] item = string.split(",");
			for (int i = 0, len = item.length; i < len; i++) {
				String[] info = item[i].split(":");
				map.put("" + info[0], info[1]);
			}
			return map;
		} catch (Exception e) {
			return null;
		}

	}

	public static String getFormatTellDate(Context context, String time) {

		String year = context.getString(R.string.year_format);
		String month = context.getString(R.string.month_format);
		String day = context.getString(R.string.day_format);

		SimpleDateFormat sd = new SimpleDateFormat("yyyy" + year + "MM" + month
				+ "dd" + day + " HH:mm");
		Date dt = null;
		try {
			dt = new Date(Long.parseLong(time));
		} catch (Exception e) {
		}

		String s = "";
		if (dt != null) {
			s = sd.format(dt);
		}

		return s;
	}

	public static void sleepThread(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String[] getDeleteAlarmIdArray(String[] data, int position) {
		if (data.length == 1) {
			return new String[] { "0" };
		}

		String[] array = new String[data.length - 1];
		int count = 0;
		for (int i = 0; i < data.length; i++) {
			if (position != i) {
				array[count] = data[i];
				count++;
			}
		}

		return array;
	}

	public static String convertDeviceTime(int iYear, int iMonth, int iDay,
			int iHour, int iMinute) {
		int year = (2000 + iYear);
		int month = iMonth;
		int day = iDay;
		int hour = iHour;
		int minute = iMinute;

		StringBuilder sb = new StringBuilder();
		sb.append(year + "-");

		if (month < 10) {
			sb.append("0" + month + "-");
		} else {
			sb.append(month + "-");
		}

		if (day < 10) {
			sb.append("0" + day + " ");
		} else {
			sb.append(day + " ");
		}

		if (hour < 10) {
			sb.append("0" + hour + ":");
		} else {
			sb.append(hour + ":");
		}

		if (minute < 10) {
			sb.append("0" + minute);
		} else {
			sb.append("" + minute);
		}
		return sb.toString();
	}

	public static String convertPlanTime(int hour_from, int minute_from,
			int hour_to, int minute_to) {
		Log.e("plantime", "hour_from=" + hour_from + "minute_from="
				+ minute_from + "hour_to=" + hour_to + "minute_to=" + minute_to);
		StringBuilder sb = new StringBuilder();

		if (hour_from < 10) {
			sb.append("0" + hour_from + ":");
		} else {
			sb.append(hour_from + ":");
		}

		if (minute_from < 10) {
			sb.append("0" + minute_from + "-");
		} else {
			sb.append(minute_from + "-");
		}

		if (hour_to < 10) {
			sb.append("0" + hour_to + ":");
		} else {
			sb.append(hour_to + ":");
		}

		if (minute_to < 10) {
			sb.append("0" + minute_to);
		} else {
			sb.append("" + minute_to);
		}

		return sb.toString();
	}

	public static boolean isNumeric(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				deleteFile(f);
			}
			file.delete();

		}
	}

	public static int dip2px(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
