package com.paullindorff.gwt.jaxrs.client.proxy;

import com.google.gwt.user.client.rpc.SerializationException;

public interface RequestBodyWriter {
	public String write(Object object) throws SerializationException;
}
