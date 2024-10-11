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
