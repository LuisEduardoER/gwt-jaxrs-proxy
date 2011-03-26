package com.paullindorff.gwt.jaxrs.client.proxy;

import com.paullindorff.gwt.jaxrs.client.util.Base64;

public class BasicAuthentication extends HTTPAuthentication 
{
	private final String PREFIX = "Basic";
	private String token;

	public BasicAuthentication(String username, String password)
	{
		token = Base64.encode(username + ":" + password);
	}

	@Override
	protected String getPrefix() {
		return PREFIX;
	}

	@Override
	public String getToken() {
		return token;
	}
}
