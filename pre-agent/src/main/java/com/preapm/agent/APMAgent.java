package com.preapm.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.preapm.agent.util.LogManager;
import com.preapm.agent.weave.Collector;
import com.preapm.agent.weave.impl.AroundInterceptorCollector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

public class APMAgent implements ClassFileTransformer {
	
	
	private static Logger log = LogManager.getLogger(APMAgent.class);
	
	private static Collector collector = new AroundInterceptorCollector();

	private Map<ClassLoader, ClassPool> classPoolMap = new ConcurrentHashMap<>();

	@Override
	public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if ((className == null) || (classLoader == null)
				|| (classLoader.getClass().getName().equals("sun.reflect.DelegatingClassLoader"))
				|| (classLoader.getClass().getName().equals("org.apache.catalina.loader.StandardClassLoader"))
				|| (classLoader.getClass().getName().equals("javax.management.remote.rmi.NoCallStackClassLoader"))
				|| (classLoader.getClass().getName().equals("com.alibaba.fastjson.util.ASMClassLoader"))
				|| (className.indexOf("$Proxy") != -1) || (className.startsWith("java"))) {
			return null;
		}

		className = className.replaceAll("/", ".");

		System.out.println("=============" + className);
		if (!collector.isTarget(className)) {
			return null;
		}

		// 不同的ClassLoader使用不同的ClassPool
		ClassPool localClassPool;
		if (!this.classPoolMap.containsKey(classLoader)) {
			localClassPool = new ClassPool();
			localClassPool.insertClassPath(new LoaderClassPath(classLoader));
			this.classPoolMap.put(classLoader, localClassPool);
		} else {
			localClassPool = this.classPoolMap.get(classLoader);
		}

		try {
			CtClass localCtClass = localClassPool.get(className);
			byte[] arrayOfByte = collector.transform(classLoader, className, classfileBuffer, localCtClass);
			log.info(String.format("%s APM agent insert success", new Object[] { className }));
			return arrayOfByte;
		} catch (Throwable localThrowable) {
			log.severe(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(localThrowable));
			new Exception(String.format("%s APM agent insert fail", new Object[] { className }), localThrowable)
					.printStackTrace();
		}


		return null;
	}
}