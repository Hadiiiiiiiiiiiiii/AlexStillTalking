package info.templates;

import info.ThrustGraph;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plane implements Serializable {
    public String name;
    public long uid;
    public File imgFile;
    public String dir;
    public String actualName;
    public String slCost;
    String rpCost;
    public String brRB;
    public String brSB;
    public String brAB;
    public String repCostRB;
    public String repCostSB;
    public String repCostAB;
    public String crewCost;
    public String aceCrewCostGE;
    public String SLmultiAB;
    public String SLmultiRB;
    public String SLmultiSB;
    public String rpmulti;
    public String expertCost;
    public String aceRPCost;
    public String landingRip;
    public String takeOffRip;
    public String combatRip;
    public String data;
    public static String gameVer;
    public String guns;
    public boolean cxcheck;

    public int engineCount = 0;

    static {
        try {
            gameVer = Files.readString(Path.of("Data/Other/version"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> alts = new ArrayList<>();
    public boolean dualEngine = false;
    public boolean newFm = true;
    public List<Integer> speedList = new ArrayList<>();
    public boolean isactualJet;
    public boolean isProp;
    public boolean isJetOrRocket;
    double vy = 0;
    double drag = 0;
    public String velType;
    public boolean hasTwoDiffEngineTypes = false;
    public double[][] thrusts;
    public double[][] thrusts2;
    public String emptyWeight;
    public String fullWeight;
    public String length;
    public String wingSpan;
    public String ripSpeedKph;
    public String ripSpeedMach;
    public String ripSpeedKphSwept;
    public String ripSpeedMachSwept;
    public String gearRipSpeed;
    public String flapInfo;
    public String fuelWeight;

    public Plane(String name, long uid, String actualName, String dir, String slCost, String rpCost, String brRB, String brSB, String brAB,
                 String repCostRB, String repCostSB, String repCostAB, String crewCost, String expertCost, String aceCrewCostGE, String aceRPCost, String SLmultiAB, String SLmultiRB, String SLmultiSB, String
                         rpmulti, List<Integer> alts, List<Integer> speedList, double[][] thrusts, String type, boolean hasTwoDiffEngineTypes, double[][] thrusts2, boolean hasTwoEngines, String guns) {
        this(name, uid, actualName, dir, slCost, brRB, brSB, brAB, repCostRB, repCostSB, repCostAB, crewCost, expertCost, aceCrewCostGE, aceRPCost, SLmultiAB, SLmultiRB, SLmultiSB, rpmulti, alts, speedList, thrusts, type, hasTwoDiffEngineTypes, thrusts2, hasTwoEngines, guns);
    }

    public Plane(String name, long uid, String actualName, String dir, String slCost, String brRB, String brSB, String brAB,
                 String repCostRB, String repCostSB, String repCostAB, String crewCost, String expertCost, String aceCrewCostGE, String aceRPCost, String SLmultiAB, String SLmultiRB, String SLmultiSB, String
                         rpmulti, List<Integer> alts, List<Integer> speedList, double[][] thrusts, String type, boolean hasTwoDiffEngineTypes, double[][] thrusts2, boolean hasTwoEngines, String guns) {
        this.dualEngine = hasTwoEngines;
        if (thrusts2 != null)
            this.thrusts2 = thrusts2.clone();
        if (!guns.equals("\n") && !guns.equals(" ") && !guns.equals("")) {
            this.guns = guns.replace("\"", "");
        }
        this.hasTwoDiffEngineTypes = hasTwoDiffEngineTypes;
        if (thrusts != null)
            this.thrusts = thrusts.clone();
        this.velType = type;
        this.speedList.addAll(speedList);
        this.aceRPCost = aceRPCost;
        this.actualName = actualName.replace("/", "");
        this.name = name.replace("\"", "");
        if (name.contains("t帜")) {
            this.name = actualName;
        }
        this.uid = uid;
        this.imgFile = new File(("Data/Pics/" + actualName + ".png"));
        this.dir = dir;
        this.slCost = slCost;
        this.rpCost = rpCost;
        this.brRB = brRB;
        this.brSB = brSB;
        this.brAB = brAB;
        this.repCostRB = repCostRB;
        this.repCostSB = repCostSB;
        this.repCostAB = repCostAB;
        this.crewCost = crewCost;
        this.aceCrewCostGE = aceCrewCostGE;
        this.SLmultiAB = SLmultiAB;
        this.SLmultiRB = SLmultiRB;
        this.SLmultiSB = SLmultiSB;
        this.rpmulti = rpmulti;
        this.expertCost = expertCost;

        this.alts.addAll(alts);//&& name.toLowerCase().contains("mig")
        var jsonObject = getPlaneJson();
        getInfo(jsonObject);

        // System.out.println("COMPONENTLENGTH: "+dragcomponents);
        try {
            SetupFlaps();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            double cdmin = 0;
            //var jsonObject = getPlaneJson();
            try {
                cdmin += jsonObject.getJSONObject("Aerodynamics").getJSONObject("HorStabPlane").getJSONObject("Polar").getBigDecimal("CdMin").doubleValue();
                newFm = true;
                cxcheck = true;

            } catch (Exception e) {
                newFm = false;
            }
            try {
                cdmin += jsonObject.getJSONObject("Aerodynamics").getBigDecimal("CxAfterCoeff").doubleValue();
                cxcheck = true;

            } catch (Exception e) {

            }
            try {
                if (jsonObject.has("Propeller0")) {
                    isProp = true;
                }
                if (jsonObject.getJSONObject("EngineType0").getJSONObject("Main").getString("Type").equals("Jet") || jsonObject.getJSONObject("EngineType0").getJSONObject("Main").getString("Type").equals("Rocket")) {
                    isJetOrRocket = true;
                    if (!jsonObject.getJSONObject("EngineType0").getJSONObject("Main").getString("Type").equals("Rocket"))
                        isactualJet = true;
                } else
                    isProp = true;
            } catch (Exception e) {
                if (jsonObject.getJSONObject("Engine0").getJSONObject("Main").getString("Type").equals("Jet") || jsonObject.getJSONObject("Engine0").getJSONObject("Main").getString("Type").equals("Rocket")) {
                    isJetOrRocket = true;
                    if (!jsonObject.getJSONObject("Engine0").getJSONObject("Main").getString("Type").equals("Rocket"))
                        isactualJet = true;
                } else
                    isProp = true;
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        try {
            if (isactualJet) {
                for (int i = 0; i < 10; i++) {
                    String engine = "Engine" + i;
                    if (jsonObject.has(engine)) {
                        if (!jsonObject.has("Booster"))
                            engineCount++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    String getCombats(double x1, double y1, double y2, double x2, double x) {
        // if (x == 0.0)
        //     x = 0.2;
        var o = x - x1;
        var d = x2 - x1;
        var c = y2 - y1;
        return (int) ((((o) / (d)) * (c)) + y1) + "";
    }

    String getTakeOffsRip(double x1, double y1, double y2, double x2, double x) {
        //if (x == 0.0)
        //    x = 0.33;

        var o = x - x1;
        var d = x2 - x1;
        var c = y2 - y1;
        return (int) ((((o) / (d)) * (c)) + y1) + "";
    }

    void getTakeOffs(double multi) {
        List<String[]> s = new ArrayList<>();
        if (flapInfo.equals("")) {
            landingRip = "N/A";
            takeOffRip = "N/A";
            combatRip = "N/A";
            return;
        }
        var split = flapInfo.split(",");
        var x1 = 0.00;
        var y1 = 0.00;
        var y2 = 0.00;
        var x2 = 0.00;
        if (split.length == 6) {
            var pair1 = new String[]{split[0], split[1]};
            var pair2 = new String[]{split[2], split[3]};
            var pair3 = new String[]{split[4], split[5]};
            s.add(pair1);
            s.add(pair2);
            s.add(pair3);

            if (Double.parseDouble(pair1[0]) <= 0.33 && Double.parseDouble(pair2[0]) >= 0.33) {
                x1 = Double.parseDouble(pair1[0]);
                y1 = Double.parseDouble(pair1[1]);
                y2 = Double.parseDouble(pair2[1]);
                x2 = Double.parseDouble(pair2[0]);
            } else if (Double.parseDouble(pair2[0]) <= 0.33 && Double.parseDouble(pair3[0]) >= 0.33) {
                x1 = Double.parseDouble(pair2[0]);
                y1 = Double.parseDouble(pair2[1]);
                y2 = Double.parseDouble(pair3[1]);
                x2 = Double.parseDouble(pair3[0]);
            }
            takeOffRip = getTakeOffsRip(x1, y1, y2, x2, multi);

        } else if (split.length == 4) {
            x1 = Double.parseDouble(split[0]);
            y1 = Double.parseDouble(split[1]);
            y2 = Double.parseDouble(split[3]);
            x2 = Double.parseDouble(split[2]);
            if ((int) (x1 * 100) == 33)
                takeOffRip = y1 + "";
            else
                takeOffRip = getTakeOffsRip(x1, y1, y2, x2, multi);
        } else if (split.length == 2) {
            takeOffRip = "N/A";
        } else if (split.length == 8) {
            var pair1 = new String[]{split[0], split[1]};
            var pair2 = new String[]{split[2], split[3]};
            var pair3 = new String[]{split[4], split[5]};
            var pair4 = new String[]{split[6], split[7]};
            s.add(pair1);
            s.add(pair2);
            s.add(pair3);
            s.add(pair4);
            if (Double.parseDouble(pair1[0]) <= 0.33 && Double.parseDouble(pair2[0]) >= 0.33) {
                x1 = Double.parseDouble(pair1[0]);
                y1 = Double.parseDouble(pair1[1]);
                y2 = Double.parseDouble(pair2[1]);
                x2 = Double.parseDouble(pair2[0]);
            } else if (Double.parseDouble(pair2[0]) <= 0.33 && Double.parseDouble(pair3[0]) >= 0.33) {
                x1 = Double.parseDouble(pair2[0]);
                y1 = Double.parseDouble(pair2[1]);
                y2 = Double.parseDouble(pair3[1]);
                x2 = Double.parseDouble(pair3[0]);
            } else if ((Double.parseDouble(pair3[0]) <= 0.33 && Double.parseDouble(pair2[0]) >= 0.33)) {
                x1 = Double.parseDouble(pair3[0]);
                y1 = Double.parseDouble(pair3[1]);
                y2 = Double.parseDouble(pair2[1]);
                x2 = Double.parseDouble(pair2[0]);
            }
            takeOffRip = getTakeOffsRip(x1, y1, y2, x2, multi);
        } else if (split.length == 10) {
            var pair1 = new String[]{split[0], split[1]};
            var pair2 = new String[]{split[2], split[3]};
            var pair3 = new String[]{split[4], split[5]};
            var pair4 = new String[]{split[6], split[7]};
            var pair5 = new String[]{split[8], split[9]};
            s.add(pair1);
            s.add(pair2);
            s.add(pair3);
            s.add(pair4);
            s.add(pair5);
            if (Double.parseDouble(pair1[0]) <= 0.33 && Double.parseDouble(pair2[0]) >= 0.33) {
                x1 = Double.parseDouble(pair1[0]);
                y1 = Double.parseDouble(pair1[1]);
                y2 = Double.parseDouble(pair2[1]);
                x2 = Double.parseDouble(pair2[0]);
            } else if (Double.parseDouble(pair2[0]) <= 0.33 && Double.parseDouble(pair3[0]) >= 0.33) {
                x1 = Double.parseDouble(pair2[0]);
                y1 = Double.parseDouble(pair2[1]);
                y2 = Double.parseDouble(pair3[1]);
                x2 = Double.parseDouble(pair3[0]);
            } else if ((Double.parseDouble(pair3[0]) <= 0.33 && Double.parseDouble(pair4[0]) >= 0.33)) {
                x1 = Double.parseDouble(pair3[0]);
                y1 = Double.parseDouble(pair3[1]);
                y2 = Double.parseDouble(pair4[1]);
                x2 = Double.parseDouble(pair4[0]);
            } else if ((Double.parseDouble(pair4[0]) <= 0.33 && Double.parseDouble(pair5[0]) >= 0.33)) {
                x1 = Double.parseDouble(pair4[0]);
                y1 = Double.parseDouble(pair4[1]);
                y2 = Double.parseDouble(pair5[1]);
                x2 = Double.parseDouble(pair5[0]);
            }
            takeOffRip = getTakeOffsRip(x1, y1, y2, x2, multi);
        }
        if (takeOffRip == null) {
            for (int i = 0; i < s.size(); i++) {
                if (s.get(i)[0].equals("0.33")) {
                    takeOffRip = s.get(i)[1];
                }
            }
        }
    }

    void getCombat(double multi) {
        List<String[]> s = new ArrayList<>();
        if (flapInfo.equals("")) {
            landingRip = "N/A";
            takeOffRip = "N/A";
            combatRip = "N/A";
            return;
        }
        var split = flapInfo.split(",");
        var x1 = 0.00;
        var y1 = 0.00;
        var y2 = 0.00;
        var x2 = 0.00;
        if (split.length == 6) {
            var pair1 = new String[]{split[0], split[1]};
            var pair2 = new String[]{split[2], split[3]};
            var pair3 = new String[]{split[4], split[5]};
            s.add(pair1);
            s.add(pair2);
            s.add(pair3);
            if (Double.parseDouble(pair1[0]) <= 0.2 && Double.parseDouble(pair2[0]) >= 0.2) {
                x1 = Double.parseDouble(pair1[0]);
                y1 = Double.parseDouble(pair1[1]);
                y2 = Double.parseDouble(pair2[1]);
                x2 = Double.parseDouble(pair2[0]);
            } else if (Double.parseDouble(pair2[0]) <= 0.2 && Double.parseDouble(pair3[0]) >= 0.2) {
                x1 = Double.parseDouble(pair2[0]);
                y1 = Double.parseDouble(pair2[1]);
                y2 = Double.parseDouble(pair3[1]);
                x2 = Double.parseDouble(pair3[0]);
            }
            combatRip = getCombats(x1, y1, y2, x2, multi);

        } else if (split.length == 4) {
            x1 = Double.parseDouble(split[0]);
            y1 = Double.parseDouble(split[1]);
            y2 = Double.parseDouble(split[3]);
            x2 = Double.parseDouble(split[2]);

            combatRip = getCombats(x1, y1, y2, x2, multi);
        } else if (split.length == 2) {
            landingRip = split[split.length - 1];
        } else if (split.length == 8) {
            var pair1 = new String[]{split[0], split[1]};
            var pair2 = new String[]{split[2], split[3]};
            var pair3 = new String[]{split[4], split[5]};
            var pair4 = new String[]{split[6], split[7]};
            s.add(pair1);
            s.add(pair2);
            s.add(pair3);
            s.add(pair4);
            if (Double.parseDouble(pair1[0]) < 0.2 && Double.parseDouble(pair2[0]) >= 0.2) {
                x1 = Double.parseDouble(pair1[0]);
                y1 = Double.parseDouble(pair1[1]);
                y2 = Double.parseDouble(pair2[1]);
                x2 = Double.parseDouble(pair2[0]);
            } else if (Double.parseDouble(pair2[0]) < 0.2 && Double.parseDouble(pair3[0]) >= 0.2) {
                x1 = Double.parseDouble(pair2[0]);
                y1 = Double.parseDouble(pair2[1]);
                y2 = Double.parseDouble(pair3[1]);
                x2 = Double.parseDouble(pair3[0]);
            } else if ((Double.parseDouble(pair3[0]) < 0.2 && Double.parseDouble(pair2[0]) >= 0.2)) {
                x1 = Double.parseDouble(pair3[0]);
                y1 = Double.parseDouble(pair3[1]);
                y2 = Double.parseDouble(pair2[1]);
                x2 = Double.parseDouble(pair2[0]);
            }
            combatRip = getCombats(x1, y1, y2, x2, 0.2);
            landingRip = split[split.length - 1];
        } else if (split.length == 10) {
            var pair1 = new String[]{split[0], split[1]};
            var pair2 = new String[]{split[2], split[3]};
            var pair3 = new String[]{split[4], split[5]};
            var pair4 = new String[]{split[6], split[7]};
            var pair5 = new String[]{split[8], split[9]};
            s.add(pair1);
            s.add(pair2);
            s.add(pair3);
            s.add(pair4);
            s.add(pair5);

            if (Double.parseDouble(pair1[0]) < 0.2 && Double.parseDouble(pair2[0]) >= 0.2) {
                x1 = Double.parseDouble(pair1[0]);
                y1 = Double.parseDouble(pair1[1]);
                y2 = Double.parseDouble(pair2[1]);
                x2 = Double.parseDouble(pair2[0]);
            } else if (Double.parseDouble(pair2[0]) < 0.2 && Double.parseDouble(pair3[0]) >= 0.2) {
                x1 = Double.parseDouble(pair2[0]);
                y1 = Double.parseDouble(pair2[1]);
                y2 = Double.parseDouble(pair3[1]);
                x2 = Double.parseDouble(pair3[0]);
            } else if ((Double.parseDouble(pair3[0]) < 0.2 && Double.parseDouble(pair4[0]) >= 0.2)) {
                x1 = Double.parseDouble(pair3[0]);
                y1 = Double.parseDouble(pair3[1]);
                y2 = Double.parseDouble(pair4[1]);
                x2 = Double.parseDouble(pair4[0]);
            } else if ((Double.parseDouble(pair4[0]) < 0.2 && Double.parseDouble(pair5[0]) >= 0.2)) {
                x1 = Double.parseDouble(pair4[0]);
                y1 = Double.parseDouble(pair4[1]);
                y2 = Double.parseDouble(pair5[1]);
                x2 = Double.parseDouble(pair5[0]);
            }
            combatRip = getCombats(x1, y1, y2, x2, multi);
            landingRip = split[split.length - 1];

        }

        if (combatRip == null) {
            for (int i = 0; i < s.size(); i++) {
                if (s.get(i)[0].equals("0.20")) {
                    combatRip = s.get(i)[1];
                }
            }
        }

    }

    void SetupFlaps() {
        if (flapInfo.equals("")) {
            landingRip = "N/A";
            takeOffRip = "N/A";
            combatRip = "N/A";
            return;
        }
        try {
            String tnt = null; //assign your JSON String here
            try {
                tnt = Files.readString(Path.of("Data/FM/" + actualName + ".blkx"));
            } catch (IOException e) {
                System.out.println("COPE");
            }
            JSONObject tntJson = new JSONObject(tnt);
            boolean important;
            try {
                important = tntJson.getJSONObject("AvailableControls").getBoolean("hasFlapsControl");
            } catch (Exception e) {
                important = !tntJson.getJSONObject("AvailableControls").getJSONArray("hasFlapsControl").isEmpty();
            }

            if (important) {

                var in = flapInfo.split(",");
                landingRip = in[in.length - 1];
                var d = tntJson.getJSONObject("Mass");
                try {
                    if (tntJson.getJSONObject("Aerodynamics").has("FlapsAxis")) {
                        d = tntJson.getJSONObject("Aerodynamics").getJSONObject("FlapsAxis");
                        {

                            if (d.has("Takeoff")) {
                                double multi = d.getJSONObject("Takeoff").getBigDecimal("Flaps").doubleValue();
                                if (d.getJSONObject("Takeoff").getBoolean("Presents")) {
                                    getTakeOffs(multi);
                                }

                            }
                            if (d.has("Combat")) {
                                if (d.getJSONObject("Combat").getBoolean("Presents")) {
                                    double multi = d.getJSONObject("Combat").getBigDecimal("Flaps").doubleValue();
                                    getCombat(multi);
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    throw new Exception(e);
                }
                try {
                    if (tntJson.getJSONObject("AvailableControls").has("hasTakeoffFlapsPosition")) {

                        try {
                            if (tntJson.getJSONObject("AvailableControls").getBoolean("hasTakeoffFlapsPosition") && takeOffRip == null) {
                                getTakeOffs(0.33);
                            }
                            if (tntJson.getJSONObject("AvailableControls").getBoolean("hasCombatFlapsPosition") && combatRip == null) {
                                getCombat(0.2);

                            }
                        } catch (Exception e) {
                            System.out.println("plane: " + actualName);
                        }
                    }
                } catch (Exception e) {
                    throw new Exception(e);
                }

            }
        } catch (Exception e) {
            if (actualName.equals("i-225")) {
                System.out.println("pagman " + Arrays.stream(e.getStackTrace()).toList());
            }
        }
        if (actualName.equals("i-225")) {
            System.out.println("pagman " + landingRip + " " + takeOffRip + " " + combatRip + " " + flapInfo);
        }
    }

    public static String getName(String planeName) throws IOException {
        //planeName = planeName.toLowerCase().replace(" ", "_");
        List<String> keyList = new ArrayList<>();
        List<String> valList = new ArrayList<>();
        var info = new File("Data/Other/fm_data_db.csv");
        var data = Files.readString(info.toPath());
        var data2 = data.split("\n");

        info = new File("Data/Other/fm_names_db.csv");
        data = Files.readString(info.toPath());
        var data3 = data.split("\n");


        for (int i = 0; i < data2.length; i++) {
            var dataName = data2[i].split(";")[0];
            for (int k = 0; k < data3.length; k++) {
                var namesArr = data3[k].split(";")[1];
                var realname = data3[k].split(";")[0];
                if (namesArr.equals(dataName)) {
                    //  System.out.println(data2[i]);
                    keyList.add(realname);
                    valList.add(data2[i]);
                }
            }
        }

        return valList.get(keyList.indexOf(planeName)).split(";")[0];
    }

    //
    public double getThrust(double alt, double speed, double[][] thrusts) {
        double thrust = 0;
        int bottomAlt = 0;
        int upperAlt = 0;


        double bottomThrust = 0;
        double topThrust = 0;

        for (int i = 0; i < alts.size() - 1; i++) {
            if (alt >= alts.get(i) && alt <= alts.get(i + 1)) {
                bottomAlt = alts.get(i);
                upperAlt = alts.get(i + 1);
                break;
            }
        }

        SplineInterpolator interpolator = new SplineInterpolator();
        double[] speedsArray = new double[speedList.size()];
        double[] thrustsArray = thrusts[alts.indexOf(bottomAlt)];

        for (int i = 0; i < speedsArray.length; i++) {
            speedsArray[i] = speedList.get(i);
        }
        PolynomialSplineFunction lowerAltFunc = interpolator.interpolate(speedsArray, thrustsArray);
        thrustsArray = thrusts[alts.indexOf(upperAlt)];
        PolynomialSplineFunction upperAltFunc = interpolator.interpolate(speedsArray, thrustsArray);

        bottomThrust = lowerAltFunc.value(speed);
        topThrust = upperAltFunc.value(speed);
        var diff = topThrust - bottomThrust;
        var steps = upperAlt - bottomAlt;
        var step = diff / steps;
        thrust = bottomThrust;
        while (bottomAlt < alt) {
            thrust += step;
            bottomAlt++;
        }
        if (!hasTwoDiffEngineTypes && thrusts2 == null && !actualName.contains("yak_141"))
            return thrust * engineCount;
        else
            return thrust;
    }


    public void getInfo(JSONObject plane) {
        try {
            setFlapsInfo(plane);
            JSONObject mass = plane.getJSONObject("Mass");
            emptyWeight = mass.getBigDecimal("EmptyMass").toString();
            fuelWeight = String.valueOf(getFuelMass(plane));
            fullWeight = mass.getBigDecimal("MaxFuelMass0").add(mass.getBigDecimal("OilMass").add(mass.getBigDecimal("EmptyMass"))).toPlainString();
            gearRipSpeed = mass.getBigDecimal("GearDestructionIndSpeed").toString();
            length = plane.getBigDecimal("Length").toString();
            try {
                JSONObject wingPlane = plane.getJSONObject("Aerodynamics").getJSONObject("WingPlane");
                JSONObject strength = wingPlane.getJSONObject("Strength");
                ripSpeedKph = strength.getBigDecimal("VNE").toString();
                ripSpeedMach = strength.getBigDecimal("MNE").toString();
                wingSpan = wingPlane.getBigDecimal("Span").toString();
            } catch (JSONException e) {
                try {
                    ripSpeedKph = plane.getBigDecimal("Vne").toString();
                    ripSpeedMach = plane.getBigDecimal("VneMach").toString();
                    wingSpan = plane.getBigDecimal("Wingspan").toString();
                } catch (Exception ee) {
                    try {
                        System.out.println(ripSpeedKph + " " + ripSpeedMach + " " + wingSpan + " AAAA " + actualName);
                        ripSpeedKph = plane.getJSONObject("Aerodynamics").getJSONObject("WingPlaneSweep0").getJSONObject("Strength").getBigDecimal("VNE").toString();
                        ripSpeedMach = plane.getJSONObject("Aerodynamics").getJSONObject("WingPlaneSweep0").getJSONObject("Strength").getBigDecimal("MNE").toString();
                        wingSpan = plane.getJSONObject("Aerodynamics").getJSONObject("WingPlaneSweep0").getBigDecimal("Span").toString();

                        for (int i = 0; i < 4; i++) {
                            JSONObject wingPlaneSweep = plane.getJSONObject("Aerodynamics").getJSONObject("WingPlaneSweep" + i);
                            JSONObject strength = wingPlaneSweep.getJSONObject("Strength");
                            ripSpeedKphSwept = strength.getBigDecimal("VNE").toString();
                            ripSpeedMachSwept = strength.getBigDecimal("MNE").toString();
                        }
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getFuelMass(JSONObject plane) {
        double fuelMass = 0.0;
        try {
            JSONObject parts = plane.getJSONObject("Mass").getJSONObject("Parts");
            for(String key : parts.keySet()) {
                if (key.contains("capacity") && !parts.getBoolean(key.substring(0, key.indexOf("_capacity")) + "_external")) {
                    fuelMass += parts.getDouble(key);
                }
            }
        } catch (JSONException e) {
        }
        return fuelMass;
    }
    public void setFlapsInfo(JSONObject plane) {
        String ret = "";
        boolean errorOccurred = false;
        try {
            ret += plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP0").getBigDecimal(0) + ",";
            ret += plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP0").getBigDecimal(1);
        } catch (Exception e) {
            errorOccurred = true;
        }
        if (!errorOccurred) {
            ret += ",";
        }
        try {
            ret += plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP1").getBigDecimal(0) + ",";
            ret += plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP1").getBigDecimal(1);
            errorOccurred = false;
        } catch (Exception ee) {
            errorOccurred = true;
        }
        if (!errorOccurred) {
            ret += ",";
        }
        try {
            ret += plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP2").getBigDecimal(0) + ",";
            ret += plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP2").getBigDecimal(1);
        } catch (Exception eee) {
        }
        if (ret.isEmpty()) {
            try {
                JSONArray array = plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP");
                for (int i = 0; i < array.length(); i++) {
                    ret += array.getBigDecimal(i);
                    if (i < array.length() - 1) {
                        ret += ",";
                    }
                }
            } catch (Exception e) {
            }
        }
        if (actualName.contains("p-39n")) {
            System.out.println("46dd: " + ret);
        }
        this.flapInfo = ret;
    }



    private JSONObject getPlaneJson() {
        File info1 = new File("Data/FM/" + actualName + ".blkx");//TODO
        String planeJson = null;
        try {
            planeJson = Files.readString(info1.toPath());
        } catch (IOException e) {
            System.out.println("no FM ong2" + actualName);
            e.printStackTrace();
        }
        return new JSONObject(planeJson);
    }

    @Override
    public String toString() {
        return "\nNewPlane{" +
                "name='" + name + '\'' +
                ", uid=" + uid +
                ", imgLink='" + imgFile + '\'' +
                ", dir='" + dir + '\'' +
                ", actualName='" + actualName + '\'' +
                ", name2='" + name + '\'' +
                ", uid1=" + uid +
                ", slCost='" + slCost + '\'' +
                ", rpCost='" + rpCost + '\'' +
                ", brRB='" + brRB + '\'' +
                ", brSB='" + brSB + '\'' +
                ", brAB='" + brAB + '\'' +
                ", repCostRB='" + repCostRB + '\'' +
                ", repCostSB='" + repCostSB + '\'' +
                ", repCostAB='" + repCostAB + '\'' +
                ", crewCost='" + crewCost + '\'' +
                ", aceCrewCost='" + aceCrewCostGE + '\'' +
                ", SLmultiAB='" + SLmultiAB + '\'' +
                ", SLmultiRB='" + SLmultiRB + '\'' +
                ", SLmultiSB='" + SLmultiSB + '\'' +
                ", rpmulti='" + rpmulti + '\'' +
                ", expertCost='" + expertCost + '\'' +
                ", aceRPCost='" + aceRPCost + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
