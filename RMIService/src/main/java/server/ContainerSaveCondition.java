package server;

//save implementation service
public interface ContainerSaveCondition {

    boolean addService(Service service, String name);

    boolean removeService(String serviceName);

    Service getService(String name) throws ServiceNotFoundException;

    boolean contains(String serviceName);
}
