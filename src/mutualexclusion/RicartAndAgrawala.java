package mutualexclusion;

import chronos.DirectClock;
import chronos.LamportClock;
import communication.Chatter;
import communication.Dataframe;

import javax.sound.midi.SysexMessage;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RicartAndAgrawala implements Mutex {
    private int myts = Integer.MAX_VALUE; //mytimestamp
    private LamportClock c = new LamportClock();
    private LinkedList<Integer> pendingQ = new LinkedList<>();
    private int ID; // id of the current node
    private int numOkay = 0;
    private Chatter chatter; //for communication
    private int N; //number of programs
    private volatile boolean running = false;

    public RicartAndAgrawala(Chatter chatter, int ID, int N) {
        this.ID = ID;
        this.chatter = chatter;
        this.N = N;
    }

    public RicartAndAgrawala() {
    }

    @Override
    public synchronized void requestCS() {
        running = true;
        c.tick();
        myts = c.getValue();
        broadcastMsg("request", myts);
        numOkay = 0;
        while (numOkay < N - 1) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        numOkay++;
//        if(numOkay == N)
//            running = false;
    }

    @Override
    public boolean okayCS() {
        return false;
    }

    @Override
    public Mutex ID(int ID) {
        this.ID = ID;
        return this;
    }

    @Override
    public Mutex N(int N) {
        this.N = N;
        return this;
    }

    @Override
    public Mutex Chatter(Chatter chatter) {
        this.chatter = chatter;
        new Handler(this).start(); // Thread to receive messages

        return this;
    }

    public void broadcastMsg(String msg, int timestamp) {
        Dataframe dataframe = new Dataframe()
                .source(ID)
                .timestamp(timestamp)
                .message(msg)
                .destination(Dataframe.BROADCAST);

        chatter.send(dataframe.toString());
    }

    public synchronized void handleMsg(Dataframe df) {
        Dataframe dataframe;
        int timeStamp = df.getTimestamp();
        int src = df.getSrc();
        String tag = df.getMessage();
        c.receiveAction(src, timeStamp);
        if (df.getMessage().equals("request")) {
            if ((myts == Integer.MAX_VALUE) || (timeStamp < myts) || (timeStamp == myts) && (src < ID)) {
                dataframe = new Dataframe(ID, myts, "okay", src);
                chatter.send(dataframe.toString());
            } else {
                pendingQ.add(src);
            }
        } else if (df.getMessage().equals("okay")) {
            numOkay++;
            if (numOkay == N - 1) {
                notify();
            } else if(numOkay == N)
                running = false;
        }
    }

    private class Handler extends Thread {
        //constructor
        public Handler(RicartAndAgrawala RaA) {
            //this.RaA = RaA;
        }

        @Override
        public void run() {
            while(true) {
                if(running) {
                    String line = chatter.read();
                    if(line.equals(Dataframe.STOP)) {
                        running = false;
                        continue;
                    }
                    // if received message is "do" meaning that comes from heavyweight, continue
                    Dataframe aux = new Dataframe(line);
                    handleMsg(aux);
                }
            }
        }

    }
}