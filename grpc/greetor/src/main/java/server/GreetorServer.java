package server;

import com.thoughtworks.greetor.GreetorGrpc;
import com.thoughtworks.greetor.GreetorRequest;
import com.thoughtworks.greetor.GreetorResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.logging.Logger;

public class GreetorServer {

    private static final Logger logger = Logger.getLogger(GreetorServer.class.getName());

    private int port = 42420;
    private Server server;

    public static void main(String[] args) throws Exception {
        logger.info("Server startup. Args = " + Arrays.toString(args));
        final GreetorServer greetorServer = new GreetorServer();

        greetorServer.start();
        greetorServer.blockUntilShutdown();
    }

    private void start() throws Exception {
        logger.info("Starting the grpc server");

        server = ServerBuilder.forPort(port)
                .addService(new GreetorServiceImpl())
                .build()
                .start();

        logger.info("Server started. Listening on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** JVM is shutting down. Turning off grpc server as well ***");
            GreetorServer.this.stop();
            System.err.println("*** shutdown complete ***");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    private class GreetorServiceImpl extends GreetorGrpc.GreetorImplBase {

        @Override
        public void greetPerson(GreetorRequest request, StreamObserver<GreetorResponse> responseObserver) {

            String name = request.getName();

            String greetings = "Hello" + name;

            GreetorResponse greets = GreetorResponse.newBuilder()
                    .setGreetings(greetings)
                    .build();

            responseObserver.onNext(greets);
            responseObserver.onCompleted();
        }
    }

}
