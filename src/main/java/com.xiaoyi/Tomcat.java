package com.xiaoyi;

import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tomcat {
    private int port = 8080;

    /**
     * Tomcat下管理的应用映射
     */
    private Map<String,Context> contextMap = new HashMap<String,Context>();

    public static void main(String[] args) {
        Tomcat server = new Tomcat();
        server.deployApps();
        System.out.println("server startup successfully");
        server.start();
    }

    /**
     * 部署应用
     */
    private void deployApps() {
        File webapps = new File(System.getProperty("user.dir"), "webapps");
        //System.out.println(webapps.getPath());//D:\codes\IdeaProject\MyTomcat\webapps
        for (String app : webapps.list()) {
            deployApp(webapps,app);
        }
    }

    /**
     * 应用内servlet
     */
    private void deployApp(File webapps,String appName){
        Context context = new Context(appName);

        File appDirectory = new File(webapps,appName);
        File classesDirectory = new File(appDirectory,"classes");
        //System.out.println(classesDirectory.getPath());//D:\codes\IdeaProject\MyTomcat\webapps\hello\classes
        List<File> files = getAllFilePath(classesDirectory);

        //反射加载类判断是否为HttpServlet的子类
        for (File classFile : files) {
            String name = classFile.getPath();
            name = name.replace(classesDirectory.getPath() + "\\","");
            name = name.replace(".class","");
            name = name.replace("\\",".");

            try{
                //loadClass
                URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{classesDirectory.toURL()});
                Class<?> sClass = urlClassLoader.loadClass(name);
                if (HttpServlet.class.isAssignableFrom(sClass)){
                   if (sClass.isAnnotationPresent(WebServlet.class)){
                       WebServlet annotation = sClass.getAnnotation(WebServlet.class);
                       String[] urlPatterns = annotation.urlPatterns();
                       for (String urlPattern : urlPatterns){
                           context.addUrlPatternMapping(urlPattern, (Servlet) sClass.newInstance());
                       }
                   }
                }
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        contextMap.put(appName,context);
    }

    public List<File> getAllFilePath(File srcFile){
        List<File> fileList = new ArrayList<File>();
        File[] files = srcFile.listFiles();

        if(files!=null){
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.addAll(getAllFilePath(file));
                } else {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    public Map<String, Context> getContextMap() {
        return contextMap;
    }

    public void start()
    {
        try{
            ExecutorService executorService = Executors.newFixedThreadPool(20);//线程池
            //create socket service
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                //获取连接对象
                Socket socket = serverSocket.accept();
                //线程池内处理逻辑，保证从消息的处理不阻塞
                executorService.execute(new SocketProcessor(socket,this));
            }
            //loop accept requests
        }catch (Exception e){

        }
    }
}
