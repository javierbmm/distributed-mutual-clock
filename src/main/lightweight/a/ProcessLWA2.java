package main.lightweight.a;

import communication.Chatter;
import communication.Dataframe;
import process.Lightweight;
import utils.constants;

import java.util.Scanner;

public class ProcessLWA2 extends Lightweight {
    public ProcessLWA2(String myID, int serverPort) {
        super(myID, serverPort);
    }

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
