package nelson.tools.jsonConverter.model4json.handler.field.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nelson.tools.jsonConverter.helper.*;
import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.XmlFieldType;
import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;

/**
 * Handle the XmlField that the Type is Float.
 * 
 * @author nelson
 *
 */
public class FloatFieldHandle extends FieldHandler<Float> {
	private static Log LOG = LogFactory.getLog(FloatFieldHandle.class);

	public FloatFieldHandle(FieldHandler<?> parent) {
		super(parent);
	}

	@Override
	public Float postFieldValue(XmlField field, Object source) {
		if (source == null)
			return getDefaultValue(field);
		if (field.getType() != XmlFieldType.FLOAT)
			return getDefaultValue(field);
		if ((field.getMapType() == XmlFieldMapType.NORMAL || field.getMapType() == XmlFieldMapType.REF)
				&& StringUtils.isBlank(field.getFrom()))
			return getDefaultValue(field);
		if (source instanceof Float)
			return (Float) source;
		if (source instanceof Number)
			return ((Number) source).floatValue();

		String tmpRe = JacksonUtils.toJson(source);
		Float re = null;
		try {
			re = Float.parseFloat(tmpRe);
		} catch (Exception ex) {
			LOG.warn(String.format("[FloatFieldHandle.postFieldValue] failed to convert String[%s] error: ", tmpRe),
					ex);
		}
		return re == null ? getDefaultValue(field) : re;
	}

	/**
	 * if the result is null, then use the function to check again.
	 * 
	 * @param field
	 * @return
	 */
	private Float getDefaultValue(XmlField field) {
		if (field.isNullable())
			return null;
		String result = field.getDefaultValue();
		if (result == null)
			return null;
		try {
			return Float.parseFloat(result);
		} catch (Exception ex) {
			LOG.warn(String.format("[BooleanFieldHandle.postFieldValue] failed to convert default value[%s] error: ",
					result), ex);
		}
		return null;
	}

}