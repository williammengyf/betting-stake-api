import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionHandler implements HttpHandler {
    private static final Pattern SESSION_PATTERN = Pattern.compile("/(\\d+)/session");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Matcher matcher = SESSION_PATTERN.matcher(path);

        if (exchange.getRequestMethod().equalsIgnoreCase("GET") && matcher.matches()) {
            int customId = Integer.parseInt(matcher.group(1));
            String session = SessionManager.getSession(customId);
            exchange.sendResponseHeaders(200, session.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(session.getBytes());
            }
        } else if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            String error = "405 Method Not Allowed: Only GET method is supported.";
            exchange.sendResponseHeaders(405, error.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        } else {
            String error = "405 Method Not Allowed: Only GET method is supported.";
            exchange.sendResponseHeaders(405, error.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        }
    }
}
