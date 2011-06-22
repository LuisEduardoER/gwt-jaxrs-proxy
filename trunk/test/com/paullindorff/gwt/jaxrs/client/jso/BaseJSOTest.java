package com.paullindorff.gwt.jaxrs.client.jso;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;
import com.paullindorff.gwt.jaxrs.client.test.ui.BaseJSOTestDefs;
import com.paullindorff.gwt.jaxrs.client.test.ui.TestResult;

public class BaseJSOTest extends GWTTestCase {

	private static final BaseJSOTestDefs tests = new BaseJSOTestDefs();

	@Override
	public String getModuleName() {
		return BaseJSOTestDefs.TEST_MODULE_NAME;
	}

	@Test
	public void testRemoveMapWrapper() {
		TestResult result = tests.testRemoveMapWrapper();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testIsObject() {
		TestResult result = tests.testIsObject();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testIsArray() {
		TestResult result = tests.testIsArray();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testIsNumber() {
		TestResult result = tests.testIsNumber();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testContainsAttribute() {
		TestResult result = tests.testContainsAttribute();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testClassifyAttribute() {
		TestResult result = tests.testClassifyAttribute();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testGetKeySet() {
		TestResult result = tests.testCreateWithNoContent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testCreateWithNoContent() {
		TestResult result = tests.testCreateWithNoContent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testCreateWithPartialContent() {
		TestResult result = tests.testCreateWithPartialContent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testCreateWithSimpleContent() {
		TestResult result = tests.testCreateWithSimpleContent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testCreateWithArrayContent() {
		TestResult result = tests.testCreateWithArrayContent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testCreateWithObjectContent() {
		TestResult result = tests.testCreateWithObjectContent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testPrimitiveValuesPresent() {
		TestResult result = tests.testPrimitiveValuesPresent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testPrimitiveValuesNotPresentException() {
		TestResult result = tests.testPrimitiveValuesNotPresentException();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testWrapperValuesPresent() {
		TestResult result = tests.testWrapperValuesPresent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testWrapperValuesNotPresentNull() {
		TestResult result = tests.testWrapperValuesNotPresentNull();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testObjectPresent() {
		TestResult result = tests.testObjectNotPresentNull();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testObjectNotPresentNull() {
		TestResult result = tests.testObjectNotPresentNull();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testAttributeNotAnObjectNull() {
		TestResult result = tests.testArrayPresent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testArrayPresent() {
		TestResult result = tests.testArrayPresent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testArrayNotPresentNull() {
		TestResult result = tests.testArrayNotPresentNull();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testSingleValueArray() {
		TestResult result = tests.testSingleValueArray();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testAttributeNotAnArray() {
		TestResult result = tests.testAttributeNotAnArray();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testNumberArrayPresent() {
		TestResult result = tests.testNumberArrayPresent();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testNumberArrayNotPresentNull() {
		TestResult result = tests.testNumberArrayNotPresentNull();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testEmbeddedObjectPresent() {
		TestResult result = tests.testToJSON();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testEmbeddedObjectNotPresentNull() {
		TestResult result = tests.testToJSON();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testCollection() {
		TestResult result = tests.testToJSON();
		assertTrue(result.getMessage(), result.isPassed());
	}

	@Test
	public void testToJSON() {
		TestResult result = tests.testToJSON();
		assertTrue(result.getMessage(), result.isPassed());
	}
}
