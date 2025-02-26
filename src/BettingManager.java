import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BettingManager {
    private static final int MAX_TOP_STAKES = 20;
    private static final ConcurrentHashMap<Integer, PriorityQueue<StakeEntry>> betOffers = new ConcurrentHashMap<>();

    private record StakeEntry(int customerId, int stake) {}

    public static synchronized void addStake(int betOfferId, int customerId, int stake) {
        betOffers.putIfAbsent(betOfferId, new PriorityQueue<>((Comparator.comparingInt(o -> o.stake))));
        PriorityQueue<StakeEntry> stakesHeap = betOffers.get(betOfferId);
        StakeEntry newStake = new StakeEntry(customerId, stake);

        boolean customerExists = false;
        for (StakeEntry entry : stakesHeap) {
            if (entry.customerId == customerId) {
                customerExists = true;
                if (entry.stake < stake) {
                    stakesHeap.remove(entry);
                    stakesHeap.offer(newStake);
                }
                break;
            }
        }

        if (!customerExists) {
            stakesHeap.offer(newStake);
        }

        if (stakesHeap.size() > MAX_TOP_STAKES) {
            stakesHeap.remove();
        }
    }

    public static List<String> getHighStakes(int betOfferId) {
        PriorityQueue<StakeEntry> stakesHeap = betOffers.get(betOfferId);

        if (stakesHeap == null || stakesHeap.isEmpty()) {
            return Collections.emptyList();
        }

        List<StakeEntry> sortedStakes = new ArrayList<>(stakesHeap);
        sortedStakes.sort((o1, o2) -> Integer.compare(o2.stake(), o1.stake()));

        List<String> result = new ArrayList<>();
        for (StakeEntry entry : sortedStakes) {
            result.add(entry.customerId() + "=" + entry.stake());
        }
        return result;
    }
}
