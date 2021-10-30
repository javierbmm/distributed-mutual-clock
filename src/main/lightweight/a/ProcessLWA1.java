package main.lightweight.a;

import communication.Chatter;
import communication.Dataframe;
import process.Lightweight;
import utils.constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ProcessLWA1 extends Lightweight {
    public ProcessLWA1(String myID, int serverPort) {
        super(myID, serverPort);
    }

    public static void main(String[] args) {
        Chatter chatter = new Chatter();
        chatter.connectTo(null, constants.PORT_A);
        String inputString = "";

        while(!inputString.equals(Dataframe.CLOSE)) {
            System.out.println("Insert command: ");
            Scanner scanner = new Scanner(System.in);
            inputString = scanner.nextLine();

            chatter.send(inputString);
        }
    }
}
