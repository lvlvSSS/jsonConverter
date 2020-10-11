package nelson.tools.jsonConverter.model4json;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.XmlFieldMapType;
import nelson.tools.jsonConverter.model4json.XmlFieldType;
import nelson.tools.jsonConverter.model4json.XmlModel;
import nelson.tools.jsonConverter.model4json.XmlModelType;

/**
 * analysis the model4json.xml
 * <p/>
 * this class should be singleton
 * <p/>
 * 
 * @author nelson
 *
 */
public enum XmlModel4JsonManager {
	INSTANCE;
	private Log LOG = LogFactory.getLog(XmlModel4JsonManager.class);

	public Map<String, XmlModel> getModels() {
		return this.models;
	}

	public XmlModel getModel(String modelName) {
		if (StringUtils.isBlank(modelName))
			return null;
		if (models == null || models.size() < 1)
			return null;
		if (models.containsKey(modelName))
			return models.get(modelName);
		return null;
	}

	private Map<String, XmlModel> models;

	private XmlModel4JsonManager() {
	}

	/**
	 * 
	 * @param path the file path of the xml configuration, usually named as
	 *             'model4json.xml'
	 */
	public void init(String path) throws IOException, DocumentException {

		if (!path.endsWith(".xml")) {
			LOG.warn(String.format(
					"[XmlModelManager.init] failed ! because the com.bd.util.model4json.XmlModel4JsonManager.path[%s] is not xml file",
					path));
			return;
		}
		LOG.info(String.format("[XmlModel4JsonManager.init] start to analysis the xml file[%s]", path));

		models = new HashMap<String, XmlModel>();
		InputStream inputStream = new FileInputStream(path);
		SAXReader reader = new SAXReader();
		Document doc = reader.read(inputStream);
		// get the 'model' nodes.
		List<Node> nodes = doc.selectNodes("/models/model");
		for (Node modelNode : nodes) {
			String modelname = getAttributeValue(modelNode, "name");
			String modelver = getAttributeValue(modelNode, "ver");
			String modelgroup = getAttributeValue(modelNode, "group");
			XmlModelType modelsourcetype = XmlModel.toXmlModeType(getAttributeValue(modelNode, "sourceType"));
			XmlModelType modelresulttype = XmlModel.toXmlModeType(getAttributeValue(modelNode, "resultType"));
			String modelfrom = getAttributeValue(modelNode, "from");
			if (StringUtils.isBlank(modelname))
				continue;

			List<Node> fieldNodes = modelNode.selectNodes("field");
			if (fieldNodes.size() < 1)
				continue;

			XmlModel model = new XmlModel();
			model.setName(modelname);
			model.setGroup(modelgroup);
			model.setVer(modelver);
			model.setSourceType(modelsourcetype);
			model.setResultType(modelresulttype);
			model.setFrom(modelfrom);
			// handle the field
			for (Node fieldNode : fieldNodes) {
				String fieldName = getAttributeValue(fieldNode, "name");
				XmlFieldType fieldType = XmlField.toFieldType(getAttributeValue(fieldNode, "type"));
				if (fieldType == XmlFieldType.NONE)
					continue;

				XmlField field = new XmlField();
				field.setName(fieldName);
				field.setType(fieldType);
				field.setFrom(getAttributeValue(fieldNode, "from"));
				field.setMapType(XmlField.toFieldMapType(getAttributeValue(fieldNode, "mapType")));
				field.setConverter(getAttributeValue(fieldNode, "converter"));
				field.setDefaultValue(getAttributeValue(fieldNode, "defaultValue"));
				String fieldRefAlias = getAttributeValue(fieldNode, "refAlias");
				field.setRefAlias(StringUtils.isBlank(fieldRefAlias) ? fieldName : fieldRefAlias);

				int length = 0;
				String lengthStr = getAttributeValue(fieldNode, "length");
				try {
					length = Integer.parseInt(StringUtils.isBlank(lengthStr) ? "0" : lengthStr);
				} catch (NumberFormatException ex) {
					LOG.warn("[XmlModel4JsonManager.init] fieldLength errors: ", ex);
				}
				field.setLength(length);

				field.setNullable(!StringUtils.equalsIgnoreCase(getAttributeValue(fieldNode, "nullable"), "false"));

				if (field.getMapType() == XmlFieldMapType.JS_FUNCTION) {
					Node scriptNode = fieldNode.selectSingleNode("script");
					if (scriptNode == null)
						continue;
					String subFieldScript = scriptNode.getText();
					if (StringUtils.isBlank(subFieldScript))
						continue;
					String subFieldFunction = getAttributeValue(fieldNode, "functionInName");
					if (StringUtils.isBlank(subFieldFunction))
						continue;
					field.setScript(subFieldScript);
					field.setFunctionInName(subFieldFunction);
				}

				field.setFields(initSubFields(fieldNode));

				model.getFields().add(field);
			}

			models.put(modelname, model);
		}

		if (inputStream != null)
			inputStream.close();
		LOG.info(String.format("[XmlModel4JsonManager.init] analysis the xml file[%s] successfully!", path));
	}

