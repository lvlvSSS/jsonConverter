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
		// 若MapType不是NORMAL或者REF，交给下一个ValueHandler处理。
		if (fieldHandle.getXmlField().getMapType() != XmlFieldMapType.JS_FUNCTION) {
			fieldHandle.setValueHandler(MappingValueHandle.INSTANCE);
			return fieldHandle.getValueHandler().handle(fieldHandle, source);
		}
		// 若field中from字段为空，则JsExecutor的入参为null。
		if (StringUtils.isBlank(fieldHandle.getXmlField().getFrom()))
			return JsExecutor.INSTANCE.execute(fieldHandle.getXmlField().getScript(),
					fieldHandle.getXmlField().getFunctionInName());

		// from属性以','分隔。
		List<String> arrs = Arrays.asList(fieldHandle.getXmlField().getFrom().split(",")).stream().filter(str -> {
			if (StringUtils.isBlank(str))
				return false;
			return true;
		}).collect(Collectors.toList());
		// 获取入参Object[] params
		Object[] params = new Object[arrs.size()];
		// List<Object> params = new ArrayList<Object>();
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
	 * 递归，从refs中获取入参值，若当前的fieldHandle中没有，就从parentHandler的fieldHandle中的refs获取。
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
