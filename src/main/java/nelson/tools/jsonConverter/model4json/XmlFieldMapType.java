package nelson.tools.jsonConverter.model4json;

import nelson.tools.jsonConverter.model4json.handler.value.ValueHandler;

/**
 * the enum indicates that how to get the source data. the function should be
 * implemeted in the sub class of the {@link ValueHandler}
 */
public enum XmlFieldMapType {
	/**
	 * used for the params of the field that the XmlMapFieldType is JS_FUNCTION. and
	 * if the XmlMapFieldType is REF, this field would not be used as result.
	 */
	REF,
	// normal use.
	NORMAL,
	// indicates that use the result of the javascript
	JS_FUNCTION,
	// convert would be used here.
	MAPPING;
}
