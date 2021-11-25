package main;

import communication.Chatter;
import process.Heavyweight;
import utils.constants;
import utils.constants.*;

public class ProcessB {
    public static void main(String args[]) {
        // As ProcessA is the leader, we have to connect to it:
        Chatter chatter = new Chatter();
        chatter.connectTo(null, constants.PORT_LEADER);

        Heavyweight heavyweight = new Heavyweight(constants.AMOUNT_PROCESS_B, 1, chatter).debug(true);
        heavyweight
                .listen(constants.PORT_B)
                .execute();
    }
}
