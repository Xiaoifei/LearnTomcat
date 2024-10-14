![](IMG/Pasted%20image%2020241011225855.png)
![](IMG/Pasted%20image%2020241011225913.png)
![](IMG/Pasted%20image%2020241013113954.png)
### 基础
- MyHttpRequest
    - `parse()`（解析流）
    - `parseUri()`（请求的内容存储到uri）
    - `getUri()`（返回uri）
- MyHttpResponse
    - `sendRedirect(String uri)`（判断资源是否存在，不存在返回404  ，存在返回目标资源）
    - `getResponseMessage(String code, String message)`（构造响应消息）
- MyHttpServer
    - `start()`（循环接收浏览器请求，并且处理请求）
- Test`
    - `main()`（主方法测试类）

###### MyHttpRequest
```C#
import java.io.IOException;
import java.io.InputStream;

public class MyHttpRequest {
    private InputStream inputStream;
    private String uri;

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
            parseUri(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 请求判断
     */
    public void parseUri(String request) {
        int index1,index2;
        index1 = request.indexOf(" ");//找空格下标
        index2 = request.indexOf(" ",index1+1);//找空格下标2
        uri = request.substring(index1+1,index2);//输出资源位置
        System.out.println(uri);
    }

    public String getUri() {
        return this.uri;
    }
}
```

###### MyHttpResponse
```C#
import java.io.*;

public class MyHttpResponse {
    private int statusCode;
    private String statusMessage;

    private OutputStream outputStream;

    public MyHttpResponse(OutputStream outputStream){
        this.outputStream = outputStream;
    }

    public void sendRedirect(String uri){
        //判断资源是否存在
        // 不存在返回404
        // 存在返回目标资源
        File file = new File(System.getProperty("user.dir")+"/WebContent" + uri);
        if (file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[(int)file.length()];
                fileInputStream.read(bytes);
                String result = new String(bytes);
//                System.out.println(result);
                String response = getResponseMessage("200,",result);
                this.outputStream.write(response.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }else {
            try {
                String error = getResponseMessage("404","404 File Not Found!");
                this.outputStream.write(error.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getResponseMessage(String code, String message){
        return "HTTP/1.1 " + code + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + message.length() +
                "\r\n" +
                "\r\n" +
                message;

    }
}
```

###### MyHttpServer
```C#
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyHttpServer {
    private int port = 8080;

    public void receiving()
    {
        try{
            //create socket service
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                //获取连接对象
                Socket socket = serverSocket.accept();
                //获取链接对象输入流
                InputStream inputStream = socket.getInputStream();
                //创建request
                MyHttpRequest request = new MyHttpRequest(inputStream);
                //解析请求
                request.parse();
                //创建响应
                OutputStream outputStream = socket.getOutputStream();
                MyHttpResponse response = new MyHttpResponse(outputStream);
                //进行响应
                response.sendRedirect(request.getUri());

            }
            //loop accept requests
        }catch (Exception e){

        }

    }
}
```

###### Test
```C#
public class Test {
    public static void main(String[] args) {
        System.out.println("server startup successfully");
        MyHttpServer server = new MyHttpServer();
        server.receiving();
    }
}
```

### 进阶
![](IMG/Pasted%20image%2020241012213802.png)
![](IMG/Pasted%20image%2020241012213832.png)

Tomcat的两个重要身份：
1. http服务器
2. Tomcat是一个Servlet容器

使用Servlet规范来实现Tomcat
- AbstractHttpServletRequest *(implements HttpServletRequest)*
- AbstractHttpServletResponse *(implements HttpServletResponse)*
- ResponseServletOutputStream *(extends ServletOutputStream)*
    - `write(int b)`
    - `getBytes()`
    - `getPos()`
- MyHttpRequest *(extends AbstractHttpServletRequest)*
    - `parse()`（解析流）
    - `parseMessageLine()`（请求行解析）
    - `getMethod()`（返回请求方式）
    - `getRequestURL()`（返回请求地址，通常为**appName/Servlet** ）
    - `getProtocol()`（返回协议）
- MyHttpResponse *(extends AbstractHttpServletResponse)*
    - `public MyHttpResponse(MyHttpRequest request, OutputStream outputStream)`（构造方法）
    - `setStatus(int i, String s)`
    - `getStatus()`
    - `addHeader(String s, String s1)`
    - `ResponseServletOutputStream getOutputStream()`（servlet的doGet所write的消息体）
    - `complete()`（完成响应，调用下面三个方法）
    - `sendResponseMsgLine()`
    - `sendResponseMsgHeader()`
    - `sendResponseMsgBody()`
- Tomcat (添加线程池)
    - `main(String[] args)`（启动Tomcat）
    - `deployApps()`（利用反射部署指定目录下的应用）
    - `start()`（循环接收浏览器请求，并且处理请求）
    - `getAllFilePath()`（递归的获得项目内所有文件）
    - `getContextMap()`（添加应用映射）
- SocketProcessor *(implements Runnable)*
    - `run()`（调用processSocket）
    - `processSocket(Socket socket)`（处理请求）
- Context
    - `addUrlPatternMapping(String pattern, Servlet servlet)`（添加应用下Servlet映射）
    - `getByUrlPatternMapping(String urlPattern)` （得到Servlet对象）
- DefaultServlet *(extends HttpServlet)*