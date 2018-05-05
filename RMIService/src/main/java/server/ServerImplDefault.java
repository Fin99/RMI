package server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerImplDefault implements Server {
    private Container container;
    private int port;

    @Override
    public Server setContainer(Container container) {
        this.container = container;
        return this;
    }

    @Override
    public Server setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public boolean addService(Service service, String serviceName) {
        return container.addService(service, serviceName);
    }

    @Override
    @Deprecated
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            new ClientThreadImpl(container, serverSocket.accept()).start();
        }
    }

    @Override
    public Service findService(String serviceName) throws ServiceNotFoundException {
        return container.getService(serviceName);
    }

    public static Server build() {
        ServerImplDefault serverImplDefault = new ServerImplDefault();
        serverImplDefault.container = new ContainerImplDefault();
        serverImplDefault.port = 6968;
        return serverImplDefault;
    }
}

