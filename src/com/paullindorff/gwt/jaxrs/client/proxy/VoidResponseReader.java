package com.paullindorff.gwt.jaxrs.client.proxy;

public class VoidResponseReader implements ResponseReader {
	public Object read(String response) {
		return null;
	}
}
