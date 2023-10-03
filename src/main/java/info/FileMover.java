package info;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileMover {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting to Copy Files...");
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isWindows = osName.startsWith("windows");

        String dirX = "Data/Git";
        String dirY = "Data";
        String dirY2 = "Data/Other";
        moveFiles(dirX + "/aces.vromfs.bin_u/gamedata/flightmodels", dirY + "/AllFMs/");
        moveFiles(dirX + "/aces.vromfs.bin_u/gamedata/flightmodels/fm", dirY + "/FM/");
        moveFiles(dirX + "/aces.vromfs.bin_u/gamedata/weapons", dirY + "/weapons/");
        moveFiles(dirX + "/tex.vromfs.bin_u/aircrafts", dirY + "/Pics/");
        moveFiles(dirX + "/aces.vromfs.bin_u/gamedata/weapons/rocketguns/", dirY + "/Missiles/");
        moveFiles(dirX + "/aces.vromfs.bin_u/gamedata/flightmodels/weaponpresets/", dirY + "/Presets/");
        moveFiles(dirX + "/aces.vromfs.bin_u/gamedata/weapons/containers/", dirY + "/Presets/containers/");

        moveFile(dirX + "/aces.vromfs.bin_u/gamedata/damage_model/explosive.blkx", dirY2);
        moveFile(dirX + "/char.vromfs.bin_u/config/wpcost.blkx", dirY2);

        moveFile(dirX + "/lang.vromfs.bin_u/lang/units_weaponry.csv", dirY2);
        moveFile(dirX + "/lang.vromfs.bin_u/lang/units.csv", dirY2);
        moveFile(dirX + "/lang.vromfs.bin_u/lang/units_modifications.csv", dirY2);
        moveFile(dirX + "/aces.vromfs.bin_u/version", dirY2);


      //  copyDirectory(new File(dirY),new File("/home/handels/Dev/AfricaBot/exe/Logger/Data/"));
        System.out.println("Done.");
    }

    private static void moveFiles(String source, String destination) {
        File sourceDir = new File(source);
        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        Files.copy(file.toPath(), new File(destination + file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.out.println("Error moving file: " + file.getName());
                    }
                }
            }
        }
    }

    public static void moveFile(String source, String destination) {
        File file = new File(source);
        if (file.isFile()) {
            try {
                Files.copy(file.toPath(), new File(destination + "/" + file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Error moving file: " + file.getName());
            }
        }
    }
    public static void copyDirectory(File sourceDir, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        File[] children = sourceDir.listFiles();
        if (children != null) {
            for (File sourceChild : children) {
                String name = sourceChild.getName();
                File destChild = new File(destDir, name);
                if (sourceChild.isDirectory()) {
                    copyDirectory(sourceChild, destChild);
                } else {
                    Files.copy(sourceChild.toPath(), destChild.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

}