package nelson.tools.jsonConverter.model4json.handler.field.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nelson.tools.jsonConverter.helper.*;
import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;
import nelson.tools.jsonConverter.model4json.handler.field.impl.MapFieldHandle;

/**
 * Handle the XmlField that the Type is LIST.
 * 
 * @author nelson
 *
 */
public class ListFieldHandle extends FieldHandler<List<Object>> {
	public ListFieldHandle(FieldHandler<?> parent) {
		super(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> postFieldValue(XmlField field, Object source) {
		if (source == null)
			return null;
		List<Object> re = new ArrayList<Object>();

		// check whether the source could be converted to Map.
		Map<String, Object> mapSource = source instanceof Map ? (Map<String, Object>) source
				: JacksonUtils.fromJsonMap(JacksonUtils.toJson(source), String.class, Object.class);
		// if could be converted to Map, then use the MapFieldHandle to get result.
		if (mapSource != null) {
			if (field.getFields() == null || field.getFields().size() <= 0)
				return null;
			Object tmp = new MapFieldHandle(this).getFieldValue(field, mapSource);
			if (tmp != null)
				re.add(tmp);
			return re.size() > 0 ? re : null;
		}

		// check whether source could be converted to List
		List<Object> listSource = source instanceof List ? (List<Object>) source
				: JacksonUtils.fromJsonList(JacksonUtils.toJson(source), Object.class);
		if (listSource != null) {
			// if there are no subFields，then use source directly.
			if (field.getFields() == null || field.getFields().size() < 1) {
				for (Object item : listSource) {
					re.add(JacksonUtils.toJson(item));
				}
			}
			// if there exists the subField，then use the MapFieldHandle to analysis.
			else {
				for (Object item : listSource) {
					Map<String, Object> listMapSource = JacksonUtils.fromJsonMap(JacksonUtils.toJson(item),
							String.class, Object.class);
					if (listMapSource == null)
						continue;
					Object listMapRe = new MapFieldHandle(this).getFieldValue(field, listMapSource);
					if (listMapRe != null)
						re.add(listMapRe);
				}
			}

			return re.size() > 0 ? re : null;
		}

		return null;
	}
}
