package pt.ulisboa.tecnico.sec.links;

import java.net.SocketTimeoutException;
import java.security.PublicKey;

import pt.ulisboa.tecnico.sec.ibft.HDLProcess;
import pt.ulisboa.tecnico.sec.messages.LinkMessage;

// Authenticated Perfect point to point link using Perfect links
public class AuthenticatedPerfectLink extends Channel {

    private PerfectLink plInstance;

    public AuthenticatedPerfectLink(HDLProcess p) {
        super(p);
        plInstance = new PerfectLink(p);
    }

    public void send(LinkMessage message) throws IllegalStateException, InterruptedException {
        System.err.printf("[%s] APL: Signing message %s\n", this.owner, message);
        message.getMessage().signMessage(this.owner.getPrivateKey());
        // TODO: Fix any process can use: KeyHandler.getPrivateKey(otherID) to get other private key;
        plInstance.send(message);
    }

    public LinkMessage deliver() throws IllegalStateException, InterruptedException, SocketTimeoutException {
        LinkMessage message = null;
        PublicKey senderPubKey = null;

        // Wait for a message that was not delivered yet with valid signature
        do {
            message = plInstance.deliver();
            senderPubKey = message.getSender().getPublicKey();
            System.err.printf("[%s] APL: Received message %s\n", this.owner, message);
        } while (!message.getTerminate() && !message.getMessage().hasValidSignature(senderPubKey));

        assert(message != null);

        System.err.printf("[%s] APL: Message signature verified! Delivering message %d ...\n", this.owner, message.getId());
        return message;
    }

    public void close() {
        plInstance.close();
    }

}
