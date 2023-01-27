import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DictionaryChecker {
    public static final String exportFile = new String(System.getProperty("user.dir")+"\\newDict.txt");
    public static final String deletedWordsFile = new String(System.getProperty("user.dir")+"\\deletedWords.txt");
    public static final Path PATH_OF_EXPORT_FILE = Path.of(exportFile);
    public static final Path PATH_OF_DELETED_WORDS_FILE = Path.of(deletedWordsFile);
    private static int count = 0;
    private final String dictionaryURL = "https://www.thefreedictionary.com/";
    List<String> words;
    private AtomicReference<ArrayList<String>> outputList = new AtomicReference<>(new ArrayList<>());
    private AtomicReference<ArrayList<String>> deletedList = new AtomicReference<>(new ArrayList<>());

    public DictionaryChecker(List<String> words) throws IOException {
        this.words = words;
        Files.deleteIfExists(PATH_OF_EXPORT_FILE);
        Files.deleteIfExists(PATH_OF_DELETED_WORDS_FILE);

        Files.createFile(PATH_OF_EXPORT_FILE);
        Files.createFile(PATH_OF_DELETED_WORDS_FILE);
    }

    public void verifyWords() throws IOException {
        words.parallelStream().forEach(word -> {
            try {
                if (isReal(word)) outputList.get().add(word);
                else deletedList.get().add(word);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });

        saveResultsToFile();
    }

    private void saveResultsToFile() throws IOException {
        List<String> sorted = outputList.get().stream().sorted().collect(Collectors.toList());
        Files.write(PATH_OF_EXPORT_FILE, sorted, Charset.defaultCharset());
        System.out.println("Program End. Saved results to: %s".formatted(PATH_OF_EXPORT_FILE.toFile().getAbsolutePath()));

        Files.write(PATH_OF_DELETED_WORDS_FILE, deletedList.get().stream().sorted().collect(Collectors.toList()), Charset.defaultCharset());
        int size = deletedList.get().size();
        System.out.println("Program End. Saved deleted words to: %s".formatted(PATH_OF_DELETED_WORDS_FILE.toFile().getAbsolutePath()));
    }

    private boolean isReal(String word) throws MalformedURLException {

        try {
            org.jsoup.nodes.Document document = Jsoup.connect(this.dictionaryURL + word).get();
            Element mainTxt = document.getElementById("MainTxt");
            List<TextNode> isNotAvailable = mainTxt.childNodes().stream().filter(TextNode.class::isInstance).map(TextNode.class::cast)
                    .filter(textNode -> textNode.getWholeText().contains("not available in the")).collect(Collectors.toList());
            if (isNotAvailable.size() > 0) return false;
            return true;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}
