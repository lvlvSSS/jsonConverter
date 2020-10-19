package nelson.tools.jsonConverter.helper;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Js engine for executing js script.
 */
public enum JsExecutor {
	INSTANCE;
	private static Log LOG = LogFactory.getLog(JsExecutor.class);

	private JsExecutor() {
		ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName("javascript");
	}

	private ScriptEngine engine;

	public Object execute(String jsFunctionBody, String jsFunctionName, Object... args) {
		if (StringUtils.isBlank(jsFunctionName) || StringUtils.isBlank(jsFunctionBody))
			return null;

		try {
			engine.eval(jsFunctionBody);
			if (engine instanceof Invocable) {
				return ((Invocable) engine).invokeFunction(jsFunctionName, args);
			}
		} catch (Exception ex) {
			LOG.error(String.format("[JsExecutor.execute] errors: function[%s] in js[%s] can't be called:",
					jsFunctionName, jsFunctionBody), ex);
		}
		return null;
	}
}