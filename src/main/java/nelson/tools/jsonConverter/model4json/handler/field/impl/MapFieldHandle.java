package nelson.tools.jsonConverter.model4json.handler.field.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nelson.tools.jsonConverter.helper.*;
import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.XmlFieldType;
import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;
import nelson.tools.jsonConverter.model4json.handler.field.impl.BooleanFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.field.impl.FloatFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.field.impl.IntegerFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.field.impl.ListFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.field.impl.StringFieldHandle;

/**
 * Handle the XmlField that the Type is Map.
 * 
 * @author nelson
 *
 */
public class MapFieldHandle extends FieldHandler<Map<String, Object>> {
	private static Log LOG = LogFactory.getLog(MapFieldHandle.class);

	public MapFieldHandle(FieldHandler<?> parent) {
		super(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> postFieldValue(XmlField field, Object source) {
		if (source == null)
			return null;
		// the ListFieldHanlde would use the MapFieldHandle directly，so need to check
		// the parentHandler
		if (!(field.getType() == XmlFieldType.MAP || this.parentHandler.getXmlField().getType() == XmlFieldType.LIST)
				|| field.getFields() == null || field.getFields().size() < 1) {
			LOG.error(String.format(
					"[MapFieldHandle.postFieldValue] field[%s] occurs error: current field[type(%s), fields(%d)] can't be analyzed by Map handler",
					field.getName(), field.getType().toString(),
					field.getFields() == null ? 0 : field.getFields().size()));
			return null;
		}

		Map<String, Object> tmpSource = source instanceof Map ? (Map<String, Object>) source
				: JacksonUtils.fromJsonMap(JacksonUtils.toJson(source), String.class, Object.class);
		if (tmpSource == null) {
			LOG.error(String.format(
					"[MapFieldHandle.postFieldValue] field[%s] occurs error: current field[type(%s), fields(%d)] has no right source",
					field.getName(), field.getType().toString(),
					field.getFields() == null ? 0 : field.getFields().size()));
			return null;
		}

		Map<String, Object> re = new HashMap<String, Object>();
		Map<String, Object> refs = getRefs(field.getFields(), tmpSource);
		for (XmlField item : field.getFields()) {
			if (item.getType() == XmlFieldType.NONE || item.getMapType() == XmlFieldMapType.REF)
				continue;
			Object tmp = getUnSpecifiedFieldValue(item, tmpSource, refs);
			re.put(item.getName(), tmp);
		}
		return re.size() > 0 ? re : null;
	}

	/**
	 * the XmlMapFieldType.REF of XmlField would be handled here.
	 * 
	 * @param fields
	 * @param source
	 * @return
	 */
	private Map<String, Object> getRefs(List<XmlField> fields, Map<String, Object> source) {
		Map<String, Object> re = new HashMap<String, Object>();
		for (XmlField subField : fields) {
			if (subField.getMapType() != XmlFieldMapType.REF)
				continue;
			Object tmp = getUnSpecifiedFieldValue(subField, source, null);
			if (tmp == null)
				continue;
			re.put(subField.getRefAlias(), tmp);
		}
		return re;
	}

	/**
	 * factory method choose the appropriate FieldHandler to handle the XmlField
	 * based on the Type.
	 * 
	 * @param field
	 * @param source
	 * @param refs   this would set the references for every non-ref FieldHandler.
	 * @return
	 */
	private Object getUnSpecifiedFieldValue(XmlField field, Map<String, Object> source, Map<String, Object> refs) {
		Object re = null;
		FieldHandler<?> tmpHandle = null;
		if (field.getType() == XmlFieldType.STRING) {
			tmpHandle = new StringFieldHandle(this);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		} else if (field.getType() == XmlFieldType.INT) {
			tmpHandle = new IntegerFieldHandle(this);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		} else if (field.getType() == XmlFieldType.BOOLEAN) {
			tmpHandle = new BooleanFieldHandle(this);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		} else if (field.getType() == XmlFieldType.FLOAT) {
			tmpHandle = new FloatFieldHandle(this);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		} else if (field.getType() == XmlFieldType.NONE) {
			re = null;
		} else if (field.getType() == XmlFieldType.LIST) {
			tmpHandle = new ListFieldHandle(this);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		} else if (field.getType() == XmlFieldType.MAP) {
			tmpHandle = new MapFieldHandle(this);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		}

		return re;
	}

}