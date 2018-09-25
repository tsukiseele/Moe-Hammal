package com.tsukiseele.moehammal.app;

import javafx.scene.control.Alert;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CrashManager implements Thread.UncaughtExceptionHandler {
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		System.out.println("Crash: " + sw);
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(" Error! ");
		alert.setContentText(" 程序出现致命异常，崩溃信息：\n" + sw.toString());
		alert.showAndWait();
	}
}
