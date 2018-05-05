package client;

import sending.Send;
import server.ServiceNotFoundException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.Socket;

public class RegistryImpl implements Registry {
    private final Socket server;
    private final DataOutputStream serverOutput;
    private final DataInputStream serverInput;

    public RegistryImpl(int port) throws IOException {
        server = new Socket(InetAddress.getLocalHost(), port);
        serverOutput = new DataOutputStream(server.getOutputStream());
        serverInput = new DataInputStream(server.getInputStream());
    }

    @Override
    public Object lookup(String name, boolean saveCondition) throws IOException, ServiceNotFoundException {
        //check correctness service name
        if (name == null || name.equals("")) throw new IllegalArgumentException("Invalid service name");
        //send type request
        Send.writeToByteArray(serverOutput, "lookup");
        //send message name service
        Send.writeToByteArray(serverOutput, name);
        //send type condition
        if(saveCondition){
            Send.writeToByteArray(serverOutput, "Save");
        } else {
            Send.writeToByteArray(serverOutput, "NotSave");
        }
        //receive number interfaces that the class implements or exception
        Object numberInterfacesOrException = Send.readFromByteArray(serverInput);
        if (numberInterfacesOrException instanceof Throwable)
            throw (ServiceNotFoundException) numberInterfacesOrException;
        //check whether server has found service
        if (numberInterfacesOrException == null) throw new IllegalArgumentException("Invalid service name");
        int numberInterfaces = (int) numberInterfacesOrException;
        //create and return proxy
        Class[] interfaces = new Class[numberInterfaces];
        for (int i = 0; i < numberInterfaces; i++) {
            interfaces[i] = (Class) Send.readFromByteArray(serverInput);
        }
        ClassLoader loader = interfaces[0].getClassLoader();
        InvocationHandler handler = new RMIInvocationHandler(serverInput, serverOutput, name);
        return Proxy.newProxyInstance(loader, interfaces, handler);
    }

}
