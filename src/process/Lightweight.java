package process;

import communication.Chatter;
import communication.Dataframe;
import mutualexclusion.Mutex;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Lightweight {
    private final int myID;
    private volatile int token;
    private final Chatter chatter;
    private final String name;
    private Mutex mutex;
    private int amountReplicas;

    public Lightweight(String name, int myID, int serverPort, int N) {
        this.myID = myID;
        this.chatter = greetings(serverPort);
        this.amountReplicas = N;
        this.name = name;
    }

    public void execute() {
        while (true) {
            waitHeavyweight();
            mutex.requestCS();
            printMsg(); // print to screen (CS)
            mutex.releaseCS();
            notifyHeavyweight();
        }

        //sendCloseMsg();
    }

    private Chatter greetings(int serverPort){
        Chatter chatter = new Chatter();
        chatter.connectTo(null, serverPort);
        chatter.send(String.valueOf(myID));

        return chatter;
    }

    private void waitHeavyweight() {
        String msg = "" ;
        while(!msg.equals(Dataframe.START))
            msg = chatter.read();
    }

    private void notifyHeavyweight() {
        Dataframe df = Dataframe.parse(-1, -1, Dataframe.DONE, -1);
        chatter.send(df.toString());
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

    public void printMsg() {
        for (int i = 1; i <= 10; i++) {
            String timeStamp = "[" + new SimpleDateFormat("HH.mm.ss").format(new Date()) + "]";
            String index = "[" + i + "]";
            String nameID = name + (myID+1);
            System.out.println(index + timeStamp + " I am the process lightweight " + nameID);
            wait(1000);
        }
        System.out.println("_".repeat(24));
    }

    private static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public Lightweight mutexMethod(Mutex method) {
        this.mutex = method
                .ID(myID)
                .N(amountReplicas)
                .Chatter(chatter);

        return this;
    }

    public Lightweight debug(boolean mode) {
        chatter.debug(mode);
        return this;
    }
}
