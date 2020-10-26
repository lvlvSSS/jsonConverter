package nelson.tools.jsonConverter.model4json.handler.value.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;
import nelson.tools.jsonConverter.model4json.handler.value.ValueHandler;

public enum MappingValueHandle implements ValueHandler {
	INSTANCE;
	private static Log LOG = LogFactory.getLog(MappingValueHandle.class);

	// this map could be set by the data from the database;
	// the key is converter, the value(Map<String,Object>) is the mappings.
	private Map<String, Map<String, Object>> mapping;

	public void setMapping(Map<String, Map<String, Object>> map) {
		this.mapping = map;
	}

	@Override
	public Object handle(FieldHandler<?> fieldHandle, Object source) {
		if (source == null)
			return null;
		// ValueHandler workflow：NORMAL/REF -> JS_FUNCTION -> MAPPING
		if (fieldHandle.getXmlField().getMapType() != XmlFieldMapType.MAPPING) {
			LOG.error(String.format(
					"[MappingValueHandle.handle] field[%s](mapType[%s]) can't handle the source in FieldHandler[%s]",
					fieldHandle.getXmlField().getName(), fieldHandle.getXmlField().getMapType(),
					fieldHandle.getClass().toString()));
			return null;
		}
		if (this.mapping.size() <= 0 || !this.mapping.containsKey(fieldHandle.getXmlField().getConverter())) {
			LOG.error(String.format(
					"[MappingValueHandle.handle] field[%s](mapType[%s]) - [%s] can't be handled in FieldHandler[%s]",
					fieldHandle.getXmlField().getName(), fieldHandle.getXmlField().getMapType(),
					fieldHandle.getXmlField().getConverter(), fieldHandle.getClass().toString()));
			return null;
		}
		return mapping.get(fieldHandle.getXmlField().getConverter()).get(fieldHandle.getXmlField().getFrom());
	}

}
