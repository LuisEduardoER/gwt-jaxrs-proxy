package com.paullindorff.gwt.jaxrs.client.test.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.paullindorff.gwt.jaxrs.client.jso.BaseJSO;
import com.paullindorff.gwt.jaxrs.client.jso.UndefinedJavaScriptAttributeException;

@SuppressWarnings("unchecked")
public class BaseJSOTestDefs {
	
	private static final double FLOAT_FUDGE = 0.00001;
	public static final String TEST_MODULE_NAME = "com.paullindorff.gwt.jaxrs.client.test.RunTests";

	//TODO: standard minimal set of JSON objects

	public TestResult testRemoveMapWrapper() {
		String oneEntrySingle = "{\"data\":{\"entry\":{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3,4,5,6,7]},\"attr1\":520.97,\"attr2\":1174937678981}}}}";
		String multipleEntrySingle = "{\"data1\":{\"entry\":{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3,4,5,6,7]},\"attr1\":520.97,\"attr2\":1174937678981}}},\"data2\":{\"entry\":{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3,4,5,6,7]},\"attr1\":520.97,\"attr2\":1174937678981}}}}";
		String oneEntryArray = "{\"data\":{\"entry\":[{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"B\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"C\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"attr1\":520.97,\"attr2\":1180039965032}}]}}";
		String multipleEntryArray = "{\"data1\":{\"entry\":[{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"B\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"C\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"attr1\":520.97,\"attr2\":1180039965032}}]},\"data2\":{\"entry\":[{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"B\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"C\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"attr1\":520.97,\"attr2\":1180039965032}}]}}";
		String noEntries = "{\"obj\":{\"inner1\":{\"inner2\":{\"inner3\":{\"inner4\":{\"inner5\":\"value\"}}}}}}";

		if (!BaseJSO.removeMapWrapper(oneEntrySingle, "entry").equals("{\"data\":{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3,4,5,6,7]},\"attr1\":520.97,\"attr2\":1174937678981}}}"))
			return failTest("one entry single test failed");
		if (!BaseJSO.removeMapWrapper(multipleEntrySingle, "entry").equals("{\"data1\":{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3,4,5,6,7]},\"attr1\":520.97,\"attr2\":1174937678981}},\"data2\":{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3,4,5,6,7]},\"attr1\":520.97,\"attr2\":1174937678981}}}"))
			return failTest("multiple entry single tests failed");
		if (!BaseJSO.removeMapWrapper(oneEntryArray, "entry").equals("{\"data\":[{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"B\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"C\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"attr1\":520.97,\"attr2\":1180039965032}}]}"))
			return failTest("one entry array test failed");
		if (!BaseJSO.removeMapWrapper(multipleEntryArray, "entry").equals("{\"data1\":[{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"B\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"C\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"attr1\":520.97,\"attr2\":1180039965032}}],\"data2\":[{\"key\":\"A\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"B\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"interval_micros\":520.97,\"start_time_millis\":1180039965032}},{\"key\":\"C\",\"value\":{\"data_points\":{\"data_point\":[1,2,3]},\"attr1\":520.97,\"attr2\":1180039965032}}]}"))
			return failTest("multiple entry array test failed");
			if (!BaseJSO.removeMapWrapper(noEntries, "entry").equals(noEntries))
			return failTest("json with no map wrappers was altered incorrectly");

		return passTest();
	}

