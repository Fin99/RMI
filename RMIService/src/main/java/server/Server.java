package server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;

//process request client. Save within oneself one container. Protocol UDP.
/*
    1. request from client
    2. find this service in container
    3. response found service client
 */
public interface Server {
    Server setContainer(Container container);

    Server setPort(int port);

    boolean addService(Service service, String serviceName);

    void start() throws IOException;

    Service findService(String serviceName) throws ServiceNotFoundException;
}
