package com.rnt.SecuritySystem.util;

import java.util.Set;
import java.util.regex.Pattern;

public class CheckMaliciousPalyload{

	private static final Set<String> SQL_BLACKLIST = Set.of("--", "/*", "*/", "xp_", "exec ", "sp_", "sysobjects",
			"union select", "information_schema", "waitfor delay", "drop table", "truncate table");

	private static final Set<String> XSS_BLACKLIST = Set.of("<script>", "</script>", "javascript:", "onload=",
			"onerror=", "onclick=", "document.cookie", "eval(", "alert(");

	private static final Set<String> PATH_BLACKLIST = Set.of("../", "~/", "/etc/passwd", "cmd.exe", "bash -c", "|", "&",
			"$(");

	private static final Pattern SQL_REGEX = Pattern.compile(
			"(?i)(\\b(select|insert|update|delete|drop|alter|create|rename|truncate|backup)\\b|\\b(and|or)\\b.+\\b(like|=|>|<)\\b)");

	
	public static boolean checkValue(String value) {
		if (value == null)
			return false;
		String lowerValue = value.toLowerCase();
		return SQL_BLACKLIST.stream().anyMatch(lowerValue::contains) || SQL_REGEX.matcher(lowerValue).find()
				|| XSS_BLACKLIST.stream().anyMatch(lowerValue::contains)
				|| PATH_BLACKLIST.stream().anyMatch(lowerValue::contains);
	}
	
	
 
}
