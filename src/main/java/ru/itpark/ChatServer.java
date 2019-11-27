package ru.itpark;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {
    private List<ChatHandler> handlers = Collections.synchronizedList(new ArrayList<>());
    public ChatServer(int port) {
        try (ServerSocket service = new ServerSocket(port)) {
            while (true) {
                Socket socket = service.accept();
                System.out.println("Accepted from: " + socket.getInetAddress());
                ChatHandler handler = new ChatHandler(socket, handlers);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String args0 = "8082";
        new ChatServer(Integer.parseInt(args0));
    }
}
