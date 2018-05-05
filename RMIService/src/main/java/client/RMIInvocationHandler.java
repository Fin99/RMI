package client;

import sending.Send;
import server.ServiceNotFoundException;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RMIInvocationHandler implements InvocationHandler {
    private final DataInputStream serverInput;
    private final DataOutputStream serverOutput;
    private final String name;

    RMIInvocationHandler(InputStream inputStream, OutputStream outputStream, String name){
        serverInput = new DataInputStream(inputStream);
        serverOutput = new DataOutputStream(outputStream);
        this.name = name;
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws IOException, ServiceNotFoundException, IllegalArgumentException{
        Object result;
        //send type request
        Send.writeToByteArray(serverOutput, "invoke");
        //send name service
        Send.writeToByteArray(serverOutput, name);
        //send name method
        Send.writeToByteArray(serverOutput, m.getName());
        //send count args method
        if (args == null) {
            Send.writeToByteArray(serverOutput, 0);
        } else {
            Send.writeToByteArray(serverOutput, args.length);
            //send args
            for (Object a : args) {
                if (!(a instanceof Serializable))
                    throw new IllegalArgumentException("All arguments must implement the interface Serializable");
                Send.writeToByteArray(serverOutput, a);
            }
            //send class args
            for (Class a : m.getParameterTypes()) {
                Send.writeToByteArray(serverOutput, a);
            }
        }
        //message is sent. receive response
        result = Send.readFromByteArray(serverInput);
        if (result instanceof ServiceNotFoundException) throw (ServiceNotFoundException) result;
        if (result instanceof IllegalArgumentException) throw (IllegalArgumentException) result;
        return result;
    }

}
