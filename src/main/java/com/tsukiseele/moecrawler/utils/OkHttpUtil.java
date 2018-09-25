package com.tsukiseele.moecrawler.utils;


import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {
	private static OkHttpUtil INSTANCE;
	private static OkHttpClient client;
	private Request.Builder request;
	private Call call;
	
	public static OkHttpClient getOkHttpClient() {
		if (client == null)
			return client = new OkHttpClient.Builder()
				.cookieJar(new CookieJar() {
					private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
					
					@Override
					public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
						cookieStore.put(httpUrl.host(), cookies);
					}
					@Override
					public List<Cookie> loadForRequest(HttpUrl httpUrl) {
						List<Cookie> cookies = cookieStore.get(httpUrl.host());
						return cookies == null ? new ArrayList<>() : cookieStore.get(httpUrl.host());
					}
				})
				.retryOnConnectionFailure(true)
				.readTimeout(20, TimeUnit.SECONDS)
				.connectTimeout(15, TimeUnit.SECONDS)
				.build();
		return client;
	}
	
	private OkHttpUtil() {
		getOkHttpClient();
	}
	
	public static OkHttpUtil build() {
		if (INSTANCE == null)
			INSTANCE = new OkHttpUtil();
		return INSTANCE;
	}
	public OkHttpUtil url(String url) {
		request = new Request.Builder().url(url);
		return this;
	}
	public OkHttpUtil header(String key, String value) {
		request.header(key, value);
		return this;
	}
	public OkHttpUtil headers(Headers headers) {
		request.headers(headers);
		return this;
	}
	public Response execute() throws IOException {
		call = client.newCall(request.build());
		
		Response response = call.execute();
		if (response.isSuccessful())
			return response;
		else
			throw new IOException("Unexpected code " + response);
	}
	public void cancel() {
		call.cancel();
	}
}
