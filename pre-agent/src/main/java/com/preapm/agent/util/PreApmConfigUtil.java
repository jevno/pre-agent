package com.preapm.agent.util;

import com.preapm.agent.bean.AopExpressBean;
import com.preapm.agent.weave.parser.ProxyPointcut;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class PreApmConfigUtil {
	
	private static Map<String, ProxyPointcut> targetMap = new HashMap<>();

	static {
		try {
			initYmlConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initYmlConfig() throws Exception {
		YmlConfigUtil.loadYml("test.yml");
		LinkedHashMap<String,String> value = (LinkedHashMap) YmlConfigUtil.getValue("target:set");
		Set<String> keys = value.keySet();
		for (String key: keys) {

			AopExpressBean aopExpressBean = new AopExpressBean();
			String includeStr = "target:set" + ":" + key + ":includeMethods";
			String includeMethodsStr = (String) YmlConfigUtil.getValue(includeStr);
			Set<String> includeMethodSet = new LinkedHashSet<>();
			if (!StringUtils.isEmpty(includeMethodsStr)){
				String[] includeMethodArr = includeMethodsStr.split("--");
				for (int i = 0; i < includeMethodArr.length; i++) {
					includeMethodSet.add(includeMethodArr[i]);
				}
			}

			String excludeStr = "target:set" + ":" + key + ":excludeMethods";
			String excludeMethodsStr = (String) YmlConfigUtil.getValue(excludeStr);
			Set<String> excludeMethodSet = new LinkedHashSet<>();
			if (!StringUtils.isEmpty(excludeMethodsStr)){
				String[] excludeMethodsArr = excludeMethodsStr.split("--");
				for (int i = 0; i < excludeMethodsArr.length; i++) {
					excludeMethodSet.add(excludeMethodsArr[i]);
				}
			}

			aopExpressBean.setIncludeMethods(includeMethodSet);
			aopExpressBean.setExcludeMethods(excludeMethodSet);

			Set<String> set = new HashSet<String>();
			set.addAll(includeMethodSet);
			set.retainAll(excludeMethodSet);
			if(set.size() > 0){
				throw new Exception("same method in includeMethodSet and excludeMethodSet");
			}

			ProxyPointcut proxyPointcut = new ProxyPointcut();
			proxyPointcut.setExpression(key);
			proxyPointcut.setAopExpressBean(aopExpressBean);

			targetMap.put(key,proxyPointcut);
		}


	}


	public static boolean isTarget(String className, String method) {
		boolean flag = isTarget(className);
		if(!flag) {
			return flag;
		}

		Collection<ProxyPointcut> values = targetMap.values();
		for (ProxyPointcut value : values) {
			AopExpressBean aopExpressBean = value.getAopExpressBean();
			Set<String> includeMethods = aopExpressBean.getIncludeMethods();

			Set<String> excludeMethods = aopExpressBean.getExcludeMethods();

			//拦截所有方法
			if(includeMethods.size() == 0 && excludeMethods.size() == 0){
				return true;
			}

			if(includeMethods.contains(method)){
				return true;
			}
			if(excludeMethods.contains(method)){
				return false;
			}
		}

		return flag;
	}

	public static boolean isTarget(String className) {
		Collection<ProxyPointcut> values = targetMap.values();
		for (ProxyPointcut value : values) {
			if(value.matches(className)){
				return true;
			}else {
				return false;
			}
		}
		return false;
	}
	
	
	

}
