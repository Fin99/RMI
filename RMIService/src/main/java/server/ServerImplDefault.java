package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerImplDefault implements Server {
    private ContainerSaveCondition containerSaveCondition;
    private ContainerNotSaveCondition containerNotSaveCondition;
    private int port;

    @Override
    public Server setContainer(ContainerSaveCondition container) {
        containerSaveCondition = container;
        return this;
    }

    @Override
    public Server setContainer(ContainerNotSaveCondition container) {
        containerNotSaveCondition = container;
        return this;
    }

    @Override
    public Server setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public boolean addService(Service service, String serviceName) {
        if(containerNotSaveCondition.contains(serviceName)) return false;
        return containerSaveCondition.addService(service, serviceName);

    }

    @Override
    public boolean addService(Class service, String serviceName) {
        if(containerSaveCondition.contains(serviceName)) return false;
        return containerNotSaveCondition.addTypeService(service, serviceName);
    }

    @Override
    @Deprecated
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            new ClientThreadImpl(containerNotSaveCondition, containerSaveCondition, serverSocket.accept()).start();
        }
    }

    @Override
    public Service findService(String serviceName) throws ServiceNotFoundException {
        return containerSaveCondition.getService(serviceName);
    }

    @Override
    public Service findService(String serviceName, Socket client) throws ServiceNotFoundException {
        return containerNotSaveCondition.getService(serviceName, client);
    }

    public static Server build() {
        ServerImplDefault serverImplDefault = new ServerImplDefault();
        serverImplDefault.containerSaveCondition = new ContainerSaveConditionImplDefault();
        serverImplDefault.containerNotSaveCondition = new ContainerNotSaveConditionImplDefault();
        serverImplDefault.port = 6968;
        return serverImplDefault;
    }
}

