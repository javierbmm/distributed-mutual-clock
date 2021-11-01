package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Chatter {
    private int socket;
    private final Boolean isServer;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    // Use this constructor to initialize a server-side connection
    public Chatter(ServerSocket serverSocket) {
        isServer = true;
        // this.socket = socket;
        this.serverSocket = serverSocket;
    }

    // Use this constructor to initialize a client-side connection
    public Chatter() {
        isServer = false;
    }
    public void openConnection() {
        assert !isServer;
        try {
            // serverSocket = new ServerSocket(socket);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error opening connection");
            e.printStackTrace();
        }
    }

    public Chatter connectTo(String ip, int port) {
        assert isServer;
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error connecting to "+ip+":"+port);
            e.printStackTrace();
        }

        return this;
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
