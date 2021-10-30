package mutualexclusion;

import chronos.DirectClock;
import chronos.LamportClock;
import communication.Chatter;
import communication.Dataframe;

import java.util.LinkedList;

public class RicartAndAgrawala implements Mutex {
    private int myts; //mytimestamp
    private LamportClock c = new LamportClock();
    private LinkedList<Integer> pendingQ = new LinkedList<>();
    private int ID; // id of the current node
    private int numOkay = 0;
    private Chatter chatter; //for communication
    private int N; //number of programs

    public RicartAndAgrawala (Chatter chatter, int ID, int N) {
        this.ID = ID;
        this.chatter = chatter;
        this.N = N;
    }
    @Override
    public synchronized void requestCS() {
        c.tick();
        myts = c.getValue();
        broadcastMsg("request", myts);
        numOkay = 0;
        while (numOkay < N-1) {
            // TODO: Define myWait()
            //myWait();
        }
    }

    @Override
    public void releaseCS() {
        myts = Integer.MAX_VALUE;
        Dataframe dataframe;
        while (!pendingQ.isEmpty()) {
            int pid = pendingQ.removeFirst();
            dataframe = new Dataframe(ID, c.getValue(), "okay", pid);
            chatter.send(dataframe.toString());
        }
    }

    @Override
    public boolean okayCS() {
        return false;
    }
    public void broadcastMsg(String msg, int timestamp){
        Dataframe dataframe = new Dataframe(myts, timestamp, msg, Dataframe.BROADCAST);
        chatter.send(dataframe.toString());
        // TODO: Implement waitAck and call it here to wait for every process to send ACK message
    }
    public synchronized void handleMsg(Dataframe df, int src, String tag) {
        Dataframe dataframe;

        int timeStamp = df.getTimestamp();
        c.receiveAction(src, timeStamp);
        if(df.getMessage().equals("request")) {
            if((myts == Integer.MAX_VALUE) || (timeStamp < myts) || (timeStamp == myts) && (src < ID)) {
                dataframe = new Dataframe(ID, myts, "okay", src);
                chatter.send(dataframe.toString());
            } else {
                pendingQ.add(ID);
            }
        } else if(df.getMessage().equals("okay")) {
            numOkay ++;
            if (numOkay == N - 1) {
                notify();
            }
        }
    }
}