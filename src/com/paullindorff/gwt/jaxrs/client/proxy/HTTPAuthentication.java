package com.paullindorff.gwt.jaxrs.client.proxy;

public abstract class HTTPAuthentication
{
	private final String headerName = "Authorization";

	public String getHeaderName() {
		return headerName;
	}

	public String getValue() {
		return getPrefix() + " " + getToken();
	}

	protected abstract String getPrefix();
	protected abstract String getToken();
}
