import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
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
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            InputStream requestBody = exchange.getRequestBody();
            String stakeStr = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8).trim();
            int stake = Integer.parseInt(stakeStr);
            int customerId = SessionManager.getCustomerId(sessionKey);
            if (customerId == -1) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            BettingManager.addStake(betOfferId, customerId, stake);
            exchange.sendResponseHeaders(200, -1);
        } else {
            exchange.sendResponseHeaders(400, -1);
        }
    }
}
