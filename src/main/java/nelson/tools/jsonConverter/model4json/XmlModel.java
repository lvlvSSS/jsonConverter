package nelson.tools.jsonConverter.model4json;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * the model of model4json.xml
 * 
 */
public class XmlModel {
	private static Log LOG = LogFactory.getLog(XmlModel.class);

	public XmlModel() {
		this.fields = new ArrayList<XmlField>();
	}

	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String theName) {
		this.name = theName;
	}

	private String ver;

	public String getVer() {
		return this.ver;
	}

	public void setVer(String theVer) {
		this.ver = theVer;
	}

	private String group;

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String theGroup) {
		this.group = theGroup;
	}

	private String from;

	public String getFrom() {
		return this.from;
	}

	public void setFrom(String theFrom) {
		this.from = theFrom;
	}

	private XmlModelType sourceType;

	public XmlModelType getSourceType() {
		return this.sourceType;
	}

	public void setSourceType(XmlModelType theSourceType) {
		this.sourceType = theSourceType;
	}

	private XmlModelType resultType;

	public XmlModelType getResultType() {
		return this.resultType;
	}

	public void setResultType(XmlModelType theResultType) {
		this.resultType = theResultType;
	}

	private List<XmlField> fields;

	public List<XmlField> getFields() {
		return this.fields;
	}

	public void setFields(List<XmlField> theFields) {
		this.fields = theFields;
	}

	public static XmlModelType toXmlModeType(String value) {
		if (StringUtils.isBlank(value))
			return XmlModelType.ARRAY;
		try {
			return Enum.valueOf(XmlModelType.class, value.toUpperCase());
		} catch (IllegalArgumentException ex) {
			LOG.error("[XmlField.toFieldMapType] errors: ", ex);
		}
		return XmlModelType.ARRAY;
	}
}
