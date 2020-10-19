package nelson.tools.jsonConverter.model4json.handler.value;

import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;

/**
 * the subclass of this class need to handle the mapType in model4json.xml
 * <p/>
 * attention：the subclass should be singleton. In order to make the thread safe,
 * please don't declare the field of the subclass.
 *
 */
public interface ValueHandler {
	Object handle(FieldHandler<?> fieldHandle, Object source);
}
