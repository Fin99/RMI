package client;

import com.sun.istack.internal.NotNull;
import server.ServiceNotFoundException;

import java.io.IOException;

public interface Registry {
    Object lookup(@NotNull String name) throws IOException, ServiceNotFoundException;
}