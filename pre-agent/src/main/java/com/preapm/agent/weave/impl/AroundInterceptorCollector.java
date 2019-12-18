package com.preapm.agent.weave.impl;

import java.util.Set;
import java.util.logging.Logger;

import com.preapm.agent.bean.PluginJarBean;
import com.preapm.agent.constant.BaseConstants;
import com.preapm.agent.util.ClassLoaderUtil;
import com.preapm.agent.util.LogManager;
import com.preapm.agent.util.PreApmConfigUtil;
import com.preapm.agent.weave.ClassReplacer;
import com.preapm.agent.weave.ClassWrapper;
import com.preapm.agent.weave.Collector;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

public class AroundInterceptorCollector extends Collector {
	private static Logger log = LogManager.getLogger(AroundInterceptorCollector.class);

	public static AroundInterceptorCollector INSTANCE = new AroundInterceptorCollector();

	public AroundInterceptorCollector() {
	}

	private static String beginSrc = BaseConstants.NULL;
	private static String endSrc = BaseConstants.NULL;
	private static String errorSrc = BaseConstants.NULL;

	@Override
	public byte[] transform(ClassLoader classLoader, String className, byte[] classfileBuffer, CtClass ctClass) {
		try {
			// 加载插件后，初始化插件
			com.preapm.agent.util.ClassLoaderUtil.loadJarByClassName(className);

			ClassReplacer replacer = new ClassReplacer(className, classLoader, ctClass);
			for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
				String longName = ctMethod.getLongName();
				if ((Modifier.isPublic(ctMethod.getModifiers())) && (!Modifier.isStatic(ctMethod.getModifiers())
						&& (!Modifier.isNative(ctMethod.getModifiers()))) && isTarget(className, longName)) {
					Set<PluginJarBean> plugins = PreApmConfigUtil.getPlugins(className);
					ClassWrapper classWrapper = new ClassWrapperAroundInterceptor(plugins);
					classWrapper.beginSrc(beginSrc);
					classWrapper.endSrc(endSrc);
					classWrapper.errorSrc(errorSrc);
					replacer.replace(classLoader, classfileBuffer, ctClass, ctMethod, classWrapper);
				}
			}
			return replacer.replace();
		} catch (Exception e) {
			log.severe(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e));
		}

		return new byte[0];
	}

}
