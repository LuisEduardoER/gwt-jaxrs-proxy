package com.paullindorff.gwt.jaxrs.client.jso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.json.client.JSONObject;

//TODO: introspect for properties, if only a single one exists, move up the chain
//http://stackoverflow.com/questions/208016/how-to-list-the-properties-of-a-javascript-object
//http://www.nczonline.net/blog/2010/07/27/determining-if-an-object-property-exists/?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed:+nczonline+(NCZOnline+-+The+Official+Web+Site+of+Nicholas+C.+Zakas

/**
 * Base instance for JavaScript Overlay Types, including construction from JSON and convenience methods
 * {@link http://code.google.com/webtoolkit/doc/latest/DevGuideCodingBasicsOverlay.html}
 * 
 * NOTE: JavaScript objects and JSNI do not support autoboxing/unboxing, so there are explicit get/set pairs for both primitives and wrapper objects
 * @author plindorff
 *
 */
public abstract class BaseJSO extends JavaScriptObject
{
// construction
	/**
	 * Constructors must be protected in subclasses of JavaScriptObject, as specified in the GWT docs
	 */
	protected BaseJSO() {}

	/**
	 * Returns a new JSO as described by the JSON string
	 * NOTE: the parenthesis are needed so that the parser does not evaluate the first item as a JS label
	 * @param json
	 * @param prefixLists
	 * @return
	 */
	public static final JavaScriptObject createObject(String json)
	{
		//check for special cases that cause issues: null, empty string, empty square brackets
		if (json == null || json.length() == 0 || json.equals("[]"))
			return createObject();

		try
		{
			// attempt evaluation of the supplied JSON string
			return eval(json);
		} catch (Exception e)
		{
			// if evaluation fails, return a blank JSO (delegate to GWT's supplied JavaScriptObject.createObject)
			// NOTE: this is required for correct behavior, especially in the case of a JSON string representing an empty collection
			return createObject();
		}
	}

	/**
	 * Returns a new JSO as described by the json String,
	 * trimming off any of the json prefixes specified by prefixLists
	 * @param json
	 * @param prefixLists
	 * @return
	 */
	public static final JavaScriptObject createObject(String json, String[]...prefixLists)
	{
		return createObject(trimPrefixes(json, prefixLists));
	}


	/**
	 * Creates an instance of JavaScriptObject, the overlay type, by using JSNI to evaluate the provided JSON string
	 *  
	 * @param jsonString
	 * @return
	 */
	private static native JavaScriptObject eval(String jsonString) /*-{
		// NOTE: the open/close parenthesis are required, otherwise, JS tries to evaluate the first attribute as a JS label
		return eval('(' + jsonString + ')');
	}-*/;

