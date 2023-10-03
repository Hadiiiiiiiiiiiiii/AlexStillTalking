package info.templates;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Shell implements Serializable {
      String type;
      String speed;
      String maxDist;
      String explosiveMass;
      public String mass;
      String drag;
      String caliber;
      String tntEquenvlant;
      String explosiveType;
      DecimalFormat df = new DecimalFormat("0.00");
    public Shell(String type, String speed, String maxDist, String explosiveMass, String mass, String drag, String caliber, String tntEquivalent,String explosiveType) {
        this(type, speed, maxDist, mass, drag,caliber);
        this.explosiveType = explosiveType.toUpperCase().replace("_","-");
        this.tntEquenvlant = df.format(Double.parseDouble(tntEquivalent)*1000)+"";
        this.explosiveMass = df.format(Double.parseDouble(explosiveMass)*1000)+"";
    }
    public Shell(String type, String speed, String maxDist, String mass, String drag, String caliber) {
        this.type = type.toUpperCase().replace("_","-");
        this.speed = speed;
        this.maxDist = maxDist;
        this.mass = (df.format(Double.parseDouble(mass)*1000)).replace(",",".");
        this.drag = drag;
        this.caliber = Double.parseDouble(caliber)*1000+"";
    }

    @Override
    public String toString() {

        if (explosiveMass !=null)
            return
                            " type = " + type + "\n" +
                            "       speed = " + speed + " m/s\n" +
                            "       maxDist = "  + maxDist + "m\n" +
                            "       mass = " + mass + "g\n" +
                            "       drag = " + drag + "\n" +
                            "       caliber = " + caliber + "mm\n"+
                            "       explosiveMass = " + explosiveMass + "g\n"+
                            "       TNT Equivalent = " + tntEquenvlant + "g\n"+
                            "       explosiveType = " + explosiveType + "\n";
        else
            return
                            " type = " + type + "\n" +
                            "       speed = " + speed + " m/s\n" +
                            "       maxDist = "  + maxDist + "m\n" +
                            "       mass = " + mass + "g\n" +
                            "       drag = " + drag + "\n" +
                            "       caliber = " + caliber + "mm\n";
    }
}
