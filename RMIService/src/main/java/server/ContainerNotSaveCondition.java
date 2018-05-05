package server;

import java.net.Socket;

public interface ContainerNotSaveCondition {
    boolean addTypeService(Class service, String name);

    boolean removeTypeService(String serviceName);

    Service addService(String name, Socket socket) throws InstantiationException, IllegalAccessException, ServiceNotFoundException;

    void removeService(Socket socket);

    Service getService(String name, Socket socket) throws ServiceNotFoundException;

    boolean contains(String serviceName);
}
