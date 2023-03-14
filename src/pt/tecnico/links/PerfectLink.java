package pt.tecnico.links;

import java.util.ArrayList;
import java.util.List;

import pt.tecnico.ibft.HDLProcess;
import pt.tecnico.messages.LinkMessage;

// Perfect point to point link using Stubborn links
public class PerfectLink extends Channel {
    
    private StubbornLink _slInstance;
    private List<LinkMessage> _delivered;

    public PerfectLink(HDLProcess p) {
        super(p);
        _slInstance = new StubbornLink(p);
        _delivered = new ArrayList<LinkMessage>();
    }


    public void send(LinkMessage message) throws IllegalStateException {
        System.err.printf("[%s] PL: Sending message %s\n", this.owner, message);
        _slInstance.send(message);
    }


    public LinkMessage deliver() throws IllegalStateException, InterruptedException {
        LinkMessage message;

        // Wait for a message that was not delivered yet
        do {
            message = _slInstance.deliver();
            System.err.printf("[%s] PL: Received message %s\n", this.owner, message);
        } while (_delivered.contains(message));

        assert(message != null);

        System.err.printf("[%s] PL: Message not yet delivered! Delivering message %d ...\n", this.owner, message.getId());
        _delivered.add(message);
        return message;
    }


    public void close() {
        _slInstance.close();
    }

}
