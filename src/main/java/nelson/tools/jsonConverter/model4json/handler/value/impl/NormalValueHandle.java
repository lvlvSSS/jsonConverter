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
 * handle the XmlField that the MapType is NORMAL/REF, this class is the
 * implement class of {@link FieldHandler}.
 */
public enum NormalValueHandle implements ValueHandler {
	INSTANCE;
	private Log LOG = LogFactory.getLog(NormalValueHandle.class);
	// the source could be Array/List, so considering the pattern like 'a[1]'.
	private String indexPattern = "^([^\\[\\]]*)\\[(\\d+)]$";

	@SuppressWarnings("unchecked")
	@Override
	public Object handle(FieldHandler<?> fieldHandle, Object source) {
		if (source == null)
			return null;
		// if the MapType is not NORMAL/REF，then use the
		// ValueHandler[JsFunctionValueHandle] to handle.
		if (fieldHandle.getXmlField().getMapType() != XmlFieldMapType.NORMAL
				&& fieldHandle.getXmlField().getMapType() != XmlFieldMapType.REF) {
			fieldHandle.setValueHandler(JsFuncValueHandle.INSTANCE);
			return fieldHandle.getValueHandler().handle(fieldHandle, source);
		}
		// as the ListFieldHandle use the MapFieldHandle to handle，so we need to judge
		// as below:
		if (fieldHandle.getClass() == MapFieldHandle.class && fieldHandle.getParentHandler() != null
				&& fieldHandle.getParentHandler().getClass() == ListFieldHandle.class)
			return source;

		// if the from of the field is empty，then return the source directly.
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
		//
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