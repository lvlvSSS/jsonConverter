package nelson.tools.jsonConverter.model4json;

import nelson.tools.jsonConverter.model4json.handler.value.ValueHandler;

/**
 * 表示应该如何获取源数据，具体的处理实现为{@link ValueHandler} 的子类
 * 
 * @author nelson
 *
 */
public enum XmlFieldMapType {
	// 供其他的field使用，如JS_FUNCTION的入参。但不会作为结果输出
	REF,
	// 正常使用
	NORMAL,
	// 表示为JS脚本
	JS_FUNCTION,
	// 需要做转换，converter在此使用
	MAPPING;
}
