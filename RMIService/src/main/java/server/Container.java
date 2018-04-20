package server;

//save implementation service
public interface Container {

    boolean addService(Service service, String name);

    boolean removeService(String serviceName);

    Service getService(String name) throws ServiceNotFoundException;
}
