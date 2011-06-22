package com.paullindorff.gwt.jaxrs.client.proxy;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RequestCallbackAdapter<T> implements RequestCallback
{
	private enum ResponseClass {
		META(100),
		SUCCESS(200),
		REDIRECTION(300),
		CLIENT_ERROR(400),
		SERVER_ERROR(500);

		private int rangeStart;

		private ResponseClass(int rangeStart) {
			this.rangeStart = rangeStart;
		}

		public int getRangeStart() {
			return rangeStart;
		}

		public static ResponseClass fromStatusCode(int statusCode) {
			if (isStatusCodeInClass(META, statusCode))
				return META;
			if (isStatusCodeInClass(SUCCESS, statusCode))
				return SUCCESS;
			if (isStatusCodeInClass(REDIRECTION, statusCode))
				return SUCCESS;
			if (isStatusCodeInClass(CLIENT_ERROR, statusCode))
				return CLIENT_ERROR;
			if (isStatusCodeInClass(SERVER_ERROR, statusCode))
				return SERVER_ERROR;
			else return null;
		}

		private static boolean isStatusCodeInClass(ResponseClass responseClass, int statusCode) {
			return (statusCode >= responseClass.getRangeStart() && statusCode <= (responseClass.getRangeStart() + 99));
		}
	}

	private final ResponseReader responseReader;
	private final AsyncCallback<T> callback;

	public RequestCallbackAdapter(ResponseReader responseReader, AsyncCallback <T> callback)
	{
		this.responseReader = responseReader;
		this.callback = callback;
	}

	@SuppressWarnings("unchecked")
	public void onResponseReceived(Request request, Response response)
	{
		// TODO: get the response status and ensure that the request did not produce an error from the server
		int statusCode = response.getStatusCode();
		ResponseClass responseClass = ResponseClass.fromStatusCode(statusCode);

		switch (responseClass)
		{
			case SUCCESS:
			{
				switch (statusCode)
				{
					case Response.SC_OK:		// get response entity body and parse into an object
												T result = (T)responseReader.read(response.getText());
												callback.onSuccess(result);
												break;
					case Response.SC_CREATED:
					case Response.SC_ACCEPTED:	// return location header value (T should be String)
												String locationHeader = response.getHeader("Location");
												callback.onSuccess((T)locationHeader);
												break;
					case Response.SC_NO_CONTENT:
					default:					// empty response body, return null (T should be Void)
												callback.onSuccess(null);
												break;
				}
			}
			break;
			case REDIRECTION:
			{
				// we don't handle these, and the I-Grid server shouldn't be sending them...
				callback.onFailure(new UnknownResponseException());
			}
			break;
			case CLIENT_ERROR:
			{
				// parse the error message object and throw the appropriate exception...
				switch (statusCode) {
					case Response.SC_UNAUTHORIZED:
					case Response.SC_FORBIDDEN:			// throw a security exception
														callback.onFailure(new SecurityException());
														break;
					case Response.SC_BAD_REQUEST:		// problem with parameters, parse error message and throw an appropriate exception
														//TODO: parse error message
														callback.onFailure(new BadRequestException());
														break;
					case Response.SC_NOT_FOUND:			// requested a non-existent entity, parse error message and throw an appropriate exception
														//TODO: parse error message
														callback.onFailure(new NotFoundException());
														break;
					case Response.SC_CONFLICT:			// the entity already exists (most likely a PUT); let user know via an appropriate exception
														//TODO: parse error message
														callback.onFailure(new ConflictException());
														break;
					default:							// throw a generic client error exception
														callback.onFailure(new WebServiceException());
														break;
				}
			}
			break;
			case SERVER_ERROR:
			{
				// throw a generic server error exception
				callback.onFailure(new WebServiceException());
			}
			break;
			default:
			{
				//  throw a generic exception, unknown response code
				callback.onFailure(new UnknownResponseException());
			}
		}
	}

	public void onError(Request request, Throwable exception)
	{
		callback.onFailure(exception);
	}
}
