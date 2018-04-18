package client;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MyInvocationHandler implements InvocationHandler {
    private final int port;
    private final String name;

    MyInvocationHandler(int port, String name) {
        this.port = port;
        this.name = name;
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object result;
        //send arguments method to server
        byte[] bufferName = writeToByteArray(name);
        byte[] buffer = new byte[bufferName.length+1];
        buffer[0] = 1;
        System.arraycopy(bufferName, 0, buffer, 1, bufferName.length);
        DatagramSocket request = new DatagramSocket();
        DatagramPacket packetName = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
        request.send(packetName);
        buffer = writeToByteArray(m.getName());
        DatagramPacket packetMethod = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
        request.send(packetMethod);
        buffer = writeToByteArray(new Integer(args.length));
        DatagramPacket packetCountArgs = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
        request.send(packetCountArgs);
        for (Object a: args) {
            buffer = writeToByteArray(a);
            DatagramPacket packetArgs = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
            request.send(packetArgs);
        }
        request.close();
        //message is sent. receive response
        byte[] bytes = new byte[10000];
        DatagramSocket response = new DatagramSocket(port+1);
        DatagramPacket resultInvokeMethod = new DatagramPacket(bytes, bytes.length);
        response.receive(resultInvokeMethod);
        result = readFromByteArray(bytes);
        response.close();
        return result;
    }

    private static byte[] writeToByteArray(Object element) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(element);
        return baos.toByteArray();
    }

    private Object readFromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bais);
        return in.readObject();

    }
}
