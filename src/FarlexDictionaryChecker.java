import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FarlexDictionaryChecker extends AbstractDictionaryChecker {
    private final String dictionaryURL = "https://www.thefreedictionary.com/";

    public FarlexDictionaryChecker(List<String> words) throws IOException {
        super(words);
    }

    boolean isReal(String word) {

        try {
            org.jsoup.nodes.Document document = Jsoup.connect(this.dictionaryURL + word).userAgent("Mozilla").get();
            Element mainTxt = document.getElementById("MainTxt");
            List<TextNode> isNotAvailable = mainTxt.childNodes().stream().filter(TextNode.class::isInstance).map(TextNode.class::cast)
                    .filter(textNode -> textNode.getWholeText().contains("Sorry, no results for")).collect(Collectors.toList());
            if (isNotAvailable.size() > 0) return false;
            return true;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}
