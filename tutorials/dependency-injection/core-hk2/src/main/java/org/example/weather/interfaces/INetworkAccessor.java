package org.example.weather.interfaces;

import org.jvnet.hk2.annotations.Contract;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Tool outline for network activity.
 */
@Contract
public interface INetworkAccessor {
	/**
	 * @return External IP address of current user.
	 *
	 * @throws IOException
	 * 		When the external address could not be retrieved.
	 */
	InetAddress externalAddress() throws IOException;
}
