package org.example.weather.interfaces;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Tool outline for network activity.
 */
public interface INetworkAccessor {
	/**
	 * @return External IP address of current user.
	 *
	 * @throws IOException
	 * 		When the external address could not be retrieved.
	 */
	InetAddress externalAddress() throws IOException;
}
