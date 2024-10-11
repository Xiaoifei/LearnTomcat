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
