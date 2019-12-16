package com.preapm.agent.aop;

import javassist.CtMethod;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ProxyPointcut {

    /**
     * 切点解析器
     */
    private PrePointcutParser pointcutParser;

    /**
     * (AspectJ)表达式
     */
    private String expression;

    /**
     * 表达式解析器
     */
    private PrePointcutExpressionImpl pointcutExpression;

    /**
     * AspectJ语法集合
     */
    private static final Set<PointcutPrimitive> DEFAULT_SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
    }

    public ProxyPointcut() {
        this(DEFAULT_SUPPORTED_PRIMITIVES);
    }

    public ProxyPointcut(Set<PointcutPrimitive> supportedPrimitives) {
        pointcutParser = PrePointcutParser
                .getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(supportedPrimitives);
    }

    /**
     * Class是否匹配切点表达式
     */
    public boolean matches(Class<?> targetClass) {
        checkReadyToMatch();
        return pointcutExpression.couldMatchJoinPointsInType(targetClass);
    }

    /**
     * ClassName是否匹配切点表达式
     */
    public boolean matches(String className) {
        checkReadyToMatch();
        return pointcutExpression.couldMatchJoinPointsInType(className);
    }

    /**
     * Method是否匹配切点表达式
     */
    public boolean matches(Method method) {
        checkReadyToMatch();
        ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
        if (shadowMatch.alwaysMatches()) {
            return true;
        } else if (shadowMatch.neverMatches()) {
            return false;
        }
        // TODO:还有个情况不判断了！详情见org.springframework.aop.aspectj.AspectJExpressionPointcut@matches()方法
        return false;
    }

    /**
     * 初始化切点解析器
     */
    private void checkReadyToMatch() {
        if (null == pointcutExpression) {
            pointcutExpression = pointcutParser.parsePointcutExpression(expression);
        }
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
