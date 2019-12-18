package com.preapm.agent.util;

import com.preapm.agent.bean.PluginConfigBean;
import com.preapm.agent.constant.YMLConstants;
import com.preapm.agent.weave.pattern.JdkRegexpMethodPointcut;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


public class PreApmConfigUtil {

//	private static Map<String,PluginConfigBean> pluginMap = new HashMap<>();
	private static PluginConfigBean pluginConfigBean = new PluginConfigBean();

	static {
//		try {
//			initYmlConfig();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	private static void initYmlConfig(){
		YmlConfigUtil.loadYml("test2.yml");
		LinkedHashMap<String,Object> plugins = (LinkedHashMap)YmlConfigUtil.getValue("plugins:config");
		List<String> jars = (List<String>) plugins.get("jars");
		List<String> jarList = jars.stream().filter(jar -> !StringUtils.isEmpty(jar)).collect(Collectors.toList());
		
		pluginConfigBean.setJars(jarList);

		List<String> loadPatterns = (List<String>)plugins.get(YMLConstants.LOAD_PATTERNS);
		List<String> loadPatternsList = loadPatterns.stream().filter(loadPattern -> !StringUtils.isEmpty(loadPattern)).collect(Collectors.toList());

		pluginConfigBean.setLoadPatterns(loadPatternsList);

		plugins.remove("jars");
		plugins.remove(YMLConstants.LOAD_PATTERNS);

		ArrayList<String> patternList = new ArrayList<>();
		ArrayList<String> excludedPatternList = new ArrayList<>();
		JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
		Set<String> pluginNames = plugins.keySet();
		ArrayList<PluginConfigBean.ApplicationBean> applicationBeans = new ArrayList<>();
		pluginNames.forEach(appName ->{
			LinkedHashMap<String, List<String>> value = (LinkedHashMap)YmlConfigUtil.getValue("plugins:config" + ":" + appName);

			PluginConfigBean.ApplicationBean applicationBean = new PluginConfigBean.ApplicationBean();
			applicationBean.setApplicationName(appName);
			List<String> patterns = value.get(YMLConstants.PATTERNS);
			List<String> excludedPatterns = value.get(YMLConstants.EXCLUDED_PATTERNS);
			applicationBean.setPatterns(patterns);
			applicationBean.setExcludedPatterns(excludedPatterns);
			applicationBeans.add(applicationBean);

			if(null != patterns && patterns.size() > 0){
				List<String> collect = patterns.stream().filter(pattern -> !StringUtils.isEmpty(pattern)).collect(Collectors.toList());
				patternList.addAll(collect);

			}
			if(null != excludedPatterns && excludedPatterns.size() > 0){
				List<String> collect = excludedPatterns.stream().filter(excludedPattern -> !StringUtils.isEmpty(excludedPattern)).collect(Collectors.toList());
				excludedPatternList.addAll(collect);
			}

		});
		pluginConfigBean.setApplicationBeans(applicationBeans);

		String[] patternsArr = patternList.toArray(new String[patternList.size()]);
		pointcut.setPatterns(patternsArr);
		pointcut.setCompiledPatterns(pointcut.compilePatterns(patternsArr));

		String[] excludedPatternArr = excludedPatternList.toArray(new String[excludedPatternList.size()]);
		pointcut.setExcludedPatterns(excludedPatternArr);
		pointcut.setCompiledExclusionPatterns(pointcut.compilePatterns(excludedPatternArr));

		pluginConfigBean.setPointcut(pointcut);
	}


	public static boolean isTarget(String className, String method) {
		String fullName = className + "." + method;

		return pluginConfigBean.getPointcut().matchesPattern(fullName);
	}

	public static boolean isTarget(String className) {
		return pluginConfigBean.getLoadPatterns().contains(className);
	}

	public static boolean isTargetJar(String jarName) {
		return pluginConfigBean.getJars().contains(jarName);
	}


	public static void main(String[] args) throws Exception {
//		YmlConfigUtil.loadYml("test2.yml");
//		LinkedHashMap<String,Object> plugins = (LinkedHashMap)YmlConfigUtil.getValue("plugins");
//
//		Set<String> pluginNames = plugins.keySet();
//		pluginNames.forEach(pluginName ->{
//			LinkedHashMap<String,Object> appNames = (LinkedHashMap)plugins.get(pluginName);
//			appNames.keySet().forEach(appName -> {
//				LinkedHashMap<String, List<String>> value = (LinkedHashMap)YmlConfigUtil.getValue("plugins:" + pluginName + ":" + appName);
//				PluginConfigBean plugin = new PluginConfigBean();
//				plugin.setPluginName(pluginName);
//				plugin.setApplicationName(appName);
//				plugin.setPatternMap(value);
//			});
//		});

		initYmlConfig();

		boolean print = isTarget("com.preapm.agent.Bootstrap", "print");

		boolean target = isTarget("com.preapm.agent.Bootstrap");
		System.out.println(target);

		System.out.println(print);
	}



}
