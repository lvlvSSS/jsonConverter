package nelson.tools.jsonConverter.model4json.handler.field;

import java.util.Map;

import nelson.tools.jsonConverter.model4json.XmlField;
import nelson.tools.jsonConverter.model4json.handler.value.ValueHandler;
import nelson.tools.jsonConverter.model4json.handler.value.impl.NormalValueHandle;

import lombok.Getter;
import lombok.Setter;

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

	@Getter
	protected FieldHandler<?> parentHandler;

	@Getter
	@Setter
	protected Map<String, Object> refs;

	private int index = -1;

	public final int getIndex() {
		return this.index;
	}

	@Getter
	@Setter
	private XmlField xmlField;

	private ValueHandler valueHandler;

	public final ValueHandler getValueHandler() {
		return this.valueHandler;
	}

	public final void setValueHandler(ValueHandler handle) {
		index++;
		this.valueHandler = handle;
	}
}
