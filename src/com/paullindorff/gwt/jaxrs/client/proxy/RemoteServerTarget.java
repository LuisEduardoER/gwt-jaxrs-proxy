package com.paullindorff.gwt.jaxrs.client.proxy;

public class RemoteServerTarget implements WebServiceTarget
{
	private String baseURL;

	public RemoteServerTarget(String host, String path, boolean useSSL) {
		String url = host + path;
		if (useSSL)
			this.baseURL = "https://" + url;
		else
			this.baseURL = "http://" + url;
	}

	public RemoteServerTarget(String host, String port, String path, boolean useSSL) {
		String url = host + ":" + port + path;
		if (useSSL)
			this.baseURL = "https://" + url;
		else
			this.baseURL = "http://" + url;
	}

	public String getBaseURL() {
		return this.baseURL;
	}
}
