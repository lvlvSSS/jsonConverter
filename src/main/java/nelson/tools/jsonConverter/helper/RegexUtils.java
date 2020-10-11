package nelson.tools.jsonConverter.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;

public class RegexUtils {

	public static boolean isMatch(String pattern, String target) {
		if (StringUtils.isBlank(pattern) || StringUtils.isBlank(target)) {
			return false;
		}
		boolean re = false;
		try {
			re = Pattern.matches(pattern, target);
		} catch (PatternSyntaxException e) {

		}
		return re;
	}

	/**
	 * 在字符串中找到字典（eg: “ab=cd”,ab为key,cd为value)中value值
	 * 
	 * @param pattern    要匹配的正则模式
	 * @param target     目标字符串
	 * @param key        对应的key的字符串
	 * @param keyIndex   key字符串的index
	 * @param valueIndex value字符串的index
	 * @return 返回value字符串
	 */
	public static String getValue(String pattern, String target, String key, int keyIndex, int valueIndex) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(target);
		while (m.find()) {
			if (StringUtils.equalsIgnoreCase(m.group(keyIndex), key)) {
				return m.group(valueIndex);
			}
		}
		return null;
	}

	public static String getValue(String pattern, String target, int index) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(target);
		while (m.find()) {
			return m.group(index);
		}
		return null;
	}
}