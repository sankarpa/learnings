package client;

import com.thoughtworks.greetor.GreetorGrpc;
import com.thoughtworks.greetor.GreetorRequest;
import com.thoughtworks.greetor.GreetorResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GreetorClient {

    private static final Logger logger = Logger.getLogger(GreetorClient.class.getName());

    private final ManagedChannel channel;
    private GreetorGrpc.GreetorBlockingStub blockingStub;

    public GreetorClient(String hostname, int port) {
        channel = ManagedChannelBuilder.forAddress(hostname, port)
                .usePlaintext(true)
                .build();
        blockingStub = GreetorGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws InterruptedException {
        GreetorClient client = new GreetorClient("localhost", 42420);
        String name = args.length > 0 ? args[0] : "unknown";

        try {
            client.greet(name);
        } finally {
            client.shutdown();
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
    }

    public void greet(String name) {
        GreetorRequest greetorRequest = GreetorRequest.newBuilder()
                .setName(name)
                .build();
        GreetorResponse greetorResponse = blockingStub.greetPerson(greetorRequest);
        System.out.print(greetorResponse.toString());
    }

}
