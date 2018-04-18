package server;

//save implementation service
public interface Container {

    boolean addService(Service service, String name);

    boolean remoteService(String serviceName);

    Service getService(String name);
}
