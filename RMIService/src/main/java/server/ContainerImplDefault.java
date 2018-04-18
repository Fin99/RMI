package server;

import java.util.HashMap;

public class ContainerImplDefault implements Container {
    private HashMap<String, Service> container;

    {
        container = new HashMap<>();
    }

    @Override
    public boolean addService(Service service, String name) {
        return container.put(name, service) == null;
    }

    @Override
    public boolean remoteService(String serviceName) {
        return container.remove(serviceName) != null;
    }

    @Override
    public Service getService(String name) {
        return container.get(name);
    }
}
