package info.templates;

import javax.sound.midi.SysexMessage;
import java.io.Serializable;
import java.util.ArrayList;

public class Dragcomponent implements Serializable {
    ArrayList<Double> machCrit = new ArrayList<>(7);
    ArrayList<Double> machMax = new ArrayList<>(7);
    ArrayList<Double> multMachMax = new ArrayList<>(7);
    ArrayList<Double> multLineCoeff = new ArrayList<>(7);
    ArrayList<Double> multLimit = new ArrayList<>(7);
    int type; /* 1 = wing(no flaps), 2 = fuselage, 3 = horzstab, 4 = vert stab;*/
    static String[] test = {"wing","fuselage","horzstab", "vert stab"};
    Dragcomponent(int type) {
        this.type = type;
    }

    public double calcCdMult(double machSpeed) {
        double totalCdMult = 0;
        double totalCdMult2 = 0;

        for (int i = 0; i < machCrit.size(); i++) {
            if (machSpeed >= machCrit.get(i) && machMax.get(i) > machSpeed) {

                double relevantMachCrit = machCrit.get(i);
                double relevantMachMax = machMax.get(i);
                double relevantMultMachMax = multMachMax.get(i);
                double relevantMultLineCoeff = multLineCoeff.get(i);

                double range = relevantMachMax - relevantMachCrit;
                double percentageInRange = (machSpeed - relevantMachCrit) / range;
                if (type == 4) {
                   /* System.out.println(machCrit.get(i));
                    System.out.println(machMax.get(i));
                    System.out.println( multMachMax.get(i));
                    System.out.println(relevantMultMachMax * percentageInRange);*/
                }
           //     totalCdMult2 += relevantMultMachMax * percentageInRange;
           //     totalCdMult2 += relevantMultLineCoeff * percentageInRange;
           //     System.out.println("totalcurrent "+totalCdMult2+"  multlimit  "+multLimit.get(i)+" "+test[type-1]+" index: "+machCrit.indexOf(relevantMachCrit));
           //     if (totalCdMult2 > multLimit.get(i) &&  multLimit.get(i) > 0) {
           //         totalCdMult2 += multLimit.get(i);
           //         System.out.println("would have been "+totalCdMult2+" but multlimit is "+multLimit.get(i)+" "+test[type-1]+" index: "+i);
           //        // System.out.println("added: "+multLimit.get(i));
           //     }
           //     else {
                    totalCdMult += relevantMultMachMax * percentageInRange;
                   // totalCdMult += relevantMultLineCoeff * percentageInRange;//preset 1
                    totalCdMult += relevantMultLineCoeff ;//preset 2
           //     }
            }
        }
        System.out.println(totalCdMult+"  "+test[type-1]);
        return totalCdMult;
    }
}