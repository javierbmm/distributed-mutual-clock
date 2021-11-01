package process;

import communication.Chatter;
import communication.Dataframe;
import mutualexclusion.Mutex;

public class Lightweight {
    private final int myID;
    private volatile int token;
    private final Chatter chatter;
    private Mutex mutex;
    private int amountReplicas;

    public Lightweight(int myID, int serverPort, int N) {
        this.myID = myID;
        Chatter chatter = new Chatter();
        chatter.connectTo(null, serverPort);
        this.chatter = chatter;
        this.amountReplicas = N;
        // Moving this line to a specific method (mutexMethod()):
        // this.lamport = new Lamport(myID, N, chatter);
    }

    public void execute() {
        // Waiting a couple of seconds for other process to initialize
        // TODO: Instead of waiting, processes must send messages to each other to announce that they are alive.
        System.out.println("Waiting...");
        wait(10000);
        for (int i = 0; i < 2; i++) {
            //waitHeavyWeight();
            mutex.requestCS();
            printMsg(); // print to screen (CS)
            mutex.releaseCS();
            // notifyHeavyWeight();
        }

        sendCloseMsg();
    }

    private void sendCloseMsg() {
        // Goodbye my lover
        String bye = new Dataframe()
                .timestamp(Integer.MAX_VALUE)
                .source(myID)
                .message(Dataframe.CLOSE)
                .destination(Dataframe.BROADCAST)
                .toString();

        chatter.send(bye);
        chatter.stop();
    }

    private void notifyHeavyWeight() {
    }

    public void printMsg() {
        for (int i = 0; i < 10; i++) {
            System.out.println("I am the process lightweight" + myID);
            wait(1000);
        }
    }

    private static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void waitHeavyWeight() {
        while (token == -1)
            ;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public Lightweight mutexMethod(Mutex method) {
        this.mutex = method
                .ID(myID)
                .N(amountReplicas)
                .Chatter(chatter);

        return this;
    }
}
