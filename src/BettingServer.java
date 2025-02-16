import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class BettingServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);

        server.createContext("/session", new SessionHandler());
        server.createContext("/stake", new StakeHandler());
        server.createContext("/highstakes", new HighStakesHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8001");
    }
}
