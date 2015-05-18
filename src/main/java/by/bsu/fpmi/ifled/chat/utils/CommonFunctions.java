package by.bsu.fpmi.ifled.chat.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CommonFunctions {

	public static String fixSqlFieldValue(String value) {
		if (value == null) {
			return null;
		}
		int length = value.length();
		StringBuffer fixedValue = new StringBuffer(length * 2 + 2);
		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			if (c == '\'') {
				fixedValue.append("''");
			}
			else {
				fixedValue.append(c);
			}
		}
		return fixedValue.toString();
	}
	
	public static String nowTime() {
		Date now = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
		return ft.format(now);
	}
	public static String nowDate() {
		Date now = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
		return ft.format(now);
	}
}
