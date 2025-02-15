import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BettingManager {
    private static final ConcurrentHashMap<Integer, Map<Integer, Integer>> stakes = new ConcurrentHashMap<>();

    public static synchronized void addStake(int betOfferId, int customerId, int stake) {
        stakes.putIfAbsent(betOfferId, new ConcurrentHashMap<>());
        int currentStake = stakes.get(betOfferId).getOrDefault(customerId, 0);
        stakes.get(betOfferId).put(customerId, Math.max(currentStake, stake));
    }
}
