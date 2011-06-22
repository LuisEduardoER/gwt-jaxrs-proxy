package com.paullindorff.gwt.jaxrs.client.proxy;

import java.util.logging.Logger;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class RESTResourceProxy
{
	private static final Logger logger = Logger.getLogger(RESTResourceProxy.class.getName());

	private static final String HEADER_ACCEPT = "Accept";
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	private static final String MEDIA_TYPE = "application/json";

	protected <T> void performRESTRequest(String method, String path, ResponseReader responseReader, HTTPAuthentication authentication, WebServiceTarget target, AsyncCallback<T> callback)
	{
		performRESTRequest(method, path, null, responseReader, authentication, target, callback);
	}

	protected <T> void performRESTRequest(String method, String path, String body, ResponseReader responseReader, HTTPAuthentication authentication, WebServiceTarget target, AsyncCallback<T> callback)
	{
		// create a callback adapter: RequestCallback -> AsyncCallback<T>
		RequestCallback handler = new RequestCallbackAdapter<T>(responseReader, callback);

		try
		{
			// construct the full URL from appRegistry and supplied resource path
			String url = target.getBaseURL() + path;

			// parse the HTTP method
			Method httpMethod = getHTTPMethodForName(method);
	
			// assemble the request
			RequestBuilder builder = new RequestBuilder(httpMethod, url);
			builder.setCallback(handler);
	
			// request JSON from the server
			builder.setHeader("Accept", MEDIA_TYPE);

			// add authentication header
			builder.setHeader(authentication.getHeaderName(), authentication.getValue());
			// add request body, if specified
			if (body != null)
			{
				logger.fine("added request entity body: " + body);
				builder.setHeader("Content-Type", MEDIA_TYPE);
				builder.setRequestData(body);
			}

			// send the request
			logger.info("sending request: " + method + " " + path + " to " + target.getBaseURL() + "; " + HEADER_ACCEPT + ": " + builder.getHeader(HEADER_ACCEPT) + (body == null ? "" : "; " +HEADER_CONTENT_TYPE + ": " + builder.getHeader(HEADER_CONTENT_TYPE)));
			builder.send();
		} catch (RequestException re)
		{
			handler.onError(null, re);
		}
	}

	protected static String getStringValue(Object o, boolean encode)
	{
		if (o == null)
			return null;

		String value = o.toString();
		if (encode)
			return URL.encodeQueryString(value);
		else
			return value;
	}

	private static RequestBuilder.Method getHTTPMethodForName(String name)
	{
		if (RequestBuilder.GET.toString().equalsIgnoreCase(name))
			return RequestBuilder.GET;
		else
		if (RequestBuilder.POST.toString().equalsIgnoreCase(name))
			return RequestBuilder.POST;
		else
		if (RequestBuilder.PUT.toString().equalsIgnoreCase(name))
			return RequestBuilder.PUT;
		else
		if (RequestBuilder.DELETE.toString().equalsIgnoreCase(name))
			return RequestBuilder.DELETE;
		else
		if (RequestBuilder.HEAD.toString().equalsIgnoreCase(name))
			return RequestBuilder.HEAD;
		else
			throw new RuntimeException("invalid HTTP method specified: " + name);
	}
}
