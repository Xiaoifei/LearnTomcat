package com.xiaoyi;

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;


/**
 * 用于存储每一个应用下有哪些映射
 */
public class Context {
    private String appName;
    private Map<String, Servlet> urlPatternMapping = new HashMap<String, Servlet>();

    public Context(String appName) {
        this.appName = appName;
    }

    public void addUrlPatternMapping(String pattern, Servlet servlet) {
        urlPatternMapping.put(pattern, servlet);
    }

    public Servlet getByUrlPatternMapping(String urlPattern) {
        for(String key : urlPatternMapping.keySet()) {
            if(key.contains(urlPattern)) {
                return urlPatternMapping.get(key);
            }
        }
        return null;
    }
}
