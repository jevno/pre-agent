package com.preapm.agent.bean;

import java.util.LinkedHashSet;
import java.util.Set;

public class AopExpressBean {

    private String target;
    private String set;
    private Set<String> includeMethods;
    private Set<String> excludeMethods;


    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public Set<String> getIncludeMethods() {
        return includeMethods;
    }

    public void setIncludeMethods(Set<String> includeMethods) {
        if(null == includeMethods || includeMethods.size() == 0){
            this.includeMethods = new LinkedHashSet<>();
        }else {
            this.includeMethods = includeMethods;
        }
    }

    public Set<String> getExcludeMethods() {
        return excludeMethods;
    }

    public void setExcludeMethods(Set<String> excludeMethods) {

        if(null == excludeMethods || excludeMethods.size() == 0){
            this.excludeMethods = new LinkedHashSet<>();
        }else {
            this.excludeMethods = excludeMethods;
        }
    }
}
