package mutualexclusion;

public interface Mutex {
    public void requestCS();

    public void releaseCS();

    public boolean okayCS();
}
