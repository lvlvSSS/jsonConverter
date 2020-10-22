package nelson.tools.jsonConverter.model4json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;

import nelson.tools.jsonConverter.helper.*;
import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.XmlFieldType;
import nelson.tools.jsonConverter.model4json.XmlModel;
import nelson.tools.jsonConverter.model4json.XmlModel4JsonManager;
import nelson.tools.jsonConverter.model4json.XmlModelType;
import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;
import nelson.tools.jsonConverter.model4json.handler.field.impl.BooleanFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.field.impl.FloatFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.field.impl.IntegerFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.field.impl.ListFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.field.impl.MapFieldHandle;
import nelson.tools.jsonConverter.model4json.handler.field.impl.StringFieldHandle;

public class ModelHandle {
	public ModelHandle() throws IOException, DocumentException {
		this.modelManager = new XmlModel4JsonManager();
	}

	public ModelHandle(XmlModel4JsonManager manager) {
		this.modelManager = manager;
	}

	/**
	 * 
	 * @param model4json the specified path for model4json.xml
	 * @throws IOException
	 * @throws DocumentException
	 */
	public ModelHandle(String model4json) throws IOException, DocumentException {
		this.modelManager = new XmlModel4JsonManager(model4json);
	}

	private static Log LOG = LogFactory.getLog(ModelHandle.class);
	private XmlModel4JsonManager modelManager;

	/**
	 * 
	 * @param modelName the name of <B>model</B> in model4json.xml
	 * @param jsonStr   the json string that need to be converted
	 * @return the json string that have been converted.
	 */
	public String getJson(String modelName, String jsonStr) {
		XmlModel model = modelManager.getModel(modelName);
		if (model == null || model.getFields() == null || model.getFields().size() < 1)
			return null;
		LOG.debug(String.format(
				"[ModelHandle.getJson] start to handle model - name[%s], resultType[%s], sourceType[%s], from[%s]",
				modelName, model.getResultType(), model.getSourceType(), model.getFrom()));
		// 根据model的from属性，进行转换，获取source
		LOG.debug(String.format("[ModelHandle.getModelSource] origin source - [%s]", jsonStr));
		jsonStr = getModelSource(model.getFrom(), jsonStr);
		LOG.debug(String.format("[ModelHandle.getModelSource] converted source - [%s]", jsonStr));

		Object result = null;
		if (model.getResultType() == XmlModelType.ARRAY && model.getSourceType() == XmlModelType.ARRAY) {
			List<Object> listSource = JacksonUtils.fromJsonList(jsonStr, Object.class);
			if (listSource == null)
				return null;
			result = getJsonListFromListObject(model.getFields(), listSource);
		} else if (model.getResultType() == XmlModelType.ARRAY && model.getSourceType() == XmlModelType.SINGLE) {
			Map<String, Object> tmpSource = JacksonUtils.fromJsonMap(jsonStr, String.class, Object.class);
			result = getJsonListFromObject(model.getFields(), tmpSource);
		} else {
			result = getJsonMap(model.getFields(), jsonStr);
		}
		return result == null ? null : JacksonUtils.toJson(result);
	}

