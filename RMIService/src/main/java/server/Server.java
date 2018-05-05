package server;

import java.io.IOException;

public interface Server {
    Server setContainer(Container container);

    Server setPort(int port);

    boolean addService(Service service, String serviceName);

    void start() throws IOException;

    Service findService(String serviceName) throws ServiceNotFoundException;
}
