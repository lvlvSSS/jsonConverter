package nelson.tools.jsonConverter.model4json.handler.value.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import nelson.tools.jsonConverter.helper.*;
import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;
import nelson.tools.jsonConverter.model4json.handler.value.ValueHandler;
import nelson.tools.jsonConverter.model4json.handler.value.impl.MappingValueHandle;

public enum JsFuncValueHandle implements ValueHandler {
	INSTANCE;

	@Override
	public Object handle(FieldHandler<?> fieldHandle, Object source) {
		if (source == null)
			return null;
		// if the MapType is not NORMAL/REF,then use the next ValueHandler.
		if (fieldHandle.getXmlField().getMapType() != XmlFieldMapType.JS_FUNCTION) {
			fieldHandle.setValueHandler(MappingValueHandle.INSTANCE);
			return fieldHandle.getValueHandler().handle(fieldHandle, source);
		}
		// the params of the JsExecutor should be null, if the from of the field is
		// empty.
		if (StringUtils.isBlank(fieldHandle.getXmlField().getFrom()))
			return JsExecutor.INSTANCE.execute(fieldHandle.getXmlField().getScript(),
					fieldHandle.getXmlField().getFunctionInName());

		// from is separated by ','
		List<String> arrs = Arrays.asList(fieldHandle.getXmlField().getFrom().split(",")).stream().filter(str -> {
			if (StringUtils.isBlank(str))
				return false;
			return true;
		}).collect(Collectors.toList());
		// Get the input params.
		Object[] params = new Object[arrs.size()];
		for (int i = 0; i < arrs.size(); i++) {
			Object paramRe = getParam(arrs.get(i), fieldHandle);
			if (paramRe == null)
				params[i] = "";
			params[i] = paramRe;
		}
		return JsExecutor.INSTANCE.execute(fieldHandle.getXmlField().getScript(),
				fieldHandle.getXmlField().getFunctionInName(), params);
	}

	/**
	 * Get the param from the fieldHandle recursively.
	 * 
	 * @param param
	 * @param fieldHandle
	 * @return
	 */
	private Object getParam(String param, FieldHandler<?> fieldHandle) {
		if (fieldHandle == null)
			return null;
		if (fieldHandle.getRefs() == null || fieldHandle.getRefs().size() < 1
				|| !fieldHandle.getRefs().containsKey(param)) {
			if (fieldHandle.getParentHandler() == null)
				return null;
			return getParam(param, fieldHandle.getParentHandler());
		}
		return fieldHandle.getRefs().get(param);
	}

}
