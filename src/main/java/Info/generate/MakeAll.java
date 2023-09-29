package Info.generate;

import Info.FileMover;
import Info.PlanesManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class MakeAll {
    public static void main(String[] args) throws IOException {
        pull();
        moveFiles();
        new Thread(() -> {
            try {
                MakeGuns.main(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                Comparator.main(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                MakeMissiles.main(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> PlanesManager.main(null)).start();


    }
    public static void pull() {
        try {
            String directory = "Data/Git";
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(new File(directory));
            builder.command("git", "stash");
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Git stash completed successfully");
            } else {
                System.out.println("Git stash failed with exit code " + exitCode);
            }
            builder.command("git", "pull", "origin", "master");
            process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Git pull completed successfully");
            } else {
                System.out.println("Git pull failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    static void moveFiles() throws IOException {
            String version1 = new String(Files.readAllBytes(Paths.get("Data/Other/version")));
            String version2 = new String(Files.readAllBytes(Paths.get("Data/Git/aces.vromfs.bin_u/version")));
        System.out.println("Local Game version: "+version1);
        System.out.println("Git Game version: "+version2);
    //        int[] v1 = Arrays.stream(version1.split("\\.")).mapToInt(Integer::parseInt).toArray();
    //        int[] v2 = Arrays.stream(version2.split("\\.")).mapToInt(Integer::parseInt).toArray();

    //        if (v1[0] < v2[0] || (v1[0] == v2[0] && v1[1] < v2[1])) {
                System.out.println("Files Moved");
                FileMover.main(null);
          //  }
          //  else {
          //      System.out.println("No Files were moved");
          //  }
    }

}
