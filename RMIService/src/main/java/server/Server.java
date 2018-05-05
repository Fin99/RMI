package server;

import java.io.IOException;
import java.net.Socket;

public interface Server {
    Server setContainer(ContainerSaveCondition container);
    Server setContainer(ContainerNotSaveCondition container);

    Server setPort(int port);

    boolean addService(Service service, String serviceName);

    boolean addService(Class service, String serviceName);

    void start() throws IOException;

    Service findService(String serviceName) throws ServiceNotFoundException;

    Service findService(String serviceName, Socket client) throws ServiceNotFoundException;
}
