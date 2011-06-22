package com.paullindorff.gwt.jaxrs.client.proxy;

import com.google.gwt.core.client.GWT;

public class HostModeProxyTarget implements WebServiceTarget
{
	private final String HOSTMODEPROXYSERVLET_HOSTNAME = "localhost";
	private final String HOSTMODEPROXYSERVLET_PORT = "8081";

	private String baseURL;

	public HostModeProxyTarget(String path) {
		this.baseURL = GWT.getHostPageBaseURL() + "servlet/proxy?http://" + HOSTMODEPROXYSERVLET_HOSTNAME + ":" + HOSTMODEPROXYSERVLET_PORT + path;
	}

	public String getBaseURL() {
		return this.baseURL;
	}

}
