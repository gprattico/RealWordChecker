import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class AbstractDictionaryChecker {
    public static final String exportFile = new String(System.getProperty("user.dir")+"\\newDict.txt");
    public static final String deletedWordsFile = new String(System.getProperty("user.dir")+"\\deletedWords.txt");
    public static final Path PATH_OF_EXPORT_FILE = Path.of(exportFile);
    public static final Path PATH_OF_DELETED_WORDS_FILE = Path.of(deletedWordsFile);
    List<String> words;
    public AtomicReference<ArrayList<String>> outputList = new AtomicReference<>(new ArrayList<>());
    public AtomicReference<ArrayList<String>> deletedList = new AtomicReference<>(new ArrayList<>());

    public AbstractDictionaryChecker(List<String> words) throws IOException {
        this.words = words;

        Files.deleteIfExists(PATH_OF_EXPORT_FILE);
        Files.deleteIfExists(PATH_OF_DELETED_WORDS_FILE);

        Files.createFile(PATH_OF_EXPORT_FILE);
        Files.createFile(PATH_OF_DELETED_WORDS_FILE);
    }

    public void verifyWords() throws IOException {
        System.out.println("Try connecting to dictionary");
        words.parallelStream().forEach(word -> {
            if (isReal(word)) outputList.get().add(word);
            else deletedList.get().add(word);
        });

        saveResultsToFile();
    }

    void saveResultsToFile() throws IOException {
        List<String> sorted = outputList.get().stream().sorted().collect(Collectors.toList());
        Files.write(PATH_OF_EXPORT_FILE, sorted, Charset.defaultCharset());
        System.out.println("Program End. Saved results to: %s".formatted(PATH_OF_EXPORT_FILE.toFile().getAbsolutePath()));

        Files.write(PATH_OF_DELETED_WORDS_FILE, deletedList.get().stream().sorted().collect(Collectors.toList()), Charset.defaultCharset());
        int size = deletedList.get().size();
        System.out.println("Program End. Saved deleted words to: %s".formatted(PATH_OF_DELETED_WORDS_FILE.toFile().getAbsolutePath()));
    }

    abstract boolean isReal(String word);

}

