package mutualexclusion;

import chronos.DirectClock;
import communication.Chatter;
import communication.Dataframe;

import javax.xml.crypto.Data;

public class Lamport implements Mutex {
    DirectClock directClock;
    int[] queue; // request queue
    int ID;
    Chatter chatter;

    public Lamport(int ID, int N, Chatter chatter) {
        this.ID = ID;
        this.chatter = chatter;
        directClock = new DirectClock(N, ID);
        queue = new int[N];
        for(int i=0; i<N; i++) {
            queue[i] = Integer.MAX_VALUE;
        }
    }

    @Override
    public synchronized void requestCS() {
        directClock.tick();
        queue[ID] = directClock.getValue(ID);
        broadcastMsg("request", queue[ID]);
        while(!okayCS()) {
            // TODO: Define myWait()
            //myWait();
        }
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

    private boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
        if(entry2 == Integer.MAX_VALUE) return false;
        boolean isEntryGreater = entry1 > entry2,
                isEqualTo = entry1 == entry2,
                isIDGreater = pid1 > pid2;

        return (isEntryGreater || (isEqualTo && isIDGreater) );
    }

    public synchronized void handleMsg(Dataframe df, int src, String tag) {
        int timeStamp = df.getTimestamp();
        directClock.receiveAction(src, timeStamp);
        if(df.getMessage().equals("request")) {
            queue[src] = timeStamp;
            chatter.send(Dataframe.parse(ID, timeStamp, "ack", src).toString());
        }
    }
    public void waitAck() {

    }

    public void broadcastMsg(String msg, int timestamp){
        Dataframe dataframe = new Dataframe(ID, timestamp, msg, Dataframe.BROADCAST);
        chatter.send(dataframe.toString());
        // TODO: Implement waitAck and call it here to wait for every process to send ACK message
    }
}
