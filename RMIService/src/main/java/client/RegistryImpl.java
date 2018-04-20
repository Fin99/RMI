package client;

import com.sun.istack.internal.NotNull;
import server.ServiceNotFoundException;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RegistryImpl implements Registry {
    private int port;

    public RegistryImpl(int port) {
        this.port = port;
    }

    @Override
    public Object lookup(@NotNull String name) throws IOException, ServiceNotFoundException {
        //check correctness service name
        if(name == null || name.equals("")) throw new IllegalArgumentException("Invalid service name");
        //send message name service
        byte[] bufferName = writeToByteArray(name);
        byte[] buffer = new byte[bufferName.length + 1];
        buffer[0] = 0;
        System.arraycopy(bufferName, 0, buffer, 1, bufferName.length);
        DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
        DatagramSocket socket = new DatagramSocket();
        socket.send(requestPacket);
        socket.close();
        //receive implementation
        byte[] receiveMas = new byte[10000];
        DatagramPacket responsePacket = new DatagramPacket(receiveMas, receiveMas.length);
        socket = new DatagramSocket(port + 1);
        socket.setSoTimeout(2000);
        socket.receive(responsePacket);
        socket.close();
        Object implementation = readFromByteArray(receiveMas);
        if(implementation instanceof Throwable)throw (ServiceNotFoundException) implementation;
        //check whether server has found service
        if (implementation == null) throw new IllegalArgumentException("Invalid service name");
        //create and return proxy
        Class[] interfaze = implementation.getClass().getInterfaces();
        ClassLoader loader = implementation.getClass().getClassLoader();
        InvocationHandler handler = new RMIInvocationHandler(10001, name);
        return Proxy.newProxyInstance(loader, interfaze, handler);
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

    private Object readFromByteArray(byte[] bytes){
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bais);) {
            return in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
