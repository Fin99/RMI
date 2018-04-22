import server.RMIServer;
import server.Server;

import java.io.IOException;

public class MyRMIServer {
    public static void main(String[] args) throws IOException {
        Server server = RMIServer.getServer(10001);
        server.addService(new RMIServiceCalculatorImpl(), "service/calculator");
        server.start();
    }
}
