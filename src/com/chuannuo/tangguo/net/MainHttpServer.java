package com.chuannuo.tangguo.net;

public class MainHttpServer {
	private static TGHttpClient client;
	public MainHttpServer() {
		MainHttpServer.client = new TGHttpClient();
		client.setTimeout(10000);
	}
	public static void getData(String urlString, RequestParams params,
			TGHttpResponseHandler res) {
		if (params==null) {
			client.get(urlString, res);
		}
		else {
			client.get(urlString, params, res);
		}
	}
	public static void getData(String urlString, RequestParams params,
			JsonHttpResponseHandler res) {
		if (params==null) {
			client.get(urlString, res);
		}
		else {
			client.get(urlString, params, res);
		}
	}
	public static void getData(String uString, BinaryHttpResponseHandler bHandler) {
		client.get(uString, bHandler);
	}

	public static void postData(String urlString, TGHttpResponseHandler res) {
		client.post(urlString, res);
	}

	public static void postData(String url, RequestParams params,
			TGHttpResponseHandler handler) {
		client.post(url, params, handler);
	}

	public static TGHttpClient getClient() {
		return client;
	}

}