	public List<Map<String, Object>> getJsonList(List<XmlField> modelFields, List<Map<String, Object>> source) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> mapSource : source) {
			Map<String, Object> refs = getRefs(modelFields, mapSource);
			Map<String, Object> tmpRe = new HashMap<String, Object>();
			for (XmlField field : modelFields) {
				if (field.getMapType() == XmlFieldMapType.REF || field.getType() == XmlFieldType.NONE)
					continue;
				Object fieldRe = getUnSpecifiedFieldValue(field, mapSource, refs);
				tmpRe.put(field.getName(), fieldRe);
			}
			result.add(tmpRe);
		}
		return result;
	}

	public List<Map<String, Object>> getJsonListFromListObject(List<XmlField> modelFields, List<Object> source) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Object mapSource : source) {
			Map<String, Object> refs = getRefs(modelFields, mapSource);
			Map<String, Object> tmpRe = new HashMap<String, Object>();
			for (XmlField field : modelFields) {
				if (field.getMapType() == XmlFieldMapType.REF || field.getType() == XmlFieldType.NONE)
					continue;
				Object fieldRe = getUnSpecifiedFieldValue(field, mapSource, refs);
				tmpRe.put(field.getName(), fieldRe);
			}
			result.add(tmpRe);
		}
		return result;
	}

	public List<Map<String, Object>> getJsonList(List<XmlField> modelFields, Map<String, Object> source) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> refs = getRefs(modelFields, source);
		Map<String, Object> tmpRe = new HashMap<String, Object>();
		for (XmlField field : modelFields) {
			if (field.getMapType() == XmlFieldMapType.REF || field.getType() == XmlFieldType.NONE)
				continue;
			Object fieldRe = getUnSpecifiedFieldValue(field, source, refs);
			tmpRe.put(field.getName(), fieldRe);
		}
		result.add(tmpRe);
		return result;
	}

	public List<Map<String, Object>> getJsonListFromObject(List<XmlField> modelFields, Object source) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> refs = getRefs(modelFields, source);
		Map<String, Object> tmpRe = new HashMap<String, Object>();
		for (XmlField field : modelFields) {
			if (field.getMapType() == XmlFieldMapType.REF || field.getType() == XmlFieldType.NONE)
				continue;
			Object fieldRe = getUnSpecifiedFieldValue(field, source, refs);
			tmpRe.put(field.getName(), fieldRe);
		}
		result.add(tmpRe);
		return result;
	}

	public Map<String, Object> getJsonMap(List<XmlField> modelFields, Object source) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> refs = getRefs(modelFields, source);
		for (XmlField field : modelFields) {
			if (field.getMapType() == XmlFieldMapType.REF || field.getType() == XmlFieldType.NONE)
				continue;
			Object fieldRe = getUnSpecifiedFieldValue(field, source, refs);
			result.put(field.getName(), fieldRe);
		}
		return result;
	}

	/**
	 * Get the refs for the <B>sub field</B> that the <B>type</B> of
	 * {@link XmlField} is JS_FUNCTION.
	 * 
	 * @param fields only use the <B>mapType</B> of {@link XmlField} is <B>REF</B>
	 * @param source
	 * @return
	 */
	private Map<String, Object> getRefs(List<XmlField> fields, Object source) {
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
	 * factory method，choose the specified {@link FieldHandler} based on the
	 * <B>type</B> in {@link XmlField}
	 * 
	 * @param field
	 * @param source
	 * @param refs   set <B>refs</B> to every <B>{@link FieldHandler}</B>，used for
	 *               the MapType of <B>Field</B> is JS_FUNCTION。
	 * @return
	 */
	private Object getUnSpecifiedFieldValue(XmlField field, Object source, Map<String, Object> refs) {
		Object re = null;
		FieldHandler<?> tmpHandle = null;
		if (field.getType() == XmlFieldType.STRING) {
			tmpHandle = new StringFieldHandle(null);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		} else if (field.getType() == XmlFieldType.INT) {
			tmpHandle = new IntegerFieldHandle(null);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		} else if (field.getType() == XmlFieldType.BOOLEAN) {
			tmpHandle = new BooleanFieldHandle(null);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		} else if (field.getType() == XmlFieldType.FLOAT) {
			tmpHandle = new FloatFieldHandle(null);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		} else if (field.getType() == XmlFieldType.NONE) {
			re = null;
		} else if (field.getType() == XmlFieldType.LIST) {
			tmpHandle = new ListFieldHandle(null);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		} else if (field.getType() == XmlFieldType.MAP) {
			tmpHandle = new MapFieldHandle(null);
			tmpHandle.setRefs(refs);
			re = tmpHandle.getFieldValue(field, source);
		}
		return re;
	}

	private String getModelSource(String from, String jsonStr) {
		if (StringUtils.isBlank(from))
			return jsonStr;
		List<String> arrs = Arrays.asList(from.split("\\.")).stream().filter(str -> {
			if (StringUtils.isBlank(str))
				return false;
			return true;
		}).collect(Collectors.toList());
		if (arrs == null || arrs.size() <= 0)
			return jsonStr;
		Map<String, Object> mapSource = JacksonUtils.fromJsonMap(jsonStr, String.class, Object.class);
		if (mapSource == null) {
			LOG.error(String.format("[Modelhandle.getModelSource] can't convert source[%s] to map ", jsonStr));
			return null;
		}
		return JacksonUtils.toJson(getSourceFromProperties(arrs, 0, mapSource));
	}

	/**
	 * considering the List/Array, this may be like 'a[1]'
	 */
	private final String indexPattern = "^([^\\[\\]]*)\\[(\\d+)]$";

	private Object getSourceFromProperties(List<String> properties, int index, Map<String, Object> source) {
		if (source == null)
			return null;
		if (properties.size() <= index)
			return source;
		if (properties.size() == index + 1) {
			return getProperty(properties.get(index), source);
		}
		Object tmp = getProperty(properties.get(index), source);
		return getSourceFromProperties(properties, index + 1,
				JacksonUtils.fromJsonMap(JacksonUtils.toJson(tmp), String.class, Object.class));
	}

	private Object getProperty(String property, Map<String, Object> source) {
		if (source == null)
			return null;
		Object result = null;
		if (RegexUtils.isMatch(indexPattern, property)) {
			int index = Integer.parseInt(RegexUtils.getValue(indexPattern, property, 2));
			if (index < 0) {
				LOG.error(String.format("[ModelHandle.getProperty] the index[%s] of property[%s] is illegal.", index,
						property));
				return null;
			}
			String prefix = RegexUtils.getValue(indexPattern, property, 1);
			if (!source.containsKey(prefix)) {
				LOG.error(String.format("[ModelHandle.getProperty] map source[%s] doesn't contain %s",
						JacksonUtils.toJson(source), prefix));
				return null;
			}
			List<Object> listMapSource = JacksonUtils.fromJsonList(JacksonUtils.toJson(source.get(prefix)),
					Object.class);
			if (listMapSource == null || listMapSource.size() <= index) {
				LOG.error(String.format(
						"[ModelHandle.getProperty] the index[%s] of property[%s] is greater than size[%s]", index,
						property, listMapSource.size()));
				return null;
			}
			result = listMapSource.get(index);
		} else {
			if (!source.containsKey(property)) {
				LOG.error(String.format("[ModelHandle.getProperty] map source[%s] doesn't contain %s ",
						JacksonUtils.toJson(source), property));
				return null;
			}
			result = source.get(property);
		}
		return result;
	}
}