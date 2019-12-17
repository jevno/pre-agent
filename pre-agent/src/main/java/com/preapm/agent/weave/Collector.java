package com.preapm.agent.weave;

import com.preapm.agent.util.PreApmConfigUtil;

import javassist.CtClass;

public abstract class Collector {

	public boolean isTarget(String className, String method) {
		boolean target = PreApmConfigUtil.isTarget(className, method);

		return target;
	}

	public boolean isTarget(String methodFullName) {
		boolean target = PreApmConfigUtil.isTarget(methodFullName);
		return target;
	}


	public abstract byte[] transform(ClassLoader classLoader, String className, byte[] classfileBuffer,
			CtClass ctClass);

}