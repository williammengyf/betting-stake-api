import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouterHandler implements HttpHandler {
    private static final Pattern SESSION_PATTERN = Pattern.compile("^/(\\d+)/session$");
    private static final Pattern STAKE_PATTERN = Pattern.compile("^/(\\d+)/stake\\?sessionkey=([a-zA-Z0-9]+)$");
    private static final Pattern HIGHSTAKES_PATTERN = Pattern.compile("^/(\\d+)/highstakes$");

    private final SessionHandler sessionHandler = new SessionHandler();
    private final StakeHandler stakeHandler = new StakeHandler();
    private final HighStakesHandler highStakesHandler = new HighStakesHandler();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String path = exchange.getRequestURI().getPath() + (query != null ? "?" + query : "");

        Matcher sessionMatcher = SESSION_PATTERN.matcher(path);
        Matcher stakeMatcher = STAKE_PATTERN.matcher(path);
        Matcher highStakesMatcher = HIGHSTAKES_PATTERN.matcher(path);

        if (sessionMatcher.matches()) {
            sessionHandler.handle(exchange);
        } else if (stakeMatcher.matches()) {
            stakeHandler.handle(exchange);
        } else if (highStakesMatcher.matches()) {
            highStakesHandler.handle(exchange);
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }
}
