package com.xiaoyi;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MyHttpResponse extends AbstractHttpServletResponse{
    private MyHttpRequest request;
    private OutputStream outputStream;

    private int statusCode = 200;
    private String message = "OK";
    private Map<String,String> headers = new HashMap<String,String>();//消息行键值对容器
    private ResponseServletOutputStream messageBody = new ResponseServletOutputStream();//消息体

    private final byte SP = ' ';
    private final byte CR = '\r';
    private final byte LF = '\n';

    public MyHttpResponse(MyHttpRequest request, OutputStream outputStream){
        this.request = request;
        this.outputStream = outputStream;
    }

//    public void sendRedirect(String uri) {
//        //判断资源是否存在
//        // 不存在返回404
//        // 存在返回目标资源
//        File file = new File(System.getProperty("user.dir") + "/WebContent" + uri);
//        if (file.exists()) {
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                byte[] bytes = new byte[(int) file.length()];
//                fileInputStream.read(bytes);
//                String result = new String(bytes);
////                System.out.println(result);
//                String response = getResponseMessage("200,", result);
//                this.outputStream.write(response.getBytes());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            try {
//                String error = getResponseMessage("404", "404 File Not Found!");
//                this.outputStream.write(error.getBytes());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

//    public String getResponseMessage(String code, String message){
//        return "HTTP/1.1 " + code + "\r\n" +
//                "Content-Type: text/html\r\n" +
//                "Content-Length: " + message.length() +
//                "\r\n" +
//                "\r\n" +
//                message;
//    }

    @Override
    public void setStatus(int i, String s) {
        statusCode = i;
        message = s;
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    @Override
    public void addHeader(String s, String s1) {
        headers.put(s, s1);
    }

    /**
     * servlet的doGet所write的的消息体
     */
    @Override
    public ResponseServletOutputStream getOutputStream() {
        return messageBody;
    }

    public void complete()  {
        try{
            sendResponseMsgLine();
            sendResponseMsgHeader();
            sendResponseMsgBody();
//              String response = "Hello World";
//              this.outputStream.write(response.getBytes());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void sendResponseMsgLine() throws IOException {
        outputStream.write(request.getProtocol().getBytes());
        outputStream.write(SP);
        outputStream.write(statusCode);
        outputStream.write(SP);
        outputStream.write(message.getBytes());
        outputStream.write(CR);
        outputStream.write(LF);
    }

    private void sendResponseMsgHeader() throws IOException {
        if (!headers.containsKey("Content-Length")) {
            addHeader("Content-Length", String.valueOf(getOutputStream().getPos()));
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            outputStream.write(key.getBytes());
            outputStream.write(":".getBytes());
            outputStream.write(SP);
            outputStream.write(value.getBytes());
            outputStream.write(CR);
            outputStream.write(LF);
        }
        outputStream.write(CR);
        outputStream.write(LF);
    }

    /**
     * 得到servlet的doGet所write的的消息体
     */
    private void sendResponseMsgBody()  {
        try {
            outputStream.write(getOutputStream().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
