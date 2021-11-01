package mutualexclusion;

import communication.Chatter;

public interface Mutex {
    public void requestCS();

    public void releaseCS();

    public boolean okayCS();

    public Mutex ID(int ID);

    public Mutex N(int N);

    public Mutex Chatter(Chatter chatter);
}
