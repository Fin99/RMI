package server;

public class RMIServer {
    public static Server getServer(int port) {
        return ServerImplDefault.build().setPort(port);
    }
}
