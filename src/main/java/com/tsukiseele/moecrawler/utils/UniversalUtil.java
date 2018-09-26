package com.tsukiseele.moecrawler.utils;

import java.io.*;
import java.lang.reflect.Field;

public class UniversalUtil {
	// 序列化拷贝对象
	public static <T> T cloneObject(T object) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		T newObject = null;
		try {
			// 序列化
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			// 反序列化
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ois = new ObjectInputStream(bais);
			newObject = (T) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newObject;
	}
	
	// 利用反射生成一个包含对象所有字段信息的字符串
	public static String toString(Object obj) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		try {
			for (Class<?> type = obj.getClass(); type != Object.class; type = type.getSuperclass()) {
				Field[] fields = type.getDeclaredFields();
				for(Field field : fields) {
					field.setAccessible(true);
					sb.append(String.format("%s = %s, ", field.getName(), field.get(obj)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.substring(0, sb.length() - 2) + ']';
	}
} 

