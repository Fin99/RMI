package server;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Arrays;
import java.util.stream.Stream;

public class ServerImplDefault implements Server {
    private Container container;
    private int port;

    @Override
    public Server setContainer(Container container) {
        this.container = container;
        return this;
    }

    @Override
    public Server setPort(int port) {
        this.port = port;
//        if (!isSocketEmpty(port)) throw new IllegalArgumentException();
        return this;
    }

    @Override
    public boolean addService(Service service, String serviceName) {
        return container.addService(service, serviceName);
    }


    @Override
    public void start() throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        while (true) {
            byte[] receiveArray = new byte[10000];
            DatagramPacket receivePacket = new DatagramPacket(receiveArray, receiveArray.length);
            DatagramSocket socket = new DatagramSocket(port);
            socket.receive(receivePacket);
            Object result;
            byte[] buffer = new byte[receiveArray.length - 1];
            if (receiveArray[0] == 0) {
                //client ask service
                System.arraycopy(receiveArray, 1, buffer, 0, buffer.length);
                result = getServiceToClient(buffer);
            } else {
                //client ask result invoke this method
                System.arraycopy(receiveArray, 1, buffer, 0, buffer.length);
                result = getResultInvokeMethodToClient(buffer, socket);
            }
            //send result to client
            byte[] bufferResult = writeToByteArray(result);
            DatagramPacket packetResult = new DatagramPacket(bufferResult, bufferResult.length, InetAddress.getLocalHost(), port+1/*receivePacket.getPort()*/);
            socket.close();
            socket = new DatagramSocket();
            socket.send(packetResult);
            socket.close();
        }
    }

    private Object getResultInvokeMethodToClient(byte[] arrayCopy, DatagramSocket socket) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String name = (String) readFromByteArray(arrayCopy);
        byte[] buffer = new byte[10000];
        DatagramPacket packetMethod = new DatagramPacket(buffer, buffer.length);
        socket.receive(packetMethod);
        String method = (String) readFromByteArray(buffer);
        DatagramPacket packetCountArgs = new DatagramPacket(buffer, buffer.length);
        socket.receive(packetCountArgs);
        Integer countArgs = (Integer) readFromByteArray(buffer);
        Object[] args = new Object[countArgs];
        Class[] argsClass = new Class[countArgs];
        for (int i = 0; i < countArgs; i++) {
            DatagramPacket packetArg = new DatagramPacket(buffer, buffer.length);
            socket.receive(packetArg);
            args[i] = readFromByteArray(buffer);
            argsClass[i] = args[i].getClass();
        }

        Service service = findService(name);
        Method methodService = service.getClass().getMethod(method, argsClass);
        return methodService.invoke(service, args);
    }

    private Object getServiceToClient(byte[] arrayCopy) throws IOException, ClassNotFoundException {
        //find service for client
        String string = (String) readFromByteArray(arrayCopy);
        return findService(string);
    }

    @Override
    public Service findService(String serviceName) {
        return container.getService(serviceName);
    }

    public static Server build() {
        ServerImplDefault serverImplDefault = new ServerImplDefault();
        serverImplDefault.container = new ContainerImplDefault();
        //serverImplDefault.port = findEmptySocket();
        return serverImplDefault;
    }

    private static int findEmptySocket() {
        int testPort = 10001;
        /*while (true) {
            if (isSocketEmpty(testPort)) {
                break;
            } else {
                testPort++;
            }
        }*/
        return testPort;
    }

    private static boolean isSocketEmpty(int port) {
        Socket s = null;
        try {
            s = new Socket("localhost", port);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("You should handle this error.", e);
                }
            }
        }
    }

    private static byte[] writeToByteArray(Object element) {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(baos);) {
            out.writeObject(element);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object readFromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                ObjectInputStream in = new ObjectInputStream(bais);) {
            return in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

