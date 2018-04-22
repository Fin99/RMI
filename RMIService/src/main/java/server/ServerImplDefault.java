package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerImplDefault implements Server {
    private Container container;
    private int port;

    public static Server build() {
        ServerImplDefault serverImplDefault = new ServerImplDefault();
        serverImplDefault.container = new ContainerImplDefault();
        serverImplDefault.port = 6968;
        return serverImplDefault;
    }

    private static byte[] writeToByteArray(Object element) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(baos);) {
            out.writeObject(element);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Server setContainer(Container container) {
        this.container = container;
        return this;
    }

    @Override
    public Server setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public boolean addService(Service service, String serviceName) {
        return container.addService(service, serviceName);
    }

    @Override
    public void start() throws IOException {
        while (true) {
            //wait request client
            byte[] receiveArray = new byte[10000];
            DatagramPacket receivePacket = new DatagramPacket(receiveArray, receiveArray.length);
            DatagramSocket socket = new DatagramSocket(port);
            socket.receive(receivePacket);
            Object result;
            byte[] buffer = new byte[receiveArray.length - 1];
            if (receiveArray[0] == 0) {
                //client ask service
                System.arraycopy(receiveArray, 1, buffer, 0, buffer.length);
                System.out.println("Client ask service...");
                result = getServiceToClient(buffer);
                System.out.println("Service was send");
            } else {
                //client ask result invoke this method
                System.arraycopy(receiveArray, 1, buffer, 0, buffer.length);
                System.out.println("Client ask result invoke method...");
                result = getResultInvokeMethodToClient(buffer, socket);
                System.out.println("Result was send");
            }
            //send result to client
            byte[] bufferResult = writeToByteArray(result);
            DatagramPacket packetResult = new DatagramPacket(bufferResult, bufferResult.length, InetAddress.getLocalHost(), port + 1/*receivePacket.getPort()*/);
            socket.close();
            socket = new DatagramSocket();
            socket.send(packetResult);
            socket.close();
        }
    }

    private Object getResultInvokeMethodToClient(byte[] arrayCopy, DatagramSocket socket) throws IOException {
        //get name service
        String name = (String) readFromByteArray(arrayCopy);
        //get name method
        byte[] buffer = new byte[10000];
        DatagramPacket packetMethod = new DatagramPacket(buffer, buffer.length);
        socket.receive(packetMethod);
        String method = (String) readFromByteArray(buffer);
        //get count args method
        DatagramPacket packetCountArgs = new DatagramPacket(buffer, buffer.length);
        socket.receive(packetCountArgs);
        Integer countArgs = (Integer) readFromByteArray(buffer);
        Object[] args = new Object[countArgs];
        Class[] argsClass = new Class[countArgs];
        //if count args greater than zero then get all args and class this args
        for (int i = 0; i < countArgs; i++) {
            DatagramPacket packetArg = new DatagramPacket(buffer, buffer.length);
            socket.receive(packetArg);
            args[i] = readFromByteArray(buffer);
        }
        for (int i = 0; i < countArgs; i++) {
            DatagramPacket packetArg = new DatagramPacket(buffer, buffer.length);
            socket.receive(packetArg);
            argsClass[i] = (Class) readFromByteArray(buffer);
        }
        // invoke method and return result
        Service service = null;
        try {
            service = findService(name);
        } catch (ServiceNotFoundException e) {
            return writeToByteArray(new ServiceNotFoundException(name));
        }
        Method methodService = null;
        try {
            methodService = service.getClass().getMethod(method, argsClass);
            return methodService.invoke(service, args);
        } catch (NoSuchMethodException e) {
            return writeToByteArray(new IllegalArgumentException("The requested method was not found"));
        } catch (IllegalAccessException | InvocationTargetException e) {
            return writeToByteArray(new IllegalArgumentException("The requested method is not available"));
        }
    }

    private Object getServiceToClient(byte[] arrayCopy) {
        //find service for client
        String serviceName = (String) readFromByteArray(arrayCopy);
        try {
            return findService(serviceName);
        } catch (ServiceNotFoundException e) {
            return new ServiceNotFoundException(serviceName);
        }
    }

    @Override
    public Service findService(String serviceName) throws ServiceNotFoundException {
        return container.getService(serviceName);
    }

    private Object readFromByteArray(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bais);) {
            return in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

