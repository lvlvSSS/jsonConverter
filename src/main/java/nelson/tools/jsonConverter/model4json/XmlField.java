package nelson.tools.jsonConverter.model4json;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.XmlFieldType;

/**
 * the data model for the field in model4json.xml
 * 
 * @author nelson
 *
 */
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

	public String getName() {
		return this.name;
	}

	public void setName(String theName) {
		this.name = theName;
	}

	private XmlFieldType type;

	public XmlFieldType getType() {
		return this.type;
	}

	public void setType(XmlFieldType theType) {
		this.type = theType;
	}

	/**
	 * 对于MapType不为{@code XmlFieldType.JS_FUNCTION}时，该成员表示源数据的来源。
	 * <p/>
	 * 而对于MapType为{@code XmlFieldType.JS_FUNCTION}时，该成员表示入参。
	 * <p/>
	 * 具体的MapType见{@link XmlFieldMapType}
	 */
	private String from;

	public String getFrom() {
		return this.from;
	}

	public void setFrom(String theFrom) {
		this.from = theFrom;
	}

	private XmlFieldMapType mapType;

	public XmlFieldMapType getMapType() {
		return this.mapType;
	}

	public void setMapType(XmlFieldMapType theMapType) {
		this.mapType = theMapType;
	}

	/**
	 * MapType为{@code XmlFieldType.JS_FUNCTION}时，使用的function名称
	 */
	private String functionInName;

	public String getFunctionInName() {
		return this.functionInName;
	}

	public void setFunctionInName(String theFunctionInName) {
		this.functionInName = theFunctionInName;
	}

	/**
	 * MapType为{@code XmlFieldType.JS_FUNCTION}时，js主体
	 */
	private String script;

	public String getScript() {
		return this.script;
	}

	public void setScript(String theScript) {
		this.script = theScript;
	}

	/**
	 * MapType为{@code XmlFieldType.MAPPING}时，convert才会使用
	 */
	private String converter;

	public String getConverter() {
		return this.converter;
	}

	public void setConverter(String theConverter) {
		this.converter = theConverter;

	}

	private String defaultValue;

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String theDefaultValue) {
		this.defaultValue = theDefaultValue;
	}

	/**
	 * MapType为{@code XmlFieldType.REF}时使用
	 */
	private String refAlias;

	public String getRefAlias() {
		if (StringUtils.isBlank(this.refAlias)) {
			this.refAlias = this.name;
			return this.name;
		}
		return this.refAlias;
	}

	public void setRefAlias(String theRefAlias) {
		this.refAlias = theRefAlias;
	}

	private int length;

	public int getLength() {
		return this.length;
	}

	public void setLength(int theLength) {
		this.length = theLength;
	}

	private boolean nullable;

	public boolean isNullable() {
		return this.nullable;
	}

	public void setNullable(boolean theNullable) {
		this.nullable = theNullable;
	}
	/**
	 * sub fields
	 */
	private List<XmlField> fields;

	public List<XmlField> getFields() {
		return this.fields;
	}

	public void setFields(List<XmlField> theFields) {
		this.fields = theFields;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof XmlField))
			return false;
		XmlField other = (XmlField) o;
		if (!other.getType().equals(this.getType()))
			return false;
		if (!other.getScript().equals(this.getScript()))
			return false;
		if (!other.getRefAlias().equals(this.getRefAlias()))
			return false;
		if (!other.getName().equals(this.getName()))
			return false;
		if (!other.getMapType().equals(this.getMapType()))
			return false;
		if (other.getLength() != this.getLength())
			return false;
		if (!other.getFunctionInName().equals(this.getFunctionInName()))
			return false;
		if (!other.getFrom().equals(this.getFrom()))
			return false;
		if (!other.getDefaultValue().equals(this.getDefaultValue()))
			return false;
		if (!other.getConverter().equals(this.getConverter()))
			return false;
		// compare the sub field recursively
		if (other.getFields() != null && this.getFields() != null) {
			if (other.getFields().size() != this.getFields().size())
				return false;
			for (int i = 0; i < this.getFields().size(); i++) {
				final int index = i;
				if (other.getFields().stream().filter((item) -> {
					if (item.equals(this.getFields().get(index)))
						return true;
					return false;
				}).findFirst().isPresent())
					continue;
				return false;
			}
		} else if (other.getFields() == null && this.getFields() != null)
			return false;
		else if (other.getFields() != null && this.getFields() == null)
			return false;

		return true;
	}
}
