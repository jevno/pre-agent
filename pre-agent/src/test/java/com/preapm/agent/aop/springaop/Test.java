package com.preapm.agent.aop.springaop;


import com.preapm.agent.Bootstrap;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) {
        JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
        String[] strings = new String[1];

        strings[0] = "public java.lang.String com.preapm.agent.Bootstrap.print\\(java.lang.String\\)";


        pointcut.setPatterns(strings);

        Pattern compile = Pattern.compile(strings[0]);

        Pattern[] p = new Pattern[1];
        p[0] = compile;
        pointcut.setCompiledPatterns(p);

        Method method = Bootstrap.class.getMethods()[1];
        System.out.println(method.toString());


//        System.out.println(pointcut.matchesPattern("com.preapm.agent.Bootstrap.print"));
        System.out.println(pointcut.matchesPattern(method.toString()));

    }
}
