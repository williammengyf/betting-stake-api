import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StakeHandler implements HttpHandler {
    private static final Pattern STAKE_PATTERN = Pattern.compile("/(\\d+)/stake\\?sessionkey=([a-zA-Z0-9]+)");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath() + "?" + exchange.getRequestURI().getQuery();
        Matcher matcher = STAKE_PATTERN.matcher(path);

        if (exchange.getRequestMethod().equalsIgnoreCase("POST") && matcher.matches()) {
            int betOfferId = Integer.parseInt(matcher.group(1));
            String sessionKey = matcher.group(2);

            if (!SessionManager.isValidSession(sessionKey)) {
                String error = "403 Forbidden: Invalid session key.";
                exchange.sendResponseHeaders(403, error.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(error.getBytes());
                }
                return;
            }

            InputStream requestBody = exchange.getRequestBody();
            String stakeStr = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8).trim();
            int stake = Integer.parseInt(stakeStr);
            int customerId = SessionManager.getCustomerId(sessionKey);
            if (customerId == -1) {
                String error = "403 Forbidden: Invalid session key.";
                exchange.sendResponseHeaders(403, error.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(error.getBytes());
                }
                return;
            }

            BettingManager.addStake(betOfferId, customerId, stake);
            String response = "betOfferId=" + betOfferId + ",customerId=" + customerId + ",stake=" + stake;
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            String error = "405 Method Not Allowed: Only POST method is supported.";
            exchange.sendResponseHeaders(405, error.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        } else {
            String error = "400 Bad Request: Invalid request format.";
            exchange.sendResponseHeaders(400, error.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        }
    }
}
