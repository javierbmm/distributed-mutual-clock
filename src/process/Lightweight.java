package process;

import communication.Chatter;

public class Lightweight {
    private final String myID;
    private volatile int token;
    private final Chatter server;

    public Lightweight(String myID, int serverPort) {
        this.myID = myID;
        this.server = new Chatter();
    }

    private void execute() {
    /* TODO: Review and implement
    while(1) {
        waitHeavyWeight();
        requestCS();
        for (int i = 0; i < 10; i++) {
            printf("I am the process lightweight %s\n", myID);
            espera1Segon();
        }
        releaseCS();
        notifyHeavyWeight();
    }
     */
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
        }
        catch(InterruptedException ex) {
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

}
