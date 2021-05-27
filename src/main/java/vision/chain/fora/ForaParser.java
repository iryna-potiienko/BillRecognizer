package vision.fora;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static vision.util.CheckUtils.extractContentTillSymbols;
import static vision.util.CheckUtils.extractItemPerPriceMap;
import static vision.util.CheckUtils.extractPrices;
import static vision.util.CheckUtils.moveIteratorToGivenSymbols;
import static vision.util.CheckUtils.removeSingleItems;

public class ForaParser {
    private static final List<String> CONTENT_END_SYMBOLS = new ArrayList<>();

    static {
        CONTENT_END_SYMBOLS.add("КОРЕКЦІЯ");
    }

    public static Map<String, String> parseForaChain(List<String> lines) {
        lines = removeSingleItems(lines);

        Iterator<String> iterator = lines.iterator();

        moveIteratorToGivenSymbols(iterator);

        List<String> content = extractContentTillSymbols(iterator, CONTENT_END_SYMBOLS);

        List<String> prices = extractPrices(content);

        content.removeAll(prices);

        if (prices.size() != content.size()) {
            throw new RuntimeException("Number of prices and item names if not match for ATB");
        }

        return extractItemPerPriceMap(content, prices);
    }
}
