package com.tsukiseele.moecrawler.utils;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

	private static Level level = Level.VERBOSE;
	private static PrintStream logWriter;
	private static String logFilePath;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private LogUtil() {}

	public static void setStream(PrintStream writer) {
		logWriter = writer;
	}

	public static void setLogFilePath(String logOutputPath) {
		logFilePath = logOutputPath;
	}

	static {
		logWriter = System.out;
	}
	public static Level getLevel() {
		return level;
	}
	public static void setLevel(Level le) {
		level = le;
	}
	public static void v(String tag, String message) {
		if (level.getLevel() < 1) {
			logWriter.println("[" + dateFormat.format(System.currentTimeMillis()) + "]" + ": " +  level.getName() + "  " + tag + "  " + message);
		}
	}
	public static void d(String tag, String message) {
		if (level.getLevel() < 2) {
			logWriter.println("[" + dateFormat.format(System.currentTimeMillis()) + "]" + ": " +  level.getName() + "  " + tag + "  " + message);
		}
	}
	public static void i(String tag, String message) {
		if (level.getLevel() < 3) {
			logWriter.println("[" + dateFormat.format(System.currentTimeMillis()) + "]" + ": " +  level.getName() + "  " + tag + "  " + message);
		}
	}
	public static void w(String tag, String message) {
		if (level.getLevel() < 4) {
			logWriter.println("[" + dateFormat.format(System.currentTimeMillis()) + "]" + ": " +  level.getName() + "  " + tag + "  " + message);

		}
	}
	public static void e(String tag, String message) {
		if (level.getLevel() < 5) {
			logWriter.println("[" + dateFormat.format(System.currentTimeMillis()) + "]" + ": " +  level.getName() + "  " + tag + "  " + message);
		}
	}
	public static void e(Exception e) {
		if (level.getLevel() < 5) {
			logWriter.println("[" + dateFormat.format(System.currentTimeMillis()) + "]" + ": " +  level.getName() + "  " + e.toString());
		}
	}

	public static boolean printDebugLog(String tag, String message) {
		if (logFilePath == null)
			return false;
		PrintStream debugLogWrite = null;
		try {
			debugLogWrite = new PrintStream(new FileOutputStream(logFilePath, true), true, "UTF-8");
			debugLogWrite.printf("%s\nTag : %s\nMsg : %s\n", dateFormat.format(new Date()), tag, message);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				debugLogWrite.close();
			} catch (Exception e) {
			}
		}
		return true;
	}
	public enum Level {
		VERBOSE(0),
		DEBUG(1),
		INFO(2),
		WARN(3),
		ERROR(4),
		CLOSE(5);

		private int level;
		private Level(int level) {
			this.level = level;
		}
		public int getLevel() {
			return level;
		}
		public String getName() {
			return name();//.charAt(0);
		}
	}
}

