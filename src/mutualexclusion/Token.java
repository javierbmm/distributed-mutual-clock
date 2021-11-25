package mutualexclusion;

import communication.Chatter;
import communication.Dataframe;

import javax.xml.crypto.Data;
import java.util.LinkedList;

public class Token implements Mutex {
    boolean haveToken;
    final int leader;
    private final LinkedList<Integer> pendingQ = new LinkedList<>();
    private Chatter chatter;
    private int ID;

    public Token(int ID, int leader, Chatter chatter) {
        this.leader = leader;
        this.chatter = chatter;
        this.ID = ID;
        haveToken = (ID == leader);
        new Handler().start();
    }

    @Override
    public synchronized void requestCS() {
        sendMsg(leader, Dataframe.REQUEST);
        while(!haveToken){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void releaseCS() {
        sendMsg(leader, Dataframe.RELEASE);
        haveToken = false;
    }

    public synchronized void handleMsg(Dataframe df) {
        String msg = df.getMessage();
        switch (msg) {
            case Dataframe.REQUEST:
                if (!haveToken) {
                    sendMsg(df.getSrc(), Dataframe.ACK);
                    haveToken = false;
                } else
                    pendingQ.add(df.getSrc());
                break;
            case Dataframe.RELEASE:
                if (!pendingQ.isEmpty()) {
                    int pid = pendingQ.removeFirst();
                    if(pendingQ.isEmpty()) {
                        haveToken = true;
                    } else
                        sendMsg(pid, Dataframe.ACK);
                } else
                    haveToken = true;
                break;
            case Dataframe.ACK:
                haveToken = true;
                break;
        }
        notify();
    }

    @Override
    public boolean okayCS() {
        return false;
    }

    @Override
    public Mutex ID(int ID) {
        return null;
    }

    @Override
    public Mutex N(int N) {
        return null;
    }

    @Override
    public Mutex Chatter(Chatter chatter) {
        return null;
    }

    private void sendMsg(int dest, String msg) {
        Dataframe df = Dataframe.parse(ID,0, msg, dest);
        chatter.send(df.toString());
    }

    private class Handler extends Thread {
        //constructor
        public Handler() {
        }

        @Override
        public void run() {
            while(true) {
                String line = chatter.read();
                Dataframe aux = new Dataframe(line);
                handleMsg(aux);
            }
        }

    }

}
