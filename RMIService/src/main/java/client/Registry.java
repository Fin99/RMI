package client;

import server.ServiceNotFoundException;

import java.io.IOException;

public interface Registry {
    Object lookup( String name) throws IOException, ServiceNotFoundException;
}