	/**
	 * Trims off the JSON container specified by the included prefixLists,
	 *  each one containing variations on a single container specification
	 * @param jsonString
	 * @param prefixLists
	 * @return
	 */
	private static String trimPrefixes(String jsonString, String[]... prefixLists)
	{
		String result = jsonString;
		for (String[] prefixes : prefixLists) {
			for (String prefix : prefixes) {
				if (jsonString.startsWith(prefix)) {
					// remove beginning prefix and associated trailing brace
					result = jsonString.substring(prefix.length(), jsonString.length()-1);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Trims off the specified wrapper container around a JAXB 'listified' Map, if it exists
	 * @param json
	 * @param wrapper
	 * @return
	 */
	public static final native String removeMapWrapper(String json, String wrapper) /*-{
		if (json.match('\\{"' + wrapper + '":\\[.*}}]}')) {			// multi-element list
			// the 'new RegExp(...,"g") forces JavaScript to replace all instead of just the first one
			return json.replace(new RegExp('\\{"' + wrapper + '":\\[',"g"), '[').replace(new RegExp('}}]}',"g"), '}}]');
		} else if (json.match('{\\"' + wrapper + '\\":{.*}}}')) { 	// single element list
			return json.replace(new RegExp('{"' + wrapper + '":',"g"), '').replace(new RegExp('}}}',"g"), '}}');
		} else {
			return json;
		}
	}-*/;

	/**
	 * Returns this object as a JSON string
	 */
	public final String toJSON(String prefix) {
		JSONObject thisObj = new JSONObject(this);
		if (prefix != null)
			return "{" + prefix + ":" + thisObj.toString() + "}";
		else
			return thisObj.toString();
	}

// attribute checks
	/**
	 * Checks to see if the underlying JavaScript object contains a field/attribute with the specified name,
	 * @param name
	 * @return
	 */
	public final native boolean containsAttribute(String name) /*-{
		return !(typeof this[name] == "undefined")
	}-*/;

	/**
	 * 
	 */
	public final native boolean isNumber(String name) /*-{
		// alternatively:   return !isNaN(parseFloat(n)) && isFinite(n);  this would work with strings containing numbers as well
		// http://stackoverflow.com/questions/18082/validate-numbers-in-javascript-isnumeric
		//https://developer.mozilla.org/en/ECMAScript_DontEnum_attribute
		return (typeof this[name] == "number" && isFinite(this[name]));
	}-*/;

	/**
	 * Checks to see if the attribute specified by the given name is an array
	 * @param name
	 * @return
	 */
	public final native boolean isArray(String name) /*-{
		return (this[name].constructor == Array);
	}-*/;

	/**
	 * Checks to see if the attribute specified by the given name is an object
	 * @param name
	 * @return
	 */
	public final native boolean isObject(String name) /*-{
		return ((null !== this[name]) && (typeof(this[name]) == "object") && (this[name].constructor != Array));
	}-*/;

	/**
	 * Returns the JavaScript classification of the attribute specified by the given name.  Useful for debugging.
	 * @param name
	 * @return
	 */
	public final native String classifyAttribute(String name) /*-{
		return Object.prototype.toString.call(this[name]);
	}-*/;

	/**
	 * Returns the set of defined keys (attribute names) for this JavaScriptObject.
	 * Keys returned match JavaScript's hasOwnProperty() semantics.
	 * @return
	 */
	public final Set<String> getKeySet() {
		JSONObject thisObj = new JSONObject(this);

		/*
		 * NOTE: GWT's JSONObject.keySet() returns a Set implementation in which iterator() does not match contains()!
		 *  JSONObject.keySet().iterator() iterates through the set of keys defined on *this* object (internally populated via JavaScript's hasOwnProperty()),
		 *  while contains() internally calls JavaScript's "var key in myObject", which returns ALL properties - including those defined on the Object prototype.
		 */

		// we use our own set to just return keys defined on this object (new HashSet(Collection) uses Iterator, which is the correct set of keys)
		Set<String> myKeys = new HashSet<String>(thisObj.keySet());
		return myKeys;
	}

// native getters/setters

	protected final native JsArray<?> getArray_(String name) /*-{
		return this[name];
	}-*/;

	protected final native JsArray<?> setArray_(String name, JsArray<?> value) /*-{
		this[name] = value;
	}-*/;

	protected final native JsArrayNumber getNumberArray_(String name) /*-{
		return this[name];
	}-*/;

	protected final native JsArrayNumber setNumberArray_(String name, JsArrayNumber value) /*-{
		this[name] = value;
	}-*/;

	protected final native JavaScriptObject getObject_(String name) /*-{
		return this[name];
	}-*/;

	protected final native void setObject_(String name, JavaScriptObject value) /*-{
		this[name] = value;
	}-*/;

	protected final native String getString_(String name) /*-{
		return String(this[name]);
	}-*/;
	
	protected final native void setString_(String name, String value) /*-{
		this[name] = value;
	}-*/;

	protected final native boolean getBoolean_(String name) /*-{
		return this[name];
	}-*/;
	
	protected final native void setBoolean_(String name, boolean value) /*-{
		this[name] = value;
	}-*/;

	protected final native double getDouble_(String name) /*-{
		return this[name];
	}-*/;

	protected final native void setDouble_(String name, double value) /*-{
		this[name] = value;
	}-*/;

	protected final native float getFloat_(String name) /*-{
		return this[name];
	}-*/;
	
	protected final native void setFloat_(String name, float value) /*-{
		this[name] = value;
	}-*/;

	protected final native int getInteger_(String name) /*-{
		return this[name];
	}-*/;
	
	protected final native void setInteger_(String name, int value) /*-{
		this[name] = value;
	}-*/;

// public getters/setters; both primitive and wrapper
	/**
	 * Returns the specified attribute of the underlying object as a String, or null if the attribute doesn't exist
	 * 
	 * @param name
	 * @return value held by underlying JavaScript object as a String
	 */
	public final String getString(String name) {
		if (this.containsAttribute(name))
			return getString_(name);
		else
			return null;
	}

	/**
	 * Attempts to retrieve the requested attribute.  If it is not found, it attempts to "unrwap" the JSON object
	 *  by retrieving the list of attributes; if only one exists, attribute retrieval is attempted from within the sub-object
	 * @param name
	 * @return
	 */
	public final String getStringRecursive(String name) {
		if (containsAttribute(name))
			return getString_(name);
		else
		{
			Set<String> keys = getKeySet();
			if (keys.size() == 1)
			{
				String wrapperName = keys.iterator().next();
				if (isObject(wrapperName))
				{
					BaseJSO wrapper = (BaseJSO)getObject(wrapperName);
					if (wrapper.containsAttribute(name))
						return wrapper.getString_(name);
					else
						return null;
				} else
					return null;
			} else
			{
				return null;
			}
		}
	}

	/**
	 * Stores a String into the specified attribute of the underlying object
	 * @param name
	 * @param value
	 */
	public final void setString(String name, String value) {
		setString_(name, value);
	}

	/**
	 * Returns a native JavaScript array of objects for the specified element, or null if the attribute doesn't exist (or isn't an array)
	 * @param name
	 * @return JSArray<T>
	 */
	@SuppressWarnings("rawtypes")
	public final JsArray getArray(String name)
	{
		if (containsAttribute(name) && isArray(name))
			return getArray_(name);
		else
			return null;
	}

	// TODO: setArray?

	/**
	 * Returns a native JavaScript array of numbers for the specified element, or null if the attribute doesn't exist (or isn't an array)
	 * @param name
	 * @return JSArray<T>
	 */
	public final JsArrayNumber getNumberArray(String name) {
		if (containsAttribute(name) && isArray(name))
			return getNumberArray_(name);
		else
			return null;
	}

	// TODO: setNumberArray?

	/**
	 * Returns a sub-JSO specified by the element name, or null if the attribute doesn't exist (or isn't an Object)
	 * @param name
	 * @return
	 */
	public final JavaScriptObject getObject(String name) {
		if (containsAttribute(name) && isObject(name))
			return getObject_(name);
		else
			return null;
	}

	public final void setObject(String name, JavaScriptObject value) {
		setObject_(name, value);
	}

	/**
	 * Returns a Java Collection specified by the element name, or null if the element doesn't exist
	 * @param name
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public final Collection getCollection(String name)
	{
		// return a pure collection of interfaces
		Collection collection = new ArrayList();

		JsArray array = getArray(name);
		if (array == null)	// some JSON formats will omit array brackets for an array of one item
		{
			JavaScriptObject singlet = getObject(name);
			if (singlet != null)	// single item was returned
				collection.add(getObject(name));
		} else
		{
			// convert JSArray to collection
			for (int i = 0; i < array.length(); i++)
				collection.add(array.get(i));
		}

		return collection;
	}

	// boolean
	public final boolean getBooleanPrimitive(String name) {
		if (containsAttribute(name))
			return getBoolean_(name);
		else
			throw new UndefinedJavaScriptAttributeException("boolean primitive '" + name + "' not found in JavaScriptObject");
	}

	public final void setBooleanPrimitive(String name, boolean value) {
		setBoolean_(name, value);
	}

	public final Boolean getBoolean(String name) {
		return containsAttribute(name) ? new Boolean(getBoolean_(name)) : null;
	}

	public final void setBoolean(String name, Boolean value) {
		setBoolean_(name, value.booleanValue());
	}

	// double
	public final double getDoublePrimitive(String name) {
		if (containsAttribute(name))
			return getDouble_(name);
		else
			throw new UndefinedJavaScriptAttributeException("double primitive '" + name + "' not found in JavaScriptObject");
	}

	public final void setDoublePrimitive(String name, double value) {
		setDouble_(name, value);
	}

	public final Double getDouble(String name) {
		return containsAttribute(name) ? new Double(getDouble_(name)) : null;
	}

	public final void setDouble(String name, Double value) {
		setDouble_(name, value.doubleValue());
	}

	// float
	public final float getFloatPrimitive(String name) {
		if (containsAttribute(name))
			return getFloat_(name);
		else
			throw new UndefinedJavaScriptAttributeException("float primitive '" + name + "' not found in JavaScriptObject");
	}

	public final void setFloatPrimitive(String name, float value) {
		setFloat_(name, value);
	}

	public final Float getFloat(String name) {
		return containsAttribute(name) ? new Float(getFloat_(name)) : null;
	}

	public final void setFloat(String name, Float value) {
		setFloat_(name, value.floatValue());
	}

	// int/Integer
	public final int getIntegerPrimitive(String name) {
		if (containsAttribute(name))
			return getInteger_(name);
		else
			throw new UndefinedJavaScriptAttributeException("int primitive '" + name + "' not found in JavaScriptObject");
	}

	public final void setIntegerPrimitive(String name, int value) {
		setInteger_(name, value);
	}

	public final Integer getInteger(String name) {
		return containsAttribute(name) ? new Integer(getInteger_(name)) : null;
	}

	public final void setInteger(String name, Integer value) {
		setInteger_(name, value.intValue());
	}

	// long/Long
	// long values are stored in the overlayed JSON object as a String (JS doesn't handle long/Long well natively)
	public final long getLongPrimitive(String name) {
		if (containsAttribute(name))
			return Long.parseLong(getString(name));
		else
			throw new UndefinedJavaScriptAttributeException("long primitive '" + name + "' not found in JavaScriptObject");
	}

	public final void setLongPrimitive(String name, long value) {
		setString_(name, String.valueOf(value));
	}

	public final Long getLong(String name) {
		return containsAttribute(name) ? new Long(getString_(name)) : null;
	}

	public final void setLong(String name, Long value) {
		setString_(name, value.toString());
	}
}
