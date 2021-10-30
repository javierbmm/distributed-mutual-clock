package process;

import communication.Chatter;
import communication.Dataframe;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Heavyweight {
    private int answers = 0;
    private final static int NUM_LIGHTWEIGHTS = 3;
    private ServerSocket serverSocket;

    public Heavyweight() {
    }

    public void listen() {
        try {
            serverSocket = new ServerSocket(utils.constants.PORT_A);
           // serverSocket.setSoTimeout(500);
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Server error");
        }

        for(int i=0; i<3; i++) {
            Chatter chatter = new Chatter(serverSocket);
            chatter.openConnection();
            new Handler(chatter)
                .start();
        }
    }

    /* TODO: review and implement
    while(1)
    {
        while (!token) listenHeavyweight();
        for (int i = 0; i < NUM_LIGHTWEIGHTS; i++) sendActionToLightweight();
        while (answersfromLightweigth < NUM_LIGHTWEIGHTS) listenLightweight();
        token = 0;
        sendTokenToHeavyweight();
    }

     */

    private void listenLightweight() {
        // wait -> listen -> send broadcast -> go next
    }

    private class Handler extends Thread{
        private ServerSocket socket;
        private final Chatter clientChatter;
        private static List<Chatter> clientList = new ArrayList<>();

        //constructor
        public Handler(Chatter chatter) {
            this.clientChatter = chatter;
            clientList.add(chatter);
        }

        @Override
        public void run() {
            Dataframe dataframe;
            //print whatever client is saying as long as it is not "Over"
            String line = "";
            do {
                line = clientChatter.read();
                dataframe = new Dataframe(line);
                // TODO do stuff depending on frame
                if(dataframe.getDest() == Dataframe.BROADCAST)
                    broadcast(dataframe.getMessage());

            } while(!dataframe.getMessage().equals(Dataframe.CLOSE));

            //closes connection when client terminates the connection
            System.out.print("Closing Connection");
            clientChatter.stop();
        }

        private void broadcast(String msg) {
            for(Chatter client : clientList) {
                client.send(msg);
            }
        }
    }
}
