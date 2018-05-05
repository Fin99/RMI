package server;

import sending.Send;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ClientThreadImpl extends Thread implements ClientThread {
    private final ContainerSaveCondition containerSaveCondition;
    private final ContainerNotSaveCondition containerNotSaveCondition;
    private final DataInputStream clientInput;
    private final DataOutputStream clientOutput;
    private final Socket client;

    ClientThreadImpl(ContainerNotSaveCondition containerNotSaveCondition, ContainerSaveCondition container, Socket socket) throws IOException {
        this.containerNotSaveCondition = containerNotSaveCondition;
        this.containerSaveCondition = container;
        clientInput = new DataInputStream(socket.getInputStream());
        clientOutput = new DataOutputStream(socket.getOutputStream());
        client = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //wait request client
                String typeRequest = (String) Send.readFromByteArray(clientInput);
                if (typeRequest.equals("lookup")) {
                    //client ask service
                    System.out.println("Client ask service...");
                    getServiceToClient();
                    System.out.println("Service was send");
                } else {
                    //client ask result invoke this method
                    System.out.println("Client ask result invoke method...");
                    getResultInvokeMethodToClient();
                    System.out.println("Result was send");
                }
            }
        } catch (IOException e) {
            containerNotSaveCondition.removeService(client);
            new IOException("Client disconnect").printStackTrace();
        }
    }

    private void getResultInvokeMethodToClient() throws IOException {
        //get name service
        String name = (String) Send.readFromByteArray(clientInput);
        //get name method
        String method = (String) Send.readFromByteArray(clientInput);
        //get count args method
        Integer countArgs = (Integer) Send.readFromByteArray(clientInput);
        Object[] args = new Object[countArgs];
        Class[] argsClass = new Class[countArgs];
        //if count args greater than zero then get all args and class this args
        for (int i = 0; i < countArgs; i++) {
            args[i] = Send.readFromByteArray(clientInput);
        }
        for (int i = 0; i < countArgs; i++) {
            argsClass[i] = (Class) Send.readFromByteArray(clientInput);
        }
        // invoke method and return result
        Service service = null;
        try {
            service = containerSaveCondition.getService(name);
        } catch (ServiceNotFoundException e) {
            try {
                service = containerNotSaveCondition.getService(name, client);
            } catch (ServiceNotFoundException e1) {
                Send.writeToByteArray(clientOutput, new ServiceNotFoundException(name));
            }
        }
        Method methodService = null;
        try {
            methodService = service.getClass().getMethod(method, argsClass);
            Send.writeToByteArray(clientOutput, methodService.invoke(service, args));
        } catch (NoSuchMethodException e) {
            Send.writeToByteArray(clientOutput, new IllegalArgumentException("The requested method was not found"));
        } catch (IllegalAccessException | InvocationTargetException e) {
            Send.writeToByteArray(clientOutput, new IllegalArgumentException("The requested method is not available"));
        }
    }

    private void getServiceToClient() throws IOException {
        //find service for client
        String serviceName = (String) Send.readFromByteArray(clientInput);
        String typeCondition = (String) Send.readFromByteArray(clientInput);
        try {
            if(typeCondition.equals("Save")) {
                //send all interfaces that the class implements
                Class[] interfaces = containerSaveCondition.getService(serviceName).getClass().getInterfaces();
                Send.writeToByteArray(clientOutput, interfaces.length);
                for (Class interfaze : interfaces) {
                    Send.writeToByteArray(clientOutput, interfaze);
                }
            } else if(typeCondition.equals("NotSave")){
                Class[] interfaces =  containerNotSaveCondition.addService(serviceName, client).getClass().getInterfaces();
                Send.writeToByteArray(clientOutput, interfaces.length);
                for (Class interfaze : interfaces) {
                    Send.writeToByteArray(clientOutput, interfaze);
                }
            } else {
                throw new ServiceNotFoundException(serviceName);
            }
        } catch (ServiceNotFoundException e) {
            Send.writeToByteArray(clientOutput, new ServiceNotFoundException(serviceName));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
