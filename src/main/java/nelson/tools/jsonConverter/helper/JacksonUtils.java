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
	 * json的转换mapper
	 */
	public static ObjectMapper jsonMapper;
	/**
	 * xml的转换mapper
	 */
	public static XmlMapper xmlMapper;

	static {
		/// Json mapper.
		jsonMapper = new ObjectMapper();
		// 在遇到未知属性时，不抛出异常
		jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 在遇到忽略的属性时，直接忽略
		jsonMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
		// 把java.util.Date, Calendar输出为数字（时间戳）
		jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		// 美化输出，使用空格
		jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
		// 允许序列化空的pojo类
		jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		// 强制JSON 空字符串("")转换为null对象值:
		jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		// 在JSON中允许C/C++ 样式的注释(非标准，默认禁用)
		jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		// 允许没有引号的字段名（非标准）
		jsonMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 允许单引号（非标准）
		jsonMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// 强制转义非ASCII字符
		// jsonMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		jsonMapper.findAndRegisterModules();

		// Xml mapper
		xmlMapper = new XmlMapper();
		// 在遇到未知属性时，不抛出异常
		xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 若类没有指定名称，则使用类名
		xmlMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		// 在遇到忽略的属性时，直接忽略
		xmlMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
		// 把java.util.Date, Calendar输出为数字（时间戳）
		xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		// 美化输出，使用空格
		xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
		// 允许序列化空的pojo类
		xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		// 强制JSON 空字符串("")转换为null对象值:
		xmlMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		// 在JSON中允许C/C++ 样式的注释(非标准，默认禁用)
		xmlMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		// 允许没有引号的字段名（非标准）
		xmlMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 允许单引号（非标准）
		xmlMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// 强制转义非ASCII字符
		// xmlMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		xmlMapper.findAndRegisterModules();
	}

	/**
	 * 将对象序列化为JSON格式
	 * 
	 * @param source 指定对象
	 * @return 返回 Json字符串
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
	 * 将Json字符串反序列化为List<T>类型
	 * 
	 * @param json  json字符串，
	 * @param clazz 表示T的class
	 * @return 返回List
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
	 * 将json字符串转换成T对象
	 * 
	 * @param json  为json字符串
	 * @param clazz 为T的class
	 * @return 返回T对象
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
	 * 将json字符串转换成Map
	 * 
	 * @param json   json字符串
	 * @param clazzK Key的class
	 * @param clazzV Value的class
	 * @return 返回Map<K,V>
	 */
	public static <K, V> Map<K, V> fromJsonMap(String json, Class<K> clazzK, Class<V> clazzV) {

		if (json == null)
			return null;

		JavaType jt = jsonMapper.getTypeFactory().constructParametricType(Map.class, clazzK, clazzV);
		Map<K, V> re = null;

		try {
			re = jsonMapper.readValue(json, jt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.warn(String.format("Convert json[%s] to Map<%s,%s> failed!", json, clazzK.getName(), clazzV.getName()),
					e);
		}
		return re;
	}

	/**
	 * 将对象序列化为xml字符串
	 * 
	 * @param source 目标对象
	 * @return 返回source序列化后的xml字符串
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
	 * 将xml字符串反序列化成T类型对象
	 * 
	 * @param <T>   范型参数T，T不能为集合对象
	 * @param xml   xml字符串
	 * @param clazz T的class
	 * @return 返回T的对象
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
