package com.cj.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class DataAdapterUtil
{
	public abstract class JSONDataHandler
	{
		public abstract JSONObject doJsonObject(JSONObject data);
		public abstract Object doObject(Object data);
	}
	public static List<Map<String, Object>> FromJsonArray(JSONArray jsonArray,
	JSONDataHandler handler)
	{
		ArrayList<Map<String, Object>> resArrayList = new ArrayList<Map<String, Object>>();
		for (int iter = 0; iter < jsonArray.length(); iter++)
		{
			try
			{
				JSONObject data = jsonArray.getJSONObject(iter);
				if (handler != null)
				{
					data = handler.doJsonObject(data);
				}
				Map<String, Object> map = new HashMap<String, Object>();
				JSONArray names = data.names();
				for (int iter1 = 0; iter1 < names.length(); iter1++)
				{
					map.put(names.getString(iter1),
					data.getString(names.getString(iter1)));
				}
				resArrayList.add(map);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		return resArrayList;
	}

	public static List<Map<String, Object>> FromJsonArray(JSONArray jsonArray)
	{
		return FromJsonArray(jsonArray, null);
	}

	public static List<Map<String, Object>> FromArray(Object[] data,
	Object[] values, String Key, String ValueKey, JSONDataHandler handler)
	{
		if (Key == null || Key.equals(""))
		{
			Key = "Text";
		}
		if (ValueKey == null || ValueKey.equals(""))
		{
			ValueKey = "Value";
		}
		ArrayList<Map<String, Object>> resArrayList = new ArrayList<Map<String, Object>>();
		for (int iter = 0; iter < data.length; iter++)
		{
			try
			{
				if (handler != null)
				{
					data[iter] = handler.doObject(data[iter]);
				}
				Map<String, Object> map = new HashMap<String, Object>();

				map.put(Key, data[iter]);
				map.put(ValueKey, values[iter]);

				resArrayList.add(map);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return resArrayList;
	}

	public static List<Map<String, Object>> FromArray(Object[] data, String Key)
	{
		return FromArray(data, data, Key, "", null);
	}

	public static List<Map<String, Object>> FromArray(Object[] data,
	Object[] values, String Key, String ValueKey)
	{
		return FromArray(data, values, Key, ValueKey, null);
	}

	public static List<Map<String, Object>> FromArray(Object[] data,
			JSONDataHandler handler)
	{
		return FromArray(data, data, null, null, handler);
	}

	public static List<Map<String, Object>> FromArray(Object[] data,
	Object[] values, JSONDataHandler handler)
	{
		return FromArray(data, values, null, null, handler);
	}

	public static List<Map<String, Object>> FromArray(Object[] data)
	{
		return FromArray(data, data, null, null, null);
	}

	public static List<Map<String, Object>> FromArray(Object[] data,
	Object[] values)
	{
		return FromArray(data, values, null, null, null);
	}

	public static class MySimpDataAdpter
										extends
											SimpleAdapter
	{

		public List<Map<String, Object>> mItemList = new ArrayList<Map<String, Object>>();
		int count = 0;
		private OnGetViewListener getViewListener;

		public MySimpDataAdpter(Context context,
		List<Map<String, Object>> data, int resource, String[] from, int[] to)
		{
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			mItemList = data;
			if (data == null)
			{
				count = 0;
			}
			else
			{
				count = data.size();
			}
		}

		public void SetGetViewListener(OnGetViewListener l)
		{
			getViewListener = l;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public Object getItem(int position)
		{
			return mItemList.get(position);
		}

		@Override
		public int getCount()
		{
			return mItemList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			convertView = super.getView(position, convertView, parent);

			if (getViewListener != null)
			{
				convertView = getViewListener.OnGetView(convertView, position,
				mItemList.get(position));
			}
			return convertView;
		}

		@Override
		public View getDropDownView(int position, View convertView,
		ViewGroup parent)
		{
			convertView = super.getDropDownView(position, convertView, parent);

			if (getViewListener != null)
			{
				convertView = getViewListener.OnGetView(convertView, position,
				mItemList.get(position));
			}
			return convertView;
		}
	}

	public static interface OnGetViewListener
	{
		View OnGetView(View convertView, final int position,
		Map<String, Object> data);

	}

}
