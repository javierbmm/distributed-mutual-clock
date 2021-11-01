package main.lightweight.a;

import process.Lightweight;
import utils.constants;

public class ProcessLWA3 {
    public static void main(String[] args) {
        Lightweight lightweight = new Lightweight(2, constants.PORT_A, constants.AMOUNT_PROCESS_A);

        lightweight.execute();
    }
}
