import com.google.gson.reflect.TypeToken;
import org.apache.lucene.benchmark.byTask.feeds.DocData;
import org.apache.lucene.benchmark.byTask.feeds.NoMoreDataException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;

public class ClueWeb22Parser {

    private String docsDirsRelative;

    private ArrayList<Path> inputFiles = new ArrayList<Path>();

    private BufferedReader reader;

    private int fileInd = 0;

    public ClueWeb22Parser (String docsDirsRelative) throws IOException {
        this.docsDirsRelative = docsDirsRelative;
        this.collectFiles(this.inputFiles);
    }

    public DocData getNextDocData() throws IOException, NoMoreDataException {
         if (this.reader == null)
            this.openNextFile();

        while (true) {

            String line = this.reader.readLine();

            if (line == null) {
                this.openNextFile();
                continue;
            }

            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            HashMap<String, String> json = gson.fromJson(line.trim(), type);

            String url = json.getOrDefault("URL", "").trim();
            String urlHash = json.getOrDefault("URL-hash", "").trim();
            String language = json.getOrDefault("Language", "").trim().toLowerCase();
            String id = json.getOrDefault("ClueWeb22-ID", "").trim();
            String text = json.getOrDefault("Clean-Text", "").trim();
            int newlineIndex = text.indexOf("\n");
            String title = newlineIndex > -1 ? text.substring(0, newlineIndex) : "";

            DocData docData = new DocData();

            Properties pr = new Properties();
            pr.setProperty ("url", url);
            pr.setProperty ("urlHash", urlHash);
            pr.setProperty ("language", language);
            docData.setProps(pr);

            docData.setBody(text);
            docData.setTitle(title);
            docData.setName(id);

            return docData;
        }
    }

    private void openNextFile() throws IOException, NoMoreDataException {

        if (this.inputFiles.size() == this.fileInd) {
            System.out.println("Done reading all files");
            throw new NoMoreDataException();
        }

        Path f = this.inputFiles.get(this.fileInd++);
        System.out.println("opening: " + f + " length: " + Files.size(f));

        try {
            // InputStream inputStream = StreamUtils.inputStream(f);
            FileInputStream fileInputStream = new FileInputStream(f.toString());
            GZIPInputStream inputStream = new GZIPInputStream(fileInputStream);

            this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1.name()));
        } catch (Exception var3) {
            System.out.println("Skipping 'bad' file " + f.toAbsolutePath() + " due to " + var3.getMessage());
            this.openNextFile();
        }
    }

    private void collectFiles(ArrayList<Path> files) throws IOException {

        String[] docsDirsRelativeArr = this.docsDirsRelative.split(",");

        for (String docsDirRelative: docsDirsRelativeArr) {
            String docsDir = (new File(docsDirRelative)).getAbsolutePath();
            Path docsDirPath = Paths.get(docsDir);

            Files.walkFileTree(docsDirPath, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (Files.isReadable(file) && file.toString().endsWith(".json.gz")) {
                        files.add(file.toRealPath());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        }

	System.out.println(this.inputFiles.size() + " files will be read");
    }


}
