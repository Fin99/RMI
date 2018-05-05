package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContainerNotSaveConditionImplDefault implements ContainerNotSaveCondition {
    private List<TypeService> typesService;
    private List<ServiceImpl> services;

    {
        typesService = new ArrayList<>();
        services = new ArrayList<>();
    }

    @Override
    public boolean addTypeService(Class service, String name) {
        if (!Service.class.isAssignableFrom(service))
            throw new ClassCastException("Class not implements interface Service");
        TypeService typeService = new TypeService(service, name);
        if (typesService.contains(typeService)) return false;
        typesService.add(typeService);
        return true;
    }

    @Override
    public boolean removeTypeService(String serviceName) {
        return typesService.remove(new TypeService(null, serviceName));
    }

    @Override
    public Service addService(String name, Socket socket) throws InstantiationException, IllegalAccessException, ServiceNotFoundException {
        TypeService typeService = null;
        for (TypeService tService : typesService) {
            if (tService.name.equals(name)) typeService = tService;
        }
        if (typeService == null) throw new ServiceNotFoundException(name + "(ServiceType)");
        for (ServiceImpl s : services) {
            if (s.socket.equals(socket) && s.typeService.equals(typeService)) return s.getService();
        }
        Service service = typeService.createInstanceTypeService();
        services.add(new ServiceImpl(typeService, socket, service));
        return service;
    }

    @Override
    public void removeService(Socket socket) {
        services.removeIf(s -> s.socket.equals(socket));
    }

    @Override
    public Service getService(String name, Socket socket) throws ServiceNotFoundException {
        for (ServiceImpl s : services) {
            if (s.socket.equals(socket) && s.typeService.name.equals(name)) return s.getService();
        }
        throw new ServiceNotFoundException(name + "(" + socket.getInetAddress().toString() + ":" + socket.getPort() + ")");
    }

    @Override
    public boolean contains(String serviceName) {
        return typesService.contains(new TypeService(null, serviceName));
    }

    private static class TypeService {
        private Class service;
        private String name;

        TypeService(Class service, String name) {
            this.service = service;
            this.name = name;
        }

        Service createInstanceTypeService() throws IllegalAccessException, InstantiationException {
            return (Service) service.newInstance();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof TypeService)) return false;
            return name.equals(((TypeService) obj).name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class ServiceImpl {
        private TypeService typeService;
        private Socket socket;
        private Service service;


        public ServiceImpl(TypeService typeService, Socket socket, Service service) {
            this.typeService = typeService;
            this.socket = socket;
            this.service = service;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof ServiceImpl)) return false;
            return typeService.name.equals(((ServiceImpl) obj).typeService.name) && socket.equals(((ServiceImpl) obj).socket);
        }

        public Service getService() {
            return service;
        }
    }
}
