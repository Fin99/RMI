package client;

public class RMIClient {
    public static Registry createRegistry(int port) {
        return new RegistryImpl(port);
    }
}
