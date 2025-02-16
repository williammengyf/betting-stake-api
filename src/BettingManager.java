import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BettingManager {
    private static final ConcurrentHashMap<Integer, Map<Integer, Integer>> betOffers = new ConcurrentHashMap<>();

    public static synchronized void addStake(int betOfferId, int customerId, int stake) {
        betOffers.putIfAbsent(betOfferId, new ConcurrentHashMap<>());
        int currentStake = betOffers.get(betOfferId).getOrDefault(customerId, 0);
        betOffers.get(betOfferId).put(customerId, Math.max(currentStake, stake));
    }

    public static List<String> getHighStakes(int betOfferId) {
        if (!betOffers.containsKey(betOfferId)) {
            return Collections.emptyList();
        }

        List<Map.Entry<Integer, Integer>> stakeList = new ArrayList<>(betOffers.get(betOfferId).entrySet());

        stakeList.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));

        List<String> result = new ArrayList<>();
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : stakeList) {
            result.add(entry.getKey() + "=" + entry.getValue());
            count++;
            if (count == 20) break;
        }

        return result;
    }
}
