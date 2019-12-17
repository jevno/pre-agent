package com.preapm.agent.util;

import com.preapm.agent.weave.pattern.JdkRegexpMethodPointcut;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class PreApmConfigUtil {
	
//	private static Map<String, String> targetMap = new HashMap<>();

	private static JdkRegexpMethodPointcut jdkRegexp = new JdkRegexpMethodPointcut();

	static {
		try {
			initYmlConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initYmlConfig() throws Exception {
		YmlConfigUtil.loadYml("test.yml");
		String patternsStr = (String)YmlConfigUtil.getValue("target:list:patterns");
		String excludedPatternsStr = (String)YmlConfigUtil.getValue("target:list:excludedPatterns");

		if(!StringUtils.isEmpty(patternsStr)){
			String[] patterns = patternsStr.split(",");
			jdkRegexp.setPatterns(patterns);
			Pattern[] patternsArr = new Pattern[patterns.length];

			for (int i = 0; i < patterns.length; i++) {
				patternsArr[i] = Pattern.compile(patterns[0]);
			}

			jdkRegexp.setCompiledPatterns(patternsArr);
		}

		if(!StringUtils.isEmpty(excludedPatternsStr)){
			String[] excludedPatterns = excludedPatternsStr.split(",");
			jdkRegexp.setExcludedPatterns(excludedPatterns);
			Pattern[] excludedPatternsArr = new Pattern[excludedPatterns.length];

			for (int i = 0; i < excludedPatterns.length; i++) {
				excludedPatternsArr[i] = Pattern.compile(excludedPatterns[0]);
			}
			jdkRegexp.setCompiledExclusionPatterns(excludedPatternsArr);
		}

	}


	public static boolean isTarget(String className, String method) {
		boolean flag = isTarget(className);
		if(!flag) {
			return flag;
		}

		return flag;
	}

	public static boolean isTarget(String methodFullName) {
		return jdkRegexp.matchesPattern(methodFullName);
	}


	public static void main(String[] args) throws Exception {
		initYmlConfig();
		boolean b = jdkRegexp.matchesPattern("com.preapm.agent.Bootstrap.print");

		System.out.println(b);

	}
	
	

}
