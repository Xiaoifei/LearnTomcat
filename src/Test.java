public class Test {
    public static void main(String[] args) {
        System.out.println("server startup successfully");
        MyHttpServer server = new MyHttpServer();
        server.receiving();
    }
}
