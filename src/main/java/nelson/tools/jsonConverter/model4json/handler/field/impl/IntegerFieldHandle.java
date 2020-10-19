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
 * Handle the XmlField that the Type is Integer.
 * 
 * @author nelson
 *
 */
public class IntegerFieldHandle extends FieldHandler<Integer> {
	private Log LOG = LogFactory.getLog(IntegerFieldHandle.class);

	public IntegerFieldHandle(FieldHandler<?> parent) {
		super(parent);
	}

	@Override
	public Integer postFieldValue(XmlField field, Object source) {
		if (source == null)
			return getDefaultValue(field);
		if (field.getType() != XmlFieldType.INT)
			return getDefaultValue(field);
		if ((field.getMapType() == XmlFieldMapType.NORMAL || field.getMapType() == XmlFieldMapType.REF)
				&& StringUtils.isBlank(field.getFrom()))
			return getDefaultValue(field);
		String tmpRe = JacksonUtils.toJson(source);
		Integer re = null;
		try {
			re = Integer.parseInt(tmpRe);
		} catch (Exception ex) {
			LOG.warn(String.format("[IntegerFieldHandle.postFieldValue] failed to convert String[%s] error: ", tmpRe),
					ex);
		}
		return re == null ? getDefaultValue(field) : re;
	}

	/**
	 * 若得到的值为null，则做如下判断
	 * 
	 * @param field
	 * @return
	 */
	private Integer getDefaultValue(XmlField field) {
		if (field.isNullable())
			return null;
		String result = field.getDefaultValue();
		if (result == null)
			return null;
		try {
			return Integer.parseInt(result);
		} catch (Exception ex) {
			LOG.warn(String.format("[BooleanFieldHandle.postFieldValue] failed to convert default value[%s] error: ",
					result), ex);
		}
		return null;
	}

}