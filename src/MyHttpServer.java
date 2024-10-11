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
