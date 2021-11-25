package main.lightweight.a;

import mutualexclusion.Lamport;
import process.Lightweight;
import utils.constants;

public class ProcessLWA2 {
    public static void main(String[] args) {
        Lightweight lightweight = new Lightweight("A", 1, constants.PORT_A, constants.AMOUNT_PROCESS_A);

        lightweight.mutexMethod(new Lamport())
                   .execute();
    }
}
