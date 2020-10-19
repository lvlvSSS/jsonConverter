package nelson.tools.jsonConverter.model4json.handler.field.impl;

import org.apache.commons.lang.StringUtils;

import nelson.tools.jsonConverter.helper.*;
import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.XmlFieldType;
import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;

/**
 * Handle the XmlField that the Type is String.
 * 
 * @author nelson
 *
 */
public class StringFieldHandle extends FieldHandler<String> {

	public StringFieldHandle(FieldHandler<?> parent) {
		super(parent);
	}

	public String postFieldValue(XmlField field, Object source) {
		if (source == null)
			return getDefaultValue(field);
		if (field.getType() != XmlFieldType.STRING)
			return getDefaultValue(field);
		if ((field.getMapType() == XmlFieldMapType.NORMAL || field.getMapType() == XmlFieldMapType.REF)
				&& StringUtils.isBlank(field.getFrom()))
			return getDefaultValue(field);
		String result = source instanceof String ? (String) source : JacksonUtils.toJson(source);
		return (result.length() > field.getLength() && field.getLength() > 0) ? result.substring(0, field.getLength())
				: result;
	}

	/**
	 * if can't get the value，then try to get default value;
	 * 
	 * @param field
	 * @return
	 */
	private String getDefaultValue(XmlField field) {
		if (field.isNullable())
			return null;
		return (field.getDefaultValue() != null && field.getDefaultValue().length() > field.getLength()
				&& field.getLength() > 0) ? field.getDefaultValue().substring(0, field.getLength())
						: field.getDefaultValue();
	}

}