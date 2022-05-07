package rpi;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static enums.Constant.ipAddr;
import static enums.Constant.portNum;

public class RPICommunicator {


    private Socket socket = null;
    private InputStream inputStream = null;
    private static RPICommunicator rpiCommunicator = null;
    private OutputStream dout = null;

    private RPICommunicator() {
    }

    public static RPICommunicator getInstance() {
        if (rpiCommunicator == null) rpiCommunicator = new RPICommunicator();
        return rpiCommunicator;
    }

    public boolean connect() {
        if (socket == null) {
            try {
                socket = new Socket(ipAddr, portNum);
                inputStream = socket.getInputStream();
                dout = socket.getOutputStream();
            } catch (IOException e) {
                System.out.println("Cant connect");
                return false;
            }
        }
        return true;
    }

    public void sendMessage(String message) {
        try {
            dout.write(message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readMessage() throws IOException {
        byte[] byteData = new byte[512];
        int size = 0;
        inputStream.read(byteData);
        while (size < 512) {
            if (byteData[size] == 0) break;

            size++;
        }
        return new String(byteData, 0, size, StandardCharsets.UTF_8);
    }

    public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
//                inputStream.close();
//                printStream.close();
//                printStream =null;
                socket = null;
//                inputStream =null;
                System.out.println("Connection closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
