package com.xiaoyi;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class ResponseServletOutputStream extends ServletOutputStream {
    //存储消息体（因为doGet执行完毕之后才会写入到消息体最终被Socket发送到客户端）
    private byte[] messageBody = new byte[1024];
    private int pos = 0;

    @Override
    public void write(int b) throws IOException {
        messageBody[pos] = (byte)b;
        pos++;
    }

    public byte[] getBytes(){
        return messageBody;
    }

    public int getPos() {
        return pos;
    }
}
