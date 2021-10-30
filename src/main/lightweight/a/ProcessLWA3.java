package main.lightweight.a;

import communication.Chatter;
import communication.Dataframe;
import utils.constants;

public class ProcessLWA3 {

    public static void main(String[] args) {
        Chatter chatter = new Chatter();
        chatter.connectTo(null, constants.PORT_A);
        String inputString = "";

        while(!inputString.equals(Dataframe.CLOSE)) {
            inputString = chatter.read();
            System.out.println(inputString);
        }
    }
}
