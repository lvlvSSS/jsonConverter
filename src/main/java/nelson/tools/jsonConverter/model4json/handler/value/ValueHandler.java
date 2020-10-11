package nelson.tools.jsonConverter.model4json.handler.value;

import nelson.tools.jsonConverter.model4json.handler.field.FieldHandler;

/**
 * 接口的实现类 处理model4json.xml中的mapType。
 * <p/>
 * 用法：实现类需要做单例，尽可能减少堆中对象的创建，实现类中不可有成员变量，避免线程安全问题
 * 
 * @author nelson
 *
 */
public interface ValueHandler {
	Object handle(FieldHandler<?> fieldHandle, Object source);
}
