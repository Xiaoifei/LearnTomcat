package com.xiaoyi;

import java.io.IOException;
import java.io.InputStream;

public class MyHttpRequest extends AbstractHttpServletRequest {
    private InputStream inputStream;
    private String method;
    private String url;
    private String protocl;

    public MyHttpRequest(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * 解析流
     */
    public void parse(){
        try {
            byte[] bytes = new byte[1024];
            inputStream.read(bytes);
            String request = new String(bytes);
            parseMessageLine(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 请求消息行
     */
    public void parseMessageLine(String request) {
        System.out.println("---");
        System.out.println(request);
        System.out.println("---");

        if (!request.isEmpty()) {
            int index1,index2,index3;
            index1 = request.indexOf(" ");//找空格下标
            method = request.substring(0, index1);;//输出方法
            index2 = request.indexOf(" ",index1+1);//找空格下标2
            url = request.substring(index1+1,index2);//输出资源位置
            index3 = request.indexOf("\r\n",index2+1);//找下标3
            protocl = request.substring(index2+1,index3);;//输出协议

            System.out.println(method + " " + url + " " + protocl);
        }else {
            System.out.println("空");
        }

    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(url);
    }

    @Override
    public String getProtocol() {
        return protocl;
    }
}
