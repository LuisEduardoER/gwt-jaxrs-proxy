package com.paullindorff.gwt.jaxrs.client.jso;

/**
 * This exception is thrown when a GWT JavaScript overlay type attempts to get a value for a non-existant attribute of the underlying JavaScript object.
 * @author plindorff
 *
 */
@SuppressWarnings("serial")
public class UndefinedJavaScriptAttributeException extends RuntimeException {

	public UndefinedJavaScriptAttributeException() {}

	public UndefinedJavaScriptAttributeException(String message) {
		super(message);
	}
}
