package info.generate;

import info.templates.Missile;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class MakeMissiles {
    public static ArrayList<Missile> missiles = new ArrayList<>();
    static String missilesSaveLoc = "Data/Ser/missiles.ser";

    public static void main(String[] args) throws IOException {
        missiles.clear();
        File dir = new File("Data/Missiles/");
        File[] listFiles = dir.listFiles();
        Missile m = null;
        for (int i = 0; i < listFiles.length; i++) {
            String name = listFiles[i].getName().replace(".blkx", "");
            var actualname = MakeGuns.getName(name).replace("\"","");
            if (actualname.contains("500 kg FAB-500M-62 "))
                actualname = name;
            m = new Missile(name, actualname, i);
            System.out.println(actualname);
            missiles.add(m);

        }
        saveMsls();

    }

    public static void readMsls() {
        try {
            //URL url = WikiManager.class.getClassLoader().getResource("commits.ser");
            var f = new File(missilesSaveLoc);

            BufferedInputStream bis = new BufferedInputStream(f.toURL().openStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }
            byte[] largeFileBytesToCheck = buf.toByteArray();
            missiles = SerializationUtils.deserialize(largeFileBytesToCheck);
            System.out.println("missiles: " + missiles.size());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void saveMsls() {

        byte[] data = SerializationUtils.serialize((Serializable) missiles);
        try {
            Files.write(Path.of(missilesSaveLoc), data);
        } catch (IOException e) {
            System.out.println("fucked in savemsls");
            e.printStackTrace();
        }
    }
}
