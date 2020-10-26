package nelson.tools.jsonConverter.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class JacksonUtils {

	private static Log LOG = LogFactory.getLog(JacksonUtils.class);
	/**
	 * the converter for json in jackson
	 */
	public static ObjectMapper jsonMapper;
	/**
	 * the converter for xml in jackson.
	 */
	public static XmlMapper xmlMapper;

	static {

		jsonMapper = new ObjectMapper();
		// do not throw exception when the json string has unknown field.
		jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// ingore the properties if the properties should be ignored.
		jsonMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);

		jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

		jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		// Force to derialize the empty string of field as null.
		jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

		jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

		jsonMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

		jsonMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// force to convert the non-ascii chars.
		// jsonMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		jsonMapper.findAndRegisterModules();

		xmlMapper = new XmlMapper();

		xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// if the name is not specified, use the class name.
		xmlMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

		xmlMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);

		xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

		xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		xmlMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

		xmlMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

		xmlMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

		xmlMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// force to convert the non-ascii chars.
		// xmlMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		xmlMapper.findAndRegisterModules();
	}

	/**
	 * Serialize the object as json string.
	 * 
	 * @param source the object
	 * @return the json string.
	 */
	public static String toJson(Object source) {
		if (source == null)
			return null;
		if (source.getClass() == String.class)
			return source.toString();
		try {
			return jsonMapper.writeValueAsString(source);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			LOG.warn(String.format("Convert %s to json failed!", source.getClass().getName()), e);
		}
		return null;
	}

	/**
	 * Deserialize the json string as List<T>.
	 * 
	 * @param json  the json string，
	 * @param clazz the type of the class
	 * @return return the List<T>.
	 */
	public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
		if (json == null)
			return null;
		JavaType jt = jsonMapper.getTypeFactory().constructParametricType(List.class, clazz);
		List<T> re = null;
		try {
			re = jsonMapper.readValue(json, jt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.warn(String.format("Convert json[%s] to List<%s> failed!", json, clazz.getName()), e);
		}
		return re;
	}

	/**
	 * Deserialize the json string as T.
	 * 
	 * @param json
	 * @param clazz
	 * @return return the T object.
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		if (json == null)
			return null;

		T re = null;
		try {
			re = jsonMapper.readValue(json, clazz);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.warn(String.format("Convert json[%s] to Object[%s] failed!", json, clazz.getName()), e);
		}
		return re;
	}

	/**
	 * Deserialize the json string as map object.
	 * 
	 * @param json
	 * @param clazzK the class of K
	 * @param clazzV the class of V
	 * @return return the Map<K,V>
	 */
	public static <K, V> Map<K, V> fromJsonMap(String json, Class<K> clazzK, Class<V> clazzV) {

		if (json == null)
			return null;

		JavaType jt = jsonMapper.getTypeFactory().constructParametricType(Map.class, clazzK, clazzV);
		Map<K, V> re = null;

		try {
			re = jsonMapper.readValue(json, jt);
		} catch (Exception e) {
			LOG.warn(String.format("Convert json[%s] to Map<%s,%s> failed!", json, clazzK.getName(), clazzV.getName()),
					e);
		}
		return re;
	}

	/**
	 * Serialize the object as xml string.
	 * 
	 * @param source the target object
	 * @return
	 */
	public static String toXml(Object source) {
		if (source == null)
			return null;

		try {
			return xmlMapper.writeValueAsString(source);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			LOG.warn(String.format("Convert %s to xml failed!", source.getClass().getName()), e);
		}
		return null;
	}

	/**
	 * Deserialize the xml string as T object.
	 * 
	 * @param <T>   <B>T should not be the collection or iterable.</B>
	 * @param xml
	 * @param clazz the class of T
	 * @return
	 */
	public static <T> T fromXml(String xml, Class<T> clazz) {
		if (xml == null)
			return null;

		T re = null;
		try {
			re = xmlMapper.readValue(xml, clazz);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.warn(String.format("Convert xml[%s] to Object[%s] failed!", xml, clazz.getName()), e);
		}
		return re;
	}
}
