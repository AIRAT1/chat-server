package ru.itpark;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ChatHandler extends Thread {
    private Socket socket;
    private final DataInputStream inStream;
    private final DataOutputStream outStream;
    private boolean isOn;

    private static List<ChatHandler> handlers = Collections.synchronizedList(new ArrayList<>());

    public ChatHandler(Socket socket) throws IOException {
        this.socket = socket;
        inStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        outStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {
        isOn = true;
        try {
            handlers.add(this);
            while (isOn) {
                String msg = inStream.readUTF();
                broadcast(msg);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            handlers.remove(this);
            try {
                outStream.close();
            }catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                socket.close();
            }catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    protected static void broadcast(String message) {
        synchronized (handlers) {
            Iterator<ChatHandler> it = handlers.iterator();
            while (it.hasNext()) {
                ChatHandler c = it.next();
                try {
                    synchronized (c.outStream) {
                        c.outStream.writeUTF(message);
                    }
                    c.outStream.flush();
                }catch (IOException e) {
                    e.printStackTrace();
                    c.isOn = false;
                }
            }
        }
    }
}
