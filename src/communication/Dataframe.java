package communication;

/* Class to define and operate messages sent through sockets */
public class Dataframe {
    public final static int BROADCAST = -1;
    private int src;
    private int timestamp;
    private String message;
    private int dest;

    private static final String
            INVALID_FRAME = "ERROR. Invalid dataframe <%s>. It must have 3 comma separated values\n.";

    public Dataframe(int src, int timestamp, String message, int dest) {
        this.src = src;
        this.timestamp = timestamp;
        this.message = message;
        this.dest = dest;
    }

    public Dataframe(String frame) {
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
        return src + "," + timestamp + "," + message;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
