package Info;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileComparator {
    public static void compareFiles(String file1, String file2, String txtName) throws IOException {
        String cwd = System.getProperty("user.dir") + "/Data/Scripts/";
        file1 = cwd + file1;
        file2 = cwd + file2;

        List<String> text1 = Files.readAllLines(Paths.get(file1));
        List<String> text2 = Files.readAllLines(Paths.get(file2));
        List<String> diff = difflib.DiffUtils.generateUnifiedDiff(file1, file2, text1, difflib.DiffUtils.diff(text1, text2), 0);
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(cwd + "txts/" + txtName))) {
            for (String line : diff) {
                if (line.startsWith("--- ") || line.startsWith("+++ ")) {
                    continue;
                }
                writer.write(line + "\n");
            }
        }
    }

}