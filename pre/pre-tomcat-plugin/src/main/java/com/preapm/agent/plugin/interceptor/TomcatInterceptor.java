package com.preapm.agent.plugin.interceptor;

import java.math.BigInteger;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.preapm.agent.common.bean.MethodInfo;
import com.preapm.agent.common.interceptor.AroundInterceptor;
import com.preapm.sdk.zipkin.ZipkinClientContext;
import com.preapm.sdk.zipkin.util.InetAddressUtils;
import com.preapm.sdk.zipkin.util.TraceKeys;

import zipkin.Endpoint;

/**
 * org.apache.catalina.connector.Request.Request()
 * org.apache.catalina.connector.Request.getRequest()
 * 
 * org.apache.catalina.core.ApplicationFilterChain.doFilter(ServletRequest,
 * ServletResponse)
 * 
 * @author Zengmin.Zhang
 *
 */
public class TomcatInterceptor implements AroundInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(TomcatInterceptor.class);

	@Override
	public void before(MethodInfo methodInfo) {
		int ipv4 = InetAddressUtils.localIpv4();
		Endpoint endpoint = Endpoint.builder().serviceName(ZipkinClientContext.serverName).ipv4(ipv4).build();
		methodInfo.setLocalVariable(new Object[] {endpoint});
		try {
			HttpServletRequest request = (HttpServletRequest) methodInfo.getArgs()[0];
			//System.out.println("request================="+request);
			String url = request.getRequestURL().toString();
			String trace_id = request.getHeader(com.preapm.sdk.zipkin.util.TraceKeys.TRACE_ID);
			String span_id = request.getHeader(com.preapm.sdk.zipkin.util.TraceKeys.SPAN_ID);
			logger.info("获取trace_id：" + trace_id);
			logger.info("获取trace_id：" + span_id);
			if (trace_id != null && span_id != null) {
				BigInteger trace_id_bi = new BigInteger(trace_id, 16);
				BigInteger span_id_bi = new BigInteger(span_id, 16);
				ZipkinClientContext.getClient().startSpan(trace_id_bi.longValue(), span_id_bi.longValue(), url);
			}else {
				ZipkinClientContext.getClient().startSpan(url);
			}
			ZipkinClientContext.getClient().sendBinaryAnnotation(com.preapm.sdk.zipkin.util.TraceKeys.PRE_NAME,Arrays.toString(methodInfo.getPlugins()), endpoint);
			ZipkinClientContext.getClient().sendAnnotation(TraceKeys.SERVER_SEND, endpoint);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void exception(MethodInfo methodInfo) {
	}

	@Override
	public void after(MethodInfo methodInfo) {
		Endpoint endpoint = (Endpoint) methodInfo.getLocalVariable()[0];
		ZipkinClientContext.getClient().sendAnnotation(TraceKeys.SERVER_RECV, endpoint );
		ZipkinClientContext.getClient().finishSpan();

	}

	@Override
	public String name() {
		return this.getClass().getName();
	}

}
