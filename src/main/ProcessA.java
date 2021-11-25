package main;

import communication.Chatter;
import process.Heavyweight;
import utils.constants;
import utils.constants.*;

import java.io.IOException;
import java.net.ServerSocket;

public class ProcessA {
    public static void main(String args[]) {
        // As this is the leader process, we have to open connection for the other heavyweight to connect to it:
        Chatter leaderServer = beginLeader();

        Heavyweight heavyweight = new Heavyweight(constants.AMOUNT_PROCESS_A, 0, leaderServer).debug(true);
        heavyweight
                .listen(constants.PORT_A)
                .execute();
    }

    private static Chatter beginLeader() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(constants.PORT_LEADER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Chatter chatter = new Chatter(socket);
        chatter.openConnection();

        return chatter;
    }
}
