import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BettingManager {
    private static final ConcurrentHashMap<Integer, Map<Integer, List<Integer>>> betOffers = new ConcurrentHashMap<>();

    private record StakeEntry(int customerId, int stake) {}

    public static synchronized void addStake(int betOfferId, int customerId, int stake) {
        betOffers.putIfAbsent(betOfferId, new ConcurrentHashMap<>());
        betOffers.get(betOfferId).putIfAbsent(customerId, new ArrayList<>());
        betOffers.get(betOfferId).get(customerId).add(stake);
    }

    public static List<String> getHighStakes(int betOfferId) {
        if (!betOffers.containsKey(betOfferId)) {
            return Collections.emptyList();
        }

        List<StakeEntry> stakeList = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : betOffers.get(betOfferId).entrySet()) {
            int customerId = entry.getKey();
            int maxStake = Collections.max(entry.getValue());
            stakeList.add(new StakeEntry(customerId, maxStake));
        }

        stakeList.sort((o1, o2) -> Integer.compare(o2.stake, o1.stake));

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(20, stakeList.size()); i++) {
            StakeEntry entry = stakeList.get(i);
            result.add(entry.customerId + "=" + entry.stake);
        }

        return result;
    }
}
