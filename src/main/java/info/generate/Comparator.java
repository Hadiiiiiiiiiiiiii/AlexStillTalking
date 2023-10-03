package info.generate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import info.FileComparator;
import info.templates.Commit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.SerializationUtils;


public class Comparator implements Serializable {
    public static List<Commit> commits = new ArrayList<>();
    static String commitsSaveLoc = "Data/Ser/commits.ser";
    static String PATH_TO_GUNS = "/aces.vromfs.bin_u/gamedata/weapons/";
    static String PATH_TO_MISSILES = "/aces.vromfs.bin_u/gamedata/weapons/rocketguns/";
    static String PATH_TO_FMs = "/aces.vromfs.bin_u/gamedata/flightmodels/fm/";
    static String token;

    {
        try {
            token = Files.readString(Path.of("tokens.env")).split(" ")[1];
        } catch (IOException e) {
            System.out.println("cant read github token");
        }
    }

    public static void main(String[] args) throws IOException {makeCommits();
        for (int i = 0; i < commits.size(); i++) {

            System.out.println(commits.get(i).commit.message);
        }
    }

    public static File compareAndSave(String fmname1, String fmname2, String gameversion1, String gameversion2, String type) {
        String path1 = "";
        String path2 = "";

        if (type.equals("FM")) {
            path1 = PATH_TO_FMs + fmname1;
            path2 = PATH_TO_FMs + fmname2;
        } else if (type.equals("GUNS")) {
            path1 = PATH_TO_GUNS + fmname1;
            path2 = PATH_TO_GUNS + fmname2;
        } else if (type.equals("MISSILES")) {
            path1 = PATH_TO_MISSILES + fmname1;
            path2 = PATH_TO_MISSILES + fmname2;
        }
        try {

            var a = saveFile(path1.replace(gameversion1, ""), gameversion1, fmname1.replace(".blkx", ""));
            var b = saveFile(path2.replace(gameversion2, ""), gameversion2, fmname2.replace(".blkx", ""));
            if (a && b) {
                String s = "diff_" + fmname1.replace(".blkx", "") + "_" + fmname2.replace(".blkx", "") + "_" + gameversion1 + "_" + gameversion2 + ".txt";
                String saveLoc = "Data/Scripts/txts/" + s.toLowerCase();
                FileComparator.compareFiles("tmp/" + fmname1.replace(".blkx", "") + gameversion1 + ".txt", "tmp/" + fmname2.replace(".blkx", "") + gameversion2 + ".txt", s.toLowerCase());
                return new File(saveLoc);
            }
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    static boolean saveFile(String pathToFile, String gameversion, String filename)  {
        String owner = "gszabi99";
        String repo = "War-Thunder-Datamine";
        //System.out.println(pathToFile);
        //   String branch = "master";

        String outputFile = "Data/Scripts/tmp/" + filename + gameversion + ".txt";
        Commit c = null;
        for (int i = 0; i < commits.size(); i++) {
            if (commits.get(i).commit.message.equals(gameversion)) {
                c = commits.get(i);
            }
        }
        try {
            URL url = new URL(c.url.replace("https://api.github.com/repos/gszabi99", "https://raw.githubusercontent.com/gszabi99").replace("/tree", "") /*+ "/contents/"*/ + pathToFile);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/vnd.github.v3.raw");
            String encoded = Base64.getEncoder().encodeToString((owner + ":" + token).getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encoded);

            String content = new String(connection.getInputStream().readAllBytes());

            OutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        return false;
        }
        return true;
    }





    public static void saveCommits() {

        byte[] data = SerializationUtils.serialize((Serializable) commits);
        try {
            Files.write(Path.of(commitsSaveLoc), data);
        } catch (IOException e) {
            System.out.println("FUCK");
            e.printStackTrace();
        }
    }

    public static void makeCommits() {
        System.out.println("starting...");
        makecommits();

        saveCommits();
        readCommits();
        System.out.println("done");

    }
    private static void makecommits() {
        String owner = "gszabi99";
        String repo = "War-Thunder-Datamine";
        String url = String.format("https://api.github.com/repos/%s/%s/commits?per_page=100&page=1", owner, repo);
        String token = null;
        try {
            token = Files.readString(Path.of("tokens.env")).split(" ")[1];//
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpClient client = HttpClient.newHttpClient();
        int pageCount = 20;
        for (int i = 1; i <= pageCount; i++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "&page=" + i))
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/vnd.github+json")
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                break;
            }
            String responseBody = response.body();
            Gson gson = new Gson();
            List<Commit> pageCommits = gson.fromJson(responseBody, new TypeToken<List<Commit>>() {
            }.getType());
            commits.addAll(pageCommits);

        }

        for (int i = 0; i < commits.size(); i++) {
            commits.get(i).url = commits.get(i).url.replace("commits", "tree");
        }
    }


    public static String getGameVersion(String nodots) {
        for (int i = 0; i < commits.size(); i++) {
            if (commits.get(i).commit.message.replace(".", "").equals(nodots)) {
                return commits.get(i).commit.message;
            }
        }
        return null;
    }

    public static void readCommits() {

        try {
            commits.clear();
            //URL url = WikiManager.class.getClassLoader().getResource("commits.ser");
            var f = new File(commitsSaveLoc);
            BufferedInputStream bis = new BufferedInputStream(f.toURL().openStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }
            byte[] largeFileBytesToCheck = buf.toByteArray();
            commits = SerializationUtils.deserialize(largeFileBytesToCheck);
            System.out.println("commits: " + commits.size());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

