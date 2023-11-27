package info;

import info.generate.MakeGuns;
import info.templates.Plane;
import info.templates.TableBuilder;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PlanesManager {
    public static ArrayList<Plane> planes = new ArrayList<>(1020);
    static List<Integer> altList = new ArrayList<>();
    public static List<Integer> speedList = new ArrayList<>();
    public static String INFO2;
    static double[][] thrust = new double[0][0];
    static double[][] thrust2 = new double[0][0];
    static boolean hasTwoEngineTypes;
    static boolean hasTwoEngines;
    static String type;
    static String saveLoc = "Data/Ser/Planes.ser";


    {
        try {
            INFO2 = Files.readString(Path.of("Data/Other/units.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PlanesManager() {
        readPlanes();

        System.out.println(planes.stream().filter(p -> p.isJetOrRocket).toList().size());


    }

    public static void main(String[] args) {
        new PlanesManager().makePlanes();

    }

    public void makePlanes() {
        try {
            planes.clear();
            long uid = 0;
            File dir = new File("Data/AllFMs/");
            String INFO = Files.readString(Path.of("Data/Other/wpcost.blkx"));
            JSONObject wpcost = new JSONObject(INFO);
            planes.clear();
            System.out.println(dir.isDirectory());
            if (dir.isDirectory()) {
                File[] listFiles = dir.listFiles();

                for (int i = 0; i < listFiles.length; i++) {
                    String jsonString = Files.readString(Path.of(listFiles[i].getPath()));
                    var realName = listFiles[i].getName().replace(".blkx", "");
                    String name;
                    try {
                        name = getNamePlane(realName);
                    } catch (Exception e) {
                        name = realName;
                    }
                    try {
                        JSONObject planejsn = null;
                        try {
                            planejsn = wpcost.getJSONObject(realName);
                        } catch (Exception eee) {
                            try {
                                planejsn = wpcost.getJSONObject(realName.replace("-", "_"));
                            } catch (Exception E) {
                                try {
                                    planejsn = wpcost.getJSONObject(realName.replace("_", "-"));
                                } catch (Exception e) {

                                    try{
                                        planejsn = wpcost.getJSONObject(realName+"_shop");
                                    }
                                    catch (Exception ee)
                                    {
                                        System.out.println(e.getMessage() + " avbcdefg " + realName);

                                    }
                                }
                            }
                        }
                        JSONObject allFMJson = new JSONObject(jsonString);
                        String actualName = "";
                        try{
                           actualName = allFMJson.getString("fmFile").replace(".blk", "").replace("fm/", "");
                        }
                        catch (Exception e)
                        {
                            actualName = realName;
                        }
                        Plane plane;
                        var path = new File("Data/FM/" + actualName + ".blkx");

                        makeThrustTables(new JSONObject(Files.readString(path.toPath())),actualName);
                        if (planejsn.has("reqExp"))
                            plane = new Plane(name, uid, actualName, listFiles[i].getName().replace(".blkx", ""),
                                    Integer.toString(planejsn.getInt("value")), Integer.toString(planejsn.getInt("reqExp")), getBR(planejsn.getInt("economicRankHistorical")),
                                    getBR(planejsn.getInt("economicRankSimulation")), getBR(planejsn.getInt("economicRankArcade")),
                                    Integer.toString(planejsn.getInt("repairCostHistorical")), Integer.toString(planejsn.getInt("repairCostSimulation")), Integer.toString(planejsn.getInt("repairCostArcade")),
                                    Integer.toString(planejsn.getInt("trainCost")), Integer.toString(planejsn.getInt("train2Cost")), Integer.toString(planejsn.getInt("train3Cost_gold")),
                                    Integer.toString(planejsn.getInt("train3Cost_exp")), planejsn.getBigDecimal("rewardMulArcade").toString(), planejsn.getBigDecimal("rewardMulHistorical").toString(),
                                    planejsn.getBigDecimal("rewardMulSimulation").toString(), planejsn.getBigDecimal("expMul").toString(), altList, speedList, thrust, type, hasTwoEngineTypes, thrust2, hasTwoEngines, getBlkString(new JSONObject(jsonString), name));
                        else
                            plane = new Plane(name, uid, actualName, listFiles[i].getName().replace(".blkx", ""),
                                    Integer.toString(planejsn.getInt("value")), getBR(planejsn.getInt("economicRankHistorical")),
                                    getBR(planejsn.getInt("economicRankSimulation")), getBR(planejsn.getInt("economicRankArcade")),
                                    Integer.toString(planejsn.getInt("repairCostHistorical")), Integer.toString(planejsn.getInt("repairCostSimulation")), Integer.toString(planejsn.getInt("repairCostArcade")),
                                    String.valueOf(planejsn.getInt("trainCost")), Integer.toString(planejsn.getInt("train2Cost")), Integer.toString(planejsn.getInt("train3Cost_gold")),
                                    Integer.toString(planejsn.getInt("train3Cost_exp")), planejsn.getBigDecimal("rewardMulArcade").toString(), planejsn.getBigDecimal("rewardMulHistorical").toString(),
                                    planejsn.getBigDecimal("rewardMulSimulation").toString(), planejsn.getBigDecimal("expMul").toString(), altList, speedList, thrust, type, hasTwoEngineTypes, thrust2, hasTwoEngines, getBlkString(new JSONObject(jsonString), name));

                        hasTwoEngineTypes = false;
                        hasTwoEngines = false;
                        speedList.clear();
                        altList.clear();
                        thrust = null;
                        thrust2 = null;
                        uid++;
                        //  System.out.println(realName);
                        if (!planes.contains(plane))
                            planes.add(plane);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            //   System.out.println(plane);

            savePlanes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("new Len: " + planes.size());
        for (int i = 0; i < planes.size(); i++) {
            System.out.println(planes.get(i));
        }
    }


    public static String getNamePlane(String name) {

        String[] d = INFO2.split("\n");

        for (int i = 0; i < d.length; i++) {
            var c = d[i].split(";");
            if (name.equalsIgnoreCase(c[0].replace("\"", "").replace("_shop", ""))) {
                return c[1];
            }
        }
        for (int i = 0; i < d.length; i++) {
            var c = d[i].split(";");
            if (name.contains(c[0].replace("\"", ""))) {
                return c[1];
            } else if (c[0].replace("\"", "").replace("_shop", "").contains(name)) {
                return c[1];
            }
        }
        return name;
    }

    public String getBlkString(JSONObject json, String name2) {
        StringBuilder blkString = new StringBuilder();
        HashMap<String, Integer> gunCount = new HashMap<>();
        try {
            JSONObject commonWeapons = json.getJSONObject("commonWeapons");
            Object weapon = commonWeapons.get("Weapon");
            if (weapon instanceof JSONArray) {
                JSONArray weaponArray = (JSONArray) weapon;
                for (int i = 0; i < weaponArray.length(); i++) {
                    JSONObject weaponObj = weaponArray.getJSONObject(i);
                    String blk = weaponObj.getString("blk");
                    blk = blk.substring(blk.lastIndexOf("/") + 1).replace(".blk", "");
                    String gunName = !MakeGuns.getName(blk).contains("bomb") ? MakeGuns.getName(blk) : blk;
                    gunCount.put(gunName, gunCount.getOrDefault(gunName, 0) + 1);
                }
            } else if (weapon instanceof JSONObject) {
                JSONObject weaponObj = (JSONObject) weapon;
                String blk = weaponObj.getString("blk");
                blk = blk.substring(blk.lastIndexOf("/") + 1).replace(".blk", "");
                String gunName = !MakeGuns.getName(blk).contains("bomb") ? MakeGuns.getName(blk) : blk;
                gunCount.put(gunName, gunCount.getOrDefault(gunName, 0) + 1);
            }
        } catch (JSONException e) {
            try {
                JSONObject weaponSlots = json.getJSONObject("WeaponSlots");
                JSONArray weaponSlotArray = weaponSlots.getJSONArray("WeaponSlot");
                for (int i = 0; i < weaponSlotArray.length(); i++) {
                    JSONObject weaponSlotObj = weaponSlotArray.getJSONObject(i);
                    Object weaponPresetObj = weaponSlotObj.get("WeaponPreset");
                    if (weaponPresetObj instanceof JSONArray) {
                        JSONArray weaponPresetArray = (JSONArray) weaponPresetObj;
                        for (int j = 0; j < weaponPresetArray.length(); j++) {
                            JSONObject weaponPreset = weaponPresetArray.getJSONObject(j);
                            String name = weaponPreset.getString("name");
                            if (name.equals(json.getJSONObject("commonWeapons").getJSONObject("Weapon").getString("preset"))) {
                                if (weaponPreset.get("Weapon") instanceof JSONArray) {
                                    JSONArray weaponArray = weaponPreset.getJSONArray("Weapon");
                                    for (int k = 0; k < weaponArray.length(); k++) {
                                        if (weaponArray.getJSONObject(k).getString("trigger").equals("cannon")) {
                                            String blk = weaponArray.getJSONObject(k).getString("blk");
                                            blk = blk.substring(blk.lastIndexOf("/") + 1).replace(".blk", "");
                                            String gunName = !MakeGuns.getName(blk).contains("bomb") ? MakeGuns.getName(blk) : blk;
                                            gunCount.put(gunName, gunCount.getOrDefault(gunName, 0) + 1);
                                        }
                                    }

                                } else {
                                    if (weaponPreset.get("Weapon") instanceof JSONObject) {
                                        var weaponArray = weaponPreset.getJSONObject("Weapon");
                                        if (weaponArray.getString("trigger").equals("cannon")) {
                                            String blk = weaponArray.getString("blk");
                                            blk = blk.substring(blk.lastIndexOf("/") + 1).replace(".blk", "");
                                            String gunName = !MakeGuns.getName(blk).contains("bomb") ? MakeGuns.getName(blk) : blk;
                                            gunCount.put(gunName, gunCount.getOrDefault(gunName, 0) + 1);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (weaponPresetObj instanceof JSONObject) {
                        JSONObject weaponPreset = (JSONObject) weaponPresetObj;
                        String name = weaponPreset.getString("name");
                        if (name.equals(json.getJSONObject("commonWeapons").getJSONObject("Weapon").getString("preset"))) {
                            if (weaponPreset.get("Weapon") instanceof JSONArray) {
                                JSONArray weaponArray = weaponPreset.getJSONArray("Weapon");
                                for (int k = 0; k < weaponArray.length(); k++) {
                                    if (weaponArray.getJSONObject(k).getString("trigger").equals("cannon")) {
                                        String blk = weaponArray.getJSONObject(k).getString("blk");
                                        blk = blk.substring(blk.lastIndexOf("/") + 1).replace(".blk", "");
                                        String gunName = !MakeGuns.getName(blk).contains("bomb") ? MakeGuns.getName(blk) : blk;
                                        gunCount.put(gunName, gunCount.getOrDefault(gunName, 0) + 1);
                                    }
                                }

                            } else {
                                if (weaponPreset.get("Weapon") instanceof JSONObject) {
                                    var weaponArray = weaponPreset.getJSONObject("Weapon");
                                    if (weaponArray.getString("trigger").equals("cannon")) {
                                        String blk = weaponArray.getString("blk");
                                        blk = blk.substring(blk.lastIndexOf("/") + 1).replace(".blk", "");
                                        String gunName = !MakeGuns.getName(blk).contains("bomb") ? MakeGuns.getName(blk) : blk;
                                        gunCount.put(gunName, gunCount.getOrDefault(gunName, 0) + 1);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e2) {
            }
        }
        for (Map.Entry<String, Integer> entry : gunCount.entrySet()) {
            if (entry.getValue() > 1) {
                blkString.append(entry.getValue()).append("x ").append(entry.getKey()).append("\n");
            } else {
                blkString.append(entry.getKey()).append("\n");
            }
        }
        return blkString.toString();
    }

    public String getData(long id) {

        Plane p = null;
        for (int i = 0; i < planes.size(); i++) {

            if (planes.get(i).uid == id) {
                p = planes.get(i);
                break;
            }
        }
        if (p.data != null)
            return p.data;

        String brs = new TableBuilder()
                .addHeaders("AB", "RB", "SB")
                .setValues(new String[][]{
                        {p.brAB, p.brRB, p.brSB}
                }).addRowNames("BR")
                .setName("")
                .frame(true)
                .setBorders(TableBuilder.Borders.newFrameBorders('-', '|', '+', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '))
                .build();

        String rips = null;
        //     if (p.info != null) {
        if (p.ripSpeedMachSwept == null)
            rips = "Rip Speeds (km/h)" +
                    "\n     Wings:      " + p.ripSpeedKph +
                    "\n     Wings Mach: " + p.ripSpeedMach +
                    "\n     Gear:       " + p.gearRipSpeed +
                    "\nFlaps" +
                    "\n     Combat:   " + p.combatRip +
                    "\n     Take-off: " + p.takeOffRip +
                    "\n     Landing:  " + p.landingRip
                    ;
        else {
            //  System.out.println(p.info[6]);
            rips = "Rip Speeds (km/h)" +
                    "\n     Wings:      " +
                    "\n       At 0% Wing sweep:   " + p.ripSpeedKph +
                    "\n       At 100% Wing sweep: " + p.ripSpeedKphSwept +
                    "\n     Wings Mach: " +
                    "\n       At 0% Wing sweep:   " + p.ripSpeedMach +
                    "\n       At 100% Wing sweep: " + p.ripSpeedMachSwept +
                    "\n     Gear:       " + p.gearRipSpeed +
                    "\nFlaps" +
                    "\n     Combat:     " + p.combatRip +
                    "\n     Take-off:   " + p.takeOffRip +
                    "\n     Landing:    " + p.landingRip
            ;
        }
        System.out.println(p.ripSpeedKphSwept + " " + p.ripSpeedKphSwept);
        // System.out.println(rips);

        String repCost = new TableBuilder()
                //.setAlignment(TableBuilder.CENTER) // setting center alignment (already set by default)
                .addHeaders("AB", "RB", "SB") // setting headers
                .setValues(new String[][]{ // setting values as 2d array
                        {p.repCostAB, p.repCostRB, p.repCostSB}
                }).addRowNames("SL") // setting row names
                .setName("") // the name (displayed in the top left corner)
                .frame(true) // activating framing
                .setBorders(TableBuilder.Borders.newFrameBorders('-', '|', '+', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '))
                .build();
        String crew = new TableBuilder()
                //.setAlignment(TableBuilder.CENTER) // setting center alignment (already set by default)
                .addHeaders("Basic", "Expert", "Ace") // setting headers
                .setValues(new String[][]{ // setting values as 2d array
                        {p.crewCost + " SL", p.expertCost + " SL", p.aceCrewCostGE + " GE"}
                }).addRowNames("") // setting row names
                .setName("") // the name (displayed in the top left corner)
                .frame(true) // activating framing
                .setBorders(TableBuilder.Borders.newFrameBorders('-', '|', '+', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '))
                .build();
        crew += "\nAce RP: " + p.aceRPCost + " RP";
        crew += "\nCost:   " + p.slCost + " " + "SL";

        String multipliers = new TableBuilder()
                .addHeaders("AB", "RB", "SB") // setting headers
                .setValues(new String[][]{ // setting values as 2d array
                        {(int) (Double.parseDouble(p.SLmultiAB) * 100) + "%", (int) (Double.parseDouble(p.SLmultiRB) * 100) + "%", (int) (Double.parseDouble(p.SLmultiSB) * 100) + "%"},
                        {(int) (Double.parseDouble(p.rpmulti) * 100) + "%", (int) (Double.parseDouble(p.rpmulti) * 100) + "%", (int) (Double.parseDouble(p.rpmulti) * 100) + "%"}

                }).addRowNames("SL", "RP") // setting row names
                .setName("") // the name (displayed in the top left corner)
                .frame(true) // activating framing
                .setBorders(TableBuilder.Borders.newFrameBorders('-', '|', '+', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '))
                .build();
        String moreInfo =
                "\nMore Info: " +
                        "\n     Empty mass:     " + p.emptyWeight + " kg" +
                        "\n     Fuel mass:      " + p.fullWeight + " kg" +
                        "\n     WingSpan:       " + p.wingSpan + "m" +
                        "\n     FM name:        " + p.actualName;


        if (p.guns == null)
            p.data = "\n\n" + rips + "\n" + brs + "\n\n Repair Cost: \n" + repCost + "\nCrew Cost \n" + crew + "\n\nMultipliers: \n" + multipliers + moreInfo;
        else
            p.data = "\nGuns:\n" + p.guns + "\n" + rips + "\n" + brs + "\n\n Repair Cost: \n" + repCost + "\nCrew Cost \n" + crew + "\n\nMultipliers: \n" + multipliers + moreInfo;


        p.data = p.data.replace("null", "N/A");
        return p.data;
    }

    public String getLoadouts(long id) {
        // id = 0;
        Plane p = null;
        for (int i = 0; i < planes.size(); i++) {
            //   System.out.println(planes.get(i).uid);

            if (planes.get(i).uid == id) {
                p = planes.get(i);
                //          System.out.println(planes.get(i));
                break;
            }
        }

        String data = null;
        try {
            data = Files.readString(Path.of("Data/AllFMs/" + p.dir + ".blkx"));//TODO
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject fmJson = new JSONObject(data);
        var actualFM = new JSONObject(data);
        fmJson = fmJson.getJSONObject("weapon_presets");
        var arr = new JSONArray();
        try {
            arr = fmJson.getJSONArray("preset");
        } catch (Exception EE) {
            return null;
        }
        var maxLen = 0;
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            var tmp = arr.getJSONObject(i).getString("blk").split("/");
            var pagamn = tmp[tmp.length - 1].replace(".blkx", "").replace(".blk", "");
            try {
                data = Files.readString(Path.of("Data/Presets/" + pagamn + ".blkx"));//TODO
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            names.add("");
            var presetJson = new JSONObject(data);

            try {
                if (!presetJson.isEmpty() && presetJson.has("Weapon")) {
                    var presetArr = presetJson.getJSONArray("Weapon");
                    for (int j = 0; j < presetArr.length(); j++) {
                        if (maxLen < presetArr.getJSONObject(j).getInt("slot"))
                            maxLen = presetArr.getJSONObject(j).getInt("slot");
                    }
                }
            } catch (Exception e) {
                try {
                    if (!presetJson.isEmpty()) {
                        var presetArr = presetJson.getJSONObject("Weapon");
                        if (maxLen < presetArr.getInt("slot"))
                            maxLen = presetArr.getInt("slot");
                    }
                } catch (Exception ee) {
                    maxLen = presetJson.length();
                    if (presetJson.isEmpty()) {
                        // System.out.println(pagamn);
                        return null;

                    }
                }
            }
        }
        var name = "";
        var table = new String[names.size()][];
        names.clear();
        for (int i = 0; i < arr.length(); i++) {
            var tmp = arr.getJSONObject(i).getString("blk").split("/");
            var pagamn = tmp[tmp.length - 1].replace(".blkx", "").replace(".blk", "");
            try {
                data = Files.readString(Path.of("Data/Presets/" + pagamn + ".blkx"));//TODO
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            var tmpstr = new String[maxLen];
            var presetJson = new JSONObject(data);
            try {
                var presetArr = presetJson.getJSONArray("Weapon");
                for (int j = 0; j < presetArr.length(); j++) {
                    tmpstr[presetArr.getJSONObject(j).getInt("slot") - 1] = "x";
                    name = presetArr.getJSONObject(j).getString("preset");
                }
            } catch (Exception e) {
                try {
                    if (!presetJson.isEmpty()) {
                        var presetArr = presetJson.getJSONObject("Weapon");
                        tmpstr[presetArr.getInt("slot") - 1] = "x";
                        name = presetArr.getString("preset");
                    }
                } catch (Exception ee) {

                }
            }
            table[i] = tmpstr;
            //System.out.println(name.length());
            var s = getNamePreset(actualFM, name);
            if (s != null)
                names.add(s.replace("3x 500 kg FAB-500M-62 bomb", name));
        }
        var namees = new String[names.size()];
        for (int i = 0; i < names.size(); i++) {
            namees[i] = names.get(i);
        }
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (table[i][j] == null)
                    table[i][j] = " ";
            }
        }
        String multipliers = new TableBuilder()
                .setValues(table).addRowNames(namees)
                .setName("")
                .frame(true)
                .setBorders(TableBuilder.Borders.newFrameBorders('-', '|', '+', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '))
                .build();
        return "Weapon Presets for " + p.name + "\n" + multipliers;
    }

    String getNamePreset(JSONObject fmJson, String val) {
        try {
            var slots = fmJson.getJSONObject("WeaponSlots").getJSONArray("WeaponSlot");
            for (int i = 0; i < slots.length(); i++) {
                try {
                    var weaponPresets = slots.getJSONObject(i).getJSONArray("WeaponPreset");
                    for (int j = 0; j < weaponPresets.length(); j++) {
                        if (weaponPresets.getJSONObject(j).getString("name").equals(val)) {
                            if (!weaponPresets.getJSONObject(j).getJSONObject("Weapon").getString("blk").contains("containers")) {
                                var weapon = weaponPresets.getJSONObject(j).getJSONObject("Weapon").getString("blk").split("/");
                                var blk = weapon[weapon.length - 1].replace(".blk", "");
                                if (!MakeGuns.getName(blk + "/short").contains("500 kg FAB-500M-62"))
                                    return MakeGuns.getName(blk + "/short").replace("\"", "");
                                else
                                    return MakeGuns.getName(blk).replace("\"", "");
                            } else {
                                String data;
                                try {
                                    var weapon = weaponPresets.getJSONObject(j).getJSONObject("Weapon").getString("blk").split("/");
                                    var blk = weapon[weapon.length - 1];
                                    data = Files.readString(Path.of("Data/Presets/containers/" + blk + "x"));
                                } catch (IOException eeeee) {
                                    throw new RuntimeException(eeeee);
                                }
                                JSONObject container = new JSONObject(data);
                                int amount = container.getInt("bullets");
                                var blk2 = container.getString("blk").split("/")[container.getString("blk").split("/").length - 1].replace(".blk", "");
                                if (!MakeGuns.getName(blk2 + "/short").contains("500 kg FAB-500M-62"))
                                    return amount + "x " + MakeGuns.getName(blk2 + "/short").replace("\"", "");
                                else
                                    return amount + "x " + MakeGuns.getName(blk2).replace("\"", "");
                            }
                        }
                    }
                } catch (Exception e) {
                    try {
                        var weaponPresets = slots.getJSONObject(i).getJSONObject("WeaponPreset");
                        if (weaponPresets.getString("name").equals(val)) {
                            if (!weaponPresets.getJSONObject("Weapon").getString("blk").contains("containers")) {
                                var weapon = weaponPresets.getJSONObject("Weapon").getString("blk").split("/");
                                var blk = weapon[weapon.length - 1].replace(".blk", "");

                                if (!MakeGuns.getName(blk + "/short").contains("500 kg FAB-500M-62"))
                                    return MakeGuns.getName(blk + "/short").replace("\"", "");
                                else
                                    return MakeGuns.getName(blk).replace("\"", "");
                            } else {
                                String data;
                                try {
                                    var weapon = weaponPresets.getJSONObject("Weapon").getString("blk").split("/");
                                    var blk = weapon[weapon.length - 1];
                                    data = Files.readString(Path.of("Data/Presets/containers/" + blk + "x"));
                                } catch (IOException eeeee) {
                                    throw new RuntimeException(e);
                                }
                                JSONObject container = new JSONObject(data);
                                int amount = container.getInt("bullets");
                                var blk2 = container.getString("blk").split("/")[container.getString("blk").split("/").length - 1].replace(".blk", "");
                                if (!MakeGuns.getName(blk2 + "/short").contains("500 kg FAB-500M-62"))
                                    return amount + "x " + MakeGuns.getName(blk2 + "/short").replace("\"", "");
                                else
                                    return amount + "x " + MakeGuns.getName(blk2).replace("\"", "");
                            }

                        }
                    } catch (Exception ee) {
                        return val;
                    }
                }
            }
            return val;
        } catch (Exception e) {
            return null;
        }
    }

    public void readPlanes() {

        try {
            var f = new File(saveLoc);
            BufferedInputStream bis = new BufferedInputStream(f.toURL().openStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }
            byte[] largeFileBytesToCheck = buf.toByteArray();
            planes = SerializationUtils.deserialize(largeFileBytesToCheck);
            System.out.println("s: " + planes.size());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void savePlanes() {

        byte[] data = SerializationUtils.serialize(planes);
        try {
            Files.write(Path.of(saveLoc), data);
        } catch (IOException e) {
            System.out.println("FUCK");
            e.printStackTrace();
        }
    }

    public static String getBR(int br) {
        int br1 = 1;
        int dot = 0;
        if (br == 0)
            return "1.0";
        while (br != 0) {
            if (dot == 0) {
                dot = 3;
                br--;
            } else if (dot == 3) {
                dot = 7;
                br--;
            } else if (dot == 7) {
                dot = 0;
                br1++;
                br--;
            }

        }
        return br1 + "." + dot;
    }

    public Plane getPlane(Long id) {
        Plane p = null;
        for (int i = 0; i < planes.size(); i++) {
            //   System.out.println(planes.get(i).uid);

            if (Objects.equals(planes.get(i).uid, id)) {
                return planes.get(i);
                //          System.out.println(planes.get(i));

            }
        }
        return null;
    }

    public static void makeThrustTables(JSONObject json, String name) {
        var usesOld = false;
        var added = false;
   //     var actualactual = planeName;
        hasTwoEngines = false;
        hasTwoEngineTypes = false;

        JSONObject json1 = null;

        try {
            if (!name.equals("yak_141")) {
                hasTwoEngineTypes = json.has("EngineType1");
                hasTwoEngines = json.has("Engine1");
            }
            json1 = json.getJSONObject("EngineType0").getJSONObject("Main");


        } catch (Exception e) {
            try {
                json1 = json.getJSONObject("Engine0").getJSONObject("Main");
                usesOld = true;

            } catch (Exception ee) {
                //return;
            }
        }
        if (json1.has("Type"))
            if (json1.getString("Type").equals("Jet")) {
                type = "IAS";
                if (json1.getJSONObject("ThrustMax").has("VelocityType"))
                    type = "TAS";
                var thustMax0 = json1.getJSONObject("ThrustMax").getBigDecimal("ThrustMax0").doubleValue();
                var AfterburnerBoost = json1.getBigDecimal("AfterburnerBoost").doubleValue();
                double thrustMult = 1.0;
                if (json1.has("Mode3")) {
                    thrustMult = json1.getJSONObject("Mode3").getBigDecimal("ThrustMult").doubleValue();
                }
                if (json1.has("Mode4")) {
                    thrustMult = json1.getJSONObject("Mode4").getBigDecimal("ThrustMult").doubleValue();
                }
                if (json1.has("Mode5")) {
                    thrustMult = json1.getJSONObject("Mode5").getBigDecimal("ThrustMult").doubleValue();
                }
                if (json1.has("Mode6")) {
                    thrustMult = json1.getJSONObject("Mode6").getBigDecimal("ThrustMult").doubleValue();
                }
                if (json1.has("Mode7")) {
                    thrustMult = json1.getJSONObject("Mode7").getBigDecimal("ThrustMult").doubleValue();
                }
                if (json1.has("Mode8")) {
                    thrustMult = json1.getJSONObject("Mode8").getBigDecimal("ThrustMult").doubleValue();
                }
                if (json1.has("Mode9")) {
                    thrustMult = json1.getJSONObject("Mode9").getBigDecimal("ThrustMult").doubleValue();
                }
                List<String> endings = getEndings(json1);
                thrust = new double[altList.size()][speedList.size()];
                json1 = json1.getJSONObject("ThrustMax");
                double Thrust;
                List<Double> ThrustList = new ArrayList<>();
                try {
                    for (int j = 0; j < altList.size(); j++) {
                        for (int i = 0; i < speedList.size(); i++) {
                            try {

                                var ThrustMaxCoeff = json1.getBigDecimal("ThrustMaxCoeff" + "_" + j + "_" + i).doubleValue();
                                double ThrAftMaxCoeff = 1.0;
                                try {
                                    ThrAftMaxCoeff = json1.getBigDecimal("ThrAftMaxCoeff" + "_" + j + "_" + i).doubleValue();
                                } catch (Exception ignored) {
                                }
                                Thrust = ThrustMaxCoeff * ThrAftMaxCoeff * thustMax0 * thrustMult * AfterburnerBoost;
                                //     System.out.println("Thrust "+ThrustList);
                                ThrustList.add(Thrust);
                            } catch (Exception e) {
                                break;
                            }
                        }
                        //break;
                        for (int i = 0; i < thrust[j].length; i++) {
                            thrust[j][i] = ThrustList.get(i);
                        }
                        ThrustList.clear();
                    }
                } catch (Exception eee) {
                    System.out.println(eee + "  " + speedList.size() + " " + altList.size());
                }
                if (!usesOld)
                    try {
                        json1 = json.getJSONObject("EngineType1").getJSONObject("Main");
                    } catch (Exception eee) {
                        json1 = json.getJSONObject("Engine0").getJSONObject("Main");
                    }
                else
                    json1 = json.getJSONObject("Engine0").getJSONObject("Main");
                if (hasTwoEngineTypes && hasTwoEngines || hasTwoEngines && usesOld) {
                    hasTwoEngineTypes = true;


                    if (json1.has("Type"))
                        if (json1.getString("Type").equals("Jet")) {
                            thustMax0 = json1.getJSONObject("ThrustMax").getBigDecimal("ThrustMax0").doubleValue();
                            AfterburnerBoost = json1.getBigDecimal("AfterburnerBoost").doubleValue();
                            thrustMult = 1.0;
                            if (json1.has("Mode3")) {
                                thrustMult = json1.getJSONObject("Mode3").getBigDecimal("ThrustMult").doubleValue();
                            }
                            if (json1.has("Mode4")) {
                                thrustMult = json1.getJSONObject("Mode4").getBigDecimal("ThrustMult").doubleValue();
                            }
                            if (json1.has("Mode5")) {
                                thrustMult = json1.getJSONObject("Mode5").getBigDecimal("ThrustMult").doubleValue();
                            }
                            if (json1.has("Mode6")) {
                                thrustMult = json1.getJSONObject("Mode6").getBigDecimal("ThrustMult").doubleValue();
                            }
                            if (json1.has("Mode7")) {
                                thrustMult = json1.getJSONObject("Mode7").getBigDecimal("ThrustMult").doubleValue();
                            }
                            if (json1.has("Mode8")) {
                                thrustMult = json1.getJSONObject("Mode8").getBigDecimal("ThrustMult").doubleValue();
                            }
                            if (json1.has("Mode9")) {
                                thrustMult = json1.getJSONObject("Mode9").getBigDecimal("ThrustMult").doubleValue();
                            }
                            endings = getEndings(json1);
                            thrust2 = new double[altList.size()][speedList.size()];
                            json1 = json1.getJSONObject("ThrustMax");
                            ThrustList = new ArrayList<>();
                            try {
                                for (int j = 0; j < altList.size(); j++) {
                                    for (int i = 0; i < speedList.size(); i++) {
                                        try {

                                            var ThrustMaxCoeff = json1.getBigDecimal("ThrustMaxCoeff" + "_" + j + "_" + i).doubleValue();
                                            double ThrAftMaxCoeff = 1.0;
                                            try {
                                                ThrAftMaxCoeff = json1.getBigDecimal("ThrAftMaxCoeff" + "_" + j + "_" + i).doubleValue();
                                            } catch (Exception ignored) {
                                            }
                                            Thrust = ThrustMaxCoeff * ThrAftMaxCoeff * thustMax0 * thrustMult * AfterburnerBoost;
                                            ThrustList.add(Thrust);
                                        } catch (Exception eee) {
                                        }
                                    }
                                    //break;
                                    for (int i = 0; i < thrust2[j].length; i++) {
                                        thrust2[j][i] = ThrustList.get(i);
                                    }
                                    ThrustList.clear();
                                }
                            } catch (Exception eee) {
                                System.out.println(eee + "  " + speedList.size() + " " + altList.size());
                            }
                        }
                }
            }
    }


    static List<String> getEndings(JSONObject json1) {
        var altEndings = new ArrayList<String>();
        var speedEndings = new ArrayList<String>();
        var finalList = new ArrayList<String>();
        var json = json1.getJSONObject("ThrustMax");
        altList.clear();
        speedList.clear();
        for (int i = 0; i <= 12; i++) {
            String altKey = "Altitude_" + i;
            if (json.has(altKey)) {
                altList.add(json.getBigDecimal(altKey).intValue());
                altEndings.add("_" + i);
            }
        }
        for (int i = 0; i <= 15; i++) {
            String speedKey = "Velocity_" + i;
            if (json.has(speedKey)) {
                speedEndings.add("_" + i);
                speedList.add(json.getBigDecimal(speedKey).intValue());
            }
        }
        for (String altEnding : altEndings) {
            for (String speedEnding : speedEndings) {
                finalList.add(altEnding + speedEnding);
            }
        }
        return finalList;
    }

    public static Plane getPlaneById (long id) {
        for (int i = 0; i < planes.size(); i++) {
            if (planes.get(i).uid == id)
            {
                return planes.get(i);
            }
        }
        return null;
    }


}
