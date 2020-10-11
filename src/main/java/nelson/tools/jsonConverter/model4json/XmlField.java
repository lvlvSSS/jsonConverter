package nelson.tools.jsonConverter.model4json;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.XmlFieldType;

import lombok.Data;

/**
 * model4json.xml中field数据模型
 * 
 * @author nelson
 *
 */
@Data
public class XmlField {
	private static Log LOG = LogFactory.getLog(XmlField.class);

	public static XmlFieldType toFieldType(String fieldType) {
		if (StringUtils.isBlank(fieldType))
			return XmlFieldType.NONE;

		switch (fieldType.toLowerCase()) {
		case "string":
			return XmlFieldType.STRING;
		case "int":
			return XmlFieldType.INT;
		case "decimal":
			return XmlFieldType.FLOAT;
		case "boolean":
			return XmlFieldType.BOOLEAN;
		case "list":
			return XmlFieldType.LIST;
		case "map":
			return XmlFieldType.MAP;
		default:
			return XmlFieldType.NONE;
		}
	}

	public static XmlFieldMapType toFieldMapType(String mapType) {
		if (StringUtils.isBlank(mapType))
			return XmlFieldMapType.NORMAL;

		try {
			return Enum.valueOf(XmlFieldMapType.class, mapType.toUpperCase());
		} catch (IllegalArgumentException ex) {
			LOG.error("[XmlField.toFieldMapType] errors: ", ex);
		}
		return XmlFieldMapType.NORMAL;
	}

	private String name;

	private XmlFieldType type;
	/**
	 * 对于MapType不为{@code XmlFieldType.JS_FUNCTION}时，该成员表示源数据的来源。
	 * <p/>
	 * 而对于MapType为{@code XmlFieldType.JS_FUNCTION}时，该成员表示入参。
	 * <p/>
	 * 具体的MapType见{@link XmlFieldMapType}
	 */
	private String from;

	private XmlFieldMapType mapType;

	/**
	 * MapType为{@code XmlFieldType.JS_FUNCTION}时，使用的function名称
	 */
	private String functionInName;

	/**
	 * MapType为{@code XmlFieldType.JS_FUNCTION}时，js主体
	 */
	private String script;

	/**
	 * MapType为{@code XmlFieldType.MAPPING}时，convert才会使用
	 */
	private String converter;

	private String defaultValue;

	/**
	 * MapType为{@code XmlFieldType.REF}时使用
	 */
	private String refAlias;

	private int length;

	private boolean nullable;

	private List<XmlField> fields;
}
