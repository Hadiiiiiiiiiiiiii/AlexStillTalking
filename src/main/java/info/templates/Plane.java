package info.templates;

import info.ThrustGraph;
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
    static public int fuckedUp = 0;
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

    public Plane(String name, long uid, String actualName, String dir,  String slCost, String rpCost, String brRB, String brSB, String brAB,
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
        this.actualName = actualName;
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

        try {
            SetupFlaps();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            double cdmin = 0;
            //var jsonObject = getPlaneJson();
            try {
                cdmin += jsonObject.getJSONObject("Aerodynamics").getJSONObject("WingPlane").getJSONObject("FlapsPolar0").getBigDecimal("CdMin").doubleValue();
                if (cdmin <= 0.0) {
                    newFm = true;
                }
            } catch (Exception e) {
                newFm = false;
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

            //   System.out.println("\n\nFLAPS: "+actualName+" "+landingRip + " " + takeOffRip + " " + combatRip+"\n\n");

        } catch (Exception e) {
            fuckedUp++;
            if (actualName.equals("i-225")) {
                System.out.println("pagman " + Arrays.stream(e.getStackTrace()).toList());
            }
        }
        if (actualName.equals("i-225")) {
            System.out.println("pagman " + landingRip + " " + takeOffRip + " " + combatRip + " " + flapInfo);
        }
    }

    public int getDrag(double speed, double alt, int extraWeight, double aoa) {

        JSONObject json = getPlaneJson();
        if (json.has("Engine1")) {
            if (json.has("EngineType1")) {
                if (json.getJSONObject("EngineType1").has("Booster"))
                    dualEngine = !json.getJSONObject("EngineType1").getBoolean("Booster");
            } else
                dualEngine = true;
        }

        drag = 0;

        var owaldsNoFlaps = getNoFlapsOwalds(json);
        var owaldsFuselage = getFuselageOswalds(json);
        var owaldsHorzStab = getHorStabOwalds(json);
        var owaldsVertStab = getVertStabOwalds(json);


        var cd_NoFlaps = getNoFlapsCd(json, owaldsNoFlaps);
        var area0 = getNoFlapsArea(json);
        var cl0 = getNoFlapsCl(json, owaldsNoFlaps);

        var cd_Fuselage = getFuselageCD(json, owaldsFuselage);
        var area1 = getFuselageArea(json);
        var cl1 = getFuselageCL(json, owaldsFuselage);

        var cd_HorzStab = getHorStabCd(json, owaldsHorzStab);
        var area2 = getHorStabArea(json);
        var cl2 = getHorStabCl(json, owaldsHorzStab);

        var cd_VertStab = getVertStabCd(json, owaldsVertStab);
        var area3 = getvertStabArea(json);
        var cl3 = getVertStabCl(json, owaldsVertStab);

        var speed2 = speed * 0.277778;
        var dens = ThrustGraph.getDensityAtAlt((int) alt);

        var totalcd = cd_NoFlaps + cd_Fuselage + cd_HorzStab + cd_VertStab;
        var totalArea = getNoFlapsArea(json) + getFuselageArea(json) + getHorStabArea(json) + getvertStabArea(json);
        var totalWalads = getNoFlapsOwalds(json) + getHorStabOwalds(json) + getVertStabOwalds(json) + getFuselageOswalds(json);
        var totalcl = cl0 + cl1 + cl2 + cl3;


        var ar = Math.pow(Double.parseDouble(wingSpan), 2) / area0;
        var cd_0LiftDrag = (Math.pow(totalcl, 2)) / (Math.PI * getNoFlapsOwalds(json) * ar);
        var cd0_i_noFlaps = cd_NoFlaps + (Math.pow(cl0, 2) / (Math.PI * ar * owaldsNoFlaps));
        var cd1_i_Fuselage = cd_Fuselage + (Math.pow(cl1, 2) / (Math.PI * ar * owaldsFuselage));
        var cd2_i_HorzStab = cd_HorzStab + (Math.pow(cl2, 2) / (Math.PI * ar * owaldsHorzStab));
        var cd3_i_VertStab = cd_VertStab + (Math.pow(cl3, 2) / (Math.PI * ar * owaldsVertStab));

        var zeroLiftDrag = cd_0LiftDrag * dens * ((speed2 * speed2) / 2) * area0;
        drag += cd0_i_noFlaps * dens * ((speed2 * speed2) / 2) * area0;
        drag += cd1_i_Fuselage * dens * ((speed2 * speed2) / 2) * area1;
        drag += cd2_i_HorzStab * dens * ((speed2 * speed2) / 2) * area2;
        drag += cd3_i_VertStab * dens * ((speed2 * speed2) / 2) * area3;
        drag += zeroLiftDrag;
        //drag = drag * 0.101972;

        /*--acell--*/
        // thrust = thrust * 9.80665;
        // var force = thrust - drag;
        //  var accel = force / weight;
        //System.out.println(drag * 0.1019716);
        //System.out.println(cd_NoFlaps);
        //System.out.println(getNoFlapsOwalds(json));
        //System.out.println(area0);
        //System.out.println(zeroLiftDrag);
        //System.out.println(accel);
        //System.out.println("zero: "+zeroLiftDrag);
        //System.out.println("Thrust: "+thrust/9.80665);
        // System.out.println(totalcd*dens*((speed2*speed2)/2)*totalcd);
        return (int) (drag * 0.1019716);
    }


    static double getFuselageCD(JSONObject jsonObject, double owalds) {
        var cd = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("FuselagePlane").getJSONObject("Polar").has("CdMin")) {
                cd += jsonObject.getJSONObject("Aerodynamics").getJSONObject("FuselagePlane").getJSONObject("Polar").getBigDecimal("CdMin").doubleValue();

            }
        } catch (Exception e) {
            //System.out.println(jsonObject);
            //e.printStackTrace();
        }
        return cd;

        //return cd + owalds * Math.pow(aoa, 2);
    }

    static double getFuselageOswalds(JSONObject jsonObject) {
        var cd = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("FuselagePlane").getJSONObject("Polar").has("OswaldsEfficiencyNumber")) {
                cd += jsonObject.getJSONObject("Aerodynamics").getJSONObject("FuselagePlane").getJSONObject("Polar").getBigDecimal("OswaldsEfficiencyNumber").doubleValue();

            }
        } catch (Exception e) {

        }
        return cd;
    }

    static double getFuselageArea(JSONObject jsonObject) {
        var area = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("FuselagePlane").has("Areas")) {
                area += jsonObject.getJSONObject("Aerodynamics").getJSONObject("FuselagePlane").getJSONObject("Areas").getBigDecimal("Main").doubleValue();

            }
        } catch (Exception e) {

        }
        return area;
    }

    static double getFuselageCL(JSONObject jsonObject, double owalds) {
        var cl = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("FuselagePlane").getJSONObject("Polar").has("Cl0")) {
                cl += jsonObject.getJSONObject("Aerodynamics").getJSONObject("FuselagePlane").getJSONObject("Polar").getBigDecimal("Cl0").doubleValue();

            }
        } catch (Exception e) {

        }
        return cl;
        //return cl+ owalds * Math.pow(aoa, 2);
    }

    static double getNoFlapsCd(JSONObject jsonObject, double owalds) {
        var cdmin = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("WingPlane").getJSONObject("FlapsPolar0").has("CdMin")) {
                cdmin += jsonObject.getJSONObject("Aerodynamics").getJSONObject("WingPlane").getJSONObject("FlapsPolar0").getBigDecimal("CdMin").doubleValue();

            }
        } catch (Exception e) {

        }
        //   System.out.println(aoa);
        //   System.out.println(cdmin);
        return cdmin;
        //return cdmin + owalds * Math.pow(aoa, 2);
    }

    static double getNoFlapsOwalds(JSONObject jsonObject) {
        var cdmin = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("WingPlane").getJSONObject("FlapsPolar0").has("OswaldsEfficiencyNumber")) {
                cdmin += jsonObject.getJSONObject("Aerodynamics").getJSONObject("WingPlane").getJSONObject("FlapsPolar0").getBigDecimal("OswaldsEfficiencyNumber").doubleValue();

            }
        } catch (Exception e) {

        }
        return cdmin;
    }

    static double getNoFlapsArea(JSONObject jsonObject) {
        var cl = 0.0;

        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("WingPlane").has("Areas")) {
                var js = jsonObject.getJSONObject("Aerodynamics");
                cl += js.getJSONObject("WingPlane").getJSONObject("Areas").getBigDecimal("LeftIn").doubleValue();
                cl += js.getJSONObject("WingPlane").getJSONObject("Areas").getBigDecimal("LeftMid").doubleValue();
                cl += js.getJSONObject("WingPlane").getJSONObject("Areas").getBigDecimal("LeftOut").doubleValue();

                cl += js.getJSONObject("WingPlane").getJSONObject("Areas").getBigDecimal("RightIn").doubleValue();
                cl += js.getJSONObject("WingPlane").getJSONObject("Areas").getBigDecimal("RightMid").doubleValue();
                cl += js.getJSONObject("WingPlane").getJSONObject("Areas").getBigDecimal("RightOut").doubleValue();

                cl += js.getJSONObject("WingPlane").getJSONObject("Areas").getBigDecimal("Aileron").doubleValue();
            }
        } catch (Exception e) {

        }
        return cl;
    }

    static double getNoFlapsCl(JSONObject jsonObject, double owalds) {
        var cl = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("WingPlane").getJSONObject("FlapsPolar0").has("Cl0")) {
                cl += jsonObject.getJSONObject("Aerodynamics").getJSONObject("WingPlane").getJSONObject("FlapsPolar0").getBigDecimal("Cl0").doubleValue();

            }
        } catch (Exception e) {

        }
        // return cl + owalds * Math.pow(aoa, 2);
        return cl;

    }

    static double getHorStabCd(JSONObject jsonObject, double owalds) {
        var cl = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("HorStabPlane").getJSONObject("Polar").has("CdMin")) {
                cl += jsonObject.getJSONObject("Aerodynamics").getJSONObject("HorStabPlane").getJSONObject("Polar").getBigDecimal("CdMin").doubleValue();
            }
        } catch (Exception e) {

        }
        //return cl + owalds * Math.pow(aoa, 2);
        return cl;
    }

    static double getHorStabOwalds(JSONObject jsonObject) {
        var cl = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("HorStabPlane").getJSONObject("Polar").has("OswaldsEfficiencyNumber")) {
                cl += jsonObject.getJSONObject("Aerodynamics").getJSONObject("HorStabPlane").getJSONObject("Polar").getBigDecimal("OswaldsEfficiencyNumber").doubleValue();
            }
        } catch (Exception e) {

        }
        return cl;
    }

    static double getHorStabArea(JSONObject jsonObject) {
        var cl = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("HorStabPlane").has("Areas")) {
                cl += jsonObject.getJSONObject("Aerodynamics").getJSONObject("HorStabPlane").getJSONObject("Areas").getBigDecimal("Main").doubleValue();
                cl += jsonObject.getJSONObject("Aerodynamics").getJSONObject("HorStabPlane").getJSONObject("Areas").getBigDecimal("Elevator").doubleValue();
            }
        } catch (Exception e) {

        }
        return cl;
    }

    static double getHorStabCl(JSONObject jsonObject, double owalds) {
        var cl = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("HorStabPlane").getJSONObject("Polar").has("Cl0")) {
                cl += jsonObject.getJSONObject("Aerodynamics").getJSONObject("HorStabPlane").getJSONObject("Polar").getBigDecimal("Cl0").doubleValue();

            }
        } catch (Exception e) {

        }
        //return cl + owalds * Math.pow(aoa, 2);
        return cl;

    }

    static double getVertStabCd(JSONObject jsonObject, double owalds) {
        var cl = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("VerStabPlane").getJSONObject("Polar").has("CdMin")) {
                cl += jsonObject.getJSONObject("Aerodynamics").getJSONObject("VerStabPlane").getJSONObject("Polar").getBigDecimal("CdMin").doubleValue();
            }
        } catch (Exception e) {

        }
        //return cl + owalds * Math.pow(aoa, 2);
        return cl;
    }

    static double getVertStabOwalds(JSONObject jsonObject) {
        var cl = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("VerStabPlane").getJSONObject("Polar").has("OswaldsEfficiencyNumber")) {
                cl += jsonObject.getJSONObject("Aerodynamics").getJSONObject("VerStabPlane").getJSONObject("Polar").getBigDecimal("OswaldsEfficiencyNumber").doubleValue();
            }
        } catch (Exception e) {

        }
        return cl;
    }

    static double getvertStabArea(JSONObject jsonObject) {
        var area = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("VerStabPlane").has("Areas")) {
                area += jsonObject.getJSONObject("Aerodynamics").getJSONObject("VerStabPlane").getJSONObject("Areas").getBigDecimal("Main").doubleValue();
                area += jsonObject.getJSONObject("Aerodynamics").getJSONObject("VerStabPlane").getJSONObject("Areas").getBigDecimal("Rudder").doubleValue();
            }
        } catch (Exception e) {

        }
        return area;
    }

    static double getVertStabCl(JSONObject jsonObject, double owalds) {
        var cl = 0.0;
        try {
            if (jsonObject.getJSONObject("Aerodynamics").getJSONObject("VerStabPlane").getJSONObject("Polar").has("Cl0")) {
                cl += jsonObject.getJSONObject("Aerodynamics").getJSONObject("VerStabPlane").getJSONObject("Polar").getBigDecimal("Cl0").doubleValue();
            }
        } catch (Exception e) {

        }
        return cl;
        //return cl + owalds * Math.pow(aoa, 2);
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

        int bottomSpeed = 0;
        int topSpeed = 0;

        double bottomThrust = 0;
        double topThrust = 0;

        for (int i = 0; i < alts.size() - 1; i++) {
            if (alt >= alts.get(i) && alt <= alts.get(i + 1)) {
                bottomAlt = alts.get(i);
                upperAlt = alts.get(i + 1);
                break;
            }
        }
        bottomSpeed = speedList.get(speedList.size() - 2);
        topSpeed = speedList.get(speedList.size() - 1);

        for (int i = 0; i < speedList.size() - 1; i++) {
            if (speed >= speedList.get(i) && speed <= speedList.get(i + 1)) {
                bottomSpeed = speedList.get(i);
                topSpeed = speedList.get(i + 1);
                break;
            }
        }
        double bottomBottomThrust = thrusts[alts.indexOf(bottomAlt)][speedList.indexOf(bottomSpeed)];
        double upperBottomThrust = thrusts[alts.indexOf(bottomAlt)][speedList.indexOf(topSpeed)];

        double bottomUpperThrust = thrusts[alts.indexOf(upperAlt)][speedList.indexOf(bottomSpeed)];
        double upperUpperThrust = thrusts[alts.indexOf(upperAlt)][speedList.indexOf(topSpeed)];
        bottomThrust = getThrustAt(bottomSpeed, bottomBottomThrust, topSpeed, upperBottomThrust, speed);

        topThrust = getThrustAt(bottomSpeed, bottomUpperThrust, topSpeed, upperUpperThrust, speed);

        var diff = topThrust - bottomThrust;
        var steps = upperAlt - bottomAlt;
        var step = diff / steps;
        thrust = bottomThrust;
        while (bottomAlt < alt) {
            thrust += step;
            bottomAlt++;
        }
        // System.out.println(thrusts2);
        if (!hasTwoDiffEngineTypes && thrusts2 == null && !actualName.contains("yak_141"))
            return thrust * engineCount;
        else
            return thrust;
    }

    double getThrustAt(double x1, double y1, double x2, double y2, double x) {
        var o = x - x1;
        var d = x2 - x1;
        var c = y2 - y1;
        return (int) ((((o) / (d)) * (c)) + y1);
    }

    // mig_29_9_13;15.1;11.36;37.98;10725;4628;1575;2.35;700;0;90;0.2,1018,1,463;-390000,910000;2;0;0;30,-20,29,-22
    public void getInfo(JSONObject plane) {
        try {
            setFlapsInfo(plane);
            JSONObject mass = plane.getJSONObject("Mass");
            emptyWeight = mass.getBigDecimal("EmptyMass").toString();
            fullWeight = mass.getBigDecimal("MaxFuelMass0").add(mass.getBigDecimal("OilMass").add(mass.getBigDecimal("EmptyMass"))).toPlainString();
            fuelWeight = mass.getBigDecimal("MaxFuelMass0").toPlainString();
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
                        System.out.println(ripSpeedKph+" "+ripSpeedMach+" "+wingSpan+" AAAA "+actualName);
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


    public void setFlapsInfo(JSONObject plane) {

        String ret = "";
        try {
            ret += plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP0").getBigDecimal(0) + ",";
            ret += plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP0").getBigDecimal(1);
            ret += ","+plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP1").getBigDecimal(0) + ",";
            ret += plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP1").getBigDecimal(1) ;
            ret += ","+plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP2").getBigDecimal(0) + ",";
            ret += plane.getJSONObject("Mass").getJSONArray("FlapsDestructionIndSpeedP2").getBigDecimal(1) ;
        } catch (Exception e) {
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
