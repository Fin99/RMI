import server.RMIServer;
import server.Server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.rmi.RemoteException;

public class MyRMIServer {
    public static void main(String[] args) throws IOException {
        Server server = RMIServer.getServer(8899);
        server.addService(new RMIServiceCalculatorImpl(), "service/calculator");
        server.start();
    }
}
