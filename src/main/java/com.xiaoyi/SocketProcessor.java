package com.xiaoyi;

import javax.servlet.Servlet;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketProcessor implements Runnable {
    private Socket socket;
    private Tomcat tomcat;
    public SocketProcessor(Socket socket,Tomcat tomcat) {
        this.socket = socket;
        this.tomcat = tomcat;
    }

    @Override
    public void run() {
        processSocket(socket);
    }

    /**
     * 处理Socket连接
     */
    private void processSocket(Socket socket){
        try{
            //获取链接对象输入流
            InputStream inputStream = socket.getInputStream();
            //创建request
            MyHttpRequest request = new MyHttpRequest(inputStream);
            //解析请求
            request.parse();

            //创建响应
            OutputStream outputStream = socket.getOutputStream();
            MyHttpResponse response = new MyHttpResponse(request,outputStream);

            //实例化servlet对象，使用自带的Servlet类进行处理
            //匹配servlet，doGet
            String requestUrl = request.getRequestURL().toString();

            //System.out.println(requestUrl);
            requestUrl = requestUrl.substring(1);
            String[] parts = requestUrl.split("/");
            String appName = parts[0];
            Context context = tomcat.getContextMap().get(appName);

            //由于请求了/favicon.ico，会将其解析为应用，所以会导致下标越界
            if(parts.length > 1){
                Servlet servlet = context.getByUrlPatternMapping(parts[1]);
                if(servlet != null){
                    servlet.service(request, response);
                    //进行响应
                    response.complete();
                }else {//找不到Servlet的情况（返回404）
                    DefaultServlet defaultServlet = new DefaultServlet();
                    defaultServlet.service(request, response);
                    response.complete();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
