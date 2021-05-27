package vision.silpo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static vision.util.CheckUtils.extractContentTillSymbols;
import static vision.util.CheckUtils.extractItemPerPriceMap;
import static vision.util.CheckUtils.extractPrices;
import static vision.util.CheckUtils.moveIteratorToGivenSymbols;
import static vision.util.CheckUtils.removeNotNeededItemsByPredicate;
import static vision.util.CheckUtils.removeSingleItems;

public class SilpoParser {
    private static final List<String> CONTENT_END_SYMBOLS = new ArrayList<>();
    private static final Predicate<String> X_PREDICATE = s -> s.contains("X");
    private static final Predicate<String> REMOVE_PREDICATE = s -> !s.contains(" A") && s.contains(",") && s.length() < 7;

    static {
        CONTENT_END_SYMBOLS.add("Спец");
        CONTENT_END_SYMBOLS.add("акції");
    }

    public static Map<String, String> parseSilpoChain(List<String> lines) {
        lines = removeSingleItems(lines);

        lines = removeNotNeededItemsByPredicate(lines, X_PREDICATE.negate());

        Iterator<String> iterator = lines.iterator();

        moveIteratorToGivenSymbols(iterator);

        List<String> content = extractContentTillSymbols(iterator, CONTENT_END_SYMBOLS);

        List<String> prices = extractPrices(content);

        content.removeAll(prices);

        content = removeNotNeededItemsByPredicate(content, REMOVE_PREDICATE.negate());

        if (prices.size() != content.size()) {
            throw new RuntimeException("Number of prices and item names if not match for Silpo");
        }

        return extractItemPerPriceMap(content, prices);
    }

}
