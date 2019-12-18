package com.preapm.agent.bean;

import com.preapm.agent.weave.pattern.JdkRegexpMethodPointcut;

import java.util.ArrayList;
import java.util.List;

public class PluginConfigBean {

    private String pluginName;

    private List<String> jars;

    private List<String> loadPatterns;

    private ArrayList<ApplicationBean> applicationBeans;

    private JdkRegexpMethodPointcut pointcut;

    public ArrayList<ApplicationBean> getApplicationBeans() {
        return applicationBeans;
    }

    public void setApplicationBeans(ArrayList<ApplicationBean> applicationBeans) {
        this.applicationBeans = applicationBeans;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public List<String> getLoadPatterns() {
        return loadPatterns;
    }

    public void setLoadPatterns(List<String> loadPatterns) {
        this.loadPatterns = loadPatterns;
    }


    public List<String> getJars() {
        return jars;
    }

    public void setJars(List<String> jars) {
        this.jars = jars;
    }

    public JdkRegexpMethodPointcut getPointcut() {
        return pointcut;
    }

    public void setPointcut(JdkRegexpMethodPointcut pointcut) {
        this.pointcut = pointcut;
    }

    public static class ApplicationBean{

        private String applicationName;

        private List<String> patterns;
        private List<String> excludedPatterns;

        public String getApplicationName() {
            return applicationName;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public List<String> getPatterns() {
            return patterns;
        }

        public void setPatterns(List<String> patterns) {
            this.patterns = patterns;
        }

        public List<String> getExcludedPatterns() {
            return excludedPatterns;
        }

        public void setExcludedPatterns(List<String> excludedPatterns) {
            this.excludedPatterns = excludedPatterns;
        }
    }
}
