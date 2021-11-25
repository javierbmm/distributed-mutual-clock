package process;

import communication.Chatter;
import communication.Dataframe;
import mutualexclusion.Token;
import utils.constants;
import utils.constants.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Heavyweight {
    private volatile int answers = 0;
    private final int NUM_LIGHTWEIGHTS;
    private ServerSocket serverSocket;
    private Boolean debug = false;
    private final Token token;
    private final Chatter server;
    private final int ID;
    private Map<Integer, Chatter> clientList = new HashMap<Integer, Chatter>();

    public Heavyweight(int num_lightweights, int id, Chatter chatter) {
        this.NUM_LIGHTWEIGHTS = num_lightweights;
        ID = id;
        server = chatter;
        token = new Token(ID, constants.LEADER, server);
    }

    public Heavyweight debug(boolean mode) {
        this.debug = mode;
        return this;
    }

    public Heavyweight listen(int port) {
        try {
            serverSocket = new ServerSocket(port);
           // serverSocket.setSoTimeout(500);
        }
        catch(IOException e){
            e.printStackTrace();
            System.err.println("Server error");
        }

        for(int i=0; i<NUM_LIGHTWEIGHTS; i++) {
            Chatter chatter = new Chatter(serverSocket).debug(debug);
            chatter.openConnection();
            // Greetings message to store the client ID
            String id = chatter.read();
            new Handler(chatter, Integer.parseInt(id))
                .start();
        }

        return this;
    }

    public void execute() {
        while(true) {
            answers = 0;
            token.requestCS();
            System.out.println("I have the Token");
            sendActionToLightweight(Dataframe.START);
            while (answers < NUM_LIGHTWEIGHTS) ;
//            sendTokenToHeavyweight();
            sendActionToLightweight(Dataframe.STOP);
            token.releaseCS();
        }
    }


    private void sendActionToLightweight(String action) {
        for(Chatter client : clientList.values()) {
            client.send(action);
        }
    }

    private class Handler extends Thread{
        private ServerSocket socket;
        private final Chatter clientChatter;

        //constructor
        public Handler(Chatter chatter, int id) {
            this.clientChatter = chatter;
            clientList.put(id, chatter);
        }

        @Override
        public void run() {
            Dataframe dataframe;
            //print whatever client is saying as long as it is not "Close"
            String line = "";
            do {
                line = clientChatter.read();
                dataframe = new Dataframe(line);
                if(debug)
                    System.out.println("[SERVER]: Received "+dataframe);
                // TODO do stuff depending on frame
                if(dataframe.getMessage().equals(Dataframe.DONE))
                    answers++;
                else if(dataframe.getDest() == Dataframe.BROADCAST)
                    broadcast(dataframe);
                else {
                    Chatter dest = clientList.get(dataframe.getDest());
                    dest.send(dataframe.toString());
                }
            } while(!dataframe.getMessage().equals(Dataframe.CLOSE));

            //closes connection when client terminates the connection
            System.out.print("Closing Connection");
            clientChatter.stop();
        }

        private void broadcast(Dataframe msg) {
            for(Chatter client : clientList.values()) {
                if(clientChatter.equals(client))
                    continue;

                client.send(msg.toString());
            }
        }
    }
}
