package com.cj.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cj.utils.DataAdapterUtil.MySimpDataAdpter;
import com.homecoolink.R;
import com.homecoolink.utils.T;

public class SpinnerUtil {
	private Spinner spinner;
	private String TextCol = "Text";
	private String ValueCol = "Value";
	private Activity contextthis;
	private SpinnerListener onItemSelectedListener = null;
	private DataAdapterUtil.OnGetViewListener onGetViewListener = null;
	private List<Map<String, Object>> Extras = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> extras = new ArrayList<Map<String, Object>>();
	private Boolean isiniting = true;
	public Boolean HasBinded = false;

	public Spinner GetSpinner() {
		return spinner;
	}

	public SpinnerUtil(Activity context, Spinner s) {
		contextthis = context;
		spinner = s;
	}

	public SpinnerUtil(Activity context, int s) {
		contextthis = context;
		try {
			spinner = (Spinner) context.findViewById(s);
		} catch (Exception e) {
			T.showShort(contextthis, "got wrong para of spinner");

		}
	}

	public SpinnerUtil SetOnSelectedListener(SpinnerListener listener) {
		onItemSelectedListener = listener;
		return this;
	}

	public SpinnerUtil SetOnGetViewListener(
			DataAdapterUtil.OnGetViewListener listener) {
		onGetViewListener = listener;
		return this;
	}

	public SpinnerUtil AddExtraValue(String Text, String Value) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(TextCol, Text);
		item.put(ValueCol, Value);
		extras.add(item);
		return this;
	}

	public SpinnerUtil ClearExtraValue() {

		extras.clear();
		return this;
	}

	public SpinnerUtil SetBind(String TextFiled, String ValueFiled) {
		TextCol = TextFiled;
		ValueCol = ValueFiled;
		return this;
	}

	public Object getSelected(String Col) {
		if (spinner != null) {
			if (Extras.get(spinner.getSelectedItemPosition()).containsKey(Col)) {
				return Extras.get(spinner.getSelectedItemPosition()).get(Col);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public void setSelected(Object Value) {
		if (spinner != null && HasBinded) {
			int i = 0;
			for (Map<String, Object> item : Extras) {
				if (item.containsKey(ValueCol)
						&& item.get(ValueCol).equals(Value)) {
					spinner.setSelection(i);
					return;
				}
				i++;
			}
		}
	}

	public Object getSelectedValue() {
		return getSelected(ValueCol);
	}

	public Object getSelectedText() {
		return getSelected(TextCol);
	}

	public SpinnerUtil Bindspinner(String[] items) {

		return Bindspinner(DataAdapterUtil.FromArray(items));
	}

	public SpinnerUtil Bindspinner(String[] items, String[] values) {

		return Bindspinner(DataAdapterUtil.FromArray(items, values));
	}

	public SpinnerUtil Bindspinner(JSONArray data) {

		return Bindspinner(DataAdapterUtil.FromJsonArray(data));
	}

	public SpinnerUtil Bindspinner(List<Map<String, Object>> data) {
		if (spinner != null) {
			Extras.clear();
			for (Map<String, Object> item : extras) {
				Extras.add(item);
			}
			for (Map<String, Object> item : data) {
				Extras.add(item);
			}
			// new ButtonUtil(spinner).SetBackGround(R.drawable.bg_select1,
			// R.drawable.bg_select);
			MySimpDataAdpter adapter = new MySimpDataAdpter(contextthis,
					Extras, R.layout.item_share_spinner,
					new String[] { TextCol }, new int[] { R.id.textSpinner });
			if (onGetViewListener != null) {
				adapter.SetGetViewListener(onGetViewListener);
			}
			adapter.setDropDownViewResource(R.layout.item_share_spinner);
			spinner.setAdapter(adapter);
			HasBinded = true;
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if (view != null) {
						TextView textView = (TextView) view
								.findViewById(R.id.textSpinner);
						textView.setPadding(textView.getPaddingLeft(),
								textView.getPaddingTop() - 8,
								textView.getPaddingRight(),
								textView.getPaddingBottom() - 8);
						textView.setTextSize(16);
						Map<String, Object> item = Extras.get(position);
						if (onItemSelectedListener != null) {

							onItemSelectedListener.onSelected(
									item.containsKey(TextCol) ? item
											.get(TextCol) : null,
									item.containsKey(ValueCol) ? item
											.get(ValueCol) : null, item,
									isiniting);
						}
						if (isiniting) {
							isiniting = false;
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

		}
		return this;
	}

	public static abstract interface SpinnerListener {
		public void onSelected(Object Text, Object Val,
				Map<String, Object> item, Boolean isIniting);

	}
}
