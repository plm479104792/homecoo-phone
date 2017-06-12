package com.homecoolink.utils;

import java.util.Comparator;

public class LanguageComparator_EN implements Comparator<String> {

	@Override
	public int compare(String ostr1, String ostr2) {
		// TODO Auto-generated method stub
		return ostr1.compareToIgnoreCase(ostr2);
	}

}
