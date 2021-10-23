package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Chatter {
    private final int socket;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Chatter(int socket) {
        this.socket = socket;
    }

    public void openConnection() {
        try {
            serverSocket = new ServerSocket(socket);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error opening connection");
            e.printStackTrace();
        }
    }

    public void connectTo(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error connecting to "+ip+":"+port);
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if(in!=null) in.close();
            if(out!=null) out.close();
            if(clientSocket!=null) clientSocket.close();
            if(serverSocket!=null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection");
            e.printStackTrace();
        }
    }

    public String read() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void send(String message) {
        out.println(message);
    }
}
