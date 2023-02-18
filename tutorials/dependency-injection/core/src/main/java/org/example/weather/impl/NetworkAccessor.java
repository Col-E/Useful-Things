package org.example.weather.impl;

import org.example.weather.interfaces.INetworkAccessor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Implementation to lookup network information.
 */
public class NetworkAccessor implements INetworkAccessor {
	private InetAddress externalAddress;

	@Override
	public InetAddress externalAddress() throws IOException {
		if (externalAddress == null)
			externalAddress = lookupExternalAddress();
		return externalAddress;
	}

	private InetAddress lookupExternalAddress() throws IOException {
		String ip = new String(new URL("http://checkip.amazonaws.com")
				.openConnection()
				.getInputStream()
				.readAllBytes(),
				StandardCharsets.UTF_8)
				.trim();
		return InetAddress.getByName(ip);
	}
}
