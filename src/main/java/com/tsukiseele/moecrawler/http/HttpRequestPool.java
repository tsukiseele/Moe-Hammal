package com.tsukiseele.moecrawler.http;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpRequestPool {
	private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, Integer.MAX_VALUE, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

	public static void execute(Runnable runnable) {
		threadPool.execute(runnable);
	}
}
