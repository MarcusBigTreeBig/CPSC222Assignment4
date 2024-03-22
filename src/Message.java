public class Message {

    private Node sender;
    private Node receiver;
    private int leaderID;
    private Edge smallest;
    private MessageType type;

    public Message () {

    }

    public static Message createMergeMessage (Node n) {
        Message m = new Message();
        m.type = MessageType.MERGE;
        m.sender = n;
        return m;
    }

    public static Message createMWOEBroadCast () {
        Message m = new Message();
        m.type = MessageType.MWOE_BROADCAST;
        return m;
    }

    public static Message createLeaderBroadCast (Node sender, int leaderID) {
        Message m = new Message();
        m.type = MessageType.LEADER_BROADCAST;
        m.sender = sender;
        m.leaderID = leaderID;
        return m;
    }

    public Edge getSmallest() {
        return smallest;
    }

    public MessageType getType() {
        return type;
    }

    public int getLeaderID() {
        return leaderID;
    }

    public Node getReceiver() {
        return receiver;
    }

    public Node getSender() {
        return sender;
    }

    public void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }

    public void setReceiver(Node receiver) {
        this.receiver = receiver;
    }

    public void setSender(Node sender) {
        this.sender = sender;
    }

    public void setSmallest(Edge smallest) {
        this.smallest = smallest;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
