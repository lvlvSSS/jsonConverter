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
	 * look up the value of the pattern.（eg: “ab=cd”,ab is <b>key</b>,then cd is the
	 * <b>value</b>)
	 * 
	 * @param pattern    the regex pattern.
	 * @param target     the target string.
	 * @param key        the key string that you want to extract.
	 * @param keyIndex   the index for key string.
	 * @param valueIndex the index for value string.
	 * @return
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