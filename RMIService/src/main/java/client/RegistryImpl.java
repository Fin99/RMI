package client;

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

    //I shell send name this class and receive class
    @Override
    public Object lookup(String name) throws IOException, ClassNotFoundException {
        //send message for server
        byte[] bufferName = writeToByteArray(name);
        byte[] buffer = new byte[bufferName.length+1];
        buffer[0] = 0;
        System.arraycopy(bufferName, 0, buffer, 1, bufferName.length);
        DatagramPacket requestPacket = new DatagramPacket(buffer,  buffer.length, InetAddress.getLocalHost(),  port);
        DatagramSocket socket = new DatagramSocket();
        socket.send(requestPacket);
        socket.close();
        //receive implementation
        byte[] receiveMas = new byte[10000];
        DatagramPacket responsePacket = new DatagramPacket(receiveMas, receiveMas.length);
        socket = new DatagramSocket(port+1);
        socket.receive(responsePacket);
        socket.close();
        Object implementation = readFromByteArray(receiveMas);
        //create and return proxy
        Class[] interfaze = implementation.getClass().getInterfaces();
        ClassLoader loader = implementation.getClass().getClassLoader();
        InvocationHandler handler = new MyInvocationHandler(10001, name);
        return Proxy.newProxyInstance(loader, interfaze, handler);
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
