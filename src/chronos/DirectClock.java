package chronos;

import jdk.jshell.execution.Util;

import static java.lang.Math.max;

public class DirectClock {
    public int[] clock;
    private final int myId;

    public DirectClock(int numProc, int id) {
        myId = id;
        clock = new int[numProc];
        for (int i = 0; i < numProc; i++) {
            clock[i] = 0;
        }
        clock[myId] = 1;
    }

    public int getValue (int i) {
        return clock[i];
    }
    public void tick () {
        clock[myId]++;
    }
    public void sendAction () {
        //sentValue = clock[myID];
        tick();
    }
    public void receiveAction (int sender, int sentValue) {
        clock[sender] = max(clock[sender], sentValue);
        clock[myId] = max(clock[myId], sentValue) + 1;
    }
}
