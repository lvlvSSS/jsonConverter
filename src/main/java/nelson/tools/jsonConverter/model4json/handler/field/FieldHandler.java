package nelson.tools.jsonConverter.model4json.handler.field;

import java.util.Map;

import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.handler.value.ValueHandler;
import nelson.tools.jsonConverter.model4json.handler.value.impl.NormalValueHandle;

/**
 * the subclass is used to handle the attribute -'type' of the model4json.xml
 * 
 * @author nelson
 *
 */
public abstract class FieldHandler<T> {
	public FieldHandler(FieldHandler<?> parent) {
		this.parentHandler = parent;
		setValueHandler(NormalValueHandle.INSTANCE);
	}

	public T getFieldValue(XmlField field, Object source) {
		setXmlField(field);
		return postFieldValue(field, getValueHandler().handle(this, source));
	}

	public abstract T postFieldValue(XmlField field, Object source);

	protected FieldHandler<?> parentHandler;

	public FieldHandler<?> getParentHandler() {
		return this.parentHandler;
	}

	protected Map<String, Object> refs;

	public Map<String, Object> getRefs() {
		return this.refs;
	}

	public void setRefs(Map<String, Object> theRefs) {
		this.refs = theRefs;
	}

	private int index = -1;

	public final int getIndex() {
		return this.index;
	}

	private XmlField xmlField;

	public XmlField getXmlField() {
		return this.xmlField;
	}

	public void setXmlField(XmlField theXmlField) {
		this.xmlField = theXmlField;
	}

	private ValueHandler valueHandler;

	public final ValueHandler getValueHandler() {
		return this.valueHandler;
	}

	public final void setValueHandler(ValueHandler handle) {
		index++;
		this.valueHandler = handle;
	}
}
