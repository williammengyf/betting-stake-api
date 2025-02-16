import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighStakesHandler implements HttpHandler {
    private static final Pattern HIGH_STAKES_PATTERN = Pattern.compile("/(\\d+)/highstakes");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Matcher matcher = HIGH_STAKES_PATTERN.matcher(path);

        if (exchange.getRequestMethod().equalsIgnoreCase("GET") && matcher.matches()) {
            int betOfferId = Integer.parseInt(matcher.group(1));
            List<String> highStakes = BettingManager.getHighStakes(betOfferId);
            String response = String.join(",", highStakes);
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(400, -1);
        }
    }
}
