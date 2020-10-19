package nelson.tools.jsonConverter.model4json.handler.value.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nelson.tools.jsonConverter.helper.JacksonUtils;
import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;
import nelson.tools.jsonConverter.model4json.handler.field.impl.ListFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.field.impl.MapFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.value.ValueHandler;
import nelson.tools.jsonConverter.helper.RegexUtils;

/**
 * 处理MapType为NORMAL/REF的XmlField,为{@link FieldHandler}的实现类。
 * 
 * @author nelson
 *
 */
public enum NormalValueHandle implements ValueHandler {
	INSTANCE;
	private Log LOG = LogFactory.getLog(NormalValueHandle.class);
	// 有可能是List或者Array，如a[1]这种模式
	private String indexPattern = "^([^\\[\\]]*)\\[(\\d+)]$";

	@SuppressWarnings("unchecked")
	@Override
	public Object handle(FieldHandler<?> fieldHandle, Object source) {
		if (source == null)
			return null;
		// 若MapType不是NORMAL或者REF，交给下一个ValueHandler处理。
		if (fieldHandle.getXmlField().getMapType() != XmlFieldMapType.NORMAL
				&& fieldHandle.getXmlField().getMapType() != XmlFieldMapType.REF) {
			fieldHandle.setValueHandler(JsFuncValueHandle.INSTANCE);
			return fieldHandle.getValueHandler().handle(fieldHandle, source);
		}
		// 由于ListFieldHandle时直接交给MapFieldHandle来处理的，所以需要做如下判断
		if (fieldHandle.getClass() == MapFieldHandle.class && fieldHandle.getParentHandler() != null
				&& fieldHandle.getParentHandler().getClass() == ListFieldHandle.class)
			return source;

		// 若field中from字段为空，则直接返回source。
		if (StringUtils.isBlank(fieldHandle.getXmlField().getFrom()))
			return source;

		Map<String, Object> mapSource = source instanceof Map ? (Map<String, Object>) source
				: JacksonUtils.fromJsonMap(JacksonUtils.toJson(source), String.class, Object.class);
		if (mapSource == null) {
			LOG.error(String.format(
					"[NormalValueHandle.handle] field[%s] can't convert source[%s] map in FieldHandler[%s]",
					fieldHandle.getXmlField().getName(), JacksonUtils.toJson(source),
					fieldHandle.getClass().toString()));
			return null;
		}
		// 若不为空，则需要进行分割from，分割符为'.'
		List<String> arrs = Arrays.asList(fieldHandle.getXmlField().getFrom().split("\\.")).stream().filter(str -> {
			if (StringUtils.isBlank(str))
				return false;
			return true;
		}).collect(Collectors.toList());

		// String[] arrs = fieldHandle.getXmlField().getFrom().split(".");
		if (arrs.size() <= fieldHandle.getIndex()) {
			LOG.error(String.format(
					"[NormalValueHandle.handle] field[%s] - the index[%d] not less than the length[%d] of splited string - from[%s] in FieldHandler[%s]",
					fieldHandle.getXmlField().getName(), fieldHandle.getIndex(), arrs.size(),
					fieldHandle.getXmlField().getFrom(), fieldHandle.getClass().toString()));
			return source;
		}
		String arrTarget = arrs.get(fieldHandle.getIndex());

		Object result = null;
		if (RegexUtils.isMatch(indexPattern, arrTarget)) {
			int arrTargetIndex = Integer.parseInt(RegexUtils.getValue(indexPattern, arrTarget, 2));
			if (arrTargetIndex < 0)
				return null;
			String arrTargetPrefix = RegexUtils.getValue(indexPattern, arrTarget, 1);
			if (!mapSource.containsKey(arrTargetPrefix)) {
				LOG.error(String.format(
						"[NormalValueHandle.handle] field[%s] map source[%s] doesn't contain %s in FieldHandler[%s]",
						fieldHandle.getXmlField().getName(), JacksonUtils.toJson(source), arrTargetPrefix,
						fieldHandle.getClass().toString()));
				return null;
			}
			List<Object> listMapSource = JacksonUtils.fromJsonList(JacksonUtils.toJson(mapSource.get(arrTargetPrefix)),
					Object.class);
			if (listMapSource == null || listMapSource.size() <= arrTargetIndex)
				return null;
			result = listMapSource.get(arrTargetIndex);
		} else {
			if (!mapSource.containsKey(arrTarget)) {
				LOG.error(String.format(
						"[NormalValueHandle.handle] field[%s] map source[%s] doesn't contain %s in FieldHandler[%s]",
						fieldHandle.getXmlField().getName(), JacksonUtils.toJson(source), arrTarget,
						fieldHandle.getClass().toString()));
				return null;
			}
			result = mapSource.get(arrTarget);
		}

		if (fieldHandle.getIndex() + 1 == arrs.size())
			return result;

		fieldHandle.setValueHandler(NormalValueHandle.INSTANCE);
		return fieldHandle.getValueHandler().handle(fieldHandle, result);
	}

}