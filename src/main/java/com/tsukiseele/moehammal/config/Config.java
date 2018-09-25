package com.tsukiseele.moehammal.config;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Config {
	private Config() {}

	public static final File PATH_ROOT = new File(System.getProperty("user.dir"));
	public static final File PATH_DOWNLOAD = new File(PATH_ROOT, "download/");
	public static final File PATH_SOURCE = new File(PATH_ROOT, "source/");
	public static final File PATH_SOURCE_IMAGES = new File(PATH_SOURCE, "images/");
	public static final File PATH_SOURCE_RULES = new File(PATH_SOURCE, "rules/");

	public static final String APPLICTION_NAME = "Moe-Hammal";
	public static final String APPLICTION_VERSION = "1.0.1";
	public static final String APPLICTION_STATE = "build";
	public static final String APPLICTION_TITLE = APPLICTION_NAME + " " + APPLICTION_VERSION + " " + APPLICTION_STATE;

	static {
		Field[] fields = Config.class.getDeclaredFields();
		Method method;
		try {
			method = File.class.getDeclaredMethod("mkdirs");
			for (Field field : fields) {
				if (field.getType().isAssignableFrom(File.class)) {
					method.invoke(field.get(null));
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