	/**
	 * init the sub field of the field
	 * 
	 * @param fieldNode
	 * @return
	 */
	private List<XmlField> initSubFields(Node fieldNode) {
		XmlFieldType fieldType = XmlField.toFieldType(getAttributeValue(fieldNode, "type"));
		if (fieldType != XmlFieldType.LIST && fieldType != XmlFieldType.MAP)
			return null;
		List<Node> subFieldNodes = fieldNode.selectNodes("field");
		if (subFieldNodes.size() < 1)
			return null;

		List<XmlField> subFields = new ArrayList<XmlField>();
		for (Node subFieldNode : subFieldNodes) {
			String subFieldName = getAttributeValue(subFieldNode, "name");
			XmlFieldType subFieldType = XmlField.toFieldType(getAttributeValue(subFieldNode, "type"));
			if (subFieldType == XmlFieldType.NONE)
				continue;

			XmlField subField = new XmlField();
			subField.setName(subFieldName);
			subField.setType(subFieldType);

			String subFieldFrom = getAttributeValue(subFieldNode, "from");
			XmlFieldMapType subFieldMapType = XmlField.toFieldMapType(getAttributeValue(subFieldNode, "mapType"));
			String subFieldConverter = getAttributeValue(subFieldNode, "converter");
			String subFieldDefaultValue = getAttributeValue(subFieldNode, "defaultValue");
			String subFieldRefAlias = getAttributeValue(subFieldNode, "refAlias");
			int subFieldLength = 0;
			String subLengthStr = getAttributeValue(subFieldNode, "length");
			try {
				subFieldLength = Integer.parseInt(StringUtils.isBlank(subLengthStr) ? "0" : subLengthStr);
			} catch (NumberFormatException ex) {
				LOG.warn("[XmlModel4JsonManager.initSubFields] subFieldLength errors: ", ex);
			}

			boolean subFieldNullable = !StringUtils.equalsIgnoreCase(getAttributeValue(subFieldNode, "nullable"),
					"false");

			subField.setFrom(subFieldFrom);
			subField.setMapType(subFieldMapType);
			subField.setConverter(subFieldConverter);
			subField.setDefaultValue(subFieldDefaultValue);
			subField.setLength(subFieldLength);
			subField.setNullable(subFieldNullable);
			subField.setRefAlias(StringUtils.isBlank(subFieldRefAlias) ? subFieldName : subFieldRefAlias);
			if (subFieldMapType == XmlFieldMapType.JS_FUNCTION) {
				Node scriptNode = subFieldNode.selectSingleNode("script");
				if (scriptNode == null)
					continue;
				String subFieldScript = scriptNode.getText();
				if (StringUtils.isBlank(subFieldScript))
					continue;
				String subFieldFunction = getAttributeValue(subFieldNode, "functionInName");
				if (StringUtils.isBlank(subFieldFunction))
					continue;
				subField.setScript(subFieldScript);
				subField.setFunctionInName(subFieldFunction);
			}

			if (subFieldType == XmlFieldType.LIST || subFieldType == XmlFieldType.MAP)
				subField.setFields(initSubFields(subFieldNode));

			subFields.add(subField);
		}
		return subFields;
	}

	/**
	 * get the attribute from the {@link Node}
	 * 
	 * @param node
	 * @param attribute
	 * @return
	 */
	private String getAttributeValue(Node node, String attribute) {
		String targetAttr = String.format("@%s", attribute);
		Object target = node.selectObject(targetAttr);
		if (target == null)
			return null;
		try {
			Attribute attr = (Attribute) target;
			return attr.getValue();
		} catch (Exception ex) {
			return null;
		}
	}
}
