package nelson.tools.jsonConverter.model4json.handler.field.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nelson.tools.jsonConverter.helper.JacksonUtils;
import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.XmlFieldType;
import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;

public class BooleanFieldHandle extends FieldHandler<Boolean> {
	private static Log LOG = LogFactory.getLog(BooleanFieldHandle.class);

	public BooleanFieldHandle(FieldHandler<?> parent) {
		super(parent);
	}

	@Override
	public Boolean postFieldValue(XmlField field, Object source) {
		if (source == null)
			return getDefaultValue(field);
		if (field.getType() != XmlFieldType.BOOLEAN)
			return getDefaultValue(field);
		if ((field.getMapType() == XmlFieldMapType.NORMAL || field.getMapType() == XmlFieldMapType.REF)
				&& StringUtils.isBlank(field.getFrom()))
			return getDefaultValue(field);

		String tmpRe = JacksonUtils.toJson(source);
		Boolean re = null;
		try {
			re = Boolean.parseBoolean(tmpRe);
		} catch (Exception ex) {
			LOG.warn(String.format("[BooleanFieldHandle.postFieldValue] failed to convert String[%s] error: ", tmpRe),
					ex);
		}
		return re == null ? getDefaultValue(field) : re;
	}

	/**
	 * try to get the default value of the field.
	 * 
	 * @param field
	 * @return
	 */
	private Boolean getDefaultValue(XmlField field) {
		if (field.isNullable())
			return null;
		String result = field.getDefaultValue();
		if (result == null)
			return null;
		try {
			return Boolean.parseBoolean(result);
		} catch (Exception ex) {
			LOG.warn(String.format("[BooleanFieldHandle.postFieldValue] failed to convert default value[%s] error: ",
					result), ex);
		}
		return null;
	}

}
