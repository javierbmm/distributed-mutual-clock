package main.lightweight.b;

import mutualexclusion.RicartAndAgrawala;
import process.Lightweight;
import utils.constants;

public class ProcessLWB3 {
    public static void main(String[] args) {
        Lightweight lightweight = new Lightweight("B", 2, constants.PORT_B, constants.AMOUNT_PROCESS_B);

        lightweight.mutexMethod(new RicartAndAgrawala())
                   .execute();
    }
}
