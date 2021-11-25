package communication;

/* Class to define and operate messages sent through sockets */
public class Dataframe {
    public final static int BROADCAST = -1;
    public final static String
            CLOSE = "close",
            ACK = "ack",
            RELEASE = "release",
            REQUEST = "request",
            STOP = "stop",
            DONE = "done",
            START = "start";
    private int src;
    private int timestamp;
    private String message;
    private int dest; // src,timestamp,message,dest

    private static final String
            INVALID_FRAME = "ERROR. Invalid dataframe <%s>. It must have 4 comma separated values.\n";

    public Dataframe(int src, int timestamp, String message, int dest) {
        this.src = src;
        this.timestamp = timestamp;
        this.message = message;
        this.dest = dest;
    }

    public Dataframe() { }

    public Dataframe(String frame) {
        assert frame != null;
        String[] data = frame.split(",");
        if(data.length != 4)
            throw new RuntimeException(String.format(INVALID_FRAME, frame));

        this.src = Integer.parseInt(data[0]);
        this.timestamp = Integer.parseInt(data[1]);
        this.message = data[2];
        this.dest = Integer.parseInt(data[3]);
    }

    public static Dataframe parse(int src, int timestamp, String message, int dest) {
        return new Dataframe(src, timestamp, message, dest);
    }

    public String toString() {
        // Comma separated values
        return src + "," + timestamp + "," + message + "," + dest;
    }

    public int getDest() {
        return dest;
    }

    public Dataframe destination(int dest) {
        this.dest = dest;
        return this;
    }

    public int getSrc() {
        return src;
    }

    public Dataframe source(int src) {
        this.src = src;
        return this;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Dataframe timestamp(int timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Dataframe message(String message) {
        this.message = message;
        return this;
    }
}
