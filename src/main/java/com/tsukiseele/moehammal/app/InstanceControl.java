package com.tsukiseele.moehammal.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.tsukiseele.moehammal.MainApplication;
import com.tsukiseele.moehammal.config.Config;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import sun.applet.Main;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.TimerTask;

//应用控制类
public class InstanceControl {
	// 必须是静态变量，且不可调用close()，否则将会使文件锁失效
	private static FileChannel channel;

	//判断该应用是否已启动
	public static boolean isRunning() {
		try {
			//获得实例标志文件
			File lockFile = new File(Config.PATH_ROOT, "control.bin");
			lockFile.createNewFile();
			lockFile.deleteOnExit();
			//获得文件锁
			channel = new RandomAccessFile(lockFile, "rw").getChannel();
			if (channel.tryLock() == null)
				return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	// 检查应用是否是单例
	public static boolean checkControl() {
		if (InstanceControl.isRunning()) {
//			JFXDialog dialog = new JFXDialog();
//			JFXDialogLayout dialogLayout = new JFXDialogLayout();
//			dialogLayout.setHeading(new Label(Config.APPLICTION_TITLE));
//			dialogLayout.setBody(new Label("在您的计算机上已经启动过了本程序！" + "\n重复启动会过度消耗您的计算机资源。"));
//			dialogLayout.setActions(new JFXButton("确定"));
//			dialog.setContent(dialogLayout);
//			dialog.show(new StackPane());
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle(Config.APPLICTION_TITLE);
			alert.setContentText("在您的计算机上已经启动过了本程序！" + "\n重复启动会过度消耗您的计算机资源。");
			new java.util.Timer().schedule(new TimerTask() {
				private int i = 5;
				@Override
				public void run() {
					if (--i > 0)
						Platform.runLater(() -> alert.setHeaderText("该窗口将在 " + i + " 秒后自动关闭"));
					else
						System.exit(0);
				}
			}, 0, 1000);
			alert.showAndWait();
			System.exit(0);
		} else {
			return true;
		}

		return false;
	}
}