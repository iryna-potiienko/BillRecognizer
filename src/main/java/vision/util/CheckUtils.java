package vision;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CheckUtils {

    public static List<String> removeNotNeededItems(List<String> items, List<String> itemsToRemove) {
        return items.stream()
                .filter(s -> itemsToRemove.stream().noneMatch(s::contains))
                .collect(Collectors.toList());
    }

    public static List<String> extractPrices(List<String> items) {
        return items.stream().filter(s -> {
            var replaced = s.replace(" A", "");

            return replaced.length() == 4 || replaced.length() == 5;
        }).collect(Collectors.toList());
    }

    public static void moveToGivenSymbols(Iterator<String> iterator, List<String> symbols) {
        while (iterator.hasNext()) {
            var line = iterator.next();

            if (line.contains("Чек") || line.contains("YEK")) {
                break;
            }

            if (!iterator.hasNext()) {
                throw new RuntimeException("Failed to find start of content");
            }
        }
    }
}
