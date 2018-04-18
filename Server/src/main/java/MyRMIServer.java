import server.RMIServer;
import server.Server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.rmi.RemoteException;

public class MyRMIServer {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Server server = RMIServer.getServer(10001);
        server.addService(new RMIServiceCalculatorImpl(), "service/calculator");
        server.start();
    }
}
