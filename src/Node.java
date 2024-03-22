import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Node implements Runnable{

    private int id;
    private BlockingQueue<Message> messages;
    private ArrayList<Edge> edges;
    private int leaderID;
    private int level;

    public Node(int id){
        messages = new ArrayBlockingQueue<>(20);
        edges = new ArrayList<Edge>();
        this.id = id;
    }

    public synchronized void receiveMessage(Message mess){
        messages.add(mess);
    }

    public synchronized void addEdge (Edge e) {
        edges.add(e);
    }
    public synchronized void removeEdge (Edge e) {
        edges.remove(e);
    }

    public ArrayList<Edge> getEdges () {
        return edges;
    }

    public int getID () {
        return id;
    }

    @Override
    public String toString () {
        return ""+getID();
    }

    public BlockingQueue<Message> getMessages () {
        return messages;
    }

    public void setMessages (BlockingQueue<Message> messages) {
        this.messages = messages;
    }

    @Override
    public void run() {

        //initialize itself to start MST
        for (Edge e: edges) {
            e.setInUse(false);
        }
        level = 0;
        leaderID = id;
        Message sending;

        //find MWOE for single node
        Edge mwoe = edges.get(0);
        for (Edge e: edges) {
            if (e.getWeight() < mwoe.getWeight()) {
                mwoe = e;
            }
        }
        System.out.println("ID: " + id + " MWOE: " + mwoe);

        //request merge with another single node component
        sending = Message.createMergeMessage(this);
        mwoe.getNode().receiveMessage(sending);

        boolean running = true;
        while (running) {
            if (messages.peek() != null) {//read top message

                //take message if there is one to take
                Message receiving = null;
                try {
                    receiving = messages.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (receiving != null) {//should make sure a message received before doing anything with it

                    //leader change
                    if (receiving.getType() == MessageType.LEADER_BROADCAST) {
                        leaderID = receiving.getLeaderID();
                        sending = Message.createLeaderBroadCast(this, leaderID);
                        for (Edge e: edges) {
                            if (e.isInUse() && receiving.getSender() != e.getNode()) {//send to all other nodes in component
                                e.getNode().receiveMessage(sending);
                            }
                        }
                    }

                    //TODO: MWOE search
                    //MWOE broadcast
                    if (receiving.getType() == MessageType.MWOE_BROADCAST) {
                        //TODO: need way to convert to convergecast
                        sending = Message.createMWOEBroadCast();
                        for (Edge e: edges) {
                            if (e.isInUse() && receiving.getSender() != e.getNode()) {//send to all other nodes in component
                                e.getNode().receiveMessage(sending);
                            }
                        }
                    }

                    //TODO: MWOE convergecast

                    //merge with another single node component
                    //should generalize past single node
                    if (receiving.getType() == MessageType.MERGE && mwoe.getNode() == receiving.getSender()) {//merge if both ends agree
                        mwoe.setInUse(true);
                        if (this.getID() > mwoe.getNode().getID()) {
                            leaderID = this.getID();
                        } else {
                            leaderID = mwoe.getNode().getID();
                        }
                        System.out.println("Added: " + mwoe);
                        //TODO: broadcast leader change

                        //broadcast MWOE search
                        if (id == leaderID) {//TODO: I think this should work
                            this.receiveMessage(Message.createLeaderBroadCast(this, leaderID));
                        }

                    }//TODO: what happens in else clause?

                }

            }
        }

    }

}
