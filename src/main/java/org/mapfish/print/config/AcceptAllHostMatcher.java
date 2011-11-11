package org.mapfish.print.config;

import org.mapfish.print.config.HostMatcher;

/**
 * Used to validate the access to all map service hosts 
 */
public class AcceptAllHostMatcher extends HostMatcher{

	public String toString() {
		return "ALL ALLOW";
	}
}

