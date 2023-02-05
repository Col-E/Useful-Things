package org.example.weather.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.IntFunction;

/**
 * Util for HTTP requests.
 */
public class HTTP {
	/**
	 * @param path
	 * 		URL path.
	 * @param badStatusMessageProvider
	 * 		Message formatter for non-200 status codes.
	 *
	 * @return String of content at url.
	 *
	 * @throws IOException
	 * 		When the connection could not be read.
	 */
	public static String readString(String path, IntFunction<String> badStatusMessageProvider) throws IOException {
		URL url = new URL(path);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		int status = con.getResponseCode();
		if (status != 200) {
			throw new IOException(badStatusMessageProvider.apply(status));
		}
		return new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
	}
}
