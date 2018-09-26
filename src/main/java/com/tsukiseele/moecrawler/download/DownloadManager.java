package com.tsukiseele.moecrawler.download;

import com.tsukiseele.moecrawler.download.DownloadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static com.tsukiseele.moecrawler.download.DownloadInfo.STATE_NONE;
import static com.tsukiseele.moecrawler.download.DownloadInfo.STATE_START;
import static com.tsukiseele.moecrawler.download.DownloadInfo.STATE_LOADING;
import static com.tsukiseele.moecrawler.download.DownloadInfo.STATE_CANCEL;
import static com.tsukiseele.moecrawler.download.DownloadInfo.STATE_WAIT;
import static com.tsukiseele.moecrawler.download.DownloadInfo.STATE_SUCCESS;
import static com.tsukiseele.moecrawler.download.DownloadInfo.STATE_ERROR;

public class DownloadManager {
	private static final Map<Integer, DownloadTask> downloadTasks = Collections.synchronizedMap(new HashMap<Integer, DownloadTask>());


	private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

	public static void setPoolSize(int poolSize) {
		threadPool.setCorePoolSize(poolSize);
	}
	// 取消下载
	public static synchronized void cancel(DownloadTask task) {
		if (downloadTasks.containsKey(task.hashCode())) {
			downloadTasks.remove(task.hashCode());
			threadPool.remove(task);
			task.cancel();
		}
	}
	// 暂停下载
	public static void pause(String url) {
		if (downloadTasks.containsKey(url.hashCode())) {

			downloadTasks.get(url.hashCode()).cancel();
		}
	}
	// 恢复下载
	public static void resume(String url) {
		if (downloadTasks.containsKey(url.hashCode())) {
			DownloadTask task = downloadTasks.get(url.hashCode());
			if (task.isState(STATE_CANCEL) || task.isState(STATE_ERROR))
				threadPool.execute(task);
		}
	}
	public static void restart(String url) {
		if (downloadTasks.containsKey(url.hashCode())) {
			DownloadTask task = downloadTasks.get(url.hashCode());
			cancel(task);
			execute(task);
		}
	}
	// 执行下载
	public static synchronized void execute(DownloadTask task) {
		if (downloadTasks.containsKey(task.hashCode())) {
			return;
		} else {
			downloadTasks.put(task.hashCode(), task);
			threadPool.execute(task);
			if (threadPool.getActiveCount() >= threadPool.getCorePoolSize()) {
				task.notifyWait();
			}
		}
	}

	public static ThreadPoolExecutor getThreadPool() {
		return threadPool;
	}

	public static List<DownloadTask> getDownloadList() {
		return new ArrayList<DownloadTask>(downloadTasks.values());
	}
}
