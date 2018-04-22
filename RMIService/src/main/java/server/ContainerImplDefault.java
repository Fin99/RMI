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
    public boolean removeService(String serviceName) {
        return container.remove(serviceName) != null;
    }

    @Override
    public Service getService(String name) throws ServiceNotFoundException {
        Service service = container.get(name);
        if (service == null) throw new ServiceNotFoundException(name);
        return service;
    }
}