	public TestResult testIsObject() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"notobj1\":5,\"notobj2\":6.0,\"notobj3\":true,\"notobj4\":\"mystring\",\"notobj5\":[1,2,3],\"isobj1\":{\"objval1\":1},\"isobj2\":{},\"nullvalue\":null,\"nanvalue\":NaN,\"infinityvalue\":Infinity}");

		if (iut.isObject("notobj1"))
			return failTest("integer should return false");
		if (iut.isObject("notobj2"))
			return failTest("number should return false");
		if (iut.isObject("notobj3"))
			return failTest("boolean should return false");
		if (iut.isObject("notobj4"))
			return failTest("string should return false");
		if (iut.isObject("notobj5"))
			return failTest("array should return false");
		if (iut.isObject("nullvalve"))
			return failTest("null should return false");
		if (iut.isObject("nanvalue"))
			return failTest("NaN should return false");
		if (iut.isObject("infinityvalue"))
			return failTest("Infinity should return false");
		if (!iut.isObject("isobj1"))
			return failTest("object with attribute should return true");
		if (!iut.isObject("isobj2"))
			return failTest("empty object should return true");

		return passTest();
	}

	public TestResult testIsArray() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"notarr1\":5,\"notarr2\":6.0,\"notarr3\":true,\"notarr4\":\"mystring\",\"notarr5\":{\"objval1\":1},\"notarr6\":{}," +
				"\"isarr1\":[],\"isarr2\":[1,2,3],\"isarr3\":[false, true],\"isarr4\":[\"one\",\"two\"],\"isarr5\":[{},{}],\"isarr6\":[{\"val1\":1},{\"val1\":2}],\"isarr7\":[1,2.0,true,\"mystring\",{},{\"val1\":3},[],[1,2,3]]}");

		if (iut.isArray("notarr1"))
			return failTest("integer should return false");
		if (iut.isArray("notarr2"))
			return failTest("number should return false");
		if (iut.isArray("notarr3"))
			return failTest("boolean should return false");
		if (iut.isArray("notarr4"))
			return failTest("string should return false");
		if (iut.isArray("notarr5"))
			return failTest("object with attribute should return false");
		if (iut.isArray("notarr6"))
			return failTest("empty object should return false");
		if (!iut.isArray("isarr1"))
			return failTest("empty square brackets should return true");
		if (!iut.isArray("isarr2"))
			return failTest("integer array should return true");
		if (!iut.isArray("isarr3"))
			return failTest("boolean array should return true");
		if (!iut.isArray("isarr4"))
			return failTest("string array should return true");
		if (!iut.isArray("isarr5"))
			return failTest("array of empty object should return true");
		if (!iut.isArray("isarr6"))
			return failTest("array of non-empty objects should return true");
		if (!iut.isArray("isarr7"))
			return failTest("mixed array should return true");

		return passTest();
	}

	public TestResult testIsNumber() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"zeroint\":0,\"negint\":-6,\"posint\":4,\"zerofloat\":0.0,\"negfloat\":-5.2,\"posfloat\":7.1,\"octal\":0400,\"hex\":0xFF," +
				"\"emptystring\":\"\",\"alphastring\":\"abcde\",\"numericstring\":\"123\",\"alphanumericstring\":\"123abc\"," +
				"\"booleantrue\":true,\"booleanfalse\":false,\"nullvalue\":null,\"nanvalue\":NaN,\"infinityvalue\":Infinity," +
				"\"emptyobj\":{},\"obj\":{\"myvalue\":3},\"emptyarr\":[],\"arr\":[1,2,3]}");
		
		if (!iut.isNumber("zeroint"))
			return failTest("integer zero should be a number");
		if (!iut.isNumber("negint"))
			return failTest("negative integer literal should be a number");
		if (!iut.isNumber("posint"))
			return failTest("positive integer literal should be a number");
		if (!iut.isNumber("zerofloat"))
			return failTest("float zero should be a number");
		if (!iut.isNumber("negfloat"))
			return failTest("negative float literal should be a number");
		if (!iut.isNumber("posfloat"))
			return failTest("positive float literal should be a number");
		if (!iut.isNumber("octal"))
			return failTest("octal literal should be a number");
		if (!iut.isNumber("hex"))
			return failTest("hexadecimal literal should be a number");
		if (iut.isNumber("emptystring"))
			return failTest("empty string is not a number");
		if (iut.isNumber("alphastring"))
			return failTest("alpha string is not a number");
		if (iut.isNumber("numericstring"))
			return failTest("numeric string is not a number");
		if (iut.isNumber("alphanumericstring"))
			return failTest("alphanumeric string is not a number");
		if (iut.isNumber("booleantrue"))
			return failTest("boolean true is not a number");
		if (iut.isNumber("booleanfalse"))
			return failTest("boolean false is not a number");
		if (iut.isNumber("nullvalue"))
			return failTest("null is not a number");
		if (iut.isNumber("nanvalue"))
			return failTest("NaN is not a number");
		if (iut.isNumber("infinityvalue"))
			return failTest("Infinity is not a number");
		if (iut.isNumber("emptyobj"))
			return failTest("empty object is not a number");
		if (iut.isNumber("obj"))
			return failTest("object is not a number");
		if (iut.isNumber("emptyarr"))
			return failTest("empty array is not a number");
		if (iut.isNumber("arr"))
			return failTest("array is not a number");

		return passTest();
	}

	
	public TestResult testContainsAttribute() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"zeroint\":0,\"zerofloat\":0.0,\"booleanfalse\":false,\"nullvalue\":null,\"nanvalue\":NaN,\"emptystring\":\"\"," +
				"\"integer\":5,\"number\":5.5,\"string\":\"i am a string\",\"emptyobj\":{},\"obj\":{\"myvalue\":3},\"emptyarr\":[],\"arr\":[1,2,3]}");

		if (!iut.containsAttribute("zeroint"))
			return failTest("integer zero should exist");
		if (!iut.containsAttribute("zerofloat"))
			return failTest("float zero should exist");
		if (!iut.containsAttribute("booleanfalse"))
			return failTest("boolean false should exist");
		if (!iut.containsAttribute("nullvalue"))
			return failTest("null should exist");
		if (!iut.containsAttribute("nanvalue"))
			return failTest("NaN should exist");
		if (!iut.containsAttribute("emptystring"))
			return failTest("empty string should exist");
		if (!iut.containsAttribute("integer"))
			return failTest("integer should exist");
		if (!iut.containsAttribute("number"))
			return failTest("number should exist");
		if (!iut.containsAttribute("string"))
			return failTest("string should exist");
		if (!iut.containsAttribute("emptyobj"))
			return failTest("empty object should exist");
		if (!iut.containsAttribute("obj"))
			return failTest("object should exist");
		if (!iut.containsAttribute("emptyarr"))
			return failTest("empty array should exist");
		if (!iut.containsAttribute("arr"))
			return failTest("array should exist");
		if (iut.containsAttribute("idontexist"))
			return failTest("undefined attribute should not exist");

		return passTest();
	}

	
	public TestResult testClassifyAttribute() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"zeroint\":0,\"negint\":-6,\"posint\":4,\"zerofloat\":0.0,\"negfloat\":-5.2,\"posfloat\":7.1,\"octal\":0400,\"hex\":0xFF," +
				"\"emptystring\":\"\",\"alphastring\":\"abcde\",\"numericstring\":\"123\",\"alphanumericstring\":\"123abc\"," +
				"\"booleantrue\":true,\"booleanfalse\":false,\"nullvalue\":null,\"nanvalue\":NaN,\"infinityvalue\":Infinity," +
				"\"emptyobj\":{},\"obj\":{\"myvalue\":3},\"emptyarr\":[],\"arr\":[1,2,3]}");

		if (!"[object Number]".equals(iut.classifyAttribute("zeroint")))
			return failTest("integer zero should return [object Number] but instead returned " + iut.classifyAttribute("zeroint"));
		if (!"[object Number]".equals(iut.classifyAttribute("negint")))
			return failTest("negative integer should return [object Number] but instead returned " + iut.classifyAttribute("negint"));
		if (!"[object Number]".equals(iut.classifyAttribute("posint")))
			return failTest("positive integer should return [object Number] but instead returned " + iut.classifyAttribute("postint"));
		if (!"[object Number]".equals(iut.classifyAttribute("zerofloat")))
			return failTest("float zero should return [object Number] but instead returned " + iut.classifyAttribute("zerofloat"));
		if (!"[object Number]".equals(iut.classifyAttribute("negfloat")))
			return failTest("negative float should return [object Number] but instead returned " + iut.classifyAttribute("negfloat"));
		if (!"[object Number]".equals(iut.classifyAttribute("posfloat")))
			return failTest("positive float should return [object Number] but instead returned " + iut.classifyAttribute("posfloat"));
		if (!"[object Number]".equals(iut.classifyAttribute("octal")))
			return failTest("octal literal should return [object Number] but instead returned " + iut.classifyAttribute("octal"));
		if (!"[object Number]".equals(iut.classifyAttribute("hex")))
			return failTest("hexadecimal literal zero should return [object Number] but instead returned " + iut.classifyAttribute("hex"));
		if (!"[object String]".equals(iut.classifyAttribute("emptystring")))
			return failTest("empty string should return [object String] but instead returned " + iut.classifyAttribute("emptystring"));
		if (!"[object String]".equals(iut.classifyAttribute("alphastring")))
			return failTest("alpha string should return [object String] but instead returned " + iut.classifyAttribute("alphastring"));
		if (!"[object String]".equals(iut.classifyAttribute("numericstring")))
			return failTest("numeric string should return [object String] but instead returned " + iut.classifyAttribute("numericstring"));
		if (!"[object String]".equals(iut.classifyAttribute("alphanumericstring")))
			return failTest("alphanumeric string should return [object String] but instead returned " + iut.classifyAttribute("alphanumericstring"));
		if (!"[object Boolean]".equals(iut.classifyAttribute("booleantrue")))
			return failTest("boolean true should return [object Boolean] but instead returned " + iut.classifyAttribute("booleantrue"));
		if (!"[object Boolean]".equals(iut.classifyAttribute("booleanfalse")))
			return failTest("boolean false should return [object Boolean] but instead returned " + iut.classifyAttribute("booleanfalse"));
		if (!"[object Number]".equals(iut.classifyAttribute("nanvalue")))
			return failTest("NaN should return [object Number] but instead returned " + iut.classifyAttribute("nanvalue"));
		if (!"[object Number]".equals(iut.classifyAttribute("infinityvalue")))
			return failTest("Infinity zero should return [object Number] but instead returned " + iut.classifyAttribute("infinityvalue"));
		if (!"[object Object]".equals(iut.classifyAttribute("emptyobj")))
			return failTest("empty object should return [object Object] but instead returned " + iut.classifyAttribute("emptyobj"));
		if (!"[object Object]".equals(iut.classifyAttribute("obj")))
			return failTest("object with attribute should return [object Object] but instead returned " + iut.classifyAttribute("obj"));
		if (!"[object Array]".equals(iut.classifyAttribute("emptyarr")))
			return failTest("empty array should return [object Array] but instead returned " + iut.classifyAttribute("emptyarr"));
		if (!"[object Array]".equals(iut.classifyAttribute("arr")))
			return failTest("array should return [object Array] but instead returned " + iut.classifyAttribute("arr"));

		return passTest();
	}

	
	public TestResult testGetKeySet() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"zeroint\":0,\"negint\":-6,\"posint\":4,\"zerofloat\":0.0,\"negfloat\":-5.2,\"posfloat\":7.1,\"octal\":0400,\"hex\":0xFF," +
				"\"emptystring\":\"\",\"alphastring\":\"abcde\",\"numericstring\":\"123\",\"alphanumericstring\":\"123abc\"," +
				"\"booleantrue\":true,\"booleanfalse\":false,\"nullvalue\":null,\"nanvalue\":NaN,\"infinityvalue\":Infinity," +
				"\"emptyobj\":{},\"obj\":{\"myvalue\":3},\"emptyarr\":[],\"arr\":[1,2,3]}");

		Set<String> keys = iut.getKeySet();
		// make sure all keys defined above return true
		if (!keys.contains("zeroint"))
			return failTest("defined key 'zeroint' should be present");
		if (!keys.contains("posint"))
			return failTest("defined key 'posint' should be present");
		if (!keys.contains("negint"))
			return failTest("defined key 'negint' should be present");
		if (!keys.contains("zerofloat"))
			return failTest("defined key 'zerofloat' should be present");
		if (!keys.contains("negfloat"))
			return failTest("defined key 'negfloat' should be present");
		if (!keys.contains("posfloat"))
			return failTest("defined key 'posfloat' should be present");
		if (!keys.contains("octal"))
			return failTest("defined key 'octal' should be present");
		if (!keys.contains("hex"))
			return failTest("defined key 'hex' should be present");
		if (!keys.contains("emptystring"))
			return failTest("defined key 'emptystring' should be present");
		if (!keys.contains("alphastring"))
			return failTest("defined key 'alphastring' should be present");
		if (!keys.contains("numericstring"))
			return failTest("defined key 'numericstring' should be present");
		if (!keys.contains("alphanumericstring"))
			return failTest("defined key 'alphanumericstring' should be present");
		if (!keys.contains("booleantrue"))
			return failTest("defined key 'booleantrue' should be present");
		if (!keys.contains("booleanfalse"))
			return failTest("defined key 'booleanfalse' should be present");
		if (!keys.contains("nullvalue"))
			return failTest("defined key 'nullvalue' should be present");
		if (!keys.contains("nanvalue"))
			return failTest("defined key 'nanvalue' should be present");
		if (!keys.contains("infinityvalue"))
			return failTest("defined key 'infinityvalue' should be present");
		if (!keys.contains("emptyobj"))
			return failTest("defined key 'emptyobj' should be present");
		if (!keys.contains("obj"))
			return failTest("defined key 'obj' should be present");
		if (!keys.contains("emptyarr"))
			return failTest("defined key 'emptyarr' should be present");
		if (!keys.contains("arr"))
			return failTest("defined key 'arr' should be present");

		// a key not defined above should return false
		if (keys.contains("idontexist"))
			return failTest("nonexistant key 'idontexist' should return false");

		// check for JavaScript Object prototype properties; these technically exist on all objects, but should return false
		if (keys.contains("constructor"))
			return failTest("Object prototype key 'constructor' should not be present");
		if (keys.contains("hasOwnProperty"))
			return failTest("Object prototype key 'hasOwnProperty' should not be present");
		if (keys.contains("isPrototypeOf"))
			return failTest("Object prototype key 'isPrototypeOf' should not be present");
		if (keys.contains("length"))
			return failTest("Object prototype key 'length' should not be present");
		if (keys.contains("propertyIsEnumerable"))
			return failTest("Object prototype key 'propertyIsEnumerable' should not be present");
		if (keys.contains("prototype"))
			return failTest("Object prototype key 'prototype' should not be present");
		if (keys.contains("toString"))
			return failTest("Object prototype key 'toString' should not be present");
		if (keys.contains("toLocaleString"))
			return failTest("Object prototype key 'toLocaleString' should not be present");
		if (keys.contains("unique"))
			return failTest("Object prototype key 'unique' should not be present");
		if (keys.contains("valueOf"))
			return failTest("Object prototype key 'valueOf' should not be present");

		// now, define the above properties and make sure they now return true; they shouldn't be masked by the Object prototype versions
		// NOTE: hasOwnProperty is off limits for redefinition in all browsers, constructor, isPrototypeOf, isPropertyEnumerable,
		//		toString, toLocaleString, valueOf are off limits in IE (probably a very bad idea to redefine most others as well)
		BaseJSO iut2 = (BaseJSO)BaseJSO.createObject("{\"constructor\":1,\"isPrototypeOf\":2,\"length\":3,\"propertyIsEnumerable\":4,\"prototype\":5,\"toString\":6,\"toLocaleString\":7,\"unique\":8,\"valueOf\":9}");
		Set<String> keys2 = iut2.getKeySet();

		if (!keys2.contains("length"))
			return failTest("after redefinition, key 'length' should be present");
		if (!keys2.contains("prototype"))
			return failTest("after redefinition, key 'prototype' should be present");
		if (!keys2.contains("unique"))
			return failTest("after redefinition, key 'unique' should be present");

		return passTest();
	}

	
	public TestResult testCreateWithNoContent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject(null);
		if (iut == null)
			return failTest("createObject(null) should return a blank JSO");
		BaseJSO iut2 = (BaseJSO)BaseJSO.createObject("");
		if (iut2 == null)
			return failTest("createObject('') should return a blank JSO");
		BaseJSO iut3 = (BaseJSO)BaseJSO.createObject("[]");
		if (iut3 == null)
			return failTest("createObject('[]') should return a blank JSO");
		BaseJSO iut4 = (BaseJSO)BaseJSO.createObject("{}");
		if (iut4 == null)
			return failTest("createObject('{}') should return a blank JSO");

		return passTest();
	}

	
	public TestResult testCreateWithPartialContent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{thisisincomplete");
		if (iut == null)
			return failTest("createObject with incomplete JSON should return a blank JSO");

		return passTest();
	}

	
	public TestResult testCreateWithSimpleContent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"value1\":\"stringval\"}");
		if (!"stringval".equals(iut.getString("value1")))
			return failTest("createObject with single string value did not return correct value");

		return passTest();
	}

	
	public TestResult testCreateWithArrayContent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"array1\":[\"stringval1\",\"stringval2\"]}");
		if (iut.getArray("array1") == null)
			return failTest("createObject with array content did not contain an array object");

		return passTest();
	}
	
	public TestResult testCreateWithObjectContent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"object1\":{\"objvalue1\":\"stringval\"}}");
		if (iut.getObject("object1") == null)
			return failTest("createObject with object content did not contain an object");
	
		return passTest();
	}
	
	public TestResult testPrimitiveValuesPresent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"stringval\":\"i am a string\",\"emptystringval\":\"\",\"intval\":5,\"floatval\":6.025,\"doubleval\":999999999999999999999999999999999999999.9999,\"longval\":1201651536904672,\"booleanval1\":true,\"booleanval2\":false}");

		if (5 != iut.getIntegerPrimitive("intval"))
			return failTest("int primitive didn't match");
		if (Math.abs(6.025f - iut.getFloatPrimitive("floatval")) > FLOAT_FUDGE)
			return failTest("float primitive didn't match");
		if (Math.abs(999999999999999999999999999999999999999.9999 - iut.getDoublePrimitive("doubleval")) > FLOAT_FUDGE)
			return failTest("double primitive didn't match");
		if (1201651536904672L != iut.getLongPrimitive("longval"))
			return failTest("long primitive didn't match");
		if (!iut.getBooleanPrimitive("booleanval1"))
			return failTest("boolean true primitive didn't match");
		if (iut.getBooleanPrimitive("booleanval2"))
			return failTest("boolean false primitive didn't match");
	
		return passTest();
	}
	
	public TestResult testPrimitiveValuesNotPresentException() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{}");
		try
		{
			iut.getIntegerPrimitive("missingint");
			return failTest("missing integer primitive should have thrown an exception");
		} catch (UndefinedJavaScriptAttributeException expected) {
			// good	
		} catch (Exception e) {
			return failTest("incorrect exception thrown on missing element: " + e);
		}

		try
		{
			iut.getFloatPrimitive("missingfloat");
			return failTest("missing float primitive should have thrown an exception");
		} catch (UndefinedJavaScriptAttributeException expected) {
			// good	
		} catch (Exception e) {
			return failTest("incorrect exception thrown on missing element: " + e);
		}

		try
		{
			iut.getDoublePrimitive("missingdouble");
			return failTest("missing double primitive should have thrown an exception");
		} catch (UndefinedJavaScriptAttributeException expected) {
			// good	
		} catch (Exception e) {
			return failTest("incorrect exception thrown on missing element: " + e);
		}

		try
		{
			iut.getLongPrimitive("missinglong");
			return failTest("missing long primitive should have thrown an exception");
		} catch (UndefinedJavaScriptAttributeException expected) {
			// good	
		} catch (Exception e) {
			return failTest("incorrect exception thrown on missing element: " + e);
		}

		try
		{
			iut.getBooleanPrimitive("missingboolean");
			return failTest("missing boolean primitive should have thrown an exception");
		} catch (UndefinedJavaScriptAttributeException expected) {
			// good	
		} catch (Exception e) {
			return failTest("incorrect exception thrown on missing element: " + e);
		}

		return passTest();
	}
	
	public TestResult testWrapperValuesPresent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"stringval\":\"i am a string\",\"emptystringval\":\"\",\"intval\":5,\"floatval\":6.025,\"doubleval\":999999999999999999999999999999999999999.9999,\"longval\":1201651536904672,\"booleanval1\":true,\"booleanval2\":false}");

		if (!"i am a string".equals(iut.getString("stringval")))
			return failTest("String value didn't match");
		if (!"".equals(iut.getString("emptystringval")))
			return failTest("empty String value didn't match");
		if (!new Integer(5).equals(iut.getInteger("intval")))
			return failTest("Integer didn't match");
		if (Math.abs(new Float(6.025f).floatValue() - iut.getFloat("floatval").floatValue()) > FLOAT_FUDGE)
			return failTest("Float didn't match");
		if (Math.abs(new Double(999999999999999999999999999999999999999.9999).doubleValue() - iut.getDouble("doubleval").doubleValue()) > FLOAT_FUDGE)
			return failTest("Double didn't match");
		if (!new Long(1201651536904672L).equals(iut.getLong("longval")))
			return failTest("Long didn't match");
		if (!new Boolean(true).equals(iut.getBoolean("booleanval1")))
			return failTest("Boolean true didn't match");
		if (!new Boolean(false).equals(iut.getBoolean("booleanval2")))
			return failTest("Boolean false didn't match");
	
		return passTest();
	}
	
	public TestResult testWrapperValuesNotPresentNull() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{}");

		if (iut.getString("missing") != null)
			return failTest("missing String should return null");
		if (iut.getInteger("missing") != null)
			return failTest("missing Integer should return null");
		if (iut.getFloat("missing") != null)
			return failTest("missing Float should return null");
		if (iut.getDouble("missing") != null)
			return failTest("missing Double should return null");
		if (iut.getLong("missing") != null)
			return failTest("missing Long should return null");
		if (iut.getBoolean("missing") != null)
			return failTest("missing Boolean should return null");
	
		return passTest();
	}
	
	public TestResult testObjectPresent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"obj\":{\"intval\":5}}");
		if (!iut.isObject("obj"))
			return failTest("object expected");
		BaseJSO obj = (BaseJSO)iut.getObject("obj");
		if (5 != obj.getIntegerPrimitive("intval"))
			return failTest("object value didn't match");
	
		return passTest();
	}
	
	public TestResult testObjectNotPresentNull() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{}");
		if (iut.getObject("missing") != null)
			return failTest("missing Object should return null");
	
		return passTest();
	}
	
	public TestResult testAttributeNotAnObjectNull() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"intval\":5}");
		if (iut.getObject("intval") != null)
			return failTest("getObject on non-object should return null");
	
		return passTest();
	}
	
	public TestResult testArrayPresent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"arr\":[{\"val1\":2},{\"val1\":3}]}");
		if (!iut.isArray("arr"))
			return failTest("array expected");
		JsArray<BaseJSO> arr = iut.getArray("arr");
		if (2 != arr.length())
			return failTest("array length mismatch");
		for (int i = 0; i < arr.length(); i++)
		{
			if (!(arr.get(i) instanceof JavaScriptObject))
				return failTest("object expected");
			if (!arr.get(i).containsAttribute("val1"))
				return failTest("object should contain attribute");
		}

		return passTest();
	}
	
	public TestResult testArrayNotPresentNull() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{}");
		if (iut.getArray("missing") != null)
			return failTest("missing array should return null");
	
		return passTest();
	}
	
	public TestResult testSingleValueArray() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"arr\":[{\"val1\":2}]}");
		if (!iut.isArray("arr"))
			return failTest("array expected");
		JsArray<BaseJSO> arr = iut.getArray("arr");
		if (1 != arr.length())
			return failTest("array length mismatch");
		if (arr.get(0) == null)
			return failTest("array entry should not be null");
		if (!arr.get(0).containsAttribute("val1"))
			return failTest("array entry (object) should contain value");
	
		return passTest();
	}
	
	public TestResult testAttributeNotAnArray() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"intval\":5}");
		if (iut.isArray("intval"))
			return failTest("non-array attribute should't be an array");
	
		return passTest();
	}
	
	public TestResult testNumberArrayPresent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"arr\":[1,2,3]}");
		if (!iut.isArray("arr"))
			return failTest("NumberArray should not be null");
		JsArrayNumber arr = iut.getNumberArray("arr");
		if (3 != arr.length())
			return failTest("array length mismatch");
	
		return passTest();
	}
	
	public TestResult testNumberArrayNotPresentNull() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{}");
		if (iut.getNumberArray("missing") != null)
			return failTest("missing NumberArray should return null");
	
		return passTest();
	}
	
	public TestResult testEmbeddedObjectPresent() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"obj\":{\"value1\":1}}");
		if (iut.getObject("obj") == null)
			return failTest("embedded object should not be null");
		BaseJSO obj = (BaseJSO)iut.getObject("obj");
		if (1 != obj.getIntegerPrimitive("value1"))
			return failTest("embedded object value mismatch");
	
		return passTest();
	}
	
	public TestResult testEmbeddedObjectNotPresentNull() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{}");
		if (iut.getObject("missing") != null)
			return failTest("missing embedded Object should return null");
	
		return passTest();
	}
	
	public TestResult testCollection() {
		BaseJSO iut = (BaseJSO)BaseJSO.createObject("{\"arr\":[{\"val1\":2},{\"val1\":3}]}");
		Collection<BaseJSO> collection = iut.getCollection("arr");
		if (collection == null)
			return failTest("collection should not be null");
		if (2 != collection.size())
			return failTest("collection size mismatch");
		Iterator<BaseJSO> i = collection.iterator();
		if (i.next().getIntegerPrimitive("val1") != 2)
			return failTest("collection member value mismatch");
		if (i.next().getIntegerPrimitive("val1") != 3)
			return failTest("collection member value mismatch");
	
		return passTest();
	}
	
	public TestResult testToJSON() {
		String json = "{\"zeroint\":0, \"negint\":-6, \"posint\":4, \"zerofloat\":0.0, \"negfloat\":-5.2, \"posfloat\":7.1, \"octal\":0400, \"hex\":0xFF, " +
						"\"emptystring\":\"\", \"alphastring\":\"abcde\", \"numericstring\":\"123\", \"alphanumericstring\":\"123abc\", " +
						"\"booleantrue\":true, \"booleanfalse\":false, \"nullvalue\":null, \"nanvalue\":NaN, \"infinityvalue\":Infinity, " +
						"\"emptyobj\":{}, \"obj\":{\"myvalue\":3}, \"emptyarr\":[], \"arr\":[1,2,3]}";
		BaseJSO iut = (BaseJSO)BaseJSO.createObject(json);
		BaseJSO fromJSON = (BaseJSO)BaseJSO.createObject(iut.toJSON(null));

		for (String key : iut.getKeySet())
		{
			if (iut.isObject(key))
			{
				BaseJSO iutSubObj = (BaseJSO)iut.getObject(key);
				BaseJSO fromJSONSubObj = (BaseJSO)fromJSON.getObject(key);
				for (String subKey : iutSubObj.getKeySet())
				{
					if (!iutSubObj.getString(subKey).equals(fromJSONSubObj.getString(subKey)))
						return failTest("iutSubObj." + subKey + ":" + iutSubObj.getString(subKey) + ", fromJSONSubObj." + subKey + "=" + fromJSONSubObj.getString(subKey));
				}
			} else
				if (!iut.getString(key).equals(fromJSON.getString(key)))
					return failTest("iut." + key + ":" + iut.getString(key) + ", fromJSON." + key + "=" + fromJSON.getString(key));
		}

		return passTest();
	}
	
	private TestResult failTest(String message) {
		return new TestResult(false, message);
	}

	private TestResult passTest() {
		return new TestResult(true);
	}
}
