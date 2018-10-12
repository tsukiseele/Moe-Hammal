package com.tsukiseele.moehammal.app;

import javafx.scene.control.Alert;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CrashManager implements Thread.UncaughtExceptionHandler {
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(" Error! ");
		alert.setHeaderText(" 程序出现致命错误！抛出如下异常 ");
		alert.setContentText(sw.toString());
		alert.showAndWait();
	}
}
