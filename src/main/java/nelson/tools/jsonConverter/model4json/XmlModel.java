package nelson.tools.jsonConverter.model4json;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * model4json.xml中的model的模型。
 * 
 * @author nelson
 *
 */
public class XmlModel {
	private static Log LOG = LogFactory.getLog(XmlModel.class);

	public XmlModel() {
		this.fields = new ArrayList<XmlField>();
	}

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String ver;

	@Getter
	@Setter
	private String group;

	@Getter
	@Setter
	private String from;

	@Getter
	@Setter
	private XmlModelType sourceType;

	@Getter
	@Setter
	private XmlModelType resultType;

	@Getter
	@Setter
	private List<XmlField> fields;

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
