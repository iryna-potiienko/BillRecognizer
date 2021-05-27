package vision.atb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static vision.util.CheckUtils.extractContentTillSymbols;
import static vision.util.CheckUtils.extractItemPerPriceMap;
import static vision.util.CheckUtils.extractMatchedSymbols;
import static vision.util.CheckUtils.extractPrices;
import static vision.util.CheckUtils.moveIteratorToGivenSymbols;
import static vision.util.CheckUtils.removeNotNeededItemsByPredicate;
import static vision.util.CheckUtils.removeSingleItems;

public class ATBParser {
    private static final List<String> MATCH_SYMBOLS = new ArrayList<>();
    private static final List<String> CONTENT_END_SYMBOLS = new ArrayList<>();
    private static final Predicate<String> SALES_PREDICATE = s -> s.contains("-");

    static {
        MATCH_SYMBOLS.add("Паk");
        MATCH_SYMBOLS.add(" г");
        MATCH_SYMBOLS.add("гат");
        MATCH_SYMBOLS.add(" кг");
        MATCH_SYMBOLS.add(" шт");
        MATCH_SYMBOLS.add("Ci");

        CONTENT_END_SYMBOLS.add("CUMA");
        CONTENT_END_SYMBOLS.add("СУМА");
        CONTENT_END_SYMBOLS.add("СУNА");
        CONTENT_END_SYMBOLS.add("CYMA");
        CONTENT_END_SYMBOLS.add("CUMA");
    }

    public static Map<String, String> parseATBChain(List<String> lines) {
        lines = removeSingleItems(lines);

        Iterator<String> iterator = lines.iterator();

        moveIteratorToGivenSymbols(iterator);

        List<String> content = extractContentTillSymbols(iterator, CONTENT_END_SYMBOLS);

        List<String> prices = extractPrices(content);

        prices = removeNotNeededItemsByPredicate(prices, SALES_PREDICATE.negate());

        List<String> matchedSymbols = extractMatchedSymbols(content, MATCH_SYMBOLS);

        if (prices.size() != matchedSymbols.size()) {
            throw new RuntimeException("Number of prices and item names if not match for ATB");
        }

        return extractItemPerPriceMap(matchedSymbols, prices);
    }
}
