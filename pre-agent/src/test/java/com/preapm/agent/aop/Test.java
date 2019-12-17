package com.preapm.agent.aop;

import com.preapm.agent.test.ClassLoaderTest;


public class Test {

    public static void main(String[] args) {
        ProxyPointcut proxyPointcut = new ProxyPointcut();
        proxyPointcut.setExpression("execution(public * com.preapm.agent.aop.*.*(..))");

        String name = ClassLoaderTest.class.getName();

        boolean matches = proxyPointcut.matches(ClassLoaderTest.class);
        System.out.println(name);
        System.out.println(proxyPointcut.getExpression());
        System.out.println(matches);


    }


}
