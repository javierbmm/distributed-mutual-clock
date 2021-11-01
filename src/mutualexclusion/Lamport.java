package mutualexclusion;

import chronos.DirectClock;
import communication.Chatter;
import communication.Dataframe;

import javax.xml.crypto.Data;

public class Lamport implements Mutex {
    private DirectClock directClock;
    private int[] queue; // request queue
    private int ID;
    private Chatter chatter;
    private volatile int ackCounter;
    private volatile boolean hasReleased = false;

    public Lamport(int ID, int N, Chatter chatter) {
        this.ID = ID;
        this.chatter = chatter;
        directClock = new DirectClock(N, ID);
        queue = new int[N];
        for(int i=0; i<N; i++) {
            queue[i] = Integer.MAX_VALUE;
        }
    }

    public Lamport() {
    }

    @Override
    public synchronized void requestCS() {
        directClock.tick();
        queue[ID] = directClock.getValue(ID);
        broadcastMsg("request", queue[ID]);
        waitAck(); // Waiting for everyone else to give me their 'ack' and timestamp.
        while(!okayCS()) {
            myWait();
        }
    }

    private void myWait() {
        String line = chatter.read();
        Dataframe aux = new Dataframe(line);
        handleMsg(aux);
    }

    @Override
    public void releaseCS() {
        queue[ID] = Integer.MAX_VALUE;
        broadcastMsg("release", directClock.getValue(ID));
    }

    @Override
    public boolean okayCS() {
        for(int j=0; j < queue.length; j++) {
            if(isGreater(queue[ID], ID, queue[j], j))
                return false;
            if(isGreater(queue[ID], ID, directClock.getValue(j), j))
                return false;
        }

        return true;
    }

    @Override
    public Mutex ID(int ID) {
        this.ID = ID;
        return this;
    }

    @Override
    public Mutex N(int N) {
        directClock = new DirectClock(N, ID);
        queue = new int[N];
        for(int i=0; i<N; i++) {
            queue[i] = Integer.MAX_VALUE;
        }

        return this;
    }

    @Override
    public Mutex Chatter(Chatter chatter) {
        this.chatter = chatter;

        return this;
    }

    private boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
        if(entry2 == Integer.MAX_VALUE) return false;
        boolean isEntryGreater = entry1 > entry2,
                isEqualTo = entry1 == entry2,
                isIDGreater = pid1 > pid2;

        return (isEntryGreater || (isEqualTo && isIDGreater) );
    }

    public synchronized void handleMsg(Dataframe df) {
        // todo this might be inside a thread
        int timeStamp = df.getTimestamp();
        int src = df.getSrc();
        String msg = df.getMessage();
        directClock.receiveAction(src, timeStamp);
        System.out.println("Received this message: <"+df+">");
        switch (msg) {
            case Dataframe.REQUEST -> {
                queue[src] = timeStamp;
                chatter.send(Dataframe.parse(ID, timeStamp, "ack", src).toString());
            }
            case Dataframe.ACK -> ackCounter++;
            case Dataframe.RELEASE -> hasReleased = true;
        }
    }

    public void waitAck() {
        ackCounter = 0;
        while(ackCounter < queue.length-1) {
            myWait();
            System.out.println("ack: "+ackCounter);
        }
    }

    public void broadcastMsg(String msg, int timestamp){
        Dataframe dataframe = new Dataframe()
                .source(ID)
                .timestamp(timestamp)
                .message(msg)
                .destination(Dataframe.BROADCAST);

        chatter.send(dataframe.toString());
    }
}
