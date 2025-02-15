import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final ConcurrentHashMap<Integer, String> sessions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> sessionExpiry = new ConcurrentHashMap<>();
    private static final long SESSION_DURATION = 10 * 60 * 1000;

    public static synchronized String getSession(int customerId) {
        long currentTime = System.currentTimeMillis();
        String existingSession = sessions.get(customerId);

        if (existingSession != null && sessionExpiry.get(existingSession) > currentTime) {
            return existingSession;
        }

        String newSession = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 7);
        sessions.put(customerId, newSession);
        sessionExpiry.put(newSession, currentTime + SESSION_DURATION);
        return newSession;
    }

    public static boolean isValidSession(String session) {
        return sessionExpiry.containsKey(session) && sessionExpiry.get(session) > System.currentTimeMillis();
    }
}
