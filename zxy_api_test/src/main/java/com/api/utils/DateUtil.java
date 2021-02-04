package com.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

public class DateUtil {
	private static Logger log = Logger.getLogger(DateUtil.class);

	// 获取当前时间，为LONG类型的字符串，可以根据当前时间自定义时间"time++3d"
	public static String getTime(String timeStr) {
		Date d = new Date();
		long tempTime = 0;
		if (timeStr.contains("++")) {
			int addTime = Integer.parseInt(timeStr.substring(timeStr.indexOf("++") + 2, timeStr.length() - 1));
			if (timeStr.toLowerCase().endsWith("d")) {
				tempTime = d.getTime() + addTime * 24 * 60 * 60 * 1000;
			} else if (timeStr.toLowerCase().endsWith("h")) {
				tempTime = d.getTime() + addTime * 60 * 60 * 1000;
			} else if (timeStr.toLowerCase().endsWith("m")) {
				tempTime = d.getTime() + addTime * 60 * 1000;
			} else if (timeStr.toLowerCase().endsWith("s")) {
				tempTime = d.getTime() + addTime * 1000;
			} else {
				log.error(timeStr + "调整时间只支持天、时、分、秒（d、h、m、s）结尾！");
			}
		} else if (timeStr.contains("--")) {
			int addTime = Integer.parseInt(timeStr.substring(timeStr.indexOf("--") + 2, timeStr.length() - 1));
			if (timeStr.toLowerCase().endsWith("d")) {
				tempTime = d.getTime() - addTime * 24 * 60 * 60 * 1000;
			} else if (timeStr.toLowerCase().endsWith("h")) {
				tempTime = d.getTime() - addTime * 60 * 60 * 1000;
			} else if (timeStr.toLowerCase().endsWith("m")) {
				tempTime = d.getTime() - addTime * 60 * 1000;
			} else if (timeStr.toLowerCase().endsWith("s")) {
				tempTime = d.getTime() - addTime * 1000;
			} else {
				log.error(timeStr + "调整时间只支持天、时、分、秒（d、h、m、s）结尾！");
			}
		} else if (timeStr.toLowerCase().equals("time")) {
			tempTime = tempTime + d.getTime();
		} else {
			log.error(timeStr + "时间格式化错误！");
		}
		String time = tempTime + "";
		return time;
	}

	// 将当前时间，转换格式化日期"yyyyMMddHH:mm:ss"
	public static String formatCurrentTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
		String date = sdf.format(d);
		return date;
	}

	// 设置自定义格式日期："formatTime++3h(yyyy-MM-dd HH:mm:ss)"
	public static String formatTime(String timeStr) {
		String formatStr = timeStr.substring(timeStr.indexOf("(") + 1, timeStr.indexOf(")"));
		Date d = new Date();
		String date = "";
		if (timeStr.startsWith("formatTime") && !timeStr.contains("++") && !timeStr.contains("--")) {
			SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
			date = sdf.format(d.getTime());
		} else if (timeStr.startsWith("formatTime") && timeStr.contains("++")) {
			String time = timeStr.substring(timeStr.indexOf("++") + 2, timeStr.indexOf("("));
			long tempTime = Long.parseLong(time.substring(0, time.length() - 1));
			if (time.toLowerCase().endsWith("d")) {
				tempTime = d.getTime() + tempTime * 24 * 60 * 60 * 1000;
			} else if (time.toLowerCase().endsWith("h")) {
				tempTime = d.getTime() + tempTime * 60 * 60 * 1000;
			} else if (time.toLowerCase().endsWith("m")) {
				tempTime = d.getTime() + tempTime * 60 * 1000;
			} else if (time.toLowerCase().endsWith("s")) {
				tempTime = d.getTime() + tempTime * 1000;
			} else {
				log.error(timeStr + "调整时间只支持天、时、分、秒（d、h、m、s）结尾！");
			}
			date = formatLongTime(tempTime + "", formatStr);
		} else if (timeStr.startsWith("formatTime") && timeStr.contains("--")) {
			String time = timeStr.substring(timeStr.indexOf("--") + 2, timeStr.indexOf("("));
			long tempTime = Long.parseLong(time.substring(0, time.length() - 1));
			if (time.toLowerCase().endsWith("d")) {
				tempTime = d.getTime() - tempTime * 24 * 60 * 60 * 1000;
			} else if (time.toLowerCase().endsWith("h")) {
				tempTime = d.getTime() - tempTime * 60 * 60 * 1000;
			} else if (time.toLowerCase().endsWith("m")) {
				tempTime = d.getTime() - tempTime * 60 * 1000;
			} else if (time.toLowerCase().endsWith("s")) {
				tempTime = d.getTime() - tempTime * 1000;
			} else {
				log.error(timeStr + "调整时间只支持天、时、分、秒（d、h、m、s）结尾！");
			}
			date = formatLongTime(tempTime + "", formatStr);
		} else {
			log.error(timeStr + "时间格式化错误！");
		}
		return date;
	}

	// 将long类型的其他时间，转换格式化日期"yyyyMMdd HH:mm:ss"
	private static String formatLongTime(String longTime, String formatStr) {
		long a = Long.parseLong(longTime);
		Date dd = new Date();
		dd.setTime(a);
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		String date = sdf.format(dd);
		return date;
	}

	// 将"yyyyMMdd HH:mm:ss"日期转换为Long类型
	public static long parseFormatDate(String formateDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Date d = null;
		try {
			d = sdf.parse(formateDate);
		} catch (ParseException e) {
			log.error("[" + formateDate + "]转换出现异常！");
		}
		return d.getTime();
	}

	public static void main(String[] args) {
//		System.out.println(formatLongTime(getTime("TIME"), "yyyyMMdd HH:mm:ss"));
//		System.out.println(formatLongTime("1573747200000", "yyyyMMdd HH:mm:ss"));
//		System.out.println("计划日期：" + formatTime("formatTime++242d(yyyy-MM-dd)"));
//		System.out.println(formatTime("formatTime(yyyy-MM-dd)"));
//		System.out.println(getTime("TIME++3s"));
//		System.out.println(formatTime("formatTime++5m(yyyy-MM-dd+HH:mm)"));
//		System.out.println();
//		System.out.println(formatTime("formatTime--5d(yyyy-MM-dd)"));
//		System.out.println();
//		System.out.println(getTime("time"));
//		System.out.println(getTime("time++5d"));
	}
}
