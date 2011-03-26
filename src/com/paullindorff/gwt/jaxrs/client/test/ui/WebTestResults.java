package com.paullindorff.gwt.jaxrs.client.test.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WebTestResults extends Composite {

	interface ResultsStyle extends CssResource {
		String pass();
		String fail();
	}

	private static WebTestResultsUiBinder uiBinder = GWT.create(WebTestResultsUiBinder.class);

	interface WebTestResultsUiBinder extends UiBinder<Widget, WebTestResults> {}

	@UiField ResultsStyle style;
	@UiField Label testIsObjectResultLabel;
	@UiField Label testIsArrayResultLabel;
	@UiField Label testIsNumberResultLabel;
	@UiField Label testContainsAttributeResultLabel;
	@UiField Label testClassifyAttributeResultLabel;
	@UiField Label testGetKeySetResultLabel;
	@UiField Label testCreateWithNoContentResultLabel;
	@UiField Label testCreateWithPartialContentResultLabel;
	@UiField Label testCreateWithSimpleContentResultLabel;
	@UiField Label testCreateWithArrayContentResultLabel;
	@UiField Label testCreateWithObjectContentResultLabel;
	@UiField Label testPrimitiveValuesPresentResultLabel;
	@UiField Label testPrimitiveValuesNotPresentExceptionResultLabel;
	@UiField Label testWrapperValuesPresentResultLabel;
	@UiField Label testWrapperValuesNotPresentNullResultLabel;
	@UiField Label testObjectPresentResultLabel;
	@UiField Label testObjectNotPresentNullResultLabel;
	@UiField Label testAttributeNotAnObjectNullResultLabel;
	@UiField Label testArrayPresentResultLabel;
	@UiField Label testArrayNotPresentNullResultLabel;
	@UiField Label testSingleValueArrayResultLabel;
	@UiField Label testAttributeNotAnArrayResultLabel;
	@UiField Label testNumberArrayPresentResultLabel;
	@UiField Label testNumberArrayNotPresentNullResultLabel;
	@UiField Label testEmbeddedObjectPresentResultLabel;
	@UiField Label testEmbeddedObjectNotPresentNullResultLabel;
	@UiField Label testCollectionResultLabel;
	@UiField Label testToJSONResultLabel;

	@UiField Label testIsObjectMessageLabel;
	@UiField Label testIsArrayMessageLabel;
	@UiField Label testIsNumberMessageLabel;
	@UiField Label testContainsAttributeMessageLabel;
	@UiField Label testClassifyAttributeMessageLabel;
	@UiField Label testGetKeySetMessageLabel;
	@UiField Label testCreateWithNoContentMessageLabel;
	@UiField Label testCreateWithPartialContentMessageLabel;
	@UiField Label testCreateWithSimpleContentMessageLabel;
	@UiField Label testCreateWithArrayContentMessageLabel;
	@UiField Label testCreateWithObjectContentMessageLabel;
	@UiField Label testPrimitiveValuesPresentMessageLabel;
	@UiField Label testPrimitiveValuesNotPresentExceptionMessageLabel;
	@UiField Label testWrapperValuesPresentMessageLabel;
	@UiField Label testWrapperValuesNotPresentNullMessageLabel;
	@UiField Label testObjectPresentMessageLabel;
	@UiField Label testObjectNotPresentNullMessageLabel;
	@UiField Label testAttributeNotAnObjectNullMessageLabel;
	@UiField Label testArrayPresentMessageLabel;
	@UiField Label testArrayNotPresentNullMessageLabel;
	@UiField Label testSingleValueArrayMessageLabel;
	@UiField Label testAttributeNotAnArrayMessageLabel;
	@UiField Label testNumberArrayPresentMessageLabel;
	@UiField Label testNumberArrayNotPresentNullMessageLabel;
	@UiField Label testEmbeddedObjectPresentMessageLabel;
	@UiField Label testEmbeddedObjectNotPresentNullMessageLabel;
	@UiField Label testCollectionMessageLabel;
	@UiField Label testToJSONMessageLabel;

	public WebTestResults() {
		initWidget(uiBinder.createAndBindUi(this));

		BaseJSOTestDefs tests = new BaseJSOTestDefs();
		TestResult testIsObjectResult = tests.testIsObject();
		TestResult testIsArrayResult = tests.testIsArray();
		TestResult testIsNumberResult = tests.testIsNumber();
		TestResult testContainsAttributeResult = tests.testContainsAttribute();
		TestResult testClassifyAttributeResult = tests.testClassifyAttribute();
		TestResult testGetKeySetResult = tests.testGetKeySet();
		TestResult testCreateWithNoContentResult = tests.testCreateWithNoContent();
		TestResult testCreateWithPartialContentResult = tests.testCreateWithPartialContent();
		TestResult testCreateWithSimpleContentResult = tests.testCreateWithSimpleContent();
		TestResult testCreateWithArrayContentResult = tests.testCreateWithArrayContent();
		TestResult testCreateWithObjectContentResult = tests.testCreateWithObjectContent();
		TestResult testPrimitiveValuesPresentResult = tests.testPrimitiveValuesPresent();
		TestResult testPrimitiveValuesNotPresentExceptionResult = tests.testPrimitiveValuesNotPresentException();
		TestResult testWrapperValuesPresentResult = tests.testWrapperValuesPresent();
		TestResult testWrapperValuesNotPresentNullResult = tests.testWrapperValuesNotPresentNull();
		TestResult testObjectPresentResult = tests.testObjectPresent();
		TestResult testObjectNotPresentNullResult = tests.testObjectNotPresentNull();
		TestResult testAttributeNotAnObjectNullResult = tests.testAttributeNotAnObjectNull();
		TestResult testArrayPresentResult = tests.testArrayPresent();
		TestResult testArrayNotPresentNullResult = tests.testArrayNotPresentNull();
		TestResult testSingleValueArrayResult = tests.testSingleValueArray();
		TestResult testAttributeNotAnArrayResult = tests.testAttributeNotAnArray();
		TestResult testNumberArrayPresentResult = tests.testNumberArrayPresent();
		TestResult testNumberArrayNotPresentNullResult = tests.testNumberArrayNotPresentNull();
		TestResult testEmbeddedObjectPresentResult = tests.testEmbeddedObjectPresent();
		TestResult testEmbeddedObjectNotPresentNullResult = tests.testEmbeddedObjectNotPresentNull();
		TestResult testCollectionResult = tests.testCollection();
		TestResult testToJSONResult = tests.testToJSON();
		
		testIsObjectResultLabel.addStyleName((testIsObjectResult.isPassed() ? style.pass() : style.fail()));
		testIsObjectMessageLabel.setText(testIsObjectResult.getMessage());

		testIsArrayResultLabel.addStyleName((testIsArrayResult.isPassed() ? style.pass() : style.fail()));
		testIsArrayMessageLabel.setText(testIsArrayResult.getMessage());

		testIsNumberResultLabel.addStyleName((testIsNumberResult.isPassed() ? style.pass() : style.fail()));
		testIsNumberMessageLabel.setText(testIsNumberResult.getMessage());

		testContainsAttributeResultLabel.addStyleName((testContainsAttributeResult.isPassed() ? style.pass() : style.fail()));
		testContainsAttributeMessageLabel.setText(testContainsAttributeResult.getMessage());

		testClassifyAttributeResultLabel.addStyleName((testClassifyAttributeResult.isPassed() ? style.pass() : style.fail()));
		testClassifyAttributeMessageLabel.setText(testClassifyAttributeResult.getMessage());
		
		testGetKeySetResultLabel.addStyleName((testGetKeySetResult.isPassed() ? style.pass() : style.fail()));
		testGetKeySetMessageLabel.setText(testGetKeySetResult.getMessage());

		testCreateWithNoContentResultLabel.addStyleName((testCreateWithNoContentResult.isPassed() ? style.pass() : style.fail()));
		testCreateWithNoContentMessageLabel.setText(testCreateWithNoContentResult.getMessage());

		testCreateWithPartialContentResultLabel.addStyleName((testCreateWithPartialContentResult.isPassed() ? style.pass() : style.fail()));
		testCreateWithPartialContentMessageLabel.setText(testCreateWithPartialContentResult.getMessage());

		testCreateWithSimpleContentResultLabel.addStyleName((testCreateWithSimpleContentResult.isPassed() ? style.pass() : style.fail()));
		testCreateWithSimpleContentMessageLabel.setText(testCreateWithSimpleContentResult.getMessage());

		testCreateWithArrayContentResultLabel.addStyleName((testCreateWithArrayContentResult.isPassed() ? style.pass() : style.fail()));
		testCreateWithArrayContentMessageLabel.setText(testCreateWithArrayContentResult.getMessage());

		testCreateWithObjectContentResultLabel.addStyleName((testCreateWithObjectContentResult.isPassed() ? style.pass() : style.fail()));
		testCreateWithObjectContentMessageLabel.setText(testCreateWithObjectContentResult.getMessage());

		testPrimitiveValuesPresentResultLabel.addStyleName((testPrimitiveValuesPresentResult.isPassed() ? style.pass() : style.fail()));
		testPrimitiveValuesPresentMessageLabel.setText(testPrimitiveValuesPresentResult.getMessage());

		testPrimitiveValuesNotPresentExceptionResultLabel.addStyleName((testPrimitiveValuesNotPresentExceptionResult.isPassed() ? style.pass() : style.fail()));
		testPrimitiveValuesNotPresentExceptionMessageLabel.setText(testPrimitiveValuesNotPresentExceptionResult.getMessage());

		testWrapperValuesPresentResultLabel.addStyleName((testWrapperValuesPresentResult.isPassed() ? style.pass() : style.fail()));
		testWrapperValuesPresentMessageLabel.setText(testWrapperValuesPresentResult.getMessage());

		testWrapperValuesNotPresentNullResultLabel.addStyleName((testWrapperValuesNotPresentNullResult.isPassed() ? style.pass() : style.fail()));
		testWrapperValuesNotPresentNullMessageLabel.setText(testWrapperValuesNotPresentNullResult.getMessage());

		testObjectPresentResultLabel.addStyleName((testObjectPresentResult.isPassed() ? style.pass() : style.fail()));
		testObjectPresentMessageLabel.setText(testObjectPresentResult.getMessage());

		testObjectNotPresentNullResultLabel.addStyleName((testObjectNotPresentNullResult.isPassed() ? style.pass() : style.fail()));
		testObjectNotPresentNullMessageLabel.setText(testObjectNotPresentNullResult.getMessage());

		testAttributeNotAnObjectNullResultLabel.addStyleName((testAttributeNotAnObjectNullResult.isPassed()? style.pass() : style.fail()));
		testAttributeNotAnObjectNullMessageLabel.setText(testAttributeNotAnObjectNullResult.getMessage());

		testArrayPresentResultLabel.addStyleName((testArrayPresentResult.isPassed() ? style.pass() : style.fail()));
		testArrayPresentMessageLabel.setText(testArrayPresentResult.getMessage());

		testArrayNotPresentNullResultLabel.addStyleName((testArrayNotPresentNullResult.isPassed() ? style.pass() : style.fail()));
		testArrayNotPresentNullMessageLabel.setText(testArrayNotPresentNullResult.getMessage());

		testSingleValueArrayResultLabel.addStyleName((testSingleValueArrayResult.isPassed() ? style.pass() : style.fail()));
		testSingleValueArrayMessageLabel.setText(testSingleValueArrayResult.getMessage());

		testAttributeNotAnArrayResultLabel.addStyleName((testAttributeNotAnArrayResult.isPassed() ? style.pass() : style.fail()));
		testAttributeNotAnArrayMessageLabel.setText(testAttributeNotAnArrayResult.getMessage());

		testNumberArrayPresentResultLabel.addStyleName((testNumberArrayPresentResult.isPassed() ? style.pass() : style.fail()));
		testNumberArrayPresentMessageLabel.setText(testNumberArrayPresentResult.getMessage());

		testNumberArrayNotPresentNullResultLabel.addStyleName((testNumberArrayNotPresentNullResult.isPassed() ? style.pass() : style.fail()));
		testNumberArrayNotPresentNullMessageLabel.setText(testEmbeddedObjectPresentResult.getMessage());

		testEmbeddedObjectPresentResultLabel.addStyleName((testEmbeddedObjectPresentResult.isPassed() ? style.pass() : style.fail()));
		testEmbeddedObjectPresentMessageLabel.setText(testEmbeddedObjectPresentResult.getMessage());

		testEmbeddedObjectNotPresentNullResultLabel.addStyleName((testEmbeddedObjectNotPresentNullResult.isPassed() ? style.pass() : style.fail()));
		testEmbeddedObjectNotPresentNullMessageLabel.setText(testEmbeddedObjectNotPresentNullResult.getMessage());

		testCollectionResultLabel.addStyleName((testCollectionResult.isPassed() ? style.pass() : style.fail()));
		testCollectionMessageLabel.setText(testCollectionResult.getMessage());

		testToJSONResultLabel.addStyleName((testToJSONResult.isPassed() ? style.pass() : style.fail()));
		testToJSONMessageLabel.setText(testToJSONResult.getMessage());
	}
}
