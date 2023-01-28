import javax.naming.ConfigurationException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        String filePath = args.length > 0 ? args[0] : "error";
        if (filePath.equals("error")) throw new ConfigurationException("Please pass the path to the file.");
        Path toDictionary = Path.of(filePath);
        if (toDictionary.toFile().isDirectory()) throw new ConfigurationException("You passed a directory, please pass a file");

        System.out.println("Hello world!\nFound dictionary at: %s".formatted(toDictionary.toFile().getAbsolutePath()));

        List<String> words = Files.readAllLines(toDictionary);
        words.size();
        if (words.size() == 0) {
            System.out.println("No words found in the dictionary. Exiting...");
            System.exit(0);
        }

        //new FarlexDictionaryChecker(words).verifyWords();
        new CollinsDictionaryChecker(words).verifyWords();
    }
}