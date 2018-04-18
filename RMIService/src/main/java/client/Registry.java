package client;

import java.io.IOException;

//find server use specified ip and port. Registry can make request for server to find needed service
public interface Registry {
    Object lookup(String name) throws IOException, ClassNotFoundException;
}
