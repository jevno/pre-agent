package com.preapm.agent.util;

import com.preapm.agent.bean.PluginConfigBean;
import com.preapm.agent.constant.YMLConstants;
import com.preapm.agent.weave.pattern.JdkRegexpMethodPointcut;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


public class PreApmConfigUtil {

	private static Map<String,PluginConfigBean> pluginMap = new HashMap<>();

	static {
//		try {
//			initYmlConfig();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	private static void initYmlConfig(){
		YmlConfigUtil.loadYml("test2.yml");
		LinkedHashMap<String,Object> plugins = (LinkedHashMap)YmlConfigUtil.getValue("plugins");

		Set<String> pluginNames = plugins.keySet();
		pluginNames.forEach(pluginName ->{

			LinkedHashMap<String,Object> appNames = (LinkedHashMap)plugins.get(pluginName);

			PluginConfigBean plugin = new PluginConfigBean();
			plugin.setPluginName(pluginName);
			ArrayList<PluginConfigBean.ApplicationBean> applicationBeans = new ArrayList<>();

			appNames.keySet().forEach(appName -> {
				if(appName.equals(YMLConstants.LOAD_PATTERNS)){
					List<String> value = (ArrayList)YmlConfigUtil.getValue("plugins:" + pluginName + ":" + appName);
					plugin.setLoadPatterns(value);
				}else{

					LinkedHashMap<String, List<String>> value = (LinkedHashMap)YmlConfigUtil.getValue("plugins:" + pluginName + ":" + appName);

					PluginConfigBean.ApplicationBean applicationBean = new PluginConfigBean.ApplicationBean();
					applicationBean.setApplicationName(appName);
					List<String> patterns = value.get(YMLConstants.PATTERNS);
					List<String> excludedPatterns = value.get(YMLConstants.EXCLUDED_PATTERNS);

					applicationBean.setPatterns(patterns);
					applicationBean.setExcludedPatterns(excludedPatterns);
					applicationBeans.add(applicationBean);
				}
			});
			plugin.setApplicationBeans(applicationBeans);

			pluginMap.put(pluginName,plugin);

		});


//
//		pluginMap.values().forEach(pluginConfigList -> {
//			pluginConfigList.forEach(pluginConfigBean -> {
//				JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
//
//
//				List<String> patterns = pluginConfigBean.getPatternMap().get(YMLConstants.PATTERNS);
//				List<String> excludedPatterns = pluginConfigBean.getPatternMap().get(YMLConstants.EXCLUDED_PATTERNS);
//
//				if(null != patterns && patterns.size() > 0){
//					List<String> collect = patterns.stream().filter(pattern -> StringUtils.isEmpty(pattern)).collect(Collectors.toList());
//					String[] patternsArr = collect.toArray(new String[collect.size()]);
//					pointcut.setPatterns(patternsArr);
//					pointcut.setCompiledPatterns(pointcut.compilePatterns(patternsArr));
//				}
//				if(null != excludedPatterns && excludedPatterns.size() > 0){
//					List<String> collect = excludedPatterns.stream().filter(excludedPattern -> StringUtils.isEmpty(excludedPattern)).collect(Collectors.toList());
//					String[] excludedPatternArr = collect.toArray(new String[collect.size()]);
//					pointcut.setExcludedPatterns(excludedPatternArr);
//					pointcut.setCompiledExclusionPatterns(pointcut.compilePatterns(excludedPatternArr));
//				}
//				pluginConfigBean.setRegexpMethod(pointcut);
//			});
//		});


	}


	public static boolean isTarget(String className, String method) {
		boolean flag = isTarget(className);
		if(!flag) {
			return flag;
		}

		return flag;
	}

	public static boolean isTarget(String className) {

//		pluginMap.forEach((key,pluginConfigBeans)->{
//			pluginConfigBeans.forEach(pluginConfig ->{
//				List<String> list = pluginConfig.getPatternMap().get(YMLConstants.LOAD_PATTERNS);
//				List<String> loadPatterns = list.stream().filter(pattern -> StringUtils.isEmpty(pattern)).collect(Collectors.toList());
//
//				return;
//			});
//		});

		return true;
	}

	public static boolean isTargetJar(String jarName) {
		return pluginMap.containsKey(jarName);
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

		System.out.println(pluginMap);

		System.out.println("aa");
	}



}
