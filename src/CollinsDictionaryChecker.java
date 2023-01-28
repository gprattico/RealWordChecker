import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollinsDictionaryChecker extends AbstractDictionaryChecker {

    private final String dictionaryURL = "https://www.collinsdictionary.com/dictionary/italian-english/";

    AtomicReference<List<String>> suggestedWords = new AtomicReference<>(new ArrayList<>());

    public CollinsDictionaryChecker(List<String> words) throws IOException {
        super(words);
    }

    @Override
    boolean isReal(String word) {
        try {
            org.jsoup.nodes.Document document = Jsoup.connect(this.dictionaryURL + word).userAgent("Mozilla").get();
            Elements cB1 = document.getElementsByClass("cB").get(0).getElementsContainingText("Sorry, no results for");
            if (cB1.size() > 0) {
                collectSuggestions(document);
                return false;
            }

            return true;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }

    private void collectSuggestions(Document document) {
        Elements listDiv = document.getElementsByClass("columns2");
        List<Node> htmlListOfWords = listDiv.get(0).childNodes().stream().filter(Element.class::isInstance).collect(Collectors.toList());

        for (Node tempNode : htmlListOfWords) {
            Node textNode = tempNode.childNode(0).childNode(0);
            String suggestedWord = ((TextNode) textNode).getWholeText();
            suggestedWords.get().add(suggestedWord);
        }
    }

    @Override
    void saveResultsToFile() throws IOException {
        Stream<String> joinedWordsAndSuggestionsStream = Stream.concat(outputList.get().stream(), suggestedWords.get().stream());
        List<String> finalProduct = joinedWordsAndSuggestionsStream.sorted().distinct().collect(Collectors.toList());

        //List<String> sorted = outputList.get().stream().sorted().collect(Collectors.toList());
        Files.write(PATH_OF_EXPORT_FILE, finalProduct, Charset.defaultCharset());
        System.out.println("Program End. Saved results to: %s".formatted(PATH_OF_EXPORT_FILE.toFile().getAbsolutePath()));

        Files.write(PATH_OF_DELETED_WORDS_FILE, deletedList.get().stream().sorted().collect(Collectors.toList()), Charset.defaultCharset());
        int size = deletedList.get().size();
        System.out.println("Program End. Saved deleted words to: %s".formatted(PATH_OF_DELETED_WORDS_FILE.toFile().getAbsolutePath()));
        // do stuff for suggested
    }
}
