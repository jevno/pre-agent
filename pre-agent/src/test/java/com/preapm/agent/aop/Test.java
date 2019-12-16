package com.preapm.agent.aop;

import com.preapm.agent.Bootstrap;
import com.preapm.agent.test.ClassLoaderTest;
import com.preapm.agent.util.PreApmConfigUtil;

import java.lang.reflect.Method;

public class Test {

    public static void main(String[] args) {
//        ProxyPointcut proxyPointcut = new ProxyPointcut();
////        proxyPointcut.setExpression("execution(public * com.preapm.agent.*.*(..))");
////        proxyPointcut.setExpression("execution(public * com.preapm.agent.test.ClassLoaderTest.initAndLoad())");
//        String name = ClassLoaderTest.class.getName();
//
//        Method[] methods = ClassLoaderTest.class.getMethods();
////        String name = Test.class.getName();
//        System.out.println(name);
//
//        boolean matches = proxyPointcut.matches(name);
//
//        System.out.println(matches);

//        boolean target = PreApmConfigUtil.isTarget(Bootstrap.class.getName(), "com.preapm.agent.Bootstrap.print(java.lang.String)");
        boolean target = PreApmConfigUtil.isTarget(Bootstrap.class.getName(), "com.preapm.agent.Bootstrap.print()");

        System.out.println(target);


    }


}
