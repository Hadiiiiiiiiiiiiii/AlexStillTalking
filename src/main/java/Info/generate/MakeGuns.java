package Info.generate;

import Info.templates.Belt;
import Info.templates.Gun;
import Info.templates.Shell;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class MakeGuns {
    static String INFO2;
    static {
        try {
            INFO2 = Files.readString(Path.of("Data/Other/units_weaponry.csv"));
            String a = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static Gun gun1;
    static String[] units_weaponry = INFO2.split("\n");
    static ArrayList<Gun> guns = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        guns.clear();
        System.out.println("starting....");
        File dir = new File("Data/weapons/");
        String tnt = Files.readString(Path.of("Data/Other/explosive.blkx"));
        JSONObject tntJson = new JSONObject(tnt);
        int id = 0;
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                //boolean firstrun = true;
                //     listFiles[i].getName();
                String jsonString = Files.readString(Path.of(listFiles[i].getPath()));
                JSONObject obj = new JSONObject(jsonString);
                JSONObject originalOnj = new JSONObject(jsonString);
                JSONArray arrPresets = new JSONArray();
                ArrayList<String> arrPresetsNames = new ArrayList<>();
                if (obj.names() != null)
                    for (int j = 0; j < obj.names().length(); j++) {
                        String name = obj.names().get(j).toString();
                        if (name.contains("universal") || name.contains("ground_targets") || name.contains("air_targets") ||
                                name.contains("stealth") || name.contains("tracers") || name.contains("armor_targets") ||
                                name.contains("_antibomber") || name.contains("_antitank")) {
                            if (obj.getJSONObject(name).has("bullet")) {
                                arrPresets.put(obj.get(name));
                                arrPresetsNames.add(name);
                            }
                        }
                    }
                try {
                    //System.out.println(listFiles[i].getName());
                    var name = getName(listFiles[i].getName());
                    if (name.contains(" bomb"))
                        name = listFiles[i].getName().replace(".blkx","");
                    BigDecimal shotFreq = originalOnj.getBigDecimal("shotFreq").multiply(BigDecimal.valueOf(60));
                    shotFreq = shotFreq.setScale(2, RoundingMode.HALF_UP);
                    gun1 = new Gun(name+"("+listFiles[i].getName()+")", shotFreq+"", id,listFiles[i].getName());
                    id++;
                    for (int k = -1; k < arrPresets.length(); k++) {
                        Belt belt;
                        if (k != -1) {
                            obj = arrPresets.getJSONObject(k);
                            belt = new Belt(arrPresetsNames.get(k));
                        } else {
                            obj = originalOnj;
                            belt = new Belt("default");
                        }
                        JSONArray arr = obj.getJSONArray("bullet");
                        for (int j = 0; j < arr.length(); j++) {
                            Shell shell;
                            if (arr.getJSONObject(j).has("explosiveMass") && arr.getJSONObject(j).has("Cx"))
                                shell = new Shell(arr.getJSONObject(j).getString("bulletType")
                                        , arr.getJSONObject(j).getBigDecimal("speed").toPlainString()
                                        , arr.getJSONObject(j).getBigDecimal("maxDistance").toPlainString()
                                        , arr.getJSONObject(j).getBigDecimal("explosiveMass").toPlainString()
                                        , arr.getJSONObject(j).getBigDecimal("mass").toPlainString(), arr.getJSONObject(j).getBigDecimal("Cx").toPlainString()
                                        , arr.getJSONObject(j).getBigDecimal("caliber").toPlainString()
                                        , arr.getJSONObject(j).getBigDecimal("explosiveMass").multiply(tntJson.getJSONObject("explosiveTypes").getJSONObject(arr.getJSONObject(j).getString("explosiveType")).getBigDecimal("strengthEquivalent")).toPlainString()
                                        , arr.getJSONObject(j).getString("explosiveType"));
                            else if (!arr.getJSONObject(j).has("explosiveMass") && arr.getJSONObject(j).has("Cx"))
                                shell = new Shell(arr.getJSONObject(j).getString("bulletType"), arr.getJSONObject(j).getBigDecimal("speed").toPlainString(), arr.getJSONObject(j).getBigDecimal("maxDistance").toPlainString()
                                        , arr.getJSONObject(j).getBigDecimal("mass").toPlainString(), arr.getJSONObject(j).getBigDecimal("Cx").toPlainString(),
                                        arr.getJSONObject(j).getBigDecimal("caliber").toPlainString());
                            else
                                shell = new Shell(arr.getJSONObject(j).getString("bulletType"), arr.getJSONObject(j).getBigDecimal("speed").toPlainString(), arr.getJSONObject(j).getBigDecimal("maxDistance").toPlainString()
                                        , arr.getJSONObject(j).getBigDecimal("mass").toPlainString(), "0.35",
                                        arr.getJSONObject(j).getBigDecimal("caliber").toPlainString());
                            gun1.add(shell);
                            belt.addShell(shell);
                        }
                        gun1.addBelt(belt);
                    }
                    if (!guns.stream().map(g -> g.name).toList().contains(gun1.name)) {
                        guns.add(gun1);
              }
                } catch (Exception ignored) {
                    try {
                        id++;
                        JSONObject obj1 = obj.getJSONObject("bullet");

                        if (obj1.has("explosiveMass") && obj1.has("Cx"))
                            gun1.add(new Shell(obj1.getString("bulletType")
                                    , obj1.getBigDecimal("speed").toPlainString()
                                    , obj1.getBigDecimal("maxDistance").toPlainString()
                                    , obj1.getBigDecimal("explosiveMass").toPlainString()
                                    , obj1.getBigDecimal("mass").toPlainString(),
                                    obj1.getBigDecimal("Cx").toPlainString()
                                    , obj1.getBigDecimal("caliber").toPlainString()
                                    , obj1.getBigDecimal("explosiveMass").multiply(tntJson.getJSONObject("explosiveTypes").getJSONObject(obj1.getString("explosiveType")).getBigDecimal("strengthEquivalent")).toPlainString()
                                    , obj1.getString("explosiveType")));

                        else if (!obj1.has("explosiveMass") && obj1.has("Cx"))
                            gun1.add(new Shell(obj1.getString("bulletType"),
                                    obj1.getBigDecimal("speed").toPlainString(),
                                    obj1.getBigDecimal("maxDistance").toPlainString()
                                    , obj1.getBigDecimal("mass").toPlainString(),
                                    obj1.getBigDecimal("Cx").toPlainString()
                                    , obj1.getBigDecimal("caliber").toPlainString()));
                        else
                            gun1.add(new Shell(obj1.getString("bulletType"),
                                    obj1.getBigDecimal("speed").toPlainString(),
                                    obj1.getBigDecimal("maxDistance").toPlainString(),
                                    obj1.getBigDecimal("mass").toPlainString(), "0.35",
                                    obj1.getBigDecimal("caliber").toPlainString()));

                        if (!guns.stream().map(g -> g.name).toList().contains(gun1.name)) {
                            guns.add(gun1);
                        }
                   } catch (Exception ignored2) {

                    }
                }
            }
        }
        saveGuns();
    }
    public static void saveGuns()
    {
        byte[] data = SerializationUtils.serialize( guns);
        Path gunspath = Path.of("Data/Ser/guns.ser");
        try {
            // System.out.println(gunspath);
            Files.write(gunspath, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Guns->  "+guns.size());
        int k = 0;
        for (int i = 0; i < guns.size(); i++) {
            k += guns.get(i).shells.size();
        }
        System.out.println("Shells-> "+k);
    }
    public static String getName(String name)  {
        String nameold = name+"";
        name = name.replace(".blkx","").replace("lau_10a_","us_").replace("lau_3a_","us_2_75_in_");
        for (int i = 0; i < units_weaponry.length; i++) {
            var c = units_weaponry[i].split(";");
            if (name.equalsIgnoreCase(c[0].replace("\"","").replace("weapons/","")))
            {
                return c[1];
            }
        }
        for (int i = 0; i < units_weaponry.length; i++) {
            var c = units_weaponry[i].split(";");
            if (name.contains(c[0].replace("\"","").replace("weapons/","")))
            {
                return c[1];
            } else if (c[0].replace("\"","").replace("weapons/","").contains(name))
            {
                return c[1];
            }
        }

        return nameold;
    }
    public static String getLangName(String name) {
        if (name.contains("ground_targets")) {
            return "Ground targets";
        } else if (name.contains("air_targets")) {
            return "Air targets";
        } else if (name.contains("universal")) {
            return "Universal";
        } else if (name.contains("stealth")) {
            return "Stealth";
        } else if (name.contains("tracers")) {
            return "Tracers";
        } else if (name.contains("armor_targets")) {
            return "Armor targets";
        } else if (name.contains("_antibomber")) {
            return "Anti-bomber";
        } else if (name.contains("_antitank")) {
            return "Anti-tank";
        }
        return name;
    }
}