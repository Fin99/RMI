package client;

import java.io.IOException;

public class RMIClient {
    public static Registry createRegistry(int port) {
        try {
            return new RegistryImpl(port);
        } catch (IOException e) {
            new IOException("Failed to connect to server").printStackTrace();
        }
        return null;
    }
}
