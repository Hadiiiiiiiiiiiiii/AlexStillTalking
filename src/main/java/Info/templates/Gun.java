package Info.templates;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Gun implements Serializable {
    public String name;
    public List<Shell> shells = new ArrayList<>();
    public long id;
    public String actualname;
    public String rof;
    public ArrayList<Belt> belts = new ArrayList<>();

    public Gun(String name, String rof, int id, String actualname) throws IOException {
        this.rof =rof;
        this.actualname = actualname.replace(".blkx","");
        this.name = name.replace("\"","");
       // System.out.println(this.name);
        this.id = id;
      // this.name = name.replace("gun","").replace("cannon","").replace("_"," ");
        //this.name = name;
    }
    public void add(Shell s)
    {
        for (int i = 0; i < shells.size(); i++) {
            if (shells.get(i).type.equals(s.type))
            {
                return;
            }
        }
       // System.out.println(name+" "+s.type+" "+shells.size()+" "+System.identityHashCode(this) );
        shells.add(s);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        shells = shells.stream().sorted(Comparator.comparing(o -> o.type)).toList();
        for (int i = 0; i < shells.size(); i++) {
            b.append(shells.get(i)).append('\n');
        }
        return "Name: " + name+"\n\n"+
        "ROF: "+rof+" RPM\n" +
                "Shells: \n" +
                b.toString()+
                "\nBelts:\n" +
                getBelts()+"\n"
                +"game version: "+Plane.gameVer;
    }
    public String getBelts()
    {
        var s = new StringBuilder();
        for (Belt belt : belts) {
        s.append(belt);
        }
        return s.toString();
    }

    public void addBelt(Belt belt)
    {
        belts.add(belt);
    }

}
